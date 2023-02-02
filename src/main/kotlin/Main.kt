import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import javax.swing.JFileChooser
import javax.swing.filechooser.FileSystemView
import java.io.File

@Composable
fun app() {
    MaterialTheme {
        mainComposable()
    }
}


fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        state = WindowState(size = DpSize(450.dp, 350.dp)),
        resizable = false,
        title = "New categories seed file generator"
    ) {
        app()
    }
}


@Composable
fun mainComposable() {
    var commonBtnText by remember { mutableStateOf("open") }
    var dumpBtnText by remember { mutableStateOf("open") }
    var commonFilePass by remember { mutableStateOf("") }
    var staplesDumpPass by remember { mutableStateOf("") }
    var textFieldValue by remember { mutableStateOf("") }
    MaterialTheme(
        colors = darkColors(
            primary = Color.LightGray,
            background = Color.Gray
        )
    ) {
        Column(
            modifier = Modifier.size(width = 400.dp, height = 1000.dp)
        ) {
            //OpenFileElement("Choose common dump", "213", onClick1 = ret())
            OpenFileElement("Select common dump", commonBtnText) {
                commonFilePass = pickFile(".csv")
                if (commonFilePass != "") commonBtnText = "Selected"
            }
            OpenFileElement("Select staples dump", dumpBtnText) {
                staplesDumpPass = pickFile(".xlsx")
                if (staplesDumpPass != "") dumpBtnText = "Selected"
            }
            Row {
                TextField(
                    modifier = Modifier.verticalScroll(rememberScrollState()).size(280.dp, 100.dp)
                        .padding(start = 20.dp, top = 10.dp),
                    maxLines = 3,
                    value = textFieldValue,
                    onValueChange = { textFieldValue = it },
                    label = { Text("Enter categories") },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        unfocusedBorderColor = MaterialTheme.colors.primary.copy(alpha = ContentAlpha.high),
                        unfocusedLabelColor = MaterialTheme.colors.primary.copy(alpha = ContentAlpha.high),
                    )
                )
                Button(
                    modifier = Modifier.size(290.dp, 100.dp).padding(start = 20.dp),
                    onClick = {
                        SeedFileGenerator(commonFilePass, staplesDumpPass, textFieldValue).generateSeedRequestFile()
                    },
                    shape = RoundedCornerShape(50),
                ) {
                    Text("Generate", fontSize = 12.sp)
                }
            }
        }
    }
}

fun pickFile(extension: String): String {
    val jfc = JFileChooser(FileSystemView.getFileSystemView().homeDirectory)
    jfc.currentDirectory = File(System.getProperty("user.dir"))
    val returnValue = jfc.showOpenDialog(null)
    return if (returnValue == JFileChooser.APPROVE_OPTION) {
        val selectedFile = jfc.selectedFile
        if (selectedFile.absolutePath.endsWith(extension)) return selectedFile.absolutePath else ""
    } else ""
}


