package com.diazmoviles.app.presentation.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.diazmoviles.app.presentation.viewmodel.AdminProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductFormScreen(
    onBack: () -> Unit,
    viewModel: AdminProductViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var nombre by remember { mutableStateOf(uiState.producto?.nombre ?: "") }
    var marcaId by remember { mutableStateOf(0) }
    var categoriaId by remember { mutableStateOf(0) }
    var modelo by remember { mutableStateOf(uiState.producto?.modelo ?: "") }
    var precio by remember { mutableStateOf(uiState.producto?.precio ?: "") }
    var stock by remember { mutableStateOf(uiState.producto?.stock?.toString() ?: "0") }
    var descripcion by remember { mutableStateOf(uiState.producto?.descripcion ?: "") }
    var imagenUrl by remember { mutableStateOf(uiState.producto?.imagenUrl ?: "") }
    var marcaDropdown by remember { mutableStateOf(false) }
    var categoriaDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.producto) {
        uiState.producto?.let { p ->
            nombre = p.nombre
            marcaId = p.marcaId
            categoriaId = p.categoriaId
            modelo = p.modelo
            precio = p.precio
            stock = p.stock.toString()
            descripcion = p.descripcion
            imagenUrl = p.imagenUrl ?: ""
        }
    }

    LaunchedEffect(uiState.success) {
        if (uiState.success) onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Inventory2, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(if (uiState.isEditing) "Editar Producto" else "Nuevo Producto")
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
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Información del producto", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(16.dp))

                        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                        Spacer(Modifier.height(8.dp))

                        ExposedDropdownMenuBox(expanded = marcaDropdown, onExpandedChange = { marcaDropdown = it }) {
                            OutlinedTextField(
                                value = uiState.marcas.find { it.id == marcaId }?.nombre ?: "Seleccionar",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Marca") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = marcaDropdown) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(expanded = marcaDropdown, onDismissRequest = { marcaDropdown = false }) {
                                uiState.marcas.forEach { marca ->
                                    DropdownMenuItem(
                                        text = { Text(marca.nombre) },
                                        onClick = { marcaId = marca.id; marcaDropdown = false }
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        ExposedDropdownMenuBox(expanded = categoriaDropdown, onExpandedChange = { categoriaDropdown = it }) {
                            OutlinedTextField(
                                value = uiState.categorias.find { it.id == categoriaId }?.nombre ?: "Seleccionar",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Categoría") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriaDropdown) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(expanded = categoriaDropdown, onDismissRequest = { categoriaDropdown = false }) {
                                uiState.categorias.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat.nombre) },
                                        onClick = { categoriaId = cat.id; categoriaDropdown = false }
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = modelo, onValueChange = { modelo = it }, label = { Text("Modelo") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                        Spacer(Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = precio, onValueChange = { precio = it }, label = { Text("Precio") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
                            OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stock") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
                        }

                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), minLines = 3)
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = imagenUrl, onValueChange = { imagenUrl = it }, label = { Text("URL de imagen") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    }
                }

                if (uiState.error != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(uiState.error!!, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.padding(12.dp))
                    }
                }

                Button(
                    onClick = {
                        viewModel.guardarProducto(
                            nombre, marcaId, categoriaId, modelo, precio,
                            stock.toIntOrNull() ?: 0, descripcion, imagenUrl
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    enabled = !uiState.isSaving && nombre.isNotBlank() && marcaId > 0 && categoriaId > 0 && precio.isNotBlank(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                    } else {
                        Icon(if (uiState.isEditing) Icons.Default.Check else Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(if (uiState.isEditing) "Actualizar" else "Crear Producto", style = MaterialTheme.typography.titleMedium)
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
