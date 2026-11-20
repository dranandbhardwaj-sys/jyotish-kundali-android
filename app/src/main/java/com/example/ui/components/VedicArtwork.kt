package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CosmicMandalaBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "mandala_spin")
    val slowAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 90000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "outer_ring"
    )
    val counterAngle by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 60000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "inner_ring"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val center = Offset(w * 0.82f, h * 0.45f)
        val baseRadius = minOf(w, h) * 0.48f

        // Deep maroon celestial radial wash
        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF4B091B),
                    Color(0xFF280610),
                    Color(0xFF140409)
                ),
                start = Offset.Zero,
                end = Offset(w, h)
            )
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    GoldPrimary.copy(alpha = 0.22f),
                    RoyalCrimson.copy(alpha = 0.14f),
                    Color.Transparent
                ),
                center = center,
                radius = baseRadius * 1.6f
            ),
            radius = baseRadius * 1.6f,
            center = center
        )

        // Starfield dots
        val stars = listOf(
            0.12f to 0.18f, 0.26f to 0.32f, 0.42f to 0.14f, 0.58f to 0.24f,
            0.18f to 0.72f, 0.35f to 0.80f, 0.50f to 0.65f, 0.66f to 0.84f,
            0.88f to 0.14f, 0.92f to 0.78f, 0.08f to 0.48f
        )
        stars.forEachIndexed { idx, (sx, sy) ->
            drawCircle(
                color = GoldBright.copy(alpha = if (idx % 2 == 0) 0.45f else 0.22f),
                radius = if (idx % 3 == 0) 2.2.dp.toPx() else 1.3.dp.toPx(),
                center = Offset(w * sx, h * sy)
            )
        }

        // Rotating 12-spoke Zodiac Astrolabe Ring
        rotate(degrees = slowAngle, pivot = center) {
            drawCircle(
                color = ChartLineGold.copy(alpha = 0.28f),
                radius = baseRadius,
                center = center,
                style = Stroke(width = 1.2.dp.toPx())
            )
            drawCircle(
                color = ChartLineGold.copy(alpha = 0.16f),
                radius = baseRadius * 0.84f,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )
            for (i in 0 until 12) {
                val rad = Math.toRadians((i * 30).toDouble())
                val p1 = Offset(
                    center.x + (baseRadius * 0.84f * cos(rad)).toFloat(),
                    center.y + (baseRadius * 0.84f * sin(rad)).toFloat()
                )
                val p2 = Offset(
                    center.x + (baseRadius * cos(rad)).toFloat(),
                    center.y + (baseRadius * sin(rad)).toFloat()
                )
                drawLine(
                    color = ChartLineGold.copy(alpha = 0.26f),
                    start = p1,
                    end = p2,
                    strokeWidth = 1.dp.toPx()
                )
            }
        }

        // Counter-rotating sacred 8-petal star
        rotate(degrees = counterAngle, pivot = center) {
            val rInner = baseRadius * 0.64f
            drawRect(
                color = GoldBright.copy(alpha = 0.20f),
                topLeft = Offset(center.x - rInner * 0.7f, center.y - rInner * 0.7f),
                size = Size(rInner * 1.4f, rInner * 1.4f),
                style = Stroke(width = 1.dp.toPx())
            )
            rotate(degrees = 45f, pivot = center) {
                drawRect(
                    color = GoldBright.copy(alpha = 0.20f),
                    topLeft = Offset(center.x - rInner * 0.7f, center.y - rInner * 0.7f),
                    size = Size(rInner * 1.4f, rInner * 1.4f),
                    style = Stroke(width = 1.dp.toPx())
                )
            }
            drawCircle(
                color = GoldPrimary.copy(alpha = 0.45f),
                radius = baseRadius * 0.16f,
                center = center,
                style = Stroke(width = 1.4.dp.toPx())
            )
        }
    }
}

@Composable
fun VedicGoldDivider(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(14.dp)
    ) {
        val cy = size.height / 2f
        val cx = size.width / 2f
        val d = 5.dp.toPx()

        drawLine(
            brush = Brush.horizontalGradient(
                colors = listOf(Color.Transparent, ChartLineGold.copy(alpha = 0.55f)),
                startX = 0f,
                endX = cx - d * 2
            ),
            start = Offset(0f, cy),
            end = Offset(cx - d * 2, cy),
            strokeWidth = 1.dp.toPx()
        )
        drawLine(
            brush = Brush.horizontalGradient(
                colors = listOf(ChartLineGold.copy(alpha = 0.55f), Color.Transparent),
                startX = cx + d * 2,
                endX = size.width
            ),
            start = Offset(cx + d * 2, cy),
            end = Offset(size.width, cy),
            strokeWidth = 1.dp.toPx()
        )
        val path = Path().apply {
            moveTo(cx, cy - d)
            lineTo(cx + d, cy)
            lineTo(cx, cy + d)
            lineTo(cx - d, cy)
            close()
        }
        drawPath(path = path, color = GoldPrimary)
    }
}

@Composable
fun KundaliChartView(
    kundali: KundaliResult,
    chartStyle: ChartStyle,
    division: ChartDivision,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val lagnaRashi = if (division == ChartDivision.D1) kundali.lagnaRashi else kundali.navamshaLagnaRashi

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF2B0B15), Color(0xFF16060B))
                )
            )
            .border(1.5.dp, ChartLineGold.copy(alpha = 0.65f), RoundedCornerShape(16.dp))
            .padding(6.dp)
    ) {
        val boxWidth = maxWidth
        val boxHeight = maxHeight

        if (chartStyle == ChartStyle.NORTH_INDIAN) {
            // Draw Authentic North Indian Diamond Geometry
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val stroke = 1.4.dp.toPx()
                val lineColor = ChartLineGold.copy(alpha = 0.85f)

                // Outer border
                drawRect(color = lineColor, style = Stroke(width = stroke))
                // Diagonals
                drawLine(lineColor, Offset(0f, 0f), Offset(w, h), strokeWidth = stroke)
                drawLine(lineColor, Offset(w, 0f), Offset(0f, h), strokeWidth = stroke)
                // Inner diamond
                val diamond = Path().apply {
                    moveTo(w / 2f, 0f)
                    lineTo(w, h / 2f)
                    lineTo(w / 2f, h)
                    lineTo(0f, h / 2f)
                    close()
                }
                drawPath(diamond, color = lineColor, style = Stroke(width = stroke))
                // Subtle golden highlight in House 1 (Lagna)
                val lagnaPath = Path().apply {
                    moveTo(w / 2f, 0f)
                    lineTo(w * 0.75f, h * 0.25f)
                    lineTo(w / 2f, h / 2f)
                    lineTo(w * 0.25f, h * 0.25f)
                    close()
                }
                drawPath(lagnaPath, color = GoldPrimary.copy(alpha = 0.08f))
            }

            // House centers in normalized [0..1] coordinates for Houses 1..12
            val houseCenters = listOf(
                0.50f to 0.24f, // 1
                0.25f to 0.11f, // 2
                0.11f to 0.25f, // 3
                0.25f to 0.50f, // 4
                0.11f to 0.75f, // 5
                0.25f to 0.89f, // 6
                0.50f to 0.76f, // 7
                0.75f to 0.89f, // 8
                0.89f to 0.75f, // 9
                0.75f to 0.50f, // 10
                0.89f to 0.25f, // 11
                0.75f to 0.11f  // 12
            )

            val rashiCenters = listOf(
                0.50f to 0.42f, // 1
                0.25f to 0.21f, // 2
                0.21f to 0.25f, // 3
                0.42f to 0.50f, // 4
                0.21f to 0.75f, // 5
                0.25f to 0.79f, // 6
                0.50f to 0.58f, // 7
                0.75f to 0.79f, // 8
                0.79f to 0.75f, // 9
                0.58f to 0.50f, // 10
                0.79f to 0.25f, // 11
                0.75f to 0.21f  // 12
            )

            for (house in 1..12) {
                val rashiNum = ((lagnaRashi.number - 1 + (house - 1)) % 12) + 1
                val rx = rashiCenters[house - 1].first
                val ry = rashiCenters[house - 1].second
                Text(
                    text = rashiNum.toString(),
                    color = GoldMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .offset(
                            x = boxWidth * rx - 8.dp,
                            y = boxHeight * ry - 7.dp
                        )
                )

                val planetsInHouse = kundali.grahaPositions.filter {
                    (if (division == ChartDivision.D1) it.house else it.navamshaHouse) == house
                }
                val (cx, cy) = houseCenters[house - 1]
                val cellWidth = if (house in listOf(1, 4, 7, 10)) boxWidth * 0.28f else boxWidth * 0.21f
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .width(cellWidth)
                        .offset(
                            x = boxWidth * cx - cellWidth / 2,
                            y = boxHeight * cy - 18.dp
                        )
                ) {
                    if (house == 1) {
                        Text(
                            text = language.t("लग्न", "Asc"),
                            color = GoldBright,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (planetsInHouse.isNotEmpty()) {
                        val text = planetsInHouse.joinToString(" ") { gp ->
                            val abbr = if (language == AppLanguage.HI) gp.graha.abbrHi else gp.graha.abbrEn
                            if (gp.isRetrograde && gp.graha != Graha.RAHU && gp.graha != Graha.KETU) "$abbr*" else abbr
                        }
                        Text(
                            text = text,
                            color = IvoryCream,
                            fontSize = 11.sp,
                            lineHeight = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            // South Indian Fixed-Rashi 4x4 Chart
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val cw = w / 4f
                val ch = h / 4f
                val stroke = 1.3.dp.toPx()
                val lineColor = ChartLineGold.copy(alpha = 0.85f)

                drawRect(color = lineColor, style = Stroke(width = stroke))
                // Vertical lines
                drawLine(lineColor, Offset(cw, 0f), Offset(cw, h), strokeWidth = stroke)
                drawLine(lineColor, Offset(cw * 2, 0f), Offset(cw * 2, ch), strokeWidth = stroke)
                drawLine(lineColor, Offset(cw * 2, ch * 3), Offset(cw * 2, h), strokeWidth = stroke)
                drawLine(lineColor, Offset(cw * 3, 0f), Offset(cw * 3, h), strokeWidth = stroke)
                // Horizontal lines
                drawLine(lineColor, Offset(0f, ch), Offset(w, ch), strokeWidth = stroke)
                drawLine(lineColor, Offset(0f, ch * 2), Offset(cw, ch * 2), strokeWidth = stroke)
                drawLine(lineColor, Offset(cw * 3, ch * 2), Offset(w, ch * 2), strokeWidth = stroke)
                drawLine(lineColor, Offset(0f, ch * 3), Offset(w, ch * 3), strokeWidth = stroke)
            }

            // Grid coords (col, row) for Rashi 1..12 in South Indian chart
            val rashiGridCoords = mapOf(
                12 to (0 to 0), 1 to (1 to 0), 2 to (2 to 0), 3 to (3 to 0),
                11 to (0 to 1),                                4 to (3 to 1),
                10 to (0 to 2),                                5 to (3 to 2),
                9 to (0 to 3),  8 to (1 to 3), 7 to (2 to 3), 6 to (3 to 3)
            )

            val cellW = boxWidth / 4f
            val cellH = boxHeight / 4f

            rashiGridCoords.forEach { (rashiNum, coord) ->
                val rashi = Rashi.fromNumber(rashiNum)
                val isLagna = rashi == lagnaRashi
                val planets = kundali.grahaPositions.filter {
                    (if (division == ChartDivision.D1) it.rashi else it.navamshaRashi) == rashi
                }
                Box(
                    modifier = Modifier
                        .offset(x = cellW * coord.first, y = cellH * coord.second)
                        .size(width = cellW, height = cellH)
                        .background(if (isLagna) GoldPrimary.copy(alpha = 0.12f) else Color.Transparent)
                        .padding(4.dp)
                ) {
                    Text(
                        text = if (isLagna) "${rashi.symbol} ${language.t("लग्न", "Asc")}" else rashi.symbol,
                        color = if (isLagna) GoldBright else GoldMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.TopStart)
                    )
                    if (planets.isNotEmpty()) {
                        Text(
                            text = planets.joinToString(" ") { gp ->
                                val abbr = if (language == AppLanguage.HI) gp.graha.abbrHi else gp.graha.abbrEn
                                if (gp.isRetrograde && gp.graha != Graha.RAHU && gp.graha != Graha.KETU) "$abbr*" else abbr
                            },
                            color = IvoryCream,
                            fontSize = 10.5.sp,
                            lineHeight = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }

            // Center 2x2 Sanctum Label
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .offset(x = cellW, y = cellH)
                    .size(width = cellW * 2, height = cellH * 2)
                    .padding(8.dp)
            ) {
                Text(
                    text = if (division == ChartDivision.D1) {
                        language.t("जन्म लग्न चक्र", "Rashi Chart (D-1)")
                    } else {
                        language.t("नवांश चक्र", "Navamsha (D-9)")
                    },
                    style = MaterialTheme.typography.titleSmall,
                    color = GoldPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (language == AppLanguage.HI) lagnaRashi.nameHi else lagnaRashi.nameEn,
                    style = MaterialTheme.typography.bodyMedium,
                    color = IvoryCream,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = kundali.birthDetails.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedGoldText,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun GunaScoreGauge(
    score: Double,
    maxScore: Double = 36.0,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val targetSweep = ((score / maxScore).toFloat().coerceIn(0f, 1f)) * 270f
    val animatedSweep by animateFloatAsState(
        targetValue = targetSweep,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "guna_gauge"
    )
    val gaugeColor = when {
        score >= 25.0 -> ShubhEmerald
        score >= 18.0 -> GoldPrimary
        else -> CautionAmber
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(164.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 14.dp.toPx()
            val inset = strokeWidth / 2f + 6.dp.toPx()
            val arcSize = Size(size.width - inset * 2, size.height - inset * 2)
            val topLeft = Offset(inset, inset)

            // Background Track
            drawArc(
                color = ElevatedMaroon,
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            // Active Score Arc
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(RoyalCrimsonLight, GoldPrimary, gaugeColor)
                ),
                startAngle = 135f,
                sweepAngle = animatedSweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (score % 1.0 == 0.0) score.toInt().toString() else String.format("%.1f", score),
                style = MaterialTheme.typography.displaySmall,
                color = GoldBright,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "/ 36 ${language.t("गुण", "Gunas")}",
                style = MaterialTheme.typography.labelMedium,
                color = MutedGoldText
            )
        }
    }
}
