package com.example.buttonstate


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material.icons.filled.UsbOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.buttonstate.ui.theme.ButtonStateTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

sealed class OpeButton(
    val icons: Pair<ImageVector, ImageVector?>,
    val label: Pair<String, String?>
){
    object USB: OpeButton(
        icons = Pair(Icons.Filled.Usb, Icons.Filled.UsbOff),
        label = Pair("接続","切断")
    )

    object Play: OpeButton(
        icons = Pair(Icons.Filled.PlayArrow, null),
        label = Pair("スタート", null)
    )

    object Clack1: OpeButton(
        icons = Pair(Icons.Filled.Done, null),
        label = Pair("１ハゼ", null)
    )

    object Clack2: OpeButton(
        icons = Pair(Icons.Filled.DoneAll, null),
        label = Pair("２ハゼ", null)
    )

    object Stop: OpeButton(
        icons = Pair(Icons.Filled.Stop, null),
        label = Pair("ストップ", null)
    )

    object Save: OpeButton(
        icons = Pair(Icons.Filled.Download, null),
        label = Pair("保存", null)
    )

    object Clear: OpeButton(
        icons = Pair(Icons.Filled.Refresh, null),
        label = Pair("クリア", null)
    )
}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ButtonStateTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        Greeting(
                            name = "Android",
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val usb = rememberSaveable {
        mutableStateOf(false)
    }
    val stopWatch = rememberSaveable {
        mutableStateOf(StopWatchState.Idle)
    }
    val dataState = rememberSaveable {
        mutableStateOf(ChartDataState.Null)
    }

    var visible by remember {
        mutableStateOf(false)
    }
    val list = listOf<OpeButton>(
        OpeButton.USB,
        OpeButton.Play,
        OpeButton.Clack1,
        OpeButton.Clack2,
        OpeButton.Stop,
        OpeButton.Save,
        OpeButton.Clear
    )

    list.forEach { item ->
        OpeBtn(
            btn = item,
            usb = usb.value,
            stopWatchState = stopWatch,
            dataState= dataState
        ){
            when(item) {
                OpeButton.USB -> {
                    usb.value = !usb.value
                }
                OpeButton.Clack1 -> {
                    println("クラック1がクリックされたよ")
                }
                OpeButton.Clack2 -> {
                    println("クラック2がクリックされたよ")
                }
                OpeButton.Clear -> {
                    if (dataState.value == ChartDataState.Saved){
                        stopWatch.value = StopWatchState.Idle
                        dataState.value = ChartDataState.Null
                    } else if (dataState.value == ChartDataState.Unsaved){
                        visible = true
                    }
                }
                OpeButton.Play -> {
                    if ((stopWatch.value == StopWatchState.Idle || stopWatch.value == StopWatchState.Stopped) && usb.value){
                        stopWatch.value = StopWatchState.Started
                        dataState.value = ChartDataState.Unsaved
                    }
                }
                OpeButton.Save -> {
                    if (dataState.value == ChartDataState.Unsaved && stopWatch.value == StopWatchState.Stopped){
//                        savingが挟まる
                        dataState.value = ChartDataState.Saved
                    }
                }
                OpeButton.Stop -> {
                    if (stopWatch.value == StopWatchState.Started){
                        stopWatch.value = StopWatchState.Stopped
                    }
                }
            }
        }
    }

    if (visible) {
        AlertDialog(
            text = {
                   Text(text = "データを保存していません。\nクリアしますか。")
            },
            onDismissRequest = {
                visible = false
            },
            dismissButton = {
                TextButton(onClick = {
                    visible = false
                }) {
                    Text(text = "キャンセル")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    stopWatch.value = StopWatchState.Idle
                    dataState.value = ChartDataState.Null
                    visible = false
                }) {
                    Text(text = "OK")
                }
            })
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OpeBtn(
    btn: OpeButton,
    usb: Boolean,
    dataState: MutableState<ChartDataState>,
    stopWatchState: MutableState<StopWatchState>,
    modifier: Modifier = Modifier,
    click: () -> Unit
) {
    Row(
        modifier = modifier
            .height(60.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = modifier.width(20.dp))

        Box(
            modifier = modifier
                .width(50.dp)
                .height(50.dp)
                .background(
                    color = colorSelect(btn, usb, stopWatchState.value, dataState.value),
                    RoundedCornerShape(50)
                )
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = click,
                    onLongClick = {
                        if (btn == OpeButton.Clack1 || btn == OpeButton.Clack2){
                            println("クラックがながおしされたよ")
                        }
                    }
                ),

            contentAlignment = Alignment.Center
        ){
            if (btn.icons.second != null){
                if (usb){
                    Icon(
                        imageVector = btn.icons.first,
                        contentDescription = btn.label.first,
                        modifier = modifier.size(20.dp)
                    )
                } else {
                    Icon(
                        imageVector = btn.icons.second!!,
                        contentDescription = btn.label.second,
                        modifier = modifier.size(20.dp)
                    )
                }
            } else {
               Icon(
                   imageVector = btn.icons.first,
                   contentDescription = btn.label.first,
                   modifier = modifier.size(20.dp)
               )
            }
        }
        Spacer(modifier = modifier.width(20.dp))
        Text(text = btn.label.first)
    }
}

@Composable
fun colorSelect(
    btn: OpeButton,
    usb: Boolean,
    stop: StopWatchState,
    data: ChartDataState
): Color {
    when(btn) {
        OpeButton.Clack1 -> {
            return Color.Cyan.copy(0.4f)
        }
        OpeButton.Clack2 -> {
            return Color.Cyan.copy(0.4f)
        }
        OpeButton.Clear -> {
            if (stop == StopWatchState.Stopped){
                return Color.Cyan.copy(0.4f)
            }
            return Color.Gray
        }
        OpeButton.Play -> {
            if (usb && stop == StopWatchState.Idle || stop == StopWatchState.Stopped){
                return Color.Cyan.copy(0.4f)
            } else {
                return Color.Gray
            }
        }
        OpeButton.Save -> {
            if (stop == StopWatchState.Stopped && data == ChartDataState.Unsaved){
                return Color.Cyan.copy(0.4f)
            }
            return Color.Gray
        }
        OpeButton.Stop -> {
            if (stop == StopWatchState.Started){
                return Color.Cyan.copy(0.4f)
            }
            return Color.Gray
        }
        OpeButton.USB -> {
            return Color.Cyan.copy(0.4f)
        }
    }
}



enum class StopWatchState{
    Idle,
    Started,
    Stopped,
}
enum class ChartDataState{
    Null,
    Unsaved,
    Saving,
    Saved,
}

