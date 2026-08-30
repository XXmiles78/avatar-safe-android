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

@Composable
fun MyAvatarApp() {

    var searchText by remember {
        mutableStateOf("")
    }

    MaterialTheme {

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 22.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "MyAvatar",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MyAvatarDark
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Trova il tuo avatar",
                    fontSize = 17.sp,
                    color = MyAvatarLilac
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = searchText,
                    onValueChange = {
                        searchText = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    minLines = 2,
                    shape = RoundedCornerShape(18.dp),
                    label = {
                        Text("Cosa stai cercando?")
                    },
                    placeholder = {
                        Text(
                            "Es. un gatto astronauta nello spazio..."
                        )
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        // La ricerca verrà collegata nei prossimi passi.
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
                        text = "Cerca",
                        fontSize = 17.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MyAvatarLightLilac
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "Ricerca intelligente",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = MyAvatarDark
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "MyAvatar cercherà automaticamente " +
                                    "online oppure nel catalogo offline.",
                            textAlign = TextAlign.Center,
                            color = MyAvatarDark
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {

                            Text(
                                text = "☁ Online",
                                color = MyAvatarLilac,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "+",
                                color = MyAvatarDark
                            )

                            Text(
                                text = "📱 Offline",
                                color = MyAvatarLilac,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF8F6FA)
                    )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "Crea il tuo avatar",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = MyAvatarDark
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Trasforma una tua foto in un avatar.",
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                // La fotocamera verrà collegata nei prossimi passi.
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MyAvatarDark
                            )
                        ) {
                            Text("📷 Usa una foto")
                        }
                    }
                }
            }
        }
    }
}
