package com.example.skycast.models

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skycast.HourlyModel
import com.example.skycast.R
import com.example.skycast.ui.theme.BlueBlackBack


@Composable
fun FutureModelViewHolder(hourlyModel: HourlyModel) {
    Column(
        modifier = Modifier
            .width(64.dp)
            .wrapContentHeight()
            .background(
                color = BlueBlackBack,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = hourlyModel.hour,
            fontSize = 16.sp,
            color = Color.White,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Image(
            painter = painterResource(
                id = when (hourlyModel.picPath) {
                    "cloudy" -> R.drawable.cloudy_sunny
                    "sunny" -> R.drawable.sunny
                    "wind" -> R.drawable.wind
                    "rainy" -> R.drawable.rainy
                    "storm" -> R.drawable.storm
                    else -> R.drawable.sunny
                }
            ),
            contentDescription = null,
            modifier = Modifier
                .size(45.dp)
                .padding(8.dp),
            contentScale = ContentScale.Crop
        )

        Text(
            text = "${hourlyModel.temp}°",
            fontSize = 16.sp,
            color= Color.White,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

val hourlyItems = listOf(
    HourlyModel("9 PM", 28, "cloudy"),
    HourlyModel("10 PM", 27, "sunny"),
    HourlyModel("11 PM", 26, "wind"),
    HourlyModel("12 AM", 25, "rainy"),
    HourlyModel("1 AM", 24, "storm")
)

@Composable
fun WeatherDetailItem(icon: Int, value: String, label: String) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(34.dp)
        )
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            color = Color.White,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
        )
    }
}



