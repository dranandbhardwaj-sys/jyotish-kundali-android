package com.example.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.remote.OnDeviceEphemerisBackend
import com.example.model.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JyotishTopBar(
    language: AppLanguage,
    subscriptionStatus: SubscriptionStatus,
    onToggleLanguage: () -> Unit,
    onOpenPrecisionInfo: () -> Unit,
    onOpenOwnerProfile: () -> Unit,
    onOpenPremium: () -> Unit,
    onOpenWhatsAppSupport: () -> Unit,
    onOpenChatSupport: () -> Unit,
    onOpenAdminSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = ObsidianMaroon.copy(alpha = 0.96f),
            titleContentColor = IvoryCream
        ),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(RoyalCrimson, Color(0xFF3B0714))
                            )
                        )
                        .border(1.dp, GoldPrimary.copy(alpha = 0.7f), CircleShape)
                ) {
                    Text(
                        text = "ॐ",
                        color = GoldBright,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = language.t("ज्योतिष कुंडली", "Jyotish Kundali"),
                        style = MaterialTheme.typography.titleMedium,
                        color = GoldBright,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = language.t("वैदिक पंचांग एवं जन्मपत्रिका", "Vedic Astrology & Almanac"),
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedGoldText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        actions = {
            val badgeTint = when (subscriptionStatus) {
                SubscriptionStatus.PREMIUM -> ShubhEmerald
                SubscriptionStatus.EXPIRED -> ManglikCoral
                SubscriptionStatus.FREE -> GoldPrimary
            }
            IconButton(
                onClick = onOpenPremium,
                modifier = Modifier.testTag("topbar_premium_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.WorkspacePremium,
                    contentDescription = language.t("प्रीमियम सदस्यता", "Premium Membership"),
                    tint = badgeTint
                )
            }
            IconButton(
                onClick = onOpenPrecisionInfo,
                modifier = Modifier.testTag("precision_info_button")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = language.t("गणना पद्धति एवं सीमाएं", "Calculation Methodology & Limits"),
                    tint = GoldPrimary
                )
            }
            Surface(
                onClick = onToggleLanguage,
                shape = RoundedCornerShape(24.dp),
                color = CardMaroon,
                border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.6f)),
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("language_toggle_button")
                    .semantics {
                        contentDescription = language.t("भाषा बदलें", "Switch language")
                    }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Translate,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (language == AppLanguage.HI) "हिन्दी | EN" else "EN | हिन्दी",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldBright,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Box {
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier.testTag("topbar_more_menu_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = language.t("अधिक विकल्प", "More options"),
                        tint = GoldBright
                    )
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    containerColor = CardMaroon
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = language.t("आचार्य परिचय (डॉ. आनंद भारद्वाज)", "Owner Profile (Dr. Anand Bhardwaj)"),
                                color = IvoryCream
                            )
                        },
                        leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null, tint = GoldPrimary) },
                        onClick = {
                            menuExpanded = false
                            onOpenOwnerProfile()
                        },
                        modifier = Modifier.testTag("menu_owner_profile")
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = language.t("प्रीमियम सदस्यता", "Premium Membership"),
                                color = IvoryCream
                            )
                        },
                        leadingIcon = { Icon(Icons.Filled.WorkspacePremium, contentDescription = null, tint = GoldBright) },
                        onClick = {
                            menuExpanded = false
                            onOpenPremium()
                        },
                        modifier = Modifier.testTag("menu_premium")
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = language.t("व्हाट्सएप सहायता", "WhatsApp Support"),
                                color = IvoryCream
                            )
                        },
                        leadingIcon = { Icon(Icons.Filled.Chat, contentDescription = null, tint = ShubhEmerald) },
                        onClick = {
                            menuExpanded = false
                            onOpenWhatsAppSupport()
                        },
                        modifier = Modifier.testTag("menu_whatsapp_support")
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = language.t("चैट सहायता (डेमो)", "Chat Support (Demo)"),
                                color = IvoryCream
                            )
                        },
                        leadingIcon = { Icon(Icons.Outlined.Forum, contentDescription = null, tint = GoldPrimary) },
                        onClick = {
                            menuExpanded = false
                            onOpenChatSupport()
                        },
                        modifier = Modifier.testTag("menu_chat_support")
                    )
                    HorizontalDivider(color = SubtleGoldBorder)
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = language.t("एडमिन सेटिंग्स", "Admin Settings"),
                                color = GoldBright,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        leadingIcon = { Icon(Icons.Filled.Settings, contentDescription = null, tint = GoldBright) },
                        onClick = {
                            menuExpanded = false
                            onOpenAdminSettings()
                        },
                        modifier = Modifier.testTag("menu_admin_settings")
                    )
                }
            }
        }
    )
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        if (icon != null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(RoyalCrimson.copy(alpha = 0.65f))
                    .border(1.dp, GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = GoldBright,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
        }
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = GoldBright
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedGoldText
                )
            }
        }
    }
}

@Composable
fun BirthDetailsFormCard(
    title: String,
    details: BirthDetails,
    language: AppLanguage,
    offlinePlaces: List<BirthPlace>,
    onlinePlaces: List<BirthPlace>,
    isSearchingPlaces: Boolean,
    placeSearchError: String?,
    onSearchPlaces: (String) -> Unit,
    onDetailsChange: (BirthDetails) -> Unit,
    onSubmit: (() -> Unit)? = null,
    submitLabel: String? = null,
    submitTestTag: String = "generate_kundali_button",
    validationError: String? = null,
    showGender: Boolean = true,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showCityPicker by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardMaroon),
        border = BorderStroke(1.dp, SubtleGoldBorder),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = GoldBright,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = details.name,
                onValueChange = { onDetailsChange(details.copy(name = it)) },
                label = { Text(language.t("पूरा नाम", "Full Name")) },
                placeholder = { Text(language.t("नाम दर्ज करें...", "Enter full name...")) },
                leadingIcon = {
                    Icon(Icons.Filled.Person, contentDescription = null, tint = GoldPrimary)
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = SubtleGoldBorder,
                    focusedLabelColor = GoldBright,
                    unfocusedLabelColor = MutedGoldText,
                    focusedTextColor = IvoryCream,
                    unfocusedTextColor = IvoryCream,
                    cursorColor = GoldBright
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("${submitTestTag}_name_input")
            )

            if (showGender) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Gender.entries.forEach { g ->
                        val selected = details.gender == g
                        FilterChip(
                            selected = selected,
                            onClick = { onDetailsChange(details.copy(gender = g)) },
                            label = {
                                Text(
                                    text = if (language == AppLanguage.HI) g.hi else g.en,
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
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val dateText = String.format("%02d/%02d/%04d", details.day, details.month, details.year)
                PickerButtonBox(
                    label = language.t("जन्म तिथि", "Date of Birth"),
                    value = dateText,
                    icon = Icons.Outlined.CalendarMonth,
                    onClick = {
                        DatePickerDialog(
                            context,
                            { _, y, m, d ->
                                onDetailsChange(details.copy(year = y, month = m + 1, day = d))
                            },
                            details.year,
                            details.month - 1,
                            details.day
                        ).show()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("${submitTestTag}_date_picker")
                )

                val ampm = if (details.hour24 < 12) "AM" else "PM"
                val h12 = if (details.hour24 % 12 == 0) 12 else details.hour24 % 12
                val timeText = String.format("%02d:%02d %s", h12, details.minute, ampm)
                PickerButtonBox(
                    label = language.t("जन्म समय", "Birth Time"),
                    value = timeText,
                    icon = Icons.Outlined.AccessTime,
                    onClick = {
                        TimePickerDialog(
                            context,
                            { _, h, min ->
                                onDetailsChange(details.copy(hour24 = h, minute = min))
                            },
                            details.hour24,
                            details.minute,
                            false
                        ).show()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("${submitTestTag}_time_picker")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            PickerButtonBox(
                label = language.t("जन्म स्थान (शहर खोजें या कस्टम अक्षांश/देशांतर)", "Birth Place (Search City or Custom Lat/Lon)"),
                value = "${details.place.displayName(language)} (${details.place.coordinatesLabel()})",
                icon = Icons.Outlined.LocationOn,
                onClick = {
                    onSearchPlaces("")
                    showCityPicker = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("${submitTestTag}_place_picker")
            )

            if (!validationError.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = validationError,
                    style = MaterialTheme.typography.bodySmall,
                    color = ManglikCoral,
                    modifier = Modifier.testTag("${submitTestTag}_error")
                )
            }

            if (onSubmit != null && submitLabel != null) {
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = onSubmit,
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = ObsidianMaroon
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag(submitTestTag)
                ) {
                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = submitLabel,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    if (showCityPicker) {
        CitySelectorDialog(
            language = language,
            selectedCity = details.place,
            offlinePlaces = offlinePlaces,
            onlinePlaces = onlinePlaces,
            isSearching = isSearchingPlaces,
            searchError = placeSearchError,
            onSearchQueryChange = onSearchPlaces,
            onSelect = { city ->
                onDetailsChange(details.copy(place = city))
                showCityPicker = false
            },
            onDismiss = { showCityPicker = false }
        )
    }
}

@Composable
fun PickerButtonBox(
    label: String,
    value: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = DeepMaroonSurface,
        border = BorderStroke(1.dp, SubtleGoldBorder),
        modifier = modifier.minimumInteractiveComponentSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GoldPrimary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedGoldText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = IvoryCream,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun CitySelectorDialog(
    language: AppLanguage,
    selectedCity: BirthPlace,
    offlinePlaces: List<BirthPlace>,
    onlinePlaces: List<BirthPlace>,
    isSearching: Boolean,
    searchError: String?,
    onSearchQueryChange: (String) -> Unit,
    onSelect: (BirthPlace) -> Unit,
    onDismiss: () -> Unit
) {
    var modeTab by remember { mutableIntStateOf(0) } // 0 = Search City, 1 = Custom Lat/Lon
    var query by remember { mutableStateOf("") }

    var customName by remember { mutableStateOf(if (selectedCity.isCustom) selectedCity.nameEn else "") }
    var customLat by remember { mutableStateOf(selectedCity.latitude.toString()) }
    var customLon by remember { mutableStateOf(selectedCity.longitude.toString()) }
    var customTz by remember { mutableStateOf(selectedCity.timezoneOffsetHours.toString()) }
    var customError by remember { mutableStateOf<String?>(null) }

    val mergedPlaces = remember(offlinePlaces, onlinePlaces) {
        val seenNames = offlinePlaces.map { "${it.nameEn.lowercase()}_${it.latitude.toInt()}" }.toSet()
        offlinePlaces + onlinePlaces.filter { "${it.nameEn.lowercase()}_${it.latitude.toInt()}" !in seenNames }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardMaroon,
        titleContentColor = GoldBright,
        title = {
            Text(
                text = language.t("जन्म स्थान एवं निर्देशांक", "Birth Place & Coordinates"),
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(modifier = Modifier.heightIn(max = 430.dp)) {
                TabRow(
                    selectedTabIndex = modeTab,
                    containerColor = DeepMaroonSurface,
                    contentColor = GoldBright,
                    modifier = Modifier.clip(RoundedCornerShape(10.dp))
                ) {
                    Tab(
                        selected = modeTab == 0,
                        onClick = { modeTab = 0 },
                        text = { Text(language.t("शहर खोजें", "Search City"), maxLines = 1) }
                    )
                    Tab(
                        selected = modeTab == 1,
                        onClick = { modeTab = 1 },
                        text = { Text(language.t("अक्षांश/देशांतर", "Custom Lat/Lon"), maxLines = 1) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (modeTab == 0) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = {
                            query = it
                            onSearchQueryChange(it)
                        },
                        placeholder = {
                            Text(language.t("कोई भी शहर/कस्बा खोजें...", "Search any city worldwide..."))
                        },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = GoldPrimary) },
                        trailingIcon = {
                            if (isSearching) {
                                CircularProgressIndicator(
                                    color = GoldPrimary,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = SubtleGoldBorder,
                            focusedTextColor = IvoryCream,
                            unfocusedTextColor = IvoryCream
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("city_search_input")
                    )

                    if (!searchError.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = language.t(
                                "ऑनलाइन खोज अनुपलब्ध — ऑफ़लाइन सूची दिखाई जा रही है।",
                                "Online geocoding unavailable — showing offline cities."
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = CautionAmber
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(mergedPlaces, key = { it.id }) { city ->
                            val isSelected = city.id == selectedCity.id
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) RoyalCrimson else DeepMaroonSurface)
                                    .clickable { onSelect(city) }
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.LocationOn,
                                    contentDescription = null,
                                    tint = if (isSelected) GoldBright else MutedGoldText,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = city.displayName(language),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = IvoryCream,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = city.coordinatesLabel(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MutedGoldText
                                    )
                                }
                                if (isSelected) {
                                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = GoldBright)
                                }
                            }
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = customName,
                            onValueChange = { customName = it; customError = null },
                            label = { Text(language.t("स्थान का नाम", "Place Name")) },
                            placeholder = { Text(language.t("जैसे: ऋषिकेश", "e.g. Rishikesh")) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = SubtleGoldBorder,
                                focusedTextColor = IvoryCream,
                                unfocusedTextColor = IvoryCream
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("custom_place_name_input")
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = customLat,
                                onValueChange = { customLat = it; customError = null },
                                label = { Text(language.t("अक्षांश (Lat)", "Latitude")) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = SubtleGoldBorder,
                                    focusedTextColor = IvoryCream,
                                    unfocusedTextColor = IvoryCream
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("custom_lat_input")
                            )
                            OutlinedTextField(
                                value = customLon,
                                onValueChange = { customLon = it; customError = null },
                                label = { Text(language.t("देशांतर (Lon)", "Longitude")) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = SubtleGoldBorder,
                                    focusedTextColor = IvoryCream,
                                    unfocusedTextColor = IvoryCream
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("custom_lon_input")
                            )
                        }
                        OutlinedTextField(
                            value = customTz,
                            onValueChange = { customTz = it; customError = null },
                            label = { Text(language.t("UTC समय अंतर (घंटे, जैसे 5.5)", "UTC Offset (hours, e.g. 5.5)")) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = SubtleGoldBorder,
                                focusedTextColor = IvoryCream,
                                unfocusedTextColor = IvoryCream
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("custom_tz_input")
                        )

                        if (!customError.isNullOrBlank()) {
                            Text(
                                text = customError!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = ManglikCoral
                            )
                        }

                        Button(
                            onClick = {
                                val lat = customLat.toDoubleOrNull()
                                val lon = customLon.toDoubleOrNull()
                                val tz = customTz.toDoubleOrNull()
                                when {
                                    lat == null || lat !in -90.0..90.0 -> {
                                        customError = language.t("अक्षांश -90 से 90 के बीच होना चाहिए।", "Latitude must be between -90 and 90.")
                                    }
                                    lon == null || lon !in -180.0..180.0 -> {
                                        customError = language.t("देशांतर -180 से 180 के बीच होना चाहिए।", "Longitude must be between -180 and 180.")
                                    }
                                    tz == null || tz !in -12.0..14.0 -> {
                                        customError = language.t("UTC अंतर -12 से +14 के बीच होना चाहिए।", "UTC offset must be between -12 and +14.")
                                    }
                                    else -> {
                                        val label = customName.trim().ifEmpty { language.t("कस्टम स्थान", "Custom Location") }
                                        onSelect(
                                            BirthPlace(
                                                id = "custom_${lat}_${lon}",
                                                nameHi = label,
                                                nameEn = label,
                                                stateHi = "",
                                                stateEn = "",
                                                latitude = lat,
                                                longitude = lon,
                                                timezoneOffsetHours = tz,
                                                isCustom = true
                                            )
                                        )
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = ObsidianMaroon),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("apply_custom_coords_btn")
                        ) {
                            Text(language.t("निर्देशांक लागू करें", "Apply Coordinates"), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(language.t("बंद करें", "Close"), color = GoldPrimary)
            }
        }
    )
}

@Composable
fun PrecisionInfoDialog(
    language: AppLanguage,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardMaroon,
        titleContentColor = GoldBright,
        title = {
            Text(
                text = language.t("गणना पद्धति एवं सीमाएं", "Calculation Engine & Limitations"),
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = language.t(
                        "• खगोलीय आधार: यह ऐप ऑन-डिवाइस लाहिड़ी चित्रापक्ष अयनांश, जूलियन डे (UT), सूर्य-चंद्र एवं ग्रहों के कक्षीय समीकरणों, तथा स्थानीय अक्षांश/देशांतर से लग्न व नवांश (D-9) की गणना करता है।",
                        "• Ephemeris Engine: Computes Lahiri Chitrapaksha Ayanamsha, Julian Day (UT), orbital planetary longitudes, and Sidereal Lagna / Navamsha (D-9) directly from your exact birth date, time & coordinates."
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmParchment
                )
                Text(
                    text = language.t(
                        "• पंचांग एवं गोचर: तिथि, नक्षत्र, योग, करण स्थानीय सूर्योदय (उदय तिथि) पर आधारित हैं और दैनिक राशिफल चंद्र गोचर (Chandra Gochar) से निकलता है।",
                        "• Panchang & Gochar: Tithi, Nakshatra, Yoga, and Karana are evaluated at your chosen city's Sunrise, and Daily Rashifal derives from live Moon transit."
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmParchment
                )
                Text(
                    text = language.t(
                        "• स्थान खोज API: ऑनलाइन शहर खोज Open-Meteo Geocoding API से जुड़ती है तथा ऑफ़लाइन 60+ शहरों व कस्टम अक्षांश/देशांतर का समर्थन है।",
                        "• Geocoding API: Worldwide birthplace search uses Open-Meteo Geocoding API with 60+ offline cities and manual Lat/Lon/UTC entry."
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmParchment
                )
                Surface(
                    color = DeepMaroonSurface,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, SubtleGoldBorder)
                ) {
                    Text(
                        text = language.t(
                            "ज्ञात सीमाएं: (1) ग्रह अंश 1900–2100 के मध्य ~0.1°–0.5° की सटीकता रखते हैं (JPL DE431 / Swiss Ephemeris से सूक्ष्म अंतर संभव है)। (2) ऐतिहासिक डे-लाइट सेविंग (DST) स्वतः लागू नहीं होती—विदेशी जन्म के लिए कस्टम UTC अंतर समायोजित करें। (3) संधि लग्न या अति सूक्ष्म मुहूर्त के लिए वैकल्पिक सर्वर-साइड Ephemeris API (${OnDeviceEphemerisBackend.providerName}) जोड़ा जा सकता है।",
                            "Known Limitations: (1) Planetary longitudes use truncated analytical elements (~0.1°–0.5° accuracy across 1900–2100, vs arcsecond JPL DE431 / Swiss Ephemeris). (2) Historical DST rules are not auto-applied; adjust UTC offset in Custom Lat/Lon if needed. (3) For sub-arcsecond cusp precision, an optional external Swiss Ephemeris backend interface (EphemerisBackendClient) is ready to connect."
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = CautionAmber,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(language.t("समझ गया", "Got It"), color = GoldPrimary)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrahaDetailBottomSheet(
    graha: Graha,
    position: GrahaPosition?,
    language: AppLanguage,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DeepMaroonSurface,
        contentColor = IvoryCream,
        dragHandle = { BottomSheetDefaults.DragHandle(color = GoldPrimary.copy(alpha = 0.6f)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(graha.accentColor.copy(alpha = 0.18f))
                        .border(1.5.dp, graha.accentColor, CircleShape)
                ) {
                    Text(
                        text = if (language == AppLanguage.HI) graha.abbrHi else graha.abbrEn,
                        style = MaterialTheme.typography.titleLarge,
                        color = graha.accentColor,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = if (language == AppLanguage.HI) graha.nameHi else graha.nameEn,
                        style = MaterialTheme.typography.headlineSmall,
                        color = GoldBright
                    )
                    Text(
                        text = if (language == AppLanguage.HI) "${graha.natureHi} • ${graha.elementHi} तत्त्व" else "${graha.natureEn} • ${graha.elementEn}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedGoldText
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = if (language == AppLanguage.HI) graha.descHi else graha.descEn,
                style = MaterialTheme.typography.bodyLarge,
                color = WarmParchment
            )

            if (position != null) {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardMaroon),
                    border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.45f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = language.t("कुंडली में वर्तमान स्थिति", "Chart Placement"),
                            style = MaterialTheme.typography.labelLarge,
                            color = GoldBright
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        val rashiName = if (language == AppLanguage.HI) position.rashi.nameHi else position.rashi.nameEn
                        val nakName = if (language == AppLanguage.HI) position.nakshatraHi else position.nakshatraEn
                        val digName = if (language == AppLanguage.HI) position.dignity.hi else position.dignity.en
                        Text(
                            text = "${language.t("राशि", "Sign")}: $rashiName (${String.format("%.2f°", position.degreeInRashi)}) • ${language.t("भाव", "House")}: ${position.house}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = IvoryCream
                        )
                        Text(
                            text = "${language.t("नक्षत्र", "Nakshatra")}: $nakName (${language.t("पाद", "Pada")} ${position.pada}) • $digName",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedGoldText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                InfoBadgeBox(
                    label = language.t("शुभ दिन", "Weekday"),
                    value = if (language == AppLanguage.HI) graha.dayHi else graha.dayEn,
                    modifier = Modifier.weight(1f)
                )
                InfoBadgeBox(
                    label = language.t("रत्न", "Gemstone"),
                    value = if (language == AppLanguage.HI) graha.gemHi else graha.gemEn,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = RoyalCrimson.copy(alpha = 0.45f)),
                border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = language.t("वैदिक बीज मंत्र", "Vedic Beej Mantra"),
                        style = MaterialTheme.typography.labelMedium,
                        color = GoldPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = graha.mantra,
                        style = MaterialTheme.typography.titleMedium,
                        color = IvoryCream,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun InfoBadgeBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CardMaroon)
            .border(1.dp, SubtleGoldBorder, RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MutedGoldText)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = IvoryCream,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun PremiumLockCard(
    title: String,
    description: String,
    status: SubscriptionStatus,
    language: AppLanguage,
    onUpgradeClick: () -> Unit,
    modifier: Modifier = Modifier,
    upgradeButtonTag: String = "premium_lock_upgrade_btn"
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ElevatedMaroon),
        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.65f)),
        shape = MaterialTheme.shapes.large,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(RoyalCrimson)
                    .border(1.2.dp, GoldPrimary, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = language.t("प्रीमियम सुविधा लॉक है", "Premium feature locked"),
                    tint = GoldBright,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                color = if (status == SubscriptionStatus.EXPIRED) ManglikCoralBg else CautionAmberBg,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (status == SubscriptionStatus.EXPIRED) {
                        language.t("सदस्यता समाप्त (Expired)", "Membership Expired")
                    } else {
                        language.t("प्रीमियम सुविधा (Locked)", "Premium Feature Locked")
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = if (status == SubscriptionStatus.EXPIRED) ManglikCoral else CautionAmber,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = GoldBright,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = WarmParchment
            )
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onUpgradeClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldPrimary,
                    contentColor = ObsidianMaroon
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag(upgradeButtonTag)
            ) {
                Icon(Icons.Filled.WorkspacePremium, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (status == SubscriptionStatus.EXPIRED) {
                        language.t("प्रीमियम नवीनीकरण करें", "Renew Premium")
                    } else {
                        language.t("प्रीमियम अनलॉक करें", "Upgrade to Premium")
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
