package com.institute.ims.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.institute.ims.data.model.User
import com.institute.ims.data.model.UserRole

@Composable
fun RoleSelectScreen(
    users: List<User>,
    onUserSelected: (User) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedUserId by remember(users) { mutableStateOf(users.firstOrNull()?.id) }
    val selectedUser = users.firstOrNull { it.id == selectedUserId }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LedgerPalette.Parchment),
    ) {
        Surface(
            color = LedgerPalette.Ink,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 22.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = "LEDGER IMS  -  DEMO MODE",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFA09A8D),
                )
                Text(
                    text = "Sign in as",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFFF3EEE2),
                )
                Text(
                    text = "Select your role to continue",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFC9C2B5),
                )
            }
        }
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
        users.forEach { user ->
            val selected = user.id == selectedUserId
            val borderColor = if (selected) LedgerPalette.Cobalt else LedgerPalette.Rule
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedUserId = user.id },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = LedgerPalette.Surface),
                border = BorderStroke(width = if (selected) 1.5.dp else 1.dp, color = borderColor),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                color = when (user.role) {
                                    UserRole.ADMIN -> LedgerPalette.Cobalt.copy(alpha = 0.12f)
                                    UserRole.FACULTY -> LedgerPalette.Forest.copy(alpha = 0.12f)
                                },
                                shape = RoundedCornerShape(12.dp),
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = if (user.role == UserRole.ADMIN) {
                                Icons.Outlined.AdminPanelSettings
                            } else {
                                Icons.Outlined.PersonOutline
                            },
                            contentDescription = null,
                            tint = if (user.role == UserRole.ADMIN) LedgerPalette.Cobalt else LedgerPalette.Forest,
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = user.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = if (user.role == UserRole.ADMIN) {
                                "Full access - all modules"
                            } else {
                                "Exam - student view"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .background(Color.Transparent, CircleShape)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(if (selected) 10.dp else 8.dp)
                                .background(
                                    if (selected) LedgerPalette.Cobalt else Color(0xFFC8C2B7),
                                    CircleShape,
                                ),
                        )
                    }
                }
            }
        }
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1E0)),
                border = BorderStroke(1.dp, Color(0xFFF0D4AF)),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(
                    text = "Demo mode active - local data only. No credentials required.",
                    style = MaterialTheme.typography.bodySmall,
                    color = LedgerPalette.Amber,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Button(
                onClick = { selectedUser?.let(onUserSelected) },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedUser != null,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LedgerPalette.Cobalt,
                    contentColor = Color.White,
                ),
            ) {
                Text(
                    text = selectedUser?.let(::roleEntryLabel) ?: "Select a role",
                    modifier = Modifier.padding(vertical = 4.dp),
                )
            }
        }
    }
}

private fun roleEntryLabel(user: User): String = when (user.role) {
    UserRole.ADMIN -> "Enter as Institute Admin"
    UserRole.FACULTY -> "Enter as Faculty Member"
}
