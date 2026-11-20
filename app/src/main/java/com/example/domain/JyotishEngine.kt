package com.example.domain

import com.example.model.*
import java.util.Calendar
import kotlin.math.*

/**
 * Pure Kotlin Vedic Astronomy & Jyotish Computation Engine.
 *
 * Implements:
 * - Julian Day (UT) & Lahiri Chitrapaksha Ayanamsha
 * - Sun, Moon (with evection, variation, annual equation), Mercury, Venus, Mars,
 *   Jupiter & Saturn (with Great Inequality perturbations), Rahu & Ketu (Lunar Nodes)
 * - Sidereal Lagna (Ascendant) from RAMC & birth coordinates
 * - Navamsha (D-9) division
 * - Sunrise-epoch (Udaya Tithi) location- & date-dependent Panchang with end times
 * - Vimshottari Mahadasha + 9 Antardasha sub-periods
 * - Classical Brihat Parashara Ashtakoota 36-Guna Matching (exact matrices)
 * - Chandra Gochar (live lunar transit) Daily Rashifal
 */
object JyotishEngine {

    private data class NakshatraInfo(
        val hi: String,
        val en: String,
        val lord: Graha,
        val ganaIdx: Int, // 0=Deva, 1=Manushya, 2=Rakshasa
        val yoniIdx: Int, // 0..13
        val nadiIdx: Int  // 0=Adi, 1=Madhya, 2=Antya
    )

    private val nakshatras = listOf(
        NakshatraInfo("अश्विनी", "Ashwini", Graha.KETU, 0, 0, 0),
        NakshatraInfo("भरणी", "Bharani", Graha.SHUKRA, 1, 1, 1),
        NakshatraInfo("कृत्तिका", "Krittika", Graha.SURYA, 2, 2, 2),
        NakshatraInfo("रोहिणी", "Rohini", Graha.CHANDRA, 1, 3, 2),
        NakshatraInfo("मृगशिरा", "Mrigashira", Graha.MANGALA, 0, 3, 1),
        NakshatraInfo("आर्द्रा", "Ardra", Graha.RAHU, 1, 4, 0),
        NakshatraInfo("पुनर्वसु", "Punarvasu", Graha.GURU, 0, 5, 0),
        NakshatraInfo("पुष्य", "Pushya", Graha.SHANI, 0, 2, 1),
        NakshatraInfo("आश्लेषा", "Ashlesha", Graha.BUDHA, 2, 5, 2),
        NakshatraInfo("मघा", "Magha", Graha.KETU, 2, 6, 2),
        NakshatraInfo("पूर्वा फाल्गुनी", "Purva Phalguni", Graha.SHUKRA, 1, 6, 1),
        NakshatraInfo("उत्तरा फाल्गुनी", "Uttara Phalguni", Graha.SURYA, 1, 7, 0),
        NakshatraInfo("हस्त", "Hasta", Graha.CHANDRA, 0, 8, 0),
        NakshatraInfo("चित्रा", "Chitra", Graha.MANGALA, 2, 9, 1),
        NakshatraInfo("स्वाति", "Swati", Graha.RAHU, 0, 8, 2),
        NakshatraInfo("विशाखा", "Vishakha", Graha.GURU, 2, 9, 2),
        NakshatraInfo("अनुराधा", "Anuradha", Graha.SHANI, 0, 10, 1),
        NakshatraInfo("ज्येष्ठा", "Jyeshtha", Graha.BUDHA, 2, 10, 0),
        NakshatraInfo("मूल", "Mula", Graha.KETU, 2, 4, 0),
        NakshatraInfo("पूर्वाषाढ़ा", "Purva Ashadha", Graha.SHUKRA, 1, 11, 1),
        NakshatraInfo("उत्तराषाढ़ा", "Uttara Ashadha", Graha.SURYA, 1, 12, 2),
        NakshatraInfo("श्रवण", "Shravana", Graha.CHANDRA, 0, 11, 2),
        NakshatraInfo("धनिष्ठा", "Dhanishta", Graha.MANGALA, 2, 13, 1),
        NakshatraInfo("शतभिषा", "Shatabhisha", Graha.RAHU, 2, 0, 0),
        NakshatraInfo("पूर्वा भाद्रपद", "Purva Bhadrapada", Graha.GURU, 1, 13, 0),
        NakshatraInfo("उत्तरा भाद्रपद", "Uttara Bhadrapada", Graha.SHANI, 1, 7, 1),
        NakshatraInfo("रेवती", "Revati", Graha.BUDHA, 0, 1, 2)
    )

    // Yoni indices: 0=Horse,1=Elephant,2=Sheep,3=Serpent,4=Dog,5=Cat,6=Rat,7=Cow,8=Buffalo,9=Tiger,10=Deer,11=Monkey,12=Mongoose,13=Lion
    private val yoniNames = listOf(
        "अश्व" to "Horse", "गज" to "Elephant", "मेष" to "Sheep", "सर्प" to "Serpent",
        "श्वान" to "Dog", "मार्जार" to "Cat", "मूषक" to "Rat", "गौ" to "Cow",
        "महिष" to "Buffalo", "व्याघ्र" to "Tiger", "मृग" to "Deer", "वानर" to "Monkey",
        "नकुल" to "Mongoose", "सिंह" to "Lion"
    )

    // Classical 14x14 Yoni Koota compatibility matrix (0..4)
    private val yoniMatrix = arrayOf(
        intArrayOf(4,2,2,3,2,2,2,1,0,1,3,3,2,1), // 0 Horse (enemy Buffalo=8)
        intArrayOf(2,4,3,3,2,2,2,2,3,1,2,3,2,0), // 1 Elephant (enemy Lion=13)
        intArrayOf(2,3,4,2,1,2,1,3,3,1,2,0,3,1), // 2 Sheep (enemy Monkey=11)
        intArrayOf(3,3,2,4,2,1,1,1,1,2,2,2,0,2), // 3 Serpent (enemy Mongoose=12)
        intArrayOf(2,2,1,2,4,2,1,2,2,1,0,2,1,1), // 4 Dog (enemy Deer=10)
        intArrayOf(2,2,2,1,2,4,0,2,2,1,3,3,2,1), // 5 Cat (enemy Rat=6)
        intArrayOf(2,2,1,1,1,0,4,2,2,2,2,2,1,2), // 6 Rat (enemy Cat=5)
        intArrayOf(1,2,3,1,2,2,2,4,3,0,3,2,2,1), // 7 Cow (enemy Tiger=9)
        intArrayOf(0,3,3,1,2,2,2,3,4,1,2,2,2,1), // 8 Buffalo (enemy Horse=0)
        intArrayOf(1,1,1,2,1,1,2,0,1,4,1,1,2,1), // 9 Tiger (enemy Cow=7)
        intArrayOf(3,2,2,2,0,3,2,3,2,1,4,2,2,1), // 10 Deer (enemy Dog=4)
        intArrayOf(3,3,0,2,2,3,2,2,2,1,2,4,3,2), // 11 Monkey (enemy Sheep=2)
        intArrayOf(2,2,3,0,1,2,1,2,2,2,2,3,4,2), // 12 Mongoose (enemy Serpent=3)
        intArrayOf(1,0,1,2,1,1,2,1,1,1,1,2,2,4)  // 13 Lion (enemy Elephant=1)
    )

    private val ganaNames = listOf(
        "देव" to "Deva", "मनुष्य" to "Manushya", "राक्षस" to "Rakshasa"
    )

    // Classical Gana Maitri Matrix [groom][bride] -> 0..6
    private val ganaMatrix = arrayOf(
        intArrayOf(6, 6, 1), // Deva groom
        intArrayOf(5, 6, 0), // Manushya groom
        intArrayOf(1, 0, 6)  // Rakshasa groom
    )

    private val nadiNames = listOf(
        "आदि" to "Adi", "मध्य" to "Madhya", "अन्त्य" to "Antya"
    )

    // 7 Planetary Lords of Rashis: Sun(0), Moon(1), Mars(2), Mercury(3), Jupiter(4), Venus(5), Saturn(6)
    // Classical Naisargika Maitri: 2=Friend, 1=Neutral, 0=Enemy
    private val planetFriendship = arrayOf(
        //       Su Mo Ma Me Ju Ve Sa
        intArrayOf(2, 2, 2, 1, 2, 0, 0), // Sun
        intArrayOf(2, 2, 1, 2, 1, 1, 1), // Moon
        intArrayOf(2, 2, 2, 0, 2, 1, 1), // Mars
        intArrayOf(2, 0, 1, 2, 1, 2, 1), // Mercury
        intArrayOf(2, 2, 2, 0, 2, 0, 1), // Jupiter
        intArrayOf(0, 0, 1, 2, 1, 2, 2), // Venus
        intArrayOf(0, 0, 0, 2, 1, 2, 2)  // Saturn
    )

    private val tithiNames = listOf(
        "प्रतिपदा" to "Pratipada", "द्वितीया" to "Dwitiya", "तृतीया" to "Tritiya",
        "चतुर्थी" to "Chaturthi", "पञ्चमी" to "Panchami", "षष्ठी" to "Shashthi",
        "सप्तमी" to "Saptami", "अष्टमी" to "Ashtami", "नवमी" to "Navami",
        "दशमी" to "Dashami", "एकादशी" to "Ekadashi", "द्वादशी" to "Dwadashi",
        "त्रयोदशी" to "Trayodashi", "चतुर्दशी" to "Chaturdashi",
        "पूर्णिमा" to "Purnima", "अमावस्या" to "Amavasya"
    )

    private val yogaNames = listOf(
        "विष्कम्भ" to "Vishkambha", "प्रीति" to "Priti", "आयुष्मान" to "Ayushman",
        "सौभाग्य" to "Saubhagya", "शोभन" to "Shobhana", "अतिगण्ड" to "Atiganda",
        "सुकर्मा" to "Sukarma", "धृति" to "Dhriti", "शूल" to "Shula",
        "गण्ड" to "Ganda", "वृद्धि" to "Vriddhi", "ध्रुव" to "Dhruva",
        "व्याघात" to "Vyaghata", "हर्षण" to "Harshana", "वज्र" to "Vajra",
        "सिद्धि" to "Siddhi", "व्यतीपात" to "Vyatipata", "वरीयान" to "Variyan",
        "परिघ" to "Parigha", "शिव" to "Shiva", "सिद्ध" to "Siddha",
        "साध्य" to "Sadhya", "शुभ" to "Shubha", "शुक्ल" to "Shukla",
        "ब्रह्म" to "Brahma", "इन्द्र" to "Indra", "वैधृति" to "Vaidhriti"
    )

    private val movableKaranas = listOf(
        "बव" to "Bava", "बालव" to "Balava", "कौलव" to "Kaulava",
        "तैतिल" to "Taitila", "गर" to "Gara", "वणिज" to "Vanija",
        "विष्टि (भद्रा)" to "Vishti (Bhadra)"
    )

    private val fixedKaranas = mapOf(
        0 to ("किंस्तुघ्न" to "Kimstughna"),
        57 to ("शकुनि" to "Shakuni"),
        58 to ("चतुष्पद" to "Chatushpada"),
        59 to ("नाग" to "Naga")
    )

    private val dashaOrder = listOf(
        Graha.KETU to 7.0,
        Graha.SHUKRA to 20.0,
        Graha.SURYA to 6.0,
        Graha.CHANDRA to 10.0,
        Graha.MANGALA to 7.0,
        Graha.RAHU to 18.0,
        Graha.GURU to 16.0,
        Graha.SHANI to 19.0,
        Graha.BUDHA to 17.0
    )

    private fun norm360(deg: Double): Double {
        var r = deg % 360.0
        if (r < 0) r += 360.0
        return r
    }

    private fun sind(deg: Double): Double = sin(Math.toRadians(deg))
    private fun cosd(deg: Double): Double = cos(Math.toRadians(deg))
    private fun tand(deg: Double): Double = tan(Math.toRadians(deg))

    /**
     * Computes Julian Day in Universal Time (UT) from local clock time and UTC offset.
     */
    fun computeJulianDay(
        year: Int,
        month: Int,
        day: Int,
        hour24: Int,
        minute: Int,
        timezoneOffsetHours: Double
    ): Double {
        val utHour = hour24 + minute / 60.0 - timezoneOffsetHours
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = y / 100
        val b = 2 - a + (a / 4)
        return floor(365.25 * (y + 4716)) +
            floor(30.6001 * (m + 1)) +
            day + (utHour / 24.0) + b - 1524.5
    }

    /**
     * Lahiri Chitrapaksha Ayanamsha in degrees for Julian Day (IAU precession polynomial).
     */
    fun computeLahiriAyanamsha(jd: Double): Double {
        val t = (jd - 2451545.0) / 36525.0
        return 23.857092 + 1.3969713 * t - 0.0003086 * t * t
    }

    private fun sunTropicalLongitude(d: Double): Double {
        val w = 282.9404 + 4.70935e-5 * d
        val e = 0.016709 - 1.151e-9 * d
        val m = norm360(356.0470 + 0.9856002585 * d)
        val eAnom = m + Math.toDegrees(e) * sind(m) * (1.0 + e * cosd(m))
        val xv = cosd(eAnom) - e
        val yv = sqrt(1.0 - e * e) * sind(eAnom)
        val v = Math.toDegrees(atan2(yv, xv))
        return norm360(v + w)
    }

    private fun moonTropicalLongitude(d: Double): Double {
        val n = norm360(125.1228 - 0.0529538083 * d)
        val i = 5.1454
        val w = norm360(318.0634 + 0.1643573223 * d)
        val a = 60.2666
        val e = 0.054900
        val m = norm360(115.3654 + 13.0649929509 * d)
        var eAnom = m + Math.toDegrees(e) * sind(m) * (1.0 + e * cosd(m))
        repeat(3) {
            val f = eAnom - Math.toDegrees(e) * sind(eAnom) - m
            eAnom -= f / (1.0 - e * cosd(eAnom))
        }
        val xv = a * (cosd(eAnom) - e)
        val yv = a * sqrt(1.0 - e * e) * sind(eAnom)
        val v = Math.toDegrees(atan2(yv, xv))
        val r = sqrt(xv * xv + yv * yv)
        val xh = r * (cosd(n) * cosd(v + w) - sind(n) * sind(v + w) * cosd(i))
        val yh = r * (sind(n) * cosd(v + w) + cosd(n) * sind(v + w) * cosd(i))
        var lon = Math.toDegrees(atan2(yh, xh))

        val ms = norm360(356.0470 + 0.9856002585 * d)
        val ws = 282.9404 + 4.70935e-5 * d
        val ls = norm360(ms + ws)
        val lm = norm360(n + w + m)
        val dMoon = norm360(lm - ls)
        val fMoon = norm360(lm - n)
        lon += -1.274 * sind(m - 2 * dMoon) +
            0.658 * sind(2 * dMoon) -
            0.186 * sind(ms) -
            0.059 * sind(2 * m - 2 * dMoon) -
            0.057 * sind(m - 2 * dMoon + ms) +
            0.053 * sind(m + 2 * dMoon) +
            0.046 * sind(2 * dMoon - ms) +
            0.041 * sind(m - ms) -
            0.035 * sind(dMoon) -
            0.031 * sind(m + ms) -
            0.015 * sind(2 * fMoon - 2 * dMoon) +
            0.011 * sind(m - 4 * dMoon)
        return norm360(lon)
    }

    private fun geocentricPlanetLon(
        d: Double,
        n0: Double, nRate: Double,
        i0: Double,
        w0: Double, wRate: Double,
        a: Double,
        e0: Double, eRate: Double,
        m0: Double, mRate: Double,
        sunLon: Double, sunDist: Double,
        perturbationDeg: (Double) -> Double = { 0.0 }
    ): Pair<Double, Boolean> {
        fun calcLonAt(dayOffset: Double): Double {
            val dd = d + dayOffset
            val n = norm360(n0 + nRate * dd)
            val w = norm360(w0 + wRate * dd)
            val e = e0 + eRate * dd
            val m = norm360(m0 + mRate * dd)
            var ea = m + Math.toDegrees(e) * sind(m) * (1.0 + e * cosd(m))
            repeat(3) {
                val f = ea - Math.toDegrees(e) * sind(ea) - m
                ea -= f / (1.0 - e * cosd(ea))
            }
            val xv = a * (cosd(ea) - e)
            val yv = a * sqrt(1.0 - e * e) * sind(ea)
            val v = Math.toDegrees(atan2(yv, xv))
            val r = sqrt(xv * xv + yv * yv)
            val xh = r * (cosd(n) * cosd(v + w) - sind(n) * sind(v + w) * cosd(i0))
            val yh = r * (sind(n) * cosd(v + w) + cosd(n) * sind(v + w) * cosd(i0))

            val sLon = if (dayOffset == 0.0) sunLon else sunTropicalLongitude(dd)
            val ms = norm360(356.0470 + 0.9856002585 * dd)
            val sDist = if (dayOffset == 0.0) sunDist else (1.0 - 0.016709 * cosd(ms))
            val xs = sDist * cosd(sLon)
            val ys = sDist * sind(sLon)
            val xg = xh + xs
            val yg = yh + ys
            return norm360(Math.toDegrees(atan2(yg, xg)) + perturbationDeg(dd))
        }
        val lonNow = calcLonAt(0.0)
        val lonNext = calcLonAt(1.0)
        var diff = lonNext - lonNow
        if (diff > 180) diff -= 360.0
        if (diff < -180) diff += 360.0
        return lonNow to (diff < 0.0)
    }

    private fun computeAscendantTropical(jd: Double, lat: Double, lon: Double): Double {
        val t = (jd - 2451545.0) / 36525.0
        val gmst = norm360(
            280.46061837 +
                360.98564736629 * (jd - 2451545.0) +
                0.000387933 * t * t -
                t * t * t / 38710000.0
        )
        val ramc = norm360(gmst + lon)
        val eps = 23.4392911 - 0.0130042 * t
        val y = cosd(ramc)
        val x = -(sind(ramc) * cosd(eps) + tand(lat.coerceIn(-66.0, 66.0)) * sind(eps))
        return norm360(Math.toDegrees(atan2(y, x)))
    }

    private fun computeNavamshaRashi(siderealLon: Double): Rashi {
        val signIndex = (siderealLon / 30.0).toInt().coerceIn(0, 11)
        val degInSign = siderealLon % 30.0
        val navPart = (degInSign / (30.0 / 9.0)).toInt().coerceIn(0, 8)
        val startSign = when (signIndex % 4) {
            0 -> 0 // Fire -> Aries
            1 -> 9 // Earth -> Capricorn
            2 -> 6 // Air -> Libra
            else -> 3 // Water -> Cancer
        }
        val navSign = (startSign + navPart) % 12
        return Rashi.entries[navSign]
    }

    private fun evaluateDignity(graha: Graha, rashi: Rashi): GrahaDignity {
        val num = rashi.number
        val exalted = when (graha) {
            Graha.SURYA -> 1
            Graha.CHANDRA -> 2
            Graha.MANGALA -> 10
            Graha.BUDHA -> 6
            Graha.GURU -> 4
            Graha.SHUKRA -> 12
            Graha.SHANI -> 7
            Graha.RAHU -> 2
            Graha.KETU -> 8
        }
        val debilitated = ((exalted + 5) % 12) + 1
        if (num == exalted) return GrahaDignity.EXALTED
        if (num == debilitated) return GrahaDignity.DEBILITATED
        if (rashi.lord == graha) return GrahaDignity.OWN_SIGN
        val friends = when (graha) {
            Graha.SURYA -> setOf(Graha.CHANDRA, Graha.MANGALA, Graha.GURU)
            Graha.CHANDRA -> setOf(Graha.SURYA, Graha.BUDHA)
            Graha.MANGALA -> setOf(Graha.SURYA, Graha.CHANDRA, Graha.GURU)
            Graha.BUDHA -> setOf(Graha.SURYA, Graha.SHUKRA)
            Graha.GURU -> setOf(Graha.SURYA, Graha.CHANDRA, Graha.MANGALA)
            Graha.SHUKRA -> setOf(Graha.BUDHA, Graha.SHANI)
            Graha.SHANI -> setOf(Graha.BUDHA, Graha.SHUKRA)
            Graha.RAHU -> setOf(Graha.SHUKRA, Graha.SHANI, Graha.BUDHA)
            Graha.KETU -> setOf(Graha.MANGALA, Graha.GURU, Graha.SHUKRA)
        }
        return if (rashi.lord in friends) GrahaDignity.FRIENDLY else GrahaDignity.NEUTRAL
    }

    private fun fractionalYearToLabel(yearDecimal: Double): String {
        val y = floor(yearDecimal).toInt()
        val m = (((yearDecimal - y) * 12.0).toInt() + 1).coerceIn(1, 12)
        return String.format("%02d/%04d", m, y)
    }

    fun generateKundali(details: BirthDetails): KundaliResult {
        val jd = computeJulianDay(
            details.year, details.month, details.day,
            details.hour24, details.minute, details.place.timezoneOffsetHours
        )
        val ayanamsha = computeLahiriAyanamsha(jd)
        val d = jd - 2451543.5

        val sunTrop = sunTropicalLongitude(d)
        val moonTrop = moonTropicalLongitude(d)
        val sunSid = norm360(sunTrop - ayanamsha)
        val moonSid = norm360(moonTrop - ayanamsha)

        val ms = norm360(356.0470 + 0.9856002585 * d)
        val sunDist = 1.0 - 0.016709 * cosd(ms)

        val (mercTrop, mercRet) = geocentricPlanetLon(
            d, 48.3313, 3.24587e-5, 7.0047, 29.1241, 1.01444e-5,
            0.387098, 0.205635, 5.59e-10, 168.6562, 4.0923344368, sunTrop, sunDist
        )
        val (venTrop, venRet) = geocentricPlanetLon(
            d, 76.6799, 2.46590e-5, 3.3946, 54.8910, 1.38374e-5,
            0.723330, 0.006773, -1.302e-9, 48.0052, 1.6021302244, sunTrop, sunDist
        )
        val (marsTrop, marsRet) = geocentricPlanetLon(
            d, 49.5574, 2.11081e-5, 1.8497, 286.5016, 2.92961e-5,
            1.523688, 0.093405, 2.516e-9, 18.6021, 0.5240207766, sunTrop, sunDist
        )
        // Jupiter & Saturn Great Inequality perturbation terms
        val (jupTrop, jupRet) = geocentricPlanetLon(
            d, 100.4542, 2.76854e-5, 1.3030, 273.8777, 1.64505e-5,
            5.20256, 0.048498, 4.469e-9, 19.8950, 0.0830853001, sunTrop, sunDist
        ) { dd ->
            val mj = norm360(19.8950 + 0.0830853001 * dd)
            val mSat = norm360(316.9670 + 0.0334442282 * dd)
            -0.332 * sind(2 * mj - 5 * mSat - 67.6) -
                0.056 * sind(2 * mj - 2 * mSat + 21.0) +
                0.042 * sind(3 * mj - 5 * mSat + 21.0)
        }
        val (satTrop, satRet) = geocentricPlanetLon(
            d, 113.6634, 2.38980e-5, 2.4886, 339.3939, 2.97661e-5,
            9.55475, 0.055546, -9.499e-9, 316.9670, 0.0334442282, sunTrop, sunDist
        ) { dd ->
            val mj = norm360(19.8950 + 0.0830853001 * dd)
            val mSat = norm360(316.9670 + 0.0334442282 * dd)
            0.812 * sind(2 * mj - 5 * mSat - 67.6) -
                0.229 * cosd(2 * mj - 4 * mSat - 2.0) +
                0.119 * sind(mj - 2 * mSat - 3.0)
        }
        val rahuTrop = norm360(125.1228 - 0.0529538083 * d)
        val ketuTrop = norm360(rahuTrop + 180.0)

        val lagnaTrop = computeAscendantTropical(jd, details.place.latitude, details.place.longitude)
        val lagnaSid = norm360(lagnaTrop - ayanamsha)
        val lagnaSignIdx = (lagnaSid / 30.0).toInt().coerceIn(0, 11)
        val lagnaRashi = Rashi.entries[lagnaSignIdx]
        val lagnaDeg = lagnaSid % 30.0
        val navamshaLagnaRashi = computeNavamshaRashi(lagnaSid)

        val rawGrahas = listOf(
            Triple(Graha.SURYA, sunSid, false),
            Triple(Graha.CHANDRA, moonSid, false),
            Triple(Graha.MANGALA, norm360(marsTrop - ayanamsha), marsRet),
            Triple(Graha.BUDHA, norm360(mercTrop - ayanamsha), mercRet),
            Triple(Graha.GURU, norm360(jupTrop - ayanamsha), jupRet),
            Triple(Graha.SHUKRA, norm360(venTrop - ayanamsha), venRet),
            Triple(Graha.SHANI, norm360(satTrop - ayanamsha), satRet),
            Triple(Graha.RAHU, norm360(rahuTrop - ayanamsha), true),
            Triple(Graha.KETU, norm360(ketuTrop - ayanamsha), true)
        )

        val positions = rawGrahas.map { (graha, sidLon, isRetro) ->
            val signIdx = (sidLon / 30.0).toInt().coerceIn(0, 11)
            val rashi = Rashi.entries[signIdx]
            val house = ((signIdx - lagnaSignIdx + 12) % 12) + 1
            val navRashi = computeNavamshaRashi(sidLon)
            val navHouse = ((navRashi.number - navamshaLagnaRashi.number + 12) % 12) + 1
            val nakSpan = 360.0 / 27.0
            val nakIdx = (sidLon / nakSpan).toInt().coerceIn(0, 26)
            val degInNak = sidLon % nakSpan
            val pada = ((degInNak / (nakSpan / 4.0)).toInt() + 1).coerceIn(1, 4)
            val nak = nakshatras[nakIdx]
            GrahaPosition(
                graha = graha,
                longitude = sidLon,
                rashi = rashi,
                degreeInRashi = sidLon % 30.0,
                house = house,
                navamshaRashi = navRashi,
                navamshaHouse = navHouse,
                nakshatraHi = nak.hi,
                nakshatraEn = nak.en,
                nakshatraIndex = nakIdx,
                pada = pada,
                isRetrograde = isRetro,
                dignity = evaluateDignity(graha, rashi)
            )
        }

        val moonPos = positions.first { it.graha == Graha.CHANDRA }
        val sunPos = positions.first { it.graha == Graha.SURYA }
        val marsPos = positions.first { it.graha == Graha.MANGALA }

        // Vimshottari Mahadasha + 9 Antardasha Sub-Periods
        val nakSpan = 360.0 / 27.0
        val moonFracUsed = (moonSid % nakSpan) / nakSpan
        val moonFracRemaining = 1.0 - moonFracUsed
        val startLord = nakshatras[moonPos.nakshatraIndex].lord
        val startOrderIdx = dashaOrder.indexOfFirst { it.first == startLord }.coerceAtLeast(0)

        val nowCal = Calendar.getInstance()
        val nowDecYear = nowCal.get(Calendar.YEAR) + (nowCal.get(Calendar.MONTH) / 12.0) + (nowCal.get(Calendar.DAY_OF_MONTH) / 365.25)
        val birthDecYear = details.year + ((details.month - 1) / 12.0) + ((details.day - 1) / 365.25)

        val dashas = mutableListOf<DashaPeriod>()
        var cursorYear = birthDecYear
        for (i in 0 until 9) {
            val (lord, fullYears) = dashaOrder[(startOrderIdx + i) % 9]
            val span = if (i == 0) fullYears * moonFracRemaining else fullYears
            val mahaStart = cursorYear
            val mahaEnd = cursorYear + span

            // Calculate Antardashas inside this Mahadasha
            val mahaOrderIdx = dashaOrder.indexOfFirst { it.first == lord }
            val fullMahaStart = if (i == 0) mahaEnd - fullYears else mahaStart
            var adCursor = fullMahaStart
            val antardashas = mutableListOf<AntardashaPeriod>()
            for (j in 0 until 9) {
                val (subLord, subYears) = dashaOrder[(mahaOrderIdx + j) % 9]
                val adSpanYears = (fullYears * subYears) / 120.0
                val adStart = adCursor
                val adEnd = adCursor + adSpanYears
                adCursor = adEnd
                if (adEnd > mahaStart) {
                    val clippedStart = max(adStart, mahaStart)
                    antardashas.add(
                        AntardashaPeriod(
                            graha = subLord,
                            startLabel = fractionalYearToLabel(clippedStart),
                            endLabel = fractionalYearToLabel(adEnd),
                            durationMonths = ((adEnd - clippedStart) * 12.0).roundToInt().coerceAtLeast(1),
                            isCurrent = nowDecYear >= clippedStart && nowDecYear < adEnd
                        )
                    )
                }
            }

            dashas.add(
                DashaPeriod(
                    graha = lord,
                    startYear = floor(mahaStart).toInt(),
                    endYear = floor(mahaEnd).toInt(),
                    startLabel = fractionalYearToLabel(mahaStart),
                    endLabel = fractionalYearToLabel(mahaEnd),
                    durationYears = span,
                    isCurrent = nowDecYear >= mahaStart && nowDecYear < mahaEnd,
                    antardashas = antardashas
                )
            )
            cursorYear = mahaEnd
        }

        // Avakahada
        val moonNak = nakshatras[moonPos.nakshatraIndex]
        val (varnaHi, varnaEn) = when (moonPos.rashi.element) {
            Element.JAL -> "ब्राह्मण" to "Brahmin"
            Element.AGNI -> "क्षत्रिय" to "Kshatriya"
            Element.PRITHVI -> "वैश्य" to "Vaishya"
            Element.VAYU -> "शूद्र" to "Shudra"
        }
        val (vashyaHi, vashyaEn) = when (moonPos.rashi.number) {
            1, 2, 10 -> "चतुष्पद" to "Chatushpada"
            3, 6, 7, 11 -> "द्विपद" to "Dwipada"
            4, 12 -> "जलचर" to "Jalachara"
            5 -> "वनचर" to "Vanachara"
            8 -> "कीट" to "Keeta"
            else -> "द्विपद" to "Dwipada"
        }
        val (payaHi, payaEn) = when (moonPos.house) {
            1, 6, 11 -> "स्वर्ण" to "Gold (Swarna)"
            2, 5, 9 -> "रजत" to "Silver (Rajata)"
            3, 7, 10 -> "ताम्र" to "Copper (Tamra)"
            else -> "लोह" to "Iron (Loha)"
        }
        val yoni = yoniNames[moonNak.yoniIdx]
        val gana = ganaNames[moonNak.ganaIdx]
        val nadi = nadiNames[moonNak.nadiIdx]

        val manglikHouses = setOf(1, 4, 7, 8, 12)
        val isManglik = marsPos.house in manglikHouses
        val manglikReasonHi = if (isManglik) {
            "मंगल ग्रह लग्न से ${marsPos.house}वें भाव में स्थित है, अतः आंशिक/पूर्ण मांगलिक योग बनता है।"
        } else {
            "मंगल ग्रह ${marsPos.house}वें भाव में स्थित है। कुंडली मांगलिक दोष से मुक्त है।"
        }
        val manglikReasonEn = if (isManglik) {
            "Mars is placed in house ${marsPos.house} from Lagna, forming Manglik Dosha."
        } else {
            "Mars is in house ${marsPos.house} from Lagna; chart is free from Manglik Dosha."
        }

        return KundaliResult(
            birthDetails = details,
            julianDay = jd,
            ayanamsha = ayanamsha,
            lagnaRashi = lagnaRashi,
            lagnaDegree = lagnaDeg,
            navamshaLagnaRashi = navamshaLagnaRashi,
            moonRashi = moonPos.rashi,
            sunRashi = sunPos.rashi,
            moonNakshatraHi = moonNak.hi,
            moonNakshatraEn = moonNak.en,
            moonNakshatraIndex = moonPos.nakshatraIndex,
            moonPada = moonPos.pada,
            grahaPositions = positions,
            avakahada = AvakahadaDetails(
                varnaHi = varnaHi, varnaEn = varnaEn,
                vashyaHi = vashyaHi, vashyaEn = vashyaEn,
                yoniHi = yoni.first, yoniEn = yoni.second,
                ganaHi = gana.first, ganaEn = gana.second,
                nadiHi = nadi.first, nadiEn = nadi.second,
                tatvaHi = moonPos.rashi.element.hi, tatvaEn = moonPos.rashi.element.en,
                payaHi = payaHi, payaEn = payaEn
            ),
            dashas = dashas,
            isManglik = isManglik,
            manglikReasonHi = manglikReasonHi,
            manglikReasonEn = manglikReasonEn
        )
    }

    private fun fmtTime(h: Double): String {
        val totalMin = ((h * 60).roundToInt() % 1440 + 1440) % 1440
        val hh = totalMin / 60
        val mm = totalMin % 60
        val ampm = if (hh < 12) "AM" else "PM"
        val h12 = if (hh % 12 == 0) 12 else hh % 12
        return String.format("%02d:%02d %s", h12, mm, ampm)
    }

    /**
     * Location- & Date-dependent Vedic Panchang computed at exact local Sunrise (Udaya Tithi).
     */
    fun computePanchang(year: Int, month: Int, day: Int, place: BirthPlace): PanchangResult {
        val cal = Calendar.getInstance().apply { set(year, month - 1, day) }
        val dayOfYear = cal.get(Calendar.DAY_OF_YEAR)
        val decl = 23.44 * sind((360.0 / 365.0) * (dayOfYear - 81))
        val latRad = Math.toRadians(place.latitude.coerceIn(-60.0, 60.0))
        val declRad = Math.toRadians(decl)
        val cosH = (-tan(latRad) * tan(declRad)).coerceIn(-0.92, 0.92)
        val halfDayHours = Math.toDegrees(acos(cosH)) / 15.0

        // Equation of time correction (minutes -> hours)
        val bEq = Math.toRadians((360.0 / 365.0) * (dayOfYear - 81))
        val eotHours = (9.87 * sin(2 * bEq) - 7.53 * cos(bEq) - 1.5 * sin(bEq)) / 60.0
        val solarNoonLocal = 12.0 + place.timezoneOffsetHours - (place.longitude / 15.0) - eotHours
        val sunriseHrs = solarNoonLocal - halfDayHours
        val sunsetHrs = solarNoonLocal + halfDayHours

        val srHour = floor(sunriseHrs).toInt().coerceIn(0, 23)
        val srMin = ((sunriseHrs - srHour) * 60.0).roundToInt().coerceIn(0, 59)

        // Evaluate astronomical Panchang at exact local Sunrise for Udaya Tithi & location accuracy
        val jd = computeJulianDay(year, month, day, srHour, srMin, place.timezoneOffsetHours)
        val ayanamsha = computeLahiriAyanamsha(jd)
        val d = jd - 2451543.5
        val sunSid = norm360(sunTropicalLongitude(d) - ayanamsha)
        val moonSid = norm360(moonTropicalLongitude(d) - ayanamsha)

        // Instantaneous daily speeds (deg/day) for end-time estimates
        val sunSidNext = norm360(sunTropicalLongitude(d + 1.0) - ayanamsha)
        val moonSidNext = norm360(moonTropicalLongitude(d + 1.0) - ayanamsha)
        val sunSpeed = norm360(sunSidNext - sunSid).let { if (it > 180) it - 360 else it }.coerceAtLeast(0.5)
        val moonSpeed = norm360(moonSidNext - moonSid).let { if (it > 180) it - 360 else it }.coerceAtLeast(10.0)

        val diff = norm360(moonSid - sunSid)
        val tithiIdx = (diff / 12.0).toInt().coerceIn(0, 29)
        val tithiRemDeg = 12.0 - (diff % 12.0)
        val tithiProgress = (((diff % 12.0) / 12.0) * 100.0).toInt().coerceIn(1, 99)
        val tithiRemHours = (tithiRemDeg / (moonSpeed - sunSpeed).coerceAtLeast(8.0)) * 24.0
        val tithiEndStr = fmtTime(sunriseHrs + tithiRemHours)

        val isShukla = tithiIdx < 15
        val pakshaHi = if (isShukla) "शुक्ल पक्ष" else "कृष्ण पक्ष"
        val pakshaEn = if (isShukla) "Shukla Paksha" else "Krishna Paksha"
        val tithiNamePair = when (tithiIdx) {
            14 -> tithiNames[14]
            29 -> tithiNames[15]
            else -> tithiNames[tithiIdx % 15]
        }

        val nakSpan = 360.0 / 27.0
        val nakIdx = (moonSid / nakSpan).toInt().coerceIn(0, 26)
        val nak = nakshatras[nakIdx]
        val nakPada = (((moonSid % nakSpan) / (nakSpan / 4.0)).toInt() + 1).coerceIn(1, 4)
        val nakRemDeg = nakSpan - (moonSid % nakSpan)
        val nakEndStr = fmtTime(sunriseHrs + (nakRemDeg / moonSpeed) * 24.0)

        val yogaSum = norm360(sunSid + moonSid)
        val yogaIdx = (yogaSum / nakSpan).toInt().coerceIn(0, 26)
        val yogaPair = yogaNames[yogaIdx]
        val yogaRemDeg = nakSpan - (yogaSum % nakSpan)
        val yogaEndStr = fmtTime(sunriseHrs + (yogaRemDeg / (moonSpeed + sunSpeed)) * 24.0)

        val karanaIdx = (diff / 6.0).toInt().coerceIn(0, 59)
        val karanaPair = fixedKaranas[karanaIdx] ?: movableKaranas[(karanaIdx - 1) % 7]

        data class VaraInfo(val hi: String, val en: String, val lord: Graha, val dishaHi: String, val dishaEn: String)
        val dow = cal.get(Calendar.DAY_OF_WEEK)
        val vara = when (dow) {
            Calendar.SUNDAY -> VaraInfo("रविवार", "Sunday", Graha.SURYA, "पश्चिम", "West")
            Calendar.MONDAY -> VaraInfo("सोमवार", "Monday", Graha.CHANDRA, "पूर्व", "East")
            Calendar.TUESDAY -> VaraInfo("मंगलवार", "Tuesday", Graha.MANGALA, "उत्तर", "North")
            Calendar.WEDNESDAY -> VaraInfo("बुधवार", "Wednesday", Graha.BUDHA, "उत्तर", "North")
            Calendar.THURSDAY -> VaraInfo("गुरुवार", "Thursday", Graha.GURU, "दक्षिण", "South")
            Calendar.FRIDAY -> VaraInfo("शुक्रवार", "Friday", Graha.SHUKRA, "पश्चिम", "West")
            else -> VaraInfo("शनिवार", "Saturday", Graha.SHANI, "पूर्व", "East")
        }

        val dayLen = sunsetHrs - sunriseHrs
        val octant = dayLen / 8.0
        val rahuSlot = when (dow) {
            Calendar.SUNDAY -> 7
            Calendar.MONDAY -> 1
            Calendar.TUESDAY -> 6
            Calendar.WEDNESDAY -> 4
            Calendar.THURSDAY -> 5
            Calendar.FRIDAY -> 3
            else -> 2
        }
        val rahuStart = sunriseHrs + rahuSlot * octant
        val rahuEnd = rahuStart + octant

        val abhijitStart = solarNoonLocal - 0.4
        val abhijitEnd = solarNoonLocal + 0.4
        val brahmaStart = sunriseHrs - 1.6
        val brahmaEnd = sunriseHrs - 0.8

        val moonRashi = Rashi.entries[(moonSid / 30.0).toInt().coerceIn(0, 11)]
        val sunRashi = Rashi.entries[(sunSid / 30.0).toInt().coerceIn(0, 11)]

        val noteHi = "${vara.hi} एवं ${nak.hi} नक्षत्र (${moonRashi.nameHi} चंद्र) का संयोग नए संकल्प, अध्ययन और आध्यात्मिक साधना के लिए शुभ है।"
        val noteEn = "Sunrise alignment of ${vara.en} with ${nak.en} Nakshatra (Moon in ${moonRashi.nameEn}) favors study and auspicious beginnings."

        return PanchangResult(
            year = year, month = month, day = day, place = place,
            varaHi = vara.hi, varaEn = vara.en, varaLord = vara.lord,
            pakshaHi = pakshaHi, pakshaEn = pakshaEn,
            tithiHi = tithiNamePair.first, tithiEn = tithiNamePair.second,
            tithiNumber = (tithiIdx % 15) + 1,
            tithiProgressPercent = tithiProgress,
            tithiEndTime = tithiEndStr,
            nakshatraHi = nak.hi, nakshatraEn = nak.en,
            nakshatraPada = nakPada, nakshatraLord = nak.lord,
            nakshatraEndTime = nakEndStr,
            yogaHi = yogaPair.first, yogaEn = yogaPair.second,
            yogaEndTime = yogaEndStr,
            karanaHi = karanaPair.first, karanaEn = karanaPair.second,
            sunrise = fmtTime(sunriseHrs),
            sunset = fmtTime(sunsetHrs),
            rahuKaal = "${fmtTime(rahuStart)} – ${fmtTime(rahuEnd)}",
            abhijitMuhurat = "${fmtTime(abhijitStart)} – ${fmtTime(abhijitEnd)}",
            brahmaMuhurat = "${fmtTime(brahmaStart)} – ${fmtTime(brahmaEnd)}",
            moonRashi = moonRashi, sunRashi = sunRashi,
            dishaShoolHi = vara.dishaHi, dishaShoolEn = vara.dishaEn,
            auspiciousNoteHi = noteHi, auspiciousNoteEn = noteEn
        )
    }

    /**
     * Generates Chandra Gochar (live lunar transit) Daily Rashifal for a given Rashi & date.
     */
    fun generateDailyRashifal(
        rashi: Rashi,
        year: Int,
        month: Int,
        day: Int,
        place: BirthPlace = CitiesRepository.defaultCity
    ): DailyRashifal {
        val jd = computeJulianDay(year, month, day, 12, 0, place.timezoneOffsetHours)
        val ayanamsha = computeLahiriAyanamsha(jd)
        val d = jd - 2451543.5
        val moonSid = norm360(moonTropicalLongitude(d) - ayanamsha)
        val moonTransitRashi = Rashi.entries[(moonSid / 30.0).toInt().coerceIn(0, 11)]
        val transitHouse = ((moonTransitRashi.number - rashi.number + 12) % 12) + 1

        // Classical Chandra Gochar house favorability: 1, 3, 6, 7, 10, 11 are auspicious
        val auspiciousHouses = setOf(1, 3, 6, 7, 10, 11)
        val rating = when (transitHouse) {
            11, 10, 1 -> 5
            3, 6, 7, 5, 9 -> 4
            else -> 3
        }

        val moonNameHi = moonTransitRashi.nameHi
        val moonNameEn = moonTransitRashi.nameEn

        val (summaryHi, summaryEn) = when (transitHouse) {
            1 -> "आज चंद्रमा आपकी ही $moonNameHi राशि (प्रथम भाव) में गोचर कर रहे हैं — आत्मविश्वास, ऊर्जा और व्यक्तित्व में निखार आएगा।" to
                "Moon transits your 1st house ($moonNameEn) today — boosting vitality, poise, and personal clarity."
            2 -> "चंद्रमा आपके द्वितीय भाव ($moonNameHi) में हैं — वाणी में मधुरता रखें, पारिवारिक व आर्थिक चर्चाएं महत्वपूर्ण रहेंगी।" to
                "Moon transits your 2nd house ($moonNameEn) — mindful speech brings financial & family harmony."
            3 -> "चंद्रमा आपके तृतीय भाव ($moonNameHi) में हैं — साहस, पराक्रम और छोटी यात्राओं से सफलता के प्रबल योग हैं।" to
                "Moon transits your 3rd house ($moonNameEn) — courage, initiative, and communication bring success."
            4 -> "चंद्रमा चतुर्थ भाव ($moonNameHi) में हैं — माता व गृह सुख पर ध्यान दें, मन को शांत रखना लाभकारी होगा।" to
                "Moon transits your 4th house ($moonNameEn) — focus on domestic peace, rest, and emotional grounding."
            5 -> "चंद्रमा पंचम भाव ($moonNameHi) में हैं — विद्या, बुद्धि, रचनात्मकता और संतान पक्ष से शुभ समाचार संभव है।" to
                "Moon transits your 5th house ($moonNameEn) — intellect, creativity, and planning flourish today."
            6 -> "चंद्रमा षष्ठ भाव ($moonNameHi) में हैं — विरोधियों पर विजय, ऋण-शमन और कार्यक्षमता में वृद्धि होगी।" to
                "Moon transits your 6th house ($moonNameEn) — discipline overcomes obstacles and sharpens productivity."
            7 -> "चंद्रमा सप्तम भाव ($moonNameHi) में हैं — साझेदारी, व्यापारिक संवाद और दांपत्य जीवन में मधुरता बढ़ेगी।" to
                "Moon transits your 7th house ($moonNameEn) — partnerships, diplomacy, and close bonds thrive."
            8 -> "चंद्रमा अष्टम भाव ($moonNameHi) में हैं — जल्दबाजी से बचें, शोध, ध्यान और धैर्य से दिन का संचालन करें।" to
                "Moon transits your 8th house ($moonNameEn) — avoid hasty risks; favor reflection and steady focus."
            9 -> "चंद्रमा नवम भाव ($moonNameHi) में हैं — धर्म, गुरु कृपा और भाग्य का सहयोग रुके कार्यों को गति देगा।" to
                "Moon transits your 9th house ($moonNameEn) — wisdom, mentors, and good fortune guide your steps."
            10 -> "चंद्रमा दशम भाव ($moonNameHi) में हैं — कर्मक्षेत्र में मान-सम्मान, पदोन्नति व नए उत्तरदायित्व का योग है।" to
                "Moon transits your 10th house ($moonNameEn) — career recognition, leadership, and momentum peak."
            11 -> "चंद्रमा एकादश भाव ($moonNameHi) में हैं — लाभ, आय वृद्धि और मित्रों के सहयोग का श्रेष्ठ दिन है।" to
                "Moon transits your 11th house ($moonNameEn) — gains, fulfilled goals, and supportive allies await."
            else -> "चंद्रमा द्वादश भाव ($moonNameHi) में हैं — अनावश्यक खर्च नियंत्रित रखें और विश्राम व साधना को समय दें।" to
                "Moon transits your 12th house ($moonNameEn) — manage expenses calmly and prioritize restful renewal."
        }

        val (careerHi, careerEn) = if (transitHouse in auspiciousHouses) {
            "गोचर के $transitHouse वें भाव में चंद्रमा कार्यक्षेत्र में निर्णय शक्ति और धन लाभ को बल दे रहे हैं।" to
                "Lunar transit in house $transitHouse strengthens career execution and financial flow today."
        } else {
            "कार्यस्थल पर धैर्य और स्पष्ट संवाद अपनाएं; बड़े निवेश आज टालकर योजना पर ध्यान दें।" to
                "Stay methodical at work and review details thoroughly before major commitments."
        }

        val (loveHi, loveEn) = when (transitHouse) {
            1, 5, 7, 11 -> "परिवार व जीवनसाथी के साथ आत्मीयता और सुखद संवाद बढ़ेगा।" to
                "Warmth, affection, and harmonious conversations enrich relationships."
            4, 9, 2 -> "अपनों की भावनाओं का सम्मान करें, घरेलू वातावरण सौहार्दपूर्ण रहेगा।" to
                "Sensitivity to loved ones fosters comfort and domestic tranquility."
            else -> "संवाद में विनम्रता रखें ताकि अनावश्यक गलतफहमी न उत्पन्न हो।" to
                "Keep discussions gentle and patient to maintain emotional ease."
        }

        val (healthHi, healthEn) = if (transitHouse in setOf(1, 3, 6, 10, 11)) {
            "शारीरिक स्फूर्ति उत्तम रहेगी; प्रातः प्राणायाम और संतुलित आहार दिन को ऊर्जावान बनाएगा।" to
                "Physical stamina is strong; morning breathwork and balanced meals sustain energy."
        } else {
            "पर्याप्त जल पिएं और मानसिक विश्राम के लिए शाम को हल्का टहलना लाभकारी रहेगा।" to
                "Stay hydrated and take quiet breaks to ease mental fatigue."
        }

        val remedyHi = "${rashi.lord.nameHi} देव के मंत्र '${rashi.lord.mantra}' का 11 बार जप करें तथा ${rashi.luckyColorHi} रंग धारण करें।"
        val remedyEn = "Chant '${rashi.lord.mantra}' 11 times for ${rashi.lord.nameEn} and wear ${rashi.luckyColorEn} accents today."

        return DailyRashifal(
            rashi = rashi,
            moonTransitRashi = moonTransitRashi,
            moonTransitHouse = transitHouse,
            rating = rating,
            summaryHi = summaryHi,
            summaryEn = summaryEn,
            careerHi = careerHi,
            careerEn = careerEn,
            loveHi = loveHi,
            loveEn = loveEn,
            healthHi = healthHi,
            healthEn = healthEn,
            remedyHi = remedyHi,
            remedyEn = remedyEn
        )
    }

    private fun planetFriendIndex(g: Graha): Int = when (g) {
        Graha.SURYA -> 0
        Graha.CHANDRA -> 1
        Graha.MANGALA -> 2
        Graha.BUDHA -> 3
        Graha.GURU -> 4
        Graha.SHUKRA -> 5
        Graha.SHANI -> 6
        else -> 6
    }

    fun matchKundalis(groom: BirthDetails, bride: BirthDetails): KundaliMatchResult {
        val gKundali = generateKundali(groom)
        val bKundali = generateKundali(bride)

        val gNak = nakshatras[gKundali.moonNakshatraIndex]
        val bNak = nakshatras[bKundali.moonNakshatraIndex]
        val gRashi = gKundali.moonRashi
        val bRashi = bKundali.moonRashi

        // 1. Varna (max 1)
        fun varnaRank(r: Rashi): Int = when (r.element) {
            Element.JAL -> 3
            Element.AGNI -> 2
            Element.PRITHVI -> 1
            Element.VAYU -> 0
        }
        val varnaScore = if (varnaRank(gRashi) >= varnaRank(bRashi)) 1.0 else 0.0

        // 2. Vashya (max 2)
        fun vashyaGroup(r: Rashi): Int = when (r.number) {
            1, 2, 10 -> 0 // Chatushpada
            3, 6, 7, 11 -> 1 // Dwipada
            4, 12 -> 2 // Jalachara
            5 -> 3 // Vanachara
            else -> 4 // Keeta (Scorpio)
        }
        val gV = vashyaGroup(gRashi)
        val bV = vashyaGroup(bRashi)
        val vashyaScore = when {
            gV == bV -> 2.0
            (gV == 1 && bV == 2) || (gV == 2 && bV == 1) -> 1.0
            (gV == 0 && bV == 1) || (gV == 1 && bV == 0) -> 1.0
            (gV == 3 || bV == 3) -> 0.5
            else -> 1.0
        }

        // 3. Tara (max 3)
        val gToB = ((bKundali.moonNakshatraIndex - gKundali.moonNakshatraIndex + 27) % 27) % 9
        val bToG = ((gKundali.moonNakshatraIndex - bKundali.moonNakshatraIndex + 27) % 27) % 9
        val badTaras = setOf(2, 4, 6) // 3rd, 5th, 7th Taras
        val taraScore = when {
            gToB !in badTaras && bToG !in badTaras -> 3.0
            gToB !in badTaras || bToG !in badTaras -> 1.5
            else -> 0.0
        }

        // 4. Yoni (max 4) — classical 14x14 matrix
        val yoniScore = yoniMatrix[gNak.yoniIdx][bNak.yoniIdx].toDouble()

        // 5. Graha Maitri (max 5) — classical 7x7 Naisargika Maitri table
        val gLord = gRashi.lord
        val bLord = bRashi.lord
        val gIdx = planetFriendIndex(gLord)
        val bIdx = planetFriendIndex(bLord)
        val relGB = planetFriendship[gIdx][bIdx] // 2=friend,1=neutral,0=enemy
        val relBG = planetFriendship[bIdx][gIdx]
        val maitriScore = when {
            gLord == bLord -> 5.0
            relGB == 2 && relBG == 2 -> 5.0
            (relGB == 2 && relBG == 1) || (relGB == 1 && relBG == 2) -> 4.0
            relGB == 1 && relBG == 1 -> 3.0
            (relGB == 2 && relBG == 0) || (relGB == 0 && relBG == 2) -> 1.0
            (relGB == 1 && relBG == 0) || (relGB == 0 && relBG == 1) -> 0.5
            else -> 0.0
        }

        // 6. Gana (max 6) — classical 3x3 matrix
        val ganaScore = ganaMatrix[gNak.ganaIdx][bNak.ganaIdx].toDouble()

        // 7. Bhakoot (max 7) — 2/12, 5/9, 6/8 are adverse unless Rashi lords are friends or identical
        val rashiDiff = ((bRashi.number - gRashi.number + 12) % 12) + 1
        val badBhakoot = setOf(2, 5, 6, 8, 9, 12)
        val lordsFriendly = gLord == bLord || (relGB == 2 && relBG == 2)
        val bhakootScore = if (rashiDiff !in badBhakoot || lordsFriendly) 7.0 else 0.0

        // 8. Nadi (max 8)
        val nadiScore = if (gNak.nadiIdx != bNak.nadiIdx) 8.0 else 0.0

        val kootas = listOf(
            KootaScore(
                "वर्ण", "Varna", "आध्यात्मिक एवं कार्य सामंजस्य", "Spiritual & Ego Compatibility",
                gKundali.avakahada.varnaHi, gKundali.avakahada.varnaEn,
                bKundali.avakahada.varnaHi, bKundali.avakahada.varnaEn, varnaScore, 1.0
            ),
            KootaScore(
                "वश्य", "Vashya", "पारस्परिक आकर्षण एवं स्नेह", "Mutual Attraction & Harmony",
                gKundali.avakahada.vashyaHi, gKundali.avakahada.vashyaEn,
                bKundali.avakahada.vashyaHi, bKundali.avakahada.vashyaEn, vashyaScore, 2.0
            ),
            KootaScore(
                "तारा", "Tara", "भाग्य एवं दीर्घायु", "Destiny & Longevity",
                gKundali.moonNakshatraHi, gKundali.moonNakshatraEn,
                bKundali.moonNakshatraHi, bKundali.moonNakshatraEn, taraScore, 3.0
            ),
            KootaScore(
                "योनि", "Yoni", "स्वभाव एवं मानसिक निकटता", "Temperament & Intimacy",
                gKundali.avakahada.yoniHi, gKundali.avakahada.yoniEn,
                bKundali.avakahada.yoniHi, bKundali.avakahada.yoniEn, yoniScore, 4.0
            ),
            KootaScore(
                "ग्रह मैत्री", "Graha Maitri", "मानसिक तालमेल एवं मित्रता", "Mental Affinity & Friendship",
                gLord.nameHi, gLord.nameEn,
                bLord.nameHi, bLord.nameEn, maitriScore, 5.0
            ),
            KootaScore(
                "गण", "Gana", "जीवनशैली एवं चरित्र साम्य", "Nature & Lifestyle Alignment",
                gKundali.avakahada.ganaHi, gKundali.avakahada.ganaEn,
                bKundali.avakahada.ganaHi, bKundali.avakahada.ganaEn, ganaScore, 6.0
            ),
            KootaScore(
                "भकूट", "Bhakoot", "परिवार समृद्धि एवं संतान सुख", "Family Prosperity & Growth",
                gRashi.nameHi, gRashi.nameEn,
                bRashi.nameHi, bRashi.nameEn, bhakootScore, 7.0
            ),
            KootaScore(
                "नाड़ी", "Nadi", "स्वास्थ्य एवं वंश वृद्धि", "Health & Progeny Vitality",
                gKundali.avakahada.nadiHi, gKundali.avakahada.nadiEn,
                bKundali.avakahada.nadiHi, bKundali.avakahada.nadiEn, nadiScore, 8.0
            )
        )

        data class VerdictInfo(val titleHi: String, val titleEn: String, val descHi: String, val descEn: String)
        val total = kootas.sumOf { it.obtained }
        val verdict = when {
            total >= 25.0 -> VerdictInfo(
                "उत्तम मिलान (श्रेष्ठ)", "Excellent Match",
                "यह कुंडली मिलान अत्यंत शुभ और समृद्ध वैवाहिक जीवन का संकेत देता है।",
                "This Ashtakoota alignment indicates a deeply harmonious and prosperous marriage."
            )
            total >= 18.0 -> VerdictInfo(
                "शुभ एवं मध्यम मिलान", "Auspicious Match",
                "18 से अधिक गुण मिलने के कारण विवाह के लिए यह मिलान अनुकूल माना जाता है।",
                "Scoring 18+ Gunas signifies a favorable and balanced partnership."
            )
            else -> VerdictInfo(
                "सामान्य मिलान (परामर्श योग्य)", "Moderate Match (Review)",
                "गुण मिलान 18 से कम है; मांगलिक व ग्रह स्थिति का सूक्ष्म विश्लेषण आवश्यक है।",
                "Score is below 18 Gunas; deeper planetary chart consultation is recommended."
            )
        }

        val (manglikHi, manglikEn) = when {
            gKundali.isManglik && bKundali.isManglik ->
                "दोनों कुंडलियां मांगलिक हैं — अतः मंगल दोष स्वतः संतुलित (रद्द) हो जाता है।" to
                    "Both charts are Manglik — Manglik Dosha cancels out harmoniously."
            !gKundali.isManglik && !bKundali.isManglik ->
                "दोनों कुंडलियां मांगलिक दोष से पूर्णतः मुक्त हैं।" to
                    "Both charts are completely free from Manglik Dosha."
            else ->
                "एक कुंडली मांगलिक है और दूसरी नहीं — विवाह पूर्व शांति उपाय परामर्श उचित है।" to
                    "One chart has Manglik Dosha while the other does not — Manglik cancellation check advised."
        }

        return KundaliMatchResult(
            groomKundali = gKundali,
            brideKundali = bKundali,
            kootas = kootas,
            totalScore = total,
            verdictTitleHi = verdict.titleHi,
            verdictTitleEn = verdict.titleEn,
            verdictDescHi = verdict.descHi,
            verdictDescEn = verdict.descEn,
            manglikCompatHi = manglikHi,
            manglikCompatEn = manglikEn
        )
    }
}
