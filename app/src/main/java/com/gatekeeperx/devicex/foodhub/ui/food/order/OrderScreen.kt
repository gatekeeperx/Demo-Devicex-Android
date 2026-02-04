package com.gatekeeperx.devicex.foodhub.ui.food.order

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gatekeeperx.devicex.foodhub.R
import com.gatekeeperx.devicex.foodhub.ui.theme.*

@Composable
fun OrderScreen(
    viewModel: OrderViewModel = hiltViewModel(),
    onOrderCompleted: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isCompleted) {
        if (uiState.isCompleted) {
            onOrderCompleted()
        }
    }

    OrderContent(uiState = uiState)
}

@Composable
fun OrderContent(uiState: OrderUiState) {
    // Pulse animation for status icon
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Order Status Icon
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .scale(if (uiState.status == OrderStatus.DELIVERED) 1f else scale)
                    .background(
                        when (uiState.status) {
                            OrderStatus.PROCESSING -> PrimaryBeige
                            OrderStatus.PREPARING -> Color(0xFFFFE0B2)
                            OrderStatus.ON_THE_WAY -> Color(0xFFB2DFDB)
                            OrderStatus.DELIVERED -> Color(0xFFC8E6C9)
                        },
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                when (uiState.status) {
                    OrderStatus.DELIVERED -> {
                        Text(
                            text = "✓",
                            fontSize = 80.sp,
                            color = PrimaryGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    else -> {
                        Image(
                            painter = painterResource(id = R.drawable.img_noodles),
                            contentDescription = "Order",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Status Title
            Text(
                text = when (uiState.status) {
                    OrderStatus.PROCESSING -> "Processing Order"
                    OrderStatus.PREPARING -> "Preparing Your Food"
                    OrderStatus.ON_THE_WAY -> "On The Way"
                    OrderStatus.DELIVERED -> "Delivered!"
                },
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Status Description
            Text(
                text = when (uiState.status) {
                    OrderStatus.PROCESSING -> "We're confirming your order..."
                    OrderStatus.PREPARING -> "The kitchen is preparing your delicious meal"
                    OrderStatus.ON_THE_WAY -> "Your food is on its way to you!"
                    OrderStatus.DELIVERED -> "Enjoy your meal! 🍜"
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Order Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = PrimaryBeige
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    // Order Number
                    OrderDetailRow(
                        label = "Order Number",
                        value = uiState.orderNumber
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color.White, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Items
                    OrderDetailRow(
                        label = "Items",
                        value = "${uiState.itemCount} dishes"
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color.White, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Total Amount
                    OrderDetailRow(
                        label = "Total Amount",
                        value = "$${String.format("%.2f", uiState.totalAmount)}",
                        valueColor = PrimaryGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Progress Indicator
            if (uiState.status != OrderStatus.DELIVERED) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(60.dp),
                        color = PrimaryGreen,
                        strokeWidth = 4.dp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Estimated time: ${uiState.timeRemaining} seconds",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }
            } else {
                // Success Message
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFC8E6C9)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🎉",
                            fontSize = 40.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Order Delivered Successfully!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2E7D32),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Redirecting to home...",
                            fontSize = 14.sp,
                            color = Color(0xFF388E3C)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Status Timeline
            StatusTimeline(currentStatus = uiState.status)

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
fun OrderDetailRow(
    label: String,
    value: String,
    valueColor: Color = TextPrimary
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = TextSecondary
        )
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = valueColor
        )
    }
}

@Composable
fun StatusTimeline(currentStatus: OrderStatus) {
    val statuses = listOf(
        OrderStatus.PROCESSING to "Processing",
        OrderStatus.PREPARING to "Preparing",
        OrderStatus.ON_THE_WAY to "On The Way",
        OrderStatus.DELIVERED to "Delivered"
    )

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Order Timeline",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        statuses.forEachIndexed { index, (status, label) ->
            val isCompleted = status.ordinal <= currentStatus.ordinal
            val isCurrent = status == currentStatus

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status Icon
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            if (isCompleted) PrimaryGreen else Color(0xFFE0E0E0),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Text(
                            text = if (status == OrderStatus.DELIVERED) "✓" else "${index + 1}",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text = "${index + 1}",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Status Label
                Text(
                    text = label,
                    fontSize = 16.sp,
                    fontWeight = if (isCurrent) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isCompleted) TextPrimary else TextSecondary,
                    modifier = Modifier.weight(1f)
                )
            }

            // Connector line (except for last item)
            if (index < statuses.size - 1) {
                Box(
                    modifier = Modifier
                        .padding(start = 15.dp, top = 4.dp, bottom = 4.dp)
                        .width(2.dp)
                        .height(32.dp)
                        .background(
                            if (isCompleted) PrimaryGreen else Color(0xFFE0E0E0)
                        )
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
fun OrderScreenPreview() {
    FoodDeliveryTheme {
        OrderContent(
            uiState = OrderUiState(
                status = OrderStatus.ON_THE_WAY,
                timeRemaining = 5
            )
        )
    }
}
