package com.diazmoviles.app.presentation.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.diazmoviles.app.domain.model.Cliente
import com.diazmoviles.app.presentation.viewmodel.EditProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val focusNombre = remember { FocusRequester() }
    val focusApellido = remember { FocusRequester() }
    val focusCedula = remember { FocusRequester() }
    val focusEmail = remember { FocusRequester() }
    val focusTelefono = remember { FocusRequester() }
    val focusDireccion = remember { FocusRequester() }

    var showPasswordSection by remember { mutableStateOf(false) }
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var oldPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }

    val cliente = uiState.cliente
    var nombre by remember(cliente) { mutableStateOf(cliente?.nombre ?: "") }
    var apellido by remember(cliente) { mutableStateOf(cliente?.apellido ?: "") }
    var cedula by remember(cliente) { mutableStateOf(cliente?.cedula ?: "") }
    var email by remember(cliente) { mutableStateOf(cliente?.email ?: "") }
    var telefono by remember(cliente) { mutableStateOf(cliente?.telefono ?: "") }
    var direccion by remember(cliente) { mutableStateOf(cliente?.direccion ?: "") }

    LaunchedEffect(uiState.success) {
        if (uiState.success != null) {
            kotlinx.coroutines.delay(2000)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Edit, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Editar mis datos")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (cliente == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No se encontraron datos del cliente", color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                        Text(
                            "Datos personales",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(16.dp))

                        OutlinedTextField(
                            value = nombre, onValueChange = { nombre = it },
                            label = { Text("Nombre") }, singleLine = true,
                            modifier = Modifier.fillMaxWidth().focusRequester(focusNombre),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = apellido, onValueChange = { apellido = it },
                            label = { Text("Apellido") }, singleLine = true,
                            modifier = Modifier.fillMaxWidth().focusRequester(focusApellido),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = cedula, onValueChange = { cedula = it },
                            label = { Text("Cédula") }, singleLine = true,
                            modifier = Modifier.fillMaxWidth().focusRequester(focusCedula),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = email, onValueChange = { email = it },
                            label = { Text("Email") }, singleLine = true,
                            modifier = Modifier.fillMaxWidth().focusRequester(focusEmail),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Email),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = telefono, onValueChange = { telefono = it },
                            label = { Text("Teléfono") }, singleLine = true,
                            modifier = Modifier.fillMaxWidth().focusRequester(focusTelefono),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Phone),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = direccion, onValueChange = { direccion = it },
                            label = { Text("Dirección") },
                            modifier = Modifier.fillMaxWidth().focusRequester(focusDireccion),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                        )
                    }
                }

                // Password section
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    onClick = { showPasswordSection = !showPasswordSection }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Lock, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Cambiar contraseña",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                if (showPasswordSection) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (showPasswordSection) {
                            Spacer(Modifier.height(16.dp))
                            OutlinedTextField(
                                value = oldPassword, onValueChange = { oldPassword = it },
                                label = { Text("Contraseña actual") }, singleLine = true,
                                visualTransformation = if (oldPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = { oldPasswordVisible = !oldPasswordVisible }) {
                                        Icon(if (oldPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = newPassword, onValueChange = { newPassword = it },
                                label = { Text("Nueva contraseña") }, singleLine = true,
                                visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                                        Icon(if (newPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }

                uiState.error?.let {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(it, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.padding(12.dp))
                    }
                }

                uiState.success?.let {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(it, color = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.padding(12.dp))
                    }
                }

                Button(
                    onClick = {
                        viewModel.guardarTodo(
                            cliente.id, nombre, apellido, cedula, email, telefono, direccion,
                            oldPassword, newPassword
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    enabled = nombre.isNotBlank() && apellido.isNotBlank() && email.isNotBlank(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (uiState.saving) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Check, null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Guardar cambios", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}
