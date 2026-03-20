package com.fixmybill.app.presentation.screens.premium

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

private val TealPrimary = Color(0xFF0D7377)
private val SuccessGreen = Color(0xFF2EC4B6)
private val WarningOrange = Color(0xFFFF6B35)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumScreen(
    onBack: () -> Unit,
    viewModel: PremiumViewModel = hiltViewModel()
) {
    val state by viewModel.premiumState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Go Premium") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TealPrimary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Section
            Spacer(modifier = Modifier.height(8.dp))
            Icon(
                Icons.Default.WorkspacePremium,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = SuccessGreen
            )
            Text(
                text = "Unlock Pro Features",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TealPrimary
            )
            Text(
                text = "Save more with advanced AI analysis and unlimited scans",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Feature Comparison
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Feature Comparison",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Header
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text("Feature", modifier = Modifier.weight(2f), fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                        Text("Free", modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodySmall)
                        Text("Pro", modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodySmall, color = TealPrimary)
                        Text("Family", modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodySmall, color = SuccessGreen)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    val features = listOf(
                        Triple("Bill Scans/month", "3", "Unlimited") to "Unlimited",
                        Triple("AI Analysis", "Basic", "Advanced") to "Advanced",
                        Triple("Complaint Letters", "1/month", "Unlimited") to "Unlimited",
                        Triple("Bill Prediction", "No", "Yes") to "Yes",
                        Triple("Multi-language", "English", "All") to "All",
                        Triple("Family Members", "1", "1") to "5",
                        Triple("Priority Support", "No", "Yes") to "Yes"
                    )

                    features.forEach { (triple, family) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(triple.first, modifier = Modifier.weight(2f), style = MaterialTheme.typography.bodySmall)
                            Text(triple.second, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            Text(triple.third, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodySmall, color = TealPrimary)
                            Text(family, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodySmall, color = SuccessGreen)
                        }
                    }
                }
            }

            // Plan Cards
            // Free Plan
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, Color.Gray)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Free", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Current Plan", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("₹0", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text("forever", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }

            // Pro Plan
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFF0FFFE))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        color = TealPrimary,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            "MOST POPULAR",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Pro", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TealPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("₹99", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = TealPrimary)
                        Text("/month", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    }
                    Text(
                        "₹149",
                        style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.LineThrough),
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.subscribeToPlan("pro") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                    ) {
                        Text("Subscribe to Pro")
                    }
                }
            }

            // Family Plan
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFF5FFF5))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Family", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = SuccessGreen)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("₹199", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = SuccessGreen)
                        Text("/month", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    }
                    Text(
                        "₹299",
                        style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.LineThrough),
                        color = Color.Gray
                    )
                    Text("For up to 5 family members", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.subscribeToPlan("family") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                    ) {
                        Text("Subscribe to Family")
                    }
                }
            }

            // Money-back guarantee
            Text(
                text = "7-day free trial. Cancel anytime.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
