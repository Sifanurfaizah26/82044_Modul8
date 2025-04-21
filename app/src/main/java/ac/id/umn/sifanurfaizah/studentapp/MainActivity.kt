package ac.id.umn.sifanurfaizah.studentapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ac.id.umn.sifanurfaizah.studentapp.ui.theme.StudentAppTheme
import androidx.compose.material3.MaterialTheme
import com.google.firebase.FirebaseApp
import android.util.Log

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState:
                          Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)
        setContent {
            MaterialTheme {
                StudentRegistrationScreen()
            }
        }
    }
}
