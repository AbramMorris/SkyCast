package com.example.skycast.ui.screens

import android.app.TimePickerDialog
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.skycast.data.models.AlarmEntity
import com.example.skycast.ui.navigation.ScreenRoute
import com.example.skycast.ui.theme.BlueBlackBack
import com.example.skycast.util.AlarmScheduler
import com.example.skycast.viewmodel.AlarmViewModel
import java.util.*



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmScreenUI(navController: NavController, viewModel: AlarmViewModel) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // State variables
    var startHour by remember { mutableStateOf(calendar.get(Calendar.HOUR_OF_DAY)) }
    var startMinute by remember { mutableStateOf(calendar.get(Calendar.MINUTE)) }
    var selectedTime by remember { mutableStateOf("Select Time") }

    var selectedDate by remember { mutableStateOf("Select Date") }
    var selectedYear by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    var selectedDay by remember { mutableStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val selectedCity by remember { mutableStateOf(savedStateHandle?.get<String>("selectedCity") ?: "Select City") }
    val selectedLat by remember { mutableStateOf(savedStateHandle?.get<Double>("selectedLat") ?: 0.0) }
    val selectedLng by remember { mutableStateOf(savedStateHandle?.get<Double>("selectedLng") ?: 0.0) }

    // Show Time Picker
    fun showTimePicker() {
        TimePickerDialog(
            context,
            { _, hour, minute ->
                startHour = hour
                startMinute = minute
                selectedTime = String.format("%02d:%02d", hour, minute)
            },
            startHour,
            startMinute,
            false
        ).show()
    }

    // Show Date Picker
    fun showDatePicker() {
        android.app.DatePickerDialog(
            context,
            { _, year, month, day ->
                selectedYear = year
                selectedMonth = month
                selectedDay = day
                selectedDate = "$day/${month + 1}/$year"
                Log.d("datepicker", "Latitude: $selectedDate")

            },
            selectedYear,
            selectedMonth,
            selectedDay
        ).show()
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Alarm settings
        ModalBottomSheet(
            onDismissRequest = { navController.popBackStack() },
            sheetState = rememberModalBottomSheetState(),
            modifier = Modifier
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Time Picker
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showTimePicker() }
                        .padding(16.dp)
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = "Clock", tint = BlueBlackBack)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(selectedTime, color = BlueBlackBack, fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Date Picker
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker() }
                        .padding(16.dp)
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = "Date", tint = BlueBlackBack)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(selectedDate, color = BlueBlackBack, fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Select City Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate(ScreenRoute.AlarmMap.route) } // Navigate to MapSelectionScreen
                        .padding(16.dp)
                ) {
                    Icon(Icons.Default.Place, contentDescription = "Location", tint = BlueBlackBack)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(selectedCity, color = BlueBlackBack, fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Save & Cancel Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            val alarm = AlarmEntity(
                                id =System.currentTimeMillis().toInt(),
                                hour = startHour,
                                minute = startMinute,
                                label = "Alarm on $selectedDate in $selectedCity",
                                latitude = selectedLat,
                                longitude = selectedLng
                            )
                            Log.d("dateIntity", "Latitude: $selectedDate")
                            viewModel.insertAlarm(alarm) // Save to database
                            AlarmScheduler.scheduleAlarm(context, alarm)
                            navController.popBackStack()
                            Toast.makeText(context, "Alarm set for $selectedTime in $selectedCity", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text("SAVE", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4444))
                    ) {
                        Text("CANCEL", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
