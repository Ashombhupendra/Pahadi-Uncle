package com.pahadi.uncle.presentation.home.Slider

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.pahadi.uncle.PahadiUncleApplication
import com.pahadi.uncle.R
//import com.smarteist.autoimageslider.SliderViewAdapter

class SliderAdapter(val list: List<SliderItem>) {/* :
    <SliderAdapter.SliderAdapterViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup?): SliderAdapterViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.slider_layout, parent, false)
        return SliderAdapterViewHolder(v)
    }

    override fun getCount(): Int {
        if(list.size==0){
            Toast.makeText(PahadiUncleApplication.instance,"List is empty",Toast.LENGTH_LONG).show()
        }else{

        }

                  return list.size
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterViewHolder?, position: Int) {
        val sliderItem = list.get(position);
        // from url in your imageview.
        viewHolder!!.image?.let {
            Glide.with(viewHolder.itemView).load(sliderItem?.bannerlink)
                .into(
                it
            )

        }


    }


    class SliderAdapterViewHolder(itemView: View?) : SliderViewAdapter.ViewHolder(itemView) {

         val image = itemView?.findViewById<ImageView>(R.id.myimage)


    }
*/
}