package com.client.discord.ui.component

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.client.discord.bl.model.VoiceCallMember
import com.client.discord.bl.viewModel.MainViewModel
import com.client.discord.ui.BitService
import org.koin.compose.koinInject

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MemberVoiceChatComponent(member:VoiceCallMember, modifier: Modifier){
    val memberImageBitMap by member.imageBitMap.collectAsState()
    val mainViewModel : MainViewModel = koinInject()
    var initialized by remember { mutableStateOf(false) }

    LaunchedEffect (Unit) {
        if (!initialized) {
            Log.e("server-component", "initialization")
            mainViewModel.downloadMemberImage(member)
            initialized = true
        }
    }

    Column (
        modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.CenterHorizontally)

        ) {
            Image(
                bitmap = memberImageBitMap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop

            )
        }

        Text(
            text = member.name,
            modifier = Modifier.padding(start = 10.dp, top = 5.dp)
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            color = Color.White,
            fontSize = 25.sp
        )


    }
}