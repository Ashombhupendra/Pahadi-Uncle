package com.pahadi.uncle.presentation.product_details

import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.MediaController
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.pahadi.uncle.R
import com.pahadi.uncle.presentation.utils.loadImageFromUrl


sealed class ImageVideoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(url: String, rvHost: rvHost)
}

class ImageViewHolder(view: View) : ImageVideoViewHolder(view) {
    private val imageView: ImageView = view.findViewById(R.id.pager_image_item)

    override fun bind(url: String, rvHost: rvHost) {
        loadImageFromUrl(imageView, url)

        imageView.setOnClickListener {
            rvHost.navigatetoImageDetail()
        }
    }

    companion object {
        fun create(parent: ViewGroup): ImageViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.item_view_pager_image, parent, false)
            return ImageViewHolder(view)
        }
    }
}

class VideoViewHolder(view: View) : ImageVideoViewHolder(view) {
    private val videoView: VideoView = view.findViewById(R.id.pager_video_item)
    private val progressIndicator: LinearProgressIndicator =
        view.findViewById(R.id.progress_indicator)

    override fun bind(url: String,rvHost: rvHost) {
        val mediaController = MediaController(itemView.context).apply {
            setAnchorView(videoView)
        }

          Log.d("hellourl", url.toString())

        val uri = Uri.parse(url.replace(" ", "%20"))



        Log.d("VIDEO", uri.encodedPath.toString())

        videoView.apply {
            setMediaController(mediaController)
            setVideoURI(uri)
            setOnPreparedListener { mp ->
                progressIndicator.visibility = View.GONE
                Log.d("VIDEO", "2")
                //Get your video's width and height
                //Get your video's width and height
                val videoWidth = mp.videoWidth
                val videoHeight = mp.videoHeight
                  Log.d("VIDEO ", "$videoWidth : $videoHeight")
                //Get VideoView's current width and height

                //Get VideoView's current width and height
                val videoViewWidth = videoView.width
                val videoViewHeight = videoView.height

                val xScale = videoViewWidth.toFloat() / videoWidth
                val yScale = videoViewHeight.toFloat() / videoHeight

                val scale = Math.min(xScale, yScale)

                val scaledWidth = scale * videoWidth
                val scaledHeight = scale * videoHeight

                //Set the new size for the VideoView based on the dimensions of the video

                //Set the new size for the VideoView based on the dimensions of the video
                val layoutParams = videoView.layoutParams
                layoutParams.width = scaledWidth.toInt()
                layoutParams.height = scaledHeight.toInt()
                videoView.layoutParams = layoutParams
            }
            requestFocus()
            start()

        }
    }

    companion object {
        fun create(parent: ViewGroup): VideoViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.item_view_pager_video, parent, false)
            return VideoViewHolder(view)
        }
    }
}