package com.fixmybill.app.domain.usecase

import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ScanBillUseCase @Inject constructor() {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.Builder().build())

    suspend operator fun invoke(imageUri: Uri, context: android.content.Context): String {
        val image = InputImage.fromFilePath(context, imageUri)
        return suspendCoroutine { continuation ->
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    continuation.resume(visionText.text)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }
}
