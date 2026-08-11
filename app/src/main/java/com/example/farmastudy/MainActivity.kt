package com.example.farmastudy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.farmastudy.ui.navigation.FarmaNavGraph
import com.example.farmastudy.ui.theme.FarmaStudyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FarmaStudyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FarmaNavGraph(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FarmaNavGraphPreview() {
    FarmaStudyTheme {
        FarmaNavGraph()
    }
}