package com.example.wetherapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults.shape
import androidx.compose.material3.Card
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.wetherapp.data.wetherModel
import com.example.wetherapp.ui.theme.ColorBlock
import com.google.accompanist.pager.ExperimentalPagerApi

@Composable
fun MainList(list:List<wetherModel>,CurrentDay:MutableState<wetherModel>){
  LazyColumn(modifier = Modifier.fillMaxSize()){
    itemsIndexed(
      list
    ){
        _,item-> ListItem(item, CurrentDay)
    }
  }
}
@Composable
fun ListItem(item:wetherModel,CurrentDay: MutableState<wetherModel>) {

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 3.dp)
        .clickable {
          if (item.hours.isEmpty()) return@clickable
          CurrentDay.value = item
        }
        .background(ColorBlock, shape = RoundedCornerShape(5.dp))
      , horizontalArrangement = Arrangement.SpaceBetween
      , verticalAlignment = Alignment.CenterVertically

    ) {
      Column(
        modifier = Modifier
          .padding(start = 8.dp, top = 5.dp, bottom = 5.dp)
      ) {
        Text(text = item.time, color = Color.White)
        Text(text = item.condition, color = Color.White)
      }
      Text(text = item.currentTemp.ifEmpty { "${item.maxTemp}/${item.minTemp}" }, color = Color.White, style = TextStyle(fontSize = 25.sp))
      AsyncImage(model = "https:${item.icon}"
        , contentDescription = "img4"
      , modifier = Modifier
          .padding(end = 8.dp)
          .size(35.dp))

  }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogSearc(DialogState:MutableState<Boolean>,onSubmit:(String)->Unit){
    val DialogText=remember{ mutableStateOf("") }
  AlertDialog(onDismissRequest = {
    DialogState.value=false
   },
    confirmButton = {
      TextButton(onClick = {
          onSubmit(DialogText.value)
          DialogState.value=false  }) {
        Text(text = "OK")
      }
    },
    dismissButton = {
      TextButton(onClick = { DialogState.value=false  }) {
        Text(text = "Cancel")
      }
    },
    title = {
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Введите название города:")
        TextField(value = DialogText.value, onValueChange = {
            DialogText.value=it
        })
      }

    }
    )
}