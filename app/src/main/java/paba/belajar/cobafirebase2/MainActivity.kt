package paba.belajar.cobafirebase2

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        _etProvinsi = findViewById<EditText>(R.id.etProvinsi)
        _etIbukota = findViewById<EditText>(R.id.etIbuKota)
        val _btSimpan = findViewById<Button>(R.id.btnSimpan)
        val _lvData = findViewById<ListView>(R.id.lvData)

//        lvAdapter = ArrayAdapter(
//            this, android.R.layout.simple_list_item_1, dataProvinsi
//        )
        lvAdapter = SimpleAdapter(
            this,
            data,
            android.R.layout.simple_list_item_2,
            arrayOf<String>("Pro", "Ibu"),
            intArrayOf(
                android.R.id.text1,
                android.R.id.text2
            )
        )
        _lvData.adapter = lvAdapter

        fun TambahData(db: FirebaseFirestore, Provinsi: String, Ibukota : String) {
            val dataBaru = daftarProvinsi(Provinsi, Ibukota)
            db.collection("tbProvinsi")
                .add(dataBaru)
                .addOnSuccessListener {
                    _etProvinsi.setText("")
                    _etIbukota.setText("")
                    Log.d("Firebasee", "Data Berhasil Disimpan")
                }
                .addOnFailureListener {
                    Log.d("Firebasee", it.message.toString())
                }
        }



        fun readData(db: FirebaseFirestore) {
            db.collection("tbProvinsi").get()
                .addOnSuccessListener {
                    result ->
                    dataProvinsi.clear()
                    for (document in result) {
                        val readData = daftarProvinsi(
                            document.data.get("provinsi").toString(),
                            document.data.get("ibukota").toString()
                        )
                        dataProvinsi.add(readData)
                        dataProvinsi.forEach {
                            val dt: MutableMap<String, String> = HashMap(2)
                            dt["Pro"] = it.provinsi
                            dt["Ibu"] = it.ibukota
                            data.add(dt)
                        }
                    }
                    lvAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener{
                    Log.d("Firebasee", it.message.toString())
                }
        }

        _btSimpan.setOnClickListener{
            TambahData(db, _etProvinsi.text.toString(), _etIbukota.text.toString())
            readData(db)
        }
        readData(db)

    }

    val db = Firebase.firestore

    var dataProvinsi = ArrayList<daftarProvinsi>()
//    lateinit var lvAdapter : ArrayAdapter<daftarProvinsi>

    lateinit var _etProvinsi : EditText
    lateinit var _etIbukota : EditText

    var data: MutableList<Map<String, String>> = ArrayList()
    lateinit var lvAdapter : SimpleAdapter

}