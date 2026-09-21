package com.example.util

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import java.util.EnumMap

object QrCodeGenerator {

    /**
     * Builds standard UPI payment URI string:
     * upi://pay?pa=merchant@upi&pn=Merchant%20Name&am=1999.00&cu=INR&tn=Part%201%20of%203
     */
    fun buildUpiUri(
        upiVpa: String,
        merchantName: String,
        amount: Double,
        note: String
    ): String {
        val cleanVpa = upiVpa.trim()
        val cleanName = URLEncoder.encode(merchantName.trim().ifEmpty { "Merchant" }, "UTF-8")
        val formattedAmount = String.format(java.util.Locale.US, "%.2f", amount)
        val cleanNote = URLEncoder.encode(note.trim(), "UTF-8")

        return "upi://pay?pa=$cleanVpa&pn=$cleanName&am=$formattedAmount&cu=INR&tn=$cleanNote"
    }

    /**
     * Generates a QR Code Bitmap from the given string.
     */
    suspend fun generateQrBitmap(
        content: String,
        size: Int = 512,
        foregroundColor: Int = Color.BLACK,
        backgroundColor: Int = Color.WHITE
    ): Bitmap = withContext(Dispatchers.Default) {
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java).apply {
            put(EncodeHintType.CHARACTER_SET, "UTF-8")
            put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M)
            put(EncodeHintType.MARGIN, 1) // compact margin
        }

        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val pixels = IntArray(width * height)

        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (bitMatrix.get(x, y)) foregroundColor else backgroundColor
            }
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        bitmap
    }
}
