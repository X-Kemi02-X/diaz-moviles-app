package com.diazmoviles.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.diazmoviles.app.presentation.ui.admin.AdminCatalogScreen
import com.diazmoviles.app.presentation.ui.admin.AdminProductFormScreen
import com.diazmoviles.app.presentation.ui.admin.AdminScreen
import com.diazmoviles.app.presentation.ui.auth.LoginScreen
import com.diazmoviles.app.presentation.ui.cart.CartScreen
import com.diazmoviles.app.presentation.ui.checkout.CheckoutScreen
import com.diazmoviles.app.presentation.ui.detail.DetalleProductoScreen
import com.diazmoviles.app.presentation.ui.home.HomeScreen
import com.diazmoviles.app.presentation.ui.orders.OrdersScreen
import com.diazmoviles.app.presentation.ui.products.ProductosScreen
import com.diazmoviles.app.presentation.ui.register.RegisterScreen
import com.diazmoviles.app.presentation.viewmodel.AdminCatalogViewModel
import com.diazmoviles.app.presentation.viewmodel.AuthViewModel
import com.diazmoviles.app.presentation.viewmodel.CatalogType

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Home : Screen("home")
    data object Productos : Screen("productos")
    data object DetalleProducto : Screen("detalle/{productoId}") {
        fun createRoute(productoId: Int) = "detalle/$productoId"
    }
    data object Cart : Screen("carrito")
    data object Checkout : Screen("checkout")
    data object Orders : Screen("pedidos")
    data object Register : Screen("registrar")
    data object Admin : Screen("admin")
    data object AdminProductForm : Screen("admin/producto/{productoId}") {
        fun createRoute(productoId: Int? = null) =
            if (productoId != null) "admin/producto/$productoId" else "admin/producto/-1"
    }
    data object AdminMarcas : Screen("admin/marcas")
    data object AdminCategorias : Screen("admin/categorias")
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.uiState.collectAsState()

    val startDestination = if (authState.isLoggedIn) Screen.Home.route else Screen.Login.route

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToProductos = { navController.navigate(Screen.Productos.route) },
                onNavigateToCart = { navController.navigate(Screen.Cart.route) },
                onNavigateToOrders = { navController.navigate(Screen.Orders.route) },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToAdmin = { navController.navigate(Screen.Admin.route) },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                isStaff = authState.isStaff
            )
        }
        composable(Screen.Productos.route) {
            ProductosScreen(
                onProductClick = { productoId ->
                    navController.navigate(Screen.DetalleProducto.createRoute(productoId))
                }
            )
        }
        composable(
            route = Screen.DetalleProducto.route,
            arguments = listOf(navArgument("productoId") { type = NavType.IntType })
        ) {
            DetalleProductoScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Cart.route) {
            CartScreen(
                onBack = { navController.popBackStack() },
                onCheckout = { navController.navigate(Screen.Checkout.route) }
            )
        }
        composable(Screen.Checkout.route) {
            CheckoutScreen(
                onBack = { navController.popBackStack() },
                onSuccess = {
                    navController.navigate(Screen.Orders.route) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }
        composable(Screen.Orders.route) {
            OrdersScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }
        composable(Screen.Admin.route) {
            AdminScreen(
                onBack = { navController.popBackStack() },
                onAddProduct = { navController.navigate(Screen.AdminProductForm.createRoute(null)) },
                onEditProduct = { productoId -> navController.navigate(Screen.AdminProductForm.createRoute(productoId)) },
                onManageMarcas = { navController.navigate(Screen.AdminMarcas.route) },
                onManageCategorias = { navController.navigate(Screen.AdminCategorias.route) }
            )
        }
        composable(
            route = Screen.AdminProductForm.route,
            arguments = listOf(navArgument("productoId") { type = NavType.IntType })
        ) {
            AdminProductFormScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.AdminMarcas.route) {
            val catalogViewModel: AdminCatalogViewModel = hiltViewModel()
            AdminCatalogScreen(
                type = CatalogType.MARCA,
                onBack = { navController.popBackStack() },
                viewModel = catalogViewModel
            )
        }
        composable(Screen.AdminCategorias.route) {
            val catalogViewModel: AdminCatalogViewModel = hiltViewModel()
            AdminCatalogScreen(
                type = CatalogType.CATEGORIA,
                onBack = { navController.popBackStack() },
                viewModel = catalogViewModel
            )
        }
    }
}
