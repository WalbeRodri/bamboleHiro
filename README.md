# BamboleHiro

# Libs
- [Libgdx](https://libgdx.badlogicgames.com/)

# Setup
- Create a file `local.properties` to set the local AndroidSDK:
```
# Location of the android SDK
sdk.dir=/home/.../Android/Sdk
```
- ...

# Build and Running
- Desktop `./gradlew desktop:dist` and `./gradlew desktop:run`
- Android `./gradlew android:assembleDebug` and `./gradlew android:installDebug android:run`

More information about [build and run](https://github.com/libgdx/libgdx/wiki/Gradle-on-the-Commandline)
