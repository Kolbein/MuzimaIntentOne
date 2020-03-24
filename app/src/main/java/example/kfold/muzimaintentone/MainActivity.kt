package example.kfold.muzimaintentone

import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.muzima.muzimafhir.data.fhir.Patient
import example.kfold.muzimaintentone.data.Encounter
import example.kfold.muzimaintentone.data.Person
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import com.google.gson.reflect.TypeToken
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.MotionEvent
import android.view.View
import android.widget.*


class MainActivity : AppCompatActivity() {

    lateinit var mIntentTV: TextView
    lateinit var mSendIntentBtn: Button
    lateinit var mRequestListBtn: Button
    lateinit var mSendResourceBtn: Button
    lateinit var mSendPersonBtn: Button
    lateinit var mSendEncounterBtn: Button
    lateinit var mResourceET: EditText
    lateinit var mResourceLV: ListView

    lateinit var person: Person
    lateinit var encounter: Encounter

    lateinit var mListViewAdapter: ArrayAdapter<*>

    var resourceData : String? = null
    val RESOURCE_READ_REQUEST_CODE = 69

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mSendIntentBtn = sendIntentBtn
        mSendResourceBtn = sendResourceBtn
        mRequestListBtn = requestListBtn
        mSendPersonBtn = personBtn
        mSendEncounterBtn = encounterBtn
        mIntentTV = intentTV
        mResourceET = resourceET
        mResourceLV = resourceLV

        var initialList = arrayListOf<Patient>()
        mListViewAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, initialList)
        mResourceLV.adapter = mListViewAdapter
        initiateResources()
        buttonOnClickListeners()

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

    // queryType:       getOne, getList
    // resourceType:    patient, encounter...
    fun requestResource() {
        val intent: Intent = Intent().apply {
            action = "com.muzima.muzimafhir.ACTION_REQUEST_RESOURCE"
            putExtra("resourceType", "patient")
            putExtra("queryType", "getOne")
            putExtra("id", "5e2eb69b21c7a2122726889f")
            type = "text/plain"
        }
        //val shareIntent = Intent.createChooser(sendIntent, null)
        startActivityForResult(intent, RESOURCE_READ_REQUEST_CODE)
    }


    fun requestResourceList() {
        val intent: Intent = Intent().apply {
            action = "com.muzima.muzimafhir.ACTION_REQUEST_RESOURCE"
            putExtra("resourceType", "patient")
            putExtra("queryType", "getAll")
            type = "text/plain"
        }

        startActivityForResult(intent, RESOURCE_READ_REQUEST_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Check which request we're responding to
        if (requestCode == RESOURCE_READ_REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK && data != null) {

                resourceData = data.getStringExtra("resource")
                val resourceType = data.getStringExtra("resourceType")
                val queryType = data.getStringExtra("queryType")
                val gson = Gson()

                if (resourceType != null && queryType != null) {
                    if (resourceType.equals("patient") && queryType.equals("getOne")) {
                        var patient = gson.fromJson(resourceData, Patient::class.java)
                        mIntentTV.setText(patient.toString())
                    } else if (resourceType.equals("patient") && queryType.equals("getAll")) {
                        //val collectionType = object : TypeToken<List<Patient>>() {}.type
                        //val patientList = gson.fromJson(resourceData, collectionType) as List<Patient>
                        val patientList: List<Patient> = gson.fromJson(resourceData, Array<Patient>::class.java).toList()

                        mListViewAdapter = ArrayAdapter(this, R.layout.custom_textview, patientList)
                        mResourceLV.adapter = mListViewAdapter
                        mListViewAdapter.notifyDataSetChanged()
                        /*
                        var outString = ""
                        patientList.forEach { p ->
                            outString += (p.toString() + "\n\n")
                        }
                        mIntentTV.setText(outString)
                         */
                    }
                }

                // Receiving Person Object
                /*
                if (resourceDataClass.equals("person")) {
                    var person = gson.fromJson(resourceData, Person::class.java)
                    if (person != null ) {
                        var text = "Json: \n" + resourceData + "\n\n\n" +
                                "JsonToPerson toString(): \n" + person.toString()
                        mIntentTV.setText(text)
                    }
                    // Receiving Encounter Object
                } else if (resourceDataClass.equals("encounter")) {
                    var encounter = gson.fromJson(resourceData, Encounter::class.java)
                    if (encounter != null) {
                        var text = "Json: \n" + resourceData + "\n\n\n" +
                                "JsonToEncounter toString(): \n" + encounter.toString()
                        mIntentTV.setText(text)
                    }
                }
                */
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

    fun buttonEffect(button: View) {
        button.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.background.setColorFilter(-0x1f0b8adf, PorterDuff.Mode.SRC_ATOP)
                    v.invalidate()
                }
                MotionEvent.ACTION_UP -> {
                    v.background.clearColorFilter()
                    v.invalidate()
                }
            }
            false
        }
    }

    fun buttonOnClickListeners() {
        buttonEffect(mSendIntentBtn)
        mSendIntentBtn.setOnClickListener {
            requestResource()
        }

        buttonEffect(mRequestListBtn)
        mRequestListBtn.setOnClickListener {
            requestResourceList()
        }

        buttonEffect(mSendResourceBtn)
        mSendResourceBtn.setOnClickListener {
            sendResourceString()
        }

        buttonEffect(mSendPersonBtn)
        mSendPersonBtn.setOnClickListener {
            sendPersonResourceJson()
        }

        buttonEffect(mSendEncounterBtn)
        mSendEncounterBtn.setOnClickListener {
            sendEncounterResourceJson()
        }
    }
}
