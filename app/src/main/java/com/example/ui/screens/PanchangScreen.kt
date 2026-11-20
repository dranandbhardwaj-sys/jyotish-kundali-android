package com.example.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.AppLanguage
import com.example.model.BirthPlace
import com.example.model.Graha
import com.example.model.t
import com.example.ui.JyotishUiState
import com.example.ui.components.CitySelectorDialog
import com.example.ui.components.InfoBadgeBox
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun PanchangScreen(
    uiState: JyotishUiState,
    onShiftDay: (Int) -> Unit,
    onPickDate: (Int, Int, Int) -> Unit,
    onSelectPlace: (BirthPlace) -> Unit,
    onSearchPlaces: (String) -> Unit,
    onInspectGraha: (Graha) -> Unit,
    modifier: Modifier = Modifier
) {
    val lang = uiState.language
    val p = uiState.panchangResult ?: return
    val context = LocalContext.current
    var showCityPicker by remember { mutableStateOf(false) }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = modifier.fillMaxSize()
    ) {
        item {
            SectionHeader(
                title = lang.t("दैनिक वैदिक पंचांग", "Daily Vedic Panchang"),
                subtitle = lang.t("स्थानीय सूर्योदय आधारित तिथि, नक्षत्र, योग, करण", "Sunrise-based Tithi, Nakshatra, Yoga & Muhurat"),
                icon = Icons.Filled.CalendarMonth
            )
        }

        // Date & City Control Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardMaroon),
                border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = { onShiftDay(-1) },
                            modifier = Modifier.testTag("panchang_prev_day")
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = lang.t("पिछला दिन", "Previous day"),
                                tint = GoldPrimary
                            )
                        }
                        Surface(
                            onClick = {
                                DatePickerDialog(
                                    context,
                                    { _, y, m, d -> onPickDate(y, m + 1, d) },
                                    p.year, p.month - 1, p.day
                                ).show()
                            },
                            color = DeepMaroonSurface,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, SubtleGoldBorder),
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp)
                                .testTag("panchang_date_label")
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "${String.format("%02d/%02d/%04d", p.day, p.month, p.year)} • ${if (lang == AppLanguage.HI) p.varaHi else p.varaEn}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = GoldBright,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (lang == AppLanguage.HI) p.pakshaHi else p.pakshaEn,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MutedGoldText
                                )
                            }
                        }
                        IconButton(
                            onClick = { onShiftDay(1) },
                            modifier = Modifier.testTag("panchang_next_day")
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = lang.t("अगला दिन", "Next day"),
                                tint = GoldPrimary
                            )
                        }
                    }

                    Surface(
                        onClick = {
                            onSearchPlaces("")
                            showCityPicker = true
                        },
                        color = DeepMaroonSurface,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, SubtleGoldBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("panchang_place_button")
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = p.place.displayName(lang),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = IvoryCream,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Text(
                                text = p.place.coordinatesLabel(),
                                style = MaterialTheme.typography.labelSmall,
                                color = MutedGoldText
                            )
                        }
                    }
                }
            }
        }

        // Panchang 5 Angas Grid
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardMaroon),
                border = BorderStroke(1.dp, SubtleGoldBorder),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = lang.t("पंचांग के पाँच प्रमुख अंग", "Five Sacred Limbs (Panch-Anga)"),
                        style = MaterialTheme.typography.titleMedium,
                        color = GoldBright,
                        fontWeight = FontWeight.Bold
                    )

                    // Tithi with Progress & End Time
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DeepMaroonSurface)
                            .border(1.dp, SubtleGoldBorder, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(lang.t("1. तिथि (Tithi)", "1. Tithi"), style = MaterialTheme.typography.labelMedium, color = GoldPrimary)
                            Text("${lang.t("समाप्ति ~", "Ends ~")} ${p.tithiEndTime} (${p.tithiProgressPercent}%)", style = MaterialTheme.typography.labelSmall, color = MutedGoldText)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${if (lang == AppLanguage.HI) p.pakshaHi else p.pakshaEn} — ${if (lang == AppLanguage.HI) p.tithiHi else p.tithiEn}",
                            style = MaterialTheme.typography.titleMedium,
                            color = IvoryCream,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { p.tithiProgressPercent / 100f },
                            color = GoldPrimary,
                            trackColor = ElevatedMaroon,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        InfoBadgeBox(
                            label = lang.t("2. वार (Vara)", "2. Vara (Weekday)"),
                            value = if (lang == AppLanguage.HI) p.varaHi else p.varaEn,
                            modifier = Modifier.weight(1f)
                        )
                        InfoBadgeBox(
                            label = "${lang.t("3. नक्षत्र", "3. Nakshatra")} (${lang.t("~", "~")}${p.nakshatraEndTime})",
                            value = "${if (lang == AppLanguage.HI) p.nakshatraHi else p.nakshatraEn} (${lang.t("पाद", "P")} ${p.nakshatraPada})",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        InfoBadgeBox(
                            label = "${lang.t("4. योग", "4. Yoga")} (${lang.t("~", "~")}${p.yogaEndTime})",
                            value = if (lang == AppLanguage.HI) p.yogaHi else p.yogaEn,
                            modifier = Modifier.weight(1f)
                        )
                        InfoBadgeBox(
                            label = lang.t("5. करण (Karana)", "5. Karana"),
                            value = if (lang == AppLanguage.HI) p.karanaHi else p.karanaEn,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Muhurat & Solar/Lunar Timings
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardMaroon),
                border = BorderStroke(1.dp, SubtleGoldBorder),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = lang.t("सूर्य, चंद्र एवं शुभ-अशुभ मुहूर्त", "Solar, Lunar & Muhurat Timings"),
                        style = MaterialTheme.typography.titleMedium,
                        color = GoldBright,
                        fontWeight = FontWeight.Bold
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        InfoBadgeBox(
                            label = lang.t("सूर्योदय", "Sunrise"),
                            value = p.sunrise,
                            modifier = Modifier.weight(1f)
                        )
                        InfoBadgeBox(
                            label = lang.t("सूर्यास्त", "Sunset"),
                            value = p.sunset,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        MuhuratTile(
                            label = lang.t("अभिजीत मुहूर्त (शुभ)", "Abhijit Muhurat (Auspicious)"),
                            value = p.abhijitMuhurat,
                            accent = ShubhEmerald,
                            bg = ShubhEmeraldBg,
                            modifier = Modifier.weight(1f)
                        )
                        MuhuratTile(
                            label = lang.t("राहु काल (त्याज्य)", "Rahu Kaal (Avoid)"),
                            value = p.rahuKaal,
                            accent = ManglikCoral,
                            bg = ManglikCoralBg,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        InfoBadgeBox(
                            label = lang.t("ब्रह्म मुहूर्त", "Brahma Muhurat"),
                            value = p.brahmaMuhurat,
                            modifier = Modifier.weight(1f)
                        )
                        InfoBadgeBox(
                            label = lang.t("दिशा शूल", "Disha Shool"),
                            value = if (lang == AppLanguage.HI) p.dishaShoolHi else p.dishaShoolEn,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        InfoBadgeBox(
                            label = lang.t("चंद्र राशि", "Moon Sign"),
                            value = "${p.moonRashi.symbol} ${if (lang == AppLanguage.HI) p.moonRashi.nameHi else p.moonRashi.nameEn}",
                            modifier = Modifier.weight(1f)
                        )
                        InfoBadgeBox(
                            label = lang.t("सूर्य राशि", "Sun Sign"),
                            value = "${p.sunRashi.symbol} ${if (lang == AppLanguage.HI) p.sunRashi.nameHi else p.sunRashi.nameEn}",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Ruling Grahas & Auspicious Note
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = RoyalCrimson.copy(alpha = 0.45f)),
                border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = lang.t("आज का ज्योतिषीय संदेश", "Today's Jyotish Insight"),
                        style = MaterialTheme.typography.titleSmall,
                        color = GoldBright
                    )
                    Text(
                        text = if (lang == AppLanguage.HI) p.auspiciousNoteHi else p.auspiciousNoteEn,
                        style = MaterialTheme.typography.bodyMedium,
                        color = IvoryCream
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AssistChip(
                            onClick = { onInspectGraha(p.varaLord) },
                            label = {
                                Text(
                                    "${lang.t("दिन स्वामी", "Day Lord")}: ${if (lang == AppLanguage.HI) p.varaLord.nameHi else p.varaLord.nameEn}",
                                    color = GoldBright
                                )
                            },
                            border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f))
                        )
                        AssistChip(
                            onClick = { onInspectGraha(p.nakshatraLord) },
                            label = {
                                Text(
                                    "${lang.t("नक्षत्र स्वामी", "Nak. Lord")}: ${if (lang == AppLanguage.HI) p.nakshatraLord.nameHi else p.nakshatraLord.nameEn}",
                                    color = GoldBright
                                )
                            },
                            border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f))
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showCityPicker) {
        CitySelectorDialog(
            language = lang,
            selectedCity = p.place,
            offlinePlaces = uiState.offlinePlaceResults,
            onlinePlaces = uiState.onlinePlaceResults,
            isSearching = uiState.isSearchingPlaces,
            searchError = uiState.placeSearchError,
            onSearchQueryChange = onSearchPlaces,
            onSelect = {
                onSelectPlace(it)
                showCityPicker = false
            },
            onDismiss = { showCityPicker = false }
        )
    }
}

@Composable
private fun MuhuratTile(
    label: String,
    value: String,
    accent: Color,
    bg: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .border(1.dp, accent.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = accent)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = IvoryCream,
            fontWeight = FontWeight.Bold
        )
    }
}
