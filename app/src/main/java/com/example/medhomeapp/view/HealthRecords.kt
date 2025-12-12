package com.example.medhomeapp.view

import android.app.Activity
import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhomeapp.R
import com.example.medhomeapp.model.HealthRecordsModel
import com.example.medhomeapp.ui.theme.Blue10
import com.example.medhomeapp.viewmodel.HealthRecordsViewModel
import android.Manifest
import android.os.Build


class HealthRecords : ComponentActivity() {

    private val viewModel: HealthRecordsViewModel by viewModels()
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (!allGranted) {
            Toast.makeText(this, "Permissions needed to upload files", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestStoragePermissions()
        setContent {
            HealthRecordsBody(viewModel)
        }
    }
    private fun requestStoragePermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO
            )
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        permissionLauncher.launch(permissions)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthRecordsBody(viewModel: HealthRecordsViewModel) {

    val context = LocalContext.current
    val activity = context as Activity

    var isSearching by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showBottomSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var recordToDelete by remember { mutableStateOf<HealthRecordsModel?>(null) }
    var editingRecord by remember { mutableStateOf<HealthRecordsModel?>(null) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var recordTitle by remember { mutableStateOf("") }
    var recordDescription by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf<String?>(null) }

    val calendar = Calendar.getInstance()
    val datepicker = DatePickerDialog(
        context,
        { _, year, month, day ->
            selectedDate = "$day/${month + 1}/$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedFileUri = uri
        selectedFileName = uri?.lastPathSegment
    }

    val healthRecords by viewModel.healthRecords.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState()
    val successMessage by viewModel.successMessage.observeAsState()

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(successMessage) {
        successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }

    val filteredRecords =
        if (searchQuery.isEmpty()) healthRecords
        else healthRecords.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true)
        }

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Blue10,
                        titleContentColor = White
                    ),
                    title = { Text("My Records") },
                    navigationIcon = {
                        IconButton(onClick = { activity.finish() }) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_arrow_back_ios_new_24),
                                contentDescription = null,
                                tint = White
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                isSearching = !isSearching
                                if (!isSearching) searchQuery = ""
                            }
                        ) {
                            Icon(
                                painter = painterResource(
                                    if (isSearching) R.drawable.baseline_close_24
                                    else R.drawable.baseline_search_24
                                ),
                                contentDescription = null,
                                tint = White
                            )
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingRecord = null
                    recordTitle = ""
                    recordDescription = ""
                    selectedDate = ""
                    selectedFileUri = null
                    selectedFileName = null
                    showBottomSheet = true
                },
                containerColor = Blue10,
                shape = CircleShape
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_add_24),
                    contentDescription = null,
                    tint = White
                )
            }
        }
    ) { padding ->

        if (isLoading) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator(color = Blue10)
            }
        } else if (filteredRecords.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No Records Found", color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.padding(16.dp)
                .padding(padding)) {
                items(filteredRecords.size) { index ->
                    HealthRecordCard(
                        record = filteredRecords[index],
                        onEditClick = {
                            editingRecord = it
                            recordTitle = it.title
                            recordDescription = it.description
                            selectedDate = it.date
                            selectedFileName = it.fileName.takeIf { n -> n.isNotEmpty() }
                            selectedFileUri = null
                            showBottomSheet = true
                        },
                        onDeleteClick = {
                            recordToDelete = it
                            showDeleteDialog = true
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                Column(Modifier.padding(16.dp)) {

                    Text(
                        text = if (editingRecord == null) "Add Medical Record" else "Edit Medical Record",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = recordTitle,
                        onValueChange = { recordTitle = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = selectedDate,
                        onValueChange = {},
                        label = { Text("Date") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { datepicker.show() },
                        enabled = false
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = recordDescription,
                        onValueChange = { recordDescription = it },
                        label = { Text("Description") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                            .clickable { filePickerLauncher.launch("*/*") }
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedFileName == null) {
                            Text("Tap to upload file", color = Color.Gray)
                        } else {
                            Text(selectedFileName ?: "")
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (recordTitle.isNotEmpty() && selectedDate.isNotEmpty()) {

                                val record = HealthRecordsModel(
                                    id = editingRecord?.id ?: "",
                                    userId = editingRecord?.userId ?: "",
                                    title = recordTitle,
                                    date = selectedDate,
                                    description = recordDescription,
                                    fileName = selectedFileName ?: editingRecord?.fileName ?: "",
                                    fileUrl = editingRecord?.fileUrl ?: ""
                                )

                                if (editingRecord == null)
                                    viewModel.addHealthRecord(record, selectedFileUri)
                                else
                                    viewModel.updateHealthRecord(record, selectedFileUri)

                                showBottomSheet = false
                            } else {
                                Toast.makeText(context, "Title & Date required", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(Blue10)
                    ) {
                        Text(
                            text = if (editingRecord == null) "Save Record" else "Update Record",
                            color = White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (showDeleteDialog && recordToDelete != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Record?", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                text = { Text("Are you sure you want to delete this record?") },
                confirmButton = {
                    TextButton(onClick = {
                        recordToDelete?.let {
                            viewModel.deleteHealthRecord(
                                recordId = it.id,
                                fileUrl = it.fileUrl
                            )
                        }
                        showDeleteDialog = false
                        recordToDelete = null
                    }) {
                        Text("Delete", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        recordToDelete = null
                    }) {
                        Text("Cancel")
                    }
                },
            )
        }
    }
}

@Composable
fun HealthRecordCard(
    record: HealthRecordsModel,
    onEditClick: (HealthRecordsModel) -> Unit,
    onDeleteClick: (HealthRecordsModel) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {

        Column(Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = record.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_more_vert_24),
                            contentDescription = null
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                showMenu = false
                                onEditClick(record)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete", color = Color.Red) },
                            onClick = {
                                showMenu = false
                                onDeleteClick(record)
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            Text(text = record.date, fontSize = 12.sp, color = Color.Gray)

            if (record.description.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(text = record.description, fontSize = 14.sp)
            }

            if (record.fileName.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.outline_attach_file_24),
                        contentDescription = null,
                        tint = Color.Green
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(record.fileName, fontSize = 13.sp, color = Color.DarkGray)
                }
            }
        }
    }
}
