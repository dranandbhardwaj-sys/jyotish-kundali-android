package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.*
import com.example.ui.JyotishUiState
import com.example.ui.components.CosmicMandalaBackground
import com.example.ui.components.SectionHeader
import com.example.ui.components.VedicGoldDivider
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    uiState: JyotishUiState,
    onNavigateToKundali: () -> Unit,
    onNavigateToRashifal: (Rashi) -> Unit,
    onNavigateToPanchang: () -> Unit,
    onNavigateToMatching: () -> Unit,
    onNavigateToOwnerProfile: () -> Unit,
    onNavigateToPremium: () -> Unit,
    onNavigateToWhatsAppSupport: () -> Unit,
    onNavigateToChatSupport: () -> Unit,
    onNavigateToAdminSettings: () -> Unit,
    onInspectGraha: (Graha) -> Unit,
    modifier: Modifier = Modifier
) {
    val lang = uiState.language
    val panchang = uiState.panchangResult
    val owner = uiState.ownerProfile
    val membership = uiState.membership

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_lazy_list"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 92.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 1. Royal Hero Celestial Banner
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.2.dp, ChartLineGold.copy(alpha = 0.55f)),
                colors = CardDefaults.cardColors(containerColor = DeepMaroonSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 215.dp)
                ) {
                    CosmicMandalaBackground(
                        modifier = Modifier.matchParentSize()
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Surface(
                            color = RoyalCrimson.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = lang.t("॥ शुभम् भवतु • वैदिक ज्योतिष ॥", "|| SHUBHAM BHAVATU • VEDIC ASTROLOGY ||"),
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldBright,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = lang.t(
                                "अपनी सटीक जन्म कुंडली और दैनिक पंचांग जानें",
                                "Discover Your Sacred Vedic Kundali & Destiny"
                            ),
                            style = MaterialTheme.typography.headlineMedium,
                            color = IvoryCream
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = lang.t(
                                "लाहिड़ी अयनांश पर आधारित लग्न, नवांश, नवग्रह स्थिति, 12 राशिफल और 36 गुण मिलान।",
                                "Lahiri Ayanamsha Lagna & Navamsha charts, Navagraha telemetry, 12 Rashifal & 36-Guna Matching."
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = WarmParchment.copy(alpha = 0.9f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = onNavigateToKundali,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GoldPrimary,
                                    contentColor = ObsidianMaroon
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("home_create_kundali_btn")
                            ) {
                                Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = lang.t("कुंडली बनाएं", "Create Kundali"),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                            OutlinedButton(
                                onClick = onNavigateToMatching,
                                border = BorderStroke(1.dp, GoldPrimary),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldBright),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("home_match_kundali_btn")
                            ) {
                                Icon(Icons.Filled.Favorite, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = lang.t("कुंडली मिलान", "Match Kundali"),
                                    style = MaterialTheme.typography.labelLarge,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Today's Live Panchang Strip
        if (panchang != null) {
            item {
                Card(
                    onClick = onNavigateToPanchang,
                    colors = CardDefaults.cardColors(containerColor = CardMaroon),
                    border = BorderStroke(1.dp, SubtleGoldBorder),
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_panchang_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.WbSunny, contentDescription = null, tint = GoldPrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = lang.t("आज का पंचांग", "Today's Panchang"),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = GoldBright,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${panchang.day}/${panchang.month}/${panchang.year} • ${if (lang == AppLanguage.HI) panchang.varaHi else panchang.varaEn} • ${panchang.place.displayName(lang)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MutedGoldText
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = lang.t("पूरा पंचांग देखें", "View full Panchang"),
                                tint = GoldPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            PanchangMiniTile(
                                label = lang.t("तिथि", "Tithi"),
                                value = if (lang == AppLanguage.HI) panchang.tithiHi else panchang.tithiEn,
                                modifier = Modifier.weight(1f)
                            )
                            PanchangMiniTile(
                                label = lang.t("नक्षत्र", "Nakshatra"),
                                value = if (lang == AppLanguage.HI) panchang.nakshatraHi else panchang.nakshatraEn,
                                modifier = Modifier.weight(1f)
                            )
                            PanchangMiniTile(
                                label = lang.t("योग", "Yoga"),
                                value = if (lang == AppLanguage.HI) panchang.yogaHi else panchang.yogaEn,
                                modifier = Modifier.weight(1f)
                            )
                            PanchangMiniTile(
                                label = lang.t("करण", "Karana"),
                                value = if (lang == AppLanguage.HI) panchang.karanaHi else panchang.karanaEn,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "${lang.t("सूर्योदय", "Sunrise")}: ${panchang.sunrise}",
                                style = MaterialTheme.typography.labelMedium,
                                color = WarmParchment
                            )
                            Text(
                                text = "${lang.t("राहु काल", "Rahu Kaal")}: ${panchang.rahuKaal}",
                                style = MaterialTheme.typography.labelMedium,
                                color = CautionAmber
                            )
                        }
                    }
                }
            }
        }

        // 3. Quick Services Grid
        item {
            SectionHeader(
                title = lang.t("प्रमुख ज्योतिष सेवाएं", "Sacred Vedic Services"),
                subtitle = lang.t("संपूर्ण वैदिक गणना एक ही स्थान पर", "Complete Jyotish tools in one sanctuary"),
                icon = Icons.Filled.GridView
            )
            Spacer(modifier = Modifier.height(10.dp))
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val cols = if (maxWidth >= 680.dp) 4 else 2
                val items = listOf(
                    Triple(lang.t("जन्म कुंडली", "Birth Kundali"), lang.t("लग्न व नवांश चक्र", "D-1 & D-9 Charts"), Icons.Filled.Hexagon) to onNavigateToKundali,
                    Triple(lang.t("दैनिक राशिफल", "Daily Rashifal"), lang.t("12 राशियों का फल", "12 Zodiac Insights"), Icons.Filled.Stars) to { onNavigateToRashifal(uiState.selectedRashi) },
                    Triple(lang.t("दैनिक पंचांग", "Panchang"), lang.t("तिथि, नक्षत्र, मुहूर्त", "Tithi & Muhurat"), Icons.Filled.CalendarMonth) to onNavigateToPanchang,
                    Triple(lang.t("कुंडली मिलान", "Kundali Match"), lang.t("36 गुण व मांगलिक", "Ashtakoota 36 Guna"), Icons.Filled.Favorite) to onNavigateToMatching
                )
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items.chunked(cols).forEach { rowItems ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            rowItems.forEach { (meta, action) ->
                                QuickServiceCard(
                                    title = meta.first,
                                    subtitle = meta.second,
                                    icon = meta.third,
                                    onClick = action,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }

        item { VedicGoldDivider() }

        // 4. 12 Zodiac Signs Strip
        item {
            SectionHeader(
                title = lang.t("12 राशियाँ एवं आज का राशिफल", "12 Zodiac Signs & Horoscope"),
                subtitle = lang.t("अपनी राशि चुनकर विस्तृत भविष्यफल पढ़ें", "Tap any Rashi for today's prediction"),
                icon = Icons.Filled.NightlightRound
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(Rashi.entries, key = { it.number }) { rashi ->
                    val isSelected = rashi == uiState.selectedRashi
                    Surface(
                        onClick = { onNavigateToRashifal(rashi) },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) RoyalCrimson else CardMaroon,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) GoldPrimary else SubtleGoldBorder
                        ),
                        modifier = Modifier
                            .width(104.dp)
                            .testTag("home_rashi_chip_${rashi.name.lowercase()}")
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp)
                        ) {
                            Text(
                                text = rashi.symbol,
                                fontSize = 26.sp,
                                color = GoldBright
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (lang == AppLanguage.HI) rashi.nameHi else rashi.nameEn,
                                style = MaterialTheme.typography.titleSmall,
                                color = IvoryCream,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (lang == AppLanguage.HI) rashi.lord.nameHi else rashi.lord.nameEn.substringBefore(" "),
                                style = MaterialTheme.typography.labelSmall,
                                color = MutedGoldText,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // 5. Navagraha Explorer
        item {
            SectionHeader(
                title = lang.t("नवग्रह दर्शन एवं मंत्र", "Navagraha Explorer"),
                subtitle = lang.t("ग्रह के रत्न, मंत्र और प्रभाव जानने के लिए स्पर्श करें", "Tap any Graha for Vedic mantra, gemstone & traits"),
                icon = Icons.Filled.Public
            )
            Spacer(modifier = Modifier.height(10.dp))
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val cols = if (maxWidth >= 680.dp) 5 else 3
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Graha.entries.chunked(cols).forEach { row ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            row.forEach { graha ->
                                Surface(
                                    onClick = { onInspectGraha(graha) },
                                    shape = RoundedCornerShape(14.dp),
                                    color = CardMaroon,
                                    border = BorderStroke(1.dp, graha.accentColor.copy(alpha = 0.4f)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("home_graha_${graha.name.lowercase()}")
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp)
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(graha.accentColor.copy(alpha = 0.16f))
                                                .border(1.dp, graha.accentColor.copy(alpha = 0.7f), CircleShape)
                                        ) {
                                            Text(
                                                text = if (lang == AppLanguage.HI) graha.abbrHi else graha.abbrEn,
                                                color = graha.accentColor,
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.titleSmall
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = if (lang == AppLanguage.HI) graha.nameHi else graha.nameEn.substringBefore(" "),
                                            style = MaterialTheme.typography.labelLarge,
                                            color = IvoryCream,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = if (lang == AppLanguage.HI) graha.dayHi else graha.dayEn,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MutedGoldText,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                            repeat(cols - row.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        item { VedicGoldDivider() }

        // 6. Acharya Profile, Premium Membership, Support & Admin Hub
        item {
            SectionHeader(
                title = lang.t("आचार्य परामर्श, प्रीमियम एवं सहायता", "Acharya, Premium & Support"),
                subtitle = lang.t("डॉ. आनंद भारद्वाज, सदस्यता व प्रत्यक्ष सहायता", "Dr. Anand Bhardwaj, plans, support & settings"),
                icon = Icons.Filled.WorkspacePremium
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Featured Acharya Profile Card
            Card(
                onClick = onNavigateToOwnerProfile,
                colors = CardDefaults.cardColors(containerColor = CardMaroon),
                border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_owner_profile_card")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(14.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(RoyalCrimson, Color(0xFF3E0817))))
                            .border(1.5.dp, GoldPrimary, CircleShape)
                    ) {
                        if (owner.photoUri.isNotBlank()) {
                            AsyncImage(
                                model = owner.photoUri,
                                contentDescription = lang.t("आचार्य फ़ोटो", "Owner photo"),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text(
                                text = "ॐ",
                                color = GoldBright,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (lang == AppLanguage.HI) owner.nameHi else owner.nameEn,
                            style = MaterialTheme.typography.titleMedium,
                            color = GoldBright,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (lang == AppLanguage.HI) owner.titleHi else owner.titleEn,
                            style = MaterialTheme.typography.bodySmall,
                            color = IvoryCream,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (lang == AppLanguage.HI) owner.experienceHi else owner.experienceEn,
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedGoldText
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = lang.t("प्रोफ़ाइल देखें", "View Profile"),
                        tint = GoldPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val cols = if (maxWidth >= 680.dp) 4 else 2
                val hubItems = listOf(
                    Triple(
                        lang.t("प्रीमियम सदस्यता", "Premium Plan"),
                        "${if (lang == AppLanguage.HI) membership.status.hi else membership.status.en} • ₹${membership.yearlyPriceInr}",
                        Icons.Filled.WorkspacePremium
                    ) to ("home_premium_card" to onNavigateToPremium),
                    Triple(
                        lang.t("व्हाट्सएप सहायता", "WhatsApp Support"),
                        lang.t("त्वरित ज्योतिष परामर्श", "Instant Pre-filled Help"),
                        Icons.Filled.Chat
                    ) to ("home_whatsapp_card" to onNavigateToWhatsAppSupport),
                    Triple(
                        lang.t("चैट सहायता (डेमो)", "Chat Support (Demo)"),
                        lang.t("प्रश्न लिखें / डेमो चैट", "Ask & Handoff to WA"),
                        Icons.Outlined.Forum
                    ) to ("home_chat_card" to onNavigateToChatSupport),
                    Triple(
                        lang.t("एडमिन सेटिंग्स", "Admin Settings"),
                        lang.t("प्रोफ़ाइल, मूल्य व नंबर", "Edit Profile & Prices"),
                        Icons.Filled.Settings
                    ) to ("home_admin_card" to onNavigateToAdminSettings)
                )
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    hubItems.chunked(cols).forEach { rowItems ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            rowItems.forEach { (meta, actionMeta) ->
                                QuickServiceCard(
                                    title = meta.first,
                                    subtitle = meta.second,
                                    icon = meta.third,
                                    onClick = actionMeta.second,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag(actionMeta.first)
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun PanchangMiniTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(DeepMaroonSurface)
            .border(1.dp, SubtleGoldBorder.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = GoldPrimary,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = IvoryCream,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun QuickServiceCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = CardMaroon),
        border = BorderStroke(1.dp, SubtleGoldBorder),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(14.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(listOf(RoyalCrimson, Color(0xFF3E0817)))
                    )
                    .border(1.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            ) {
                Icon(icon, contentDescription = null, tint = GoldBright, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = IvoryCream,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedGoldText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
