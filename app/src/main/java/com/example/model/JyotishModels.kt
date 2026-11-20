package com.example.model

import androidx.compose.ui.graphics.Color

enum class AppLanguage {
    HI, EN
}

fun AppLanguage.t(hi: String, en: String): String = if (this == AppLanguage.HI) hi else en

enum class Gender(val hi: String, val en: String) {
    MALE("पुरुष", "Male"),
    FEMALE("महिला", "Female"),
    OTHER("अन्य", "Other")
}

enum class ChartStyle(val hi: String, val en: String) {
    NORTH_INDIAN("उत्तर भारतीय", "North Indian"),
    SOUTH_INDIAN("दक्षिण भारतीय", "South Indian")
}

enum class ChartDivision(val hi: String, val en: String) {
    D1("लग्न (D-1)", "Lagna (D-1)"),
    D9("नवांश (D-9)", "Navamsha (D-9)")
}

data class BirthPlace(
    val id: String,
    val nameHi: String,
    val nameEn: String,
    val stateHi: String,
    val stateEn: String,
    val latitude: Double,
    val longitude: Double,
    val timezoneOffsetHours: Double = 5.5,
    val isCustom: Boolean = false
) {
    fun displayName(lang: AppLanguage): String {
        val base = if (lang == AppLanguage.HI) nameHi else nameEn
        val region = if (lang == AppLanguage.HI) stateHi else stateEn
        return if (region.isNotBlank()) "$base, $region" else base
    }

    fun coordinatesLabel(): String {
        val latDir = if (latitude >= 0) "N" else "S"
        val lonDir = if (longitude >= 0) "E" else "W"
        val tzSign = if (timezoneOffsetHours >= 0) "+" else ""
        return String.format(
            "%.2f°%s, %.2f°%s • UTC%s%.1f",
            kotlin.math.abs(latitude), latDir,
            kotlin.math.abs(longitude), lonDir,
            tzSign, timezoneOffsetHours
        )
    }
}

data class BirthDetails(
    val name: String,
    val gender: Gender,
    val year: Int,
    val month: Int, // 1..12
    val day: Int,   // 1..31
    val hour24: Int, // 0..23
    val minute: Int, // 0..59
    val place: BirthPlace
)

enum class Element(val hi: String, val en: String) {
    AGNI("अग्नि", "Fire"),
    PRITHVI("पृथ्वी", "Earth"),
    VAYU("वायु", "Air"),
    JAL("जल", "Water")
}

enum class Graha(
    val id: Int,
    val nameHi: String,
    val nameEn: String,
    val abbrHi: String,
    val abbrEn: String,
    val natureHi: String,
    val natureEn: String,
    val dayHi: String,
    val dayEn: String,
    val gemHi: String,
    val gemEn: String,
    val mantra: String,
    val elementHi: String,
    val elementEn: String,
    val descHi: String,
    val descEn: String,
    val accentColor: Color
) {
    SURYA(
        id = 0, nameHi = "सूर्य", nameEn = "Sun (Surya)", abbrHi = "सू", abbrEn = "Su",
        natureHi = "क्रूर / राजसी", natureEn = "Malefic / Royal",
        dayHi = "रविवार", dayEn = "Sunday",
        gemHi = "माणिक्य (Ruby)", gemEn = "Ruby (Manikya)",
        mantra = "ॐ घृणि सूर्याय नमः",
        elementHi = "अग्नि", elementEn = "Fire",
        descHi = "आत्मा, पिता, तेज, सम्मान, नेतृत्व और राजयोग के कारक ग्रह।",
        descEn = "Significator of soul, vitality, father, authority, leadership, and royal honor.",
        accentColor = Color(0xFFF59E0B)
    ),
    CHANDRA(
        id = 1, nameHi = "चंद्र", nameEn = "Moon (Chandra)", abbrHi = "चं", abbrEn = "Mo",
        natureHi = "शुभ / सौम्य", natureEn = "Benefic / Gentle",
        dayHi = "सोमवार", dayEn = "Monday",
        gemHi = "मोती (Pearl)", gemEn = "Pearl (Moti)",
        mantra = "ॐ सों सोमाय नमः",
        elementHi = "जल", elementEn = "Water",
        descHi = "मन, माता, भावनाएं, कल्पना, शांति और तरल धन के कारक।",
        descEn = "Significator of mind, mother, emotions, intuition, serenity, and nurturing.",
        accentColor = Color(0xFFE2E8F0)
    ),
    MANGALA(
        id = 2, nameHi = "मंगल", nameEn = "Mars (Mangala)", abbrHi = "मं", abbrEn = "Ma",
        natureHi = "क्रूर / ऊर्जावान", natureEn = "Malefic / Fiery",
        dayHi = "मंगलवार", dayEn = "Tuesday",
        gemHi = "मूंगा (Red Coral)", gemEn = "Red Coral (Moonga)",
        mantra = "ॐ अं अंगारकाय नमः",
        elementHi = "अग्नि", elementEn = "Fire",
        descHi = "साहस, पराक्रम, भूमि, भाई, ऊर्जा और रक्षा शक्ति के कारक।",
        descEn = "Significator of courage, willpower, property, siblings, and vitality.",
        accentColor = Color(0xFFEF4444)
    ),
    BUDHA(
        id = 3, nameHi = "बुध", nameEn = "Mercury (Budha)", abbrHi = "बु", abbrEn = "Me",
        natureHi = "शुभ / तार्किक", natureEn = "Benefic / Analytical",
        dayHi = "बुधवार", dayEn = "Wednesday",
        gemHi = "पन्ना (Emerald)", gemEn = "Emerald (Panna)",
        mantra = "ॐ बुं बुधाय नमः",
        elementHi = "पृथ्वी", elementEn = "Earth",
        descHi = "बुद्धि, वाणी, व्यापार, गणित, लेखन और संचार के कारक।",
        descEn = "Significator of intellect, speech, commerce, logic, writing, and adaptability.",
        accentColor = Color(0xFF10B981)
    ),
    GURU(
        id = 4, nameHi = "बृहस्पति", nameEn = "Jupiter (Guru)", abbrHi = "गु", abbrEn = "Ju",
        natureHi = "परम शुभ", natureEn = "Great Benefic",
        dayHi = "गुरुवार", dayEn = "Thursday",
        gemHi = "पुखराज (Yellow Sapphire)", gemEn = "Yellow Sapphire (Pukhraj)",
        mantra = "ॐ बृं बृहस्पतये नमः",
        elementHi = "आकाश", elementEn = "Ether",
        descHi = "ज्ञान, धर्म, संतान, सौभाग्य, शिक्षा और आध्यात्मिक उन्नति के कारक।",
        descEn = "Significator of wisdom, dharma, prosperity, progeny, and spiritual grace.",
        accentColor = Color(0xFFFACC15)
    ),
    SHUKRA(
        id = 5, nameHi = "शुक्र", nameEn = "Venus (Shukra)", abbrHi = "शु", abbrEn = "Ve",
        natureHi = "शुभ / वैभव", natureEn = "Benefic / Luxurious",
        dayHi = "शुक्रवार", dayEn = "Friday",
        gemHi = "हीरा / ओपल", gemEn = "Diamond / Opal",
        mantra = "ॐ शुं शुक्राय नमः",
        elementHi = "जल", elementEn = "Water",
        descHi = "प्रेम, विवाह, कला, सौंदर्य, वाहन और भौतिक सुख-समृद्धि के कारक।",
        descEn = "Significator of love, marriage, arts, beauty, harmony, and comforts.",
        accentColor = Color(0xFFF472B6)
    ),
    SHANI(
        id = 6, nameHi = "शनि", nameEn = "Saturn (Shani)", abbrHi = "श", abbrEn = "Sa",
        natureHi = "क्रूर / न्यायप्रिय", natureEn = "Malefic / Karmic",
        dayHi = "शनिवार", dayEn = "Saturday",
        gemHi = "नीलम (Blue Sapphire)", gemEn = "Blue Sapphire (Neelam)",
        mantra = "ॐ शं शनैश्चराय नमः",
        elementHi = "वायु", elementEn = "Air",
        descHi = "कर्म, न्याय, धैर्य, अनुशासन, दीर्घायु और सेवा भाव के कारक।",
        descEn = "Significator of karma, discipline, longevity, justice, and perseverance.",
        accentColor = Color(0xFF60A5FA)
    ),
    RAHU(
        id = 7, nameHi = "राहु", nameEn = "Rahu (North Node)", abbrHi = "रा", abbrEn = "Ra",
        natureHi = "छाया ग्रह", natureEn = "Shadow Node",
        dayHi = "शनिवार", dayEn = "Saturday",
        gemHi = "गोमेद (Hessonite)", gemEn = "Hessonite (Gomed)",
        mantra = "ॐ रां राहवे नमः",
        elementHi = "वायु", elementEn = "Air",
        descHi = "महत्वाकांक्षा, नवीन तकनीक, विदेश यात्रा और आकस्मिक परिवर्तन के कारक।",
        descEn = "Significator of ambition, innovation, foreign lands, and sudden breakthroughs.",
        accentColor = Color(0xFFA78BFA)
    ),
    KETU(
        id = 8, nameHi = "केतु", nameEn = "Ketu (South Node)", abbrHi = "के", abbrEn = "Ke",
        natureHi = "छाया / मोक्षकारक", natureEn = "Shadow / Mystical",
        dayHi = "मंगलवार", dayEn = "Tuesday",
        gemHi = "लहसुनिया (Cat's Eye)", gemEn = "Cat's Eye (Lehsunia)",
        mantra = "ॐ कें केतवे नमः",
        elementHi = "अग्नि", elementEn = "Fire",
        descHi = "मोक्ष, वैराग्य, गूढ़ ज्ञान, ध्यान और पूर्वजन्म के संस्कारों के कारक।",
        descEn = "Significator of liberation, mysticism, detachment, and spiritual insight.",
        accentColor = Color(0xFFFB923C)
    )
}

enum class Rashi(
    val number: Int, // 1..12
    val symbol: String,
    val nameHi: String,
    val nameEn: String,
    val lord: Graha,
    val element: Element,
    val luckyColorHi: String,
    val luckyColorEn: String,
    val luckyNumber: Int,
    val directionHi: String,
    val directionEn: String
) {
    MESHA(1, "♈", "मेष", "Aries", Graha.MANGALA, Element.AGNI, "सिंदूरी लाल", "Crimson Red", 9, "पूर्व", "East"),
    VRISHABHA(2, "♉", "वृषभ", "Taurus", Graha.SHUKRA, Element.PRITHVI, "दुधिया सफ़ेद", "Ivory White", 6, "दक्षिण", "South"),
    MITHUNA(3, "♊", "मिथुन", "Gemini", Graha.BUDHA, Element.VAYU, "पन्ना हरा", "Emerald Green", 5, "पश्चिम", "West"),
    KARKA(4, "♋", "कर्क", "Cancer", Graha.CHANDRA, Element.JAL, "मोती सफ़ेद", "Pearl Silver", 2, "उत्तर", "North"),
    SIMHA(5, "♌", "सिंह", "Leo", Graha.SURYA, Element.AGNI, "सुनहरा केसरिया", "Royal Gold", 1, "पूर्व", "East"),
    KANYA(6, "♍", "कन्या", "Virgo", Graha.BUDHA, Element.PRITHVI, "हल्का हरा", "Sage Green", 5, "दक्षिण", "South"),
    TULA(7, "♎", "तुला", "Libra", Graha.SHUKRA, Element.VAYU, "चमकीला क्रीम", "Champagne", 6, "पश्चिम", "West"),
    VRISHCHIKA(8, "♏", "वृश्चिक", "Scorpio", Graha.MANGALA, Element.JAL, "गहरा मैरून", "Deep Maroon", 9, "उत्तर", "North"),
    DHANU(9, "♐", "धनु", "Sagittarius", Graha.GURU, Element.AGNI, "पीतांबरी पीला", "Saffron Yellow", 3, "पूर्व", "East"),
    MAKARA(10, "♑", "मकर", "Capricorn", Graha.SHANI, Element.PRITHVI, "नीलांबर", "Midnight Blue", 8, "दक्षिण", "South"),
    KUMBHA(11, "♒", "कुंभ", "Aquarius", Graha.SHANI, Element.VAYU, "जामुनी नीला", "Indigo", 8, "पश्चिम", "West"),
    MEENA(12, "♓", "मीन", "Pisces", Graha.GURU, Element.JAL, "स्वर्ण पीला", "Golden Amber", 3, "उत्तर", "North");

    companion object {
        fun fromNumber(num: Int): Rashi {
            val idx = ((num - 1) % 12 + 12) % 12
            return entries[idx]
        }
    }
}

enum class GrahaDignity(val hi: String, val en: String) {
    EXALTED("उच्च", "Exalted"),
    OWN_SIGN("स्वग्रही", "Own Sign"),
    FRIENDLY("मित्र राशि", "Friendly"),
    NEUTRAL("सम", "Neutral"),
    DEBILITATED("नीच", "Debilitated")
}

data class GrahaPosition(
    val graha: Graha,
    val longitude: Double,
    val rashi: Rashi,
    val degreeInRashi: Double,
    val house: Int, // 1..12
    val navamshaRashi: Rashi,
    val navamshaHouse: Int, // 1..12
    val nakshatraHi: String,
    val nakshatraEn: String,
    val nakshatraIndex: Int,
    val pada: Int,
    val isRetrograde: Boolean,
    val dignity: GrahaDignity
)

data class AntardashaPeriod(
    val graha: Graha,
    val startLabel: String,
    val endLabel: String,
    val durationMonths: Int,
    val isCurrent: Boolean
)

data class DashaPeriod(
    val graha: Graha,
    val startYear: Int,
    val endYear: Int,
    val startLabel: String,
    val endLabel: String,
    val durationYears: Double,
    val isCurrent: Boolean,
    val antardashas: List<AntardashaPeriod>
)

data class AvakahadaDetails(
    val varnaHi: String,
    val varnaEn: String,
    val vashyaHi: String,
    val vashyaEn: String,
    val yoniHi: String,
    val yoniEn: String,
    val ganaHi: String,
    val ganaEn: String,
    val nadiHi: String,
    val nadiEn: String,
    val tatvaHi: String,
    val tatvaEn: String,
    val payaHi: String,
    val payaEn: String
)

data class KundaliResult(
    val birthDetails: BirthDetails,
    val julianDay: Double,
    val ayanamsha: Double,
    val lagnaRashi: Rashi,
    val lagnaDegree: Double,
    val navamshaLagnaRashi: Rashi,
    val moonRashi: Rashi,
    val sunRashi: Rashi,
    val moonNakshatraHi: String,
    val moonNakshatraEn: String,
    val moonNakshatraIndex: Int,
    val moonPada: Int,
    val grahaPositions: List<GrahaPosition>,
    val avakahada: AvakahadaDetails,
    val dashas: List<DashaPeriod>,
    val isManglik: Boolean,
    val manglikReasonHi: String,
    val manglikReasonEn: String
)

data class PanchangResult(
    val year: Int,
    val month: Int,
    val day: Int,
    val place: BirthPlace,
    val varaHi: String,
    val varaEn: String,
    val varaLord: Graha,
    val pakshaHi: String,
    val pakshaEn: String,
    val tithiHi: String,
    val tithiEn: String,
    val tithiNumber: Int,
    val tithiProgressPercent: Int,
    val tithiEndTime: String,
    val nakshatraHi: String,
    val nakshatraEn: String,
    val nakshatraPada: Int,
    val nakshatraLord: Graha,
    val nakshatraEndTime: String,
    val yogaHi: String,
    val yogaEn: String,
    val yogaEndTime: String,
    val karanaHi: String,
    val karanaEn: String,
    val sunrise: String,
    val sunset: String,
    val rahuKaal: String,
    val abhijitMuhurat: String,
    val brahmaMuhurat: String,
    val moonRashi: Rashi,
    val sunRashi: Rashi,
    val dishaShoolHi: String,
    val dishaShoolEn: String,
    val auspiciousNoteHi: String,
    val auspiciousNoteEn: String
)

data class DailyRashifal(
    val rashi: Rashi,
    val moonTransitRashi: Rashi,
    val moonTransitHouse: Int,
    val rating: Int, // 3..5
    val summaryHi: String,
    val summaryEn: String,
    val careerHi: String,
    val careerEn: String,
    val loveHi: String,
    val loveEn: String,
    val healthHi: String,
    val healthEn: String,
    val remedyHi: String,
    val remedyEn: String
)

data class KootaScore(
    val nameHi: String,
    val nameEn: String,
    val descHi: String,
    val descEn: String,
    val groomValueHi: String,
    val groomValueEn: String,
    val brideValueHi: String,
    val brideValueEn: String,
    val obtained: Double,
    val maximum: Double
)

data class KundaliMatchResult(
    val groomKundali: KundaliResult,
    val brideKundali: KundaliResult,
    val kootas: List<KootaScore>,
    val totalScore: Double,
    val maxScore: Double = 36.0,
    val verdictTitleHi: String,
    val verdictTitleEn: String,
    val verdictDescHi: String,
    val verdictDescEn: String,
    val manglikCompatHi: String,
    val manglikCompatEn: String
)
