package com.institute.ims.ui.common

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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onContinueToRoleSelect: () -> Unit,
    fromDashboard: Boolean = false,
    onBackToDashboard: () -> Unit = {},
) {
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 3 })
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LedgerPalette.Ink),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .height(44.dp)
                .background(LedgerPalette.Ink),
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 44.dp, bottom = 56.dp),
                beyondViewportPageCount = 1,
            ) { page ->
                when (page) {
                    0 -> IntroPageLedger()
                    1 -> IntroPageFeatures()
                    else -> IntroPageReady()
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
            ) {
                repeat(3) { index ->
                    Dot(
                        active = pagerState.currentPage == index,
                        modifier = Modifier
                            .clickable {
                                scope.launch { pagerState.animateScrollToPage(index) }
                            },
                    )
                    if (index < 2) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }

            Text(
                text = if (fromDashboard) "← Dashboard" else "Skip",
                color = Color(0xFF6E6A62),
                fontWeight = FontWeight.W500,
                fontSize = 11.sp,
                lineHeight = 13.sp,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 24.dp, top = 56.dp)
                    .clickable {
                        if (fromDashboard) onBackToDashboard() else onContinueToRoleSelect()
                    },
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
        }
    }
}

@Composable
private fun IntroPageLedger() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(200.dp))
        LedgerMark(modifier = Modifier.padding(top = 40.dp))
        Text(
            text = "IMS",
            color = Color(0xFFF5F3EE),
            fontWeight = FontWeight.W600,
            fontSize = 32.sp,
            lineHeight = 39.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
        )
        Text(
            text = "INSTITUTE MANAGEMENT SYSTEM",
            color = Color(0xFF6E6A62),
            fontWeight = FontWeight.W500,
            fontSize = 11.sp,
            lineHeight = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
        )
        Box(
            modifier = Modifier
                .padding(top = 16.dp)
                .width(48.dp)
                .height(1.dp)
                .background(Color(0xFF3A3732)),
        )
        Text(
            text = "IIIT Hyderabad",
            color = Color(0xFFD4CFC5),
            fontWeight = FontWeight.W400,
            fontSize = 13.sp,
            lineHeight = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, start = 32.dp, end = 32.dp),
        )
    }
}

@Composable
private fun IntroPageFeatures() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(220.dp))
        Text(
            text = "One hub for your campus",
            color = Color(0xFFF5F3EE),
            fontWeight = FontWeight.W600,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Students, examinations, and institute news stay connected in a single IMS workspace.",
            color = Color(0xFFD4CFC5),
            fontWeight = FontWeight.W400,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun IntroPageReady() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(240.dp))
        Text(
            text = "You are almost there",
            color = Color(0xFFF5F3EE),
            fontWeight = FontWeight.W600,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Swipe back to review, or tap Skip to choose your demo role. You can switch roles later from the dashboard.",
            color = Color(0xFFD4CFC5),
            fontWeight = FontWeight.W400,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun Dot(
    active: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
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

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 8.dp, top = 2.dp)
                .width(9.dp)
                .height(60.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF1B4FBF)),
        )

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
