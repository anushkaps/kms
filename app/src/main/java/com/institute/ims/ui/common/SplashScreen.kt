package com.institute.ims.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
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
        // status-bar strip
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .height(44.dp)
                .background(LedgerPalette.Ink),
        )

        Text(
            text = "v2.1 Demo",
            color = Color(0xFF6E6A62),
            fontWeight = FontWeight.W400,
            fontSize = 10.sp,
            lineHeight = 12.sp,
            textAlign = TextAlign.End,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .width(64.dp)
                .padding(top = 56.dp, end = 16.dp),
        )

        // logo-group
        LedgerMark(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 290.dp),
        )

        Text(
            text = "LEDGER",
            color = Color(0xFFF5F3EE),
            fontWeight = FontWeight.W600,
            fontSize = 32.sp,
            lineHeight = 39.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(top = 372.dp),
        )

        Text(
            text = "INSTITUTE MANAGEMENT",
            color = Color(0xFF6E6A62),
            fontWeight = FontWeight.W500,
            fontSize = 11.sp,
            lineHeight = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(top = 416.dp),
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 446.dp)
                .width(48.dp)
                .height(1.dp)
                .background(Color(0xFF3A3732)),
        )

        Text(
            text = "St. Xavier's College of Engineering",
            color = Color(0xFFD4CFC5),
            fontWeight = FontWeight.W400,
            fontSize = 13.sp,
            lineHeight = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(top = 460.dp),
        )

        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 784.dp),
        ) {
            Dot(active = true)
            Box(modifier = Modifier.width(8.dp))
            Dot(active = false)
            Box(modifier = Modifier.width(8.dp))
            Dot(active = false)
        }
    }
}

@Composable
private fun Dot(active: Boolean) {
    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(
                if (active) {
                    Color(0xFF1B4FBF)
                } else {
                    Color(0xFF3A3732)
                },
            ),
    )
}

@Composable
private fun LedgerMark(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(64.dp),
    ) {
        // book-body
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 8.dp, top = 2.dp)
                .width(48.dp)
                .height(60.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFF1A1814)),
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 8.dp, top = 2.dp)
                .width(48.dp)
                .height(60.dp)
                .clip(RoundedCornerShape(6.dp))
                .border(1.5.dp, Color(0xFF1B4FBF), RoundedCornerShape(6.dp)),
        )

        // spine
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 8.dp, top = 2.dp)
                .width(9.dp)
                .height(60.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF1B4FBF)),
        )

        // rules
        RuleLine(start = 22.dp, top = 18.dp, width = 28.dp)
        RuleLine(start = 22.dp, top = 28.dp, width = 28.dp)
        RuleLine(start = 22.dp, top = 38.dp, width = 16.dp)
    }
}

@Composable
private fun RuleLine(
    start: Dp,
    top: Dp,
    width: Dp,
) {
    Box(
        modifier = Modifier
            .padding(start = start, top = top)
            .width(width)
            .height(1.5.dp)
            .background(Color(0xFF1B4FBF)),
    )
}
