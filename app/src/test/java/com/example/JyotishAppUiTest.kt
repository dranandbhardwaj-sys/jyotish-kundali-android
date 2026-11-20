package com.example

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w412dp-h915dp")
class JyotishAppUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun `language toggle switches between Hindi and English`() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("ज्योतिष कुंडली").assertIsDisplayed()

        composeTestRule.onNodeWithTag("language_toggle_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Jyotish Kundali").assertIsDisplayed()

        composeTestRule.onNodeWithTag("language_toggle_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("ज्योतिष कुंडली").assertIsDisplayed()
    }

    @Test
    fun `precision info dialog opens and closes cleanly`() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("precision_info_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("गणना पद्धति एवं सीमाएं").assertIsDisplayed()
        composeTestRule.onNodeWithText("समझ गया").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("गणना पद्धति एवं सीमाएं").assertDoesNotExist()
    }

    @Test
    fun `kundali locks premium features in Free and Expired tiers and unlocks in Premium tier`() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("nav_tab_kundali").performClick()
        composeTestRule.waitForIdle()

        // Empty state shown initially
        composeTestRule.onNodeWithTag("kundali_lazy_list")
            .performScrollToNode(hasTestTag("kundali_empty_state"))
        composeTestRule.onNodeWithTag("kundali_empty_state").assertIsDisplayed()

        // Blank name shows validation error
        composeTestRule.onNodeWithTag("generate_kundali_button").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("generate_kundali_button_error").assertIsDisplayed()

        // Enter name and generate Kundali
        composeTestRule.onNodeWithTag("generate_kundali_button_name_input")
            .performScrollTo()
            .performTextInput("Vikram Joshi")
        composeTestRule.onNodeWithTag("generate_kundali_button").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        // Basic D-1 Lagna chart unlocked in Free tier
        composeTestRule.onNodeWithTag("kundali_result_banner").assertIsDisplayed()
        composeTestRule.onNodeWithTag("kundali_chart_canvas").assertIsDisplayed()

        // Detailed report is locked in Free tier
        composeTestRule.onNodeWithTag("kundali_lazy_list")
            .performScrollToNode(hasTestTag("kundali_report_locked_card"))
        composeTestRule.onNodeWithTag("kundali_report_locked_card").assertIsDisplayed()

        // Switching to D-9 shows Premium Lock card in Free tier
        composeTestRule.onNodeWithTag("kundali_lazy_list")
            .performScrollToNode(hasTestTag("chart_div_d9"))
        composeTestRule.onNodeWithTag("chart_div_d9").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("kundali_d9_locked_card").assertIsDisplayed()

        // Subtab 1 (Avakahada) and Subtab 2 (Dasha) are locked in Free tier
        composeTestRule.onNodeWithTag("kundali_lazy_list")
            .performScrollToNode(hasTestTag("kundali_subtab_1"))
        composeTestRule.onNodeWithTag("kundali_subtab_1").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("kundali_avakahada_locked_card").assertIsDisplayed()

        composeTestRule.onNodeWithTag("kundali_subtab_2").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("kundali_dasha_locked_card").assertIsDisplayed()

        // Tap Upgrade button on locked Dasha card -> navigates to Premium screen
        composeTestRule.onNodeWithTag("kundali_dasha_upgrade_btn").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("premium_status_card").assertIsDisplayed()

        // Attempt checkout shows honest "not connected" notice
        composeTestRule.onNodeWithTag("premium_lazy_list")
            .performScrollToNode(hasTestTag("premium_checkout_button"))
        composeTestRule.onNodeWithTag("premium_checkout_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("payment_gateway_notice").assertIsDisplayed()

        // Switch status to PREMIUM via Demo Status Control
        composeTestRule.onNodeWithTag("premium_lazy_list")
            .performScrollToNode(hasTestTag("status_toggle_premium"))
        composeTestRule.onNodeWithTag("status_toggle_premium").performClick()
        composeTestRule.waitForIdle()

        // Go back to Kundali -> D-9, Dasha, and Detailed Report are unlocked!
        composeTestRule.onNodeWithTag("premium_back_btn").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("kundali_lazy_list")
            .performScrollToNode(hasTestTag("kundali_chart_canvas"))
        composeTestRule.onNodeWithTag("kundali_chart_canvas").assertIsDisplayed()
        composeTestRule.onNodeWithTag("kundali_lazy_list")
            .performScrollToNode(hasTestTag("dasha_card_ketu"))
        composeTestRule.onNodeWithTag("dasha_card_ketu").assertExists()
        composeTestRule.onNodeWithTag("kundali_lazy_list")
            .performScrollToNode(hasTestTag("kundali_detailed_report_card"))
        composeTestRule.onNodeWithTag("kundali_detailed_report_card").assertIsDisplayed()

        // Switch to EXPIRED -> features lock again
        composeTestRule.onNodeWithTag("topbar_premium_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("premium_lazy_list")
            .performScrollToNode(hasTestTag("status_toggle_expired"))
        composeTestRule.onNodeWithTag("status_toggle_expired").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("premium_back_btn").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("kundali_lazy_list")
            .performScrollToNode(hasTestTag("kundali_d9_locked_card"))
        composeTestRule.onNodeWithTag("kundali_d9_locked_card").assertIsDisplayed()
    }

    @Test
    fun `custom latitude and longitude can be applied in birthplace picker`() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("nav_tab_kundali").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("generate_kundali_button_place_picker").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("अक्षांश/देशांतर").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("custom_place_name_input").performTextInput("Haridwar Ashram")
        composeTestRule.onNodeWithTag("custom_lat_input").performTextReplacement("29.95")
        composeTestRule.onNodeWithTag("custom_lon_input").performTextReplacement("78.16")
        composeTestRule.onNodeWithTag("custom_tz_input").performTextReplacement("5.5")
        composeTestRule.onNodeWithTag("apply_custom_coords_btn").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Haridwar Ashram", substring = true).assertIsDisplayed()
    }

    @Test
    fun `rashifal panchang and matching tabs work end to end`() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("nav_tab_rashifal").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("rashi_card_simha").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("rashifal_lazy_list")
            .performScrollToIndex(2)
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("सिंह राशिफल").assertIsDisplayed()

        composeTestRule.onNodeWithTag("nav_tab_panchang").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("panchang_next_day").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("panchang_date_label").assertIsDisplayed()

        composeTestRule.onNodeWithTag("nav_tab_matching").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("matching_lazy_list")
            .performScrollToNode(hasTestTag("matching_empty_state"))
        composeTestRule.onNodeWithTag("matching_empty_state").assertIsDisplayed()

        composeTestRule.onNodeWithTag("matching_lazy_list").performScrollToIndex(0)
        composeTestRule.onNodeWithTag("groom_form_name_input").performScrollTo().performTextInput("Rohan")
        composeTestRule.onNodeWithTag("matching_lazy_list").performScrollToNode(hasTestTag("bride_form_name_input"))
        composeTestRule.onNodeWithTag("bride_form_name_input").performTextInput("Meera")
        composeTestRule.onNodeWithTag("matching_lazy_list").performScrollToNode(hasTestTag("match_kundali_submit_button"))
        composeTestRule.onNodeWithTag("match_kundali_submit_button").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("matching_result_card").assertIsDisplayed()
    }

    @Test
    fun `owner profile floating whatsapp button chat demo and admin settings work cleanly`() {
        composeTestRule.waitForIdle()

        // 1. Floating WhatsApp FAB is displayed on Home and opens WhatsApp Support
        composeTestRule.onNodeWithTag("floating_whatsapp_fab").assertIsDisplayed().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("whatsapp_placeholder_warning").assertIsDisplayed()

        // Verify all 4 WhatsApp topic buttons exist and generate pre-filled message
        composeTestRule.onNodeWithTag("whatsapp_support_lazy_list")
            .performScrollToNode(hasTestTag("whatsapp_topic_general"))
        composeTestRule.onNodeWithTag("whatsapp_topic_astrology").assertIsDisplayed()
        composeTestRule.onNodeWithTag("whatsapp_topic_premium").assertIsDisplayed()
        composeTestRule.onNodeWithTag("whatsapp_topic_kundali").assertIsDisplayed()
        composeTestRule.onNodeWithTag("whatsapp_topic_general").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("whatsapp_support_lazy_list")
            .performScrollToNode(hasTestTag("whatsapp_launch_notice"))
        composeTestRule.onNodeWithTag("whatsapp_launch_notice").assertIsDisplayed()

        // Navigate back Home
        composeTestRule.onNodeWithTag("whatsapp_back_btn").performClick()
        composeTestRule.waitForIdle()

        // 2. Owner Profile shows Dr. Anand Bhardwaj
        composeTestRule.onNodeWithTag("home_lazy_list")
            .performScrollToNode(hasTestTag("home_owner_profile_card"))
        composeTestRule.onNodeWithTag("home_owner_profile_card").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("owner_display_name").assertIsDisplayed()
        composeTestRule.onNodeWithText("डॉ. आनंद भारद्वाज").assertIsDisplayed()

        // Navigate to Chat Support Demo from Owner Profile
        composeTestRule.onNodeWithTag("owner_profile_lazy_list")
            .performScrollToNode(hasTestTag("owner_chat_btn"))
        composeTestRule.onNodeWithTag("owner_chat_btn").performClick()
        composeTestRule.waitForIdle()

        // 3. Chat Support explicitly shows Demo disclaimer & submits question locally
        composeTestRule.onNodeWithTag("chat_demo_banner").assertIsDisplayed()
        composeTestRule.onNodeWithTag("chat_message_input").performTextInput("विवाह मुहूर्त कब शुभ है?")
        composeTestRule.onNodeWithTag("chat_submit_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("विवाह मुहूर्त कब शुभ है?").assertIsDisplayed()
        composeTestRule.onNodeWithText("डेमो सूचना", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag("chat_to_whatsapp_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("chat_whatsapp_notice").assertIsDisplayed()

        // 4. Open Admin Settings from topbar menu, edit WhatsApp number & yearly price
        composeTestRule.onNodeWithTag("topbar_more_menu_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("menu_admin_settings").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("admin_settings_lazy_list")
            .performScrollToNode(hasTestTag("admin_whatsapp_input"))
        composeTestRule.onNodeWithTag("admin_whatsapp_input").performTextReplacement("+919811223344")

        composeTestRule.onNodeWithTag("admin_settings_lazy_list")
            .performScrollToNode(hasTestTag("admin_price_yearly"))
        composeTestRule.onNodeWithTag("admin_price_yearly").performTextReplacement("2499")

        composeTestRule.onNodeWithTag("admin_settings_lazy_list")
            .performScrollToNode(hasTestTag("admin_save_button"))
        composeTestRule.onNodeWithTag("admin_save_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("admin_save_notice").assertIsDisplayed()

        // Verify updated WhatsApp number & pricing propagate to WhatsApp Support and Premium screens
        composeTestRule.onNodeWithTag("floating_whatsapp_fab").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("+919811223344").assertIsDisplayed()

        composeTestRule.onNodeWithTag("topbar_premium_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("premium_lazy_list")
            .performScrollToNode(hasTestTag("plan_yearly"))
        composeTestRule.onNodeWithText("₹2499").assertIsDisplayed()
    }
}
