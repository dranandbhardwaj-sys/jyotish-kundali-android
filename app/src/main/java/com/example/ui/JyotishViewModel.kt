package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppConfigPreferences
import com.example.data.LanguagePreferences
import com.example.data.payment.PaymentCheckoutResult
import com.example.data.payment.PaymentOrderRequest
import com.example.data.payment.UnconfiguredPaymentGateway
import com.example.data.remote.GeocodingService
import com.example.domain.CitiesRepository
import com.example.domain.JyotishEngine
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

data class JyotishUiState(
    val language: AppLanguage = AppLanguage.HI,
    val selectedRashi: Rashi = Rashi.MESHA,
    val dailyRashifalList: List<DailyRashifal> = emptyList(),
    val panchangYear: Int = 2026,
    val panchangMonth: Int = 11,
    val panchangDay: Int = 20,
    val panchangPlace: BirthPlace = CitiesRepository.defaultCity,
    val panchangResult: PanchangResult? = null,
    // Kundali Generation State (no hardcoded demo user data)
    val kundaliInput: BirthDetails = defaultEmptyBirthDetails(Gender.MALE, CitiesRepository.defaultCity),
    val kundaliResult: KundaliResult? = null,
    val kundaliValidationError: String? = null,
    val chartStyle: ChartStyle = ChartStyle.NORTH_INDIAN,
    val chartDivision: ChartDivision = ChartDivision.D1,
    // Kundali Matching State (no hardcoded demo couple)
    val groomInput: BirthDetails = defaultEmptyBirthDetails(Gender.MALE, CitiesRepository.delhi),
    val brideInput: BirthDetails = defaultEmptyBirthDetails(Gender.FEMALE, CitiesRepository.defaultCity),
    val matchResult: KundaliMatchResult? = null,
    val matchValidationError: String? = null,
    // Live Birthplace Search State
    val offlinePlaceResults: List<BirthPlace> = CitiesRepository.cities,
    val onlinePlaceResults: List<BirthPlace> = emptyList(),
    val isSearchingPlaces: Boolean = false,
    val placeSearchError: String? = null,
    // Selected Graha & Methodology Info Dialog
    val inspectedGraha: Graha? = null,
    val inspectedGrahaPosition: GrahaPosition? = null,
    val showPrecisionDialog: Boolean = false,
    // Owner Profile, Premium Membership, Demo Chat & Admin Settings
    val ownerProfile: OwnerProfileConfig = OwnerProfileConfig(),
    val membership: MembershipConfig = MembershipConfig(),
    val selectedBillingCycle: BillingCycle = BillingCycle.YEARLY,
    val paymentGatewayNotice: String? = null,
    val demoChatMessages: List<DemoChatMessage> = emptyList(),
    val adminSaveNotice: String? = null
)

private fun defaultEmptyBirthDetails(gender: Gender, place: BirthPlace): BirthDetails {
    val cal = Calendar.getInstance()
    return BirthDetails(
        name = "",
        gender = gender,
        year = cal.get(Calendar.YEAR),
        month = cal.get(Calendar.MONTH) + 1,
        day = cal.get(Calendar.DAY_OF_MONTH),
        hour24 = cal.get(Calendar.HOUR_OF_DAY),
        minute = cal.get(Calendar.MINUTE),
        place = place
    )
}

class JyotishViewModel(application: Application) : AndroidViewModel(application) {
    private val languagePreferences = LanguagePreferences(application)
    private val appConfigPreferences = AppConfigPreferences(application)

    private val _uiState = MutableStateFlow(JyotishUiState())
    val uiState: StateFlow<JyotishUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        val cal = Calendar.getInstance()
        val todayY = cal.get(Calendar.YEAR)
        val todayM = cal.get(Calendar.MONTH) + 1
        val todayD = cal.get(Calendar.DAY_OF_MONTH)

        val initialPanchang = JyotishEngine.computePanchang(todayY, todayM, todayD, CitiesRepository.defaultCity)
        val initialRashifals = Rashi.entries.map { r ->
            JyotishEngine.generateDailyRashifal(r, todayY, todayM, todayD, CitiesRepository.defaultCity)
        }

        _uiState.update {
            it.copy(
                panchangYear = todayY,
                panchangMonth = todayM,
                panchangDay = todayD,
                panchangResult = initialPanchang,
                dailyRashifalList = initialRashifals
            )
        }

        viewModelScope.launch {
            languagePreferences.languageFlow.collect { lang ->
                _uiState.update { it.copy(language = lang) }
            }
        }

        viewModelScope.launch {
            appConfigPreferences.configFlow.collect { cfg ->
                _uiState.update {
                    it.copy(
                        ownerProfile = cfg.owner,
                        membership = cfg.membership
                    )
                }
            }
        }
    }

    fun toggleLanguage() {
        val next = if (_uiState.value.language == AppLanguage.HI) AppLanguage.EN else AppLanguage.HI
        _uiState.update {
            it.copy(
                language = next,
                kundaliValidationError = null,
                matchValidationError = null,
                paymentGatewayNotice = null,
                adminSaveNotice = null
            )
        }
        viewModelScope.launch {
            languagePreferences.setLanguage(next)
        }
    }

    fun setLanguage(lang: AppLanguage) {
        _uiState.update { it.copy(language = lang) }
        viewModelScope.launch {
            languagePreferences.setLanguage(lang)
        }
    }

    fun selectRashi(rashi: Rashi) {
        _uiState.update { it.copy(selectedRashi = rashi) }
    }

    fun updatePanchangDate(year: Int, month: Int, day: Int) {
        val place = _uiState.value.panchangPlace
        val result = JyotishEngine.computePanchang(year, month, day, place)
        val rashifals = Rashi.entries.map { JyotishEngine.generateDailyRashifal(it, year, month, day, place) }
        _uiState.update {
            it.copy(
                panchangYear = year,
                panchangMonth = month,
                panchangDay = day,
                panchangResult = result,
                dailyRashifalList = rashifals
            )
        }
    }

    fun shiftPanchangDay(deltaDays: Int) {
        val cal = Calendar.getInstance().apply {
            set(_uiState.value.panchangYear, _uiState.value.panchangMonth - 1, _uiState.value.panchangDay)
            add(Calendar.DAY_OF_MONTH, deltaDays)
        }
        updatePanchangDate(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    fun updatePanchangPlace(place: BirthPlace) {
        val s = _uiState.value
        val result = JyotishEngine.computePanchang(s.panchangYear, s.panchangMonth, s.panchangDay, place)
        val rashifals = Rashi.entries.map {
            JyotishEngine.generateDailyRashifal(it, s.panchangYear, s.panchangMonth, s.panchangDay, place)
        }
        _uiState.update {
            it.copy(
                panchangPlace = place,
                panchangResult = result,
                dailyRashifalList = rashifals
            )
        }
    }

    fun updateKundaliInput(updated: BirthDetails) {
        _uiState.update { it.copy(kundaliInput = updated, kundaliValidationError = null) }
    }

    fun generateKundali(): Boolean {
        val input = _uiState.value.kundaliInput
        val lang = _uiState.value.language
        if (input.name.trim().isEmpty()) {
            _uiState.update {
                it.copy(
                    kundaliValidationError = lang.t(
                        "कृपया जातक का नाम दर्ज करें।",
                        "Please enter the person's full name."
                    )
                )
            }
            return false
        }
        if (input.place.latitude !in -90.0..90.0 || input.place.longitude !in -180.0..180.0) {
            _uiState.update {
                it.copy(
                    kundaliValidationError = lang.t(
                        "अमान्य अक्षांश या देशांतर निर्देशांक।",
                        "Invalid latitude or longitude coordinates."
                    )
                )
            }
            return false
        }
        val result = JyotishEngine.generateKundali(input.copy(name = input.name.trim()))
        _uiState.update { it.copy(kundaliResult = result, kundaliValidationError = null) }
        return true
    }

    fun resetKundali() {
        _uiState.update {
            it.copy(
                kundaliInput = defaultEmptyBirthDetails(Gender.MALE, CitiesRepository.defaultCity),
                kundaliResult = null,
                kundaliValidationError = null
            )
        }
    }

    fun setChartStyle(style: ChartStyle) {
        _uiState.update { it.copy(chartStyle = style) }
    }

    fun setChartDivision(division: ChartDivision) {
        _uiState.update { it.copy(chartDivision = division) }
    }

    fun updateGroomInput(updated: BirthDetails) {
        _uiState.update { it.copy(groomInput = updated, matchValidationError = null) }
    }

    fun updateBrideInput(updated: BirthDetails) {
        _uiState.update { it.copy(brideInput = updated, matchValidationError = null) }
    }

    fun calculateMatch(): Boolean {
        val s = _uiState.value
        val lang = s.language
        if (s.groomInput.name.trim().isEmpty() || s.brideInput.name.trim().isEmpty()) {
            _uiState.update {
                it.copy(
                    matchValidationError = lang.t(
                        "कृपया वर और वधू दोनों के नाम दर्ज करें।",
                        "Please enter both Groom's and Bride's names."
                    )
                )
            }
            return false
        }
        val result = JyotishEngine.matchKundalis(
            s.groomInput.copy(name = s.groomInput.name.trim()),
            s.brideInput.copy(name = s.brideInput.name.trim())
        )
        _uiState.update { it.copy(matchResult = result, matchValidationError = null) }
        return true
    }

    fun resetMatching() {
        _uiState.update {
            it.copy(
                groomInput = defaultEmptyBirthDetails(Gender.MALE, CitiesRepository.delhi),
                brideInput = defaultEmptyBirthDetails(Gender.FEMALE, CitiesRepository.defaultCity),
                matchResult = null,
                matchValidationError = null
            )
        }
    }

    fun searchPlaces(query: String) {
        val offline = CitiesRepository.searchOffline(query)
        _uiState.update {
            it.copy(
                offlinePlaceResults = offline,
                placeSearchError = null
            )
        }
        searchJob?.cancel()
        if (query.trim().length < 2) {
            _uiState.update { it.copy(onlinePlaceResults = emptyList(), isSearchingPlaces = false) }
            return
        }
        searchJob = viewModelScope.launch {
            delay(320)
            _uiState.update { it.copy(isSearchingPlaces = true, placeSearchError = null) }
            when (val res = GeocodingService.searchCityOnline(query)) {
                is GeocodingService.SearchResult.Success -> {
                    _uiState.update {
                        it.copy(
                            onlinePlaceResults = res.places,
                            isSearchingPlaces = false,
                            placeSearchError = null
                        )
                    }
                }
                is GeocodingService.SearchResult.Error -> {
                    _uiState.update {
                        it.copy(
                            onlinePlaceResults = emptyList(),
                            isSearchingPlaces = false,
                            placeSearchError = res.message
                        )
                    }
                }
            }
        }
    }

    fun inspectGraha(graha: Graha, position: GrahaPosition? = null) {
        _uiState.update { it.copy(inspectedGraha = graha, inspectedGrahaPosition = position) }
    }

    fun dismissGrahaInspector() {
        _uiState.update { it.copy(inspectedGraha = null, inspectedGrahaPosition = null) }
    }

    fun setPrecisionDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(showPrecisionDialog = visible) }
    }

    // --- Owner Profile, Membership, Demo Chat & Admin Settings ---

    fun updateOwnerPhotoUri(uriString: String) {
        val updated = _uiState.value.ownerProfile.copy(photoUri = uriString)
        _uiState.update { it.copy(ownerProfile = updated) }
        viewModelScope.launch {
            appConfigPreferences.updatePhotoUri(uriString)
        }
    }

    fun selectBillingCycle(cycle: BillingCycle) {
        _uiState.update { it.copy(selectedBillingCycle = cycle, paymentGatewayNotice = null) }
    }

    fun setSubscriptionStatus(status: SubscriptionStatus) {
        val updated = _uiState.value.membership.copy(status = status)
        _uiState.update { it.copy(membership = updated, paymentGatewayNotice = null) }
        viewModelScope.launch {
            appConfigPreferences.updateSubscriptionStatus(status)
        }
    }

    fun attemptPremiumCheckout() {
        val s = _uiState.value
        val lang = s.language
        val amount = when (s.selectedBillingCycle) {
            BillingCycle.MONTHLY -> s.membership.monthlyPriceInr
            BillingCycle.YEARLY -> s.membership.yearlyPriceInr
            BillingCycle.LIFETIME -> s.membership.lifetimePriceInr
        }
        viewModelScope.launch {
            val res = UnconfiguredPaymentGateway.initiateCheckout(
                PaymentOrderRequest(
                    billingCycle = s.selectedBillingCycle,
                    amountInr = amount,
                    customerEmail = s.ownerProfile.email
                )
            )
            val msg = when (res) {
                is PaymentCheckoutResult.NotConfigured -> lang.t(
                    "भुगतान गेटवे (${res.provider.displayName}) अभी लाइव कनेक्ट नहीं है। कोई वास्तविक शुल्क नहीं लिया गया। प्रीमियम लॉक/अनलॉक का परीक्षण करने के लिए नीचे 'डेमो स्थिति नियंत्रण' या 'एडमिन सेटिंग्स' का उपयोग करें।",
                    "Live payment gateway (${res.provider.displayName}) is not connected yet. No real charge was made. Use the Demo Status Control below or Admin Settings to preview Premium unlock."
                )
                is PaymentCheckoutResult.Success -> lang.t("भुगतान सफल!", "Payment successful!")
                is PaymentCheckoutResult.Cancelled -> res.message
                is PaymentCheckoutResult.Failed -> res.error
            }
            _uiState.update { it.copy(paymentGatewayNotice = msg) }
        }
    }

    fun submitDemoChatQuestion(text: String, topic: WhatsAppTopic) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return
        val cal = Calendar.getInstance()
        val ts = String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
        val lang = _uiState.value.language

        val userMsg = DemoChatMessage(
            id = "u_${System.currentTimeMillis()}",
            senderIsUser = true,
            text = trimmed,
            timestampLabel = ts,
            topic = topic
        )
        val systemNotice = DemoChatMessage(
            id = "s_${System.currentTimeMillis() + 1}",
            senderIsUser = false,
            text = lang.t(
                "[डेमो सूचना] आपका प्रश्न स्थानीय सत्र में सहेजा गया है। रियल-टाइम चैट सर्वर जुड़ा न होने के कारण यह संदेश किसी एजेंट को भेजा नहीं गया है — कृपया तुरंत उत्तर के लिए नीचे 'व्हाट्सएप पर भेजें' बटन का उपयोग करें।",
                "[Demo Notice] Your question was saved locally in this demo session. Because no real-time messaging backend is connected, it was NOT delivered to a live agent — tap 'Send via WhatsApp' below for real consultation."
            ),
            timestampLabel = ts,
            topic = topic
        )
        _uiState.update {
            it.copy(demoChatMessages = it.demoChatMessages + userMsg + systemNotice)
        }
    }

    fun clearDemoChat() {
        _uiState.update { it.copy(demoChatMessages = emptyList()) }
    }

    fun saveAdminSettings(owner: OwnerProfileConfig, membership: MembershipConfig) {
        val lang = _uiState.value.language
        _uiState.update {
            it.copy(
                ownerProfile = owner,
                membership = membership,
                adminSaveNotice = lang.t(
                    "एडमिन सेटिंग्स सफलतापूर्वक सहेजी गईं।",
                    "Admin settings saved successfully."
                )
            )
        }
        viewModelScope.launch {
            appConfigPreferences.saveConfig(owner, membership)
        }
    }

    fun resetAdminDefaults() {
        val defOwner = OwnerProfileConfig()
        val defMem = MembershipConfig()
        val lang = _uiState.value.language
        _uiState.update {
            it.copy(
                ownerProfile = defOwner,
                membership = defMem,
                adminSaveNotice = lang.t(
                    "सभी एडमिन सेटिंग्स डिफ़ॉल्ट पर रीसेट की गईं।",
                    "Admin settings reset to defaults."
                )
            )
        }
        viewModelScope.launch {
            appConfigPreferences.saveConfig(defOwner, defMem)
        }
    }

    fun clearAdminNotice() {
        _uiState.update { it.copy(adminSaveNotice = null) }
    }
}
