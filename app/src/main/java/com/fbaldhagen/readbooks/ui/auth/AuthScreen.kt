package com.fbaldhagen.readbooks.ui.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fbaldhagen.readbooks.domain.usecase.AuthStatus
import com.fbaldhagen.readbooks.ui.auth.components.AuthHeader
import com.fbaldhagen.readbooks.ui.auth.components.EmailVerificationPending
import com.fbaldhagen.readbooks.ui.auth.components.LoginForm
import com.fbaldhagen.readbooks.ui.auth.components.RegisterForm

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var isLoginMode by remember { mutableStateOf(true) }

    LaunchedEffect(state.authStatus) {
        if (state.authStatus == AuthStatus.AUTHENTICATED || state.authStatus == AuthStatus.GUEST) {
            onAuthSuccess()
        }
    }

    if (state.registrationPending) {
        EmailVerificationPending(
            email = state.email,
            onBackToSignIn = {
                viewModel.dismissRegistrationPending()
                isLoginMode = true
            }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AuthHeader(
            subtitle = if (isLoginMode) "Welcome back" else "Create an account"
        )

        Spacer(modifier = Modifier.height(32.dp))

        AnimatedContent(
            targetState = isLoginMode,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "auth_form"
        ) { loginMode ->
            if (loginMode) {
                LoginForm(
                    email = state.email,
                    password = state.password,
                    isLoading = state.isLoading,
                    error = state.error,
                    onEmailChange = viewModel::onEmailChange,
                    onPasswordChange = viewModel::onPasswordChange,
                    onLogin = viewModel::login,
                    onNavigateToRegister = {
                        isLoginMode = false
                        viewModel.dismissError()
                    },
                    onContinueAsGuest = viewModel::onContinueAsGuest
                )
            } else {
                RegisterForm(
                    email = state.email,
                    password = state.password,
                    confirmPassword = state.confirmPassword,
                    passwordsMatch = state.passwordsMatch,
                    isLoading = state.isLoading,
                    error = state.error,
                    onEmailChange = viewModel::onEmailChange,
                    onPasswordChange = viewModel::onPasswordChange,
                    onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
                    onRegister = viewModel::register,
                    onNavigateToLogin = {
                        isLoginMode = true
                        viewModel.dismissError()
                        viewModel.clearPasswordConfirmation()
                    }
                )
            }
        }
    }
}