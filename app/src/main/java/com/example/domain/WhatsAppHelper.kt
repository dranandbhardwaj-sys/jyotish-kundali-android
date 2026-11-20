package com.example.domain

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.net.URLEncoder

object WhatsAppHelper {
    fun sanitizePhoneNumber(raw: String): String {
        return raw.filter { it.isDigit() }
    }

    fun buildWhatsAppUrl(phoneWithCountryCode: String, message: String): String {
        val digits = sanitizePhoneNumber(phoneWithCountryCode)
        val encoded = URLEncoder.encode(message, "UTF-8").replace("+", "%20")
        return "https://wa.me/$digits?text=$encoded"
    }

    fun openWhatsApp(context: Context, phoneWithCountryCode: String, message: String): Boolean {
        val url = buildWhatsAppUrl(phoneWithCountryCode, message)
        return try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            false
        }
    }
}
