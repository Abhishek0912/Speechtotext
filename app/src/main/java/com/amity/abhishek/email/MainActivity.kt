package com.amity.abhishek.email

import android.Manifest
import android.arch.lifecycle.Observer
import android.app.PendingIntent.getActivity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import android.support.v4.content.ContextCompat.startActivity
import android.R.attr.name
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.ResolveInfo
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.Message
import android.support.v4.app.ActivityCompat
import android.widget.Button
import android.widget.TextView
import android.view.View
import android.view.View.OnClickListener
import java.util.Arrays
import android.R.attr.button




@Suppress("NAME_SHADOWING")
class MainActivity : AppCompatActivity()  {
    private val REQUEST_RECORD_AUDIO_PERMISSION = 200
    private val REQUEST_RECORD_AUDIO_SUBJECT_PERMISSION = 200
    private val REQUEST_RECORD_AUDIO_MESSAGE_PERMISSION = 200
    private val permissions = arrayOf(Manifest.permission.RECORD_AUDIO)

    private lateinit var recipient: TextView
    private lateinit var subject: TextView
    private lateinit var message: TextView
    private lateinit var micButton: Button

    private lateinit var sendButton: Button
    private lateinit var micSubject: Button
    private lateinit var micMessage: Button



    private lateinit var speechRecognizerViewModel: SpeechRecognizerViewModel
    private lateinit var speechRecognizersubject: SpeechRecognizerSubject
    private lateinit var speechRecognizermessage: SpeechRecognizermessage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

       // sendButton = findViewById<Button>(R.id.sendEmailBtn).apply {
        //setOnClickListener(sendClickListener)}

        recipient = findViewById(R.id.recipientEt)
        subject = findViewById(R.id.subjectEt)
        message = findViewById(R.id.messageEt)
        micButton = findViewById<Button>(R.id.mic_button).apply {
            setOnClickListener(micClickListener)
        }


        // micSubject = findViewById<Button>(R.id.mic_subject).apply {
        //   setOnClickListener(micClickListenerSubject)}
        // micMessage = findViewById<Button>(R.id.mic_message).apply {
        //   setOnClickListener(micClickListenerMessage)}
        setupSpeechViewModel()
        setupSpeechViewSubject()
        setupSpeechViewMessage()
        micButton.setOnLongClickListener(View.OnLongClickListener {
            if (!speechRecognizerViewModel.permissionToRecordAudio) {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)
            }

            if (speechRecognizerViewModel.isListening) {
                speechRecognizerViewModel.stopListening()


            } else {
                speechRecognizerViewModel.startListening()
                micButton = findViewById<Button>(R.id.mic_button).apply {
                    setOnClickListener(micClickListenerSubject)
                }


            }
            false
        })


    }


    private val sendClickListener= View.OnClickListener {
        sendEmail()
    }

    private val micClickListener = View.OnClickListener {
        if (!speechRecognizerViewModel.permissionToRecordAudio) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)
            return@OnClickListener
        }

        if (speechRecognizerViewModel.isListening) {
            speechRecognizerViewModel.stopListening()
        } else {
            speechRecognizerViewModel.startListening()
            micButton = findViewById<Button>(R.id.mic_button).apply {
                setOnClickListener(micClickListenerSubject)
            }

        }


    }



    private val micClickListenerSubject = View.OnClickListener {
        if (!speechRecognizersubject.permissionToRecordAudio) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_SUBJECT_PERMISSION)
            return@OnClickListener
        }

        if (speechRecognizersubject.isListening) {
            speechRecognizersubject.stopListening()
        } else {
            speechRecognizersubject.startListening()
            micButton = findViewById<Button>(R.id.mic_button).apply {
                setOnClickListener(micClickListenerMessage)
            }


        }


    }
    private val micClickListenerMessage = View.OnClickListener {
        if (!speechRecognizermessage.permissionToRecordAudio) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_MESSAGE_PERMISSION)
            return@OnClickListener
        }

        if (speechRecognizermessage.isListening) {
            speechRecognizermessage.stopListening()
        } else {
            speechRecognizermessage.startListening()
            micButton = findViewById<Button>(R.id.mic_button).apply {
                setOnClickListener(sendClickListener)}

        }


    }

    private fun setupSpeechViewModel() {
        speechRecognizerViewModel = ViewModelProviders.of(this).get(SpeechRecognizerViewModel::class.java)
        speechRecognizerViewModel.getViewState().observe(this, Observer<SpeechRecognizerViewModel.ViewState> { viewState ->
            render(viewState)


        })

    }

    private fun setupSpeechViewSubject() {
        speechRecognizersubject = ViewModelProviders.of(this).get(SpeechRecognizerSubject::class.java)
        speechRecognizersubject.getViewState().observe(this, Observer<SpeechRecognizerSubject.ViewState> { viewState ->
            subject(viewState)
        })
    }

    private fun setupSpeechViewMessage() {
        speechRecognizermessage = ViewModelProviders.of(this).get(SpeechRecognizermessage::class.java)
        speechRecognizermessage.getViewState().observe(this, Observer<SpeechRecognizermessage.ViewState> { viewState ->
            message(viewState)
        })
    }

    private fun render(uiOutput: SpeechRecognizerViewModel.ViewState?) {
        if (uiOutput == null) return
        recipient.text = uiOutput.spokenText
        recipient.text = recipient.text.replace("\\s".toRegex(), "")


    }

    private fun subject(uiOutput: SpeechRecognizerSubject.ViewState?) {
        if (uiOutput == null) return
        subject.text = uiOutput.spokenText


    }

    private fun message(uiOutput: SpeechRecognizermessage.ViewState?) {
        if (uiOutput == null) return
        message.text = uiOutput.spokenText


    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            speechRecognizerViewModel.permissionToRecordAudio = grantResults[0] == PackageManager.PERMISSION_GRANTED
        }

        if (speechRecognizerViewModel.permissionToRecordAudio) {
            micButton.performClick()
        }
        if (requestCode == REQUEST_RECORD_AUDIO_SUBJECT_PERMISSION) {
            speechRecognizersubject.permissionToRecordAudio = grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
        if (speechRecognizersubject.permissionToRecordAudio) {
            micSubject.performClick()
        }
        if (requestCode == REQUEST_RECORD_AUDIO_MESSAGE_PERMISSION) {
            speechRecognizermessage.permissionToRecordAudio = grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
        if (speechRecognizermessage.permissionToRecordAudio) {
            micMessage.performClick()
        }
    }

    //private fun sendEmail( recipient: TextView,subject: TextView,message:TextView) {
   private fun sendEmail() {
        //Getting content for email
        val email = recipient.getText().toString().trim()
        val sub = subject.getText().toString().trim()
        val mes = message.getText().toString().trim()

        //Creating SendMail object
        val sm = SendMail(this, email, sub, mes)

        //Executing sendmail to send email
        sm.execute()
    }


    // val mIntent = Intent(Intent.ACTION_SEND)
    //mIntent.data = Uri.parse("mailto")
    //mIntent.type = "text/plain"
    //setupSpeechViewModel()


    //mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient.text.toString()))
    //mIntent.putExtra(Intent.EXTRA_SUBJECT, subject.text.toString())
    //mIntent.putExtra(Intent.EXTRA_TEXT, message.text.toString())
    //val pm = getPackageManager()
    //val matches = pm.queryIntentActivities(mIntent, 0)
    //var best: ResolveInfo? = null
    //for (info in matches)
    //  if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
    //      best = info
    //if (best != null)
    //  mIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name)
    //startActivity(mIntent)
    //}



}





