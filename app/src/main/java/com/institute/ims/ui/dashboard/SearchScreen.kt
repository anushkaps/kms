@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.institute.ims.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.institute.ims.data.model.NewsItem
import com.institute.ims.ui.common.LedgerPalette

@Composable
fun SearchScreen(
    userId: String,
    onBack: () -> Unit,
    onOpenStudents: () -> Unit,
    onOpenExams: () -> Unit,
    onOpenNews: (query: String) -> Unit,
    onOpenStudentProfile: (studentId: String) -> Unit,
    onOpenExamDetail: (examId: String) -> Unit,
    onOpenRegionalSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: DashboardViewModel = viewModel(
        key = "search-$userId",
        factory = DashboardViewModel.Factory(userId),
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }
    var selectedNewsDetail by remember { mutableStateOf<NewsItem?>(null) }

    val groupedSuggestions = remember(uiState.searchQuery, uiState.news) {
        viewModel.navigationSuggestionsGrouped()
    }

    fun applySuggestion(s: DashboardNavSuggestion) {
        when (val a = s.action) {
            DashboardNavAction.OpenStudentDirectory -> {
                viewModel.onSearchQueryChange("")
                onOpenStudents()
            }
            DashboardNavAction.OpenExamList -> {
                viewModel.onSearchQueryChange("")
                onOpenExams()
            }
            is DashboardNavAction.OpenStudentProfile -> {
                viewModel.onSearchQueryChange("")
                onOpenStudentProfile(a.studentId)
            }
            is DashboardNavAction.OpenExamDetail -> {
                viewModel.onSearchQueryChange("")
                onOpenExamDetail(a.examId)
            }
            is DashboardNavAction.OpenNews -> {
                viewModel.onSearchQueryChange("")
                onOpenNews(a.query)
            }
            is DashboardNavAction.OpenNewsDetail -> {
                viewModel.onSearchQueryChange("")
                val hit = uiState.news.find { it.id == a.newsId }
                if (hit != null) selectedNewsDetail = hit else onOpenNews("")
            }
            DashboardNavAction.OpenRegionalSettings -> {
                viewModel.onSearchQueryChange("")
                onOpenRegionalSettings()
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                IconButton(onClick = {
                    viewModel.onSearchQueryChange("")
                    onBack()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = LedgerPalette.Ink,
                    )
                }
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(22.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFD4CFC5)),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null,
                            tint = Color(0xFFD4CFC5),
                            modifier = Modifier.size(16.dp),
                        )
                        BasicTextField(
                            value = uiState.searchQuery,
                            onValueChange = viewModel::onSearchQueryChange,
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(focusRequester),
                            textStyle = MaterialTheme.typography.bodySmall.copy(
                                color = LedgerPalette.Ink,
                                fontSize = 13.sp,
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            cursorBrush = SolidColor(LedgerPalette.Cobalt),
                            decorationBox = { innerTextField ->
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    if (uiState.searchQuery.isEmpty()) {
                                        Text(
                                            text = "Search students, exams, news...",
                                            fontSize = 13.sp,
                                            color = Color(0xFF888780),
                                        )
                                    }
                                    innerTextField()
                                }
                            },
                        )
                        if (uiState.searchQuery.isNotEmpty()) {
                            TextButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                Text("Clear", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            if (uiState.searchQuery.isNotBlank()) {
                if (groupedSuggestions.isNotEmpty()) {
                    InlineSearchResultsCard(
                        groups = groupedSuggestions,
                        onPick = { applySuggestion(it) },
                        modifier = Modifier.padding(top = 8.dp),
                    )
                } else {
                    Text(
                        text = "No matches yet. Try a student name, ID, exam title, or \u201cStudents\u201d / \u201cExams\u201d.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }
        }
    }

    if (selectedNewsDetail != null) {
        val news = selectedNewsDetail!!
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { selectedNewsDetail = null },
            sheetState = sheetState,
        ) {
            NewsDetailSheetContent(
                news = news,
                onClose = { selectedNewsDetail = null },
            )
        }
    }
}
