package com.aureliajets.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aureliajets.ui.components.*
import com.aureliajets.viewmodel.AureliaViewModel
import com.aureliajets.viewmodel.AuthState

@Composable
fun LoginScreen(
    viewModel: AureliaViewModel,
    onLoginSuccess: () -> Unit,
    onSignUp: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                onLoginSuccess()            // navigate first
                viewModel.resetAuthState()  // reset after
            }
            is AuthState.Error -> {
                errorMsg = (authState as AuthState.Error).message
            }
            else -> {}
        }
    }

    AureliaBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(Modifier.height(60.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    modifier = Modifier.size(70.dp),
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFF0D1B3E)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("✈", fontSize = 30.sp, color = Color.White)
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    "Welcome",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AureliaTextField(
                    value = username,
                    onValueChange = { username = it; errorMsg = "" },
                    placeholder = "Username"
                )

                AureliaTextField(
                    value = password,
                    onValueChange = { password = it; errorMsg = "" },
                    placeholder = "Password",
                    isPassword = true
                )

                if (errorMsg.isNotEmpty()) {
                    Text(errorMsg, color = Color.Yellow, fontSize = 13.sp)
                }

                Spacer(Modifier.height(8.dp))

                AureliaButton(
                    text = if (authState is AuthState.Loading) "Logging in..." else "Login",
                    onClick = {
                        errorMsg = ""
                        if (username.isBlank() || password.isBlank()) {
                            errorMsg = "Please fill in all fields"
                        } else {
                            viewModel.login(username.trim(), password)
                        }
                    },
                    enabled = authState !is AuthState.Loading
                )

                Spacer(Modifier.height(8.dp))

                // Clickable "sign up" text
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        buildAnnotatedString {
                            append("Don't Have Account? ")
                            withStyle(SpanStyle(color = Color(0xFF42A5F5), fontWeight = FontWeight.Bold)) {
                                append("sign up")
                            }
                        },
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { onSignUp() }
                    )
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}
