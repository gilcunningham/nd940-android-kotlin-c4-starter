package com.udacity.project4.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

class AuthenticationViewModel: ViewModel() {

    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTHENTICATION
    }

    private var signInFlowLaunched = false

    var authenticationState = FirebaseUserLiveData().map { user ->
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

    fun onSignInFlowLaunched() {
        signInFlowLaunched = true
    }
}