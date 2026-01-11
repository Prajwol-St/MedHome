package com.example.medhomeapp.view

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhomeapp.BaseActivity
import com.example.medhomeapp.R
import com.example.medhomeapp.model.FoodItemModel
import com.example.medhomeapp.model.api.FoodSearchResult
import com.example.medhomeapp.ui.theme.SageGreen
import com.example.medhomeapp.viewmodel.CalorieViewModel
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset

class CaloriesCalculatorActivity : BaseActivity() {

    private val viewModel: CalorieViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CaloriesCalculatorBody(viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaloriesCalculatorBody(viewModel: CalorieViewModel) {
    val context = LocalContext.current
    val activity = context as Activity


    var showSearchBottomSheet by remember { mutableStateOf(false) }
    var showManualAddSheet by remember { mutableStateOf(false) }
    var showGoalSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDetailSheet by remember { mutableStateOf(false) }
    var foodToDelete by remember { mutableStateOf<FoodItemModel?>(null) }
    var selectedFood by remember { mutableStateOf<FoodItemModel?>(null) }
    var selectedMealType by remember { mutableStateOf("other") }
    var selectedTab by remember { mutableStateOf(0) } // 0: All, 1: Breakfast, 2: Lunch, 3: Dinner, 4: Snacks


    val searchSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val manualSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val goalSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val detailSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)


    var searchQuery by remember { mutableStateOf("") }
    var manualFoodName by remember { mutableStateOf("") }
    var manualCalories by remember { mutableStateOf("") }
    var manualProtein by remember { mutableStateOf("") }
    var manualCarbs by remember { mutableStateOf("") }
    var manualFat by remember { mutableStateOf("") }
    var servingAmount by remember { mutableStateOf("1.0") }
    var goalCalories by remember { mutableStateOf("") }
    var goalProtein by remember { mutableStateOf("") }
    var goalCarbs by remember { mutableStateOf("") }
    var goalFat by remember { mutableStateOf("") }


    val todaysFoodItems by viewModel.todaysFoodItems.observeAsState(emptyList())
    val dailySummary by viewModel.dailySummary.observeAsState()
    val searchResults by viewModel.searchResults.observeAsState(emptyList())
    val isSearching by viewModel.isSearching.observeAsState(false)
    val isLoadingFoodItems by viewModel.isLoadingFoodItems.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState()
    val successMessage by viewModel.successMessage.observeAsState()
    val calorieGoal by viewModel.calorieGoal.observeAsState()



    LaunchedEffect(Unit) {
        viewModel.loadTodaysData()
        viewModel.loadCalorieGoal()
    }


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


    val filteredFoodItems = when (selectedTab) {
        1 -> todaysFoodItems.filter { it.isBreakfast() }
        2 -> todaysFoodItems.filter { it.isLunch() }
        3 -> todaysFoodItems.filter { it.isDinner() }
        4 -> todaysFoodItems.filter { it.isSnack() }
        else -> todaysFoodItems
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SageGreen,
                    titleContentColor = Color.White
                ),
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Calorie Calculator", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "Powered by USDA Food Database",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
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
                }
            )
        }
    ) { padding ->

        // Replace the Box(modifier = Modifier.padding(padding)) { } in Part 1 with this:

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // ============================================
            // SUMMARY CARD
            // ============================================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SageGreen)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "Total Calories Today",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${dailySummary?.totalCalories?.toInt() ?: 0}",
                            color = Color.White,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold
                        )

                        // Circular Progress Indicator
                        Box(
                            modifier = Modifier.size(80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                progress = dailySummary?.calorieProgress ?: 0f,
                                modifier = Modifier.fillMaxSize(),
                                color = Color.White,
                                strokeWidth = 6.dp,
                                trackColor = Color.White.copy(alpha = 0.3f)
                            )
                            Text(
                                text = "${dailySummary?.getPercentageConsumed() ?: 0}%",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Daily Goal: ${calorieGoal?.targetCalories?.toInt() ?: 2000} kcal",
                            color = Color.White,
                            fontSize = 14.sp
                        )

                        Text(
                            "Remaining: ${dailySummary?.remainingCalories?.toInt() ?: 0} kcal",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Macros Row (if available)
                    if (dailySummary != null && dailySummary!!.totalProtein > 0) {
                        Spacer(Modifier.height(16.dp))
                        Divider(color = Color.White.copy(alpha = 0.3f))
                        Spacer(Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            MacroItem(
                                label = "Protein",
                                value = "${dailySummary?.totalProtein?.toInt() ?: 0}g",
                                color = Color.White
                            )
                            MacroItem(
                                label = "Carbs",
                                value = "${dailySummary?.totalCarbs?.toInt() ?: 0}g",
                                color = Color.White
                            )
                            MacroItem(
                                label = "Fat",
                                value = "${dailySummary?.totalFat?.toInt() ?: 0}g",
                                color = Color.White
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))


                    Button(
                        onClick = {
                            goalCalories = calorieGoal?.targetCalories?.toInt()?.toString() ?: "2000"
                            goalProtein = calorieGoal?.proteinGoal?.toInt()?.toString() ?: ""
                            goalCarbs = calorieGoal?.carbsGoal?.toInt()?.toString() ?: ""
                            goalFat = calorieGoal?.fatGoal?.toInt()?.toString() ?: ""
                            showGoalSheet = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = SageGreen
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_edit_24),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Set Daily Goal", fontWeight = FontWeight.Bold)
                    }
                }
            }


            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Search USDA Food Database",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = SageGreen
                    )

                    Spacer(Modifier.height(12.dp))

                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showSearchBottomSheet = true
                            },
                        placeholder = { Text("Search food (e.g., apple, rice, chicken)") },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.baseline_search_24),
                                contentDescription = null,
                                tint = SageGreen
                            )
                        },
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = Color.Black,
                            disabledBorderColor = SageGreen,
                            disabledPlaceholderColor = Color.Gray,
                            disabledLeadingIconColor = SageGreen
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "Results from USDA FoodData Central",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )

                    Spacer(Modifier.height(12.dp))


                    OutlinedButton(
                        onClick = {
                            manualFoodName = ""
                            manualCalories = ""
                            manualProtein = ""
                            manualCarbs = ""
                            manualFat = ""
                            servingAmount = "1.0"
                            selectedMealType = "other"
                            showManualAddSheet = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = SageGreen
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_add_24),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Add Food Manually", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Today's Meals",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SageGreen
                    )

                    Spacer(Modifier.height(12.dp))

                    // Meal Type Tabs
                    ScrollableTabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        contentColor = SageGreen,
                        edgePadding = 0.dp,
                        indicator = { tabPositions ->
                            if (selectedTab < tabPositions.size) {
                                TabRowDefaults.SecondaryIndicator(
                                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                    color = SageGreen
                                )
                            }
                        }
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = {
                                Text(
                                    "All",
                                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "🍳 Breakfast",
                                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "🍛 Lunch",
                                        fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        )
                        Tab(
                            selected = selectedTab == 3,
                            onClick = { selectedTab = 3 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "🍽️ Dinner",
                                        fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        )
                        Tab(
                            selected = selectedTab == 4,
                            onClick = { selectedTab = 4 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "🍿 Snacks",
                                        fontWeight = if (selectedTab == 4) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        )
                    }

                    Spacer(Modifier.height(16.dp))


                    if (isLoadingFoodItems) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = SageGreen)
                        }
                    }

                    else if (filteredFoodItems.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_add_24),
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "No meals added yet. Search and add food above!",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                    else {
                        Column {
                            filteredFoodItems.forEach { foodItem ->
                                FoodItemCard(
                                    foodItem = foodItem,
                                    onCardClick = {
                                        selectedFood = it
                                        showDetailSheet = true
                                    },
                                    onDeleteClick = {
                                        foodToDelete = it
                                        showDeleteDialog = true
                                    }
                                )
                                Spacer(Modifier.height(12.dp))
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))


            Button(
                onClick = {
                    Toast.makeText(context, "Daily Summary Feature", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = SageGreen
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("View Daily Summary", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(80.dp)) // Bottom padding

        }
    }

    if (showSearchBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showSearchBottomSheet = false
                searchQuery = ""
                viewModel.clearSearchResults()
            },
            sheetState = searchSheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight(0.9f)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Search USDA Food Database",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = SageGreen
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Search from 300,000+ foods with complete nutrition data",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(Modifier.height(16.dp))

                // Search Input Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        if (it.length >= 2) {
                            viewModel.searchFoods(it)
                        } else if (it.isEmpty()) {
                            viewModel.clearSearchResults()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("e.g., apple, chicken breast, rice") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.baseline_search_24),
                            contentDescription = null,
                            tint = SageGreen
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = {
                                searchQuery = ""
                                viewModel.clearSearchResults()
                            }) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_close_24),
                                    contentDescription = "Clear",
                                    tint = Color.Gray
                                )
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SageGreen,
                        cursorColor = SageGreen
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(Modifier.height(16.dp))

                when {
                    isSearching -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = SageGreen)
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    "Searching USDA database...",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }

                    searchQuery.isEmpty() -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_search_24),
                                    contentDescription = null,
                                    tint = Color.LightGray,
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    "Search for any food item",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Gray
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "Try: apple, rice, chicken, milk",
                                    fontSize = 13.sp,
                                    color = Color.LightGray
                                )
                            }
                        }
                    }

                    searchResults.isEmpty() && !isSearching -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_search_24),
                                    contentDescription = null,
                                    tint = Color.LightGray,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    "No foods found for '$searchQuery'",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "Try a different search term",
                                    fontSize = 12.sp,
                                    color = Color.LightGray
                                )
                            }
                        }
                    }

                    else -> {
                        Text(
                            "Found ${searchResults.size} results",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(Modifier.height(12.dp))

                        LazyColumn(
                            modifier = Modifier.weight(1f)
                        ) {
                            items(searchResults.size) { index ->
                                SearchResultCard(
                                    searchResult = searchResults[index],
                                    onClick = { result ->
                                        val mealTypes = arrayOf(
                                            "Other",
                                            "🍳 Breakfast",
                                            "🍛 Lunch",
                                            "🍽️ Dinner",
                                            "🍿 Snack"
                                        )

                                        val mealTypeValues = arrayOf(
                                            "other",
                                            "breakfast",
                                            "lunch",
                                            "dinner",
                                            "snack"
                                        )
                                        var selectedIndex = 0

                                        android.app.AlertDialog.Builder(context)  // Changed from androidx.appcompat
                                            .setTitle("Add ${result.description}")
                                            .setSingleChoiceItems(mealTypes, 0) { _, which ->
                                                selectedIndex = which
                                            }
                                            .setPositiveButton("Add") { dialog, _ ->
                                                viewModel.addFoodFromSearch(
                                                    searchResult = result,
                                                    servingAmount = 1.0,
                                                    mealType = mealTypeValues[selectedIndex]
                                                )
                                                showSearchBottomSheet = false
                                                searchQuery = ""
                                                viewModel.clearSearchResults()
                                                dialog.dismiss()
                                            }
                                            .setNegativeButton("Cancel") { dialog, _ ->
                                                dialog.dismiss()
                                            }
                                            .show()
                                    }
                                )
                                Spacer(Modifier.height(12.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showManualAddSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showManualAddSheet = false
                manualFoodName = ""
                manualCalories = ""
                manualProtein = ""
                manualCarbs = ""
                manualFat = ""
                servingAmount = "1.0"
                selectedMealType = "other"
            },
            sheetState = manualSheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight(0.9f)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Add Food Manually",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = SageGreen
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Enter food details and nutrition information",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(Modifier.height(20.dp))

                // Food Name
                OutlinedTextField(
                    value = manualFoodName,
                    onValueChange = { manualFoodName = it },
                    label = { Text("Food Name *") },
                    placeholder = { Text("e.g., Home-cooked Meal") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SageGreen,
                        focusedLabelColor = SageGreen,
                        cursorColor = SageGreen
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(Modifier.height(16.dp))

                // Calories (Required)
                OutlinedTextField(
                    value = manualCalories,
                    onValueChange = { manualCalories = it },
                    label = { Text("Calories (kcal) *") },
                    placeholder = { Text("e.g., 250") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SageGreen,
                        focusedLabelColor = SageGreen,
                        cursorColor = SageGreen
                    ),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Macronutrients (Optional)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )

                Spacer(Modifier.height(12.dp))

                // Protein
                OutlinedTextField(
                    value = manualProtein,
                    onValueChange = { manualProtein = it },
                    label = { Text("Protein (g)") },
                    placeholder = { Text("e.g., 20") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4CAF50),
                        focusedLabelColor = Color(0xFF4CAF50),
                        cursorColor = Color(0xFF4CAF50)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    leadingIcon = {
                        Text(
                            "P",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50),
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                )

                Spacer(Modifier.height(12.dp))

                // Carbs
                OutlinedTextField(
                    value = manualCarbs,
                    onValueChange = { manualCarbs = it },
                    label = { Text("Carbohydrates (g)") },
                    placeholder = { Text("e.g., 30") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2196F3),
                        focusedLabelColor = Color(0xFF2196F3),
                        cursorColor = Color(0xFF2196F3)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    leadingIcon = {
                        Text(
                            "C",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2196F3),
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                )

                Spacer(Modifier.height(12.dp))

                // Fat
                OutlinedTextField(
                    value = manualFat,
                    onValueChange = { manualFat = it },
                    label = { Text("Fat (g)") },
                    placeholder = { Text("e.g., 10") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF9800),
                        focusedLabelColor = Color(0xFFFF9800),
                        cursorColor = Color(0xFFFF9800)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    leadingIcon = {
                        Text(
                            "F",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF9800),
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                )

                Spacer(Modifier.height(16.dp))

                // Serving Amount
                OutlinedTextField(
                    value = servingAmount,
                    onValueChange = { servingAmount = it },
                    label = { Text("Serving Amount") },
                    placeholder = { Text("e.g., 1.5") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SageGreen,
                        focusedLabelColor = SageGreen,
                        cursorColor = SageGreen
                    ),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    supportingText = {
                        Text("1.0 = 1 serving, 1.5 = 1.5 servings", fontSize = 11.sp)
                    }
                )

                Spacer(Modifier.height(16.dp))

                // Meal Type Selection
                Text(
                    text = "Meal Type *",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MealTypeChip(
                        label = "🍳 Breakfast",
                        isSelected = selectedMealType == "breakfast",
                        onClick = { selectedMealType = "breakfast" },
                        modifier = Modifier.weight(1f)
                    )
                    MealTypeChip(
                        label = "🍛 Lunch",
                        isSelected = selectedMealType == "lunch",
                        onClick = { selectedMealType = "lunch" },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MealTypeChip(
                        label = "🍽️ Dinner",
                        isSelected = selectedMealType == "dinner",
                        onClick = { selectedMealType = "dinner" },
                        modifier = Modifier.weight(1f)
                    )
                    MealTypeChip(
                        label = "🍿 Snack",
                        isSelected = selectedMealType == "snack",
                        onClick = { selectedMealType = "snack" },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(8.dp))

                MealTypeChip(
                    label = "Other",
                    isSelected = selectedMealType == "other",
                    onClick = { selectedMealType = "other" },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))

                // Add Button
                Button(
                    onClick = {
                        if (manualFoodName.isNotEmpty() && manualCalories.isNotEmpty()) {
                            val calories = manualCalories.toDoubleOrNull()
                            val protein = manualProtein.toDoubleOrNull() ?: 0.0
                            val carbs = manualCarbs.toDoubleOrNull() ?: 0.0
                            val fat = manualFat.toDoubleOrNull() ?: 0.0
                            val serving = servingAmount.toDoubleOrNull() ?: 1.0

                            if (calories != null && calories > 0) {
                                viewModel.addManualFood(
                                    name = manualFoodName,
                                    calories = calories,
                                    protein = protein,
                                    carbs = carbs,
                                    fat = fat,
                                    servingSize = "1 serving",
                                    servingAmount = serving,
                                    mealType = selectedMealType
                                )
                                showManualAddSheet = false
                            } else {
                                Toast.makeText(
                                    context,
                                    "Please enter valid calories",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Food name and calories are required",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SageGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_add_24),
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Add Food",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }

    if (showGoalSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showGoalSheet = false
                goalCalories = ""
                goalProtein = ""
                goalCarbs = ""
                goalFat = ""
            },
            sheetState = goalSheetState
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Set Daily Goal",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = SageGreen
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Set your daily calorie and macro targets",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(Modifier.height(20.dp))

                // Calorie Goal (Required)
                OutlinedTextField(
                    value = goalCalories,
                    onValueChange = { goalCalories = it },
                    label = { Text("Daily Calorie Goal (kcal) *") },
                    placeholder = { Text("e.g., 2000") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SageGreen,
                        focusedLabelColor = SageGreen,
                        cursorColor = SageGreen
                    ),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    supportingText = {
                        Text("Recommended: 1500-2500 kcal/day", fontSize = 11.sp)
                    }
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Macro Goals (Optional)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )

                Spacer(Modifier.height(12.dp))


                OutlinedTextField(
                    value = goalProtein,
                    onValueChange = { goalProtein = it },
                    label = { Text("Protein Goal (g)") },
                    placeholder = { Text("e.g., 150") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4CAF50),
                        focusedLabelColor = Color(0xFF4CAF50),
                        cursorColor = Color(0xFF4CAF50)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF4CAF50).copy(alpha = 0.1f), CircleShape)
                                .padding(8.dp)
                        ) {
                            Text(
                                "P",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )
                        }
                    }
                )

                Spacer(Modifier.height(12.dp))


                OutlinedTextField(
                    value = goalCarbs,
                    onValueChange = { goalCarbs = it },
                    label = { Text("Carbohydrates Goal (g)") },
                    placeholder = { Text("e.g., 200") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2196F3),
                        focusedLabelColor = Color(0xFF2196F3),
                        cursorColor = Color(0xFF2196F3)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF2196F3).copy(alpha = 0.1f), CircleShape)
                                .padding(8.dp)
                        ) {
                            Text(
                                "C",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2196F3)
                            )
                        }
                    }
                )

                Spacer(Modifier.height(12.dp))

                // Fat Goal
                OutlinedTextField(
                    value = goalFat,
                    onValueChange = { goalFat = it },
                    label = { Text("Fat Goal (g)") },
                    placeholder = { Text("e.g., 50") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF9800),
                        focusedLabelColor = Color(0xFFFF9800),
                        cursorColor = Color(0xFFFF9800)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFFF9800).copy(alpha = 0.1f), CircleShape)
                                .padding(8.dp)
                        ) {
                            Text(
                                "F",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF9800)
                            )
                        }
                    }
                )

                Spacer(Modifier.height(24.dp))


                Button(
                    onClick = {
                        val calories = goalCalories.toDoubleOrNull()
                        val protein = goalProtein.toDoubleOrNull() ?: 0.0
                        val carbs = goalCarbs.toDoubleOrNull() ?: 0.0
                        val fat = goalFat.toDoubleOrNull() ?: 0.0

                        if (calories != null && calories > 0) {
                            viewModel.setCalorieGoal(
                                targetCalories = calories,
                                proteinGoal = protein,
                                carbsGoal = carbs,
                                fatGoal = fat
                            )
                            showGoalSheet = false
                        } else {
                            Toast.makeText(
                                context,
                                "Please enter a valid calorie goal",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SageGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_check_24),
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Save Goal",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
    if (showDetailSheet && selectedFood != null) {
        ModalBottomSheet(
            onDismissRequest = {
                showDetailSheet = false
                selectedFood = null
            },
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
                        text = "Food Details",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = {
                            selectedFood?.let { food ->
                                foodToDelete = food
                                showDetailSheet = false
                                showDeleteDialog = true
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_delete_24),
                            contentDescription = "Delete",
                            tint = Color.Red
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    text = "Food Name",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = selectedFood?.name ?: "",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Serving",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${selectedFood?.servingAmount} × ${selectedFood?.servingSize}",
                    fontSize = 16.sp
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Meal Type",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .background(
                            when (selectedFood?.mealType) {
                                "breakfast" -> Color(0xFFFFEBEE)
                                "lunch" -> Color(0xFFFFF3E0)
                                "dinner" -> Color(0xFFE8F5E9)
                                "snack" -> Color(0xFFF3E5F5)
                                else -> Color(0xFFEEEEEE)
                            },
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = when (selectedFood?.mealType) {
                            "breakfast" -> "🍳 Breakfast"
                            "lunch" -> "🍛 Lunch"
                            "dinner" -> "🍽️ Dinner"
                            "snack" -> "🍿 Snack"
                            else -> "Other"
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(Modifier.height(20.dp))
                Divider(color = Color.LightGray)
                Spacer(Modifier.height(20.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SageGreen.copy(alpha = 0.1f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Nutrition Information",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SageGreen
                        )

                        Spacer(Modifier.height(16.dp))

                        // Calories
                        NutritionRow(
                            label = "Calories",
                            value = "${selectedFood?.getTotalCalories()?.toInt()} kcal",
                            color = SageGreen
                        )

                        Spacer(Modifier.height(12.dp))
                        Divider(color = Color.LightGray.copy(alpha = 0.5f))
                        Spacer(Modifier.height(12.dp))

                        // Protein
                        if ((selectedFood?.protein ?: 0.0) > 0) {
                            NutritionRow(
                                label = "Protein",
                                value = "${selectedFood?.getTotalProtein()?.toInt()}g",
                                color = Color(0xFF4CAF50)
                            )
                            Spacer(Modifier.height(8.dp))
                        }

                        // Carbs
                        if ((selectedFood?.carbs ?: 0.0) > 0) {
                            NutritionRow(
                                label = "Carbohydrates",
                                value = "${selectedFood?.getTotalCarbs()?.toInt()}g",
                                color = Color(0xFF2196F3)
                            )
                            Spacer(Modifier.height(8.dp))
                        }

                        // Fat
                        if ((selectedFood?.fat ?: 0.0) > 0) {
                            NutritionRow(
                                label = "Fat",
                                value = "${selectedFood?.getTotalFat()?.toInt()}g",
                                color = Color(0xFFFF9800)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Source Information
                if (selectedFood?.isFromApi() == true) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_info_24),
                                contentDescription = null,
                                tint = Color(0xFF1976D2),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Data from USDA FoodData Central",
                                fontSize = 12.sp,
                                color = Color(0xFF1565C0),
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))
            }
        }
    }

    if (showDeleteDialog && foodToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                foodToDelete = null
            },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.baseline_delete_24),
                    contentDescription = null,
                    tint = Color.Red,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    "Delete Food Item?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Are you sure you want to delete '${foodToDelete?.name}'? This action cannot be undone.",
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        foodToDelete?.let { food ->
                            viewModel.deleteFoodItem(food.id)
                        }
                        showDeleteDialog = false
                        foodToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Delete", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showDeleteDialog = false
                        foodToDelete = null
                    }
                ) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }


}


@Composable
fun MacroItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = color,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = color.copy(alpha = 0.8f),
            fontSize = 12.sp
        )
    }
}

@Composable
fun FoodItemCard(
    foodItem: FoodItemModel,
    onCardClick: (FoodItemModel) -> Unit,
    onDeleteClick: (FoodItemModel) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick(foodItem) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Food Name
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = foodItem.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${foodItem.servingAmount} × ${foodItem.servingSize}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                // Calories Badge
                Box(
                    modifier = Modifier
                        .background(SageGreen, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${foodItem.getTotalCalories().toInt()} kcal",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Menu Button
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_more_vert_24),
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("View Details") },
                            onClick = {
                                showMenu = false
                                onCardClick(foodItem)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete", color = Color.Red) },
                            onClick = {
                                showMenu = false
                                onDeleteClick(foodItem)
                            }
                        )
                    }
                }
            }


            if (foodItem.protein > 0 || foodItem.carbs > 0 || foodItem.fat > 0) {
                Spacer(Modifier.height(12.dp))
                Divider(color = Color.LightGray)
                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    if (foodItem.protein > 0) {
                        MacroChip(
                            label = "P",
                            value = "${foodItem.getTotalProtein().toInt()}g",
                            color = Color(0xFF4CAF50)
                        )
                    }
                    if (foodItem.carbs > 0) {
                        MacroChip(
                            label = "C",
                            value = "${foodItem.getTotalCarbs().toInt()}g",
                            color = Color(0xFF2196F3)
                        )
                    }
                    if (foodItem.fat > 0) {
                        MacroChip(
                            label = "F",
                            value = "${foodItem.getTotalFat().toInt()}g",
                            color = Color(0xFFFF9800)
                        )
                    }
                }
            }

            // Meal Type Badge
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .background(
                        when (foodItem.mealType) {
                            "breakfast" -> Color(0xFFFFEBEE)
                            "lunch" -> Color(0xFFFFF3E0)
                            "dinner" -> Color(0xFFE8F5E9)
                            "snack" -> Color(0xFFF3E5F5)
                            else -> Color(0xFFEEEEEE)
                        },
                        RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = when (foodItem.mealType) {
                        "breakfast" -> "🍳 Breakfast"
                        "lunch" -> "🍛 Lunch"
                        "dinner" -> "🍽️ Dinner"
                        "snack" -> "🍿 Snack"
                        else -> "Other"
                    },
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            // API Source Badge (if from USDA)
            if (foodItem.isFromApi()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "From USDA Database",
                    fontSize = 10.sp,
                    color = Color.Gray,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}

@Composable
fun MacroChip(label: String, value: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = value,
            fontSize = 11.sp,
            color = Color.DarkGray
        )
    }
}

@Composable
fun SearchResultCard(
    searchResult: FoodSearchResult,
    onClick: (FoodSearchResult) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(searchResult) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = searchResult.description,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    maxLines = 2
                )

                Spacer(Modifier.height(6.dp))

                // Brand Name (if available)
                if (searchResult.isBrandedFood()) {
                    Text(
                        text = searchResult.brandOwner,
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                    Spacer(Modifier.height(4.dp))
                }

                // Nutrition Summary
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (searchResult.getCalories() > 0) {
                        NutrientBadge(
                            label = "Cal",
                            value = "${searchResult.getCalories().toInt()}",
                            color = SageGreen
                        )
                    }
                    if (searchResult.getProtein() > 0) {
                        NutrientBadge(
                            label = "P",
                            value = "${searchResult.getProtein().toInt()}g",
                            color = Color(0xFF4CAF50)
                        )
                    }
                    if (searchResult.getCarbs() > 0) {
                        NutrientBadge(
                            label = "C",
                            value = "${searchResult.getCarbs().toInt()}g",
                            color = Color(0xFF2196F3)
                        )
                    }
                    if (searchResult.getFat() > 0) {
                        NutrientBadge(
                            label = "F",
                            value = "${searchResult.getFat().toInt()}g",
                            color = Color(0xFFFF9800)
                        )
                    }
                }

                Spacer(Modifier.height(6.dp))

                // Data Type Badge
                Text(
                    text = when (searchResult.dataType) {
                        "Foundation" -> "Foundation Food"
                        "SR Legacy" -> "Standard Reference"
                        "Branded" -> "Branded Food"
                        else -> searchResult.dataType
                    },
                    fontSize = 10.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .background(Color(0xFFE0E0E0), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Icon(
                painter = painterResource(R.drawable.baseline_add_24),
                contentDescription = "Add",
                tint = SageGreen,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun NutrientBadge(label: String, value: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Spacer(Modifier.width(3.dp))
        Text(
            text = value,
            fontSize = 10.sp,
            color = Color.DarkGray,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun MealTypeChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) SageGreen else Color(0xFFF5F5F5)
        ),
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.White else Color.DarkGray
            )
        }
    }
}

@Composable
fun NutritionRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color, CircleShape)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 15.sp,
                color = Color.DarkGray
            )
        }
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}