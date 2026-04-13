package com.institute.ims.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.institute.ims.data.model.User

@Composable
fun RoleSelectScreen(
    users: List<User>,
    onUserSelected: (User) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Choose your role",
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = "Select how you are signing in for this demo.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        users.forEach { user ->
            Button(
                onClick = { onUserSelected(user) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("${user.displayName} (${user.role.name})")
            }
        }
    }
}
