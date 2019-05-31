package com.amity.abhishek.email

import android.Manifest
import android.R.attr.*
import android.arch.lifecycle.Observer
import android.app.PendingIntent.getActivity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import android.support.v4.content.ContextCompat.startActivity
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.pm.ResolveInfo
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.Message
import android.os.Vibrator
import android.support.v4.app.ActivityCompat
import android.widget.Button
import android.widget.TextView
import android.view.View
import android.view.View.OnClickListener
import java.util.Arrays
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import java.io.UnsupportedEncodingException


@Suppress("NAME_SHADOWING")
class MainActivity : AppCompatActivity() {


    private val REQUEST_RECORD_AUDIO_PERMISSION = 200
    private val permissions = arrayOf(Manifest.permission.RECORD_AUDIO)

    private lateinit var textField: TextView
    private lateinit var Button: Button


    private var speech: TextToSpeech? = null
    private lateinit var speechRecognizerViewModel: MainSpeechRecognizerViewModel




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toSpeech()
        textField = findViewById(R.id.text_field)
        Button = findViewById<Button>(R.id.button).apply {
            setOnClickListener(ClickListener)
        }
        setupSpeechViewModel()

    }




    private fun toSpeech() {

        try {
            speech = TextToSpeech(this, TextToSpeech.OnInitListener { i ->
                if (i == TextToSpeech.SUCCESS) {
                    val result = speech?.setLanguage(Locale.ENGLISH)
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported")
                    } else {
                        speech?.setPitch(1.0f)
                        speech?.setSpeechRate(1.0f)
                        speech?.speak("What do you want Email or Scanner", TextToSpeech.QUEUE_FLUSH, null)
                    }
                } else {
                    Log.e("TTS", "Initialization failed")
                }
            })
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }
    private val ClickListener = View.OnClickListener {
        if (!speechRecognizerViewModel.permissionToRecordAudio) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)
            return@OnClickListener
        }

        if (speechRecognizerViewModel.isListening) {
            speechRecognizerViewModel.stopListening()
        } else {
            speechRecognizerViewModel.startListening()
        }
    }

    private fun setupSpeechViewModel() {
        speechRecognizerViewModel = ViewModelProviders.of(this).get(MainSpeechRecognizerViewModel::class.java)
        speechRecognizerViewModel.getViewState().observe(this, Observer<MainSpeechRecognizerViewModel.ViewState> { viewState ->
            render(viewState)
        })
    }

    private fun render(uiOutput: MainSpeechRecognizerViewModel.ViewState?) {
        if (uiOutput == null) return

        textField.text = uiOutput.spokenText
        if(textField.text=="email"){
            val intent = Intent(this, emailpage::class.java);
            startActivity(intent) }
        else if (textField.text=="scanner"){
            val intent = Intent(this, camera::class.java);
             startActivity(intent)
        }


    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            speechRecognizerViewModel.permissionToRecordAudio = grantResults[0] == PackageManager.PERMISSION_GRANTED
        }

        if (speechRecognizerViewModel.permissionToRecordAudio) {
            Button.performClick()
        }
    }

}










