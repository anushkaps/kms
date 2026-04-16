package com.institute.ims.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val adminUser = users.firstOrNull { it.role == UserRole.ADMIN }
    val facultyUser = users.firstOrNull { it.role == UserRole.FACULTY }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F3EE)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(Color(0xFF1A1814)),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(Color(0xFF1A1814)),
        )
        Text(
            text = "LEDGER IMS - Demo Mode",
            color = Color(0xFF6E6A62),
            fontWeight = FontWeight.W500,
            fontSize = 10.sp,
            lineHeight = 12.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 24.dp, top = 56.dp),
        )
        Text(
            text = "Sign in as",
            color = Color(0xFFF5F3EE),
            fontWeight = FontWeight.W600,
            fontSize = 28.sp,
            lineHeight = 34.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 24.dp, top = 80.dp),
        )
        Text(
            text = "Select your role to continue",
            color = Color(0xFF6E6A62),
            fontWeight = FontWeight.W400,
            fontSize = 14.sp,
            lineHeight = 17.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 24.dp, top = 122.dp),
        )

        adminUser?.let { user ->
            RoleCard(
                user = user,
                selected = user.id == selectedUserId,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 244.dp),
                onClick = { selectedUserId = user.id },
            )
        }
        facultyUser?.let { user ->
            RoleCard(
                user = user,
                selected = user.id == selectedUserId,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 352.dp),
                onClick = { selectedUserId = user.id },
            )
        }

        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 684.dp)
                .width(342.dp)
                .height(48.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF0E5)),
            border = BorderStroke(1.dp, Color(0xFFF5D9B5)),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(
                text = "Demo mode - no credentials required . Local data only.",
                color = Color(0xFFB85C00),
                fontWeight = FontWeight.W400,
                fontSize = 11.sp,
                lineHeight = 13.sp,
                modifier = Modifier.padding(start = 16.dp, top = 10.dp),
            )
        }

        Button(
            onClick = { selectedUser?.let(onUserSelected) },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 748.dp)
                .width(342.dp)
                .height(52.dp),
            enabled = selectedUser != null,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1B4FBF),
                contentColor = Color.White,
            ),
        ) {
            Text(
                text = selectedUser?.let(::roleEntryLabel) ?: "Select a role",
                fontWeight = FontWeight.W600,
                fontSize = 15.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun RoleCard(
    user: User,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val accent = if (user.role == UserRole.ADMIN) Color(0xFF1B4FBF) else Color(0xFF0F7A5A)
    val iconBg = if (user.role == UserRole.ADMIN) Color(0xFFEEF2FB) else Color(0xFFEAF4EF)
    Card(
        modifier = modifier
            .width(342.dp)
            .height(88.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(
            if (selected) 2.dp else 1.dp,
            if (selected) Color(0xFF1B4FBF) else Color(0xFFD4CFC5),
        ),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(iconBg, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center,
            ) {
                if (user.role == UserRole.ADMIN) {
                    AdminGlyph()
                } else {
                    FacultyGlyph()
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.displayName,
                    color = Color(0xFF1A1814),
                    fontWeight = FontWeight.W600,
                    fontSize = 16.sp,
                    lineHeight = 19.sp,
                )
                Text(
                    text = if (user.role == UserRole.ADMIN) "Full access - all modules" else "Exam - student view",
                    color = Color(0xFF6E6A62),
                    fontWeight = FontWeight.W400,
                    fontSize = 12.sp,
                    lineHeight = 15.sp,
                )
            }
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .then(
                        if (selected) {
                            Modifier.background(accent, CircleShape)
                        } else {
                            Modifier
                                .background(Color.White, CircleShape)
                                .border(1.5.dp, Color(0xFFD4CFC5), CircleShape)
                        },
                    ),
            )
        }
    }
}

@Composable
private fun AdminGlyph() {
    Box(modifier = Modifier.size(28.dp)) {
        Box(
            modifier = Modifier
                .padding(start = 2.dp, top = 4.dp)
                .size(10.dp)
                .background(Color(0xFF1B4FBF), RoundedCornerShape(2.dp)),
        )
        Box(
            modifier = Modifier
                .padding(start = 16.dp, top = 4.dp)
                .size(10.dp)
                .background(Color(0xFF1B4FBF), RoundedCornerShape(2.dp)),
        )
        Box(
            modifier = Modifier
                .padding(start = 2.dp, top = 17.dp)
                .width(24.dp)
                .height(7.dp)
                .background(Color(0xFF1B4FBF), RoundedCornerShape(2.dp)),
        )
    }
}

@Composable
private fun FacultyGlyph() {
    Box(modifier = Modifier.size(28.dp)) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(16.dp)
                .background(Color(0xFF0F7A5A), CircleShape),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .width(28.dp)
                .height(14.dp)
                .background(Color(0xFF0F7A5A), RoundedCornerShape(4.dp)),
        )
    }
}

private fun roleEntryLabel(user: User): String = when (user.role) {
    UserRole.ADMIN -> "Enter as Institute Admin ->"
    UserRole.FACULTY -> "Enter as Faculty Member ->"
}
