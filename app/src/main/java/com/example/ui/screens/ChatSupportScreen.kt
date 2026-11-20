package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Forum
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
fun ChatSupportScreen(
    uiState: JyotishUiState,
    onSubmitQuestion: (String, WhatsAppTopic) -> Unit,
    onClearChat: () -> Unit,
    onOpenWhatsAppSupport: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)
    val lang = uiState.language
    val context = LocalContext.current

    var draftMessage by remember { mutableStateOf("") }
    var selectedTopic by remember { mutableStateOf(WhatsAppTopic.ASTROLOGY_QUESTION) }
    var handoffNotice by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .padding(bottom = 72.dp) // Leave room above floating WhatsApp FAB
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("chat_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = lang.t("वापस जाएं", "Go Back"),
                    tint = GoldBright
                )
            }
            SectionHeader(
                title = lang.t("चैट सहायता (डेमो)", "Chat Support (Demo)"),
                subtitle = lang.t("प्रश्न लिखें या व्हाट्सएप पर स्थानांतरित करें", "Submit a question or hand off to WhatsApp"),
                icon = Icons.Outlined.Forum,
                modifier = Modifier.weight(1f)
            )
            if (uiState.demoChatMessages.isNotEmpty()) {
                IconButton(onClick = onClearChat) {
                    Icon(
                        imageVector = Icons.Filled.DeleteOutline,
                        contentDescription = lang.t("चैट साफ़ करें", "Clear demo chat"),
                        tint = MutedGoldText
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Explicit Honest Demo Banner
        Surface(
            color = CautionAmberBg,
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, CautionAmber),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("chat_demo_banner")
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.padding(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    tint = CautionAmber,
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = lang.t(
                        "डेमो चैट सूचना: रियल-टाइम चैट बैकएंड अभी जुड़ा नहीं है। यहाँ भेजे गए प्रश्न केवल स्थानीय डेमो सत्र में दिखते हैं और लाइव एजेंट को डिलीवर नहीं होते। वास्तविक उत्तर के लिए 'व्हाट्सएप पर भेजें' उपयोग करें।",
                        "Demo Mode Notice: No real-time messaging backend is connected yet. Submitted messages are stored locally for demo only and are NOT delivered to an agent. Use 'Send via WhatsApp' for real support."
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = IvoryCream
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Topic chips row
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            WhatsAppTopic.entries.take(3).forEach { topic ->
                val selected = selectedTopic == topic
                FilterChip(
                    selected = selected,
                    onClick = { selectedTopic = topic },
                    label = {
                        Text(
                            text = if (lang == AppLanguage.HI) topic.titleHi else topic.titleEn.substringBefore(" "),
                            style = MaterialTheme.typography.labelSmall,
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
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Chat Transcript List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DeepMaroonSurface)
                .padding(12.dp)
                .testTag("chat_messages_list")
        ) {
            if (uiState.demoChatMessages.isEmpty()) {
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp, horizontal = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Forum,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = lang.t(
                                "नीचे अपना ज्योतिष या सहायता प्रश्न लिखें",
                                "Write your astrology or support question below"
                            ),
                            style = MaterialTheme.typography.titleSmall,
                            color = GoldBright
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = lang.t(
                                "सबमिट करने पर आपका प्रश्न स्थानीय डेमो थ्रेड में जुड़ जाएगा और आप उसे एक क्लिक में व्हाट्सएप पर भेज सकते हैं।",
                                "Submitting saves your question to this demo session so you can preview or transfer it directly to WhatsApp."
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedGoldText
                        )
                    }
                }
            } else {
                items(uiState.demoChatMessages, key = { it.id }) { msg ->
                    Row(
                        horizontalArrangement = if (msg.senderIsUser) Arrangement.End else Arrangement.Start,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            color = if (msg.senderIsUser) RoyalCrimson else CardMaroon,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(
                                1.dp,
                                if (msg.senderIsUser) GoldPrimary.copy(alpha = 0.6f) else CautionAmber.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.widthIn(max = 310.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = msg.text,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = IvoryCream
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (msg.senderIsUser) {
                                        "${lang.t("आप", "You")} • ${msg.timestampLabel}"
                                    } else {
                                        "${lang.t("डेमो सिस्टम", "Demo System")} • ${msg.timestampLabel}"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MutedGoldText
                                )
                            }
                        }
                    }
                }
            }
        }

        if (!handoffNotice.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = handoffNotice!!,
                style = MaterialTheme.typography.labelSmall,
                color = ShubhEmerald,
                modifier = Modifier.testTag("chat_whatsapp_notice")
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = draftMessage,
                onValueChange = { draftMessage = it },
                placeholder = { Text(lang.t("प्रश्न लिखें...", "Type your question...")) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = SubtleGoldBorder,
                    focusedTextColor = IvoryCream,
                    unfocusedTextColor = IvoryCream,
                    cursorColor = GoldBright
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_message_input")
            )
            FilledIconButton(
                onClick = {
                    if (draftMessage.trim().isNotEmpty()) {
                        onSubmitQuestion(draftMessage, selectedTopic)
                        draftMessage = ""
                    }
                },
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = GoldPrimary,
                    contentColor = ObsidianMaroon
                ),
                modifier = Modifier
                    .size(50.dp)
                    .testTag("chat_submit_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = lang.t("प्रश्न सबमिट करें", "Submit question")
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    val lastUserQuestion = uiState.demoChatMessages.lastOrNull { it.senderIsUser }?.text
                    val textToSend = draftMessage.trim().ifEmpty {
                        lastUserQuestion ?: if (lang == AppLanguage.HI) selectedTopic.templateHi else selectedTopic.templateEn
                    }
                    val ok = WhatsAppHelper.openWhatsApp(context, uiState.ownerProfile.whatsappNumber, textToSend)
                    handoffNotice = if (ok) {
                        lang.t("संदेश व्हाट्सएप पर भेजा गया।", "Opened WhatsApp with your question.")
                    } else {
                        lang.t(
                            "व्हाट्सएप लिंक तैयार: ${WhatsAppHelper.buildWhatsAppUrl(uiState.ownerProfile.whatsappNumber, textToSend)}",
                            "WhatsApp link ready: ${WhatsAppHelper.buildWhatsAppUrl(uiState.ownerProfile.whatsappNumber, textToSend)}"
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = ShubhEmerald,
                    contentColor = ObsidianMaroon
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .testTag("chat_to_whatsapp_button")
            ) {
                Icon(Icons.Filled.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = lang.t("व्हाट्सएप पर भेजें", "Send via WhatsApp"),
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
            OutlinedButton(
                onClick = onOpenWhatsAppSupport,
                border = BorderStroke(1.dp, GoldPrimary),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldBright),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .height(46.dp)
                    .testTag("chat_open_wa_topics_btn")
            ) {
                Text(lang.t("विकल्प", "Topics"), maxLines = 1)
            }
        }
    }
}
