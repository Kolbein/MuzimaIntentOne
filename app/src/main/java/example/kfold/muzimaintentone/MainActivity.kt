package example.kfold.muzimaintentone

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var mIntentTV : TextView
    lateinit var mSendIntentBtn : Button
    lateinit var mSendResourceBtn : Button
    lateinit var mSendPersonBtn : Button
    lateinit var mSendEncounterBtn : Button
    lateinit var mResourceET : EditText

    lateinit var person : Person
    lateinit var encounter : Encounter

    var resourceData = ""
    val RESOURCE_READ_REQUEST_CODE = 69

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mSendIntentBtn = sendIntentBtn
        mSendResourceBtn = sendResourceBtn
        mSendPersonBtn = personBtn
        mSendEncounterBtn = encounterBtn
        mIntentTV = intentTV
        mResourceET = resourceET
        initiateResources()

        mSendIntentBtn.setOnClickListener {
            requestResource()
        }

        mSendResourceBtn.setOnClickListener {
            sendResourceString()
        }

        mSendPersonBtn.setOnClickListener {
            sendPersonResourceJson()
        }

        mSendEncounterBtn.setOnClickListener {
            sendEncounterResourceJson()
        }
    }

    fun sendResourceString() {
        var resourceString = mResourceET.text.toString()
        var intent = Intent().apply {
            action = "example.kfold.muzimaintenttwo.ACTION_PROVIDER_TO_FHIR"
            putExtra("resource", resourceString)
            type = "text/plain"
        }
        startActivity(intent)

    }

    fun sendPersonResourceJson() {
        // Gson test
        var gson = Gson()
        var personJson = gson.toJson(person)

        var intent = Intent().apply {
            action = "example.kfold.muzimaintenttwo.ACTION_PROVIDER_TO_FHIR_JSON"
            putExtra("resource", personJson)
            putExtra("class", "person")
            type = "text/plain"
        }
        startActivity(intent)
    }

    fun sendEncounterResourceJson() {
        // Gson test
        var gson = Gson()
        var encounterJson = gson.toJson(encounter)

        var intent = Intent().apply {
            action = "example.kfold.muzimaintenttwo.ACTION_PROVIDER_TO_FHIR_JSON"
            putExtra("resource", encounterJson)
            putExtra("class", "encounter")
            type = "text/plain"
        }
        startActivity(intent)
    }

    fun requestResource() {
        val intent: Intent = Intent().apply {
            action = "example.kfold.muzimaintenttwo.ACTION_REQUEST_RESOURCE"
            putExtra(Intent.EXTRA_TEXT, "This is my text to send.")
            type = "text/plain"
        }
        //val shareIntent = Intent.createChooser(sendIntent, null)
        startActivityForResult(intent, RESOURCE_READ_REQUEST_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Check which request we're responding to
        if (requestCode == RESOURCE_READ_REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK && data != null) {


                resourceData = "Result: " + data?.getStringExtra("result")
                mIntentTV.setText(resourceData)
            }
        }
    }

    fun initiateResources() {
        // Testing the use of list in json parsing
        var gender = mutableListOf<String>("Male", "Trans", "Apache Heli")
        var birthdate = Date(1994, 7, 20)
        person = Person(gender, "Petrus", 25, birthdate)

        encounter = Encounter("Bad", 10, "Corona")
    }
}
