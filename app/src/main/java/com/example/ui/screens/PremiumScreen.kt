package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.AppLanguage
import com.example.model.BillingCycle
import com.example.model.SubscriptionStatus
import com.example.model.t
import com.example.ui.JyotishUiState
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun PremiumScreen(
    uiState: JyotishUiState,
    onSelectBillingCycle: (BillingCycle) -> Unit,
    onAttemptCheckout: () -> Unit,
    onSetSubscriptionStatus: (SubscriptionStatus) -> Unit,
    onOpenAdminSettings: () -> Unit,
    onOpenWhatsAppSupport: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)
    val lang = uiState.language
    val membership = uiState.membership
    val status = membership.status

    val (statusColor, statusBg) = when (status) {
        SubscriptionStatus.FREE -> GoldBright to ElevatedMaroon
        SubscriptionStatus.PREMIUM -> ShubhEmerald to ShubhEmeraldBg
        SubscriptionStatus.EXPIRED -> ManglikCoral to ManglikCoralBg
    }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("premium_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = lang.t("वापस जाएं", "Go Back"),
                    tint = GoldBright
                )
            }
            SectionHeader(
                title = lang.t("प्रीमियम सदस्यता एवं प्लान", "Premium Membership"),
                subtitle = lang.t("विस्तृत कुंडली रिपोर्ट, नवांश व दशा अनलॉक करें", "Unlock detailed Kundali reports, D-9 & Dasha insights"),
                icon = Icons.Filled.WorkspacePremium,
                modifier = Modifier.weight(1f)
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 92.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .testTag("premium_lazy_list")
        ) {

        // Current Subscription Status Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardMaroon),
                border = BorderStroke(1.2.dp, statusColor.copy(alpha = 0.7f)),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("premium_status_card")
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = lang.t("वर्तमान सदस्यता स्थिति", "Current Subscription Status"),
                                style = MaterialTheme.typography.labelMedium,
                                color = MutedGoldText
                            )
                            Text(
                                text = if (lang == AppLanguage.HI) status.hi else status.en,
                                style = MaterialTheme.typography.headlineSmall,
                                color = statusColor,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.testTag("premium_current_status_text")
                            )
                        }
                        Surface(
                            color = statusBg,
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, statusColor)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = when (status) {
                                        SubscriptionStatus.PREMIUM -> Icons.Outlined.Verified
                                        SubscriptionStatus.EXPIRED -> Icons.Filled.WarningAmber
                                        SubscriptionStatus.FREE -> Icons.Outlined.Lock
                                    },
                                    contentDescription = null,
                                    tint = statusColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = status.name,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = statusColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Text(
                        text = when (status) {
                            SubscriptionStatus.FREE -> lang.t(
                                "आप अभी मुफ़्त (Free) स्तर पर हैं। बेसिक राशिफल, पंचांग और लग्न चक्र खुले हैं; विस्तृत कुंडली रिपोर्ट, नवांश (D-9), अवकहड़ा और दशा विवरण प्रीमियम में लॉक हैं।",
                                "You are on the Free tier. Basic Rashifal, Panchang & D-1 Lagna are unlocked; detailed Kundali reports, D-9 Navamsha, Avakahada & Dasha details are locked."
                            )
                            SubscriptionStatus.PREMIUM -> lang.t(
                                "प्रीमियम सदस्यता सक्रिय है! सभी विस्तृत कुंडली रिपोर्ट, नवांश (D-9), अवकहड़ा, विंशोत्तरी दशा और ज्योतिषीय अंतर्दृष्टि अनलॉक हैं।",
                                "Premium is ACTIVE! Detailed Kundali reports, Navamsha (D-9), Avakahada, Vimshottari Dasha & expanded Vedic insights are unlocked."
                            )
                            SubscriptionStatus.EXPIRED -> lang.t(
                                "आपकी प्रीमियम सदस्यता समाप्त (Expired) हो चुकी है। प्रीमियम कुंडली रिपोर्ट और दशा विवरण पुनः अनलॉक करने के लिए नवीनीकरण करें।",
                                "Your Premium membership has EXPIRED. Renew your plan to regain access to detailed Kundali reports, D-9 and Dasha details."
                            )
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = WarmParchment
                    )
                }
            }
        }

        // Free vs Premium Comparison
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                TierComparisonCard(
                    title = lang.t("मुफ़्त (Free)", "Free Tier"),
                    badge = lang.t("बेसिक", "Basic"),
                    features = listOf(
                        lang.t("12 राशियों का बेसिक दैनिक राशिफल", "Basic 12-sign Daily Rashifal"),
                        lang.t("बेसिक दैनिक पंचांग (तिथि, नक्षत्र)", "Basic Panchang (Tithi, Nakshatra)"),
                        lang.t("बेसिक लग्न (D-1) जन्म कुंडली", "Basic D-1 Lagna Chart")
                    ),
                    isHighlighted = false,
                    modifier = Modifier.weight(1f)
                )
                TierComparisonCard(
                    title = lang.t("प्रीमियम (Premium)", "Premium Tier"),
                    badge = lang.t("संपूर्ण", "Full Access"),
                    features = listOf(
                        lang.t("विस्तृत कुंडली रिपोर्ट व विशेष अंतर्दृष्टि", "Detailed Kundali Reports & Insights"),
                        lang.t("विंशोत्तरी महादशा व अंतर्दशा विवरण", "Full Vimshottari Mahadasha & Antardasha"),
                        lang.t("नवांश (D-9) चक्र व अवकहड़ा चक्र", "Navamsha (D-9) & Avakahada Chakra")
                    ),
                    isHighlighted = true,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Admin-Editable Plan Pricing
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardMaroon),
                border = BorderStroke(1.dp, SubtleGoldBorder),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = lang.t("प्रीमियम प्लान चुनें", "Choose Your Upgrade Plan"),
                            style = MaterialTheme.typography.titleMedium,
                            color = GoldBright,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = onOpenAdminSettings) {
                            Text(lang.t("मूल्य संपादित करें", "Edit Prices"), color = GoldPrimary)
                        }
                    }

                    val plans = listOf(
                        Triple(BillingCycle.MONTHLY, membership.monthlyPriceInr, lang.t("प्रति माह", "Per Month")),
                        Triple(BillingCycle.YEARLY, membership.yearlyPriceInr, lang.t("प्रति वर्ष (लोकप्रिय)", "Per Year (Popular)")),
                        Triple(BillingCycle.LIFETIME, membership.lifetimePriceInr, lang.t("एक बार शुल्क", "One-Time"))
                    )

                    plans.forEach { (cycle, price, sub) ->
                        val selected = uiState.selectedBillingCycle == cycle
                        Surface(
                            onClick = { onSelectBillingCycle(cycle) },
                            shape = RoundedCornerShape(14.dp),
                            color = if (selected) RoyalCrimson.copy(alpha = 0.7f) else DeepMaroonSurface,
                            border = BorderStroke(1.2.dp, if (selected) GoldPrimary else SubtleGoldBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("plan_${cycle.name.lowercase()}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.padding(14.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = selected,
                                        onClick = { onSelectBillingCycle(cycle) },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = GoldBright,
                                            unselectedColor = MutedGoldText
                                        )
                                    )
                                    Column {
                                        Text(
                                            text = if (lang == AppLanguage.HI) cycle.hi else cycle.en,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = IvoryCream,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = sub,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MutedGoldText
                                        )
                                    }
                                }
                                Text(
                                    text = "₹$price",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = GoldBright,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.testTag("plan_price_${cycle.name.lowercase()}")
                                )
                            }
                        }
                    }

                    // Honest Payment Integration Status Banner
                    Surface(
                        color = CautionAmberBg,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, CautionAmber.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("payment_not_connected_banner")
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = lang.t(
                                    "भुगतान गेटवे स्थिति: एकीकरण संरचना तैयार (लाइव कनेक्ट नहीं)",
                                    "Payment Gateway Status: Structure Prepared (Not Connected Live)"
                                ),
                                style = MaterialTheme.typography.labelLarge,
                                color = CautionAmber,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = lang.t(
                                    "PaymentGatewayClient इंटरफ़ेस तैयार है, लेकिन कोई लाइव Razorpay / Google Play Billing कुंजी या मर्चेंट सर्वर कनेक्ट नहीं है।",
                                    "PaymentGatewayClient contract is prepared, but no live Razorpay / Google Play Billing key or merchant backend is connected yet."
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = WarmParchment
                            )
                        }
                    }

                    if (!uiState.paymentGatewayNotice.isNullOrBlank()) {
                        Surface(
                            color = ManglikCoralBg,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, ManglikCoral.copy(alpha = 0.65f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("payment_gateway_notice")
                        ) {
                            Text(
                                text = uiState.paymentGatewayNotice,
                                style = MaterialTheme.typography.bodySmall,
                                color = IvoryCream,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    Button(
                        onClick = onAttemptCheckout,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = ObsidianMaroon
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("premium_checkout_button")
                    ) {
                        Icon(Icons.Filled.CreditCard, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (status == SubscriptionStatus.EXPIRED) {
                                lang.t("सदस्यता नवीनीकरण (भुगतान परीक्षण)", "Renew Premium (Test Checkout)")
                            } else {
                                lang.t("प्रीमियम में अपग्रेड करें (चेकआउट)", "Upgrade to Premium (Checkout)")
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = onOpenWhatsAppSupport,
                        border = BorderStroke(1.dp, ShubhEmerald),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ShubhEmerald),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("premium_whatsapp_help_btn")
                    ) {
                        Icon(Icons.Filled.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(lang.t("प्रीमियम सहायता हेतु व्हाट्सएप करें", "Ask About Premium on WhatsApp"))
                    }
                }
            }
        }

        // Demo Subscription Status Preview Control
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DeepMaroonSurface),
                border = BorderStroke(1.dp, SubtleGoldBorder),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = lang.t(
                            "डेमो सदस्यता स्थिति नियंत्रण (लॉक/अनलॉक परीक्षण)",
                            "Demo Subscription Status Control (Preview Lock/Unlock)"
                        ),
                        style = MaterialTheme.typography.titleSmall,
                        color = GoldBright,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = lang.t(
                            "पेमेंट गेटवे कनेक्ट होने तक आप यहाँ से Free, Premium और Expired अवस्था का तुरंत पूर्वावलोकन कर सकते हैं।",
                            "Until live payment billing is connected, switch between Free, Premium, and Expired states here to verify feature locking across Kundali."
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedGoldText
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SubscriptionStatus.entries.forEach { st ->
                            val selected = status == st
                            FilterChip(
                                selected = selected,
                                onClick = { onSetSubscriptionStatus(st) },
                                label = {
                                    Text(
                                        text = when (st) {
                                            SubscriptionStatus.FREE -> "Free"
                                            SubscriptionStatus.PREMIUM -> "Premium"
                                            SubscriptionStatus.EXPIRED -> "Expired"
                                        },
                                        maxLines = 1
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = RoyalCrimson,
                                    selectedLabelColor = GoldBright,
                                    containerColor = CardMaroon,
                                    labelColor = MutedGoldText
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = selected,
                                    borderColor = SubtleGoldBorder,
                                    selectedBorderColor = GoldPrimary
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("status_toggle_${st.name.lowercase()}")
                            )
                        }
                    }
                }
            }
        }
    }
    }
}

@Composable
private fun TierComparisonCard(
    title: String,
    badge: String,
    features: List<String>,
    isHighlighted: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isHighlighted) RoyalCrimson.copy(alpha = 0.45f) else CardMaroon
        ),
        border = BorderStroke(1.dp, if (isHighlighted) GoldPrimary else SubtleGoldBorder),
        shape = MaterialTheme.shapes.large,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Surface(
                color = if (isHighlighted) GoldPrimary else DeepMaroonSurface,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isHighlighted) ObsidianMaroon else MutedGoldText,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = if (isHighlighted) GoldBright else IvoryCream,
                fontWeight = FontWeight.Bold
            )
            features.forEach { f ->
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = if (isHighlighted) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonChecked,
                        contentDescription = null,
                        tint = if (isHighlighted) ShubhEmerald else GoldPrimary,
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = f,
                        style = MaterialTheme.typography.bodySmall,
                        color = WarmParchment
                    )
                }
            }
        }
    }
}
