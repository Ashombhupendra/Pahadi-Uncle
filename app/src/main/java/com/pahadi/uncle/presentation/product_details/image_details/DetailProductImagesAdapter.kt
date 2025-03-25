package com.pahadi.uncle.presentation.product_details.image_details


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.pahadi.uncle.R
import com.pahadi.uncle.domain.utils.BASE_URL


class DetailProductImagesAdapter(// Context object
    var context: Context,// Array of images
    var images: List<listofilmage>
) : PagerAdapter() {

    // Layout Inflater
    lateinit var inflater: LayoutInflater
    override fun getCount(): Int {
        return  images.size
    }


    override fun isViewFromObject(view: View, `object`: Any): Boolean = view ==`object` as LinearLayout


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val image : PhotoView
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.image_slide_detail, container, false)
       image = view.findViewById(R.id.imageView_id)

        val url = "$BASE_URL/uploads/product/${images[position].imagename}"
        Log.d("thisisimage", url.toString())
        Glide.with(context).load(url).into(image)
        container.addView(view)
        return view
    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)

    }





}