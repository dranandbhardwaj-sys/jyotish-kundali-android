package com.example.data.payment

import com.example.model.BillingCycle

/**
 * Payment Integration Architecture Contract for Premium Membership Upgrades.
 *
 * NOTE: Live payment processing (Google Play Billing Library / Razorpay / Stripe)
 * is NOT connected yet. Do not assume or simulate real monetary charges.
 * Attach a concrete [PaymentGatewayClient] implementation once merchant credentials
 * and server-side receipt verification are configured.
 */
enum class PaymentProvider(val displayName: String) {
    GOOGLE_PLAY_BILLING("Google Play Billing"),
    RAZORPAY("Razorpay Checkout"),
    STRIPE("Stripe Payments")
}

data class PaymentOrderRequest(
    val billingCycle: BillingCycle,
    val amountInr: Int,
    val currency: String = "INR",
    val customerEmail: String = ""
)

sealed class PaymentCheckoutResult {
    data class NotConfigured(val provider: PaymentProvider, val reason: String) : PaymentCheckoutResult()
    data class Success(val transactionId: String, val billingCycle: BillingCycle) : PaymentCheckoutResult()
    data class Cancelled(val message: String) : PaymentCheckoutResult()
    data class Failed(val error: String) : PaymentCheckoutResult()
}

interface PaymentGatewayClient {
    val provider: PaymentProvider
    val isLiveGatewayConnected: Boolean
    suspend fun initiateCheckout(request: PaymentOrderRequest): PaymentCheckoutResult
}

object UnconfiguredPaymentGateway : PaymentGatewayClient {
    override val provider: PaymentProvider = PaymentProvider.RAZORPAY
    override val isLiveGatewayConnected: Boolean = false

    override suspend fun initiateCheckout(request: PaymentOrderRequest): PaymentCheckoutResult {
        return PaymentCheckoutResult.NotConfigured(
            provider = provider,
            reason = "Live payment gateway (${provider.displayName}) is not connected yet. Configure merchant keys & backend verification before enabling live charges."
        )
    }
}
