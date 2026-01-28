package com.example.medhomeapp.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medhomeapp.BaseActivity
import com.example.medhomeapp.R
import com.example.medhomeapp.model.DoctorModel
import com.example.medhomeapp.repository.DoctorRepoImpl
import com.example.medhomeapp.view.ui.theme.MintGreen
import com.example.medhomeapp.viewmodel.SearchDoctorsViewModel
import com.example.medhomeapp.viewmodel.SearchDoctorsViewModelFactory

class BookConsultationActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SearchDoctorsScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchDoctorsScreen() {
    val context = LocalContext.current
    val activity = context as? Activity

    val doctorRepo = DoctorRepoImpl()
    val viewModel: SearchDoctorsViewModel = viewModel(
        factory = SearchDoctorsViewModelFactory(doctorRepo)
    )

    val filteredDoctors by viewModel.filteredDoctors.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val specializations = viewModel.getSpecializations()

    var showFilterSheet by remember { mutableStateOf(false) }
    var selectedSpecialization by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MintGreen,
                    titleContentColor = Color.White
                ),
                title = { Text(stringResource(R.string.find_doctors)) },
                navigationIcon = {
                    IconButton(onClick = { activity?.finish() }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_arrow_back_ios_new_24),
                            contentDescription = stringResource(R.string.back),
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterSheet = true }) {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = stringResource(R.string.filter),
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.searchDoctors(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text(stringResource(R.string.search_by_name_spec)) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = stringResource(R.string.Search))
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.searchDoctors("") }) {
                            Icon(Icons.Default.Clear, contentDescription = stringResource(R.string.clear))
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )

            // Specialization Chips
            if (specializations.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedSpecialization.isEmpty(),
                            onClick = {
                                selectedSpecialization = ""
                                viewModel.filterBySpecialization("")
                            },
                            label = { Text(stringResource(R.string.all)) }
                        )
                    }
                    items(specializations) { spec ->
                        FilterChip(
                            selected = selectedSpecialization == spec,
                            onClick = {
                                selectedSpecialization = spec
                                viewModel.filterBySpecialization(spec)
                            },
                            label = { Text(spec) }
                        )
                    }
                }
            }

            // Results Count
            Text(
                text = if (filteredDoctors.size == 1) {
                    stringResource(R.string.doctors_found, filteredDoctors.size)
                } else {
                    stringResource(R.string.doctors_found_plural, filteredDoctors.size)
                },
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Doctor List
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MintGreen)
                }
            } else if (filteredDoctors.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.SearchOff,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.no_doctors_found),
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredDoctors) { doctor ->
                        DoctorCard(
                            doctor = doctor,
                            onClick = {
                                val intent = Intent(context, DoctorDetailActivity::class.java)
                                intent.putExtra("DOCTOR_ID", doctor.id)
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        }
    }

    // Filter Bottom Sheet
    if (showFilterSheet) {
        FilterBottomSheet(
            viewModel = viewModel,
            onDismiss = { showFilterSheet = false }
        )
    }
}

@Composable
fun DoctorCard(
    doctor: DoctorModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Doctor Image
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(MintGreen.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MintGreen
                )
            }

            // Doctor Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = doctor.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C3E50)
                )

                Text(
                    text = doctor.specialization,
                    fontSize = 14.sp,
                    color = MintGreen,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFFFFB300)
                    )
                    Text(
                        text = String.format("%.1f", doctor.averageRating),
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "(${doctor.totalRatings})",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Work,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${doctor.experience} ${stringResource(R.string.yrs)}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Text(
                        text = "•",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Text(
                        text = "NPR ${doctor.consultationFee.toInt()}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2C3E50)
                    )
                }
            }

            // Arrow Icon
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    viewModel: SearchDoctorsViewModel,
    onDismiss: () -> Unit
) {
    val currentFilter by viewModel.currentFilter.collectAsState()
    var minRating by remember { mutableFloatStateOf(currentFilter.minRating) }
    var minFee by remember { mutableDoubleStateOf(currentFilter.minFee) }
    var maxFee by remember { mutableDoubleStateOf(currentFilter.maxFee) }
    var sortBy by remember { mutableStateOf(currentFilter.sortBy) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = stringResource(R.string.filter_and_sort),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            // Minimum Rating
            Column {
                Text(
                    text = stringResource(R.string.minimum_rating, minRating),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Slider(
                    value = minRating,
                    onValueChange = { minRating = it },
                    valueRange = 0f..5f,
                    steps = 9,
                    colors = SliderDefaults.colors(
                        thumbColor = MintGreen,
                        activeTrackColor = MintGreen
                    )
                )
            }

            // Fee Range
            Column {
                Text(
                    text = stringResource(R.string.fee_range, minFee.toInt(), maxFee.toInt()),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                RangeSlider(
                    value = minFee.toFloat()..maxFee.toFloat(),
                    onValueChange = { range ->
                        minFee = range.start.toDouble()
                        maxFee = range.endInclusive.toDouble()
                    },
                    valueRange = 0f..10000f,
                    steps = 19,
                    colors = SliderDefaults.colors(
                        thumbColor = MintGreen,
                        activeTrackColor = MintGreen
                    )
                )
            }

            // Sort By
            Column {
                Text(
                    text = stringResource(R.string.sort_by),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SortOption(stringResource(R.string.rating_high_to_low), "rating_desc", sortBy) { sortBy = it }
                    SortOption(stringResource(R.string.fee_low_to_high), "fee_asc", sortBy) { sortBy = it }
                    SortOption(stringResource(R.string.fee_high_to_low), "fee_desc", sortBy) { sortBy = it }
                    SortOption(stringResource(R.string.experience), "experience_desc", sortBy) { sortBy = it }
                }
            }

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        minRating = 0f
                        minFee = 0.0
                        maxFee = 10000.0
                        sortBy = "rating_desc"
                        viewModel.clearFilters()
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.clear))
                }

                Button(
                    onClick = {
                        viewModel.updateFilter(
                            currentFilter.copy(
                                minRating = minRating,
                                minFee = minFee,
                                maxFee = maxFee,
                                sortBy = sortBy
                            )
                        )
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MintGreen)
                ) {
                    Text(stringResource(R.string.apply))
                }
            }
        }
    }
}

@Composable
fun SortOption(
    label: String,
    value: String,
    selectedValue: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect(value) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selectedValue == value,
            onClick = { onSelect(value) },
            colors = RadioButtonDefaults.colors(selectedColor = MintGreen)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, fontSize = 14.sp)
    }
}