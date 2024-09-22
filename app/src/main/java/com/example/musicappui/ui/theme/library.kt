package com.example.musicappui.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun library(){

    LazyColumn {
        items(libraries){ lib->
            libItem(lib=lib)

        }
    }
}

@Composable
fun libItem(lib: Lib){


    Column {
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        )
        {
            Row {
                Icon(painter = painterResource(id=lib.icon),
                    modifier = Modifier.padding(horizontal =10.dp ), contentDescription = lib.name
                )
                Text(lib.name)
            }
            Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "RightArrow")


        }
        Divider(color = Color.LightGray)
    }
}