package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.MembershipConfig
import com.example.model.OwnerProfileConfig
import com.example.model.SubscriptionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.configDataStore: DataStore<Preferences> by preferencesDataStore(name = "jyotish_admin_config")

data class PersistedAppConfig(
    val owner: OwnerProfileConfig = OwnerProfileConfig(),
    val membership: MembershipConfig = MembershipConfig()
)

class AppConfigPreferences(private val context: Context) {
    private object Keys {
        val NAME_HI = stringPreferencesKey("owner_name_hi")
        val NAME_EN = stringPreferencesKey("owner_name_en")
        val TITLE_HI = stringPreferencesKey("owner_title_hi")
        val TITLE_EN = stringPreferencesKey("owner_title_en")
        val EXP_HI = stringPreferencesKey("owner_exp_hi")
        val EXP_EN = stringPreferencesKey("owner_exp_en")
        val PHOTO_URI = stringPreferencesKey("owner_photo_uri")
        val INTRO_HI = stringPreferencesKey("owner_intro_hi")
        val INTRO_EN = stringPreferencesKey("owner_intro_en")
        val ABOUT_HI = stringPreferencesKey("owner_about_hi")
        val ABOUT_EN = stringPreferencesKey("owner_about_en")
        val PHONE = stringPreferencesKey("owner_phone")
        val EMAIL = stringPreferencesKey("owner_email")
        val WHATSAPP = stringPreferencesKey("owner_whatsapp")
        val ADDRESS_HI = stringPreferencesKey("owner_address_hi")
        val ADDRESS_EN = stringPreferencesKey("owner_address_en")
        val HOURS_HI = stringPreferencesKey("owner_hours_hi")
        val HOURS_EN = stringPreferencesKey("owner_hours_en")
        val WEBSITE_NAME = stringPreferencesKey("website_name")
        val WEBSITE_URL = stringPreferencesKey("website_url")

        val SUB_STATUS = stringPreferencesKey("sub_status")
        val PRICE_MONTHLY = intPreferencesKey("price_monthly")
        val PRICE_YEARLY = intPreferencesKey("price_yearly")
        val PRICE_LIFETIME = intPreferencesKey("price_lifetime")
    }

    val configFlow: Flow<PersistedAppConfig> = context.configDataStore.data.map { p ->
        val defOwner = OwnerProfileConfig()
        val defMem = MembershipConfig()
        val status = runCatching {
            SubscriptionStatus.valueOf(p[Keys.SUB_STATUS] ?: defMem.status.name)
        }.getOrDefault(defMem.status)

        PersistedAppConfig(
            owner = OwnerProfileConfig(
                nameHi = p[Keys.NAME_HI] ?: defOwner.nameHi,
                nameEn = p[Keys.NAME_EN] ?: defOwner.nameEn,
                titleHi = p[Keys.TITLE_HI] ?: defOwner.titleHi,
                titleEn = p[Keys.TITLE_EN] ?: defOwner.titleEn,
                experienceHi = p[Keys.EXP_HI] ?: defOwner.experienceHi,
                experienceEn = p[Keys.EXP_EN] ?: defOwner.experienceEn,
                photoUri = p[Keys.PHOTO_URI] ?: defOwner.photoUri,
                introHi = p[Keys.INTRO_HI] ?: defOwner.introHi,
                introEn = p[Keys.INTRO_EN] ?: defOwner.introEn,
                aboutHi = p[Keys.ABOUT_HI] ?: defOwner.aboutHi,
                aboutEn = p[Keys.ABOUT_EN] ?: defOwner.aboutEn,
                phone = p[Keys.PHONE] ?: defOwner.phone,
                email = p[Keys.EMAIL] ?: defOwner.email,
                whatsappNumber = p[Keys.WHATSAPP] ?: defOwner.whatsappNumber,
                addressHi = p[Keys.ADDRESS_HI] ?: defOwner.addressHi,
                addressEn = p[Keys.ADDRESS_EN] ?: defOwner.addressEn,
                supportHoursHi = p[Keys.HOURS_HI] ?: defOwner.supportHoursHi,
                supportHoursEn = p[Keys.HOURS_EN] ?: defOwner.supportHoursEn,
                websiteName = p[Keys.WEBSITE_NAME] ?: defOwner.websiteName,
                websiteUrl = p[Keys.WEBSITE_URL] ?: defOwner.websiteUrl
            ),
            membership = MembershipConfig(
                status = status,
                monthlyPriceInr = p[Keys.PRICE_MONTHLY] ?: defMem.monthlyPriceInr,
                yearlyPriceInr = p[Keys.PRICE_YEARLY] ?: defMem.yearlyPriceInr,
                lifetimePriceInr = p[Keys.PRICE_LIFETIME] ?: defMem.lifetimePriceInr
            )
        )
    }

    suspend fun saveConfig(owner: OwnerProfileConfig, membership: MembershipConfig) {
        context.configDataStore.edit { p ->
            p[Keys.NAME_HI] = owner.nameHi
            p[Keys.NAME_EN] = owner.nameEn
            p[Keys.TITLE_HI] = owner.titleHi
            p[Keys.TITLE_EN] = owner.titleEn
            p[Keys.EXP_HI] = owner.experienceHi
            p[Keys.EXP_EN] = owner.experienceEn
            p[Keys.PHOTO_URI] = owner.photoUri
            p[Keys.INTRO_HI] = owner.introHi
            p[Keys.INTRO_EN] = owner.introEn
            p[Keys.ABOUT_HI] = owner.aboutHi
            p[Keys.ABOUT_EN] = owner.aboutEn
            p[Keys.PHONE] = owner.phone
            p[Keys.EMAIL] = owner.email
            p[Keys.WHATSAPP] = owner.whatsappNumber
            p[Keys.ADDRESS_HI] = owner.addressHi
            p[Keys.ADDRESS_EN] = owner.addressEn
            p[Keys.HOURS_HI] = owner.supportHoursHi
            p[Keys.HOURS_EN] = owner.supportHoursEn
            p[Keys.WEBSITE_NAME] = owner.websiteName
            p[Keys.WEBSITE_URL] = owner.websiteUrl

            p[Keys.SUB_STATUS] = membership.status.name
            p[Keys.PRICE_MONTHLY] = membership.monthlyPriceInr
            p[Keys.PRICE_YEARLY] = membership.yearlyPriceInr
            p[Keys.PRICE_LIFETIME] = membership.lifetimePriceInr
        }
    }

    suspend fun updateSubscriptionStatus(status: SubscriptionStatus) {
        context.configDataStore.edit { p ->
            p[Keys.SUB_STATUS] = status.name
        }
    }

    suspend fun updatePhotoUri(uri: String) {
        context.configDataStore.edit { p ->
            p[Keys.PHOTO_URI] = uri
        }
    }
}
