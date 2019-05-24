@file:Suppress("DEPRECATION")

package com.amity.abhishek.email

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask
import android.widget.Toast

import java.util.Properties

import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


class SendMail//Class Constructor
(//Declaring Variables
        @SuppressLint("StaticFieldLeak") private val context: Context, //Information to send email
        private val email: String, private val subject: String, private val message: String)//Initializing variables
    : AsyncTask<Void, Void, Void>() {
    private var session: Session? = null

    //Progressdialog to show while sending email
    private var progressDialog: ProgressDialog? = null

    override fun onPreExecute() {
        super.onPreExecute()
        //Showing progress dialog while sending email
        progressDialog = ProgressDialog.show(context, "Sending message", "Please wait...", false, false)
    }

    override fun onPostExecute(aVoid: Void?) {

        super.onPostExecute(aVoid)

        //Dismissing the progress dialog
        progressDialog!!.dismiss()
        //Showing a success message
        Toast.makeText(context, "Message Sent", Toast.LENGTH_LONG).show()
    }

    override fun doInBackground(vararg params: Void): Void? {
        //Creating propertiesval a=checkNotNull(aVoid)
        val props = Properties()

        //Configuring properties for gmail
        //If you are not using gmail you may need to change the values
        props["mail.smtp.host"] = "smtp.gmail.com"
        props["mail.smtp.socketFactory.port"] = "465"
        props["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.port"] = "465"

        //Creating a new session
        session = Session.getDefaultInstance(props,
                object : javax.mail.Authenticator() {
                    //Authenticating the password
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(Config.EMAIL, Config.PASSWORD)
                    }
                })

        try {
            //Creating MimeMessage object
            val mm = MimeMessage(session)

            //Setting sender address
            mm.setFrom(InternetAddress(Config.EMAIL))
            //Adding receiver
            mm.addRecipient(Message.RecipientType.TO, InternetAddress(email))
            //Adding subject
            mm.subject = subject
            //Adding message
            mm.setText(message)

            //Sending email
            Transport.send(mm)

        } catch (e: MessagingException) {
            e.printStackTrace()
        }

        return null
    }
}