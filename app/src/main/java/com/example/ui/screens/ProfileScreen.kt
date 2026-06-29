package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AeinzBackground
import com.example.ui.components.AeinzCard
import com.example.ui.viewmodel.AeinzViewModel
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    viewModel: AeinzViewModel
) {
    val user by viewModel.userState.collectAsState()
    val transactions by viewModel.transactionsState.collectAsState()
    
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    AeinzBackground {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Header
            item {
                Text(
                    text = "My Profile",
                    color = TextPrimaryLight,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // User Info Header Card
            item {
                AeinzCard(borderGlow = true) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar placeholder with Orange glow
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(PrimaryOrange.copy(alpha = 0.15f), CircleShape)
                                .border(2.dp, PrimaryOrange, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile Avatar",
                                tint = PrimaryOrange,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column {
                            Text(
                                text = user?.name ?: "User Profile",
                                color = TextPrimaryLight,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = user?.email ?: "noemail@aeinz.com",
                                color = TextSecondaryLight,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Badge(
                                containerColor = PrimaryOrange.copy(alpha = 0.2f),
                                contentColor = PrimaryOrange
                            ) {
                                Text(
                                    text = "Wallet Active",
                                    color = PrimaryOrange,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Wallet Details & Connected Networks
            item {
                AeinzCard {
                    Text(
                        text = "Linked Mobile Network",
                        color = TextPrimaryLight,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFFF1F5F9), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhoneAndroid,
                                    contentDescription = "Phone",
                                    tint = PrimaryOrange,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = user?.mobileNetwork ?: "Mobile Money Link",
                                    color = TextPrimaryLight,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = user?.phone ?: "07...",
                                    color = TextSecondaryLight,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Badge(
                            containerColor = EmeraldGreen.copy(alpha = 0.15f),
                            contentColor = EmeraldGreen
                        ) {
                            Text(
                                text = "VERIFIED",
                                color = EmeraldGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Customer Support segment (WhatsApp & Email)
            item {
                AeinzCard(borderGlow = true) {
                    Text(
                        text = "Aeinz Customer Support",
                        color = TextPrimaryLight,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Have issues or inquiries? Get in touch with our support lines instantly.",
                        color = TextSecondaryLight,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 14.dp)
                    )

                    // WhatsApp Support row
                    SupportRow(
                        title = "WhatsApp Support (Chat Only)",
                        value = "0747944559",
                        icon = Icons.Default.Chat,
                        onClick = {
                            try {
                                val uri = Uri.parse("https://api.whatsapp.com/send?phone=256747944559")
                                val intent = Intent(Intent.ACTION_VIEW, uri)
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                clipboardManager.setText(AnnotatedString("0747944559"))
                                Toast.makeText(context, "Number copied to clipboard!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Email Support row
                    SupportRow(
                        title = "Email Support Channel",
                        value = "carlvinaeinz5@gmail.com",
                        icon = Icons.Default.Email,
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:carlvinaeinz5@gmail.com")
                                    putExtra(Intent.EXTRA_SUBJECT, "Aeinz Pocket Growth Support Inquire")
                                }
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                clipboardManager.setText(AnnotatedString("carlvinaeinz5@gmail.com"))
                                Toast.makeText(context, "Support Email copied to clipboard!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }

            // Transaction History block Title
            item {
                Text(
                    text = "Full Statement History",
                    color = TextPrimaryLight,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Full transaction histories
            if (transactions.isEmpty()) {
                item {
                    AeinzCard {
                        Text(
                            text = "No statements logged yet.",
                            color = TextSecondaryLight,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            } else {
                items(transactions) { tx ->
                    TransactionItem(tx = tx, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun SupportRow(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF1F5F9))
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryOrange,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, color = TextPrimaryLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(value, color = TextSecondaryLight, fontSize = 12.sp)
            }
        }
        Icon(
            imageVector = Icons.Default.Launch,
            contentDescription = "Open Support Link",
            tint = PrimaryOrange,
            modifier = Modifier.size(14.dp)
        )
    }
}
