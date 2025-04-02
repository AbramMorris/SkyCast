package com.example.skycast.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.skycast.viewmodel.AlarmViewModel
import android.os.Build
import androidx.compose.material3.Text
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.rememberDismissState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.skycast.R
import com.example.skycast.data.models.AlarmEntity
import com.example.skycast.ui.navigation.ScreenRoute
import com.example.skycast.ui.theme.BlueLight
import com.example.skycast.util.AlarmScheduler.cancelAlarm
import com.example.skycast.util.cancelAlarmWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch



@OptIn(ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AlarmScreen(navController: NavHostController, viewModel: AlarmViewModel) {
    val alarms by viewModel.savedAlarms.collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(ScreenRoute.AlarmBottons.route) },
                containerColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Notifications, contentDescription = "Add", tint = BlueLight)
            }
        },
                snackbarHost = {
            SnackbarHost(snackbarHostState)
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
                    text = stringResource(R.string.saved_alarms),
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
                                val dismissState = rememberDismissState(
                                    confirmStateChange = {
                                        if (it == DismissValue.DismissedToStart || it == DismissValue.DismissedToEnd) {
                                            coroutineScope.launch {
                                                viewModel.deleteAlarm(alarm)
                                                cancelAlarm(context, alarm.id)
                                                cancelAlarmWorker(context, alarm.id)
                                                val result = snackbarHostState.showSnackbar(
                                                    message = context.getString(R.string.alarm_deleted),
                                                    actionLabel = context.getString(R.string.undo),
                                                    duration = SnackbarDuration.Short
                                                )
                                                if (result == SnackbarResult.ActionPerformed) {
                                                    viewModel.insertAlarm(alarm)
                                                }
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
                                                .background(
                                                    Color.Red,
                                                    shape = RoundedCornerShape(12.dp)
                                                )
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
                    }
                }
            }
        }
    }
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
            Box(
                modifier = Modifier.width(300.dp),
            ) {
                Text(
                    text = extractCityAndCountry(alarm.label),
                    color = Color.White,
                    fontSize = 16.sp,
                    overflow = TextOverflow.Ellipsis
                )

            }
                Text(text = alarm.hour.toString() + ":" + alarm.minute.toString(), color = Color.White, fontSize = 14.sp)
           }
            Icon(Icons.Default.Notifications, contentDescription = "Alarm Icon", tint = Color.White)
        }
    }
}

fun extractCityAndCountry(address: String): String {
    try {
        return address.split(", ")
            .takeLast(3)
            .let { listOf(it.first(), it[1].split(" ").first(), it.last()) }
            .joinToString(", ")
    }catch (
        e: Exception
    ){
        return "Address not found"
    }
}

@Preview
@Composable
private fun NoAlarmsFound() {
    Spacer(modifier = Modifier.height(40.dp))
    Column (
        modifier = Modifier.fillMaxSize()
        , horizontalAlignment = Alignment.CenterHorizontally
        , verticalArrangement = Arrangement.Center
    ){Image(
        painter = painterResource(id = R.drawable.ic_cloud_alert),
        contentDescription = stringResource(R.string.no_alarms),
        modifier = Modifier.size(100.dp)
        , alignment = Alignment.Center
    )
        Spacer(modifier = Modifier.height(50.dp))

        Text(
            text = stringResource(R.string.no_alarms_found),
            color = Color.White,
            fontSize = 16.sp
        )  }

}

