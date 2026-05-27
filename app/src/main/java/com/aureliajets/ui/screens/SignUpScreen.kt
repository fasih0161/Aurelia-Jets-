package com.aureliajets.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aureliajets.ui.components.*
import com.aureliajets.viewmodel.AureliaViewModel
import com.aureliajets.viewmodel.AuthState

@Composable
fun SignUpScreen(
    viewModel: AureliaViewModel,
    onSignUpSuccess: () -> Unit,
    onBack: () -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var cnic by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                onSignUpSuccess()
                viewModel.resetAuthState()
            }
            is AuthState.Error -> {
                errorMsg = (authState as AuthState.Error).message
            }
            else -> {}
        }
    }

    AureliaBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButton(onClick = onBack)
                Text(
                    "Sign Up",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AureliaTextField(
                        value = firstName,
                        onValueChange = { input ->
                            if (input.all { it.isLetter() || it.isWhitespace() }) {
                                firstName = input.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                                errorMsg = ""
                            }
                        },
                        placeholder = "First Name",
                        modifier = Modifier.weight(1f)
                    )
                    AureliaTextField(
                        value = lastName,
                        onValueChange = { input ->
                            if (input.all { it.isLetter() || it.isWhitespace() }) {
                                lastName = input.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                                errorMsg = ""
                            }
                        },
                        placeholder = "Last Name",
                        modifier = Modifier.weight(1f)
                    )
                }

                AureliaTextField(
                    value = phone,
                    onValueChange = { input ->
                        val digits = input.filter { it.isDigit() }
                        if (digits.length <= 11) {
                            phone = digits
                            errorMsg = ""
                        }
                    },
                    placeholder = "Phone # (11 digits)",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                AureliaTextField(
                    value = cnic,
                    onValueChange = { input ->
                        val digits = input.filter { it.isDigit() }
                        if (digits.length <= 13) {
                            cnic = digits
                            errorMsg = ""
                        }
                    },
                    placeholder = "C.N.I.C. (XXXXX-XXXXXXX-X)",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = CnicVisualTransformation()
                )

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

                AureliaTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it; errorMsg = "" },
                    placeholder = "Confirm Password",
                    isPassword = true
                )

                if (errorMsg.isNotEmpty()) {
                    Text(
                        errorMsg,
                        color = Color.Yellow,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }

                AureliaButton(
                    text = if (authState is AuthState.Loading) "Submitting..." else "Submit",
                    onClick = {
                        errorMsg = ""
                        if (firstName.isBlank() || lastName.isBlank() || phone.isBlank() ||
                            cnic.isBlank() || username.isBlank() || password.isBlank() ||
                            confirmPassword.isBlank()) {
                            errorMsg = "Please fill in all fields"
                        } else if (phone.length != 11) {
                            errorMsg = "Phone number must be exactly 11 digits"
                        } else if (cnic.length != 13) {
                            errorMsg = "CNIC must be exactly 13 digits"
                        } else if (password != confirmPassword) {
                            errorMsg = "Passwords do not match"
                        } else if (password.length < 6) {
                            errorMsg = "Password must be at least 6 characters"
                        } else {
                            viewModel.signUp(
                                firstName.trim(), lastName.trim(),
                                username.trim(), password,
                                phone.trim(), cnic.trim()
                            )
                        }
                    },
                    enabled = authState !is AuthState.Loading
                )

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}
