# MA-Common-Utils

[![](https://jitpack.io/v/MohamedAlaaEldin636/MA-Common-Utils.svg)](https://jitpack.io/#MohamedAlaaEldin636/MA-Common-Utils) [![API](https://img.shields.io/badge/API-21%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=21) [![semantic-release](https://img.shields.io/badge/%20%20%F0%9F%93%A6%F0%9F%9A%80-semantic--release-e10079.svg)](https://github.com/semantic-release/semantic-release)

- Android Library to facilitate common tasks.

# Contents [▴](#ma-common-utils)

- [Install](#install-)
  - [Jitpack Environment](#jitpack-environment-)
  - [Library](#library-)
- [Features](#features-)
  - [Permissions handler](#permissions-handler-)
  - [Pick images or video handler](pick-images-or-video-handler-)
- [Usage](#usage-)
  - [How to use permissions handler](#how-to-use-permissions-handler-)
    - [Inside an `Activity`](#inside-an-activity-)
    - [Inside a `Fragment`](#inside-a-fragment-)
  - todo

## Install [▴](#contents-)

- [Jitpack Environment](#jitpack-environment-)
- [Library](#library-)

### Jitpack Environment [▴](#install-)

- in your Gradle **Top-level** build file add below code

``` kotlin
// Note this is Kotlin DSL Not Groovy.
allprojects {
    repositories {
        maven { url = uri("https://jitpack.io") }
    }
}
```

### Library [▴](#install-)

- X.Y.Z denotes library version which is [![](https://jitpack.io/v/MohamedAlaaEldin636/MA-Common-Utils.svg)](https://jitpack.io/#MohamedAlaaEldin636/MA-Common-Utils)

- in your Gradle **Module-level** build file add below code

``` kotlin 
plugins {
    kotlin("android")
    // ...
}
// ...
dependencies {
    implementation("com.github.MohamedAlaaEldin636:MA-Common-Utils:X.Y.Z")
    // ...
}
```

## Features [▴](#contents-)

1. [Permissions handler](#permissions-handler-)
2. [Pick images or video handler](pick-images-or-video-handler-)

### Permissions handler [▴](#features-)

- Handles all cases with runtime permissions, as there are several cases :-
  - User deny permission with don't show again option,
    then here when you request permission a dialog with description will appear and on click of
    it's button will open app's settings as it's the only way to change permissions in case denied
    like that.
  - User deny permissions once without don't show again so `shouldShowRequestPermissionRationale`
    would return `true` then a dialog will appear telling user to use this feature then these permissions
    are needed to be accepted.
  - In case subset of permissions are accepted you can either re-request permissions to grant them all
    or act according to permissions granted.

### Pick images or video handler [▴](#features-)

- Picks image(s) or video from either camera or gallery.
- Grants appropriate permissions according to current Android API for Ex. starting from API 33
  Manifest.permission.READ_MEDIA_IMAGES is required to get image(s) from gallery.
- You can choose any mix you want ex. get image from camera, OR get image from camera or gallery, 
  OR get image or video from camera or gallery, and in case where more than 1 option is possible 
  then you will provide the library with a view and a popup anchored to it will be shown to choose
  the required option.
- You have the ability to provide a max limit for video length.
- you can select multiple images not a single image.

## Usage [▴](#contents-)

1. [How to use permissions handler](#how-to-use-permissions-handler-)
    - [Inside an `Activity`](#inside-an-activity-)
    - [Inside a `Fragment`](#inside-a-fragment-)
2. [How to use pick images or video handler](#how-to-use-pick-images-or-video-handler-)
    - TODO

### How to use permissions handler [▴](#usage-)

1. [Inside an `Activity`](#inside-an-activity-)
2. [Inside a `Fragment`](#inside-a-fragment-)

#### Inside an `Activity` [▴](#how-to-use-permissions-handler-)

```kotlin
class MyActivity : AppCompatActivity() {

  // ---- Using ext functions ---- //

  private val launcherOfThePermission = createPermissionHandlerForSinglePermission(Manifest.permission.THE_PERMISSION) {
    // Act when permission accepted by user.
  }

  private val launcherOfSeveralPermissions = createPermissionHandlerAndActOnlyIfAllGranted(
    Manifest.permission.PERMISSION_1,
    Manifest.permission.PERMISSION_2,
    Manifest.permission.PERMISSION_3,
  ) {
    // Act when ALL permissions accepted by user.
  }

  // ---- Using constructor ---- //

  private val launcherOfSeveralPermissions2 = PermissionsHandler(
    this,
    lifecycle,
    this,
    listOf(Manifest.permission.PERMISSION_1, Manifest.permission.PERMISSION_2),
    object : PermissionsHandler.Listener {
      override fun onAllPermissionsAccepted() {
        // Act when ALL permissions accepted by user.
      }

      override fun onSubsetPermissionsAccepted(permissions: Map<String, Boolean>) {
        // Act when subset of the permissions are accepted and 
        // you Can know which are accepted by using below code
        val isPermission1Accepted = permissions[Manifest.permission.PERMISSION_1] == true
      }
    }
  )

}
```

#### Inside a `Fragment` [▴](#how-to-use-permissions-handler-)

```kotlin
class MyFragment : Fragment() {

  // MUST be used like below flow, Otherwise an exception will be thrown.
  // flow is -> created only before super.onCreate() but inside that function not on declaration
  // of the property.

  private lateinit var launcherOfThePermission: PermissionsHandler

  override fun onCreate(savedInstanceState: Bundle?) {
    launcherOfThePermission = createPermissionHandlerForSinglePermission(Manifest.permission.THE_PERMISSION) {
      // Act when permission accepted by user.
    }

    super.onCreate(savedInstanceState)
  }

}
```

### How to use pick images or video handler [▴](#usage-)

1. Setup 
2. TODO
