package com.pahadi.uncle.domain

import com.pahadi.uncle.domain.data.UserEntity
import com.pahadi.uncle.network.data.SellerDto
import com.pahadi.uncle.network.data.UserDto

object UserEntityMapper {
    fun toEntity(userDto: UserDto, sellerDto: SellerDto): UserEntity{
        return UserEntity(
            userId = userDto.userId,
            sellerId = sellerDto.profileId,
            userName = userDto.username,
                email = userDto.email,
            phoneNumber = userDto.phone,
            profile_image = userDto.profileImage!!
        )
    }
}