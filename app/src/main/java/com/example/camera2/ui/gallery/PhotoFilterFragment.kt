package com.example.camera2.ui.gallery

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PointF
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.camera2.MainActivity
import com.example.camera2.Photo
import com.example.camera2.R
import com.example.camera2.databinding.FragmentPhotoFilterBinding
import jp.wasabeef.glide.transformations.GrayscaleTransformation
import jp.wasabeef.glide.transformations.gpu.InvertFilterTransformation
import jp.wasabeef.glide.transformations.gpu.KuwaharaFilterTransformation
import jp.wasabeef.glide.transformations.gpu.SketchFilterTransformation
import jp.wasabeef.glide.transformations.gpu.SwirlFilterTransformation
import jp.wasabeef.glide.transformations.gpu.ToonFilterTransformation

class PhotoFilterFragment: Fragment(){
    private var _binding: FragmentPhotoFilterBinding? = null
    private val binding get() = _binding!!
    private var photo: Photo? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        arguments?.let {
            val safeArgs = PhotoFilterFragmentArgs.fromBundle(it)
            photo = safeArgs.photo

        }
        _binding = FragmentPhotoFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        loadImage(null)

        ArrayAdapter.createFromResource(requireActivity(), R.array.filters_array, android.R.layout.simple_spinner_dropdown_item)
            .also { adapter ->
                //specify the layout for the choices
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                //apply adapter to the spinner
                binding.filterSpinner.adapter = adapter

                binding.filterSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        var filter = parent?.getItemAtPosition(position).toString()
                        applyFilter(filter)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        TODO("Not yet implemented")
                    }
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main, menu)
        menu.findItem(R.id.save).isVisible = true
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            android.R.id.home -> findNavController().popBackStack()
            R.id.save -> {
                val image = getBitmapFromView(binding.selectedImage)
                if(image!= null) (activity as MainActivity).saveImage(image)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    private fun applyFilter(filter: String?){
        when (filter) {
            "None" -> loadImage(null)
            "Greyscale" -> loadImage(GrayscaleTransformation())
            "Swirl" -> loadImage(SwirlFilterTransformation(0.5f, 1.0f, PointF(0.5f, 0.5f)))
            "Invert filter" -> loadImage(InvertFilterTransformation())
            "Kuwahara filter" -> loadImage(KuwaharaFilterTransformation(25))
            "Sketch filter" -> loadImage(SketchFilterTransformation())
            "Toon filter" -> loadImage(ToonFilterTransformation())

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadImage(glideFilter: Transformation<Bitmap>?){
    when {
        photo != null && glideFilter != null ->{
            Glide.with(this)
                .load(photo!!.uri)
                .transform(CenterCrop(),
                glideFilter)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.selectedImage)
        }
        photo != null ->{
            Glide.with(this)
                .load(photo!!.uri)
                .transform(CenterCrop())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.selectedImage)
        }
    }
    }
    private fun getBitmapFromView(view: View): Bitmap?{
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

}