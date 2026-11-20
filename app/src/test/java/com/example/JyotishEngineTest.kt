package com.example

import com.example.domain.CitiesRepository
import com.example.domain.JyotishEngine
import com.example.model.BirthDetails
import com.example.model.BirthPlace
import com.example.model.Gender
import com.example.model.Rashi
import org.junit.Assert.*
import org.junit.Test

class JyotishEngineTest {

    @Test
    fun `kundali generation produces 9 grahas valid houses navamsha and antardashas`() {
        val details = BirthDetails(
            name = "Test Native",
            gender = Gender.MALE,
            year = 1995,
            month = 11,
            day = 14,
            hour24 = 6,
            minute = 15,
            place = CitiesRepository.defaultCity
        )
        val result = JyotishEngine.generateKundali(details)

        assertEquals(9, result.grahaPositions.size)
        assertTrue(result.ayanamsha in 23.5..24.5)
        assertTrue(result.lagnaDegree in 0.0..30.0)
        assertTrue(result.grahaPositions.all { it.house in 1..12 && it.navamshaHouse in 1..12 })
        assertEquals(9, result.dashas.size)
        assertTrue(result.dashas.all { it.antardashas.isNotEmpty() })
        // Sum of Mahadasha durations approximates 120 years minus already elapsed birth nakshatra fraction
        val totalSpan = result.dashas.sumOf { it.durationYears }
        assertTrue(totalSpan in 100.0..120.0)
    }

    @Test
    fun `kundali lagna depends on birth time and location coordinates`() {
        val morning = BirthDetails("A", Gender.MALE, 2000, 3, 21, 6, 0, CitiesRepository.delhi)
        val evening = BirthDetails("A", Gender.MALE, 2000, 3, 21, 18, 0, CitiesRepository.delhi)
        val london = BirthDetails("A", Gender.MALE, 2000, 3, 21, 6, 0,
            BirthPlace("london", "London", "London", "UK", "UK", 51.5072, -0.1276, 0.0)
        )

        val kMorning = JyotishEngine.generateKundali(morning)
        val kEvening = JyotishEngine.generateKundali(evening)
        val kLondon = JyotishEngine.generateKundali(london)

        assertNotEquals(kMorning.lagnaRashi, kEvening.lagnaRashi)
        // Different longitudes/timezones yield different Ascendant degree
        assertNotEquals(kMorning.lagnaDegree, kLondon.lagnaDegree, 0.01)
    }

    @Test
    fun `panchang calculations are location and date dependent`() {
        val delhiToday = JyotishEngine.computePanchang(2026, 11, 20, CitiesRepository.delhi)
        val delhiNextWeek = JyotishEngine.computePanchang(2026, 11, 27, CitiesRepository.delhi)
        val guwahatiToday = JyotishEngine.computePanchang(
            2026, 11, 20,
            CitiesRepository.cities.first { it.id == "guwahati" }
        )

        assertTrue(delhiToday.tithiHi.isNotBlank() && delhiToday.tithiEndTime.contains(":"))
        assertTrue(delhiToday.nakshatraEndTime.contains(":"))
        assertNotEquals(delhiToday.tithiHi, delhiNextWeek.tithiHi)
        // Guwahati is ~14.5 deg east of Delhi, so sunrise is noticeably earlier
        assertNotEquals(delhiToday.sunrise, guwahatiToday.sunrise)
    }

    @Test
    fun `daily rashifal covers all 12 zodiac signs using live moon transit`() {
        val list = Rashi.entries.map { JyotishEngine.generateDailyRashifal(it, 2026, 11, 20) }
        assertEquals(12, list.size)
        // All 12 Rashis see the same Moon transit Rashi on that date, in 12 distinct transit houses (1..12)
        assertEquals(1, list.map { it.moonTransitRashi }.distinct().size)
        assertEquals((1..12).toSet(), list.map { it.moonTransitHouse }.toSet())
    }

    @Test
    fun `kundali matching computes 8 kootas within 36 guna bounds and responds to inputs`() {
        val groom = BirthDetails("G1", Gender.MALE, 1994, 4, 12, 8, 10, CitiesRepository.delhi)
        val bride1 = BirthDetails("B1", Gender.FEMALE, 1996, 9, 5, 14, 30, CitiesRepository.defaultCity)
        val bride2 = BirthDetails("B2", Gender.FEMALE, 1997, 1, 22, 21, 45, CitiesRepository.cities[2])

        val match1 = JyotishEngine.matchKundalis(groom, bride1)
        val match2 = JyotishEngine.matchKundalis(groom, bride2)

        assertEquals(8, match1.kootas.size)
        assertEquals(36.0, match1.kootas.sumOf { it.maximum }, 0.001)
        assertTrue(match1.totalScore in 0.0..36.0)
        assertNotEquals(match1.brideKundali.moonNakshatraEn, match2.brideKundali.moonNakshatraEn)
    }
}
