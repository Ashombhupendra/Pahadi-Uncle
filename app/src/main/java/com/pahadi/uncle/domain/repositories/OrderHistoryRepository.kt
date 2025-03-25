package com.pahadi.uncle.domain.repositories

import com.pahadi.uncle.domain.utils.SharedPrefHelper
import com.pahadi.uncle.network.OrderServices
import com.pahadi.uncle.network.utils.getRetrofitService
import com.pahadi.uncle.network.utils.safelyCallApi
import retrofit2.http.Field

object OrderHistoryRepository {
    private  val user_id  = SharedPrefHelper.user.userId

    private val  orderhistoryServices = getRetrofitService(OrderServices::class.java)


    suspend fun getOrderHistory()= safelyCallApi {
        orderhistoryServices.getOrderHistory(user_id)
    }
    suspend fun getAddress()= safelyCallApi {
        orderhistoryServices.getAddress(user_id)
    }

    suspend fun deleteAddress(address_id: String) = safelyCallApi {
        orderhistoryServices.deleteAddress(address_id)
    }

    suspend fun getMyCart()= safelyCallApi {
        orderhistoryServices.getMyCart(user_id)
    }
    suspend fun deleteMyCart(cart_item_id : String)= safelyCallApi {
        orderhistoryServices.deleteMycart(cart_item_id)
    }
    suspend fun addTransaction(

      payment_mode :String,
        payment_id : String,
       products : String,
        order_amt : String,
        address_id : String,
    )= safelyCallApi {
        orderhistoryServices.SubmitTransation(user_id, payment_mode, payment_id, products, order_amt, address_id)
    }

    suspend fun addToCartProduct(
        product_id : String,
        quantity : String
    )= safelyCallApi {
        orderhistoryServices.AddtocardProduct(user_id, product_id, quantity)
    }

    suspend fun addAddress(
        full_name: String,
        phone: String,
        pincode: String,
        house_number: String,
        area: String,
        landmark: String,
        city: String,
        state: String,

        ) = safelyCallApi {
           orderhistoryServices.addAddress(user_id, full_name, phone, pincode, house_number, area, landmark, city, state)
    }
}