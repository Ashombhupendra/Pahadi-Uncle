package com.pahadi.uncle.presentation.login

enum class AuthType {
    /**
     * User with the provided number already exists
     * and device id provided matches the stored device id of the user
     * user can be logged in, in this case only
     */
    EXISTING_USER,

    DIFFERENT_UNIQ_ID,

    /**
     * no account with the provided number was found,
     * sign up is required in this case
     */
    NEW_USER,

    /**
     * user with the provided number exists,
     * but the device id provided is different from the device id provided during sign up
     * user can login only if the device id is same as the one provided during sign up.(i.e. the device should be same)
     */
    DIFFERENT_DEVICE
}
