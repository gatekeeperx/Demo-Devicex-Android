package com.gatekeeperx.devicex.foodhub.ui.food.detail

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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gatekeeperx.devicex.foodhub.R
import com.gatekeeperx.devicex.foodhub.ui.theme.*

@Composable
fun DishDetailsScreen(
    dishId: String,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit,
    viewModel: DishDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    DishDetailsContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onCartClick = onCartClick,
        onAddToCart = viewModel::onAddToCart
    )
}

@Composable
fun DishDetailsContent(
    uiState: DishDetailsUiState,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryGreen)
    ) {
        // Decorative background element (Rectangle 12 - subtle white overlay rotated)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .offset(y = 100.dp)
                .background(
                    Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(
                        topStart = 100.dp,
                        topEnd = 100.dp,
                        bottomStart = 100.dp,
                        bottomEnd = 100.dp
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Top Bar with back button and cart
            TopBarDetails(
                cartItemCount = uiState.cartItemCount,
                onBackClick = onBackClick,
                onCartClick = onCartClick
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Dish Image
            uiState.dish?.let { dish ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Image with white border and shadow (Ellipse 2 from Figma)
                    Box(
                        modifier = Modifier
                            .size(284.dp)
                            .shadow(
                                elevation = 12.dp,
                                shape = CircleShape
                            )
                            .background(Color.White, CircleShape)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = dish.imageRes),
                            contentDescription = dish.name,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // White bottom section (Rectangle 13 - rounded top corners)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(topStart = 35.dp, topEnd = 35.dp)
                        ),
                    shape = RoundedCornerShape(topStart = 35.dp, topEnd = 35.dp),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        // Description
                        Text(
                            text = dish.description,
                            fontSize = 13.sp,
                            lineHeight = 19.5.sp,
                            color = TextPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Title and Info Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left: Title and calories
                            Column {
                                Text(
                                    text = dish.name,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(
                                            style = SpanStyle(
                                                fontWeight = FontWeight.Medium
                                            )
                                        ) {
                                            append(dish.weight)
                                        }
                                        append("/")
                                        append(dish.calories)
                                    },
                                    fontSize = 15.sp,
                                    color = TextPrimary
                                )
                            }

                            // Right: Portion
                            Text(
                                text = dish.portion,
                                fontSize = 15.sp,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Restaurant Info
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Restaurant logo and info
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = dish.restaurant.logoRes),
                                    contentDescription = dish.restaurant.name,
                                    modifier = Modifier.size(48.dp)
                                )
                                Column {
                                    Text(
                                        text = dish.restaurant.name,
                                        fontSize = 14.sp,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = dish.restaurant.distance,
                                        fontSize = 13.sp,
                                        color = TextTertiary
                                    )
                                }
                            }

                            // Rating stars
                            RatingStars(rating = dish.rating)
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Divider
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0xFFD9D9D9))
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Price and Add to Cart Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Price
                            Column {
                                dish.originalPrice?.let { originalPrice ->
                                    Text(
                                        text = "Price: $${"%.2f".format(originalPrice)}",
                                        fontSize = 13.sp,
                                        color = TextTertiary,
                                        style = LocalTextStyle.current.copy(
                                            textDecoration = TextDecoration.LineThrough
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                                Text(
                                    text = "${"$%.2f".format(dish.price)}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                            }

                            // Add to Cart Button
                            Button(
                                onClick = onAddToCart,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryGreen
                                ),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier
                                    .height(56.dp)
                                    .weight(1f)
                                    .padding(start = 24.dp),
                                contentPadding = PaddingValues(horizontal = 24.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = "Add to cart",
                                        fontSize = 13.sp,
                                        color = Color.White
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(PrimaryBeige, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_plus),
                                            contentDescription = "Add",
                                            tint = PrimaryGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TopBarDetails(
    cartItemCount: Int,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 40.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back Button
        Icon(
            painter = painterResource(id = R.drawable.ic_menu), // Using menu as back arrow
            contentDescription = "Back",
            modifier = Modifier
                .size(32.dp)
                .clickable(onClick = onBackClick)
                .padding(4.dp),
            tint = Color.White
        )

        // Shopping Bag with Badge
        Box(
            modifier = Modifier.clickable(onClick = onCartClick)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_shopping_bag),
                contentDescription = "Shopping Cart",
                modifier = Modifier.size(24.dp),
                tint = Color.White
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
fun RatingStars(rating: Float) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(5) { index ->
            Icon(
                painter = painterResource(id = R.drawable.ic_star),
                contentDescription = "Star ${index + 1}",
                modifier = Modifier.size(20.dp),
                tint = if (index < rating.toInt()) Color(0xFFFFAF51) else Color.Gray
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
fun DishDetailsScreenPreview() {
    FoodDeliveryTheme {
        DishDetailsScreen(
            dishId = "noodles",
            onBackClick = {},
            onCartClick = {}
        )
    }
}
