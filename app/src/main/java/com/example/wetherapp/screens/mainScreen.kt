package com.example.wetherapp.screens
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.wetherapp.R
import com.example.wetherapp.data.wetherModel
import com.example.wetherapp.ui.theme.ColorBlock
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun mainScreen(CurrentDay: MutableState<wetherModel>,onClickSync:()->Unit,onClickSearc:()->Unit) {
    Column(
        modifier = Modifier
            .padding(5.dp)
    ) {
        Column(
            modifier = Modifier
                .background(ColorBlock.copy(alpha = 0.3f), shape = RoundedCornerShape(11.dp))
                .padding(top = 8.dp, start = 8.dp)

                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier
                        .padding(top = 8.dp, start = 8.dp),
                    text = CurrentDay.value.time,
                    style = TextStyle(fontSize = 15.sp),
                    color = Color.White
                )
                AsyncImage(
                    model = "https:${CurrentDay.value.icon}",
                    contentDescription = "img2",
                    modifier = Modifier.size(35.dp)
                )
            }
            Text(
                text = CurrentDay.value.city,
                style = TextStyle(fontSize = 25.sp),
                color = Color.White
            )
            Text(
                text =if(CurrentDay.value.currentTemp.isNotEmpty())
                    CurrentDay.value.currentTemp.toFloat().toInt().toString() + "°C"
                else "${CurrentDay.value.maxTemp.toFloat().toInt()}°C" +
                        "/${CurrentDay.value.minTemp.toFloat().toInt()}°C",
                style = TextStyle(fontSize = 65.sp),
                color = Color.White
            )
            Text(
                text = CurrentDay.value.condition,
                style = TextStyle(fontSize = 16.sp),
                color = Color.White
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { onClickSearc.invoke() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.search),
                        contentDescription = "im3",
                        tint = Color.White
                    )
                }
                Text(
                    text = "${CurrentDay.value.maxTemp.toFloat().toInt()}" +
                            "°C / ${CurrentDay.value.minTemp.toFloat().toInt()}°C",
                    style = TextStyle(fontSize = 20.sp),
                    color = Color.White, modifier = Modifier.padding(top = 3.dp)
                )
                IconButton(onClick = {onClickSync.invoke() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.sync),
                        contentDescription = "im4",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
@OptIn(ExperimentalPagerApi::class)
@Composable
fun tabsWithSwiping(daysList: MutableState<List<wetherModel>>,CurrentDay: MutableState<wetherModel>) {

    val tabTitles = listOf("HOURCE", "DAYS")
    val pagerState = rememberPagerState()
    var tabIndex = pagerState.currentPage
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .padding(end = 5.dp, start = 5.dp)
            .clip(RoundedCornerShape(5.dp))
    ) {
        androidx.compose.material.TabRow(
            selectedTabIndex = tabIndex,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                )
            }, backgroundColor = ColorBlock
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(selected = tabIndex == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(text = title, color = Color.White) }
                )
            }
        }
        HorizontalPager(
            count = tabTitles.size, state = pagerState, modifier = Modifier
                .padding(top = 5.dp, start = 5.dp) //область под таб ров
        ) { tabIndex ->
            val list=when(tabIndex){    //возможна ошибка вместо таб индекс... индекс
                0 -> GetWeatherByHours(CurrentDay.value.hours)
                1 -> daysList.value
                else -> daysList.value
            }
            MainList(list, CurrentDay)
        }
    }
}

    private fun GetWeatherByHours(hours:String):List<wetherModel> {
        if (hours.isEmpty())return listOf()
        val HaursArray=JSONArray(hours)
        val list=ArrayList<wetherModel>()
        for(i in 0 until HaursArray.length()){
            val item=HaursArray[i] as JSONObject
            list.add(
                wetherModel(
                    "",
                    item.getString("time"),
                    item.getString("temp_c").toFloat().toInt().toString()+"℃",
                    item.getJSONObject("condition").getString("text"),
                    item.getJSONObject("condition").getString("icon"),
                    "",
                    "",
                    ""
                )
            )
        }
        return list
    }
