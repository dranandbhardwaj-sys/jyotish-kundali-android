package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.model.AppLanguage
import com.example.model.t
import com.example.ui.JyotishUiState
import com.example.ui.components.CosmicMandalaBackground
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun OwnerProfileScreen(
    uiState: JyotishUiState,
    onUpdatePhotoUri: (String) -> Unit,
    onOpenAdminSettings: () -> Unit,
    onOpenWhatsAppSupport: () -> Unit,
    onOpenChatSupport: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)
    val lang = uiState.language
    val owner = uiState.ownerProfile
    val context = LocalContext.current
    var linkNotice by remember { mutableStateOf<String?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) {
                // Non-persistable URI fallback still renders for active session
            } catch (_: Exception) {
            }
            onUpdatePhotoUri(uri.toString())
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
                modifier = Modifier.testTag("owner_profile_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = lang.t("वापस जाएं", "Go Back"),
                    tint = GoldBright
                )
            }
            SectionHeader(
                title = lang.t("आचार्य एवं संस्थापक परिचय", "Acharya & Founder Profile"),
                subtitle = lang.t("वैदिक ज्योतिष विशेषज्ञ एवं मार्गदर्शन", "Vedic Astrology Scholar & Website Owner"),
                icon = Icons.Filled.Person,
                modifier = Modifier.weight(1f)
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 92.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .testTag("owner_profile_lazy_list")
        ) {

        // Hero Profile Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.2.dp, ChartLineGold.copy(alpha = 0.55f)),
                colors = CardDefaults.cardColors(containerColor = DeepMaroonSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("owner_profile_hero_card")
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    CosmicMandalaBackground(modifier = Modifier.matchParentSize())
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(RoyalCrimson, Color(0xFF3E0817)))
                                )
                                .border(2.dp, GoldPrimary, CircleShape)
                                .testTag("owner_profile_avatar")
                        ) {
                            if (owner.photoUri.isNotBlank()) {
                                AsyncImage(
                                    model = owner.photoUri,
                                    contentDescription = lang.t("आचार्य का प्रोफ़ाइल फ़ोटो", "Owner profile photo"),
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "ॐ",
                                        color = GoldBright,
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "AB",
                                        color = IvoryCream,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                border = BorderStroke(1.dp, GoldPrimary),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldBright),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.testTag("owner_change_photo_btn")
                            ) {
                                Icon(Icons.Filled.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = lang.t("फ़ोटो बदलें", "Edit Photo"),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                            if (owner.photoUri.isNotBlank()) {
                                OutlinedButton(
                                    onClick = { onUpdatePhotoUri("") },
                                    border = BorderStroke(1.dp, SubtleGoldBorder),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MutedGoldText),
                                    shape = RoundedCornerShape(20.dp),
                                    modifier = Modifier.testTag("owner_remove_photo_btn")
                                ) {
                                    Text(
                                        text = lang.t("हटाएं", "Reset"),
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (lang == AppLanguage.HI) owner.nameHi else owner.nameEn,
                            style = MaterialTheme.typography.headlineSmall,
                            color = GoldBright,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.testTag("owner_display_name")
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (lang == AppLanguage.HI) owner.titleHi else owner.titleEn,
                            style = MaterialTheme.typography.titleSmall,
                            color = IvoryCream,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = RoyalCrimson.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = if (lang == AppLanguage.HI) owner.experienceHi else owner.experienceEn,
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldBright,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
        }

        // Professional Introduction & About
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardMaroon),
                border = BorderStroke(1.dp, SubtleGoldBorder),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = lang.t("व्यावसायिक परिचय", "Professional Introduction"),
                        style = MaterialTheme.typography.titleMedium,
                        color = GoldBright,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (lang == AppLanguage.HI) owner.introHi else owner.introEn,
                        style = MaterialTheme.typography.bodyMedium,
                        color = IvoryCream
                    )
                    HorizontalDivider(color = SubtleGoldBorder)
                    Text(
                        text = lang.t("संस्थान एवं सेवा के बारे में", "About & Mission"),
                        style = MaterialTheme.typography.titleSmall,
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (lang == AppLanguage.HI) owner.aboutHi else owner.aboutEn,
                        style = MaterialTheme.typography.bodyMedium,
                        color = WarmParchment
                    )
                }
            }
        }

        // Contact Information & Website Details
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardMaroon),
                border = BorderStroke(1.dp, SubtleGoldBorder),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = lang.t("संपर्क एवं वेबसाइट विवरण", "Contact & Website Information"),
                        style = MaterialTheme.typography.titleMedium,
                        color = GoldBright,
                        fontWeight = FontWeight.Bold
                    )

                    ProfileInfoRow(
                        icon = Icons.Outlined.Email,
                        label = lang.t("ईमेल", "Email"),
                        value = owner.email
                    )
                    ProfileInfoRow(
                        icon = Icons.Outlined.Phone,
                        label = lang.t("फ़ोन नंबर", "Phone"),
                        value = owner.phone
                    )
                    ProfileInfoRow(
                        icon = Icons.Filled.Chat,
                        label = lang.t("व्हाट्सएप नंबर", "WhatsApp Number"),
                        value = if (owner.isWhatsappPlaceholder()) {
                            "${owner.whatsappNumber} (${lang.t("प्लेसहोल्डर", "Placeholder")})"
                        } else owner.whatsappNumber
                    )
                    ProfileInfoRow(
                        icon = Icons.Outlined.LocationOn,
                        label = lang.t("स्थान", "Location"),
                        value = if (lang == AppLanguage.HI) owner.addressHi else owner.addressEn
                    )
                    ProfileInfoRow(
                        icon = Icons.Outlined.Schedule,
                        label = lang.t("परामर्श समय", "Consultation Hours"),
                        value = if (lang == AppLanguage.HI) owner.supportHoursHi else owner.supportHoursEn
                    )
                    ProfileInfoRow(
                        icon = Icons.Outlined.Language,
                        label = lang.t("वेबसाइट", "Website"),
                        value = "${owner.websiteName} • ${owner.websiteUrl}"
                    )

                    if (!linkNotice.isNullOrBlank()) {
                        Text(
                            text = linkNotice!!,
                            style = MaterialTheme.typography.labelSmall,
                            color = CautionAmber
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(owner.websiteUrl)).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(intent)
                            } catch (_: Exception) {
                                linkNotice = lang.t(
                                    "वेबसाइट ब्राउज़र उपलब्ध नहीं: ${owner.websiteUrl}",
                                    "No browser available to open: ${owner.websiteUrl}"
                                )
                            }
                        },
                        border = BorderStroke(1.dp, SubtleGoldBorder),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldBright),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("owner_open_website_btn")
                    ) {
                        Icon(Icons.Outlined.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(lang.t("वेबसाइट खोलें", "Visit Website"))
                    }
                }
            }
        }

        // Action Cards: Admin Edit, WhatsApp, Chat Demo
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onOpenAdminSettings,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = ObsidianMaroon
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("owner_edit_in_admin_btn")
                ) {
                    Icon(Icons.Filled.Settings, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = lang.t("एडमिन सेटिंग्स में प्रोफ़ाइल संपादित करें", "Edit Profile in Admin Settings"),
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = onOpenWhatsAppSupport,
                        border = BorderStroke(1.dp, ShubhEmerald),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ShubhEmerald),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("owner_whatsapp_btn")
                    ) {
                        Icon(Icons.Filled.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(lang.t("व्हाट्सएप सहायता", "WhatsApp"), maxLines = 1)
                    }
                    OutlinedButton(
                        onClick = onOpenChatSupport,
                        border = BorderStroke(1.dp, GoldPrimary),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldBright),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("owner_chat_btn")
                    ) {
                        Icon(Icons.Outlined.Forum, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(lang.t("चैट डेमो", "Chat Demo"), maxLines = 1)
                    }
                }
            }
        }
    }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(DeepMaroonSurface)
            .padding(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = GoldPrimary,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MutedGoldText)
            Text(text = value, style = MaterialTheme.typography.bodyMedium, color = IvoryCream)
        }
    }
}
