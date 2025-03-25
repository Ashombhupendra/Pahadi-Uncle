package com.pahadi.uncle.presentation

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.gson.Gson
import com.pahadi.uncle.PahadiUncleApplication
import com.pahadi.uncle.R
import com.pahadi.uncle.databinding.ActivityMainBinding
import com.pahadi.uncle.domain.ResultWrapper
import com.pahadi.uncle.domain.repositories.ChatRepository
import com.pahadi.uncle.domain.utils.SharedPrefHelper
import com.pahadi.uncle.domain.utils.UniqueDeviceId
import com.pahadi.uncle.presentation.home.HomeScreenFragment
import com.pahadi.uncle.presentation.home.HomeViewModel
import com.pahadi.uncle.presentation.my_orders.OrderViewModel
//import com.razorpay.PaymentResultListener
//import eu.dkaratzas.android.inapp.update.Constants.UpdateMode
//import eu.dkaratzas.android.inapp.update.InAppUpdateManager
//import eu.dkaratzas.android.inapp.update.InAppUpdateStatus
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() /*, PaymentResultListener*/ {

    private val mOrderViewmodel by lazy {
        ViewModelProvider(this).get(OrderViewModel::class.java)
    }
    private lateinit var mBinding: ActivityMainBinding

    private lateinit var mNavController: NavController
    var doubleBackToExitPressedOnce: Boolean = false
    var boolean: Boolean = true
    var context = this
    private val appUpdateManager : AppUpdateManager by lazy { AppUpdateManagerFactory.create(this) }
//    private lateinit var inAppUpdateManager: InAppUpdateManager
    val MY_UPDATE_REQUEST_CODE : Int = 11
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "i.apps.notifications"
    private val description = "Test notification"
    private val mHomeViewModel by lazy {
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    companion object {
        var pro: Int = 0
        var notificationtype : Int = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.PahadiTheme)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        mNavController = findNavController(R.id.fragment)

//        appUpdateManager.registerListener {
//            if (it.installStatus() == InstallStatus.DOWNLOADED){
//                appUpdateManager.completeUpdate()
//
//            }
//        }

        appudatedialog()


        val uri = intent.data
        if (uri != null) {
            val params: List<String> = uri.pathSegments
            val id = params.get(params.size - 1)

            mHomeViewModel.shareid.value = id

        }

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //    showNotification("PahadiUncle", "Message from Admin ")
        Log.d("UNIQ", UniqueDeviceId.getUniqueId())
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.homeScreenFragment))
        mBinding.toolbar.setupWithNavController(mNavController, appBarConfiguration)

        setSupportActionBar(mBinding.toolbar)

        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {

                if (SharedPrefHelper.isLoggedIn) {
                    mBinding.agentLogin.visibility = View.INVISIBLE
                    mBinding.hNotification.visibility = View.VISIBLE
                    mBinding.icFav.visibility = View.VISIBLE
                    mBinding.hShoppingCart.visibility = View.VISIBLE
                    if (SharedPrefHelper.getNotificationAlert().equals("ALERT")) {
                        mBinding.hAlertNotification.visibility = View.VISIBLE
                    } else {
                        mBinding.hAlertNotification.visibility = View.INVISIBLE
                    }

                } else {
                    mBinding.agentLogin.visibility = View.VISIBLE
                   mBinding.hNotification.visibility = View.INVISIBLE
                    mBinding.hShoppingCart.visibility = View.INVISIBLE
                    mBinding.hAlertNotification.visibility = View.INVISIBLE
                    mBinding.icFav.visibility = View.INVISIBLE
                }



                if (SharedPrefHelper.isLoggedIn) {
                    if (PahadiUncleApplication.instance.isConnectedToInternet()) {
                        val user = SharedPrefHelper.user
                        lifecycleScope.launch {
                            val result = ChatRepository.chatalert(user.userId)
                            when (result) {
                                is ResultWrapper.Success -> {
                                    val status = result.response.status
                                    val message = result.response.message
                                    Log.d("CHAT", status.toString() + message.toString())
                                    if (status.equals("true") && message.equals("enable")) {

                                        mBinding.bottomHAlertNotification.visibility = View.VISIBLE


                                    } else {
                                        mBinding.bottomHAlertNotification.visibility = View.INVISIBLE

                                    }


                                }
                                is ResultWrapper.Failure -> {
                                        Log.d("error", result.errorMessage)
                                }
                            }
                        }
                    }
                }




                        handler.postDelayed(this, 5000)
            }
        }, 1000)

        mBinding.agentLogin.setOnClickListener {

            findNavController(R.id.fragment).navigate(R.id.agentLoginFragment)
        }
        mBinding.hNotification.setOnClickListener {

               val bundle = Bundle()
            bundle.putString("notify_type", "main")

            findNavController(R.id.fragment).navigate(R.id.notificationFragment, bundle)
        }
        mBinding.icFav.setOnClickListener {
            findNavController(R.id.fragment).navigate(R.id.wishList)
        }

         mBinding.hShoppingCart.setOnClickListener {
             Toast.makeText(this, "Cart View", Toast.LENGTH_SHORT).show()
             if (SharedPrefHelper.isLoggedIn) {
                 findNavController(R.id.fragment).navigate(R.id.my_Cart)
             } else {
                 findNavController(R.id.fragment).navigate(R.id.loginFragment)
             }
         }

        mBinding.homeSettings.setOnClickListener {
            if (SharedPrefHelper.isLoggedIn) {
                findNavController(R.id.fragment).navigate(R.id.settingsFragment)
            } else {
                findNavController(R.id.fragment).navigate(R.id.loginFragment)
            }

        }

        mBinding.homeChat.setOnClickListener {
            it.isFocused
            if (SharedPrefHelper.isLoggedIn) {
                if (SharedPrefHelper.isLoggedIn) {
                    findNavController(R.id.fragment).navigate(R.id.chatListFragment)
                } else {
                    findNavController(R.id.fragment).navigate(R.id.loginFragment)
                }
            }else{
                findNavController(R.id.fragment).navigate(R.id.loginFragment)
            }
        }
        /*  mBinding.floatingBtn.setOnClickListener {
              if(HomeScreenFragment.sellerlocationstatus.equals(true)){
                  val extras = FragmentNavigatorExtras(it to "circular_reveal")
                  findNavController(R.id.fragment).navigate(R.id.sellProductFragment, null, null, extras)
              }else{
                  findNavController(R.id.fragment).navigate(R.id.sellerLocationFragment)
              }
          }*/
        mBinding.homeAccount.setOnClickListener {
            if (SharedPrefHelper.isLoggedIn) {
               /* if (HomeScreenFragment.sellerlocationstatus.equals(true)) {
                    // val extras = FragmentNavigatorExtras(it to "circular_reveal")
                    findNavController(R.id.fragment).navigate(R.id.profileFragment)
                } else {
                    findNavController(R.id.fragment).navigate(R.id.sellerLocationFragment)
                }*/
                findNavController(R.id.fragment).navigate(R.id.profileFragment)
            }else{
                findNavController(R.id.fragment).navigate(R.id.loginFragment)
            }
        }

        val intent = intent
        val inss: String? = intent.getStringExtra("logout")
//        if (inss.equals("log")){
//            mBinding.agentLogin.visibility = View.VISIBLE
//
//            mNavController.addOnDestinationChangedListener { _, destination, _ ->
//                R.id.homeScreenFragment
//
//
//            }
//        }
        mNavController.addOnDestinationChangedListener { _, destination, _ ->
            mBinding.appBar.isGone = destination.id in setOf(
                R.id.splashFragment,
                R.id.loginFragment,
                R.id.registrationFragment,
                R.id.otpFragment
            )
            mBinding.toolbarLogo.isVisible = destination.id == R.id.homeScreenFragment


            mBinding.homeBottomAppBar.isVisible = destination.id in setOf(
                R.id.homeScreenFragment,
                R.id.settingsFragment,
                R.id.chatListFragment,
                R.id.profileFragment
            )
            mBinding.floatingBtn.isVisible = destination.id in setOf(
                R.id.homeScreenFragment,
                R.id.settingsFragment,
                R.id.chatListFragment,
                R.id.profileFragment
            )

            if (destination.id == R.id.agentDashboardFragment){
                notificationtype = 1
            }else{
                notificationtype = 0
            }

            mBinding.cvHome.isVisible = destination.id in setOf(
                R.id.homeScreenFragment,
                R.id.settingsFragment,
                R.id.chatListFragment,
                R.id.profileFragment
            )
            mBinding.homeWallShare.setOnClickListener {
                if (mHomeViewModel.cateitem.value != -1 && destination.id == R.id.homeScreenFragment) {
                    findNavController(R.id.fragment).popBackStack(R.id.homeScreenFragment, true)
                    findNavController(R.id.fragment).navigate(R.id.homeScreenFragment)
                } else if (destination.id != R.id.homeScreenFragment) {
                    findNavController(R.id.fragment).navigate(R.id.homeScreenFragment)
                }
            }
            mBinding.floatingBtn.setOnClickListener { view: View ->
                if (SharedPrefHelper.isLoggedIn) {
                    if (HomeScreenFragment.sellerlocationstatus.equals(true)) {

                        findNavController(R.id.fragment).navigate(R.id.sellProductFragment)
                    } else {
                        findNavController(R.id.fragment).navigate(R.id.sellerLocationFragment)
                    }
                }else{
                    findNavController(R.id.fragment).navigate(R.id.loginFragment)
                }
            }



            when (destination.id) {
                R.id.homeScreenFragment -> {
                    mBinding.homeWallShare.setImageResource(R.drawable.ic_home_icon_30)
                   // Glide.with(PahadiUncleApplication.instance.applicationContext).load(R.drawable.ic_home_icon_30).into(mBinding.homeWallShare)
                    mBinding.homeAccount.setImageResource(R.drawable.ic_account_icon)
                 /*   Glide.with(PahadiUncleApplication.instance.applicationContext).load(R.drawable.ic_account_icon).into(mBinding.homeAccount)
                    Glide.with(PahadiUncleApplication.instance.applicationContext).load(R.drawable.ic_chat_icon).into(mBinding.homeChat)
                    Glide.with(PahadiUncleApplication.instance.applicationContext).load(R.drawable.ic_setting_icon).into(mBinding.homeSettings)
*/
                    mBinding.homeChat.setImageResource(R.drawable.ic_chat_icon)
                    mBinding.homeSettings.setImageResource(R.drawable.ic_setting_icon)
                }
                R.id.chatListFragment -> {
                    mBinding.homeWallShare.setImageResource(R.drawable.ic_home_icon)
                    mBinding.homeAccount.setImageResource(R.drawable.ic_account_icon)
                    mBinding.homeChat.setImageResource(R.drawable.ic_chat_icon_30)
                    mBinding.homeSettings.setImageResource(R.drawable.ic_setting_icon)
                }
                R.id.settingsFragment -> {
                    mBinding.homeWallShare.setImageResource(R.drawable.ic_home_icon)
                    mBinding.homeAccount.setImageResource(R.drawable.ic_account_icon)
                    mBinding.homeChat.setImageResource(R.drawable.ic_chat_icon)
                    mBinding.homeSettings.setImageResource(R.drawable.ic_setting_icon_30)
                }
                R.id.profileFragment -> {
                    mBinding.homeWallShare.setImageResource(R.drawable.ic_home_icon)
                    mBinding.homeAccount.setImageResource(R.drawable.ic_account_icon_30)
                    mBinding.homeChat.setImageResource(R.drawable.ic_chat_icon)
                    mBinding.homeSettings.setImageResource(R.drawable.ic_setting_icon)
                }
            }

        }


    }

    override fun onSupportNavigateUp(): Boolean {
        return mNavController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        super.onBackPressed()
//        if (doubleBackToExitPressedOnce) {
//            // super.onBackPressed()
//           exitalertdialog()
//            return
//        }
//        doubleBackToExitPressedOnce = true
//
//
//         super.onBackPressed()
//
//        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 1000)
    }

    private fun showAlertDialog() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(
            this,
            com.google.android.material.R.style.Theme_MaterialComponents_Dialog_Alert
        )
        alertDialog.setTitle("Exit Alert")
        // set alert dialog message text color
        val message = SpannableString("Are you sure you want exit PahadiUncle")
        message.setSpan(
            ForegroundColorSpan(Color.BLACK),
            0,
            message.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        alertDialog.setMessage(message)
        alertDialog.setPositiveButton(
            "yes"
        ) { _, _ ->
            finish()
        }
        alertDialog.setNegativeButton(
            "No"
        ) { _, _ -> }
        val alert: AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }

    private fun exitalertdialog() {
        SweetAlertDialog(this@MainActivity)
            .setTitleText("Exit Alert")
            .setContentText("Are you sure you want exit PahadiUncle")
            .setCancelText("NO")
            .setConfirmText("SURE")

            .setConfirmButton("SURE", SweetAlertDialog.OnSweetClickListener {
                // Toast.makeText(requireContext(),"LOGOUT",Toast.LENGTH_SHORT).show()
                it.setCanceledOnTouchOutside(false)

            })
            .apply { setCanceledOnTouchOutside(false) }

            .show()


    }


    @SuppressLint("QueryPermissionsNeeded")
    fun shareproducts(message: String) {

        // Creating intent with action send
        val intent = Intent(Intent.ACTION_SEND)

        // Setting Intent type
        intent.type = "text/plain"

        // Setting whatsapp package name
        intent.setPackage("com.whatsapp")

        // Give your message here
        intent.putExtra(Intent.EXTRA_TEXT, message)
        val your_apppackagename = "com.whatsapp"

        try {
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$your_apppackagename")
                )
            )
        }


    }

    private fun appInstalledOrNot(): Boolean {

        try {
            packageManager.getPackageInfo("com.whatsapp", 0).applicationInfo
            return true
        } catch (e: PackageManager.NameNotFoundException) {
            return false
        }

    }


    private fun appudatedialog(){
      appUpdateManager.appUpdateInfo.addOnSuccessListener {
          if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && it.isUpdateTypeAllowed(
                  AppUpdateType.FLEXIBLE))
          {
              appUpdateManager.startUpdateFlowForResult(it, AppUpdateType.FLEXIBLE, this, MY_UPDATE_REQUEST_CODE)
          }
      }.addOnFailureListener {
          Log.d("Errorupdate", "Error : $it")
      }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


    }

    override fun onResume() {
        super.onResume()
                 appUpdateManager.appUpdateInfo.addOnSuccessListener {
                     if (it.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS )
                     {
                         appUpdateManager.startUpdateFlowForResult(it, AppUpdateType.FLEXIBLE ,this , MY_UPDATE_REQUEST_CODE)
                     }
                 }
    }




    private fun showsnack(){
        val rootView = window.decorView.findViewById<View>(android.R.id.content)
        val snackbar = Snackbar.make(
            rootView,
            "An update has just been downloaded.",
            Snackbar.LENGTH_INDEFINITE
        )
        snackbar.setAction("Install") { view: View? ->

            // Triggers the completion of the update of the app for the flexible flow.

        }
        snackbar.show()
    }

//    override fun onPaymentSuccess(razorpayPaymentID: String?) {
//
//        try {
//            Toast.makeText(this, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
//            val gson = Gson()
//            val liststring = gson.toJson(mOrderViewmodel.selectProductlist)
//            mOrderViewmodel.paymentmode.value = "Prepaid"
//            mOrderViewmodel.paymentID.value = "$razorpayPaymentID"
//            mOrderViewmodel.doTransaction(liststring)
//
//            paymentSuccessDialog()
//        } catch (e : java.lang.Exception) {
//            Log.e("MainActivity", "Exception in onPaymentSuccess", e);
//        }
//    }
//
//    override fun onPaymentError(code: Int, response: String?) {
//        try {
//             PaymentFailedDailog()
//
//        } catch (e: java.lang.Exception) {
//            Log.e("MainActivity", "Exception in onPaymentError", e)
//        }
//    }

   private fun paymentSuccessDialog(){
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_interst_dialog, null)
        //AlertDialogBuilder
        val mBuilder = androidx.appcompat.app.AlertDialog.Builder(this, R.style.dialog_background)
            .setView(mDialogView)
        val mAlertDialog = mBuilder.create()
        mAlertDialog.show()
        val btn = mDialogView.findViewById<Button>(R.id.success_ok)
        val text = mDialogView.findViewById<TextView>(R.id.payment_dialog_text)
        text.setText("Payment was successfully done")
        btn.setOnClickListener {
            val navOptions = NavOptions.Builder().setPopUpTo(R.id.my_Orders, true).build()

            findNavController(R.id.fragment).navigate(R.id.my_Orders, null, navOptions)
            mAlertDialog.cancel()
        }
    }

    fun PaymentFailedDailog(){
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_error_dialog, null)
        //AlertDialogBuilder
        val mBuilder = androidx.appcompat.app.AlertDialog.Builder(this, R.style.dialog_background)
            .setView(mDialogView)
        val mAlertDialog = mBuilder.create()
        mAlertDialog.show()
        val btn = mDialogView.findViewById<Button>(R.id.success_ok)
        val text = mDialogView.findViewById<TextView>(R.id.payment_dialog_text)


        text.setText("Payment was failed try again later to order product")
        btn.setOnClickListener {

            mAlertDialog.cancel()
        }
    }

}
