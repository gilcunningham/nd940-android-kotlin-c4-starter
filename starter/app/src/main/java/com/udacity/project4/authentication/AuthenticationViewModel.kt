package com.udacity.project4.authentication

import android.app.Activity
import android.app.Fragment
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.udacity.project4.R

class AuthenticationViewModel: ViewModel() {

    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTHENTICATION
    }

    val firebaseLoginIntent by lazy {
        AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setTheme(R.style.GreenTheme)
            .setAvailableProviders(
                arrayListOf(
                    // Give users the option to sign in / register with their email
                    // If users choose to register with their email,
                    // they will need to create a password as well
                    AuthUI.IdpConfig.EmailBuilder().build()
                )
            ).build()
    }

    private var signInFlowLaunched = false

    val authenticationState = FirebaseUserLiveData().map { user ->
        if (user != null) {
            signInFlowLaunched = false
            AuthenticationState.AUTHENTICATED
        } else {
            if (signInFlowLaunched) {
                AuthenticationState.INVALID_AUTHENTICATION
            } else {
                AuthenticationState.UNAUTHENTICATED
            }
        }
    }

    fun launchSignInFlow(loginLauncher: ActivityResultLauncher<Intent>) {
        loginLauncher.launch(firebaseLoginIntent)
        signInFlowLaunched = true
    }
}