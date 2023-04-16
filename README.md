# MA-Common-Utils

[![](https://jitpack.io/v/MohamedAlaaEldin636/MA-Common-Utils.svg)](https://jitpack.io/#MohamedAlaaEldin636/MA-Common-Utils) [![API](https://img.shields.io/badge/API-21%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=21) [![semantic-release](https://img.shields.io/badge/%20%20%F0%9F%93%A6%F0%9F%9A%80-semantic--release-e10079.svg)](https://github.com/semantic-release/semantic-release)

- Android Library to facilitate common tasks.

# Contents [▴](#ma-common-utils)

- [Features](#features-)
  - [Permissions handler](#permissions-handler-)
  - [Pick images or video handler](pick-images-or-video-handler-)
- [Usage](#usage-)

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

- TODO

## Usage [▴](#contents-)

1. [How to use permissions handler](#how-to-use-permissions-handler-)
2. TODO

### How to use permissions handler [▴](#usage-)

- Inside an `Activity`

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
