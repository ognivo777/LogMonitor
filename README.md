# LogMonitor

LogMonitor is a Java Swing application for working with live logs over remote connections (SSH/FTP/SMB/etc.). It continuously tails logs and lets you quickly search and filter what you see while keeping an optional auto-scroll "follow tail" behavior.

## What it can do

- Tail logs continuously from configured sources (for SSH it uses `tail -n 3000 -f <path>`)
- Pause/resume auto-follow while you scroll or search
- Search within the current log buffer and show match count
- Filter displayed content:
  - line filter (grep) via `Ctrl + G` / `Ctrl + Shift + G`
  - block filter via `Ctrl + B` / `Ctrl + Shift + B` (only when `blockPattern` is configured)
- Toggle line wrapping (`W`)
- Open a snapshot of the current log tab in a popup (`P`)
- Save a full remote file via the `...` menu for each configured log
- Clear the visible buffer (`C`) and reload (`F5`)

## Supported log sources

Configured in `settings.xml` via `<server ... serverType="...">`:

- `SSH` (default when `serverType` is omitted)
  - Tails using SSH
  - "Save full file" tries compression first:
    - `zip` (preferred if `zip` exists on the remote host)
    - otherwise `gzip` (if available)
- `FTP` (polls and downloads updated content)
- `SMB` (SMB v1; implemented via `jcifs`)
- `SMBv2` (SMB v2; implemented via `smbj`)
- `File` (local file)
- `SHUB` (SocketHub client)
- `Pega` (Pega log source)

## Keyboard shortcuts

Shortcuts are defined by the included `res/Shortcuts.html` and implemented primarily in `ru.lanit.dibr.utils.gui.LogPanel`.

- Search:
  - `Ctrl + F`: find text
  - `F3`: next match
  - `Shift + F3`: previous match
- Filtering:
  - `Ctrl + G`: show only lines containing text (grep filter)
  - `Ctrl + Shift + G`: hide lines containing text
  - `Ctrl + B`: show only blocks matching text/pattern (block filter; requires `blockPattern`)
  - `Ctrl + Shift + B`: hide blocks matching
- Other:
  - `F1`: open shortcuts window
  - `ESC`: close the current log window/popup
  - `C`: clear the visible log buffer
  - `P`: open a popup snapshot of the current log tab
  - `F5`: reload (and reset behavior depends on modifier)
  - `Shift + F5`: reset filters and reload
  - `W`: toggle line wrapping
  - `Ctrl + S`: search similar blocks (block-based)

## Configuration (`settings.xml`)

Run the app from a folder that contains `settings.xml` (or pass a custom path as the first argument; default is `settings.xml`). If the file does not exist, the app creates a stub `<settings> ... </settings>` file.

Key sections:

- `<theme>`: colors and font
- `<tunnel>`: optional SSH tunneling definitions
- `<server ...>`: one or more servers/hosts
- `<log ...>`: one or more logs per server

Example `settings.xml` (trimmed):

```xml
<settings>
  <theme>
    <colors background="24110b" backgroundSelected="4d5f72" text="d6cbb0" textSelected="4af733"/>
    <font name="Courier New" style="0" size="12"/>
  </theme>

  <tunnel name="SSHTUN" host="123.123.123.123" user="guest" password="pass" port="22">
    <L localPort="30022" destHost="122.168.1.15" destPort="22" />
  </tunnel>

  <server name="SSH Server" host="example.com" user="andrew" port="22" password="123" encoding="utf-8">
    <log name="Log" file="/var/myLog.log"/>
  </server>

  <server serverType="FTP" name="simple FTP" host="example.com" port="21" user="anonymous" password="1">
    <log name="acces.log" file="/log/access.log"/>
    <log name="error.log" file="/log/error.log"/>
  </server>

  <server serverType="SMB" name="SMB" host="10.15.1.22" user="smbUser" password="smbPassword">
    <log name="log" file="Share/2016.02.12_03.17.12.756.log"/>
  </server>

  <server serverType="File" name="MyLocalLog">
    <log name="log" file="C:\\tmp\\latest.log"/>
  </server>
</settings>
```

If you want block-based filtering and block popups:

- set `blockPattern` on `<log ... blockPattern="...">`
- use `Ctrl + B` / `Ctrl + Shift + B`
- double-click inside the log content to open a popup with the matching block (XML formatting can be applied when enabled)

## Build & run

Requirements:

- Java 8
- Gradle wrapper (`gradlew` / `gradlew.bat`)

Build:

```sh
# Windows (PowerShell)
.\gradlew.bat jar

# Linux/macOS
./gradlew jar
```

Run (example):

```sh
java -jar build/libs/LogMonitor-1.0-SNAPSHOT.jar settings.xml
```

Optional:

- `-fontSize <number>` (changes the UI font size)

The runnable jar is configured with `Main-Class: ru.lanit.dibr.utils.Main` (see `build.gradle`).
