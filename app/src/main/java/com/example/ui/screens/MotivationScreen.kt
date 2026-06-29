package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AeinzBackground
import com.example.ui.components.AeinzCard
import com.example.ui.viewmodel.AeinzViewModel
import com.example.ui.theme.*

@Composable
fun MotivationScreen(
    viewModel: AeinzViewModel
) {
    val tips by viewModel.tipsState.collectAsState()
    
    // Interactive Compound Calculator States
    var dailyDepositSlider by remember { mutableFloatStateOf(2000f) }
    var yearDurationSlider by remember { mutableFloatStateOf(3f) }

    val dailyAmt = dailyDepositSlider.toDouble()
    val totalYears = yearDurationSlider.toInt()
    
    // Compounding math: assume a modest 8% annual yield typical of credit union/saving societies in Uganda
    val annualRate = 0.08
    val totalDays = totalYears * 365
    val principalSaved = dailyAmt * totalDays
    
    // Compound interest calculation assuming daily deposit
    var totalValue = 0.0
    val dailyRate = annualRate / 365.0
    for (i in 1..totalDays) {
        totalValue = (totalValue + dailyAmt) * (1.0 + dailyRate)
    }
    val interestEarned = (totalValue - principalSaved).coerceAtLeast(0.0)

    val inspiringQuotes = listOf(
        "By saving small amounts of UGX daily, you build a steady ladder toward financial independence.",
        "Wealth is not about how much you make, but how much you keep and how wisely you allocate it.",
        "An emergency fund on Aeinz Pocket Growth is your shield against high-interest debt cycles.",
        "Small drops of UGX make a mighty ocean of security for your family in the future."
    )

    val activeQuote = remember { inspiringQuotes.random() }

    AeinzBackground {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            item {
                Column {
                    Text(
                        text = "Financial Literacy & Tips",
                        color = TextPrimaryLight,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Build saving habits, analyze compounds, and expand your budget knowledge",
                        color = TextSecondaryLight,
                        fontSize = 13.sp
                    )
                }
            }

            // Inspiring Quote Card
            item {
                AeinzCard(borderGlow = true) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.FormatQuote,
                            contentDescription = "Quote",
                            tint = PrimaryOrange,
                            modifier = Modifier
                                .size(36.dp)
                                .padding(end = 8.dp)
                        )
                        Column {
                            Text(
                                text = activeQuote,
                                color = TextPrimaryLight,
                                fontSize = 14.sp,
                                fontStyle = FontStyle.Italic,
                                lineHeight = 19.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "— Aeinz Financial Council",
                                color = LightOrange,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Compound Savings Calculator (Interactive Gamification)
            item {
                AeinzCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Aeinz Future Compounder",
                            color = TextPrimaryLight,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(Icons.Default.Calculate, "Calculator", tint = PrimaryOrange)
                    }
                    Text(
                        text = "Visualize how saving a small amount daily compounds over time at a standard 8% annual return.",
                        color = TextSecondaryLight,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Daily saving slider
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Daily Saving:", color = TextSecondaryLight, fontSize = 12.sp)
                            Text(
                                text = "${viewModel.formatUgx(dailyAmt)} UGX",
                                color = PrimaryOrange,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Slider(
                            value = dailyDepositSlider,
                            onValueChange = { dailyDepositSlider = it },
                            valueRange = 1000f..25000f,
                            steps = 24,
                            colors = SliderDefaults.colors(
                                thumbColor = PrimaryOrange,
                                activeTrackColor = PrimaryOrange,
                                inactiveTrackColor = Color(0xFFE2E8F0)
                            )
                        )
                    }

                    // Year Duration Slider
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Duration:", color = TextSecondaryLight, fontSize = 12.sp)
                            Text(
                                text = "$totalYears Years",
                                color = PrimaryOrange,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Slider(
                            value = yearDurationSlider,
                            onValueChange = { yearDurationSlider = it },
                            valueRange = 1f..10f,
                            steps = 8,
                            colors = SliderDefaults.colors(
                                thumbColor = PrimaryOrange,
                                activeTrackColor = PrimaryOrange,
                                inactiveTrackColor = Color(0xFFE2E8F0)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Compound Outputs
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Principal Saved Amount:", color = TextSecondaryLight, fontSize = 12.sp)
                            Text("${viewModel.formatUgx(principalSaved)} UGX", color = TextPrimaryLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Compound Growth Interest (8%):", color = TextSecondaryLight, fontSize = 12.sp)
                            Text("+${viewModel.formatUgx(interestEarned)} UGX", color = EmeraldGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        HorizontalDivider(color = Color(0xFFE2E8F0), modifier = Modifier.padding(vertical = 4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Estimated Pocket Wealth:", color = TextPrimaryLight, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("${viewModel.formatUgx(totalValue)} UGX", color = BrightGold, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Literacy Tips Title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.MenuBook, "Book", tint = PrimaryOrange, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Daily Literacy Handbooks",
                        color = TextPrimaryLight,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Display seeded literacy items
            if (tips.isEmpty()) {
                item {
                    Text(
                        text = "Loading helpful financial tips...",
                        color = TextSecondaryLight,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                items(tips) { tip ->
                    AeinzCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .background(PrimaryOrange.copy(alpha = 0.12f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Lightbulb, "Tip", tint = PrimaryOrange, modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = tip.title,
                                    color = TextPrimaryLight,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Icon(
                                imageVector = if (tip.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Fav",
                                tint = if (tip.isFavorite) CrimsonRed else TextSecondaryLight,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable { viewModel.toggleTipFavorite(tip.id, !tip.isFavorite) }
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        Text(
                            text = tip.content,
                            color = TextSecondaryLight,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                        
                        Spacer(modifier = Modifier.height(6.dp))
                        
                        Text(
                            text = "#${tip.category} • Literacy Hub",
                            color = PrimaryOrange,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
