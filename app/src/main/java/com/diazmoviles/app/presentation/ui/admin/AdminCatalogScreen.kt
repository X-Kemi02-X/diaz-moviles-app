package com.diazmoviles.app.presentation.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.diazmoviles.app.data.remote.dto.CategoriaDto
import com.diazmoviles.app.data.remote.dto.MarcaDto
import com.diazmoviles.app.presentation.viewmodel.AdminCatalogViewModel
import com.diazmoviles.app.presentation.viewmodel.CatalogType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCatalogScreen(
    type: CatalogType,
    onBack: () -> Unit,
    viewModel: AdminCatalogViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var nombreInput by remember { mutableStateOf("") }
    var descripcionInput by remember { mutableStateOf("") }
    var showForm by remember { mutableStateOf(false) }
    var editingId by remember { mutableStateOf<Int?>(null) }
    var searchText by remember { mutableStateOf("") }

    LaunchedEffect(type) {
        viewModel.load(type)
    }

    fun openCreate() {
        editingId = null
        nombreInput = ""
        descripcionInput = ""
        showForm = true
    }

    fun openEdit(item: Any) {
        editingId = if (item is MarcaDto) item.id else (item as CategoriaDto).id
        nombreInput = if (item is MarcaDto) item.nombre else (item as CategoriaDto).nombre
        descripcionInput = if (item is MarcaDto) item.descripcion else (item as CategoriaDto).descripcion
        showForm = true
    }

    val title = if (type == CatalogType.MARCA) "Marcas" else "Categorías"
    val icon = if (type == CatalogType.MARCA) Icons.Default.Bookmark else Icons.Default.Bookmarks

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(icon, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(title)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (showForm) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            if (editingId != null) "Editar $title" else "Nueva $title",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = nombreInput,
                            onValueChange = { nombreInput = it },
                            label = { Text("Nombre") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = descripcionInput,
                            onValueChange = { descripcionInput = it },
                            label = { Text("Descripción") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = { showForm = false },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Cancelar")
                            }
                            Button(
                                onClick = {
                                    viewModel.save(type, nombreInput, descripcionInput, editingId)
                                    showForm = false
                                },
                                modifier = Modifier.weight(1f),
                                enabled = nombreInput.isNotBlank(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Guardar")
                            }
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Listado de $title",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        FilledTonalButton(
                            onClick = { openCreate() },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Nueva", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }

            if (!showForm) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("Buscar $title...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(Modifier.height(8.dp))
            }

            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                uiState.items.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay registros.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                else -> {
                    val filteredItems = uiState.items.filter { item ->
                        val nombre = if (item is MarcaDto) item.nombre else (item as CategoriaDto).nombre
                        searchText.isBlank() || nombre.contains(searchText, ignoreCase = true)
                    }
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredItems, key = { if (it is MarcaDto) "marca_${it.id}" else "cat_${(it as CategoriaDto).id}" }) { item ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val nombre = if (item is MarcaDto) item.nombre else (item as CategoriaDto).nombre
                                    val desc = if (item is MarcaDto) item.descripcion else (item as CategoriaDto).descripcion
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(nombre, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                        if (desc.isNotBlank()) {
                                            Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                    FilledTonalButton(
                                        onClick = { openEdit(item) },
                                        modifier = Modifier.height(32.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Editar", modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Editar", style = MaterialTheme.typography.labelSmall)
                                    }
                                    Spacer(Modifier.width(4.dp))
                                    IconButton(
                                        onClick = { viewModel.delete(type, item) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
