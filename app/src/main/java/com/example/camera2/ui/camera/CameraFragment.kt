package com.example.camera2.ui.camera

import android.media.Image
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.camera2.MainActivity
import com.example.camera2.R
import com.example.camera2.databinding.FragmentCameraBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment() {
    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraExecutor: ExecutorService
    private var _binding: FragmentCameraBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentCameraBinding.inflate(inflater, container, false)

        return binding.root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraExecutor = Executors.newSingleThreadExecutor()
        openCamera()

        binding.fabTakePhoto.setOnClickListener{
            capturePhoto()
        }

    }

    private fun openCamera(){
        if(MainActivity.CameraPermissionHelper.hasCameraPermission(requireActivity())
            && MainActivity.CameraPermissionHelper.hasStoragePermission(requireActivity())) {

            val cameraProviderFuture = ProcessCameraProvider.getInstance(requireActivity())

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(binding.cameraFeed.surfaceProvider)
                    }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()

                try{
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
                }
                catch (e: java.lang.IllegalStateException){
                    Toast.makeText(requireActivity(), resources.getString(R.string.error_connecting_camera), Toast.LENGTH_SHORT).show()

                }

            }, ContextCompat.getMainExecutor(requireActivity()))

        } else MainActivity.CameraPermissionHelper.requestPermissions(requireActivity())
    }

    private fun capturePhoto(){
        if(!this::imageCapture.isInitialized){
            Toast.makeText(requireActivity(), getString(R.string.error_saving_photo), Toast.LENGTH_SHORT).show()
            return
        }
        val contentValues = (activity as MainActivity).prepareContentValues()
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(
            requireActivity().applicationContext.contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            .build()
        imageCapture.takePicture(
            outputFileOptions, ContextCompat.getMainExecutor(requireActivity()), object:
        ImageCapture.OnImageSavedCallback{
                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(requireActivity(), getString(R.string.error_saving_photo), Toast.LENGTH_SHORT).show()
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Toast.makeText(requireActivity(), getString(R.string.photo_saved), Toast.LENGTH_SHORT).show()
                }
        }
        )
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}