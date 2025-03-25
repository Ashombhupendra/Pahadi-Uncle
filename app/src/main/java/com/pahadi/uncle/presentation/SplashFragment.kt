package com.pahadi.uncle.presentation

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import androidx.core.animation.addListener
import androidx.navigation.fragment.findNavController
import com.pahadi.uncle.R

class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        animateSquare()


    }

    private fun animateSquare(){
        val square = requireView().findViewById<ImageView>(R.id.square_white)
        ObjectAnimator.ofFloat(square, "scaleX", .5f).apply {
            duration = 2800
            interpolator = OvershootInterpolator()
            start()
        }
        ObjectAnimator.ofFloat(square, "scaleY", .35f).apply {
            duration = 2800
            interpolator = OvershootInterpolator()
            start()
        }
        ObjectAnimator.ofFloat(square, "rotation", -100f).apply {
            duration = 2800
            interpolator = OvershootInterpolator()
            start()
            addListener(onEnd = {
                try {
                    findNavController().navigate(R.id.action_splashFragment_to_homeScreenFragment)

                }catch (e : Exception){
                    Log.d("error", e.toString())
                }
            })
        }
    }
}
