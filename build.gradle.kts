import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
	kotlin("jvm") version "1.5.21"
	id("org.jetbrains.compose") version "1.0.0-alpha3"
	kotlin("plugin.serialization") version "1.5.20"
}

group = "com.github.KamilKurde"
val currentVersion = "0.2.2"
version = currentVersion

repositories {
	mavenCentral()
	maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
	google()
}

dependencies {
	implementation(compose.desktop.currentOs)
	implementation("com.github.pgreze:kotlin-process:1.3.1")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
}

compose.desktop {
	application {
		mainClass = "MainKt"
		nativeDistributions {
			targetFormats(TargetFormat.Exe)
			packageName = "PhoenixMiner GUI"
			vendor = "github.com/KamilKurde"
			description = "GUI for PhoenixMiner"
			packageVersion = currentVersion
			windows {
				iconFile.set(project.file("src" + File.separator + "main" + File.separator + "resources" + File.separator + "icon.ico"))
				dirChooser = true
				perUserInstall = true
				shortcut = true
				menuGroup = "PhoenixMiner"
				upgradeUuid = "490C9621-F446-4A79-8DA1-4C7DD7546E97"
			}
		}
	}
}