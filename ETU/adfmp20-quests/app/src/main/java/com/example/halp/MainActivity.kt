package com.example.halp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.protobuf.LazyStringArrayList
import com.yandex.mapkit.geometry.Point
import kotlin.random.Random

private const val PERMISSION_REQUEST = 10

class MainActivity : AppCompatActivity()
{
    var user: User? = null
    var quest: Quest? = null
    var orderNum: Int = -1
    var filterQuests: QuestFilter? = null;
    var filterFavourites: QuestFilter? = null;


    lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var db: FirebaseFirestore

    private var permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    fun checkPermission(context: Context, permissionArray: Array<String>) : Boolean{
        var allSuccess = true
        for(i in permissionArray.indices) {
            if(checkCallingOrSelfPermission(permissionArray[1]) == PackageManager.PERMISSION_DENIED) {
                allSuccess = false
            }
        }
        return allSuccess
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PERMISSION_REQUEST) {
            var allSuccess = true
            for(i in permissions.indices) {
                if(grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    allSuccess = false
                    var requesrAgain = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(permissions[i])
                    if(requesrAgain) {
                        Toast.makeText(this.applicationContext, "Permission denied", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this.applicationContext, "Go to settings and enable the permission", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            if(allSuccess){
                Toast.makeText(this.applicationContext, "Permissions Granted", Toast.LENGTH_SHORT).show()

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission(this.applicationContext, permissions)) {

            } else {
                requestPermissions(permissions, PERMISSION_REQUEST)
            }
        }
        db = FirebaseFirestore.getInstance()
        //randomFill();

        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        supportActionBar?.hide(); //hide the title bar

        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_search, R.id.navigation_map, R.id.navigation_orders, R.id.navigation_favourites, R.id.loadingFragment))

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    fun randomFill()
    {
        val pics = arrayOf("https://firebasestorage.googleapis.com/v0/b/quests-15b53.appspot.com/o/1464773721129589536.jpg?alt=media&token=c0cde0b6-512b-489d-aeb6-c45be6164ff7",
            "https://firebasestorage.googleapis.com/v0/b/quests-15b53.appspot.com/o/2.jpg?alt=media&token=ed1f46c1-ed76-4af5-b5c1-5bbbf26a6418",
            "https://firebasestorage.googleapis.com/v0/b/quests-15b53.appspot.com/o/NINTCHDBPICT000568507868.jpg?alt=media&token=24d63657-bdc2-4425-8d87-1e4e3d482ec3",
            "https://firebasestorage.googleapis.com/v0/b/quests-15b53.appspot.com/o/maxresdefault.jpg?alt=media&token=8556a9a3-6dfa-4743-9475-3a7f2f2cf710",
            "https://firebasestorage.googleapis.com/v0/b/quests-15b53.appspot.com/o/solo-levelling-feature.jpg?alt=media&token=a57c6bb6-a02b-406e-927c-361155cecd07")

        val complexity = arrayOf("Easy", "So-so", "Hard", "Nuts")
        val company = arrayOf("A", "B", "C")
        val desc = arrayOf("descA", "descB", "descC")
        val genre = arrayOf("Touchy", "Notouchy", "Scary", "Boring")
        val names = arrayOf("Athyes Pharcey",
            "Gery",
            "Gerey",
            "Ealher" ,
            "Jamas" ,
            "Reyny",
            "Atham",
            "Amew",
            "Wynstio",
            "Thony")

        for( x in 0 until 50 )
        {
            val q = hashMapOf(
                "max_people" to Random.nextInt(1, 7),
                "min_people" to Random.nextInt(8, 15),
                "name" to names[Random.nextInt(0,6)],
                "complexity" to complexity[Random.nextInt(0,3)],
                "company" to company[Random.nextInt(0,2)],
                "description" to desc[Random.nextInt(0,2)],
                "duration" to Random.nextInt(30, 180),
                "genre" to genre[Random.nextInt(0,2)],
                "img_url" to pics[Random.nextInt(0,4)],
                "cost" to Random.nextInt(200,1500),
                "coords" to GeoPoint(Random.nextDouble(), Random.nextDouble()),
                "address" to "address",
                "phone" to "9(999)9999999"
            )
            db.collection("quests").add(q)
        }
    }
}
