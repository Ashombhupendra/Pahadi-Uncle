package com.pahadi.uncle.presentation.rating

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.pahadi.uncle.R
import com.pahadi.uncle.databinding.FragmentViewAllratingBinding
import com.pahadi.uncle.domain.ResultWrapper
import com.pahadi.uncle.domain.repositories.RatingRepository
import com.pahadi.uncle.network.data.RatingDTO
import kotlinx.coroutines.launch


class ViewAllrating : Fragment() {

          val ratinglist = MutableLiveData<List<RatingDTO>>()
    private lateinit var  progressbar : LinearProgressIndicator
    private lateinit var noratingfound  : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_allrating, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressbar = view.findViewById(R.id.view_all_rating_progress_indicator)
        noratingfound = view.findViewById(R.id.no_rating_found)
        val bundle = arguments
        if (bundle != null){
            val productid = bundle.getString("product_id")
            getRatings(productid.toString())
        }
        val ratingrv= view.findViewById<RecyclerView>(R.id.view_rating_rv)
        ratinglist.observe(viewLifecycleOwner){
            if (it.isNullOrEmpty()){
                noratingfound.visibility = View.VISIBLE
            }else{
                noratingfound.visibility = View.GONE

            }

            val adapter = ViewAllRatingAdapter(it as ArrayList<RatingDTO>)
            ratingrv.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }



    fun getRatings(productid : String){
        progressbar.visibility = View.VISIBLE
        lifecycleScope.launch {
            val result = RatingRepository.getRatings(productid,0)
            when(result){
                is ResultWrapper.Success ->{
                    progressbar.visibility = View.GONE
                    Log.d("viewallrating", result.response.toString())

                    val list  = mutableListOf<RatingDTO>()
                    list.addAll(result.response.map { it })
                    ratinglist.value = list
                }
                is ResultWrapper.Failure ->{
                    progressbar.visibility = View.GONE
                  Log.d("viewallrating", result.errorMessage)
                }
            }
        }

    }



}