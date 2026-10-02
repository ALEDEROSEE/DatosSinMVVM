package com.example.datossinmvvm

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import kotlinx.coroutines.launch

/**
 * Ejercicio 1: la misma pantalla pero dentro de un Scaffold.
 * Los botones "Agregar usuario" y "Listar usuarios" se movieron a la TopAppBar,
 * y se añadió un tercer botón que elimina el último registro insertado.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenUserScaffold() {
    val context = LocalContext.current
    // A diferencia de la parte guiada, aquí la base se construye una sola vez:
    // remember evita que Room.databaseBuilder se ejecute en cada recomposición.
    val db = remember { construirDatabase(context) }
    val dao = remember(db) { db.userDao() }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var ultimoId by remember { mutableStateOf("") }
    var dataUser by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Usuarios - Room") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                actions = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            if (firstName.isBlank() && lastName.isBlank()) {
                                snackbarHostState.showSnackbar("Escribe al menos un nombre")
                                return@launch
                            }
                            val user = User(0, firstName, lastName)
                            AgregarUsuario(user = user, dao = dao)
                            val ultimo = dao.getLast()
                            ultimoId = ultimo?.uid?.toString() ?: ""
                            firstName = ""
                            lastName = ""
                            snackbarHostState.showSnackbar("Guardado con uid = $ultimoId")
                        }
                    }) {
                        Icon(Icons.Filled.PersonAdd, contentDescription = "Agregar usuario")
                    }
                    IconButton(onClick = {
                        coroutineScope.launch { dataUser = getUsers(dao = dao) }
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.List,
                            contentDescription = "Listar usuarios"
                        )
                    }
                    IconButton(onClick = {
                        coroutineScope.launch {
                            val borrado = eliminarUltimo(dao)
                            dataUser = getUsers(dao = dao)
                            ultimoId = dao.getLast()?.uid?.toString() ?: ""
                            snackbarHostState.showSnackbar(
                                if (borrado == null) "No hay registros para eliminar"
                                else "Eliminado: ${borrado.firstName} ${borrado.lastName} (uid ${borrado.uid})"
                            )
                        }
                    }) {
                        Icon(Icons.Filled.DeleteSweep, contentDescription = "Eliminar el último")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = ultimoId,
                onValueChange = { },
                label = { Text("Último ID insertado (solo lectura)") },
                readOnly = true,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name: ") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name:") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Usuarios en la tabla", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = if (dataUser.isBlank()) "(pulsa el icono de lista)" else dataUser,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

/** Igual que crearDatabase() pero sin ser @Composable, para poder envolverla en remember. */
fun construirDatabase(context: Context): UserDatabase =
    Room.databaseBuilder(context, UserDatabase::class.java, "user_db").build()

/**
 * Elimina el último registro insertado (el de mayor uid) y lo devuelve.
 * Devuelve null si la tabla estaba vacía.
 */
suspend fun eliminarUltimo(dao: UserDao): User? {
    return try {
        val ultimo = dao.getLast() ?: return null
        dao.delete(ultimo)
        ultimo
    } catch (e: Exception) {
        Log.e("User", "Error: delete: ${e.message}")
        null
    }
}
