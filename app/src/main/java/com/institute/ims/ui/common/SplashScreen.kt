package com.institute.ims.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1100)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LedgerPalette.Ink),
    ) {
        Text(
            text = "V2.1 DEMO",
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF8D877B),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 28.dp, end = 24.dp),
        )
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = LedgerPalette.Cobalt.copy(alpha = 0.2f),
            ) {
                Text(
                    text = "[]",
                    color = LedgerPalette.Cobalt,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                )
            }
            Spacer(modifier = Modifier.height(22.dp))
            Text(
                text = "LEDGER",
                color = Color(0xFFF2EDE1),
                fontWeight = FontWeight.Medium,
                letterSpacing = 6.sp,
                style = MaterialTheme.typography.headlineMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "INSTITUTE MANAGEMENT",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF8D877B),
                letterSpacing = 2.sp,
            )
            Spacer(modifier = Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(1.dp)
                    .background(Color(0xFF4C473E)),
            )
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "St. Xavier's College of Engineering",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFD0C8B8),
            )
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 56.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Dot(active = true)
            Dot(active = false)
            Dot(active = false)
        }
    }
}

@Composable
private fun Dot(active: Boolean) {
    Box(
        modifier = Modifier
            .size(if (active) 9.dp else 8.dp)
            .clip(CircleShape)
            .background(
                if (active) {
                    LedgerPalette.Cobalt
                } else {
                    Color(0xFF5F5A50)
                },
            ),
    )
}
