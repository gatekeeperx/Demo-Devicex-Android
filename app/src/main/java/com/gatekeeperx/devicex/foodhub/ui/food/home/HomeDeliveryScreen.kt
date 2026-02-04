package com.gatekeeperx.devicex.foodhub.ui.food.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
fun FoodDeliveryScreen(
    viewModel: HomeDeliveryViewModel = hiltViewModel(),
    onDishClick: (String) -> Unit = {},
    onCartClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    FoodDeliveryContent(
        uiState = uiState,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onCategorySelected = viewModel::onCategorySelected,
        onDishClick = { dishId ->
            // Find the dish to get its details for tracking
            val dish = uiState.specialDishes.find { it.id == dishId }
            if (dish != null) {
                // Track product view before navigating
                viewModel.onDishSelected(dish.id, dish.name, dish.price)
            }
            // Navigate to details
            onDishClick(dishId)
        },
        onCartClick = onCartClick,
        onMenuClick = { /* Open menu */ }
    )
}

@Composable
fun FoodDeliveryContent(
    uiState: FoodDeliveryUiState,
    onSearchQueryChanged: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onDishClick: (String) -> Unit,
    onCartClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Top Bar
        TopBar(
            cartItemCount = uiState.cartItemCount,
            onCartClick = onCartClick,
            onMenuClick = onMenuClick
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Greeting
        GreetingSection(userName = uiState.userName)

        Spacer(modifier = Modifier.height(24.dp))

        // Search Bar
        SearchBar(
            query = uiState.searchQuery,
            onQueryChanged = onSearchQueryChanged
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Categories
        CategoryRow(
            categories = uiState.categories,
            onCategorySelected = onCategorySelected
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Specials Section
        SpecialsSection(
            dishes = uiState.specialDishes,
            onDishClick = onDishClick
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Cart Button
        CartButton(
            itemCount = uiState.cartItemCount,
            onClick = onCartClick
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Recommended Section
        RecommendedSection(
            dishes = uiState.recommendedDishes
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun TopBar(
    cartItemCount: Int,
    onCartClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Menu Icon
        Icon(
            painter = painterResource(id = R.drawable.ic_menu),
            contentDescription = "Menu",
            modifier = Modifier
                .size(24.dp)
                .clickable(onClick = onMenuClick),
            tint = TextSecondary
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
fun GreetingSection(userName: String) {
    Column {
        Text(
            text = "Hi $userName",
            fontSize = 32.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "What do you want to order today?",
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = TextPrimary
        )
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        placeholder = {
            Text(
                text = "Search",
                color = TextTertiary,
                fontSize = 12.sp
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "Search",
                tint = TextTertiary,
                modifier = Modifier.size(20.dp)
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = PrimaryBeige,
            focusedContainerColor = PrimaryBeige,
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Color.Transparent
        ),
        shape = RoundedCornerShape(28.dp),
        singleLine = true
    )
}

@Composable
fun CategoryRow(
    categories: List<FoodCategory>,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categories) { category ->
            CategoryChip(
                category = category,
                onClick = { onCategorySelected(category.id) }
            )
        }
    }
}

@Composable
fun CategoryChip(
    category: FoodCategory,
    onClick: () -> Unit
) {
    val backgroundColor = if (category.isSelected) PrimaryGreen else PrimaryBeige
    val contentColor = if (category.isSelected) Color.White else TextPrimary
    val iconTint = if (category.isSelected) Color.White else TextPrimary

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(id = category.icon),
                contentDescription = category.name,
                modifier = Modifier.size(20.dp),
                tint = iconTint
            )
            Text(
                text = category.name,
                fontSize = 14.sp,
                color = contentColor
            )
        }
    }
}

@Composable
fun SpecialsSection(
    dishes: List<SpecialDish>,
    onDishClick: (String) -> Unit
) {
    Column {
        Text(
            text = "Specials",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            dishes.forEach { dish ->
                SpecialDishCard(
                    dish = dish,
                    onClick = { onDishClick(dish.id) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun SpecialDishCard(
    dish: SpecialDish,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryBeige)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
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

            Spacer(modifier = Modifier.height(12.dp))

            // Name
            Text(
                text = dish.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Price
            Text(
                text = "$${String.format("%.1f", dish.price)}",
                fontSize = 16.sp,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // See Details Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "See Details",
                    fontSize = 13.sp,
                    color = TextPrimary
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(PrimaryGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_right),
                        contentDescription = "Arrow",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CartButton(
    itemCount: Int,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryOrange
            ),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.height(48.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(PrimaryBeige, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_cart),
                        contentDescription = "Cart",
                        tint = TextPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Text(
                    text = "$itemCount Iteam",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun RecommendedSection(
    dishes: List<RecommendedDish>
) {
    Column {
        Text(
            text = "Recommended",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(dishes) { dish ->
                RecommendedDishCard(dish = dish)
            }
        }
    }
}

@Composable
fun RecommendedDishCard(dish: RecommendedDish) {
    Image(
        painter = painterResource(id = dish.imageRes),
        contentDescription = dish.name,
        modifier = Modifier
            .size(180.dp, 120.dp)
            .clip(RoundedCornerShape(16.dp)),
        contentScale = ContentScale.Crop
    )
}

@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
fun FoodDeliveryScreenPreview() {
    FoodDeliveryTheme {
        FoodDeliveryScreen()
    }
}
