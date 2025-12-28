package com.example.medhomeapp.view

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhomeapp.BaseActivity
import com.example.medhomeapp.R
import com.example.medhomeapp.model.HealthRecordsModel
import com.example.medhomeapp.ui.theme.Blue10
import com.example.medhomeapp.viewmodel.HealthRecordsViewModel
import java.util.*

class HealthRecords : BaseActivity() {

    private val viewModel: HealthRecordsViewModel by viewModels()
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (!permissions.values.all { it }) {
            Toast.makeText(this, "Permissions needed to upload files", Toast.LENGTH_SHORT).show()
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
    var showDetailSheet by remember { mutableStateOf(false) }
    var recordToDelete by remember { mutableStateOf<HealthRecordsModel?>(null) }
    var editingRecord by remember { mutableStateOf<HealthRecordsModel?>(null) }
    var selectedRecord by remember { mutableStateOf<HealthRecordsModel?>(null) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val detailSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var recordTitle by remember { mutableStateOf("") }
    var recordDescription by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf<String?>(null) }

    val calendar = Calendar.getInstance()
    val datepicker = DatePickerDialog(
        context,
        { _, year, month, day -> selectedDate = "$day/${month + 1}/$year" },
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

    val filteredRecords = if (searchQuery.isEmpty()) healthRecords
    else healthRecords.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
                it.description.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Blue10,
                    titleContentColor = Color.White
                ),
                title = {
                    if (isSearching) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search records...", color = Color.White.copy(alpha = 0.7f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color.White,
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.5f)
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text("My Records")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { activity.finish() }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_arrow_back_ios_new_24),
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        isSearching = !isSearching
                        if (!isSearching) searchQuery = ""
                    }) {
                        Icon(
                            painter = painterResource(
                                if (isSearching) R.drawable.baseline_close_24
                                else R.drawable.baseline_search_24
                            ),
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            )
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
                    tint = Color.White
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (isLoading) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator(color = Blue10)
                }
            } else if (filteredRecords.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        if (searchQuery.isEmpty()) "No Records Found" else "No matching records",
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(16.dp)
                ) {
                    items(filteredRecords.size) { index ->
                        HealthRecordCard(
                            record = filteredRecords[index],
                            onCardClick = {
                                selectedRecord = it
                                showDetailSheet = true
                            },
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
        }

<<<<<<< HEAD

=======
>>>>>>> c470254bdb27f8d47dc093b484192372a5fdeace
        if (showDetailSheet && selectedRecord != null) {
            ModalBottomSheet(
                onDismissRequest = { showDetailSheet = false },
                sheetState = detailSheetState
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Record Details",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row {
                            IconButton(onClick = {
                                selectedRecord?.let { record ->
                                    editingRecord = record
                                    recordTitle = record.title
                                    recordDescription = record.description
                                    selectedDate = record.date
                                    selectedFileName = record.fileName.takeIf { it.isNotEmpty() }
                                    selectedFileUri = null
                                    showDetailSheet = false
                                    showBottomSheet = true
                                }
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_edit_24),
                                    contentDescription = "Edit",
                                    tint = Blue10
                                )
                            }
                            IconButton(onClick = {
                                selectedRecord?.let { record ->
                                    recordToDelete = record
                                    showDetailSheet = false
                                    showDeleteDialog = true
                                }
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_delete_24),
                                    contentDescription = "Delete",
                                    tint = Color.Red
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))


                    Text(
                        text = "Title",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = selectedRecord?.title ?: "",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.height(16.dp))


                    Text(
                        text = "Date",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = selectedRecord?.date ?: "",
                        fontSize = 16.sp
                    )

                    Spacer(Modifier.height(16.dp))


                    if (selectedRecord?.description?.isNotEmpty() == true) {
                        Text(
                            text = "Description",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = selectedRecord?.description ?: "",
                            fontSize = 16.sp,
                            lineHeight = 24.sp
                        )
                        Spacer(Modifier.height(16.dp))
                    }
<<<<<<< HEAD


=======
>>>>>>> c470254bdb27f8d47dc093b484192372a5fdeace
                    if (selectedRecord?.fileName?.isNotEmpty() == true &&
                        selectedRecord?.fileUrl?.isNotEmpty() == true) {
                        Text(
                            text = "Attached File",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Spacer(Modifier.height(8.dp))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedRecord?.fileUrl?.let { url ->
                                        try {
                                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                                setDataAndType(Uri.parse(url), "*/*")
                                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                                            }
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            Toast.makeText(
                                                context,
                                                "Unable to open file",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFE3F2FD)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.outline_attach_file_24),
                                    contentDescription = null,
                                    tint = Blue10,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = selectedRecord?.fileName ?: "",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = "Tap to open",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                                Icon(
                                    painter = painterResource(R.drawable.baseline_arrow_forward_ios_24),
                                    contentDescription = null,
                                    tint = Blue10,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }

                    Spacer(Modifier.height(20.dp))
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
                        modifier = Modifier.fillMaxWidth().clickable { datepicker.show() },
                        enabled = false
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = recordDescription,
                        onValueChange = { recordDescription = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth().height(120.dp)
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
                        Text(selectedFileName ?: "Tap to upload file", color = Color.Gray)
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
                            color = Color.White,
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
                            viewModel.deleteHealthRecord(it.id, it.fileUrl)
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
                }
            )
        }
    }
}

@Composable
fun HealthRecordCard(
    record: HealthRecordsModel,
    onCardClick: (HealthRecordsModel) -> Unit,
    onEditClick: (HealthRecordsModel) -> Unit,
    onDeleteClick: (HealthRecordsModel) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick(record) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(record.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
                            onClick = { showMenu = false; onEditClick(record) }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete", color = Color.Red) },
                            onClick = { showMenu = false; onDeleteClick(record) }
                        )
                    }
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(record.date, fontSize = 12.sp, color = Color.Gray)
            if (record.description.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    record.description,
                    fontSize = 14.sp,
                    maxLines = 2,
                    color = Color.DarkGray
                )
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