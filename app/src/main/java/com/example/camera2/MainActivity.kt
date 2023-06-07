package com.example.camera2

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.camera2.databinding.ActivityMainBinding
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.navigation_camera, R.id.navigation_gallery))

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(!CameraPermissionHelper.hasCameraPermission(this) || !CameraPermissionHelper.hasStoragePermission(this)){
            CameraPermissionHelper.requestPermissions(this)
        }
        else{
            recreate()
        }
    }
    fun prepareContentValues(): ContentValues{
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageName = "image_$timeStamp"

        return ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, imageName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM")
        }
    }

    fun saveImage(bitmap: Bitmap){
        val resolver = applicationContext.contentResolver
        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, prepareContentValues())
        try{
            val fos = imageUri?.let { resolver.openOutputStream(it) }
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos?.close()
            Toast.makeText(this, resources.getString(R.string.save), Toast.LENGTH_SHORT).show()
        } catch (e: FileNotFoundException) {
            Toast.makeText(
                this,
                resources.getString(R.string.error_saving_photo),
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: IOException){
            Toast.makeText(this, resources.getString(R.string.error_saving_photo), Toast.LENGTH_SHORT).show()
        }
    }

    object CameraPermissionHelper{
        private const val CAMERA_PERMISSION = Manifest.permission.CAMERA
        private const val READ_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE

        fun hasCameraPermission(activity: Activity): Boolean{
            return ContextCompat.checkSelfPermission(activity, CAMERA_PERMISSION ) == PackageManager.PERMISSION_GRANTED

        }

        fun hasStoragePermission(activity: Activity): Boolean{
            return ContextCompat.checkSelfPermission(activity, READ_PERMISSION) == PackageManager.PERMISSION_GRANTED
        }

        fun requestPermissions(activity: Activity){
            if(ActivityCompat.shouldShowRequestPermissionRationale(activity, CAMERA_PERMISSION)){
                AlertDialog.Builder(activity).apply {
                    setMessage(activity.getString(R.string.permission_required))
                    setPositiveButton(activity.getString(R.string.ok)) { _, _ ->
                        ActivityCompat.requestPermissions(
                            activity,
                            arrayOf(CAMERA_PERMISSION, READ_PERMISSION),
                            1)
                    }
                    show()
                }
            }
            else{
                ActivityCompat.requestPermissions(activity, arrayOf(CAMERA_PERMISSION, READ_PERMISSION), 1)
            }
        }

    }
}