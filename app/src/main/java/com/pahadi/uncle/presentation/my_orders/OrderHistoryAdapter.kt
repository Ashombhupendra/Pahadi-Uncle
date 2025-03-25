package com.pahadi.uncle.presentation.my_orders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pahadi.uncle.PahadiUncleApplication
import com.pahadi.uncle.R
import com.pahadi.uncle.databinding.ItemOrderHistoryBinding
import com.pahadi.uncle.network.data.OrderHistoryDTO

class OrderHistoryAdapter(val list : List<OrderHistoryDTO>) :
    RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder>(){
    class ViewHolder(val binding : ItemOrderHistoryBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(historyDTO: OrderHistoryDTO) {
             binding.itemOrderAmount.text = "₹ ${historyDTO.product_amount}"
            binding.itemOrderProductName.text = "${historyDTO.product_name}"
            binding.itemOrderQuantity.text = "${historyDTO.product_quantity}"
            binding.itemOrderTrackingNo.text ="${historyDTO.trackingID}"
            binding.itemOrderStatus.text ="${historyDTO.order_status}"

            if (historyDTO.order_status.equals("Delivered")){
                binding.itemOrderStatus.setTextColor(
                    PahadiUncleApplication.instance.applicationContext?.resources?.getColorStateList(
                    R.color.orange_dark))
            }else if (historyDTO.order_status.equals("Ordered")){
                binding.itemOrderStatus.setTextColor(
                    PahadiUncleApplication.instance.applicationContext?.resources?.getColorStateList(
                    R.color.golden))
            }else if (historyDTO.order_status.equals("Shipped")){
                binding.itemOrderStatus.setTextColor(
                    PahadiUncleApplication.instance.applicationContext?.resources?.getColorStateList(
                    R.color.green))
            }else{
                binding.itemOrderStatus.setTextColor(
                    PahadiUncleApplication.instance.applicationContext?.resources?.getColorStateList(
                    R.color.orange_dark))
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
         val binding = ItemOrderHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}