package com.pahadi.uncle.presentation.rating

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pahadi.uncle.databinding.ItemViewAllRatingBinding
import com.pahadi.uncle.domain.utils.BASE_URL
import com.pahadi.uncle.network.data.RatingDTO
import com.squareup.picasso.Picasso

class ViewAllRatingAdapter(val list: List<RatingDTO>):
 RecyclerView.Adapter<ViewAllRatingAdapter.RatingHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingHolder {
        val binding = ItemViewAllRatingBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return RatingHolder(binding)
    }

    override fun onBindViewHolder(holder: RatingHolder, position: Int) {
            holder.bind(list[position])
     }

    override fun getItemCount(): Int {
           return list.size
    }

    class RatingHolder(val mBinding : ItemViewAllRatingBinding): RecyclerView.ViewHolder(mBinding.root) {
        fun bind(ratingDTO: RatingDTO) {

             mBinding.viewRatingName.text = "${ratingDTO.username}"
            mBinding.viewRatingReview.text = "${ratingDTO.review}"
            mBinding.viewRatingDate.text = "${ratingDTO.created}"
            mBinding.viewRatingRate.rating = ratingDTO.rating
            Picasso.get().load(ratingDTO.profile).into(mBinding.userImage)

        }

    }
}