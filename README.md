# CutoutScreenSupport
Android cutout screen support Android P. Android O support huawei, xiaomi, oppo and vivo.

## Usage

1. whether the mobile phone is cutout screen
```kotlin
CutoutScreenSupport.isCutoutScreen(window)
```

2. obtain cutout screen size
```kotlin
// cutout height
var cutoutHeight = CutoutScreenSupport.getCutoutHeight(window)

// cutout rect
val cutoutRect = CutoutScreenSupport.getCutoutRect(window)
```

3. occupy cutout screen
```kotlin
CutoutScreenSupport.setDisplayCutoutScreen(window)
```

4. clear occupy cutout screen
```kotlin
CutoutScreenSupport.clearDisplayCutoutScreen(window)
```

## License

    Copyright 2020 hacket

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.