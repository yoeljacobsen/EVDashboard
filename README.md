# EVDashboard

This project is an Android Auto application designed to display electric vehicle (EV) related information, such as State of Charge (SoC) and Range.

## Development Journey and Obstacles

This section outlines the challenges faced during the development of this application, particularly concerning the integration with Android Auto's Car API and handling car hardware data.

### Initial Problem: CarInfo Instantiation and API Level 0

**Problem:** The application failed to instantiate `CarInfo` on both the Desktop Head Unit (DHU) and a real car. Additionally, the Car API level was consistently displayed as 0.

**Initial Diagnosis & Attempted Fixes:**
*   **Missing Dependency:** The initial thought was a missing dependency. `app-automotive` was checked in `app/build.gradle`.
*   **`automotive_app_desc.xml`:** An invalid `app` attribute was found and removed from this file.
*   **`minCarApiLevel` Placement:** The `meta-data` tag for `androidx.car.app.minCarApiLevel` was found to be incorrectly placed in `AndroidManifest.xml` and was moved to be a direct child of the `application` tag.
*   **App Categorization:** The app was initially categorized as `NAVIGATION`, which was incorrect for a dashboard application. This was changed to `IOT` in `AndroidManifest.xml`. An attempt was made to override `isNavigation()` in `EVDashboardCarAppService`, but this method does not exist in `CarAppService`.

### Recent Fixes

**1. `CAR_FUEL` Security Exception Resolution:**
The persistent `java.lang.SecurityException: The car app does not have the required permission: com.google.android.gms.permission.CAR_FUEL` was resolved. The issue stemmed from `addEnergyLevelListener` being called on `ProjectedCarInfo` instances, which implicitly required the `CAR_FUEL` permission. The logic in `EVDashboardSession.java` was corrected to ensure that `addEnergyLevelListener` is only invoked when `mCarInfo` is *not* an instance of `ProjectedCarInfo`, thus preventing the security exception in the Android Auto environment.

**2. `NullPointerException` on `onDestroy` Resolution:**
A `java.lang.NullPointerException` occurring during the `onDestroy` lifecycle event was resolved. This happened because `removeEnergyLevelListener` was being called on a `null` `mEnergyLevelListener` when the app was running in Android Auto mode (where the listener was never initialized). A null check for `mEnergyLevelListener` was added in the `onDestroy` method to prevent this crash.

**3. Compilation Errors Resolution:**
Several structural compilation errors in `EVDashboardSession.java` were identified and resolved. These errors were primarily due to incorrect brace matching and `try-catch` block placement introduced during previous modifications. The file's structure was corrected to ensure proper syntax and compilation.

### Persistent `CAR_FUEL` Security Exception

**Problem:** Despite removing the `CAR_FUEL` permission from `AndroidManifest.xml` and attempting to prioritize `getBatteryPercent()` over `getFuelPercent()`, a `java.lang.SecurityException: The car app does not have the required permission: com.google.android.gms.permission.CAR_FUEL` continued to occur. This crash happened specifically when `mCarInfo.addEnergyLevelListener` was called.

**Diagnosis & Attempted Fixes:**
*   **Runtime Permission:** It was identified that `CAR_FUEL` is a runtime permission. Attempts were made to request this permission programmatically using `CarContext.requestPermissions()`, but this led to further build errors due to incorrect usage of the API.
*   **Explicit Property Request:** An attempt was made to explicitly request only `PROPERTY_BATTERY_PERCENTAGE` and `PROPERTY_RANGE_REMAINING_METERS` when adding the `EnergyLevelListener`. This also resulted in build errors as these properties were not directly available on `EnergyLevel` or `CarInfo` as assumed.
*   **Lingering References:** A comprehensive search for "CAR_FUEL" across the project confirmed no explicit references remained in the source code. This suggested the issue was likely due to cached data on the Android Auto host. Clearing Android Auto app data and cache was recommended to the user.

### Current Status and Understanding

The persistent `CAR_FUEL` security exception, even after extensive attempts to remove the permission and control data requests, indicates a fundamental behavior of the Android Auto host when dealing with `ProjectedCarInfo`. It appears that when `addEnergyLevelListener` is called on a `ProjectedCarInfo` object (which is what Android Auto provides), the host implicitly attempts to provide *all* available `EnergyLevel` data, including fuel-related data, and requires the `CAR_FUEL` permission to do so.

**Current Solution:**
To circumvent the `CAR_FUEL` permission issue, the application now explicitly uses **mock data for `EnergyLevel` when running on Android Auto (i.e., when `mCarInfo` is an instance of `ProjectedCarInfo`)**.

**Remaining Issues:**
*   **Car API Level Display:** The Car API level is still displayed as 0. This needs further investigation to understand why `getCarAppApiLevel()` is returning 0 in the Android Auto environment.
*   **Version Ascent:** The app version is not ascending as expected, indicating a potential issue with the build and deployment process not correctly updating the installed APK.

**Next Steps:**
*   Further investigate why `getCarAppApiLevel()` returns 0 on Android Auto.
*   Verify the build and deployment process to ensure the latest APK with the correct version is always installed.
*   Continue testing on a real car to confirm the mock data workaround is effective and to gather more specific logs for the remaining issues.