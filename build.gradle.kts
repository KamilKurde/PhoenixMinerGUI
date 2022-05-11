import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.jetbrainsCompose
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.6.10"
	id("org.jetbrains.compose") version "1.1.1"
	kotlin("plugin.serialization") version "1.6.10"
}

group = "com.github.KamilKurde"
val currentVersion = "0.7.5"
val appName = "PhoenixMiner GUI"
version = currentVersion

repositories {
	mavenCentral()
	jetbrainsCompose()
	google()
	maven { url = uri("https://jitpack.io") }
}

tasks.withType<KotlinCompile> {
	kotlinOptions.jvmTarget = "16"
}

java {
	sourceCompatibility = JavaVersion.VERSION_16
	targetCompatibility = JavaVersion.VERSION_16
}
dependencies {
	implementation(compose.desktop.currentOs)
	implementation("com.github.pgreze:kotlin-process:1.3.1")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
	implementation("com.github.KamilKurde:cdalf:0.2.10")
}

compose.desktop {
	application {
		mainClass = "MainKt"
		nativeDistributions {
			targetFormats(TargetFormat.Exe)
			packageName = appName
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

abstract class UpdateVersionFile : DefaultTask() {
	
	@Input
	var file = ""
	
	@Input
	var version = ""
	
	@TaskAction
	fun updateVersion() {
		File(file).writeText("const val VERSION = \"$version\"")
	}
}

tasks.register<UpdateVersionFile>("updateVersionFileToCurrent")
{
	file = project.file("src" + File.separator + "main" + File.separator + "kotlin" + File.separator + "version.kt").absolutePath
	version = currentVersion
}

tasks.register<UpdateVersionFile>("updateVersionFileToDevelopment")
{
	file = project.file("src" + File.separator + "main" + File.separator + "kotlin" + File.separator + "version.kt").absolutePath
	version = "DEVELOPMENT"
}

tasks.register<Copy>("copyExe")
{
	from(tasks.getByName("packageExe"))
	into(project.projectDir.absolutePath + File.separator + "distributables")
	rename("(.+)", appName.replace(" ", "") + "-" + currentVersion + "-Setup.exe")
}

tasks.register<Zip>("zipDistributable")
{
	from(tasks.getByName("createDistributable"))
	archiveFileName.set(appName.replace(" ", "") + "-" + currentVersion + "-Portable.zip")
	destinationDirectory.set(File(project.projectDir.absolutePath + File.separator + "distributables" + File.separator))
}

tasks.register<Delete>("cleanDistributablesDir")
{
	delete(project.projectDir.absolutePath + File.separator + "distributables")
}

// Main task to use for building distributables for both installer and portable
tasks.register<GradleBuild>("bundleDistributables")
{
	dependsOn("updateVersionFileToCurrent", "cleanDistributablesDir")
	tasks = listOf("zipDistributable", "copyExe")
	finalizedBy("updateVersionFileToDevelopment")
}