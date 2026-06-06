package com.diazmoviles.app.presentation.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diazmoviles.app.data.remote.dto.CategoriaDto
import com.diazmoviles.app.data.remote.dto.MarcaDto
import com.diazmoviles.app.domain.model.Cliente
import com.diazmoviles.app.domain.model.Venta
import com.diazmoviles.app.presentation.viewmodel.AdminUiState
import com.diazmoviles.app.presentation.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    onBack: () -> Unit,
    onAddProduct: () -> Unit,
    onEditProduct: (Int) -> Unit,
    viewModel: AdminViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    var deleteId by remember { mutableStateOf<Int?>(null) }
    var deleteType by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { viewModel.cargarTodo() }
    LaunchedEffect(uiState.error) { uiState.error?.let { snackbar.showSnackbar(it); viewModel.clearError() } }

    if (deleteId != null) {
        AlertDialog(
            onDismissRequest = { deleteId = null },
            icon = { Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        when (deleteType) {
                            "producto" -> viewModel.eliminarProducto(deleteId!!)
                            "marca" -> viewModel.eliminarMarca(deleteId!!)
                            "categoria" -> viewModel.eliminarCategoria(deleteId!!)
                            "cliente" -> viewModel.eliminarCliente(deleteId!!)
                            "venta" -> viewModel.eliminarVenta(deleteId!!)
                        }
                        deleteId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Eliminar") }
            },
            dismissButton = { OutlinedButton(onClick = { deleteId = null }) { Text("Cancelar") } }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AdminPanelSettings, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Panel Admin")
                    }
                },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Summary cards
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Triple(Icons.Default.Inventory, "Productos", uiState.productos.size.toString()),
                    Triple(Icons.Default.Bookmark, "Marcas", uiState.marcas.size.toString()),
                    Triple(Icons.Default.Bookmarks, "Categorías", uiState.categorias.size.toString()),
                    Triple(Icons.Default.People, "Clientes", uiState.clientes.size.toString()),
                    Triple(Icons.Default.ShoppingCart, "Ventas", uiState.ventas.size.toString()),
                ).forEach { (icon, label, count) ->
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(icon, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                            Text(count, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                        }
                    }
                }
            }

            // Tabs
            val tabs = listOf("Productos", "Marcas", "Categorías", "Clientes", "Ventas")
            TabRow(
                selectedTabIndex = uiState.selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { i, title ->
                    Tab(
                        selected = uiState.selectedTab == i,
                        onClick = { viewModel.selectTab(i) },
                        text = { Text(title, fontWeight = if (uiState.selectedTab == i) FontWeight.Bold else FontWeight.Normal, fontSize = 13.sp) }
                    )
                }
            }

            when (uiState.selectedTab) {
                0 -> ProductosTab(uiState, viewModel, onAddProduct, onEditProduct, { deleteId = it; deleteType = "producto" })
                1 -> MarcasTab(uiState, viewModel, { deleteId = it; deleteType = "marca" })
                2 -> CategoriasTab(uiState, viewModel, { deleteId = it; deleteType = "categoria" })
                3 -> ClientesTab(uiState, viewModel, { deleteId = it; deleteType = "cliente" })
                4 -> VentasTab(uiState, viewModel, { deleteId = it; deleteType = "venta" })
            }
        }
    }
}

// ── Tab Productos ──
@Composable
private fun ProductosTab(
    uiState: AdminUiState, vm: AdminViewModel,
    onAddProduct: () -> Unit, onEditProduct: (Int) -> Unit,
    onDeleteRequest: (Int) -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            OutlinedTextField(
                value = uiState.searchProductos,
                onValueChange = { vm.buscarProductos(it) },
                placeholder = { Text("Buscar productos...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                shape = RoundedCornerShape(12.dp)
            )
            if (uiState.isLoadingProductos) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else if (uiState.productos.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay productos", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.productos, key = { "p_${it.id}" }) { p ->
                        Card(
                            onClick = { onEditProduct(p.id) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            elevation = CardDefaults.cardElevation(1.dp)
                        ) {
                            Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(p.nombre, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                    Text("${p.marcaNombre} · $${p.precio} · Stock: ${p.stock}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Box(Modifier.size(30.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Edit, "Editar", Modifier.size(15.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Spacer(Modifier.width(4.dp))
                                IconButton(onClick = { onDeleteRequest(p.id) }, Modifier.size(30.dp)) {
                                    Icon(Icons.Default.Delete, "Eliminar", Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = { vm.irPaginaAnterior() },
                                enabled = uiState.currentPage > 1,
                                shape = RoundedCornerShape(10.dp)
                            ) { Text("← Anterior") }
                            Text(
                                "Pág. ${uiState.currentPage}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            OutlinedButton(
                                onClick = { vm.irPaginaSiguiente() },
                                enabled = uiState.hasMorePages,
                                shape = RoundedCornerShape(10.dp)
                            ) { Text("Siguiente →") }
                        }
                    }
                    item { Spacer(Modifier.height(72.dp)) }
                }
            }
        }
        FloatingActionButton(
            onClick = onAddProduct,
            modifier = Modifier.padding(16.dp).align(Alignment.BottomEnd),
            containerColor = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(16.dp)
        ) { Icon(Icons.Default.Add, "Crear producto", tint = MaterialTheme.colorScheme.onPrimary) }
    }
}

// ── Tab Marcas ──
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MarcasTab(
    uiState: AdminUiState, vm: AdminViewModel,
    onDeleteRequest: (Int) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var editingId by remember { mutableStateOf<Int?>(null) }
    var showForm by remember { mutableStateOf(false) }

    val filtered = if (uiState.searchMarcas.isBlank()) uiState.marcas
        else uiState.marcas.filter { it.nombre.contains(uiState.searchMarcas, true) }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            OutlinedTextField(
                value = uiState.searchMarcas,
                onValueChange = { vm.buscarMarcas(it) },
                placeholder = { Text("Buscar marcas...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                shape = RoundedCornerShape(12.dp)
            )
            if (showForm) {
                Card(Modifier.fillMaxWidth().padding(horizontal = 12.dp), shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text(if (editingId != null) "Editar marca" else "Nueva marca", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(onClick = { showForm = false }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp)) {
                                Icon(Icons.Default.Close, null, Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Cancelar")
                            }
                            Button(onClick = { vm.guardarMarca(nombre, desc, editingId); showForm = false; nombre = ""; desc = "" }, modifier = Modifier.weight(1f), enabled = nombre.isNotBlank(), shape = RoundedCornerShape(10.dp)) {
                                Icon(Icons.Default.Check, null, Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Guardar")
                            }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
            if (uiState.isLoadingMarcas) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else if (filtered.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No hay marcas", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            } else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(filtered, key = { "m_${it.id}" }) { item ->
                        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), elevation = CardDefaults.cardElevation(1.dp)) {
                            Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(item.nombre, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                    if (item.descripcion.isNotBlank()) Text(item.descripcion, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                FilledTonalButton(onClick = { editingId = item.id; nombre = item.nombre; desc = item.descripcion; showForm = true }, modifier = Modifier.height(30.dp), shape = RoundedCornerShape(8.dp), contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)) {
                                    Icon(Icons.Default.Edit, null, Modifier.size(14.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Editar", style = MaterialTheme.typography.labelSmall)
                                }
                                Spacer(Modifier.width(2.dp))
                                IconButton(onClick = { onDeleteRequest(item.id) }, Modifier.size(28.dp)) {
                                    Icon(Icons.Default.Delete, "Eliminar", Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                    item { Spacer(Modifier.height(72.dp)) }
                }
            }
        }
        FloatingActionButton(
            onClick = { editingId = null; nombre = ""; desc = ""; showForm = true },
            modifier = Modifier.padding(16.dp).align(Alignment.BottomEnd),
            containerColor = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(16.dp)
        ) { Icon(Icons.Default.Add, "Nueva marca", tint = MaterialTheme.colorScheme.onPrimary) }
    }
}

// ── Tab Categorías ──
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoriasTab(
    uiState: AdminUiState, vm: AdminViewModel,
    onDeleteRequest: (Int) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var editingId by remember { mutableStateOf<Int?>(null) }
    var showForm by remember { mutableStateOf(false) }

    val filtered = if (uiState.searchCategorias.isBlank()) uiState.categorias
        else uiState.categorias.filter { it.nombre.contains(uiState.searchCategorias, true) }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            OutlinedTextField(
                value = uiState.searchCategorias,
                onValueChange = { vm.buscarCategorias(it) },
                placeholder = { Text("Buscar categorías...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                shape = RoundedCornerShape(12.dp)
            )
            if (showForm) {
                Card(Modifier.fillMaxWidth().padding(horizontal = 12.dp), shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text(if (editingId != null) "Editar categoría" else "Nueva categoría", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(onClick = { showForm = false }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp)) {
                                Icon(Icons.Default.Close, null, Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Cancelar")
                            }
                            Button(onClick = { vm.guardarCategoria(nombre, desc, editingId); showForm = false; nombre = ""; desc = "" }, modifier = Modifier.weight(1f), enabled = nombre.isNotBlank(), shape = RoundedCornerShape(10.dp)) {
                                Icon(Icons.Default.Check, null, Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Guardar")
                            }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
            if (uiState.isLoadingCategorias) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else if (filtered.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No hay categorías", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            } else {
                LazyColumn(contentPadding = PaddingValues(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(filtered, key = { "c_${it.id}" }) { item ->
                        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), elevation = CardDefaults.cardElevation(1.dp)) {
                            Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(item.nombre, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                    if (item.descripcion.isNotBlank()) Text(item.descripcion, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                FilledTonalButton(onClick = { editingId = item.id; nombre = item.nombre; desc = item.descripcion; showForm = true }, modifier = Modifier.height(30.dp), shape = RoundedCornerShape(8.dp), contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)) {
                                    Icon(Icons.Default.Edit, null, Modifier.size(14.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Editar", style = MaterialTheme.typography.labelSmall)
                                }
                                Spacer(Modifier.width(2.dp))
                                IconButton(onClick = { onDeleteRequest(item.id) }, Modifier.size(28.dp)) {
                                    Icon(Icons.Default.Delete, "Eliminar", Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                    item { Spacer(Modifier.height(72.dp)) }
                }
            }
        }
        FloatingActionButton(
            onClick = { editingId = null; nombre = ""; desc = ""; showForm = true },
            modifier = Modifier.padding(16.dp).align(Alignment.BottomEnd),
            containerColor = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(16.dp)
        ) { Icon(Icons.Default.Add, "Nueva categoría", tint = MaterialTheme.colorScheme.onPrimary) }
    }
}

// ── Tab Clientes ──
@Composable
private fun ClientesTab(
    uiState: AdminUiState, vm: AdminViewModel,
    onDeleteRequest: (Int) -> Unit
) {
    val filtered = if (uiState.searchClientes.isBlank()) uiState.clientes
        else uiState.clientes.filter { it.nombre.contains(uiState.searchClientes, true) || it.cedula.contains(uiState.searchClientes) }

    Column(Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = uiState.searchClientes,
            onValueChange = { vm.buscarClientes(it) },
            placeholder = { Text("Buscar clientes...") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            shape = RoundedCornerShape(12.dp)
        )
        if (uiState.isLoadingClientes) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else if (filtered.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No hay clientes", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        } else {
            LazyColumn(contentPadding = PaddingValues(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                filtered.forEach { c ->
                    item(key = "cl_${c.id}") {
                        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), elevation = CardDefaults.cardElevation(1.dp)) {
                            Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text("${c.nombre} ${c.apellido}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                    Text("${c.cedula} · ${c.email} · ${c.telefono}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                IconButton(onClick = { onDeleteRequest(c.id) }, Modifier.size(30.dp)) {
                                    Icon(Icons.Default.Delete, "Eliminar", Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

// ── Tab Ventas ──
@Composable
private fun VentasTab(
    uiState: AdminUiState, vm: AdminViewModel,
    onDeleteRequest: (Int) -> Unit
) {
    var expandId by remember { mutableStateOf<Int?>(null) }

    Column(Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = uiState.searchVentas,
            onValueChange = { vm.buscarVentas(it) },
            placeholder = { Text("Buscar ventas...") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            shape = RoundedCornerShape(12.dp)
        )
        if (uiState.isLoadingVentas) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else if (uiState.ventas.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No hay ventas", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        } else {
            val filtered = if (uiState.searchVentas.isBlank()) uiState.ventas
                else uiState.ventas.filter { it.id.toString() == uiState.searchVentas.trim() }
            LazyColumn(contentPadding = PaddingValues(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(filtered, key = { "v_${it.id}" }) { venta ->
                    Card(
                        onClick = { expandId = if (expandId == venta.id) null else venta.id },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Column(Modifier.fillMaxWidth().padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                Column(Modifier.weight(1f)) {
                                    Text("Venta #${venta.id}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                    Text(venta.clienteNombre ?: "N/A", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text(
                                    venta.estado.replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = when (venta.estado) {
                                        "completada" -> MaterialTheme.colorScheme.primary
                                        "cancelada", "anulada" -> MaterialTheme.colorScheme.error
                                        else -> MaterialTheme.colorScheme.tertiary
                                    }
                                )
                            }
                            Text("$${venta.total} · ${venta.fecha.take(10)} · ${venta.metodoPago}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (venta.detalles.isNotEmpty()) {
                                Spacer(Modifier.height(4.dp))
                                venta.detalles.forEach { d ->
                                    Text("• ${d.productoNombre} x${d.cantidad} = $${d.subtotal}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            if (expandId == venta.id) {
                                Spacer(Modifier.height(8.dp))
                                Divider()
                                Spacer(Modifier.height(8.dp))
                                if (venta.estado == "pendiente") {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                        Button(
                                            onClick = { vm.actualizarEstadoVenta(venta.id, "completada") },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp)
                                        ) { Text("Confirmar") }
                                        OutlinedButton(
                                            onClick = { vm.actualizarEstadoVenta(venta.id, "cancelada") },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                        ) { Text("Cancelar") }
                                    }
                                } else if (venta.estado == "completada") {
                                    Button(
                                        onClick = { vm.actualizarEstadoVenta(venta.id, "anulada") },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                    ) { Text("Anular venta") }
                                }
                                Spacer(Modifier.height(8.dp))
                                IconButton(onClick = { onDeleteRequest(venta.id) }, modifier = Modifier.size(30.dp)) {
                                    Icon(Icons.Default.Delete, "Eliminar", Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}
