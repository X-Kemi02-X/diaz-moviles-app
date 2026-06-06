package com.diazmoviles.app.presentation.ui.checkout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.diazmoviles.app.presentation.viewmodel.CheckoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onBack: () -> Unit,
    onSuccess: (ventaId: Int) -> Unit,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var cedula by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var metodoPago by remember { mutableStateOf("efectivo") }
    var observacion by remember { mutableStateOf("") }

    LaunchedEffect(uiState.clienteExistente) {
        uiState.clienteExistente?.let { c ->
            nombre = c.nombre; apellido = c.apellido; cedula = c.cedula
            email = c.email; telefono = c.telefono; direccion = c.direccion
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Long) }
    }

    LaunchedEffect(uiState.success, uiState.ventaId) {
        if (uiState.success && uiState.ventaId != null) onSuccess(uiState.ventaId!!)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AddShoppingCart, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Checkout")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(16.dp))
                    Text("Procesando compra...", style = MaterialTheme.typography.titleMedium)
                    Text("Estamos registrando tus datos", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding)
                    .verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(8.dp))
                            Text("Datos del cliente", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(Modifier.height(16.dp))
                        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = apellido, onValueChange = { apellido = it }, label = { Text("Apellido") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = cedula, onValueChange = { cedula = it }, label = { Text("Cédula") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Email), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Phone), keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }))
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done), keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }))
                    }
                }

                Card(
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Payment, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(8.dp))
                            Text("Método de pago", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(Modifier.height(12.dp))
                        val opcionesPago = listOf("efectivo" to "Efectivo", "tarjeta" to "Tarjeta", "transferencia" to "Transferencia")
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            opcionesPago.forEach { (value, label) ->
                                FilterChip(
                                    selected = metodoPago == value,
                                    onClick = { metodoPago = value },
                                    label = { Text(label) },
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(value = observacion, onValueChange = { observacion = it }, label = { Text("Observación (opcional)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), minLines = 2)
                    }
                }

                Button(
                    onClick = { viewModel.realizarCheckout(nombre, apellido, cedula, email, telefono, direccion, metodoPago, observacion) },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    enabled = nombre.isNotBlank() && apellido.isNotBlank() && cedula.isNotBlank(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Confirmar compra", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
