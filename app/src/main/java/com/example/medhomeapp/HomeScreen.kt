package com.example.medhomeapp

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medhomeapp.ui.theme.Blue10

@Composable
fun HomeScreen(){

    val context = LocalContext.current
    val activity = context as Activity

        Column(
            modifier = Modifier
                .fillMaxSize()

                .background(White)
        ){

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 19.dp, vertical = 8.dp)

            ){
                Image(
                    painter = painterResource(R.drawable.baseline_person_24),
                    contentDescription = null,
                    modifier = Modifier
                        .height(29.dp)
                        .width(29.dp)
                        .clip(shape = CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column{
                    Text("Welcome", style = TextStyle(
                        fontSize = 22.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold
                    ))
                    Text("Username", style = TextStyle(
                        fontSize = 19.sp,
                        color = Color.Black
                    ) )

                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 19.dp, vertical = 15.dp)
            ){
                OptionCard(
                    Modifier
                        .clickable(onClick = {
                            val intent = Intent(
                                context,
                                HealthRecords::class.java

                            )
                            context.startActivity(intent)
                        })
                        .weight(.8f),

                    R.drawable.bookingpast,
                    "Past Booking"

                )
                Spacer(modifier = Modifier.width(15.dp))
                OptionCard(
                    Modifier
                        .weight(.8f),
                    R.drawable.baseline_phone_in_talk_24,
                    "Book Consultation"
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 19.dp, vertical = 15.dp)
            ){
                OptionCard(
                    Modifier
                        .weight(.8f),
                    R.drawable.baseline_chat_24,
                    "AI Health Assistant"

                )
                Spacer(modifier = Modifier.width(15.dp))
                OptionCard(
                    Modifier
                        .weight(.8f),
                    R.drawable.baseline_access_time_filled_24,
                    "Medicine Reminders"
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 19.dp, vertical = 15.dp)
            ){
                OptionCard(
                    Modifier
                        .weight(.8f),
                    R.drawable.baseline_edit_calendar_24,
                    "Appointments"

                )
                Spacer(modifier = Modifier.width(15.dp))
                OptionCard(
                    Modifier
                        .weight(.8f),
                    R.drawable.baseline_directions_run_24,
                    "Calories Calculator"
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 19.dp, vertical = 15.dp)
            ){
                OptionCard(
                    Modifier
                        .weight(.8f),
                    R.drawable.baseline_bloodtype_24,
                    "Blood Donation"

                )
                Spacer(modifier = Modifier.width(15.dp))
                OptionCard(
                    Modifier
                        .weight(.8f),
                    R.drawable.box,
                    "Health Packages"
                )
            }

        }
    }

@Composable
fun OptionCard(modifier: Modifier, image: Int, label: String){
    Card(
        modifier = modifier

            .height(110.dp),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ){
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Image(
                painter = painterResource(image),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Blue10),
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(label, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }

    }

}
