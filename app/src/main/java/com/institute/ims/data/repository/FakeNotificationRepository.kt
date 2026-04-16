package com.institute.ims.data.repository

import com.institute.ims.data.model.DemoNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Seeded inbox items for the dashboard hub; read state drives the unread dot. */
object FakeNotificationRepository {
    private val seed = listOf(
        DemoNotification(
            id = "notif-1",
            title = "Exam schedule published",
            subtitle = "Examinations",
            body = "Spring session mid-term dates are now visible under Examinations for CS-24.",
        ),
        DemoNotification(
            id = "notif-2",
            title = "Two students pending verification",
            subtitle = "Student records",
            body = "Batch CS-24 has two records awaiting admin approval before transcripts sync.",
        ),
        DemoNotification(
            id = "notif-3",
            title = "Report center reminder",
            subtitle = "Grading",
            body = "Published finals for DAA still have 3 students without entered remarks.",
        ),
        DemoNotification(
            id = "notif-4",
            title = "Regional settings",
            subtitle = "Institute",
            body = "Default currency display was updated for the demo workspace (local only).",
        ),
    )

    private val _readIds = MutableStateFlow<Set<String>>(emptySet())
    val readIds: StateFlow<Set<String>> = _readIds.asStateFlow()

    fun notifications(): List<DemoNotification> = seed

    fun hasUnread(): Boolean = seed.any { it.id !in _readIds.value }

    fun markRead(id: String) {
        _readIds.update { it + id }
    }

    fun markAllRead() {
        _readIds.update { seed.map { n -> n.id }.toSet() }
    }
}
