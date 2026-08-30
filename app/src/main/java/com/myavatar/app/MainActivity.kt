package com.myavatar.app

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import java.io.File
import android.os.Handler
import android.os.Looper
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject
import coil3.compose.AsyncImage
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
private fun searchOnline(
    query: String,
    onResult: (List<String>) -> Unit
) {
    Thread {
        try {
            val url = URL("https://myavatar-ai.xxmiles78.workers.dev/")
            val connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true
            connection.connectTimeout = 15000
            connection.readTimeout = 30000

            val body = JSONObject().apply {
                put("prompt", query)
                put("num_images", 6)
            }.toString()

            connection.outputStream.use { output ->
                output.write(body.toByteArray(Charsets.UTF_8))
            }

            val responseCode = connection.responseCode

            if (responseCode in 200..299) {
                val responseText =
                    connection.inputStream.bufferedReader().use { it.readText() }

                val json = JSONObject(responseText)
                val imageUrls = json.optJSONArray("images")
    ?.let { array ->
        List(array.length()) { index -> array.getString(index) }
    } ?: emptyList()

                Handler(Looper.getMainLooper()).post {
    onResult(imageUrls)
}
            } else {
                Handler(Looper.getMainLooper()).post {
                    onResult(emptyList())
                }
            }

            connection.disconnect()

        } catch (e: Exception) {
            Handler(Looper.getMainLooper()).post {
                onResult(emptyList())
            }
        }
    }.start()
}
data class OfflineAvatar(
    val name: String,
    val imageRes: Int,
    val keywords: List<String>
)

private val offlineAvatars = listOf(

    OfflineAvatar(
        "Gatto spaziale",
        R.drawable.avatar_gatto_spaziale,
        listOf("gatto", "spazio", "astronauta", "stelle", "universo", "viola", "blu")
    ),

    OfflineAvatar(
        "Volpe fantasy",
        R.drawable.avatar_volpe_fantasy,
        listOf("volpe", "fantasy", "magia", "magico", "foresta", "natura", "viola")
    ),

    OfflineAvatar(
        "Panda kawaii",
        R.drawable.avatar_panda_kawaii,
        listOf("panda", "kawaii", "carino", "dolce", "tenero")
    ),

    OfflineAvatar(
        "Unicorno",
        R.drawable.avatar_unicorno,
        listOf("unicorno", "fantasy", "arcobaleno", "magia", "rosa", "viola")
    ),

    OfflineAvatar(
        "Robot futuristico",
        R.drawable.avatar_robot_futuristico,
        listOf("robot", "futuro", "futuristico", "tecnologia", "spazio", "gaming")
    ),

    OfflineAvatar(
        "Esploratrice dello spazio",
        R.drawable.avatar_esploratrice_spazio,
        listOf("spazio", "astronauta", "esploratrice", "stelle", "pianeta", "universo")
    )
)

@Composable
fun MyAvatarApp() {

    val context = LocalContext.current

    var searchText by remember {
        mutableStateOf("")
    }

    var results by remember {
        mutableStateOf<List<OfflineAvatar>>(emptyList())
    }
var onlineResults by remember {
    mutableStateOf<List<String>>(emptyList())
}
    var selectedAvatar by remember {
        mutableStateOf<OfflineAvatar?>(null)
    }

    var savedAvatar by remember {
        mutableStateOf<OfflineAvatar?>(null)
    }

    var personalPhoto by remember {
        mutableStateOf<Bitmap?>(null)
    }

    var showPhotoScreen by remember {
        mutableStateOf(false)
    }

    /*
     * Carica la foto personale salvata quando l'app viene aperta.
     */
    LaunchedEffect(Unit) {
        personalPhoto = loadPersonalPhoto(context)
    }

    /*
     * Fotocamera.
     * TakePicturePreview non richiede FileProvider:
     * Android restituisce direttamente una Bitmap.
     */
    val cameraLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.TakePicturePreview()
        ) { bitmap ->

            if (bitmap != null) {
                savePersonalPhoto(context, bitmap)
                personalPhoto = bitmap
            }
        }

    /*
     * Selezione di una foto già presente nel telefono.
     */
    val galleryLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->

            if (uri != null) {

                try {

                    val bitmap = context.contentResolver
                        .openInputStream(uri)
                        ?.use { input ->
                            BitmapFactory.decodeStream(input)
                        }

                    if (bitmap != null) {
                        savePersonalPhoto(context, bitmap)
                        personalPhoto = bitmap
                    }

                } catch (_: Exception) {
                    // Se la foto non può essere letta,
                    // non modifichiamo quella già salvata.
                }
            }
        }

    /*
     * Richiesta del permesso fotocamera.
     */
    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {
                cameraLauncher.launch(null)
            }
        }

    fun openCamera() {

        val permissionGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

        if (permissionGranted) {
            cameraLauncher.launch(null)
        } else {
            cameraPermissionLauncher.launch(
                Manifest.permission.CAMERA
            )
        }
    }

    /*
     * Schermata foto personale.
     */
    if (showPhotoScreen) {

        PersonalPhotoScreen(
            photo = personalPhoto,
            onBack = {
                showPhotoScreen = false
            },
            onCamera = {
                openCamera()
            },
            onGallery = {
                galleryLauncher.launch("image/*")
            }
        )

        return
    }

    /*
     * Schermata dettaglio avatar.
     */
    if (selectedAvatar != null) {

        AvatarDetail(
            avatar = selectedAvatar!!,
            onBack = {
                selectedAvatar = null
            },
            onUseAvatar = {
                saveAvatar(
                    context,
                    selectedAvatar!!.name
                )

                savedAvatar = selectedAvatar
                selectedAvatar = null
            }
        )

        return
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 20.dp,
                    vertical = 24.dp
                ),
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

            Spacer(modifier = Modifier.height(18.dp))

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

        onlineResults = emptyList()

        searchOnline(searchText) { urls ->
            onlineResults = urls
        }
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

            Spacer(modifier = Modifier.height(14.dp))

            /*
             * Accesso alla foto personale.
             */
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showPhotoScreen = true
                    },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MyAvatarLightLilac
                )
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    if (personalPhoto != null) {

                        Image(
                            bitmap = personalPhoto!!.asImageBitmap(),
                            contentDescription = "La tua foto",
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )

                    } else {

                        Text(
                            text = "📷",
                            fontSize = 38.sp
                        )
                    }

                    Spacer(
                        modifier = Modifier.size(14.dp)
                    )

                    Column {

                        Text(
                            text = "Crea il tuo avatar",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MyAvatarDark
                        )

                        Text(
                            text = if (personalPhoto != null)
                                "Foto pronta per essere trasformata"
                            else
                                "Scatta o scegli una foto",
                            color = MyAvatarDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (savedAvatar != null) {

                Text(
                    text = "⭐ Il mio avatar",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = MyAvatarDark
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Image(
                        painter = painterResource(
                            id = savedAvatar!!.imageRes
                        ),
                        contentDescription = savedAvatar!!.name,
                        modifier = Modifier
                            .size(65.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(
                        modifier = Modifier.size(12.dp)
                    )

                    Text(
                        text = savedAvatar!!.name,
                        fontWeight = FontWeight.Bold,
                        color = MyAvatarDark
                    )
                }
            }

            if (results.isEmpty() && onlineResults.isEmpty()) {

                WelcomeSection()

            } else {

                Text(
                    text = "Avatar consigliati",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MyAvatarDark
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    items(results) { avatar ->

                        AvatarCard(
                            avatar = avatar,
                            onClick = {
                                selectedAvatar = avatar
                            }
                        )
                    }

                                items(onlineResults) { imageUrl ->

                OnlineAvatarCard(
                    imageUrl = imageUrl
                )
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
        return offlineAvatars
    }

    val matches = offlineAvatars
        .map { avatar ->

            var score = 0

            for (word in words) {

                if (
                    avatar.keywords.any { keyword ->
                        keyword.contains(word) ||
                            word.contains(keyword)
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
        .take(6)
        .map {
            it.first
        }

    return if (matches.isEmpty()) {
        offlineAvatars
    } else {
        matches
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
                text = "Scrivi una frase intera e MyAvatar " +
                    "troverà gli avatar più adatti.",
                textAlign = TextAlign.Center,
                color = MyAvatarDark
            )
        }
    }
}

@Composable
private fun AvatarCard(
    avatar: OfflineAvatar,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MyAvatarLightLilac
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(
                    id = avatar.imageRes
                ),
                contentDescription = avatar.name,
                modifier = Modifier
                    .size(145.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = avatar.name,
                fontWeight = FontWeight.Bold,
                color = MyAvatarDark,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Tocca per vedere",
                fontSize = 12.sp,
                color = MyAvatarLilac
            )
        }
    }
}
@Composable
private fun OnlineAvatarCard(
    imageUrl: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MyAvatarLightLilac
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Avatar online",
                modifier = Modifier
                    .size(145.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Avatar online",
                fontWeight = FontWeight.Bold,
                color = MyAvatarDark,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Tocca per vedere",
                fontSize = 12.sp,
                color = MyAvatarLilac
            )
        }
    }
}
@Composable
private fun AvatarDetail(
    avatar: OfflineAvatar,
    onBack: () -> Unit,
    onUseAvatar: () -> Unit
) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Anteprima",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MyAvatarDark
            )

            Spacer(modifier = Modifier.height(30.dp))

            Image(
                painter = painterResource(
                    id = avatar.imageRes
                ),
                contentDescription = avatar.name,
                modifier = Modifier
                    .size(250.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = avatar.name,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = MyAvatarDark
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = onUseAvatar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MyAvatarLilac
                )
            ) {
                Text(
                    text = "⭐ Usa questo avatar",
                    fontSize = 17.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MyAvatarDark
                )
            ) {
                Text(
                    text = "← Torna ai risultati",
                    fontSize = 17.sp
                )
            }
        }
    }
}

@Composable
private fun PersonalPhotoScreen(
    photo: Bitmap?,
    onBack: () -> Unit,
    onCamera: () -> Unit,
    onGallery: () -> Unit
) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "La tua foto",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MyAvatarDark
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "La foto rimane sul telefono.",
                textAlign = TextAlign.Center,
                color = MyAvatarDark
            )

            Spacer(modifier = Modifier.height(28.dp))

            if (photo != null) {

                Image(
                    bitmap = photo.asImageBitmap(),
                    contentDescription = "Foto personale",
                    modifier = Modifier
                        .size(240.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

            } else {

                Column(
                    modifier = Modifier
                        .size(240.dp)
                        .clip(CircleShape),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = "📷",
                        fontSize = 80.sp
                    )

                    Text(
                        text = "Nessuna foto",
                        color = MyAvatarDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = onCamera,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MyAvatarLilac
                )
            ) {

                Text(
                    text = "📷 Scatta una foto",
                    fontSize = 17.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onGallery,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MyAvatarDark
                )
            ) {

                Text(
                    text = "🖼️ Scegli dal telefono",
                    fontSize = 17.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "La trasformazione in avatar IA " +
                    "arriverà nel prossimo passaggio.",
                textAlign = TextAlign.Center,
                fontSize = 13.sp,
                color = MyAvatarLilac
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray
                )
            ) {

                Text(
                    text = "← Torna a MyAvatar",
                    color = MyAvatarDark
                )
            }
        }
    }
}

private fun saveAvatar(
    context: Context,
    avatarName: String
) {

    context
        .getSharedPreferences(
            "myavatar_preferences",
            Context.MODE_PRIVATE
        )
        .edit()
        .putString(
            "saved_avatar",
            avatarName
        )
        .apply()
}

private fun savePersonalPhoto(
    context: Context,
    bitmap: Bitmap
) {

    val file = File(
        context.filesDir,
        "myavatar_personal_photo.png"
    )

    file.outputStream().use { output ->

        bitmap.compress(
            Bitmap.CompressFormat.PNG,
            100,
            output
        )
    }
}

private fun loadPersonalPhoto(
    context: Context
): Bitmap? {

    val file = File(
        context.filesDir,
        "myavatar_personal_photo.png"
    )

    return if (file.exists()) {
        BitmapFactory.decodeFile(
            file.absolutePath
        )
    } else {
        null
    }
}
