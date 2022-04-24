package functions

import com.github.pgreze.process.process
import data.tmp
import kotlinx.coroutines.*
import java.io.File

object Shortcut {
	
	val location get() = System.getenv("APPDATA") + File.separator + "Microsoft" + File.separator + "Windows" + File.separator + "Start Menu" + File.separator + "Programs" + File.separator + "Startup" + File.separator + "PhoenixMinerGUI.lnk"
	
	val exeLocation get(): String = File("PhoenixMiner GUI.exe").absolutePath
	
	val exist get() = File(location).exists()
	
	fun delete() = File(location).delete()
	
	@Suppress("EXPERIMENTAL_IS_NOT_ENABLED", "BlockingMethodInNonBlockingContext", "OPT_IN_IS_NOT_ENABLED")
	@OptIn(ExperimentalCoroutinesApi::class)
	fun create() {
		CoroutineScope(Job() + Dispatchers.IO).launch {
			val file = File(tmp + File.separator + "startupShortcutMaker.vbs")
			file.createNewFile()
			
			// Based on https://superuser.com/questions/392061/how-to-make-a-shortcut-from-cmd#answer-392082
			file.writeText(
				"Set oWS = WScript.CreateObject(\"WScript.Shell\")\n" +
						"sLinkFile = \"$location\"\n" +
						"Set oLink = oWS.CreateShortcut(sLinkFile)\n" +
						"oLink.IconLocation=\"${File("PhoenixMiner GUI.ico").absolutePath}, 0\"\n" +
						"oLink.TargetPath = \"$exeLocation\"\n" +
						"oLink.Save\n"
			)
			process("wscript", file.absolutePath)
		}
	}
}