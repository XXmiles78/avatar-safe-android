package com.myavatar.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyAvatarApp()
        }
    }
}

private val MyAvatarLilac = Color(0xFF9B7EBD)
private val MyAvatarLightLilac = Color(0xFFF4EFF9)
private val MyAvatarDark = Color(0xFF30263A)

data class OfflineAvatar(
    val name: String,
    val icon: String,
    val keywords: List<String>
)

private val offlineAvatars = listOf(

    OfflineAvatar(
        name = "Gatto spaziale",
        icon = "🐱",
        keywords = listOf(
            "gatto", "gatti", "spazio", "astronauta",
            "stelle", "universo", "viola", "blu"
        )
    ),

    OfflineAvatar(
        name = "Volpe fantasy",
        icon = "🦊",
        keywords = listOf(
            "volpe", "fantasy", "magia", "magico",
            "foresta", "natura", "arancione"
        )
    ),

    OfflineAvatar(
        name = "Panda kawaii",
        icon = "🐼",
        keywords = listOf(
            "panda", "kawaii", "carino", "dolce",
            "bianco", "nero", "tenero"
        )
    ),

    OfflineAvatar(
        name = "Unicorno",
        icon = "🦄",
        keywords = listOf(
            "unicorno", "fantasy", "arcobaleno",
            "magia", "rosa", "viola", "azzurro"
        )
    ),

    OfflineAvatar(
        name = "Robot futuristico",
        icon = "🤖",
        keywords = listOf(
            "robot", "futuro", "futuristico", "tecnologia",
            "spazio", "gaming", "blu", "metallo"
        )
    ),

    OfflineAvatar(
        name = "Esploratrice dello spazio",
        icon = "🚀",
        keywords = listOf(
            "spazio", "astronauta", "esploratrice",
            "stelle", "pianeta", "universo", "razzo"
        )
    ),

    OfflineAvatar(
        name = "Cucciolo",
        icon = "🐶",
        keywords = listOf(
            "cane", "cani", "cucciolo", "animale",
            "carino", "dolce", "tenero"
        )
    ),

    OfflineAvatar(
        name = "Volpe stellare",
        icon = "🦊",
        keywords = listOf(
            "volpe", "stelle", "spazio",
            "galassia", "viola", "blu"
        )
    ),

    OfflineAvatar(
        name = "Gatto musicale",
        icon = "🎧",
        keywords = listOf(
            "gatto", "musica", "cuffie",
            "canzone", "music", "viola", "rosa"
        )
    ),

    OfflineAvatar(
        name = "Avatar sportivo",
        icon = "🏀",
        keywords = listOf(
            "sport", "basket", "calcio", "pallone",
            "sportivo", "energia"
        )
    ),

    OfflineAvatar(
        name = "Avatar musicale",
        icon = "🎵",
        keywords = listOf(
            "musica", "canzone", "cantante",
            "note", "cuffie", "rosa", "viola"
        )
    ),

    OfflineAvatar(
        name = "Natura incantata",
        icon = "🌸",
        keywords = listOf(
            "natura", "fiori", "fiore", "foresta",
            "giardino", "primavera", "rosa", "verde"
        )
    )
)

@Composable
fun MyAvatarApp() {

    var searchText by remember {
        mutableStateOf("")
    }

    var results by remember {
        mutableStateOf<List<OfflineAvatar>>(emptyList())
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "MyAvatar",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MyAvatarDark
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Trova il tuo avatar",
                fontSize = 17.sp,
                color = MyAvatarLilac
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                shape = RoundedCornerShape(18.dp),
                label = {
                    Text("Cosa stai cercando?")
                },
                placeholder = {
                    Text(
                        "Es. un gatto viola nello spazio..."
                    )
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    results = searchOffline(searchText)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MyAvatarLilac
                )
            ) {
                Text(
                    text = "🔎 Cerca",
                    fontSize = 17.sp
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (results.isEmpty()) {

                WelcomeSection()

            } else {

                Text(
                    text = "Risultati offline",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MyAvatarDark
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(results) { avatar ->
                        AvatarCard(avatar)
                    }
                }
            }
        }
    }
}

private fun searchOffline(
    query: String
): List<OfflineAvatar> {

    val words = query
        .lowercase()
        .replace(",", " ")
        .replace(".", " ")
        .replace("!", " ")
        .replace("?", " ")
        .split(" ")
        .filter {
            it.length >= 3
        }

    if (words.isEmpty()) {
        return offlineAvatars.take(6)
    }

    return offlineAvatars
        .map { avatar ->

            var score = 0

            for (word in words) {

                if (avatar.keywords.any { keyword ->
                        keyword.contains(word) || word.contains(keyword)
                    }
                ) {
                    score++
                }
            }

            avatar to score
        }
        .filter {
            it.second > 0
        }
        .sortedByDescending {
            it.second
        }
        .take(8)
        .map {
            it.first
        }
}

@Composable
private fun WelcomeSection() {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MyAvatarLightLilac
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "✨ Cerca come vuoi",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = MyAvatarDark
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Puoi scrivere una frase intera. " +
                        "MyAvatar cercherà gli avatar più adatti " +
                        "nel catalogo sicuro offline.",
                textAlign = TextAlign.Center,
                color = MyAvatarDark
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text("🐱")
                Text("🦊")
                Text("🦄")
                Text("🤖")
                Text("🌸")
            }
        }
    }

    Spacer(modifier = Modifier.height(18.dp))

    Text(
        text = "Esempi",
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = MyAvatarDark
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "• un gatto nello spazio\n" +
                "• una volpe magica viola\n" +
                "• un panda carino\n" +
                "• un avatar musicale",
        textAlign = TextAlign.Center,
        color = MyAvatarDark
    )
}

@Composable
private fun AvatarCard(
    avatar: OfflineAvatar
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MyAvatarLightLilac
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Column(
                modifier = Modifier
                    .size(105.dp)
                    .background(
                        color = Color.White,
                        shape = CircleShape
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = avatar.icon,
                    fontSize = 52.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = avatar.name,
                fontWeight = FontWeight.Bold,
                color = MyAvatarDark,
                textAlign = TextAlign.Center
            )
        }
    }
}
