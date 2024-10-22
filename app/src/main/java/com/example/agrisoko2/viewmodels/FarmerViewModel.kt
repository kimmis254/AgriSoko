import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FarmerViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // State variables for farmer data
    val totalSales = mutableStateOf("0")
    val pendingOrders = mutableStateOf("0")
    val totalEarnings = mutableStateOf("$0")
    val activeListings = mutableStateOf("0")
    val products = mutableStateListOf<Product>()
    val orders = mutableStateListOf<Order>()
    val profile = mutableStateOf(Profile("", ""))
    val earningsOverTime = mutableStateOf(listOf<Float>()) // Example for analytics

    // Fetch farmer data based on the role in the 'users' collection
    fun fetchFarmerData(onRoleDetected: (String) -> Unit) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch

            try {
                // Fetch user profile from 'users' collection
                val userSnapshot = firestore.collection("users").document(userId).get().await()

                // Check the user's role
                val role = userSnapshot.getString("role")

                if (role == "farmer") {
                    // Set profile information for farmers
                    profile.value = Profile(
                        name = userSnapshot.getString("name") ?: "Unknown",
                        email = userSnapshot.getString("email") ?: "No Email"
                    )

                    // Fetch farmer's products
                    val productsSnapshot = firestore.collection("users").document(userId)
                        .collection("products").get().await()
                    productsSnapshot.forEach { document ->
                        val product = Product(
                            id = document.id,
                            name = document.getString("name") ?: "Unnamed",
                            price = document.getDouble("price")?.toString() ?: "0.0",
                            quantity = document.getLong("quantity")?.toInt() ?: 0
                        )
                        products.add(product)
                    }

                    // Fetch farmer's orders
                    val ordersSnapshot = firestore.collection("users").document(userId)
                        .collection("orders").get().await()
                    ordersSnapshot.forEach { document ->
                        val order = Order(
                            id = document.id,
                            customerName = document.getString("customerName") ?: "Unknown",
                            status = document.getString("status") ?: "Pending"
                        )
                        orders.add(order)
                    }

                    // Fetch farmer's total sales and earnings
                    totalSales.value = userSnapshot.getLong("totalSales")?.toString() ?: "0"
                    totalEarnings.value = userSnapshot.getDouble("totalEarnings")?.toString() ?: "$0"
                    activeListings.value = products.size.toString()

                    // Notify that the role is 'farmer'
                    onRoleDetected("farmer")

                } else if (role == "customer") {
                    // Notify that the role is 'customer'
                    onRoleDetected("customer")

                } else {
                    // Notify that the role is unknown
                    onRoleDetected("unknown")
                }
            } catch (e: Exception) {
                // Handle errors (log or show an error message)
                e.printStackTrace()
                onRoleDetected("error")
            }
        }
    }

    fun deleteProduct(product: Product) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                firestore.collection("users").document(userId)
                    .collection("products").document(product.id).delete().await()
                products.remove(product)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun markOrderAsDelivered(order: Order) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                firestore.collection("users").document(userId)
                    .collection("orders").document(order.id)
                    .update("status", "Delivered").await()
                val index = orders.indexOf(order)
                if (index != -1) {
                    orders[index] = order.copy(status = "Delivered")
                }
            } catch (e: Exception) {
                e.printStackTrace() // Handle errors
            }
        }
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
    }
}

data class Product(
    val id: String = "",
    val name: String,
    val price: String,
    val quantity: Int
)

data class Order(
    val id: String,
    val customerName: String,
    val status: String
)

data class Profile(
    val name: String,
    val email: String
)
