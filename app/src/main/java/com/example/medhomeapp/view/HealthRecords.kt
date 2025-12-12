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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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

class HealthRecords : ComponentActivity() {
    private val viewModel: HealthRecordsViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HealthRecordsBody(viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthRecordsBody(viewModel: HealthRecordsViewModel){
    val context = LocalContext.current
    val activity = context as Activity
    var isSearching by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showBottomSheet by remember { mutableStateOf(false) }
    var recordToDelete by remember { mutableStateOf<HealthRecordsModel?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var editingRecord by remember { mutableStateOf<HealthRecordsModel?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var recordTitle by remember { mutableStateOf("") }
    var recordDescription by remember { mutableStateOf("") }
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(java.util.Calendar.MONTH)
    val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
    var selectedDate by remember { mutableStateOf("") }
    val datepicker = DatePickerDialog(
        context, { _, year, month, day ->
            selectedDate = "$year/${month + 1}/$day"

        }, year, month, day
    )
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf<String?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {uri : Uri? ->
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

    val filteredRecords = if (searchQuery.isEmpty()){
        healthRecords
    }else{
        healthRecords.filter {
            it.title.contains(searchQuery, ignoreCase = true)||
                    it.description.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Blue10,
                        titleContentColor = White,
                        navigationIconContentColor = White,
                        actionIconContentColor = White,
                    ),
                    title = {Text("My Records")},
                    navigationIcon = {
                        IconButton(onClick ={activity.finish()}) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_arrow_back_ios_new_24),
                                contentDescription = null
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            isSearching = !isSearching
                            if (!isSearching){
                                searchQuery = ""
                            }
                        }) {
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
                AnimatedVisibility(
                    visible = isSearching,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically()+ fadeOut()
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {searchQuery = it},
                        placeholder = {Text("Search ....")},
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.baseline_search_24),
                                contentDescription = null
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .background(White),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = White,
                            unfocusedIndicatorColor = Color.Gray,
                            focusedContainerColor = White,
                            unfocusedContainerColor = White

                        ),
                    )
                }
            }
        },
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(Blue10, shape = CircleShape),


                ){
                FloatingActionButton(
                    onClick = {
                        editingRecord = null
                        recordTitle = ""
                        recordDescription = ""
                        selectedDate = ""
                        selectedFileUri =  null
                        selectedFileName = null
                        showBottomSheet = true
                    },
                    containerColor = Color.Transparent,
                    modifier = Modifier.fillMaxSize(),
                    shape = CircleShape
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_add_24),
                        contentDescription = null,
                        tint = White
                    )
                }
            }
        }

    ){padding ->
        if (isLoading){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ){
                CircularProgressIndicator(color = Blue10)
            }
        }else if (filteredRecords.isEmpty()){
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(R.drawable.medicalrecord),
                        contentDescription = null,
                        modifier = Modifier.size(120.dp),
                        colorFilter = ColorFilter.tint(Color.LightGray)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "No Records Yet",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (searchQuery.isEmpty())
                            "Tap + to add your medical records"
                        else
                            "Try a different search term",
                            fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
                }
            }
        }
        if (showBottomSheet){
            ModalBottomSheet(
                onDismissRequest = {showBottomSheet = false},
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ){
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(text = "Add Medical Record",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = {showBottomSheet = false}) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_close_24),
                                contentDescription = null
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Title", fontSize = 14.sp,
                        fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = recordTitle,
                        onValueChange = {recordTitle = it},
                        placeholder = {Text("e.g., Annual Checkup")},
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Date", fontSize = 14.sp,
                        fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        enabled = false,
                        value = selectedDate,
                        onValueChange = {selectedDate =it},
                        placeholder = {Text("DD/MM/YYYY")},
                        modifier = Modifier.fillMaxWidth()
                            .clickable{
                                datepicker.show()
                            },
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.baseline_edit_calendar_24),
                                contentDescription = null
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Description", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = recordDescription,
                        onValueChange = {recordDescription = it},
                        placeholder = {Text("Add notes, symptoms, or details")},
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        shape = RoundedCornerShape(12.dp),


                        )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Upload Medical Report", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .border(
                                width = 2.dp,
                                color = Color(0xFF4A90E2),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clip(RoundedCornerShape(12.dp))
                            .clickable{
                                filePickerLauncher.launch("*/*")
                            }
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ){
                        if (selectedFileName == null){
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ){
                                Text("", fontSize = 48.sp)
//                                Spacer(modifier = Modifier.height(18.dp))
                                Text("Tap to upload", fontSize = 16.sp, color = Color.Gray,
                                    textAlign = TextAlign.Center)
                                Text("(PDF, JPG, PNG)", fontSize = 14.sp, color = Color.LightGray)
                            }
                        }else{
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ){
                                Text(
                                    text = selectedFileName ?: "",
                                    fontSize = 14.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = {
                                    selectedFileUri = null
                                    selectedFileName = null
                                }) {
                                    Icon(
                                        painter = painterResource(R.drawable.baseline_close_24),
                                        contentDescription = null,
                                        tint = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            showBottomSheet = false
                            recordTitle = ""
                            selectedDate = ""
                            recordDescription = ""
                            selectedFileUri = null
                            selectedFileName = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(Color.Transparent),
                        shape = RoundedCornerShape(15.dp),
                        contentPadding = ButtonDefaults.ContentPadding
                    )
                    {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Blue10),
                            contentAlignment = Alignment.Center
                        ){
                            Text(
                                text = "Save Record",
                                color = White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))

                }

            }
        }
    }
}