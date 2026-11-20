package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.model.*
import com.example.ui.JyotishUiState
import com.example.ui.components.BirthDetailsFormCard
import com.example.ui.components.GunaScoreGauge
import com.example.ui.components.InfoBadgeBox
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun MatchingScreen(
    uiState: JyotishUiState,
    onUpdateGroom: (BirthDetails) -> Unit,
    onUpdateBride: (BirthDetails) -> Unit,
    onSearchPlaces: (String) -> Unit,
    onMatch: () -> Boolean,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang = uiState.language
    val match = uiState.matchResult
    var formsExpanded by remember(match) { mutableStateOf(match == null) }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWide = maxWidth >= 820.dp

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .fillMaxSize()
                .testTag("matching_lazy_list")
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SectionHeader(
                        title = lang.t("कुंडली मिलान (अष्टकूट)", "Kundali Matching (Ashtakoota)"),
                        subtitle = lang.t("36 गुण मिलान एवं मांगलिक दोष परीक्षण", "36 Guna compatibility & Manglik analysis"),
                        icon = Icons.Filled.Favorite,
                        modifier = Modifier.weight(1f)
                    )
                    if (match != null) {
                        Row {
                            TextButton(
                                onClick = { formsExpanded = !formsExpanded },
                                modifier = Modifier.testTag("toggle_matching_forms_btn")
                            ) {
                                Text(
                                    text = if (formsExpanded) lang.t("फॉर्म छुपाएं", "Hide Forms") else lang.t("विवरण बदलें", "Edit Forms"),
                                    color = GoldPrimary
                                )
                                Icon(
                                    imageVector = if (formsExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                    contentDescription = null,
                                    tint = GoldPrimary
                                )
                            }
                            IconButton(
                                onClick = onReset,
                                modifier = Modifier.testTag("reset_matching_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Refresh,
                                    contentDescription = lang.t("रीसेट करें", "Reset"),
                                    tint = GoldPrimary
                                )
                            }
                        }
                    }
                }
            }

            if (formsExpanded) {
                item {
                    if (isWide) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            BirthDetailsFormCard(
                                title = lang.t("वर का विवरण (Groom)", "Groom's Birth Details"),
                                details = uiState.groomInput,
                                language = lang,
                                offlinePlaces = uiState.offlinePlaceResults,
                                onlinePlaces = uiState.onlinePlaceResults,
                                isSearchingPlaces = uiState.isSearchingPlaces,
                                placeSearchError = uiState.placeSearchError,
                                onSearchPlaces = onSearchPlaces,
                                onDetailsChange = onUpdateGroom,
                                showGender = false,
                                submitTestTag = "groom_form",
                                modifier = Modifier.weight(1f)
                            )
                            BirthDetailsFormCard(
                                title = lang.t("वधू का विवरण (Bride)", "Bride's Birth Details"),
                                details = uiState.brideInput,
                                language = lang,
                                offlinePlaces = uiState.offlinePlaceResults,
                                onlinePlaces = uiState.onlinePlaceResults,
                                isSearchingPlaces = uiState.isSearchingPlaces,
                                placeSearchError = uiState.placeSearchError,
                                onSearchPlaces = onSearchPlaces,
                                onDetailsChange = onUpdateBride,
                                showGender = false,
                                submitTestTag = "bride_form",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            BirthDetailsFormCard(
                                title = lang.t("वर का विवरण (Groom)", "Groom's Birth Details"),
                                details = uiState.groomInput,
                                language = lang,
                                offlinePlaces = uiState.offlinePlaceResults,
                                onlinePlaces = uiState.onlinePlaceResults,
                                isSearchingPlaces = uiState.isSearchingPlaces,
                                placeSearchError = uiState.placeSearchError,
                                onSearchPlaces = onSearchPlaces,
                                onDetailsChange = onUpdateGroom,
                                showGender = false,
                                submitTestTag = "groom_form"
                            )
                            BirthDetailsFormCard(
                                title = lang.t("वधू का विवरण (Bride)", "Bride's Birth Details"),
                                details = uiState.brideInput,
                                language = lang,
                                offlinePlaces = uiState.offlinePlaceResults,
                                onlinePlaces = uiState.onlinePlaceResults,
                                isSearchingPlaces = uiState.isSearchingPlaces,
                                placeSearchError = uiState.placeSearchError,
                                onSearchPlaces = onSearchPlaces,
                                onDetailsChange = onUpdateBride,
                                showGender = false,
                                submitTestTag = "bride_form"
                            )
                        }
                    }
                }

                if (!uiState.matchValidationError.isNullOrBlank()) {
                    item {
                        Text(
                            text = uiState.matchValidationError,
                            style = MaterialTheme.typography.bodySmall,
                            color = ManglikCoral,
                            modifier = Modifier.testTag("match_validation_error")
                        )
                    }
                }

                item {
                    Button(
                        onClick = {
                            if (onMatch() && !isWide) {
                                formsExpanded = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = ObsidianMaroon
                        ),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("match_kundali_submit_button")
                    ) {
                        Icon(Icons.Filled.Favorite, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = lang.t("अष्टकूट गुण मिलान करें", "Calculate Ashtakoota Match"),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (match == null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardMaroon),
                        border = BorderStroke(1.dp, SubtleGoldBorder),
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("matching_empty_state")
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(22.dp)
                        ) {
                            Icon(Icons.Filled.Favorite, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = lang.t("वर एवं वधू का वास्तविक विवरण भरें", "Enter Both Birth Details for Matching"),
                                style = MaterialTheme.typography.titleMedium,
                                color = GoldBright,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = lang.t(
                                    "दोनों कुंडलियों के चंद्र नक्षत्र, राशि, योनि, नाड़ी और मंगल स्थिति के आधार पर सटीक 36 गुण मिलान प्राप्त करें।",
                                    "Calculates authentic 36-Guna Ashtakoota compatibility & Manglik Dosha from both exact birth charts."
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = MutedGoldText,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardMaroon),
                        border = BorderStroke(1.2.dp, GoldPrimary.copy(alpha = 0.6f)),
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("matching_result_card")
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            GunaScoreGauge(
                                score = match.totalScore,
                                maxScore = match.maxScore,
                                language = lang
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (lang == AppLanguage.HI) match.verdictTitleHi else match.verdictTitleEn,
                                style = MaterialTheme.typography.headlineSmall,
                                color = GoldBright
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (lang == AppLanguage.HI) match.verdictDescHi else match.verdictDescEn,
                                style = MaterialTheme.typography.bodyMedium,
                                color = WarmParchment,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                InfoBadgeBox(
                                    label = match.groomKundali.birthDetails.name,
                                    value = "${if (lang == AppLanguage.HI) match.groomKundali.moonRashi.nameHi else match.groomKundali.moonRashi.nameEn} • ${if (lang == AppLanguage.HI) match.groomKundali.moonNakshatraHi else match.groomKundali.moonNakshatraEn}",
                                    modifier = Modifier.weight(1f)
                                )
                                InfoBadgeBox(
                                    label = match.brideKundali.birthDetails.name,
                                    value = "${if (lang == AppLanguage.HI) match.brideKundali.moonRashi.nameHi else match.brideKundali.moonRashi.nameEn} • ${if (lang == AppLanguage.HI) match.brideKundali.moonNakshatraHi else match.brideKundali.moonNakshatraEn}",
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            val manglikWarning = match.groomKundali.isManglik != match.brideKundali.isManglik
                            Surface(
                                color = if (manglikWarning) CautionAmberBg else ShubhEmeraldBg,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(
                                    1.dp,
                                    if (manglikWarning) CautionAmber.copy(alpha = 0.6f) else ShubhEmerald.copy(alpha = 0.6f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (lang == AppLanguage.HI) match.manglikCompatHi else match.manglikCompatEn,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = IvoryCream,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = lang.t("अष्टकूट विस्तृत तालिका", "Ashtakoota Detailed Breakdown"),
                        style = MaterialTheme.typography.titleMedium,
                        color = GoldBright,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        match.kootas.forEach { k ->
                            KootaRowCard(koota = k, language = lang)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun KootaRowCard(koota: KootaScore, language: AppLanguage) {
    val ratio = (koota.obtained / koota.maximum).toFloat().coerceIn(0f, 1f)
    val barColor = when {
        ratio >= 0.7f -> ShubhEmerald
        ratio >= 0.4f -> GoldPrimary
        else -> ManglikCoral
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = CardMaroon),
        border = BorderStroke(1.dp, SubtleGoldBorder),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (language == AppLanguage.HI) koota.nameHi else koota.nameEn,
                        style = MaterialTheme.typography.titleSmall,
                        color = GoldBright
                    )
                    Text(
                        text = if (language == AppLanguage.HI) koota.descHi else koota.descEn,
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedGoldText
                    )
                }
                Text(
                    text = "${if (koota.obtained % 1.0 == 0.0) koota.obtained.toInt() else koota.obtained} / ${koota.maximum.toInt()}",
                    style = MaterialTheme.typography.titleMedium,
                    color = barColor,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { ratio },
                color = barColor,
                trackColor = DeepMaroonSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${language.t("वर", "Groom")}: ${if (language == AppLanguage.HI) koota.groomValueHi else koota.groomValueEn}",
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmParchment
                )
                Text(
                    text = "${language.t("वधू", "Bride")}: ${if (language == AppLanguage.HI) koota.brideValueHi else koota.brideValueEn}",
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmParchment
                )
            }
        }
    }
}
