package com.pahadi.uncle.domain

import android.util.Log
import com.pahadi.uncle.domain.utils.BASE_URL
import com.pahadi.uncle.network.data.ProductDto
import com.pahadi.uncle.presentation.product_details.ProductDetails
import com.pahadi.uncle.presentation.home.Product

object ProductMapper {
    fun toProduct(productDto: ProductDto): Product{
        return Product(
            imageUrl = "${productDto.thumb_image}",
            priceFormatted = " ₹ ${productDto.price} /-",
            title = productDto.productName,
            description = productDto.description,
            featured = productDto.featured == 1,
            outOfStock = productDto.stock == 1,
                wishlist = when(productDto.wishlist){
                                                    1 ->{
                                                        true
                                                    }
                                                     else->{
                                                       false
                                                    }
                                                    },
                showwishlist = productDto.wishlist == 1,
            ratings = productDto.ratings,
            total_rating = productDto.total_ratings
        )
    }

    fun toProductDetails(productDto: ProductDto): ProductDetails {
        Log.d("Productdetail", productDto.productImage)
        return ProductDetails(
            productId = productDto.id.toString(),
            userId = productDto.userId.toString(),
            priceFormatted = "₹ ${productDto.price} /-",
            title = productDto.productName,
            location = ": ${productDto.location}",
            description = ": ${productDto.description}",
            productunit = ": ${productDto.unit}",
            product_condition =  ": ${productDto.product_condition} ",
            product_district = ": ${productDto.product_district} ",
            productcity = ": ${productDto.city}",
            mainImageUrl = "$BASE_URL/uploads/product/${productDto.productImage}",
            featured = productDto.featured == 1,
            specification = productDto.specification,

            username = productDto.username?.capitalize() ?: productDto.productName,
            profileImage = productDto.profileImage,
            wishlist = when(productDto.wishlist){
                1 ->{
                    true
                }
                else->{
                    false
                }
            },
                showwishlist = productDto.wishlist == 1,
            ratings = productDto.ratings,
            total_rating = productDto.total_ratings

        )
    }
}
