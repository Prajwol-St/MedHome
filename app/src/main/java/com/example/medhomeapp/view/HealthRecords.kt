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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhomeapp.BaseActivity
import com.example.medhomeapp.R
import com.example.medhomeapp.model.HealthRecordsModel

import com.example.medhomeapp.repository.CommonRepoImpl
import com.example.medhomeapp.repository.HealthRecordsRepoImpl

import com.example.medhomeapp.utils.LanguageManager

import com.example.medhomeapp.view.ui.theme.MintGreen
import com.example.medhomeapp.viewmodel.HealthRecordsViewModel
import com.example.medhomeapp.viewmodel.HealthRecordsViewModelFactory
import java.util.*

class HealthRecords : BaseActivity() {

    private val viewModel: HealthRecordsViewModel by viewModels{
        HealthRecordsViewModelFactory(
            HealthRecordsRepoImpl(
                applicationContext,
                CommonRepoImpl()
            )
        )
    }
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
        viewModel.loadHealthRecords()

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
        modifier = Modifier.testTag("healthRecordsScaffold"),
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("searchField")
                        )
                    } else {
                        Text(
                            stringResource(R.string.title_my_records),
                            modifier = Modifier.testTag("titleText")
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { activity.finish() },
                        modifier = Modifier.testTag("backButton")
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_arrow_back_ios_new_24),
                            contentDescription = stringResource(R.string.cd_back),
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            isSearching = !isSearching
                            if (!isSearching) searchQuery = ""
                        },
                        modifier = Modifier.testTag("searchButton")
                    ) {
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
                shape = CircleShape,
                modifier = Modifier.testTag("addRecordButton")
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
                Box(
                    Modifier
                        .fillMaxSize()
                        .testTag("loadingBox"),
                    Alignment.Center
                ) {
                    CircularProgressIndicator(color = MintGreen)
                }
            } else if (filteredRecords.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("emptyStateBox"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_description_24),
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = Color.Gray.copy(alpha = 0.5f)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            stringResource(R.string.empty_no_records),
                            fontSize = 16.sp,
                            color = Color.Gray,
                            modifier = Modifier.testTag("emptyStateMessage")
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("recordsList"),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredRecords.size) { index ->
                        HealthRecordCard(
                            record = filteredRecords[index],
                            onCardClick = {
                                selectedRecord = it
                                showDetailSheet = true
                            },
                            onEditClick = { record ->
                                editingRecord = record
                                recordTitle = record.title
                                recordDescription = record.description
                                selectedDate = record.date
                                selectedFileName = record.fileName
                                showBottomSheet = true
                            },
                            onDeleteClick = {
                                recordToDelete = it
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }

        if (showDetailSheet && selectedRecord != null) {
            ModalBottomSheet(
                onDismissRequest = { showDetailSheet = false },
                sheetState = detailSheetState,
                modifier = Modifier.testTag("detailBottomSheet")
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        selectedRecord!!.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("detailTitle")
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        selectedRecord!!.date,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.testTag("detailDate")
                    )

                    if (selectedRecord!!.description.isNotEmpty()) {
                        Spacer(Modifier.height(16.dp))
                        Text(
                            stringResource(R.string.label_description),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            selectedRecord!!.description,
                            fontSize = 14.sp,
                            modifier = Modifier.testTag("detailDescription")
                        )
                    }

                    if (selectedRecord!!.fileName.isNotEmpty()) {
                        Spacer(Modifier.height(16.dp))
                        Text(
                            stringResource(R.string.label_attached_file),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF1FBF9), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                                .clickable {
                                    if (selectedRecord!!.fileUrl.isNotEmpty()) {
                                        val intent =
                                            Intent(Intent.ACTION_VIEW, Uri.parse(selectedRecord!!.fileUrl))
                                        context.startActivity(intent)
                                    }
                                }
                                .testTag("attachmentRow"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.outline_attach_file_24),
                                contentDescription = null,
                                tint = MintGreen
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(selectedRecord!!.fileName, fontSize = 14.sp)
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = { showDetailSheet = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("closeDetailButton"),
                        colors = ButtonDefaults.buttonColors(MintGreen)
                    ) {
                        Text(stringResource(R.string.btn_close), color = Color.White)
                    }
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                modifier = Modifier.testTag("addEditBottomSheet")
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        if (editingRecord == null) stringResource(R.string.title_add_new_record)
                        else stringResource(R.string.title_edit_record),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("sheetTitle")
                    )

                    Spacer(Modifier.height(20.dp))

                    OutlinedTextField(
                        value = recordTitle,
                        onValueChange = { recordTitle = it },
                        label = { Text(stringResource(R.string.label_title)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("titleField"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MintGreen,
                            focusedLabelColor = MintGreen,
                            cursorColor = MintGreen
                        )
                    )

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = selectedDate,
                        onValueChange = {},
                        label = { Text(stringResource(R.string.label_date)) },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(
                                onClick = { datepicker.show() },
                                modifier = Modifier.testTag("datePickerButton")
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_calendar_month_24),
                                    contentDescription = null
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { datepicker.show() }
                            .testTag("dateField"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MintGreen,
                            focusedLabelColor = MintGreen
                        )
                    )

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = recordDescription,
                        onValueChange = { recordDescription = it },
                        label = { Text(stringResource(R.string.label_description_optional)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .testTag("descriptionField"),
                        maxLines = 5,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MintGreen,
                            focusedLabelColor = MintGreen,
                            cursorColor = MintGreen
                        )
                    )

                    Spacer(Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = { filePickerLauncher.launch("*/*") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("attachFileButton"),
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
                                .padding(16.dp)
                                .testTag("selectedFileBox"),
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
                                    fileUrl = editingRecord?.fileUrl ?: "",
                                    publicId = editingRecord?.publicId ?: ""
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("saveRecordButton"),
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
                title = {
                    Text(
                        stringResource(R.string.dialog_delete_record_title),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("deleteDialogTitle")
                    )
                },
                text = {
                    Text(
                        stringResource(R.string.dialog_delete_record_message),
                        modifier = Modifier.testTag("deleteDialogMessage")
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            recordToDelete?.let {
                                viewModel.deleteHealthRecord(it.id, it.publicId)
                            }
                            showDeleteDialog = false
                            recordToDelete = null
                        },
                        modifier = Modifier.testTag("confirmDeleteButton")
                    ) {
                        Text(stringResource(R.string.btn_delete), color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            recordToDelete = null
                        },
                        modifier = Modifier.testTag("cancelDeleteButton")
                    ) {
                        Text(stringResource(R.string.btn_cancel))
                    }
                },
                modifier = Modifier.testTag("deleteDialog")
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
            .clickable { onCardClick(record) }
            .testTag("healthRecordCard_${record.id}"),
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
                    record.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("recordTitle_${record.id}")
                )
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.testTag("recordMenu_${record.id}")
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_more_vert_24),
                            contentDescription = stringResource(R.string.cd_more)
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.testTag("recordDropdownMenu_${record.id}")
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.menu_edit)) },
                            onClick = {
                                showMenu = false
                                onEditClick(record)
                            },
                            modifier = Modifier.testTag("editMenuItem_${record.id}")
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.menu_delete), color = Color.Red) },
                            onClick = {
                                showMenu = false
                                onDeleteClick(record)
                            },
                            modifier = Modifier.testTag("deleteMenuItem_${record.id}")
                        )
                    }
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(
                record.date,
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.testTag("recordDate_${record.id}")
            )
            if (record.description.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    record.description,
                    fontSize = 14.sp,
                    maxLines = 2,
                    color = Color.DarkGray,
                    modifier = Modifier.testTag("recordDescription_${record.id}")
                )
            }
            if (record.fileName.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .background(Color(0xFFF1FBF9), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                        .testTag("recordAttachment_${record.id}"),
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