package com.example.camera2.ui.gallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.GenericTransitionOptions.with
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.with
import com.example.camera2.MainActivity

import com.example.camera2.Photo
import com.example.camera2.R

class GalleryAdapter(private val activity: MainActivity, private val fragment: GalleryFragment):
    RecyclerView.Adapter<ViewHolder>() {


    var photos = listOf<Photo>()

    inner class GalleryViewHolder(itemView: View): ViewHolder(itemView), View.OnClickListener{

        internal var mImage = itemView.findViewById<View>(R.id.image) as ImageView

        init {
            itemView.isClickable = true
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener {
                fragment.showPopup(it, photos[layoutPosition])
                return@setOnLongClickListener true
            }
        }


        override fun onClick(view: View) {
            val photo = photos[layoutPosition]
            val action = GalleryFragmentDirections.actionPhotoFilter(photo)
            view.findNavController().navigate(action)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       return GalleryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.image_preview, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder as GalleryViewHolder
        val current = photos[position]
            Glide.with(activity)
            .load(current.uri)
            .centerCrop()
            .into(holder.mImage)
    }

    override fun getItemCount(): Int {
        return photos.size
    }
}