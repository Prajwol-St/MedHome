package com.example.medhomeapp.view

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhomeapp.R
import com.example.medhomeapp.ui.theme.Blue10

class BloodDonationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BloodDonationBody()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodDonationBody() {
    val context = LocalContext.current
    val activity = context as Activity
    var currentScreen by remember { mutableStateOf("main") }

    when (currentScreen) {
        "main" -> {
            MainDonationScreen(
                activity = activity,
                onPostRequestClick = { currentScreen = "post_request" },
                onJoinDonorClick = { currentScreen = "join_donor" }
            )
        }

        "post_request" -> {
            PostBloodRequestScreen(
                onBackClick = { currentScreen = "main" }
            )
        }

        "join_donor" -> {
            JoinDonorListScreen(
                onBackClick = { currentScreen = "main" }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDonationScreen(
    activity: Activity,
    onPostRequestClick: () -> Unit,
    onJoinDonorClick: () -> Unit
) {
    val bloodGroups = listOf("All", "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")
    var selectedGroup by remember { mutableStateOf("All") }

    val sampleRequests = remember {
        listOf(
            BloodRequestUI(
                id = "1",
                patientName = "John Doe",
                bloodGroup = "B+",
                hospital = "City Hospital",
                location = "Kathnandu",
                unitsNeeded = "1",
                timeAgo = "8 days ago",
                urgency = "Urgent",
                contactNumber = "9876543210"
            ),
            BloodRequestUI(
                id = "2",
                patientName = "Jane ",
                bloodGroup = "A+",
                hospital = "General Hospital",
                location = "Bhaktapur",
                unitsNeeded = "1",
                timeAgo = "22 hr ago",
                urgency = "Within 24 hours",
                contactNumber = "9876543211"
            )
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Blue10,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White,
                ),
                title = { Text("Blood Donation", fontWeight = FontWeight.Bold) },
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
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White,
                            containerColor = Color.White.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_history_24),
                            contentDescription = null
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FloatingActionButton(
                    onClick = onJoinDonorClick,
                    containerColor = Blue10,
                    shape = CircleShape
                ) {
                    Icon(
                        painter = painterResource(R.drawable.outline_person_add_24),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                FloatingActionButton(
                    onClick = onPostRequestClick,
                    containerColor = Blue10,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFFFF5F5))
        ) {

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(bloodGroups.size) { index ->
                        val group = bloodGroups[index]
                        FilterChip(
                            selected = selectedGroup == group,
                            onClick = { selectedGroup = group },
                            label = { Text(group) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Blue10,
                                selectedLabelColor = Color.White,
                                containerColor = Color.White,
                                labelColor = Color(0xFF2D3436)
                            )
                        )
                    }
                }
            }


            item {
                Text(
                    text = "Nearby Blood Requests",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3436),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
            }


            items(sampleRequests.size) { index ->
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    BloodRequestCard(
                        request = sampleRequests[index],
                        onContactClick = { request ->

                        }
                    )
                }

                if (index < sampleRequests.size - 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }


            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun BloodRequestCard(
    request: BloodRequestUI,
    onContactClick: (BloodRequestUI) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {  },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = request.patientName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3436)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${request.unitsNeeded} unit needed",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                Surface(
                    color = when (request.urgency) {
                        "Urgent" -> Color(0xFFFFEBEE)
                        "Within 24 hours" -> Color(0xFFFFF8E1)
                        else -> Color(0xFFE8F5E9)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = request.urgency,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = when (request.urgency) {
                            "Urgent" -> Color(0xFFD32F2F)
                            "Within 24 hours" -> Color(0xFFF57C00)
                            else -> Color(0xFF388E3C)
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))


            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Blue10,
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = request.bloodGroup,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = request.hospital,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2D3436)
                    )
                    Text(
                        text = request.location,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        painter = painterResource(R.drawable.baseline_access_time_filled_24),
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = request.timeAgo,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Button(
                    onClick = { onContactClick(request) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Blue10
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Contact",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostBloodRequestScreen(onBackClick: () -> Unit) {
    var patientName by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("") }
    var unitsNeeded by remember { mutableStateOf("1") }
    var hospital by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var contactNumber by remember { mutableStateOf("") }
    var urgencyLevel by remember { mutableStateOf("") }
    var additionalNotes by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Blue10,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                ),
                title = { Text("Post Blood Request", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFFFF5F5))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Patient Name", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                OutlinedTextField(
                    value = patientName,
                    onValueChange = { patientName = it },
                    placeholder = { Text("Enter patient name or leave anonymous") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Blue10,
                        focusedLabelColor = Blue10
                    )
                )
            }

            item {
                Text("Required Blood Group *", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = bloodGroup,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Select blood group needed") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier
                            .fillMaxWidth(),

                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Blue10
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-").forEach { group ->
                            DropdownMenuItem(
                                text = { Text(group) },
                                onClick = {
                                    bloodGroup = group
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                Text("Units Needed *", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                OutlinedTextField(
                    value = unitsNeeded,
                    onValueChange = { unitsNeeded = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Blue10
                    )
                )
            }

            item {
                Text("Hospital *", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                OutlinedTextField(
                    value = hospital,
                    onValueChange = { hospital = it },
                    placeholder = { Text("e.g. City General Hospital") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Blue10
                    )
                )
            }

            item {
                Text("Location *", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    placeholder = { Text("Satdobato,Lalitpur") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Blue10
                    )
                )
            }

            item {
                Text("Contact Number *", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                OutlinedTextField(
                    value = contactNumber,
                    onValueChange = { contactNumber = it },
                    placeholder = { Text("+977 XXXXX XXXXX") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Blue10
                    )
                )
            }

            item {
                Text("Urgency Level *", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                var urgencyExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = urgencyExpanded,
                    onExpandedChange = { urgencyExpanded = !urgencyExpanded }
                ) {
                    OutlinedTextField(
                        value = urgencyLevel,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Select urgency") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(urgencyExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Blue10
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = urgencyExpanded,
                        onDismissRequest = { urgencyExpanded = false }
                    ) {
                        listOf("Urgent", "Within 24 hours", "Within a week").forEach { level ->
                            DropdownMenuItem(
                                text = { Text(level) },
                                onClick = {
                                    urgencyLevel = level
                                    urgencyExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                Text("Additional Notes", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                OutlinedTextField(
                    value = additionalNotes,
                    onValueChange = { additionalNotes = it },
                    placeholder = { Text("Any additional information...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Blue10
                    )
                )
            }

            item {
                Button(
                    onClick = {  },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Blue10
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Post Blood Request", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinDonorListScreen(onBackClick: () -> Unit) {
    var bloodGroup by remember { mutableStateOf("") }
    var isAvailable by remember { mutableStateOf(false) }
    var isEmergencyAvailable by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Blue10,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                ),
                title = { Text("Join Donor List", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFFFF5F5))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Blood Group *", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = bloodGroup,
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Select your blood group") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Blue10
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-").forEach { group ->
                                DropdownMenuItem(
                                    text = { Text(group) },
                                    onClick = {
                                        bloodGroup = group
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = isAvailable,
                            onCheckedChange = { isAvailable = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Blue10
                            )
                        )
                        Text("I am currently available to donate")
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = isEmergencyAvailable,
                            onCheckedChange = { isEmergencyAvailable = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Blue10
                            )
                        )
                        Text("Available for emergency calls")
                    }

                    Button(
                        onClick = {  },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Blue10
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Save & Join Donor List", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Data class for UI
data class BloodRequestUI(
    val id: String,
    val patientName: String,
    val bloodGroup: String,
    val hospital: String,
    val location: String,
    val unitsNeeded: String,
    val timeAgo: String,
    val urgency: String,
    val contactNumber: String
)