import androidx.compose.material.MaterialTheme
import androidx.compose.ui.window.singleWindowApplication
import com.numq.common.application.Application

fun main() = singleWindowApplication {
    MaterialTheme {
        Application()
    }
}