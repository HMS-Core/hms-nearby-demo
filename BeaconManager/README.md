# NearbyMessageDemo-BeaconManager
## Contents
 * [Introduction](#introduction)
 * [Enviroment requirement](#Enviroment requirement)
 * [Installation](#installation)
 * [Procedure](#procedure)
 * [Result](#result)
 * [License](#license)

## Introduction
This demo demonstrates an example of using the API to manage beacons on the cloud. For details about the API, please refer to https://developer.huawei.com/consumer/en/doc/development/HMS-References/api-call-process.

## Enviroment requirement
Android Studio 3.0 or a later version is recommended.

## Installation
1. Register as a developer.
Register a [HUAWEI ID](https://developer.huawei.com/consumer/en/).
2. Create an app.
Create an app and enable Nearby Service by referring to [Nearby Service Preparations](https://developer.huawei.com/consumer/en/doc/development/HMS-Guides/nearby-service-preparation).
3. Build the app.
To build the demo app, import the demo project in Android Studio 3.0 or later. Then set MESSAGE_HOST in the gradle file of the demo project (\app\build.gradle). For details about how to set MESSAGE_HOST, please refer to https://developer.huawei.com/consumer/en/doc/development/HMS-References/api-call-process.
Prepare a Huawei phone and at least one BLE beacon, and install the compiled APK using the ADB on the phone.

## Procedure
1. Create a service account key and download the JSON file.
Sign in to [HUAWEI Developer](https://developer.huawei.com/consumer/en/), click **Console**, go to **HMS API Services** > **Credentials**, select the app project you have created, move the cursor to **Create credentials**, and click **Service account key**. Enter the information of service account key, and click **Create and download JSON** to download the JSON file.

2. Push the JSON file to the SD card.
For example, use the ADB to push the JSON file to **/sdcard/Download**.

3. Prepare at least one BLE beacon.

4. Open the app on the phone, tap **My Center**, and sign in by selecting the JSON file.

5. Tap **Unregistered** and refresh the page to find the BLE beacons. Tap a BLE beacon to register it, and perform others operations such as configuring the beacon attachments.

6. Tap **Registered** and check the BLE beacons you have registered. Tap a BLE beacon to view more information about it, and perform others operations such as configuring the beacon attachments.

7. Now, enjoy this app.

## Result
<img src="https://github.com/HMS-Core/hms-nearby-demo/blob/master/BeaconManager/Result_1.jpg" width = 30% height = 30% /> <img src="https://github.com/HMS-Core/hms-nearby-demo/blob/master/BeaconManager/Result_2.jpg" width = 30% height = 30% /> <img src="https://github.com/HMS-Core/hms-nearby-demo/blob/master/BeaconManager/Result_3.jpg" width = 30% height = 30% />
<img src="https://github.com/HMS-Core/hms-nearby-demo/blob/master/BeaconManager/Result_4.jpg" width = 30% height = 30% /> <img src="https://github.com/HMS-Core/hms-nearby-demo/blob/master/BeaconManager/Result_5.jpg" width = 30% height = 30% />

## License
BeaconManager sample is licensed under the [Apache License, version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
