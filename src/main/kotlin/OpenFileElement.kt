import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OpenFileElement(labelText: String, buttonText: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.padding(start = 20.dp)
    ) {
        Button(
            modifier = Modifier.padding(top = 20.dp),
            onClick =  onClick,
            shape = RoundedCornerShape(50), // = 50% percent
        ) {
            Text(buttonText)
        }
        Text(labelText, modifier = Modifier.padding(start = 30.dp, top = 25.dp))
    }
}