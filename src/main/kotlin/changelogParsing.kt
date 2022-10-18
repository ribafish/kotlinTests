
fun main() {
  val splits = changelog.split("# \\d".toRegex()).drop(1)

  println(splits)
}


val changelog = """
    # Changelog
    * Note: A: STM version | B: Nordic version | C: Bootloader version
    * Note: [u] Unleashed only | [e] Entagged only | [i] Internal (don't publish)
    * Note: rename all instances of "Unleashed" to "Entagged" for Entagged app.

    # 2022-08-17 A2.2.2
    ### Added
    - Preliminary support for more Nikon Z9 features

    # 2022-08-12 A2.2.1 B2.2.1
    ### Added
    - [u] Image previews for Canon CR3
    - [i] Image previews for Fuji RAF, Panasonic RW2 and Sony HIF
    - [i] Driving timelapse preview ratio, skipping frames
    - [i]Â Cancel image transfer BLE command

    ### Improved
    - Bluetooth parameter negotiation - more reliable connection
    - Accessory connection, especially slower ones like Holux and PINE

    ### Fixed
    - Bug where Canon EOS 5D Mark IV showed "Busy. Please wait" and couldnt turn off
    - Canon Trigger getting stuck
    - Nikon releasing USB trigger
    - [u] Nikon driving auto focus when starting video recording
    - [u] Nikon Video recording not starting on some older models
    - [u] Internal unrecovered error after doing 3 timelapses
    - [e] Leading delimiter appended to empty barcode
    - [i] Fuji preview buffering, deleting and transfer issues
    - [i] RAF, RW2, CR3 and HIF buffering implementation
    - [i] Embedded JPEG previews without merging EXIF

    ---

    # 2022-05-25 A2.2.0 B2.2.0
    ### Added
    - [i]Â Timelapse preview and preview ratio setting
    - [i] Getting any preview size during LRT ramping
    - [i] Image stacks

    ### Improved
    - [u] Immediate EV-value adjustment during LRT modified algorithms

    ### Fixed
    - Canon trigger issues
    - [e] Nikon trigger was sometimes blocked
    - [u] Duplicate previews when quality is RAW+JPEG with two memory cards
    - [u] Canon thumbnail when quality is JPEG+CR3
    - [i] GPS via BLE unknown GPS mode value
    - [i] Fuji was bricked when shooting in RAW
    - [i] Fuji aperture limits not shown
    - [i] Every Unleashed had full feature set enabled by accident

    ---

    # 2022-05-04 A2.1.4
    ### Fixed
    - [u] Autoramping Canon: Shutter speed was not ramped farther than 1.6 seconds on some cameras
    - [u] Autoramping: Ramping order now persists a setting becoming temporarily unavailable
    - [u] Nikon: Triggering with USB only

    ---


    # 2022-04-22 A2.1.3
    ### Added
    - Detecting wrongly inserted USB cable

    ### Fixed
    - 65k and more photos remaining were displayed as [?]
    - [u] Canon: Timelapse paused unexpectedly 
    - [u] Autoramping: Sometimes shutterspeed was not ramped correctly on sunrises
    - [u] Autoramping: Switching between program modes sometimes disabled ramping aperture or shutter speed
    - [u] Autoramping Canon: Sometimes not all settings were ramped
    - [u] Autoramping Nikon: Sometimes Shutterspeed jumped back to 30s during sunrise EV-24h without USB cable


    ---

    # 2022-03-21 A2.1.2 B2.1.3
    ### Added
    - [e] New barcode scanners: Inateck BCST-41 and GS M500BT-SE
    - [i] Fuji and Panasonic cameras as accessories, GPS via BLE
    - [i] Unit tests and mock objects for many of the PTP parsers
    - [i] Driving GPS settings, trigger and video controls in GPS via BLE mode

    ### Improved
    - Accessory discovery, pairing and handling
    - Internal USB data handling
    - [u] Camera shooting info now emulated using shutter speed on cameras that don't support it natively
    - [i] Refactored PTP Parsers for Canon and others for better testability
    - [i] serial number and features are dictated by nordic


    ### Fixed
    - Repeated reconnection to accessories failed eventually
    - Bug showing Unleashed is initializing
    - Unplugging USB cable on the camera side not being detected
    - [u] Settings not available until turning the mode dial on Canon
    - [u] Duplicates in image preview when shooting with 2 memory cards
    - [u] LRT algorithms and image review not always working on Z6/7 II
    - [u] Continuous shooting on some Canons
    - [e] Append barcode timeout not getting reset if duration changes
    - [i] Panasonic: Trigger timeout, trigger getting stuck
    - [i] Panasonic: Bulb triggering, half pressing
    - [i] Panasonic: USB initialization timeout if power via USB is enabled
    - [i] Panasonic: Cancelling image transfer for unsupported file types
    - [i] Panasonic: Unleashed is initializing never being cleared
    - [i] Sony: Deleting objects from RAM
    - [i] Canon: Digic 8 processor cameras support MPF, but not Innerdevelop

    ---

    # 2021-12-18 B2.1.2
    ### Added
    - [i] Syrp motion controller
    - [i] More parser unit tests
    - [i] Parser rework and fixes

    ### Improved
    - Accessory handling
    - [u] QStarz parsing more robust
    - [i] Syrp frame incrementation
    - [i] Multiple motion controller handling

    ### Fixed
    - Automatic reconnection of accessories
    - Connection parameter negotiation of accessories

    ---

    # 2021-11-23 A2.1.1 B2.1.1
    ### Added
    - [i] More parser unit tests
    - [i] BLE command for resetting Unleashed settings to default values

    ### Improved
    - USB communication
    - [i] Parser rework and fixes
    - [i] Canon: image previews for S2 and S3 sizes
    - [i] Panasonic, Fuji and Sony image previews
    - [u] Image handler

    ### Fixed
    - Nikon: Triggering in Mirror-Up (MUP) drive mode
    - [u] Canon: Camera never sleeping with GPS enabled

    ---

    # 2021-11-05 A2.1.0 B2.1.0
    ### Added
    - [i] Unit testing for parsers
    - [i] Getting and deleting images for Fuji, Panasonic and Sony
    - [i] Panasonic live view

    ### Improved
    - [u] Canon GPS priority during a timelapse/autoramping
    - USB communication with Nikon and Canon
    - [i] Major parser rework, fixed multiple packet edge cases that probably caused multiple bugs
    - [i] Canon state machine refactor
    - [i] Nikon state machine refactor

    ### Fixed
    - Settings stuck in 'initializing'
    - [u] Canon RAW and RAW+JPG image preview
    - [u] Canon triggering in GPS priority mode
    - [u] Stuck in Idle when using external GPS
    - [u] Reconnection loop when using external GPS
    - [u] Minimizing the app no longer cancels an ongoing image transfer
    - Reconnection issues issues with accessories

    ---

    # 2021-10-18 A2.0.1 B2.0.0 C2.0.0 

    ### Added 
    - New Bluetooth API - extendable and better for Android Bluetooth Stack
    - App state API
    - New APIs for future app features
    - [u] ND filter setting
    - [u] Autoramping with emulated Long Exposure setting (Shutterspeed in BULB)
    - Advanced Unleashed settings - sleep modes, timeouts, LED brightness, etc settable from app
    - Deep sleep modes including completely off mode
    - [u] Motion control support for third-party accessories
    - [u] Day/Night limits for shutter speed, aperture and ISO moved from app to firmware
    - [u] New setting to change the order of settings to be ramped
    - [u] Long exposure setting ON/OFF switching where possible
    - [u] Automatically setting long exposure to last shutter speed when switching LE ON
    - [u] Support for extended shutter speeds on Nikon Z series
    - [u] GPS mode changes to external when pairing a new GPS accessory
    - [u] Automatically disable GPS on short-interval timelapses in order to ensure higher accuracy interval shooting
    - [u] Warning on short-interval timelapses if the camera does not support them reliably
    - [u] EV value buffering for displaying graph when ramping
    - New camera model names
    - Failure recovery mechanisms
    - [e] Special barcode handling for triggering and configuration
    - [e] Special barcode to save Artist name
    - [i] Fuji support
    - [i] Sony support
    - [i] Panasonic support
    - [i] VGA, Full HD and Full resolution JPG preview images
    - [i] Gallery mode showing thumbnails of all images on card
    - [i] Selective download of individual images in VGA, Full HD and full resolution previews 
    - [i] Flexible Intervals
    - [i] Short Intervals 
    - [i] Exposure bracketing



    ### Improved
    - Bluetooth responsiveness
    - Internal communication speed increased considerably
    - USB communication reliability
    - [u] Communication with Nikon cameras without USB cable
    - [u] Timelapse and autoramping (completely overhauled)
    - Tap-to-trigger mechanism
    - [u] Image quality handling on Canon cameras with multiple card slots
    - Third-party accessory support
    - Accessory connection speed
    - [u] Bulk data transfer mechanism
    - Power and meter-on detection on Nikon cameras
    - LED sequences and responsiveness
    - Error reporting in some edge cases
    - [e] Default timeouts much longer to reflect typical usage

    ### Fixed
    - USB initialization issues
    - Slow reconnection issues
    - Battery drain issues
    - Triggering during playback on Canon
    - Several triggering issues on Canon cameras
    - Issue causing trigger detection not to work on most Nikon cameras
    - [u] Intervalometer triggering issues
    - [u] Several autoramping issues
    - [u] Edge case in autoramping changes
    - [u] Partial autoramping (e.g. ramping with manual lenses)
    - [u] Timelapse stopping after one or two shots
    - [u] Timelapse trigger not updating after the timelapse is done
    - [u] LRT Algorithm Autoramping on Nikon D3100/D3200
    - [u] Image Review for Nikon D3100/D3200
    - [u] Thumbnails sent twice on Canon cameras saving RAW + JPEG
    - [u] Video trigger not updating after stopping video on some Nikon cameras
    - [u] Video issues on Nikon Z series
    - [u] Video issues on Nikon DSLRs
    - [u] Video issues on Canon cameras
    - [u] Geotagging videos on Canon cameras
    - [u] Flickering to ISO Auto on Canon cameras in certain conditions
    - [u] Shutter speed flickering on geotagged long exposure shots in Canon cameras
    - [u] Shutter speed steps on Canon cameras
    - [u] Focus mode issues on Nikon cameras with USB cable
    - [u] EV meter parsing issue in Canon cameras
    - [u] Default initialization on some settings
    - Failure recovery mechanism issues
    - Issue causing Unleashed to think that a Nikon camera is off when it is not
    - Sleep mode failing to work sometimes
    - Bug causing Unleashed to get stuck in sleep mode if it has gone 49 days without losing power
    - Bug causing accessories not to reconnect after waking up from sleep mode 
    - [u] Handling of long lists of setting options
    - [u] Protocol issues on newer Nikon cameras without USB cable
    - Playback/Menu buttons disabled on newer Nikon cameras (Z 6 II/ Z 7 II)
    - [u] Wrong error shown on long exposure setting
    - [u] Shutter speed staying in BULB when changing from BULB to another mode in some Canon cameras",
    - SD card error on trigger not clearing when installing SD card
    - [u] Number of photos remaining in timelapse when not shooting a timelapse
    - [u] Issue causing settings not to update when the camera has values unknown to the Unleashed
    - Missing shooting information on some Canon cameras
    - Recovery mode issues
    - [e] Canon EOS R series didn't store barcodes
    - [e] Canon USB freeze with barcodes on some cameras
    - [e] Some Canon cameras becoming sluggish in Live-view after barcode scan
    - [e] Second barcode scan (after taking first photo) failing on some older Canon cameras like 7D or 5D Mark II
    - [e] Scanning or triggering not working during playback on Canon
    - [e] Scanner disconnecting unexpectedly
    - [e] Scanner not reconnecting unless camera battery was removed
    - [e] Sometimes Entagged with RFID scanner only saved part of the data
""".trimIndent()
