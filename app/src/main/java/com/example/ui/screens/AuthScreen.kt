package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AeinzBackground
import com.example.ui.components.AeinzButton
import com.example.ui.components.AeinzCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onRegisterSuccess: (String, String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var selectedNetwork by remember { mutableStateOf("MTN Mobile Money") }
    
    var nameError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }
    var pinError by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    AeinzBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            
            // App Branding
            Text(
                text = "Aeinz Pocket Growth",
                color = PrimaryOrange,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
            )
            Text(
                text = "Join over 10,000+ Ugandan Earners",
                color = TextSecondaryLight,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 28.dp)
            )

            // Input Card
            AeinzCard(borderGlow = true) {
                Text(
                    text = "Create Free Account",
                    color = TextPrimaryLight,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Name Input
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; nameError = false },
                    label = { Text("Full Name") },
                    leadingIcon = { Icon(Icons.Default.Person, "Name", tint = PrimaryOrange) },
                    singleLine = true,
                    isError = nameError,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryOrange,
                        unfocusedBorderColor = Color(0xFFCBD5E1),
                        focusedLabelColor = PrimaryOrange,
                        unfocusedLabelColor = TextSecondaryLight,
                        focusedTextColor = TextPrimaryLight,
                        unfocusedTextColor = TextPrimaryLight
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_name_field"),
                    shape = RoundedCornerShape(12.dp)
                )
                if (nameError) {
                    Text("Name cannot be empty", color = CrimsonRed, fontSize = 12.dp.value.sp, modifier = Modifier.padding(start = 4.dp, top = 2.dp))
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Email Input
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    leadingIcon = { Icon(Icons.Default.Email, "Email", tint = PrimaryOrange) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryOrange,
                        unfocusedBorderColor = Color(0xFFCBD5E1),
                        focusedLabelColor = PrimaryOrange,
                        unfocusedLabelColor = TextSecondaryLight,
                        focusedTextColor = TextPrimaryLight,
                        unfocusedTextColor = TextPrimaryLight
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_email_field"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Phone Input (Mobile Money Linkage)
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it; phoneError = false },
                    label = { Text("Ugandan Phone No (07...)") },
                    leadingIcon = { Icon(Icons.Default.Phone, "Phone", tint = PrimaryOrange) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    isError = phoneError,
                    placeholder = { Text("e.g. 0747944559") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryOrange,
                        unfocusedBorderColor = Color(0xFFCBD5E1),
                        focusedLabelColor = PrimaryOrange,
                        unfocusedLabelColor = TextSecondaryLight,
                        focusedTextColor = TextPrimaryLight,
                        unfocusedTextColor = TextPrimaryLight
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_phone_field"),
                    shape = RoundedCornerShape(12.dp)
                )
                if (phoneError) {
                    Text("Enter a valid Ugandan phone number", color = CrimsonRed, fontSize = 12.dp.value.sp, modifier = Modifier.padding(start = 4.dp, top = 2.dp))
                }

                Spacer(modifier = Modifier.height(14.dp))

                // PIN Input
                OutlinedTextField(
                    value = pin,
                    onValueChange = { if (it.length <= 4) { pin = it; pinError = false } },
                    label = { Text("4-Digit Wallet PIN") },
                    leadingIcon = { Icon(Icons.Default.Lock, "PIN", tint = PrimaryOrange) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    isError = pinError,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryOrange,
                        unfocusedBorderColor = Color(0xFFCBD5E1),
                        focusedLabelColor = PrimaryOrange,
                        unfocusedLabelColor = TextSecondaryLight,
                        focusedTextColor = TextPrimaryLight,
                        unfocusedTextColor = TextPrimaryLight
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_pin_field"),
                    shape = RoundedCornerShape(12.dp)
                )
                if (pinError) {
                    Text("PIN must be exactly 4 digits", color = CrimsonRed, fontSize = 12.dp.value.sp, modifier = Modifier.padding(start = 4.dp, top = 2.dp))
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Network Selector
                Text(
                    text = "Preferred Mobile Money Network",
                    color = TextPrimaryLight,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    listOf("MTN Mobile Money", "Airtel Money").forEach { network ->
                        val isSelected = selectedNetwork == network
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) PrimaryOrange.copy(alpha = 0.15f) else Color(0xFFF1F5F9))
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) PrimaryOrange else Color(0xFFE2E8F0),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedNetwork = network }
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = network,
                                color = if (isSelected) PrimaryOrange else TextSecondaryLight,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Submit Button
                AeinzButton(
                    text = "REGISTER & AUTO-LOGIN",
                    onClick = {
                        val isPhoneValid = phone.trim().length >= 9
                        val isPinValid = pin.trim().length == 4

                        if (name.trim().isEmpty()) nameError = true
                        if (!isPhoneValid) phoneError = true
                        if (!isPinValid) pinError = true

                        if (name.trim().isNotEmpty() && isPhoneValid && isPinValid) {
                            onRegisterSuccess(name, email, phone, pin, selectedNetwork)
                        }
                    },
                    testTag = "auth_submit_button"
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
