package com.example.wetherapp

import android.app.DownloadManager.Request
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import coil.imageLoader
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.wetherapp.data.wetherModel
import com.example.wetherapp.screens.DialogSearc

import com.example.wetherapp.screens.mainScreen
import com.example.wetherapp.screens.tabsWithSwiping
import com.example.wetherapp.ui.theme.WetherAppTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import org.json.JSONObject
import java.lang.reflect.Method

const val apikey = "96694ffc0d914663b47114252230110"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {


            WetherAppTheme {
                val daysList=remember{ mutableStateOf(listOf<wetherModel>()) }
                val DialogState=remember{ mutableStateOf(false) }

                val CurrentDay=remember{ mutableStateOf(wetherModel(
                    "","","10.0","","","10.0","10.0",""
                )) }
                if(DialogState.value){
                    DialogSearc(DialogState, onSubmit = {
                        getData(it,this,daysList,CurrentDay)
                    })}
                getData("Ulyanovsk", this,daysList,CurrentDay)
                Image(
                    painter = painterResource(id = R.drawable.gfon),
                    contentDescription = "img1",
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(0.5f),
                    contentScale = ContentScale.FillBounds
                )
                Column {
                    mainScreen(CurrentDay, onClickSync = {
                     getData("Ulyanovsk",this@MainActivity,daysList,CurrentDay)
                    }, onClickSearc = {
                        DialogState.value=true
                    }
                        )
                    tabsWithSwiping(daysList,CurrentDay)
                }
            }
        }
    }
}

private fun getData(city: String, context: Context,
                    daysList: MutableState<List<wetherModel>>,
                   CurrentDay:MutableState<wetherModel> ) {
    val url = "https://api.weatherapi.com/v1/forecast.json?key=$apikey" +
            "&q=$city" +
            "&days=3" +
            "&aqi=no&alerts=no"
    val queue = Volley.newRequestQueue(context)
    val sRequest = StringRequest(com.android.volley.Request.Method.GET,
        url,
        { response ->
            val list= getWetherByDays(response)
            CurrentDay.value=list[0]
            daysList.value=list
            Log.d("MyLog", "Response:$response") },
        { Log.d("MyLog", "VolleyError:$it") }
    )
    queue.add(sRequest)
}

private fun getWetherByDays(response: String): List<wetherModel> {
    if (response.isEmpty()) return listOf()
    val list = ArrayList<wetherModel>()
    val mainObject = JSONObject(response)
    val city = mainObject.getJSONObject("location").getString("name")
    val days = mainObject.getJSONObject("forecast").getJSONArray("forecastday")
    for (i in 0 until days.length()) {
        val item = days[i] as JSONObject
        list.add(
            wetherModel(
                city,
                item.getString("date"),
                "",
                item.getJSONObject("day").getJSONObject("condition").getString("text"),
                item.getJSONObject("day").getJSONObject("condition").getString("icon"),
                item.getJSONObject("day").getString("maxtemp_c"),
                item.getJSONObject("day").getString("mintemp_c"),
                item.getJSONArray("hour").toString()
                )
        )
    }
    list[0]=list[0].copy(
        time = mainObject.getJSONObject("current").getString("last_updated"),
        currentTemp = mainObject.getJSONObject("current").getString("temp_c")
    )
    return  list
}



