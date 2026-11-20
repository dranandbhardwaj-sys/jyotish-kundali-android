package com.example.data.remote

/**
 * Optional external Vedic Astrology / Swiss Ephemeris Backend Integration Contract.
 *
 * By default, Jyotish Kundali runs a 100% deterministic on-device astronomical calculation
 * engine (Julian Day, Lahiri Chitrapaksha Ayanamsha, truncated VSOP87/Meeus planetary longitudes,
 * Lagna, Navamsha D-9, Panchang at local Sunrise, Vimshottari Mahadasha/Antardasha, and
 * classical Ashtakoota Guna Milan) with live Open-Meteo Geocoding for worldwide birthplaces.
 *
 * If sub-arcsecond JPL DE431 / Swiss Ephemeris precision or historical DST database lookup
 * is required from a dedicated server (e.g., Prokerala, AstrologyAPI, or custom Swiss Ephemeris
 * microservice), implement [EphemerisBackendClient] and configure the endpoint/API key via Secrets.
 */
data class EphemerisRequest(
    val isoDatetime: String,
    val latitude: Double,
    val longitude: Double,
    val timezoneOffsetHours: Double,
    val ayanamsha: String = "LAHIRI"
)

data class EphemerisGrahaDto(
    val name: String,
    val siderealLongitude: Double,
    val speedDegPerDay: Double,
    val isRetrograde: Boolean
)

data class EphemerisResponse(
    val ascendantLongitude: Double,
    val ayanamshaValue: Double,
    val grahas: List<EphemerisGrahaDto>
)

interface EphemerisBackendClient {
    val providerName: String
    val isConfigured: Boolean
    suspend fun fetchSwissEphemeris(request: EphemerisRequest): Result<EphemerisResponse>
}

object OnDeviceEphemerisBackend : EphemerisBackendClient {
    override val providerName: String = "On-Device Lahiri Astronomical Engine + Open-Meteo Geocoding"
    override val isConfigured: Boolean = true

    override suspend fun fetchSwissEphemeris(request: EphemerisRequest): Result<EphemerisResponse> {
        return Result.failure(
            UnsupportedOperationException(
                "External Swiss Ephemeris backend is not attached; app uses on-device astronomical engine."
            )
        )
    }
}
