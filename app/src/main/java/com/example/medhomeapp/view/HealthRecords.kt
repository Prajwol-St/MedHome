package com.example.medhomeapp.view

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhomeapp.BaseActivity
import com.example.medhomeapp.R
import com.example.medhomeapp.model.HealthRecordsModel
import com.example.medhomeapp.utils.LanguageManager
import com.example.medhomeapp.view.ui.theme.MintGreen
import com.example.medhomeapp.viewmodel.HealthRecordsViewModel
import java.util.*

class HealthRecords : BaseActivity() {

    private val viewModel: HealthRecordsViewModel by viewModels()
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (!permissions.values.all { it }) {
//            Toast.makeText(this, "Permissions needed to upload files", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestStoragePermissions()

        val language = LanguageManager.getLanguage(this)
        setContent {
            key(language) {
                HealthRecordsBody(viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val language = LanguageManager.getLanguage(this)
        setContent {
            key(language) {
                HealthRecordsBody(viewModel)
            }
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
                    containerColor = MintGreen,
                    titleContentColor = Color.White
                ),
                title = {
                    if (isSearching) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text(stringResource(R.string.hint_search_records), color = Color.White.copy(alpha = 0.7f)) },
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
                        Text(stringResource(R.string.title_my_records))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { activity.finish() }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_arrow_back_ios_new_24),
                            contentDescription = stringResource(R.string.cd_back),
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
                            contentDescription = stringResource(R.string.cd_search),
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
                containerColor = MintGreen,
                shape = CircleShape
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_add_24),
                    contentDescription = stringResource(R.string.cd_add),
                    tint = Color.White
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (isLoading) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator(color = MintGreen)
                }
            } else if (filteredRecords.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        if (searchQuery.isEmpty()) stringResource(R.string.empty_no_records) else stringResource(R.string.empty_no_matching_records),
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
                            text = stringResource(R.string.title_record_details),
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
                                    contentDescription = stringResource(R.string.cd_edit),
                                    tint = MintGreen
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
                                    contentDescription = stringResource(R.string.cd_delete),
                                    tint = Color.Red
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    Text(
                        text = stringResource(R.string.label_title),
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
                        text = stringResource(R.string.label_date),
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
                            text = stringResource(R.string.label_description),
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

                    if (selectedRecord?.fileName?.isNotEmpty() == true &&
                        selectedRecord?.fileUrl?.isNotEmpty() == true
                    ) {
                        Text(
                            text = stringResource(R.string.label_attached_file),
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
                                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                            }
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            Toast.makeText(context, context.getString(R.string.error_cannot_open_file), Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1FBF9))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.outline_attach_file_24),
                                    contentDescription = null,
                                    tint = MintGreen
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = selectedRecord?.fileName ?: "",
                                    fontSize = 14.sp,
                                    color = Color.DarkGray
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = if (editingRecord == null) stringResource(R.string.title_add_new_record) else stringResource(R.string.title_edit_record),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(20.dp))

                    OutlinedTextField(
                        value = recordTitle,
                        onValueChange = { recordTitle = it },
                        label = { Text(stringResource(R.string.label_title_required)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = selectedDate,
                        onValueChange = {},
                        label = { Text(stringResource(R.string.label_date_required)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { datepicker.show() },
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = recordDescription,
                        onValueChange = { recordDescription = it },
                        label = { Text(stringResource(R.string.label_description_optional)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        maxLines = 5
                    )

                    Spacer(Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = { filePickerLauncher.launch("*/*") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MintGreen
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_attach_file_24),
                            contentDescription = null
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(if (selectedFileName != null) stringResource(R.string.btn_change_file) else stringResource(R.string.btn_attach_file))
                    }

                    if (selectedFileName != null) {
                        Spacer(Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF1FBF9), RoundedCornerShape(8.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(selectedFileName ?: stringResource(R.string.hint_tap_to_upload), color = Color.Gray)
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
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.error_title_date_required),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(MintGreen)
                    ) {
                        Text(
                            text = if (editingRecord == null) stringResource(R.string.btn_save_record) else stringResource(R.string.btn_update_record),
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
                title = { Text(stringResource(R.string.dialog_delete_record_title), fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                text = { Text(stringResource(R.string.dialog_delete_record_message)) },
                confirmButton = {
                    TextButton(onClick = {
                        recordToDelete?.let {
                            viewModel.deleteHealthRecord(it.id, it.fileUrl)
                        }
                        showDeleteDialog = false
                        recordToDelete = null
                    }) {
                        Text(stringResource(R.string.btn_delete), color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        recordToDelete = null
                    }) {
                        Text(stringResource(R.string.btn_cancel))
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
                            contentDescription = stringResource(R.string.cd_more)
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.menu_edit)) },
                            onClick = {
                                showMenu = false
                                onEditClick(record)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.menu_delete), color = Color.Red) },
                            onClick = {
                                showMenu = false
                                onDeleteClick(record)
                            }
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
                        .background(Color(0xFFF1FBF9), RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.outline_attach_file_24),
                        contentDescription = null,
                        tint = MintGreen
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(record.fileName, fontSize = 13.sp, color = Color.DarkGray)
                }
            }
        }
    }
}