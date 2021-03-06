package com.amity.abhishek.email

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Vibrator
import android.support.v4.app.ActivityCompat
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class emailpage : AppCompatActivity() {

    private val REQUEST_RECORD_AUDIO_PERMISSION = 200
    private val REQUEST_RECORD_AUDIO_SUBJECT_PERMISSION = 200
    private val REQUEST_RECORD_AUDIO_MESSAGE_PERMISSION = 200
    private val permissions = arrayOf(Manifest.permission.RECORD_AUDIO)

    private  var recipient: TextView?=null
    private  var subject: TextView?=null
    private  var message: TextView?=null
    private  var micButton: Button?=null

    private lateinit var sendButton: Button
    private  var micSubject: Button?=null
    private  var micMessage: Button?=null
    private var detectListener: String = "micClickListener"
    private val emailPattern = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")


    private lateinit var speechRecognizerViewModel: SpeechRecognizerViewModel
    private lateinit var speechRecognizersubject: SpeechRecognizerSubject
    private lateinit var speechRecognizermessage: SpeechRecognizermessage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emailpage)

        // sendButton = findViewById<Button>(R.id.sendEmailBtn).apply {
        //setOnClickListener(sendClickListener)}

        recipient = findViewById(R.id.recipientEt)
        subject = findViewById(R.id.subjectEt)
        message = findViewById(R.id.messageEt)
        micButton = findViewById<Button>(R.id.mic_button)?.apply {
            setOnClickListener(micClickListener)
        }


        // micSubject = findViewById<Button>(R.id.mic_subject).apply {
        //   setOnClickListener(micClickListenerSubject)}
        // micMessage = findViewById<Button>(R.id.mic_message).apply {
        //   setOnClickListener(micClickListenerMessage)}
        setupSpeechViewModel()
        setupSpeechViewSubject()
        setupSpeechViewMessage()

        micButton?.setOnLongClickListener(View.OnLongClickListener {
            if (detectListener.equals("micClickListenerSubject")) {
                detectListener = "micClickListener"
                micButton = findViewById<Button>(R.id.mic_button).apply {
                    setOnClickListener(micClickListener)
                }
            } else if (detectListener.equals("micClickListenerMessage")) {
                detectListener = "micClickListenerSubject"
                micButton = findViewById<Button>(R.id.mic_button).apply {
                    setOnClickListener(micClickListenerSubject)
                }
            } else if (detectListener.equals("sendClickListener")) {
                detectListener = "micClickListenerMessage"
                micButton = findViewById<Button>(R.id.mic_button).apply {
                    setOnClickListener(micClickListenerMessage)
                }
            }
            Toast.makeText(this, "RESET", Toast.LENGTH_SHORT).show()
            false
        })



    }


    private val sendClickListener = View.OnClickListener {
        val vibratorService = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibratorService.vibrate(500) // Using deprecated API because min sdk 21
        sendEmail()
        if(!detectListener.equals("emailInvalid")) {
            recipient?.setText(null)
            subject?.setText(null)
            message?.setText(null)
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            reset()
        }

    }
    private fun reset(){
        setupSpeechViewModel()
        setupSpeechViewSubject()
        setupSpeechViewMessage()

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
            val vibratorService = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibratorService.vibrate(100) // Using deprecated API because min sdk 21
            if(detectListener.equals("emailInvalid")){
                micButton = findViewById<Button>(R.id.mic_button).apply {
                    setOnClickListener(sendClickListener)
                }
                detectListener="sendClickListener"
            }else{
                micButton = findViewById<Button>(R.id.mic_button).apply {
                    setOnClickListener(micClickListenerSubject)}
                detectListener = "micClickListenerSubject"
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
            val vibratorService = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibratorService.vibrate(100) // Using deprecated API because min sdk 21
            micButton = findViewById<Button>(R.id.mic_button).apply {
                setOnClickListener(micClickListenerMessage)
            }

            detectListener = "micClickListenerMessage"

        }


    }
    private val micClickListenerMessage = View.OnClickListener {
        if (!speechRecognizermessage.permissionToRecordAudio) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_MESSAGE_PERMISSION)
            return@OnClickListener
        }

        if (speechRecognizermessage.isListening) {
            speechRecognizermessage.stopListening()
        } else {setupSpeechViewModel()
            setupSpeechViewSubject()
            setupSpeechViewMessage()
            speechRecognizermessage.startListening()
            val vibratorService = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibratorService.vibrate(100) // Using deprecated API because min sdk 21
            micButton = findViewById<Button>(R.id.mic_button).apply {
                setOnClickListener(sendClickListener)
            }

            detectListener = "sendClickListener"


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
        recipient?.text = uiOutput.spokenText
        recipient?.text = recipient?.text?.replace("\\s".toRegex(), "")


    }

    private fun subject(uiOutput: SpeechRecognizerSubject.ViewState?) {
        if (uiOutput == null) return
        subject?.text = uiOutput.spokenText


    }

    private fun message(uiOutput: SpeechRecognizermessage.ViewState?) {
        if (uiOutput == null) return
        message?.text = uiOutput.spokenText


    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            speechRecognizerViewModel.permissionToRecordAudio = grantResults[0] == PackageManager.PERMISSION_GRANTED
        }

        if (speechRecognizerViewModel.permissionToRecordAudio) {
            micButton?.performClick()
        }
        if (requestCode == REQUEST_RECORD_AUDIO_SUBJECT_PERMISSION) {
            speechRecognizersubject.permissionToRecordAudio = grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
        if (speechRecognizersubject.permissionToRecordAudio) {
            micSubject?.performClick()
        }
        if (requestCode == REQUEST_RECORD_AUDIO_MESSAGE_PERMISSION) {
            speechRecognizermessage.permissionToRecordAudio = grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
        if (speechRecognizermessage.permissionToRecordAudio) {
            micMessage?.performClick()
        }
    }

    //private fun sendEmail( recipient: TextView,subject: TextView,message:TextView) {
    private fun sendEmail() {
        //Getting content for email
        val email = recipient?.getText().toString().trim()

// onClick of button perform this simplest code.
        //val emailPattern = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
        val sub = subject?.getText().toString().trim()
        val mes = message?.getText().toString().trim()


        if (email.matches(emailPattern)) {

            val sm = SendMail(this, email, sub, mes)

            //Executing sendmail to send email
            sm.execute()
        } else {
            Toast.makeText(applicationContext, "Invalid email address", Toast.LENGTH_SHORT).show()
            micButton = findViewById<Button>(R.id.mic_button).apply {
                setOnClickListener(micClickListener)
            }
            detectListener="emailInvalid"
        }






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
