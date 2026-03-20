package com.fixmybill.app.presentation.screens.premium

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

private val PrimaryTeal = Color(0xFF0D7377)
private val WarningOrange = Color(0xFFFF6B35)
private val SuccessGreen = Color(0xFF2EC4B6)
private val GoldColor = Color(0xFFFFD700)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumScreen(
    onBack: () -> Unit,
    viewModel: PremiumViewModel = hiltViewModel()
) {
    val uiState by viewModel.premiumState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Premium Plans",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Hero Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(PrimaryTeal, PrimaryTeal.copy(alpha = 0.8f))
                        )
                    )
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = "Premium",
                        tint = GoldColor,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Unlock Full Power",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Save more with unlimited scans, AI analysis, and complaint generation",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.85f),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Feature Comparison
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Feature Comparison",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                FeatureComparisonRow("Bill Scans", "3/month", "Unlimited", "Unlimited")
                FeatureComparisonRow("AI Analysis", "Basic", "Advanced", "Advanced")
                FeatureComparisonRow("Complaint Letters", "1 type", "All types", "All types")
                FeatureComparisonRow("History", "30 days", "Unlimited", "Unlimited")
                FeatureComparisonRow("Family Members", "-", "-", "Up to 5")
                FeatureComparisonRow("Priority Support", "-", "Yes", "Yes")
                FeatureComparisonRow("Export Reports", "-", "PDF", "PDF & Excel")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Plan Cards
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Free Plan
                PlanCard(
                    planName = "Free",
                    price = "\u20B90",
                    period = "forever",
                    features = listOf(
                        "3 bill scans per month",
                        "Basic AI analysis",
                        "1 complaint letter type",
                        "30-day history"
                    ),
                    isCurrentPlan = !uiState.isPremium,
                    isRecommended = false,
                    accentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    onSelect = { }
                )

                // Pro Plan
                PlanCard(
                    planName = "Pro",
                    price = "\u20B999",
                    period = "/month",
                    features = listOf(
                        "Unlimited bill scans",
                        "Advanced AI analysis",
                        "All complaint types",
                        "Unlimited history",
                        "Priority support",
                        "PDF report export"
                    ),
                    isCurrentPlan = false,
                    isRecommended = true,
                    accentColor = PrimaryTeal,
                    onSelect = { viewModel.subscribe("pro") }
                )

                // Family Plan
                PlanCard(
                    planName = "Family",
                    price = "\u20B9199",
                    period = "/month",
                    features = listOf(
                        "Everything in Pro",
                        "Up to 5 family members",
                        "Shared dashboard",
                        "PDF & Excel export",
                        "Dedicated support"
                    ),
                    isCurrentPlan = false,
                    isRecommended = false,
                    accentColor = GoldColor,
                    onSelect = { viewModel.subscribe("family") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Terms note
            Text(
                text = "Cancel anytime. Billed monthly through Google Play. Prices include applicable taxes.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun FeatureComparisonRow(
    feature: String,
    free: String,
    pro: String,
    family: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = feature,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1.3f),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = free,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = pro,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            color = PrimaryTeal,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = family,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            color = GoldColor,
            fontWeight = FontWeight.SemiBold
        )
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
}

@Composable
private fun PlanCard(
    planName: String,
    price: String,
    period: String,
    features: List<String>,
    isCurrentPlan: Boolean,
    isRecommended: Boolean,
    accentColor: Color,
    onSelect: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = if (isRecommended) 6.dp else 2.dp
        ),
        border = if (isRecommended) BorderStroke(2.dp, accentColor) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = planName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
                if (isRecommended) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = accentColor
                    ) {
                        Text(
                            text = "Recommended",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                if (isCurrentPlan) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SuccessGreen.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "Current Plan",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = price,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = period,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp, start = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            features.forEach { feature ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = feature,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (isCurrentPlan) {
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = MaterialTheme.shapes.medium,
                    enabled = false
                ) {
                    Text(
                        text = "Current Plan",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else {
                Button(
                    onClick = onSelect,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = "Choose $planName",
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
