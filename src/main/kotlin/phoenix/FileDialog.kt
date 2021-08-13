package phoenix

import androidx.compose.ui.awt.ComposeWindow
import java.io.File
import java.awt.FileDialog
import java.io.FilenameFilter

fun openFileDialog(window: ComposeWindow, title: String): File = FileDialog(window, title, FileDialog.LOAD).apply {
	isMultipleMode = false
	file = "PhoenixMiner.exe"
	filenameFilter = FilenameFilter { _, name -> name?.endsWith(".exe") ?: false }
	isVisible = true
}.files[0]