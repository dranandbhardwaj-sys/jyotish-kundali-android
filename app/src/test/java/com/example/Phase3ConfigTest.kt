package com.example

import com.example.data.payment.PaymentCheckoutResult
import com.example.data.payment.PaymentOrderRequest
import com.example.data.payment.UnconfiguredPaymentGateway
import com.example.domain.WhatsAppHelper
import com.example.model.BillingCycle
import com.example.model.OwnerProfileConfig
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Phase3ConfigTest {

    @Test
    fun `sanitizePhoneNumber strips non-digits and buildWhatsAppUrl encodes message`() {
        assertEquals("919876543210", WhatsAppHelper.sanitizePhoneNumber("+91 98765-43210"))
        val url = WhatsAppHelper.buildWhatsAppUrl("+91 9876543210", "Namaste Acharya Ji")
        assertTrue(url.startsWith("https://wa.me/919876543210?text="))
        assertTrue(url.contains("Namaste%20Acharya%20Ji"))
    }

    @Test
    fun `owner profile detects placeholder whatsapp number accurately`() {
        val defaultOwner = OwnerProfileConfig()
        assertTrue(defaultOwner.isWhatsappPlaceholder())

        val configuredOwner = defaultOwner.copy(whatsappNumber = "+919811223344")
        assertFalse(configuredOwner.isWhatsappPlaceholder())
    }

    @Test
    fun `unconfigured payment gateway returns NotConfigured honestly`() = runBlocking {
        val result = UnconfiguredPaymentGateway.initiateCheckout(
            PaymentOrderRequest(
                billingCycle = BillingCycle.YEARLY,
                amountInr = 1999,
                customerEmail = "test@example.com"
            )
        )
        assertTrue(result is PaymentCheckoutResult.NotConfigured)
    }
}
