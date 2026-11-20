package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.WhatsAppHelper
import com.example.model.AppLanguage
import com.example.model.WhatsAppTopic
import com.example.model.t
import com.example.ui.JyotishUiState
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun WhatsAppSupportScreen(
    uiState: JyotishUiState,
    onOpenAdminSettings: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)
    val lang = uiState.language
    val owner = uiState.ownerProfile
    val context = LocalContext.current

    var selectedTopic by remember { mutableStateOf(WhatsAppTopic.ASTROLOGY_QUESTION) }
    var customNote by remember { mutableStateOf("") }
    var launchNotice by remember { mutableStateOf<String?>(null) }

    fun composedMessage(topic: WhatsAppTopic): String {
        val base = if (lang == AppLanguage.HI) topic.templateHi else topic.templateEn
        val note = customNote.trim()
        return if (note.isNotEmpty()) "$base\n\n${lang.t("मेरा प्रश्न", "My Question")}: $note" else base
    }

    val previewMessage = composedMessage(selectedTopic)
    val previewUrl = WhatsAppHelper.buildWhatsAppUrl(owner.whatsappNumber, previewMessage)

    fun triggerWhatsApp(topic: WhatsAppTopic) {
        selectedTopic = topic
        val msg = composedMessage(topic)
        val launched = WhatsAppHelper.openWhatsApp(context, owner.whatsappNumber, msg)
        launchNotice = if (launched) {
            lang.t(
                "व्हाट्सएप संदेश तैयार किया गया: ${owner.whatsappNumber}",
                "Prepared WhatsApp message to ${owner.whatsappNumber}"
            )
        } else {
            lang.t(
                "इस डिवाइस पर व्हाट्सएप या ब्राउज़र उपलब्ध नहीं है। लिंक: ${WhatsAppHelper.buildWhatsAppUrl(owner.whatsappNumber, msg)}",
                "No WhatsApp or browser app found on this device. Generated link: ${WhatsAppHelper.buildWhatsAppUrl(owner.whatsappNumber, msg)}"
            )
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
                modifier = Modifier.testTag("whatsapp_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = lang.t("वापस जाएं", "Go Back"),
                    tint = GoldBright
                )
            }
            SectionHeader(
                title = lang.t("व्हाट्सएप ज्योतिष सहायता", "WhatsApp Support Desk"),
                subtitle = lang.t("पूर्व-लिखित संदेश के साथ तुरंत जुड़ें", "Connect via pre-filled WhatsApp consultation"),
                icon = Icons.Filled.Chat,
                modifier = Modifier.weight(1f)
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 92.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .testTag("whatsapp_support_lazy_list")
        ) {

        // Placeholder number warning / Configured Number Banner
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (owner.isWhatsappPlaceholder()) CautionAmberBg else ShubhEmeraldBg
                ),
                border = BorderStroke(
                    1.dp,
                    if (owner.isWhatsappPlaceholder()) CautionAmber else ShubhEmerald
                ),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("whatsapp_number_card")
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = lang.t("कॉन्फ़िगर किया गया व्हाट्सएप नंबर", "Configured WhatsApp Number"),
                                style = MaterialTheme.typography.labelMedium,
                                color = MutedGoldText
                            )
                            Text(
                                text = owner.whatsappNumber,
                                style = MaterialTheme.typography.titleMedium,
                                color = IvoryCream,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.testTag("whatsapp_configured_number")
                            )
                        }
                        TextButton(
                            onClick = onOpenAdminSettings,
                            modifier = Modifier.testTag("whatsapp_edit_number_btn")
                        ) {
                            Text(lang.t("नंबर बदलें", "Configure"), color = GoldBright)
                        }
                    }
                    if (owner.isWhatsappPlaceholder()) {
                        Text(
                            text = lang.t(
                                "नोट: यह एक प्लेसहोल्डर नंबर है। लाइव उपयोग से पहले एडमिन सेटिंग्स में अंतर्राष्ट्रीय कंट्री कोड (+91...) सहित अपना वास्तविक नंबर जोड़ें।",
                                "Placeholder Alert: This is a placeholder phone number until you configure your real international number in Admin Settings."
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = CautionAmber,
                            modifier = Modifier.testTag("whatsapp_placeholder_warning")
                        )
                    }
                }
            }
        }

        // Optional user message input
        item {
            OutlinedTextField(
                value = customNote,
                onValueChange = { customNote = it },
                label = { Text(lang.t("अतिरिक्त प्रश्न या जन्म विवरण (वैकल्पिक)", "Custom Note or Question (Optional)")) },
                placeholder = { Text(lang.t("अपना प्रश्न यहाँ लिखें...", "Type your question to include in WhatsApp...")) },
                minLines = 2,
                maxLines = 4,
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
                    .testTag("whatsapp_custom_note_input")
            )
        }

        // 4 Required Topic Buttons
        item {
            Text(
                text = lang.t("सहायता विषय चुनें (पूर्व-लिखित संदेश)", "Choose a Support Topic (Pre-Filled Message)"),
                style = MaterialTheme.typography.titleMedium,
                color = GoldBright,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                WhatsAppTopic.entries.forEach { topic ->
                    val isSelected = selectedTopic == topic
                    Card(
                        onClick = { triggerWhatsApp(topic) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) RoyalCrimson.copy(alpha = 0.55f) else CardMaroon
                        ),
                        border = BorderStroke(1.dp, if (isSelected) GoldPrimary else SubtleGoldBorder),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("whatsapp_topic_${topic.id}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Icon(
                                imageVector = when (topic) {
                                    WhatsAppTopic.ASTROLOGY_QUESTION -> Icons.Filled.AutoAwesome
                                    WhatsAppTopic.PREMIUM_SUPPORT -> Icons.Filled.WorkspacePremium
                                    WhatsAppTopic.KUNDALI_HELP -> Icons.Filled.Hexagon
                                    WhatsAppTopic.GENERAL_SUPPORT -> Icons.Filled.SupportAgent
                                },
                                contentDescription = null,
                                tint = GoldBright,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (lang == AppLanguage.HI) topic.titleHi else topic.titleEn,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = IvoryCream,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (lang == AppLanguage.HI) topic.subtitleHi else topic.subtitleEn,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MutedGoldText
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = lang.t("व्हाट्सएप भेजें", "Send on WhatsApp"),
                                tint = ShubhEmerald
                            )
                        }
                    }
                }
            }
        }

        // Pre-filled Message & Generated Link Preview
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DeepMaroonSurface),
                border = BorderStroke(1.dp, SubtleGoldBorder),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("whatsapp_preview_card")
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = lang.t("संदेश पूर्वावलोकन (Pre-filled Message)", "Pre-filled Message Preview"),
                        style = MaterialTheme.typography.labelLarge,
                        color = GoldBright,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(CardMaroon)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = previewMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = IvoryCream
                        )
                    }
                    Text(
                        text = previewUrl,
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedGoldText
                    )

                    if (!launchNotice.isNullOrBlank()) {
                        Surface(
                            color = CardMaroon,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, ShubhEmerald.copy(alpha = 0.6f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("whatsapp_launch_notice")
                        ) {
                            Text(
                                text = launchNotice!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = ShubhEmerald,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    Button(
                        onClick = { triggerWhatsApp(selectedTopic) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ShubhEmerald,
                            contentColor = ObsidianMaroon
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("whatsapp_open_app_btn")
                    ) {
                        Icon(Icons.Filled.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = lang.t("व्हाट्सएप पर संदेश भेजें", "Open in WhatsApp"),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
    }
}
