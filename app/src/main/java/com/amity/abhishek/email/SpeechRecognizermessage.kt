package com.amity.abhishek.email

import android.Manifest
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.support.v4.content.ContextCompat

class SpeechRecognizermessage (application: Application) : AndroidViewModel(application), RecognitionListener {

    data class ViewState(
            val spokenText: String,
            val isListening: Boolean,
            val error: String?
    )

    private var viewState: MutableLiveData<ViewState>? = null

    private val speechRecognizer: SpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(application.applicationContext).apply {
        setRecognitionListener(this@SpeechRecognizermessage)
    }

    private val recognizerIntent: Intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, application.packageName)
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
    }

    var isListening = false
    get() = viewState?.value?.isListening ?: false

    var permissionToRecordAudio = checkAudioRecordingPermission(context = application)

    fun getViewState(): LiveData<ViewState> {
        if (viewState == null) {
            viewState = MutableLiveData()
            viewState?.value = initViewState()
        }

        return viewState as MutableLiveData<ViewState>
    }

    private fun initViewState() = ViewState(spokenText = "", isListening = false, error = null)

    fun startListening() {
        speechRecognizer.startListening(recognizerIntent)
        notifyListening(isRecording = true)
    }

    fun stopListening() {
        speechRecognizer.stopListening()
        notifyListening(isRecording = false)
    }

    private fun notifyListening(isRecording: Boolean) {
        viewState?.value = viewState?.value?.copy(isListening = isRecording)
    }

    private fun updateResults(speechBundle: Bundle?) {
        val userSaid = speechBundle?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        viewState?.value = viewState?.value?.copy(spokenText = userSaid?.get(0) ?: "")
    }

    private fun checkAudioRecordingPermission(context: Application) =
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

    override fun onPartialResults(results: Bundle?) = updateResults(speechBundle = results)
    override fun onResults(results: Bundle?) = updateResults(speechBundle = results)
    override fun onEndOfSpeech() = notifyListening(isRecording = false)

    override fun onError(errorCode: Int) {
        viewState?.value = viewState?.value?.copy(error = when (errorCode) {
            SpeechRecognizer.ERROR_AUDIO -> "error_audio_error"
            SpeechRecognizer.ERROR_CLIENT -> "error_client"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "error_permission"
            SpeechRecognizer.ERROR_NETWORK -> "error_network"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "error_timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> "error_no_match"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "error_busy"
            SpeechRecognizer.ERROR_SERVER -> "error_server"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "error_timeout"
            else -> "error_unknown"
        })
    }

    override fun onReadyForSpeech(p0: Bundle?) {}
    override fun onRmsChanged(p0: Float) {}
    override fun onBufferReceived(p0: ByteArray?) {}
    override fun onEvent(p0: Int, p1: Bundle?) {}
    override fun onBeginningOfSpeech() {}

}