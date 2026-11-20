package com.example.model

enum class SubscriptionStatus(val hi: String, val en: String) {
    FREE("मुफ़्त (Free)", "Free"),
    PREMIUM("प्रीमियम (सक्रिय)", "Premium (Active)"),
    EXPIRED("समाप्त (Expired)", "Expired")
}

enum class BillingCycle(val hi: String, val en: String) {
    MONTHLY("मासिक", "Monthly"),
    YEARLY("वार्षिक", "Yearly"),
    LIFETIME("आजीवन", "Lifetime")
}

data class OwnerProfileConfig(
    val nameHi: String = "डॉ. आनंद भारद्वाज",
    val nameEn: String = "Dr. Anand Bhardwaj",
    val titleHi: String = "वैदिक ज्योतिषाचार्य एवं वास्तु विशेषज्ञ",
    val titleEn: String = "Vedic Astrologer & Jyotish Acharya",
    val experienceHi: String = "18+ वर्षों का वैदिक ज्योतिष अनुभव",
    val experienceEn: String = "18+ Years of Vedic Astrology Experience",
    val photoUri: String = "",
    val introHi: String = "डॉ. आनंद भारद्वाज पारंपरिक पाराशरी ज्योतिष, कुंडली विश्लेषण, अष्टकूट मिलान और वैदिक पंचांग के विद्वान हैं।",
    val introEn: String = "Dr. Anand Bhardwaj is a scholar of classical Parashari Jyotish, natal chart analysis, Ashtakoota matching, and Vedic Panchang.",
    val aboutHi: String = "शास्त्रीय वैदिक परंपरा और आधुनिक खगोलीय गणनाओं के समन्वय से सटीक जन्मपत्रिका विश्लेषण, ग्रह दोष निवारण, रत्न परामर्श और जीवन मार्गदर्शन प्रदान किया जाता है।",
    val aboutEn: String = "Combining classical Vedic scriptures with astronomical precision to offer authentic birth chart insights, planetary remedies, and life guidance.",
    val phone: String = "+91 99999 99999 (Placeholder)",
    val email: String = "dranandbhardwaj@gmail.com",
    val whatsappNumber: String = "+919999999999",
    val addressHi: String = "वाराणसी एवं नई दिल्ली, भारत",
    val addressEn: String = "Varanasi & New Delhi, India",
    val supportHoursHi: String = "प्रातः 9:00 – रात्रि 8:00 (IST)",
    val supportHoursEn: String = "9:00 AM – 8:00 PM (IST)",
    val websiteName: String = "Jyotish Kundali",
    val websiteUrl: String = "https://jyotishkundali.example.com"
) {
    fun isWhatsappPlaceholder(): Boolean {
        val digits = whatsappNumber.filter { it.isDigit() }
        return digits == "919999999999" || digits.length < 8
    }
}

data class MembershipConfig(
    val status: SubscriptionStatus = SubscriptionStatus.FREE,
    val monthlyPriceInr: Int = 299,
    val yearlyPriceInr: Int = 1999,
    val lifetimePriceInr: Int = 4999
)

enum class WhatsAppTopic(
    val id: String,
    val titleHi: String,
    val titleEn: String,
    val subtitleHi: String,
    val subtitleEn: String,
    val templateHi: String,
    val templateEn: String
) {
    ASTROLOGY_QUESTION(
        id = "astrology",
        titleHi = "ज्योतिष प्रश्न पूछें",
        titleEn = "Ask an Astrology Question",
        subtitleHi = "करियर, विवाह, ग्रह दोष या मुहूर्त पर परामर्श",
        subtitleEn = "Consultation on career, marriage, grahas or muhurat",
        templateHi = "नमस्ते डॉ. आनंद भारद्वाज जी, मुझे अपनी कुंडली/ज्योतिष से जुड़ा एक प्रश्न पूछना है।",
        templateEn = "Namaste Dr. Anand Bhardwaj, I would like to ask a Vedic astrology question."
    ),
    PREMIUM_SUPPORT(
        id = "premium",
        titleHi = "प्रीमियम सहायता",
        titleEn = "Premium Support",
        subtitleHi = "प्रीमियम सदस्यता, रिपोर्ट व अपग्रेड सहायता",
        subtitleEn = "Help with Premium plans, reports & upgrades",
        templateHi = "नमस्ते, मुझे ज्योतिष कुंडली प्रीमियम सदस्यता के बारे में जानकारी चाहिए।",
        templateEn = "Namaste, I need assistance regarding Jyotish Kundali Premium membership."
    ),
    KUNDALI_HELP(
        id = "kundali",
        titleHi = "कुंडली सहायता",
        titleEn = "Kundali Help",
        subtitleHi = "जन्म विवरण, लग्न, नवांश या दशा विश्लेषण मदद",
        subtitleEn = "Help with birth details, Lagna, Navamsha or Dasha",
        templateHi = "नमस्ते, मुझे अपनी जन्म कुंडली और ग्रह दशा के विश्लेषण में सहायता चाहिए।",
        templateEn = "Namaste, I need help interpreting my birth Kundali and Dasha."
    ),
    GENERAL_SUPPORT(
        id = "general",
        titleHi = "सामान्य सहायता",
        titleEn = "General Support",
        subtitleHi = "ऐप, पंचांग या अन्य सामान्य पूछताछ",
        subtitleEn = "App usage, Panchang or general inquiries",
        templateHi = "नमस्ते, मुझे ज्योतिष कुंडली ऐप से संबंधित सामान्य सहायता चाहिए।",
        templateEn = "Namaste, I need general support regarding the Jyotish Kundali app."
    )
}

data class DemoChatMessage(
    val id: String,
    val senderIsUser: Boolean,
    val text: String,
    val timestampLabel: String,
    val topic: WhatsAppTopic? = null
)
