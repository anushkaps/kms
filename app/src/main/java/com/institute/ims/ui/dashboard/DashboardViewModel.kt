package com.institute.ims.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.institute.ims.data.model.DashboardModuleId
import com.institute.ims.data.model.UserRole
import com.institute.ims.data.repository.DashboardRepository
import com.institute.ims.data.repository.FakeDashboardRepository
import com.institute.ims.data.repository.FakeNewsRepository
import com.institute.ims.data.repository.FakeUserRepository
import com.institute.ims.data.repository.NewsRepository
import com.institute.ims.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Loads the signed-in user slice: profile header, stats, modules, and news (with search in the screen). */
class DashboardViewModel(
    private val userId: String,
    private val userRepository: UserRepository = FakeUserRepository,
    private val newsRepository: NewsRepository = FakeNewsRepository,
    private val dashboardRepository: DashboardRepository = FakeDashboardRepository,
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
                    stats = dashboardRepository.getSummaryStats(),
                    modules = dashboardRepository.getModuleCards(),
                    news = newsRepository.getNews().sortedByDescending { n -> n.publishedAtEpochMs },
                    overviewLine = dashboardRepository.getOverviewLine(user.role),
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onQuickChipClick(label: String, onOpenStudents: () -> Unit, onOpenExams: () -> Unit) {
        when (label) {
            "Students" -> onOpenStudents()
            "Exams" -> onOpenExams()
            "News" -> onSearchQueryChange("")
            else -> Unit
        }
    }

    fun onModuleClick(id: DashboardModuleId, onOpenStudents: () -> Unit, onOpenExams: () -> Unit) {
        when (id) {
            DashboardModuleId.STUDENTS -> onOpenStudents()
            DashboardModuleId.EXAMS -> onOpenExams()
        }
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
        return "Hello, $first — $tone."
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
