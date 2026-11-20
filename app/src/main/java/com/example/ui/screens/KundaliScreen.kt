package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Hexagon
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.model.*
import com.example.ui.JyotishUiState
import com.example.ui.components.BirthDetailsFormCard
import com.example.ui.components.InfoBadgeBox
import com.example.ui.components.KundaliChartView
import com.example.ui.components.PremiumLockCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun KundaliScreen(
    uiState: JyotishUiState,
    onUpdateInput: (BirthDetails) -> Unit,
    onSearchPlaces: (String) -> Unit,
    onGenerate: () -> Boolean,
    onReset: () -> Unit,
    onSetChartStyle: (ChartStyle) -> Unit,
    onSetChartDivision: (ChartDivision) -> Unit,
    onInspectGraha: (Graha, GrahaPosition) -> Unit,
    onNavigateToPremium: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang = uiState.language
    val kundali = uiState.kundaliResult
    val subscriptionStatus = uiState.membership.status
    val isPremium = subscriptionStatus == SubscriptionStatus.PREMIUM
    var selectedTab by rememberSaveable { mutableIntStateOf(0) } // 0=Grahas, 1=Avakahada, 2=Dasha
    var formExpanded by rememberSaveable(kundali) { mutableStateOf(kundali == null) }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWide = maxWidth >= 860.dp

        if (isWide) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.weight(0.42f)
                ) {
                    item {
                        SectionHeader(
                            title = lang.t("जन्म कुंडली निर्माण", "Kundali Generator"),
                            subtitle = lang.t("सटीक जन्म विवरण दर्ज करें", "Enter birth date, time & place"),
                            icon = Icons.Filled.Hexagon
                        )
                    }
                    item {
                        BirthDetailsFormCard(
                            title = lang.t("जन्म विवरण", "Birth Details"),
                            details = uiState.kundaliInput,
                            language = lang,
                            offlinePlaces = uiState.offlinePlaceResults,
                            onlinePlaces = uiState.onlinePlaceResults,
                            isSearchingPlaces = uiState.isSearchingPlaces,
                            placeSearchError = uiState.placeSearchError,
                            onSearchPlaces = onSearchPlaces,
                            onDetailsChange = onUpdateInput,
                            onSubmit = { onGenerate() },
                            submitLabel = lang.t("कुंडली बनाएं", "Generate Kundali"),
                            submitTestTag = "generate_kundali_button",
                            validationError = uiState.kundaliValidationError
                        )
                    }
                    if (kundali != null) {
                        item {
                            if (isPremium) {
                                AvakahadaCard(kundali = kundali, language = lang)
                            } else {
                                PremiumLockCard(
                                    title = lang.t("अवकहड़ा चक्र (प्रीमियम)", "Avakahada Chakra (Premium)"),
                                    description = lang.t(
                                        "वर्ण, वश्य, योनि, गण, नाड़ी एवं मांगलिक विश्लेषण प्रीमियम सदस्यता में उपलब्ध है।",
                                        "Detailed Varna, Vashya, Yoni, Gana, Nadi & Manglik analysis requires Premium."
                                    ),
                                    status = subscriptionStatus,
                                    language = lang,
                                    onUpgradeClick = onNavigateToPremium
                                )
                            }
                        }
                    }
                }

                LazyColumn(
                    contentPadding = PaddingValues(bottom = 92.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.weight(0.58f)
                ) {
                    if (kundali == null) {
                        item {
                            KundaliEmptyStateCard(language = lang)
                        }
                    } else {
                        item {
                            KundaliSummaryBanner(kundali = kundali, language = lang, onReset = onReset)
                        }
                        item {
                            KundaliChartSection(
                                kundali = kundali,
                                chartStyle = uiState.chartStyle,
                                chartDivision = uiState.chartDivision,
                                isPremium = isPremium,
                                subscriptionStatus = subscriptionStatus,
                                language = lang,
                                onSetChartStyle = onSetChartStyle,
                                onSetChartDivision = onSetChartDivision,
                                onNavigateToPremium = onNavigateToPremium
                            )
                        }
                        item {
                            KundaliDetailsTabs(
                                kundali = kundali,
                                selectedTab = selectedTab,
                                isPremium = isPremium,
                                subscriptionStatus = subscriptionStatus,
                                onSelectTab = { selectedTab = it },
                                language = lang,
                                onInspectGraha = onInspectGraha,
                                onNavigateToPremium = onNavigateToPremium
                            )
                        }
                        item {
                            DetailedReportSection(
                                kundali = kundali,
                                isPremium = isPremium,
                                subscriptionStatus = subscriptionStatus,
                                language = lang,
                                onNavigateToPremium = onNavigateToPremium
                            )
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 92.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("kundali_lazy_list")
            ) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SectionHeader(
                            title = lang.t("जन्म कुंडली", "Vedic Kundali"),
                            subtitle = lang.t("लग्न, नवांश, ग्रह स्थिति व दशा", "Lagna, Navamsha, Grahas & Dasha"),
                            icon = Icons.Filled.Hexagon,
                            modifier = Modifier.weight(1f)
                        )
                        if (kundali != null) {
                            TextButton(
                                onClick = { formExpanded = !formExpanded },
                                modifier = Modifier.testTag("toggle_kundali_form_btn")
                            ) {
                                Text(
                                    text = if (formExpanded) lang.t("फॉर्म छुपाएं", "Hide Form") else lang.t("विवरण बदलें", "Edit Details"),
                                    color = GoldPrimary
                                )
                                Icon(
                                    imageVector = if (formExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                    contentDescription = null,
                                    tint = GoldPrimary
                                )
                            }
                        }
                    }
                }

                if (formExpanded) {
                    item {
                        BirthDetailsFormCard(
                            title = lang.t("जन्म विवरण दर्ज करें", "Enter Birth Details"),
                            details = uiState.kundaliInput,
                            language = lang,
                            offlinePlaces = uiState.offlinePlaceResults,
                            onlinePlaces = uiState.onlinePlaceResults,
                            isSearchingPlaces = uiState.isSearchingPlaces,
                            placeSearchError = uiState.placeSearchError,
                            onSearchPlaces = onSearchPlaces,
                            onDetailsChange = onUpdateInput,
                            onSubmit = {
                                if (onGenerate()) {
                                    formExpanded = false
                                }
                            },
                            submitLabel = lang.t("कुंडली बनाएं", "Generate Kundali"),
                            submitTestTag = "generate_kundali_button",
                            validationError = uiState.kundaliValidationError
                        )
                    }
                }

                if (kundali == null) {
                    item {
                        KundaliEmptyStateCard(language = lang)
                    }
                } else {
                    item {
                        KundaliSummaryBanner(kundali = kundali, language = lang, onReset = onReset)
                    }
                    item {
                        KundaliChartSection(
                            kundali = kundali,
                            chartStyle = uiState.chartStyle,
                            chartDivision = uiState.chartDivision,
                            isPremium = isPremium,
                            subscriptionStatus = subscriptionStatus,
                            language = lang,
                            onSetChartStyle = onSetChartStyle,
                            onSetChartDivision = onSetChartDivision,
                            onNavigateToPremium = onNavigateToPremium
                        )
                    }
                    item {
                        KundaliDetailsTabs(
                            kundali = kundali,
                            selectedTab = selectedTab,
                            isPremium = isPremium,
                            subscriptionStatus = subscriptionStatus,
                            onSelectTab = { selectedTab = it },
                            language = lang,
                            onInspectGraha = onInspectGraha,
                            onNavigateToPremium = onNavigateToPremium
                        )
                    }
                    item {
                        DetailedReportSection(
                            kundali = kundali,
                            isPremium = isPremium,
                            subscriptionStatus = subscriptionStatus,
                            language = lang,
                            onNavigateToPremium = onNavigateToPremium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun KundaliEmptyStateCard(language: AppLanguage) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardMaroon),
        border = BorderStroke(1.dp, SubtleGoldBorder),
        shape = MaterialTheme.shapes.large,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("kundali_empty_state")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.AutoAwesome,
                contentDescription = null,
                tint = GoldPrimary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = language.t("अपनी वास्तविक जन्म कुंडली बनाएं", "Generate Your Real Vedic Birth Chart"),
                style = MaterialTheme.typography.titleMedium,
                color = GoldBright,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = language.t(
                    "ऊपर नाम, जन्म तिथि, जन्म समय और जन्म स्थान (शहर खोज या कस्टम अक्षांश/देशांतर) भरें और 'कुंडली बनाएं' दबाएं।",
                    "Enter your name, date, time, and birthplace (search worldwide or enter custom Lat/Lon) above, then tap 'Generate Kundali'."
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MutedGoldText,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun KundaliSummaryBanner(
    kundali: KundaliResult,
    language: AppLanguage,
    onReset: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardMaroon),
        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.45f)),
        shape = MaterialTheme.shapes.large,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("kundali_result_banner")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = kundali.birthDetails.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = GoldBright
                    )
                    val d = kundali.birthDetails
                    val ampm = if (d.hour24 < 12) "AM" else "PM"
                    val h12 = if (d.hour24 % 12 == 0) 12 else d.hour24 % 12
                    Text(
                        text = "${String.format("%02d/%02d/%04d %02d:%02d %s", d.day, d.month, d.year, h12, d.minute, ampm)} • ${d.place.displayName(language)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedGoldText
                    )
                    Text(
                        text = d.place.coordinatesLabel(),
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldMuted
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = if (kundali.isManglik) ManglikCoralBg else ShubhEmeraldBg,
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, if (kundali.isManglik) ManglikCoral else ShubhEmerald)
                    ) {
                        Text(
                            text = if (kundali.isManglik) language.t("मांगलिक योग", "Manglik") else language.t("अमांगलिक (शुभ)", "Non-Manglik"),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (kundali.isManglik) ManglikCoral else ShubhEmerald,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                    IconButton(
                        onClick = onReset,
                        modifier = Modifier.testTag("reset_kundali_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = langOrResetDesc(language),
                            tint = GoldPrimary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                InfoBadgeBox(
                    label = language.t("लग्न (Asc)", "Ascendant"),
                    value = "${if (language == AppLanguage.HI) kundali.lagnaRashi.nameHi else kundali.lagnaRashi.nameEn} ${String.format("%.1f°", kundali.lagnaDegree)}",
                    modifier = Modifier.weight(1f)
                )
                InfoBadgeBox(
                    label = language.t("चंद्र राशि", "Moon Sign"),
                    value = "${kundali.moonRashi.symbol} ${if (language == AppLanguage.HI) kundali.moonRashi.nameHi else kundali.moonRashi.nameEn}",
                    modifier = Modifier.weight(1f)
                )
                InfoBadgeBox(
                    label = language.t("जन्म नक्षत्र", "Nakshatra"),
                    value = "${if (language == AppLanguage.HI) kundali.moonNakshatraHi else kundali.moonNakshatraEn} (${kundali.moonPada})",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

private fun langOrResetDesc(language: AppLanguage): String =
    language.t("नई कुंडली बनाएं", "Clear and start new Kundali")

@Composable
private fun KundaliChartSection(
    kundali: KundaliResult,
    chartStyle: ChartStyle,
    chartDivision: ChartDivision,
    isPremium: Boolean,
    subscriptionStatus: SubscriptionStatus,
    language: AppLanguage,
    onSetChartStyle: (ChartStyle) -> Unit,
    onSetChartDivision: (ChartDivision) -> Unit,
    onNavigateToPremium: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardMaroon),
        border = BorderStroke(1.dp, SubtleGoldBorder),
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                ChartStyle.entries.forEach { style ->
                    val selected = chartStyle == style
                    FilterChip(
                        selected = selected,
                        onClick = { onSetChartStyle(style) },
                        label = {
                            Text(
                                text = if (language == AppLanguage.HI) style.hi else style.en,
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = RoyalCrimson,
                            selectedLabelColor = GoldBright,
                            containerColor = DeepMaroonSurface,
                            labelColor = MutedGoldText
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selected,
                            borderColor = SubtleGoldBorder,
                            selectedBorderColor = GoldPrimary
                        ),
                        modifier = Modifier.testTag("chart_style_${style.name.lowercase()}")
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                ChartDivision.entries.forEach { div ->
                    val selected = chartDivision == div
                    val lockedDiv = div == ChartDivision.D9 && !isPremium
                    FilterChip(
                        selected = selected,
                        onClick = { onSetChartDivision(div) },
                        leadingIcon = if (lockedDiv) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Lock,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        } else null,
                        label = {
                            Text(
                                text = if (language == AppLanguage.HI) div.hi else div.en,
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldPrimary,
                            selectedLabelColor = ObsidianMaroon,
                            containerColor = DeepMaroonSurface,
                            labelColor = MutedGoldText
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selected,
                            borderColor = SubtleGoldBorder,
                            selectedBorderColor = GoldPrimary
                        ),
                        modifier = Modifier.testTag("chart_div_${div.name.lowercase()}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (chartDivision == ChartDivision.D9 && !isPremium) {
                PremiumLockCard(
                    title = language.t("नवांश कुंडली (D-9) प्रीमियम में उपलब्ध", "Navamsha (D-9) Chart — Premium"),
                    description = language.t(
                        "विवाह, धर्म और सूक्ष्म ग्रह बल जानने के लिए नवांश चक्र (D-9) को प्रीमियम सदस्यता द्वारा अनलॉक करें।",
                        "Unlock the divisional Navamsha (D-9) chart for marriage and planetary strength analysis with Premium."
                    ),
                    status = subscriptionStatus,
                    language = language,
                    onUpgradeClick = onNavigateToPremium,
                    upgradeButtonTag = "kundali_d9_upgrade_btn",
                    modifier = Modifier.testTag("kundali_d9_locked_card")
                )
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    KundaliChartView(
                        kundali = kundali,
                        chartStyle = chartStyle,
                        division = chartDivision,
                        language = language,
                        modifier = Modifier
                            .widthIn(max = 380.dp)
                            .testTag("kundali_chart_canvas")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = language.t(
                        "* चिह्नित ग्रह वक्री (Retrograde) स्थिति में हैं • लाहिड़ी अयनांश: ${String.format("%.2f°", kundali.ayanamsha)}",
                        "* denotes Retrograde Graha • Lahiri Ayanamsha: ${String.format("%.2f°", kundali.ayanamsha)}"
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedGoldText
                )
            }
        }
    }
}

@Composable
private fun KundaliDetailsTabs(
    kundali: KundaliResult,
    selectedTab: Int,
    isPremium: Boolean,
    subscriptionStatus: SubscriptionStatus,
    onSelectTab: (Int) -> Unit,
    language: AppLanguage,
    onInspectGraha: (Graha, GrahaPosition) -> Unit,
    onNavigateToPremium: () -> Unit
) {
    val tabs = listOf(
        language.t("ग्रह स्थिति", "Grahas"),
        if (isPremium) language.t("अवकहड़ा चक्र", "Avakahada") else language.t("अवकहड़ा 🔒", "Avakahada 🔒"),
        if (isPremium) language.t("विंशोत्तरी दशा", "Mahadasha") else language.t("दशा 🔒", "Dasha 🔒")
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = CardMaroon,
            contentColor = GoldBright,
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, SubtleGoldBorder, RoundedCornerShape(14.dp))
        ) {
            tabs.forEachIndexed { idx, label ->
                Tab(
                    selected = selectedTab == idx,
                    onClick = { onSelectTab(idx) },
                    text = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    selectedContentColor = GoldBright,
                    unselectedContentColor = MutedGoldText,
                    modifier = Modifier.testTag("kundali_subtab_$idx")
                )
            }
        }

        when (selectedTab) {
            0 -> {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    kundali.grahaPositions.forEach { gp ->
                        GrahaPositionRow(
                            position = gp,
                            language = language,
                            onClick = { onInspectGraha(gp.graha, gp) }
                        )
                    }
                }
            }
            1 -> {
                if (isPremium) {
                    AvakahadaCard(kundali = kundali, language = language)
                } else {
                    PremiumLockCard(
                        title = language.t("अवकहड़ा एवं मांगलिक विवरण (प्रीमियम)", "Avakahada & Manglik Report (Premium)"),
                        description = language.t(
                            "पूरी अवकहड़ा तालिका और विस्तृत मांगलिक दोष निष्कर्ष देखने के लिए प्रीमियम सक्रिय करें।",
                            "Upgrade to Premium to view your complete Avakahada Chakra and Manglik analysis."
                        ),
                        status = subscriptionStatus,
                        language = language,
                        onUpgradeClick = onNavigateToPremium,
                        upgradeButtonTag = "kundali_avakahada_upgrade_btn",
                        modifier = Modifier.testTag("kundali_avakahada_locked_card")
                    )
                }
            }
            else -> {
                if (isPremium) {
                    MahadashaList(dashas = kundali.dashas, language = language)
                } else {
                    PremiumLockCard(
                        title = language.t("विंशोत्तरी महादशा एवं अंतर्दशा (प्रीमियम)", "Vimshottari Mahadasha & Antardasha (Premium)"),
                        description = language.t(
                            "120 वर्षीय विंशोत्तरी महादशा और 9 अंतर्दशा तिथियों का संपूर्ण विवरण प्रीमियम में अनलॉक करें।",
                            "Unlock full 120-year Vimshottari Mahadasha & Antardasha timelines with Premium."
                        ),
                        status = subscriptionStatus,
                        language = language,
                        onUpgradeClick = onNavigateToPremium,
                        upgradeButtonTag = "kundali_dasha_upgrade_btn",
                        modifier = Modifier.testTag("kundali_dasha_locked_card")
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailedReportSection(
    kundali: KundaliResult,
    isPremium: Boolean,
    subscriptionStatus: SubscriptionStatus,
    language: AppLanguage,
    onNavigateToPremium: () -> Unit
) {
    if (!isPremium) {
        PremiumLockCard(
            title = language.t("विस्तृत कुंडली रिपोर्ट एवं विशेष ज्योतिषीय अंतर्दृष्टि", "Detailed Kundali Report & Expanded Insights"),
            description = language.t(
                "लग्नेश बल, उच्च/स्वग्रही ग्रह योग, वर्तमान दशा प्रभाव और व्यक्तिगत वैदिक उपाय प्रीमियम सदस्यता में उपलब्ध हैं।",
                "Unlock Lagna Lord strength, exalted/own-sign yogas, active Dasha impact & personalized Vedic remedies."
            ),
            status = subscriptionStatus,
            language = language,
            onUpgradeClick = onNavigateToPremium,
            upgradeButtonTag = "kundali_report_upgrade_btn",
            modifier = Modifier.testTag("kundali_report_locked_card")
        )
        return
    }

    val lagnaLord = kundali.lagnaRashi.lord
    val lagnaLordPos = kundali.grahaPositions.firstOrNull { it.graha == lagnaLord }
    val strongGrahas = kundali.grahaPositions.filter {
        it.dignity == GrahaDignity.EXALTED || it.dignity == GrahaDignity.OWN_SIGN
    }
    val currentMahadasha = kundali.dashas.firstOrNull { it.isCurrent } ?: kundali.dashas.firstOrNull()
    val currentAntar = currentMahadasha?.antardashas?.firstOrNull { it.isCurrent }

    Card(
        colors = CardDefaults.cardColors(containerColor = RoyalCrimson.copy(alpha = 0.35f)),
        border = BorderStroke(1.2.dp, GoldPrimary),
        shape = MaterialTheme.shapes.large,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("kundali_detailed_report_card")
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.WorkspacePremium, contentDescription = null, tint = GoldBright)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = language.t("प्रीमियम विस्तृत कुंडली रिपोर्ट", "Premium Detailed Kundali Report"),
                    style = MaterialTheme.typography.titleMedium,
                    color = GoldBright,
                    fontWeight = FontWeight.Bold
                )
            }

            if (lagnaLordPos != null) {
                val lordName = if (language == AppLanguage.HI) lagnaLord.nameHi else lagnaLord.nameEn
                val rashiName = if (language == AppLanguage.HI) lagnaLordPos.rashi.nameHi else lagnaLordPos.rashi.nameEn
                val digName = if (language == AppLanguage.HI) lagnaLordPos.dignity.hi else lagnaLordPos.dignity.en
                ReportInsightBlock(
                    title = language.t("1. लग्न एवं लग्नेश विश्लेषण", "1. Ascendant & Lagna Lord Insight"),
                    body = language.t(
                        "आपकी कुंडली का लग्नेश $lordName भाव ${lagnaLordPos.house} में $rashiName राशि ($digName) में स्थित है। यह जातक के स्वास्थ्य, व्यक्तित्व और जीवन दिशा को दर्शाता है।",
                        "Your Lagna Lord $lordName is placed in House ${lagnaLordPos.house} in $rashiName ($digName), shaping vitality, core temperament, and life path."
                    )
                )
            }

            val strongNames = if (strongGrahas.isEmpty()) {
                language.t("सम एवं मिश्रित ग्रह बल — नियमित साधना से शुभ फल", "Balanced planetary dignities across houses")
            } else {
                strongGrahas.joinToString(", ") {
                    val gn = if (language == AppLanguage.HI) it.graha.nameHi else it.graha.nameEn
                    val dn = if (language == AppLanguage.HI) it.dignity.hi else it.dignity.en
                    "$gn ($dn, ${language.t("भाव", "H")} ${it.house})"
                }
            }
            ReportInsightBlock(
                title = language.t("2. प्रमुख ग्रह योग एवं बल", "2. Key Planetary Strengths"),
                body = strongNames
            )

            if (currentMahadasha != null) {
                val mdName = if (language == AppLanguage.HI) currentMahadasha.graha.nameHi else currentMahadasha.graha.nameEn
                val adName = if (currentAntar != null) {
                    if (language == AppLanguage.HI) currentAntar.graha.nameHi else currentAntar.graha.nameEn
                } else ""
                ReportInsightBlock(
                    title = language.t("3. सक्रिय दशा मार्गदर्शन", "3. Active Dasha Guidance"),
                    body = language.t(
                        "वर्तमान समय में $mdName महादशा${if (adName.isNotEmpty()) " एवं $adName अंतर्दशा" else ""} (${currentMahadasha.startLabel} – ${currentMahadasha.endLabel}) चल रही है। ${currentMahadasha.graha.mantra} का जप विशेष लाभकारी रहेगा।",
                        "Currently running $mdName Mahadasha${if (adName.isNotEmpty()) " / $adName Antardasha" else ""} (${currentMahadasha.startLabel} – ${currentMahadasha.endLabel}). Regular recitation of ${currentMahadasha.graha.mantra} supports positive outcomes."
                    )
                )
            }

            ReportInsightBlock(
                title = language.t("4. मांगलिक एवं जन्म नक्षत्र निष्कर्ष", "4. Manglik & Nakshatra Synthesis"),
                body = if (language == AppLanguage.HI) {
                    "${kundali.manglikReasonHi} • जन्म नक्षत्र ${kundali.moonNakshatraHi} (पाद ${kundali.moonPada})।"
                } else {
                    "${kundali.manglikReasonEn} • Birth Nakshatra ${kundali.moonNakshatraEn} (Pada ${kundali.moonPada})."
                }
            )
        }
    }
}

@Composable
private fun ReportInsightBlock(title: String, body: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DeepMaroonSurface)
            .padding(12.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.labelLarge, color = GoldPrimary, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = body, style = MaterialTheme.typography.bodySmall, color = WarmParchment)
    }
}

@Composable
private fun GrahaPositionRow(
    position: GrahaPosition,
    language: AppLanguage,
    onClick: () -> Unit
) {
    val g = position.graha
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = CardMaroon),
        border = BorderStroke(1.dp, SubtleGoldBorder),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("kundali_graha_row_${g.name.lowercase()}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(g.accentColor.copy(alpha = 0.16f))
                    .border(1.dp, g.accentColor.copy(alpha = 0.7f), CircleShape)
            ) {
                Text(
                    text = if (language == AppLanguage.HI) g.abbrHi else g.abbrEn,
                    color = g.accentColor,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (language == AppLanguage.HI) g.nameHi else g.nameEn,
                        style = MaterialTheme.typography.titleSmall,
                        color = IvoryCream
                    )
                    if (position.isRetrograde && g != Graha.RAHU && g != Graha.KETU) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = CautionAmberBg,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = language.t("वक्री", "Retro"),
                                style = MaterialTheme.typography.labelSmall,
                                color = CautionAmber,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                val rashiName = if (language == AppLanguage.HI) position.rashi.nameHi else position.rashi.nameEn
                val navName = if (language == AppLanguage.HI) position.navamshaRashi.nameHi else position.navamshaRashi.nameEn
                val nakName = if (language == AppLanguage.HI) position.nakshatraHi else position.nakshatraEn
                Text(
                    text = "$rashiName (${String.format("%.2f°", position.degreeInRashi)}) • D9: $navName",
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmParchment
                )
                Text(
                    text = "$nakName (${language.t("पाद", "Pada")} ${position.pada})",
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedGoldText
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${language.t("भाव", "H")} ${position.house}",
                    style = MaterialTheme.typography.labelLarge,
                    color = GoldBright
                )
                val digColor = when (position.dignity) {
                    GrahaDignity.EXALTED, GrahaDignity.OWN_SIGN -> ShubhEmerald
                    GrahaDignity.DEBILITATED -> ManglikCoral
                    else -> MutedGoldText
                }
                Text(
                    text = if (language == AppLanguage.HI) position.dignity.hi else position.dignity.en,
                    style = MaterialTheme.typography.labelSmall,
                    color = digColor
                )
            }
        }
    }
}

@Composable
private fun AvakahadaCard(kundali: KundaliResult, language: AppLanguage) {
    val a = kundali.avakahada
    Card(
        colors = CardDefaults.cardColors(containerColor = CardMaroon),
        border = BorderStroke(1.dp, SubtleGoldBorder),
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = language.t("अवकहड़ा एवं मांगलिक विवरण", "Avakahada & Manglik Summary"),
                style = MaterialTheme.typography.titleMedium,
                color = GoldBright,
                fontWeight = FontWeight.Bold
            )
            val rows = listOf(
                language.t("वर्ण", "Varna") to (if (language == AppLanguage.HI) a.varnaHi else a.varnaEn),
                language.t("वश्य", "Vashya") to (if (language == AppLanguage.HI) a.vashyaHi else a.vashyaEn),
                language.t("योनि", "Yoni") to (if (language == AppLanguage.HI) a.yoniHi else a.yoniEn),
                language.t("गण", "Gana") to (if (language == AppLanguage.HI) a.ganaHi else a.ganaEn),
                language.t("नाड़ी", "Nadi") to (if (language == AppLanguage.HI) a.nadiHi else a.nadiEn),
                language.t("तत्त्व", "Element") to (if (language == AppLanguage.HI) a.tatvaHi else a.tatvaEn),
                language.t("पाया", "Paya") to (if (language == AppLanguage.HI) a.payaHi else a.payaEn)
            )
            rows.chunked(2).forEach { pair ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    pair.forEach { (k, v) ->
                        InfoBadgeBox(label = k, value = v, modifier = Modifier.weight(1f))
                    }
                    if (pair.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
            }
            Surface(
                color = if (kundali.isManglik) ManglikCoralBg else ShubhEmeraldBg,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, if (kundali.isManglik) ManglikCoral.copy(alpha = 0.6f) else ShubhEmerald.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (language == AppLanguage.HI) kundali.manglikReasonHi else kundali.manglikReasonEn,
                    style = MaterialTheme.typography.bodySmall,
                    color = IvoryCream,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}

@Composable
private fun MahadashaList(dashas: List<DashaPeriod>, language: AppLanguage) {
    var expandedIdx by remember {
        mutableIntStateOf(dashas.indexOfFirst { it.isCurrent }.coerceAtLeast(0))
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = language.t(
                "अंतर्दशा (Sub-periods) देखने के लिए किसी भी महादशा पर स्पर्श करें",
                "Tap any Mahadasha to expand its 9 Antardasha sub-periods"
            ),
            style = MaterialTheme.typography.labelSmall,
            color = MutedGoldText
        )
        dashas.forEachIndexed { idx, d ->
            val isExpanded = expandedIdx == idx
            Card(
                onClick = { expandedIdx = if (isExpanded) -1 else idx },
                colors = CardDefaults.cardColors(
                    containerColor = if (d.isCurrent) RoyalCrimson.copy(alpha = 0.55f) else CardMaroon
                ),
                border = BorderStroke(1.dp, if (d.isCurrent) GoldPrimary else SubtleGoldBorder),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dasha_card_${d.graha.name.lowercase()}")
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(d.graha.accentColor.copy(alpha = 0.18f))
                            ) {
                                Text(
                                    text = if (language == AppLanguage.HI) d.graha.abbrHi else d.graha.abbrEn,
                                    color = d.graha.accentColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "${if (language == AppLanguage.HI) d.graha.nameHi else d.graha.nameEn} ${language.t("महादशा", "Mahadasha")}",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = IvoryCream
                                )
                                Text(
                                    text = "${String.format("%.1f", d.durationYears)} ${language.t("वर्ष", "Years")}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MutedGoldText
                                )
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${d.startLabel} – ${d.endLabel}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = GoldBright
                                )
                                if (d.isCurrent) {
                                    Text(
                                        text = language.t("वर्तमान दशा", "Active Now"),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ShubhEmerald,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                contentDescription = null,
                                tint = GoldPrimary
                            )
                        }
                    }

                    AnimatedVisibility(visible = isExpanded) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(DeepMaroonSurface)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = language.t("अंतर्दशा विवरण", "Antardasha Sub-Periods"),
                                style = MaterialTheme.typography.labelMedium,
                                color = GoldPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            d.antardashas.forEach { ad ->
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "${if (language == AppLanguage.HI) d.graha.abbrHi else d.graha.abbrEn} / ${if (language == AppLanguage.HI) ad.graha.nameHi else ad.graha.nameEn}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (ad.isCurrent) ShubhEmerald else IvoryCream,
                                        fontWeight = if (ad.isCurrent) FontWeight.Bold else FontWeight.Normal
                                    )
                                    Text(
                                        text = "${ad.startLabel} – ${ad.endLabel}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (ad.isCurrent) ShubhEmerald else MutedGoldText
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
