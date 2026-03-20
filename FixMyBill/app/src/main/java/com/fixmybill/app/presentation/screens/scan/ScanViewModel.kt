package com.fixmybill.app.presentation.screens.scan

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fixmybill.app.domain.usecase.ScanBillUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ScanState {
    data object Idle : ScanState()
    data object Scanning : ScanState()
    data object Processing : ScanState()
    data class Success(val extractedText: String, val imageUri: Uri) : ScanState()
    data class Error(val message: String) : ScanState()
}

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val scanBillUseCase: ScanBillUseCase
) : ViewModel() {

    private val _scanState = MutableStateFlow<ScanState>(ScanState.Idle)
    val scanState: StateFlow<ScanState> = _scanState.asStateFlow()

    private val _cameraPermissionGranted = MutableStateFlow(false)
    val cameraPermissionGranted: StateFlow<Boolean> = _cameraPermissionGranted.asStateFlow()

    private val _capturedImageUri = MutableStateFlow<Uri?>(null)
    val capturedImageUri: StateFlow<Uri?> = _capturedImageUri.asStateFlow()

    fun onCameraPermissionResult(granted: Boolean) {
        _cameraPermissionGranted.value = granted
    }

    fun onImageCaptured(uri: Uri) {
        _capturedImageUri.value = uri
        _scanState.value = ScanState.Scanning
    }

    fun processImage(uri: Uri, context: android.content.Context) {
        viewModelScope.launch {
            _scanState.value = ScanState.Processing
            try {
                val extractedText = scanBillUseCase(uri, context)
                if (extractedText.isBlank()) {
                    _scanState.value = ScanState.Error(
                        "Could not extract text from the image. Please try again with a clearer photo."
                    )
                } else {
                    _scanState.value = ScanState.Success(
                        extractedText = extractedText,
                        imageUri = uri
                    )
                }
            } catch (e: Exception) {
                _scanState.value = ScanState.Error(
                    message = e.message ?: "Failed to process the bill image"
                )
            }
        }
    }

    fun resetScan() {
        _scanState.value = ScanState.Idle
        _capturedImageUri.value = null
    }
}
