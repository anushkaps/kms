package com.institute.ims.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.institute.ims.data.catalog.RegionalCatalog
import com.institute.ims.data.model.DashboardModuleId
import com.institute.ims.data.model.StudentFilterCriteria
import com.institute.ims.data.model.UserRole
import com.institute.ims.data.repository.DashboardRepository
import com.institute.ims.data.repository.ExamRepository
import com.institute.ims.data.repository.FakeDashboardRepository
import com.institute.ims.data.repository.FakeExamRepository
import com.institute.ims.data.repository.FakeNewsRepository
import com.institute.ims.data.repository.FakeRegionalPreferencesRepository
import com.institute.ims.data.repository.FakeStudentRepository
import com.institute.ims.data.repository.FakeUserRepository
import com.institute.ims.data.repository.NewsRepository
import com.institute.ims.data.repository.StudentRepository
import com.institute.ims.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Loads the signed-in user slice: profile header, stats, modules, news, and hub navigation search. */
class DashboardViewModel(
    private val userId: String,
    private val userRepository: UserRepository = FakeUserRepository,
    private val newsRepository: NewsRepository = FakeNewsRepository,
    private val dashboardRepository: DashboardRepository = FakeDashboardRepository,
    private val studentRepository: StudentRepository = FakeStudentRepository,
    private val examRepository: ExamRepository = FakeExamRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        val user = userRepository.getUser(userId)
        if (user != null) {
            _uiState.update {
                it.copy(
                    displayName = user.displayName,
                    role = user.role,
                    userInitials = initialsFor(user.displayName),
                    greetingLine = greetingFor(user.displayName, user.role),
                    quickChips = dashboardRepository.getQuickChipLabels(),
                    stats = dashboardRepository.getSummaryStats(user.role),
                    modules = dashboardRepository.getModuleCards(),
                    capabilityHighlights = dashboardRepository.getCapabilityHighlights(),
                    news = newsRepository.getNews().sortedByDescending { n -> n.publishedAtEpochMs },
                    overviewLine = dashboardRepository.getOverviewLine(user.role),
                    regionalSummaryLine = RegionalCatalog.summaryLine(
                        FakeRegionalPreferencesRepository.prefsFlow.value,
                    ),
                )
            }
        }
        viewModelScope.launch {
            FakeRegionalPreferencesRepository.prefsFlow.collectLatest { p ->
                _uiState.update {
                    it.copy(regionalSummaryLine = RegionalCatalog.summaryLine(p))
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { s ->
            s.copy(
                searchQuery = query,
                newsSpotlightId = if (query.isNotBlank()) null else s.newsSpotlightId,
            )
        }
    }

    fun spotlightNews(newsId: String) {
        _uiState.update { it.copy(newsSpotlightId = newsId, searchQuery = "") }
    }

    fun clearNewsSpotlight() {
        _uiState.update { it.copy(newsSpotlightId = null) }
    }

    /** Local-only matches for modules, students, exams, and news (hub navigation). */
    fun navigationSuggestions(): List<DashboardNavSuggestion> {
        val s = _uiState.value
        val q = s.searchQuery.trim()
        if (q.isEmpty()) return emptyList()
        val ql = q.lowercase()
        val out = mutableListOf<DashboardNavSuggestion>()
        val seen = mutableSetOf<String>()

        fun add(suggestion: DashboardNavSuggestion) {
            if (seen.add(suggestion.id)) {
                out.add(suggestion)
            }
        }

        if (studentNavHint(ql)) {
            add(
                DashboardNavSuggestion(
                    id = "nav-students",
                    title = "Open Student Details",
                    subtitle = "Directory, batches, and profiles",
                    action = DashboardNavAction.OpenStudentDirectory,
                ),
            )
        }
        if (examNavHint(ql)) {
            add(
                DashboardNavSuggestion(
                    id = "nav-exams",
                    title = "Open Examinations",
                    subtitle = "Schedule, results, and report center",
                    action = DashboardNavAction.OpenExamList,
                ),
            )
        }
        if (newsNavHint(ql)) {
            add(
                DashboardNavSuggestion(
                    id = "nav-news",
                    title = "Open latest news",
                    subtitle = "View all institute updates",
                    action = DashboardNavAction.OpenNews(q),
                ),
            )
        }
        if (regionalNavHint(ql)) {
            add(
                DashboardNavSuggestion(
                    id = "nav-regional",
                    title = "Language & region settings",
                    subtitle = "Country, currency, time zone",
                    action = DashboardNavAction.OpenRegionalSettings,
                ),
            )
        }

        studentRepository.getStudents(StudentFilterCriteria(query = q))
            .take(4)
            .forEach { st ->
                add(
                    DashboardNavSuggestion(
                        id = "stu-${st.id}",
                        title = "Student: ${st.name}",
                        subtitle = "${st.studentNumber} · ${st.email}",
                        action = DashboardNavAction.OpenStudentProfile(st.id),
                    ),
                )
            }

        examRepository.getExams()
            .filter { it.title.contains(q, ignoreCase = true) }
            .take(4)
            .forEach { ex ->
                add(
                    DashboardNavSuggestion(
                        id = "exm-${ex.id}",
                        title = "Exam: ${ex.title}",
                        subtitle = "${ex.examType} · ${ex.scheduleLabel}",
                        action = DashboardNavAction.OpenExamDetail(ex.id),
                    ),
                )
            }

        s.news
            .asSequence()
            .filter { item ->
                item.title.contains(q, ignoreCase = true) ||
                    item.body.contains(q, ignoreCase = true) ||
                    (item.tag?.contains(q, ignoreCase = true) == true)
            }
            .take(3)
            .forEach { item ->
                add(
                    DashboardNavSuggestion(
                        id = "news-${item.id}",
                        title = "Latest news: ${item.title}",
                        subtitle = item.tag ?: "Institute update",
                        action = DashboardNavAction.OpenNews(item.title),
                    ),
                )
            }

        return out.take(12)
    }

    fun onQuickChipClick(
        label: String,
        onOpenStudents: () -> Unit,
        onOpenExams: () -> Unit,
        onOpenNews: () -> Unit,
    ) {
        when (label) {
            "Students" -> onOpenStudents()
            "Exams" -> onOpenExams()
            "News" -> onOpenNews()
            else -> Unit
        }
    }

    fun onModuleClick(id: DashboardModuleId, onOpenStudents: () -> Unit, onOpenExams: () -> Unit) {
        when (id) {
            DashboardModuleId.STUDENTS -> onOpenStudents()
            DashboardModuleId.EXAMS -> onOpenExams()
        }
    }

    private fun studentNavHint(q: String): Boolean {
        val hints = listOf("student", "directory", "batch", "profile", "enroll", "former", "current")
        return hints.any { q.contains(it) }
    }

    private fun examNavHint(q: String): Boolean {
        val hints = listOf("exam", "paper", "test", "quiz", "viva", "mid-term", "final", "grade", "mark")
        return hints.any { q.contains(it) }
    }

    private fun regionalNavHint(q: String): Boolean {
        val hints = listOf(
            "language",
            "locale",
            "country",
            "currency",
            "timezone",
            "time zone",
            "region",
            "i18n",
        )
        return hints.any { q.contains(it) }
    }

    private fun newsNavHint(q: String): Boolean {
        val hints = listOf("news", "notice", "update", "announcement", "latest")
        return hints.any { q.contains(it) }
    }

    private fun initialsFor(displayName: String): String {
        val parts = displayName.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        return when {
            parts.size >= 2 -> "${parts[0].first().uppercaseChar()}${parts[1].first().uppercaseChar()}"
            parts.isNotEmpty() && parts[0].length >= 2 ->
                parts[0].substring(0, 2).uppercase()
            parts.isNotEmpty() -> parts[0].first().uppercaseChar().toString()
            else -> "?"
        }
    }

    private fun greetingFor(displayName: String, role: UserRole): String {
        val first = displayName.trim().split(Regex("\\s+")).firstOrNull().orEmpty()
        val tone = when (role) {
            UserRole.ADMIN -> "Here is your institute overview"
            UserRole.FACULTY -> "Here is your teaching workspace"
        }
        return "Hello, $first - $tone."
    }

    class Factory(
        private val userId: String,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DashboardViewModel(userId = userId) as T
        }
    }
}
