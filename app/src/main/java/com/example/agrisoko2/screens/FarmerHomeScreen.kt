import FarmerViewModel
import Order
import Product
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun FarmerHomeScreen(navController: NavController, viewModel: FarmerViewModel = FarmerViewModel()) {
    // Fetch data when the screen is loaded
    LaunchedEffect(Unit) {
        viewModel.fetchFarmerData { role ->
            when (role) {
                "farmer" -> { /* Stay on the dashboard */ }
                "customer" -> {
                    // Navigate to the customer dashboard
                    navController.navigate("customer_home") {
                        popUpTo("login_screen") { inclusive = true }
                    }
                }
                "unknown", "error" -> {
                    // Handle unknown role or errors
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("login_screen") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .animateContentSize()
    ) {
        // Dashboard Header
        Text(
            text = "Farmer Dashboard",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Dashboard Overview with Stats
        DashboardOverview(viewModel)

        Spacer(modifier = Modifier.height(16.dp))

        // Manage Products Section
        ManageProductsSection(viewModel)

        Spacer(modifier = Modifier.height(16.dp))

        // Orders Management Section
        OrdersManagementSection(viewModel)

        Spacer(modifier = Modifier.height(16.dp))

        // Profile Section
        ProfileSection(viewModel)

        Spacer(modifier = Modifier.height(16.dp))

        // Logout Button
        Button(
            onClick = {
                viewModel.logout()
                navController.navigate("login_screen") {
                    popUpTo(0) { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(text = "Logout", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun DashboardOverview(viewModel: FarmerViewModel) {
    // Data from view model
    val totalSales by remember { viewModel.totalSales }
    val pendingOrders by remember { viewModel.pendingOrders }
    val totalEarnings by remember { viewModel.totalEarnings }
    val activeListings by remember { viewModel.activeListings }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Dashboard Overview", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Total Sales
            StatCard("Total Sales", totalSales)

            // Pending Orders
            StatCard("Pending Orders", pendingOrders)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Total Earnings
            StatCard("Total Earnings", totalEarnings)

            // Active Listings
            StatCard("Active Listings", activeListings)
        }
    }
}

@Composable
fun StatCard(title: String, stat: String) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge, color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = stat, style = MaterialTheme.typography.headlineMedium, color = Color.White)
    }
}

@Composable
fun ManageProductsSection(viewModel: FarmerViewModel) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Manage Products", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { /* Navigate to Add Product Screen */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Add New Product")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Smooth scrolling with LazyColumn
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(viewModel.products) { product ->
                AnimatedVisibility(visible = true, enter = androidx.compose.animation.fadeIn(tween(300))) {
                    ProductItem(product, viewModel::deleteProduct)
                }
            }
        }
    }
}

@Composable
fun ProductItem(product: Product, onDelete: (Product) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = product.name, style = MaterialTheme.typography.bodyLarge)
            Text(text = "Price: ${product.price}", style = MaterialTheme.typography.bodyMedium)
        }

        Row {
            Button(onClick = { /* Edit product logic */ }, modifier = Modifier.padding(end = 8.dp)) {
                Text(text = "Edit")
            }
            Button(
                onClick = { onDelete(product) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(text = "Delete")
            }
        }
    }
}

@Composable
fun OrdersManagementSection(viewModel: FarmerViewModel) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Orders Management", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(viewModel.orders) { order ->
                AnimatedVisibility(visible = true, enter = androidx.compose.animation.fadeIn(tween(300))) {
                    OrderItem(order, viewModel::markOrderAsDelivered)
                }
            }
        }
    }
}

@Composable
fun OrderItem(order: Order, onMarkDelivered: (Order) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = "Order #${order.id}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Customer: ${order.customerName}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Status: ${order.status}", style = MaterialTheme.typography.bodyMedium)
        }

        if (order.status == "Pending") {
            Button(onClick = { onMarkDelivered(order) }) {
                Text(text = "Mark as Delivered")
            }
        }
    }
}

@Composable
fun ProfileSection(viewModel: FarmerViewModel) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Profile", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        val profile by remember { viewModel.profile }

        Text(text = "Name: ${profile.name}", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Email: ${profile.email}", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { /* Navigate to Edit Profile Screen */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Edit Profile")
        }
    }
}
