package com.pahadi.uncle.presentation.settings

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.pahadi.uncle.R

data class SettingsItem(
    @StringRes val titleResId: Int,
    @DrawableRes val icon: Int,
    val directionId: Int
)

val allSettings = listOf(
    SettingsItem(
        R.string.about_us,
        R.drawable.ic_about_us_icon,
        R.id.aboutUsFragment
    ),
    SettingsItem(
        R.string.privacy_policy,
        R.drawable.ic_privacy_policy_icon,
        R.id.privacyPolicyFragment
    ),
    SettingsItem(
        R.string.terms_and_conditions,
        R.drawable.ic_terms___condition_icon,
        R.id.termsAndConditionsFragment
    ),
    SettingsItem(
        R.string.contact_us,
        R.drawable.ic_contact_us_icon,
        R.id.contactUsFragment
    ),
    SettingsItem(
        R.string.my_products,
        R.drawable.ic_my_product_icon,
        R.id.myProductsFragment
    ),
    SettingsItem(
        R.string.my_orders,
        R.drawable.my_orders,
        R.id.my_Orders
    ),
    SettingsItem(
        R.string.cart,
        R.drawable.shopping_cart,
        R.id.my_Cart
    ),
    SettingsItem(
        R.string.add_address,
        R.drawable.add_address,
        R.id.add_Address
    ),
    SettingsItem(
        R.string.logout,
        R.drawable.ic_logout_icon,
        R.id.logoutDialogFragment
    )      ,
    SettingsItem(
        R.string.notification ,
        R.drawable.ic_baseline_notifications_24 ,
        R.id.notificationFragment
    )
)
