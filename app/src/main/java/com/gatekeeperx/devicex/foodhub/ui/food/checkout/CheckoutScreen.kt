package com.gatekeeperx.devicex.foodhub.ui.food.checkout

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gatekeeperx.devicex.foodhub.R
import com.gatekeeperx.devicex.foodhub.ui.theme.*

@Composable
fun CheckoutScreen(
    viewModel: CheckoutViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onCheckoutClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    CheckoutContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onCartClick = onCartClick,
        onCheckoutClick = onCheckoutClick,
        onIncreaseQuantity = viewModel::increaseQuantity,
        onDecreaseQuantity = viewModel::decreaseQuantity
    )
}

@Composable
fun CheckoutContent(
    uiState: CheckoutUiState,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit,
    onCheckoutClick: () -> Unit,
    onIncreaseQuantity: (String) -> Unit,
    onDecreaseQuantity: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Top Bar
            CheckoutTopBar(
                cartItemCount = uiState.cartItemCount,
                onBackClick = onBackClick,
                onCartClick = onCartClick
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Title
            Text(
                text = "My Order",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Cart Items
            uiState.cartItems.forEach { item ->
                CartItemRow(
                    item = item,
                    onIncreaseQuantity = { onIncreaseQuantity(item.id) },
                    onDecreaseQuantity = { onDecreaseQuantity(item.id) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    color = Color(0xFFE3E3E3),
                    thickness = 1.dp
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Add bottom padding for checkout button
            Spacer(modifier = Modifier.height(100.dp))
        }

        // Bottom Checkout Button
        CheckoutButton(
            onClick = onCheckoutClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(20.dp)
        )
    }
}

@Composable
fun CheckoutTopBar(
    cartItemCount: Int,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back Arrow
        Icon(
            painter = painterResource(id = R.drawable.ic_back_arrow),
            contentDescription = "Back",
            modifier = Modifier
                .size(24.dp)
                .clickable(onClick = onBackClick),
            tint = TextPrimary
        )

        // Shopping Bag with Badge
        Box(
            modifier = Modifier.clickable(onClick = onCartClick)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_shopping_bag),
                contentDescription = "Shopping Cart",
                modifier = Modifier.size(24.dp),
                tint = TextPrimary
            )
            if (cartItemCount > 0) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 6.dp, y = (-6).dp)
                        .background(CartBadgeRed, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cartItemCount.toString(),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onIncreaseQuantity: () -> Unit,
    onDecreaseQuantity: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Image
        Box(
            modifier = Modifier
                .size(100.dp, 80.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(PrimaryBeige),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = item.imageRes),
                contentDescription = item.name,
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Info and Controls
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Name
            Text(
                text = item.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Description
            Text(
                text = item.description,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Quantity Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Minus Button
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(PrimaryBeige, CircleShape)
                        .clickable(onClick = onDecreaseQuantity),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_plus_beige),
                        contentDescription = "Decrease",
                        modifier = Modifier.size(16.dp),
                        tint = TextPrimary
                    )
                }

                // Quantity
                Text(
                    text = item.quantity.toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )

                // Plus Button
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(PrimaryBeige, CircleShape)
                        .clickable(onClick = onIncreaseQuantity),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_plus),
                        contentDescription = "Increase",
                        modifier = Modifier.size(16.dp),
                        tint = TextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Price
        Text(
            text = "$${String.format("%.2f", item.price)}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )
    }
}

@Composable
fun CheckoutButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryGreen
        ),
        shape = RoundedCornerShape(28.dp)
    ) {
        Text(
            text = "Checkout",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
fun CheckoutScreenPreview() {
    // Mock data for preview without ViewModel
    val mockItems = listOf(
        CartItem(
            id = "noodles",
            name = "Noodles",
            description = "with shrimps,egg,pork",
            price = 7.50,
            quantity = 1,
            imageRes = R.drawable.img_noodles
        ),
        CartItem(
            id = "pasta",
            name = "Pasta",
            description = "with tomato sauce",
            price = 6.20,
            quantity = 1,
            imageRes = R.drawable.img_pasta
        ),
        CartItem(
            id = "curry",
            name = "Curry",
            description = "with strawberry,tomato,egg",
            price = 7.50,
            quantity = 1,
            imageRes = R.drawable.img_recommended_1
        )
    )
    val mockUiState = CheckoutUiState.calculateTotals(mockItems)

    FoodDeliveryTheme {
        CheckoutContent(
            uiState = mockUiState,
            onBackClick = {},
            onCartClick = {},
            onCheckoutClick = {},
            onIncreaseQuantity = {},
            onDecreaseQuantity = {}
        )
    }
}
