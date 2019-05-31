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


    private lateinit var cButton: Button
    private  var eButton: Button?=null


    private var speech: TextToSpeech? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toSpeech()
         eButton = findViewById(R.id.ebutton) as Button
        eButton?.setOnClickListener {
            // Handler code here.
            val intent = Intent(this, emailpage::class.java);
            startActivity(intent)}
             cButton = findViewById(R.id.cbutton) as Button
            cButton?.setOnClickListener {
                // Handler code here.
                val intent = Intent(this, camera::class.java);
                startActivity(intent)

        }
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
                        speech?.speak("for camera tap on upper half screen and for email tap on lower half screen", TextToSpeech.QUEUE_FLUSH, null)
                    }
                } else {
                    Log.e("TTS", "Initialization failed")
                }
            })
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }

}










