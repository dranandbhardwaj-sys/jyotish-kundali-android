package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.JyotishUiState
import com.example.ui.components.InfoBadgeBox
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun RashifalScreen(
    uiState: JyotishUiState,
    onSelectRashi: (Rashi) -> Unit,
    modifier: Modifier = Modifier
) {
    val lang = uiState.language
    val selectedRashi = uiState.selectedRashi
    val activeRashifal = uiState.dailyRashifalList.firstOrNull { it.rashi == selectedRashi }
        ?: uiState.dailyRashifalList.firstOrNull()

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWide = maxWidth >= 820.dp

        if (isWide) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(0.45f)
                ) {
                    item {
                        SectionHeader(
                            title = lang.t("12 राशियाँ", "12 Zodiac Signs"),
                            subtitle = lang.t("चंद्र गोचर आधारित राशिफल", "Chandra Gochar Horoscope"),
                            icon = Icons.Filled.Stars
                        )
                    }
                    item {
                        RashiGrid(
                            selectedRashi = selectedRashi,
                            language = lang,
                            columns = 3,
                            onSelectRashi = onSelectRashi
                        )
                    }
                }
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(0.55f)
                ) {
                    if (activeRashifal != null) {
                        item {
                            DailyRashifalDetailCard(rashifal = activeRashifal, language = lang)
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("rashifal_lazy_list")
            ) {
                item {
                    SectionHeader(
                        title = lang.t("12 राशियाँ एवं दैनिक राशिफल", "12 Rashis & Daily Rashifal"),
                        subtitle = lang.t("आज के वास्तविक चंद्र गोचर पर आधारित फल", "Calculated from today's live Moon transit"),
                        icon = Icons.Filled.Stars
                    )
                }
                item {
                    RashiGrid(
                        selectedRashi = selectedRashi,
                        language = lang,
                        columns = 3,
                        onSelectRashi = onSelectRashi
                    )
                }
                if (activeRashifal != null) {
                    item {
                        DailyRashifalDetailCard(rashifal = activeRashifal, language = lang)
                    }
                }
            }
        }
    }
}

@Composable
private fun RashiGrid(
    selectedRashi: Rashi,
    language: AppLanguage,
    columns: Int,
    onSelectRashi: (Rashi) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Rashi.entries.chunked(columns).forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                row.forEach { rashi ->
                    val isSelected = rashi == selectedRashi
                    Surface(
                        onClick = { onSelectRashi(rashi) },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) RoyalCrimson else CardMaroon,
                        border = BorderStroke(1.2.dp, if (isSelected) GoldPrimary else SubtleGoldBorder),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("rashi_card_${rashi.name.lowercase()}")
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp)
                        ) {
                            Text(
                                text = rashi.symbol,
                                fontSize = 24.sp,
                                color = if (isSelected) GoldBright else GoldPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (language == AppLanguage.HI) rashi.nameHi else rashi.nameEn,
                                style = MaterialTheme.typography.titleSmall,
                                color = IvoryCream,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (language == AppLanguage.HI) rashi.element.hi else rashi.element.en,
                                style = MaterialTheme.typography.labelSmall,
                                color = MutedGoldText,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DailyRashifalDetailCard(
    rashifal: DailyRashifal,
    language: AppLanguage
) {
    val rashi = rashifal.rashi
    Card(
        colors = CardDefaults.cardColors(containerColor = CardMaroon),
        border = BorderStroke(1.2.dp, GoldPrimary.copy(alpha = 0.55f)),
        shape = MaterialTheme.shapes.large,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("rashifal_detail_card")
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(RoyalCrimson)
                            .border(1.2.dp, GoldPrimary, CircleShape)
                    ) {
                        Text(text = rashi.symbol, fontSize = 24.sp, color = GoldBright)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (language == AppLanguage.HI) "${rashi.nameHi} राशिफल" else "${rashi.nameEn} Daily Horoscope",
                            style = MaterialTheme.typography.headlineSmall,
                            color = GoldBright
                        )
                        Text(
                            text = "${language.t("स्वामी", "Lord")}: ${if (language == AppLanguage.HI) rashi.lord.nameHi else rashi.lord.nameEn} • ${language.t("चंद्र गोचर", "Moon Transit")}: ${if (language == AppLanguage.HI) rashifal.moonTransitRashi.nameHi else rashifal.moonTransitRashi.nameEn} (${language.t("भाव", "H")} ${rashifal.moonTransitHouse})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedGoldText
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) { idx ->
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = if (idx < rashifal.rating) GoldPrimary else SubtleGoldBorder,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Text(
                text = if (language == AppLanguage.HI) rashifal.summaryHi else rashifal.summaryEn,
                style = MaterialTheme.typography.bodyLarge,
                color = IvoryCream
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                InfoBadgeBox(
                    label = language.t("शुभ रंग", "Lucky Color"),
                    value = if (language == AppLanguage.HI) rashi.luckyColorHi else rashi.luckyColorEn,
                    modifier = Modifier.weight(1f)
                )
                InfoBadgeBox(
                    label = language.t("शुभ अंक", "Lucky No."),
                    value = rashi.luckyNumber.toString(),
                    modifier = Modifier.weight(0.65f)
                )
                InfoBadgeBox(
                    label = language.t("शुभ दिशा", "Direction"),
                    value = if (language == AppLanguage.HI) rashi.directionHi else rashi.directionEn,
                    modifier = Modifier.weight(0.75f)
                )
            }

            RashifalAspectBlock(
                title = language.t("करियर एवं धन", "Career & Finance"),
                body = if (language == AppLanguage.HI) rashifal.careerHi else rashifal.careerEn,
                icon = Icons.Filled.WorkOutline
            )
            RashifalAspectBlock(
                title = language.t("प्रेम एवं परिवार", "Love & Family"),
                body = if (language == AppLanguage.HI) rashifal.loveHi else rashifal.loveEn,
                icon = Icons.Filled.FavoriteBorder
            )
            RashifalAspectBlock(
                title = language.t("स्वास्थ्य", "Health & Vitality"),
                body = if (language == AppLanguage.HI) rashifal.healthHi else rashifal.healthEn,
                icon = Icons.Filled.Spa
            )

            Surface(
                color = RoyalCrimson.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = language.t("आज का वैदिक उपाय", "Today's Vedic Remedy"),
                        style = MaterialTheme.typography.labelLarge,
                        color = GoldBright
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (language == AppLanguage.HI) rashifal.remedyHi else rashifal.remedyEn,
                        style = MaterialTheme.typography.bodyMedium,
                        color = WarmParchment
                    )
                }
            }
        }
    }
}

@Composable
private fun RashifalAspectBlock(
    title: String,
    body: String,
    icon: ImageVector
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DeepMaroonSurface)
            .border(1.dp, SubtleGoldBorder.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Icon(icon, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = GoldBright
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = WarmParchment
            )
        }
    }
}
