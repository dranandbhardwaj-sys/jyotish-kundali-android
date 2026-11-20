package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.domain.WhatsAppHelper
import com.example.model.AppLanguage
import com.example.model.MembershipConfig
import com.example.model.OwnerProfileConfig
import com.example.model.SubscriptionStatus
import com.example.model.t
import com.example.ui.JyotishUiState
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun AdminSettingsScreen(
    uiState: JyotishUiState,
    onSaveSettings: (OwnerProfileConfig, MembershipConfig) -> Unit,
    onResetDefaults: () -> Unit,
    onClearNotice: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)
    val lang = uiState.language
    val context = LocalContext.current

    var nameHi by remember(uiState.ownerProfile) { mutableStateOf(uiState.ownerProfile.nameHi) }
    var nameEn by remember(uiState.ownerProfile) { mutableStateOf(uiState.ownerProfile.nameEn) }
    var titleHi by remember(uiState.ownerProfile) { mutableStateOf(uiState.ownerProfile.titleHi) }
    var titleEn by remember(uiState.ownerProfile) { mutableStateOf(uiState.ownerProfile.titleEn) }
    var experienceHi by remember(uiState.ownerProfile) { mutableStateOf(uiState.ownerProfile.experienceHi) }
    var experienceEn by remember(uiState.ownerProfile) { mutableStateOf(uiState.ownerProfile.experienceEn) }
    var photoUri by remember(uiState.ownerProfile) { mutableStateOf(uiState.ownerProfile.photoUri) }
    var introHi by remember(uiState.ownerProfile) { mutableStateOf(uiState.ownerProfile.introHi) }
    var introEn by remember(uiState.ownerProfile) { mutableStateOf(uiState.ownerProfile.introEn) }
    var aboutHi by remember(uiState.ownerProfile) { mutableStateOf(uiState.ownerProfile.aboutHi) }
    var aboutEn by remember(uiState.ownerProfile) { mutableStateOf(uiState.ownerProfile.aboutEn) }
    var phone by remember(uiState.ownerProfile) { mutableStateOf(uiState.ownerProfile.phone) }
    var email by remember(uiState.ownerProfile) { mutableStateOf(uiState.ownerProfile.email) }
    var whatsappNumber by remember(uiState.ownerProfile) { mutableStateOf(uiState.ownerProfile.whatsappNumber) }
    var addressHi by remember(uiState.ownerProfile) { mutableStateOf(uiState.ownerProfile.addressHi) }
    var addressEn by remember(uiState.ownerProfile) { mutableStateOf(uiState.ownerProfile.addressEn) }
    var supportHoursHi by remember(uiState.ownerProfile) { mutableStateOf(uiState.ownerProfile.supportHoursHi) }
    var supportHoursEn by remember(uiState.ownerProfile) { mutableStateOf(uiState.ownerProfile.supportHoursEn) }
    var websiteName by remember(uiState.ownerProfile) { mutableStateOf(uiState.ownerProfile.websiteName) }
    var websiteUrl by remember(uiState.ownerProfile) { mutableStateOf(uiState.ownerProfile.websiteUrl) }

    var monthlyStr by remember(uiState.membership) { mutableStateOf(uiState.membership.monthlyPriceInr.toString()) }
    var yearlyStr by remember(uiState.membership) { mutableStateOf(uiState.membership.yearlyPriceInr.toString()) }
    var lifetimeStr by remember(uiState.membership) { mutableStateOf(uiState.membership.lifetimePriceInr.toString()) }
    var status by remember(uiState.membership) { mutableStateOf(uiState.membership.status) }

    var validationError by remember { mutableStateOf<String?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {
            }
            photoUri = uri.toString()
        }
    }

    fun trySave() {
        val monthly = monthlyStr.toIntOrNull()
        val yearly = yearlyStr.toIntOrNull()
        val lifetime = lifetimeStr.toIntOrNull()
        val waDigits = WhatsAppHelper.sanitizePhoneNumber(whatsappNumber)

        when {
            nameEn.trim().isEmpty() || nameHi.trim().isEmpty() -> {
                validationError = lang.t("आचार्य का नाम खाली नहीं हो सकता।", "Owner name cannot be blank.")
            }
            waDigits.length < 8 -> {
                validationError = lang.t(
                    "कृपया कंट्री कोड सहित मान्य व्हाट्सएप नंबर दर्ज करें (जैसे +919876543210)।",
                    "Please enter a valid WhatsApp number with international country code (e.g. +919876543210)."
                )
            }
            monthly == null || monthly <= 0 || yearly == null || yearly <= 0 || lifetime == null || lifetime <= 0 -> {
                validationError = lang.t("कृपया मान्य प्रीमियम मूल्य (₹) दर्ज करें।", "Please enter valid positive INR prices.")
            }
            else -> {
                validationError = null
                val cleanWa = if (whatsappNumber.trim().startsWith("+")) {
                    "+$waDigits"
                } else {
                    "+$waDigits"
                }
                onSaveSettings(
                    OwnerProfileConfig(
                        nameHi = nameHi.trim(),
                        nameEn = nameEn.trim(),
                        titleHi = titleHi.trim(),
                        titleEn = titleEn.trim(),
                        experienceHi = experienceHi.trim(),
                        experienceEn = experienceEn.trim(),
                        photoUri = photoUri,
                        introHi = introHi.trim(),
                        introEn = introEn.trim(),
                        aboutHi = aboutHi.trim(),
                        aboutEn = aboutEn.trim(),
                        phone = phone.trim(),
                        email = email.trim(),
                        whatsappNumber = cleanWa,
                        addressHi = addressHi.trim(),
                        addressEn = addressEn.trim(),
                        supportHoursHi = supportHoursHi.trim(),
                        supportHoursEn = supportHoursEn.trim(),
                        websiteName = websiteName.trim(),
                        websiteUrl = websiteUrl.trim()
                    ),
                    MembershipConfig(
                        status = status,
                        monthlyPriceInr = monthly,
                        yearlyPriceInr = yearly,
                        lifetimePriceInr = lifetime
                    )
                )
            }
        }
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
                modifier = Modifier.testTag("admin_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = lang.t("वापस जाएं", "Go Back"),
                    tint = GoldBright
                )
            }
            SectionHeader(
                title = lang.t("एडमिन सेटिंग्स", "Admin Settings"),
                subtitle = lang.t("प्रोफ़ाइल, व्हाट्सएप, प्रीमियम मूल्य व वेबसाइट संपादन", "Edit Owner Profile, WhatsApp, Pricing & Support"),
                icon = Icons.Filled.Settings,
                modifier = Modifier.weight(1f)
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 92.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .testTag("admin_settings_lazy_list")
        ) {

        // Section 1: Owner Profile
        item {
            AdminSectionCard(title = lang.t("1. आचार्य / वेबसाइट स्वामी प्रोफ़ाइल", "1. Owner Profile")) {
                AdminTextField(
                    value = nameHi,
                    onValueChange = { nameHi = it; onClearNotice() },
                    label = lang.t("नाम (हिन्दी)", "Name (Hindi)"),
                    testTag = "admin_name_hi"
                )
                AdminTextField(
                    value = nameEn,
                    onValueChange = { nameEn = it; onClearNotice() },
                    label = lang.t("नाम (English)", "Name (English)"),
                    testTag = "admin_name_en"
                )
                AdminTextField(
                    value = titleHi,
                    onValueChange = { titleHi = it; onClearNotice() },
                    label = lang.t("उपाधि (हिन्दी)", "Title (Hindi)"),
                    testTag = "admin_title_hi"
                )
                AdminTextField(
                    value = titleEn,
                    onValueChange = { titleEn = it; onClearNotice() },
                    label = lang.t("उपाधि (English)", "Title (English)"),
                    testTag = "admin_title_en"
                )
                AdminTextField(
                    value = if (lang == AppLanguage.HI) experienceHi else experienceEn,
                    onValueChange = {
                        if (lang == AppLanguage.HI) experienceHi = it else experienceEn = it
                        onClearNotice()
                    },
                    label = lang.t("अनुभव", "Experience"),
                    testTag = "admin_experience"
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        border = BorderStroke(1.dp, GoldPrimary),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldBright)
                    ) {
                        Icon(Icons.Filled.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(lang.t("प्रोफ़ाइल फ़ोटो चुनें", "Pick Profile Photo"))
                    }
                    if (photoUri.isNotBlank()) {
                        TextButton(onClick = { photoUri = "" }) {
                            Text(lang.t("फ़ोटो हटाएं", "Clear"), color = ManglikCoral)
                        }
                    }
                }
                AdminTextField(
                    value = if (lang == AppLanguage.HI) introHi else introEn,
                    onValueChange = {
                        if (lang == AppLanguage.HI) introHi = it else introEn = it
                        onClearNotice()
                    },
                    label = lang.t("व्यावसायिक परिचय", "Professional Intro"),
                    minLines = 2,
                    testTag = "admin_intro"
                )
                AdminTextField(
                    value = if (lang == AppLanguage.HI) aboutHi else aboutEn,
                    onValueChange = {
                        if (lang == AppLanguage.HI) aboutHi = it else aboutEn = it
                        onClearNotice()
                    },
                    label = lang.t("संस्थान परिचय (About)", "About Section"),
                    minLines = 2,
                    testTag = "admin_about"
                )
            }
        }

        // Section 2: WhatsApp & Support Info
        item {
            AdminSectionCard(title = lang.t("2. व्हाट्सएप एवं सहायता विवरण", "2. WhatsApp & Support Info")) {
                AdminTextField(
                    value = whatsappNumber,
                    onValueChange = { whatsappNumber = it; onClearNotice() },
                    label = lang.t("व्हाट्सएप नंबर (कंट्री कोड सहित, जैसे +919876543210)", "WhatsApp Number (with country code, e.g. +919876543210)"),
                    keyboardType = KeyboardType.Phone,
                    testTag = "admin_whatsapp_input"
                )
                AdminTextField(
                    value = phone,
                    onValueChange = { phone = it; onClearNotice() },
                    label = lang.t("संपर्क फ़ोन नंबर", "Display Phone"),
                    keyboardType = KeyboardType.Phone,
                    testTag = "admin_phone_input"
                )
                AdminTextField(
                    value = email,
                    onValueChange = { email = it; onClearNotice() },
                    label = lang.t("सहायता ईमेल", "Support Email"),
                    keyboardType = KeyboardType.Email,
                    testTag = "admin_email_input"
                )
                AdminTextField(
                    value = if (lang == AppLanguage.HI) addressHi else addressEn,
                    onValueChange = {
                        if (lang == AppLanguage.HI) addressHi = it else addressEn = it
                        onClearNotice()
                    },
                    label = lang.t("पता / स्थान", "Address / City"),
                    testTag = "admin_address_input"
                )
                AdminTextField(
                    value = if (lang == AppLanguage.HI) supportHoursHi else supportHoursEn,
                    onValueChange = {
                        if (lang == AppLanguage.HI) supportHoursHi = it else supportHoursEn = it
                        onClearNotice()
                    },
                    label = lang.t("सहायता समय", "Support Hours"),
                    testTag = "admin_hours_input"
                )
            }
        }

        // Section 3: Website Details
        item {
            AdminSectionCard(title = lang.t("3. वेबसाइट विवरण", "3. Website Details")) {
                AdminTextField(
                    value = websiteName,
                    onValueChange = { websiteName = it; onClearNotice() },
                    label = lang.t("वेबसाइट नाम", "Website Name"),
                    testTag = "admin_website_name"
                )
                AdminTextField(
                    value = websiteUrl,
                    onValueChange = { websiteUrl = it; onClearNotice() },
                    label = lang.t("वेबसाइट URL", "Website URL"),
                    keyboardType = KeyboardType.Uri,
                    testTag = "admin_website_url"
                )
            }
        }

        // Section 4: Premium Pricing & Subscription Status
        item {
            AdminSectionCard(title = lang.t("4. प्रीमियम मूल्य एवं सदस्यता स्तर", "4. Premium Pricing & Status")) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AdminTextField(
                        value = monthlyStr,
                        onValueChange = { monthlyStr = it.filter(Char::isDigit); onClearNotice() },
                        label = lang.t("मासिक (₹)", "Monthly ₹"),
                        keyboardType = KeyboardType.Number,
                        testTag = "admin_price_monthly",
                        modifier = Modifier.weight(1f)
                    )
                    AdminTextField(
                        value = yearlyStr,
                        onValueChange = { yearlyStr = it.filter(Char::isDigit); onClearNotice() },
                        label = lang.t("वार्षिक (₹)", "Yearly ₹"),
                        keyboardType = KeyboardType.Number,
                        testTag = "admin_price_yearly",
                        modifier = Modifier.weight(1f)
                    )
                    AdminTextField(
                        value = lifetimeStr,
                        onValueChange = { lifetimeStr = it.filter(Char::isDigit); onClearNotice() },
                        label = lang.t("आजीवन (₹)", "Lifetime ₹"),
                        keyboardType = KeyboardType.Number,
                        testTag = "admin_price_lifetime",
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    text = lang.t("सदस्यता स्थिति चुनें", "Subscription Status"),
                    style = MaterialTheme.typography.labelMedium,
                    color = MutedGoldText
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    SubscriptionStatus.entries.forEach { st ->
                        val selected = status == st
                        FilterChip(
                            selected = selected,
                            onClick = { status = st; onClearNotice() },
                            label = { Text(st.name, maxLines = 1) },
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
                            modifier = Modifier
                                .weight(1f)
                                .testTag("admin_status_${st.name.lowercase()}")
                        )
                    }
                }
            }
        }

        // Validation / Save Notice & Buttons
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (!validationError.isNullOrBlank()) {
                    Text(
                        text = validationError!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = ManglikCoral,
                        modifier = Modifier.testTag("admin_validation_error")
                    )
                }

                if (!uiState.adminSaveNotice.isNullOrBlank()) {
                    Surface(
                        color = ShubhEmeraldBg,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, ShubhEmerald),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_save_notice")
                    ) {
                        Text(
                            text = uiState.adminSaveNotice,
                            style = MaterialTheme.typography.bodySmall,
                            color = ShubhEmerald,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Button(
                    onClick = { trySave() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = ObsidianMaroon
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("admin_save_button")
                ) {
                    Icon(Icons.Filled.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = lang.t("सभी सेटिंग्स सहेजें", "Save All Settings"),
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onResetDefaults,
                    border = BorderStroke(1.dp, SubtleGoldBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MutedGoldText),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("admin_reset_button")
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(lang.t("डिफ़ॉल्ट पर रीसेट करें", "Reset to Defaults"))
                }
            }
        }
    }
    }
}

@Composable
private fun AdminSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardMaroon),
        border = BorderStroke(1.dp, SubtleGoldBorder),
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = GoldBright,
                fontWeight = FontWeight.Bold
            )
            content()
        }
    }
}

@Composable
private fun AdminTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    testTag: String,
    modifier: Modifier = Modifier,
    minLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = minLines == 1,
        minLines = minLines,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GoldPrimary,
            unfocusedBorderColor = SubtleGoldBorder,
            focusedLabelColor = GoldBright,
            unfocusedLabelColor = MutedGoldText,
            focusedTextColor = IvoryCream,
            unfocusedTextColor = IvoryCream,
            cursorColor = GoldBright
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag)
    )
}
