package com.pahadi.uncle.presentation

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.pahadi.uncle.R
import com.pahadi.uncle.domain.repositories.AboutUsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WebViewFragment : Fragment(R.layout.fragment_web_view) {
    private lateinit var webView: WebView
    private lateinit var linearProgressIndicator: LinearProgressIndicator

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webView = view.findViewById(R.id.web_view)
        linearProgressIndicator = view.findViewById(R.id.progress_indicator)

        val destinationId = findNavController().currentDestination?.id
        lifecycleScope.launch(Dispatchers.IO) {
            val html = when (destinationId) {
                R.id.aboutUsFragment -> AboutUsRepository.getAboutUs()
                R.id.privacyPolicyFragment -> AboutUsRepository.getPrivacyPolicy()
                R.id.termsAndConditionsFragment -> AboutUsRepository.getTermsAndConditions()
                else -> "Some Error Occurred"
            }
            withContext(Dispatchers.Main){
                loadHtmlData(html)
                linearProgressIndicator.visibility = View.GONE
            }
        }
    }

    private fun loadHtmlData(html: String) {
        webView.loadData(
            """<html>
                        <body>
                            <font color='black'>
                                $html
                            </font>
                        </body>
                    </html>
                    """.trimIndent(),
            "text/html; charset=UTF-8", null
        )
    }
}
