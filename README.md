# PhoenixMinerGUI
Unofficial PhoenixMiner GUI for windows
![Unofficial PhoenixMiner GUI for windows](/src/main/resources/phoenixgui.png?raw=true "Unofficial PhoenixMiner GUI for windows")

## Installation
1. Download PhoenixMiner for windows from [official website](https://phoenixminer.org)
2. Extract it's contents
3. Download newest PhoenixMinerGUI from [Releases section](https://github.com/KamilKurde/PhoenixMinerGUI/releases)
   * if you want to test PhoenixMinerGUI without installing it, download "portable" version. Otherwise, download "installer" version.
4. Install PhoenixMinerGUI or extract its contents (for installer and portable respectively)
5. After opening PhoenixMinerGUI for the first time, it will ask you to point to PhoenixMiner.exe file (which you extracted earlier).
   * After that, PhoenixMinerGUI will check if PhoenixMiner responds correctly
   * PhoenixMinerGUI check for PhoenixMiner.exe on each startup, don't change its name because it's hardcoded into the app
   * All data including PhoenixMiner.exe path is stored in *%localappdata%\PhoenixMinerGUI*

## Usage
1. Main app screen consists of two tables, the upper one shows GPUs detected by PhoenixMiner, the bottom one shows your configured miners
2. To create new miner simply click on "Create new miner" button which will take you to the configuration screen
   * You can change name of the miner in the upper text field
   * Changes aren't saved automatically, to save changes you need to click on the "Save" button
   * Settings table consists of 4 columns:
     1. Checkbox to enable/disable option (visible only on options that aren't required by the miner)
     2. Name is simply command line argument name, table is sorted alphabetically by it (excluding required options, they are always on top)
     3. Description is shortened description from PhoenixMiner docs
     4. Value is field for changing parameter, if it's text field, and it turns red that means current data that's entered in there isn't in correct format.
        * In that case when you save settings it will take last correct value (so e.g. when you enter "80" as value of Ttli and you hit backspace it changes to "8" which is a correct value and when you hit backspace it changes to "" which isn't correct value, so when you hit "Save" button it will save Ttli as "8")
3. On the left of the miner name you have two buttons:
   * Start/Stop button which changes depending on miner status
   * Settings button which takes you back to the configuration screen of the miner (you can delete miner in there using red button on the bottom)
4. You can start miner from command line by launching PhoenixMinerGUI and passing ID or Name of the miner as arguments (you can mix usage of IDs and Names), if the name contains spaces you need to wrap it in quotation marks
   * (eg, if you have "My Miner" with ID 1, and "The other miner" with ID 2, and you want to run them both then you can do this like that: "*[Path to PhoenixMinerGUI folder]*\PhoenixMiner GUI.exe "My Miner" 2", this will start PhoenixMiner, and it will 
      automatically start both miners)

## Notes
* Keep in mind that this app is in alpha, if you encounter any bugs report them
* This app uses unfinished UI library called [Desktop Compose](https://www.jetbrains.com/lp/compose/), this means that UI of the app can behave weirdly (e.g. flicker)
* App weights a lot (along with the app files there is whole runtime environment packed, if you know how can we make this app lighter, please contact [main dev](https://github.com/KamilKurde) )
* Feel free to contribute
