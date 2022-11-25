package com.example.coffetec.sensors
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.coffetec.HomeActivity
import com.example.coffetec.databinding.ActivityInfoSensorBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

class InfoSensorActivity : AppCompatActivity() {
    lateinit var binding: ActivityInfoSensorBinding
    lateinit var sensorId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoSensorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sensorId = intent.extras?.getString("idSensor", "").toString()
        loadInformation()

        binding.editInfoSensorButton.setOnClickListener{
            changeValues()
        }

       binding.backAuxSensorHeader.setOnClickListener {
           finish()
       }
    }

    private fun changeValues() {
        Firebase.firestore.collection("sensors").document(sensorId)
            .update("name",binding.nameSensorInfoText.text.toString())

        Firebase.firestore.collection("sensors").document(sensorId)
            .update("state", "inactive")

        showMsg("Datos cambiados exitosamente")

        val intent = Intent(this, HomeActivity::class.java).apply{
            putExtra("resetSensors", "true")
        }
        startActivity(intent)
    }

    private fun loadInformation() {
        disableTextFields()
        /// Load information from Firestore
        Firebase.firestore.collection("sensors")
            .document(sensorId)
            .get()
            .addOnCompleteListener{ sensor ->
                var sensorInfo: Sensor = sensor.result.toObject(Sensor::class.java)!!
                if(sensorInfo.id !="") {
                    binding.nameSensorInfoText.setText(sensorInfo.name)
                    binding.idSensorInfoText.setText(sensorInfo.id)
                    binding.stateSensorText.setText(sensorInfo.state)
                    binding.coordinatesSensorText.setText(sensorInfo.coordinates)
                    dinamicState(sensorInfo.state)
                }
            }
        }

    private fun disableTextFields(){
        binding.idSensorInfoText.setKeyListener(null)
        binding.typeSensorInfoText.setKeyListener(null)

        binding.idSensorInfoText.setOnClickListener {
            showMsg("No se puede editar el codigo")
        }

        binding.typeSensorInfoText.setOnClickListener {
            showMsg("No se puede editar el tipo de sensor");
        }
    }

    private fun dinamicState(state: String){
        val imageResourceEx = resources.getIdentifier("@drawable/ic_baseline_close_24", null, packageName)
        val imageResourceEye = resources.getIdentifier("@drawable/ic_baseline_remove_red_eye_24", null, packageName)
        binding.stateSensorImg.visibility = View.VISIBLE

        if(state == "inactive"){
            binding.stateSensorText.setText("inactivo")
            binding.stateSensorImg.setImageResource(imageResourceEx)
        }else{
            binding.stateSensorText.setText("activo")
            binding.stateSensorImg.setImageResource(imageResourceEye)
        }
    }

    private fun showMsg(msg: String){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}