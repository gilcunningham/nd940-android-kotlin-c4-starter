package com.udacity.project4.authentication

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.firebase.ui.auth.AuthUI
import com.udacity.project4.R

class AuthenticationViewModel : ViewModel() {

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

    private val _onLoginClicked = MutableLiveData(false)
    val onLoginClicked: LiveData<Boolean> = _onLoginClicked

    val isWelcomeTextVisible = MutableLiveData(true)
    val isProgressBarVisible = MutableLiveData(true)

    val authenticationState = FirebaseUserLiveData().map { user ->
        if (user != null) {
            println("*** welcome back ${user.email}")
            //signInFlowLaunched = false
            isWelcomeTextVisible.postValue(false)
            isProgressBarVisible.postValue(true)

            AuthenticationState.AUTHENTICATED

            // show progress bar

        } else {
            println("*** auth state")
            isWelcomeTextVisible.postValue(true)
            isProgressBarVisible.postValue(false)


            //welcomeTextVisibility.postValue(View.VISIBLE)
            //doingWorkVisibility.postValue(View.GONE)
            // hide progress bar

            if (signInFlowLaunched) {
                AuthenticationState.INVALID_AUTHENTICATION
            } else {
                AuthenticationState.UNAUTHENTICATED
            }
        }
    }

    fun launchSignInFlow(loginLauncher: ActivityResultLauncher<Intent>) {
        loginLauncher.launch(firebaseLoginIntent)
        onSignInFlowLaunched()
    }

    private fun onSignInFlowLaunched() {
        signInFlowLaunched = true
        _onLoginClicked.value = false
    }

    fun loginClicked() {
        println("*** on login click")
        _onLoginClicked.value = true
    }
}