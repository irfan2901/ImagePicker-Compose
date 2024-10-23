package com.example.imagepickercompose

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil3.compose.rememberAsyncImagePainter

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val singleImage = remember { mutableStateOf<Uri?>(null) }
    val multipleImages = remember { mutableStateOf<List<Uri>>(emptyList()) }
    val context = LocalContext.current

    val pickSingleImageLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            singleImage.value = uri
        }

    val pickMultipleImagesLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
            multipleImages.value = uris
        }

    val permissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                pickSingleImageLauncher.launch("image/*")
            } else {
                Toast.makeText(context, "Permission denied...", Toast.LENGTH_SHORT).show()
            }
        }

    Column(
        modifier
            .fillMaxSize(),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp)
    ) {
        Button(onClick = {
            checkAndRequestPermission(context, permissionLauncher, pickSingleImageLauncher)
        }, modifier.padding(16.dp)) {
            Text("Pick image")
        }

        singleImage.value?.let { uri ->
            Image(
                painter = rememberAsyncImagePainter(model = uri),
                contentDescription = null,
                modifier = Modifier
                    .width(200.dp)
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .padding(4.dp)
            )
        }

        Button(onClick = {
            pickMultipleImagesLauncher.launch("image/*")
        }, modifier.padding(16.dp)) {
            Text("Pick multiple images")
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .fillMaxHeight()
        ) {
            items(multipleImages.value) { uri ->
                Box(modifier.fillMaxSize()) {
                    Image(
                        painter = rememberAsyncImagePainter(model = uri),
                        contentDescription = null,
                        modifier = Modifier
                            .width(200.dp)
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = null,
                        modifier
                            .padding(8.dp)
                            .align(alignment = Alignment.TopEnd)
                            .clickable { multipleImages.value = multipleImages.value.filter { it != uri } }
                    )
                }
            }
        }
    }
}

fun checkAndRequestPermission(
    context: Context,
    permissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
    pickSingleImageLauncher: ManagedActivityResultLauncher<String, Uri?>
) {
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE)
            } else {
                pickSingleImageLauncher.launch("image/*")
            }
        }

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                pickSingleImageLauncher.launch("image/*")
            }
        }

        else -> {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                pickSingleImageLauncher.launch("image/*")
            }
        }
    }
}
