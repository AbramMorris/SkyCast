package com.example.skycast.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.skycast.viewmodel.AlarmViewModel
import java.util.*
import android.app.AlarmManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.rememberDismissState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.skycast.R
import com.example.skycast.data.models.AlarmEntity
import com.example.skycast.data.models.SavedLocation
import com.example.skycast.ui.navigation.ScreenRoute
import com.example.skycast.ui.theme.BlueLight
import com.example.skycast.util.AlarmScheduler
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AlarmScreen(navController: NavHostController, viewModel: AlarmViewModel) {
    val alarms by viewModel.savedAlarms.collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(ScreenRoute.AlarmBottons.route) },
                containerColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Notifications, contentDescription = "Add", tint = BlueLight)
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(android.graphics.Color.parseColor("#022a9a")),
                            Color(android.graphics.Color.parseColor("#5381ff"))
                        )
                    )
                )
                .padding(16.dp)
                .padding(paddingValues)
        ) {
            Column {
                // Title
                Text(
                    text = "Saved Alarms",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (alarms.isEmpty()) {
                    NoAlarmsFound()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(alarms, key = { it.id }) { alarm ->
                            SwipeToDeleteAlarm(
                                alarm = alarm,
                                viewModel = viewModel,
                                coroutineScope = coroutineScope
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeToDeleteAlarm(
    alarm: AlarmEntity,
    viewModel: AlarmViewModel,
    coroutineScope: CoroutineScope
) {
    val dismissState = rememberDismissState(
        confirmStateChange = {
            if (it == DismissValue.DismissedToStart || it == DismissValue.DismissedToEnd) {
                coroutineScope.launch {
                    viewModel.deleteAlarm(alarm)
                    viewModel.getAllAlarms()
                }
            }
            true
        }
    )

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
        background = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .background(Color.Red, shape = RoundedCornerShape(12.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White
                )
            }
        },
        dismissContent = {
            AlarmItem(alarm)
        }
    )
}

@Composable
fun AlarmItem(alarm: AlarmEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = BlueLight)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = extractCityAndCountry(alarm.label)  , color = Color.White, fontSize = 16.sp)
                Text(text = alarm.hour.toString() + ":" + alarm.minute.toString(), color = Color.White, fontSize = 14.sp)
            }
            Icon(Icons.Default.Notifications, contentDescription = "Alarm Icon", tint = Color.White)
        }
    }
}

fun extractCityAndCountry(address: String): String {
    return address.split(", ")
        .takeLast(3) // Get the last three parts (assuming they contain city and country)
        .let { listOf(it.first(), it[1].split(" ").first(), it.last()) }
        .joinToString(", ")
}
@Preview
@Composable
private fun NoAlarmsFound() {
    Spacer(modifier = Modifier.height(40.dp))
    Column (
        modifier = Modifier.fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(android.graphics.Color.parseColor("#022a9a")),
                        Color(android.graphics.Color.parseColor("#5381ff"))
                    )
                )
            )
        , horizontalAlignment = Alignment.CenterHorizontally
        , verticalArrangement = Arrangement.Center
    ){Image(
        painter = painterResource(id = R.drawable.ic_cloud_alert), // Replace with your actual drawable
        contentDescription = "No Alarms",
        modifier = Modifier.size(80.dp)
        , alignment = Alignment.Center
    )
        Spacer(modifier = Modifier.height(50.dp))

        Text(
            text = "No alarms Found",
            color = Color.White,
            fontSize = 16.sp
        )  }

}

