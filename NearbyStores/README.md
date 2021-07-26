# NearbyStores
English | [中文](README_ZH.md)

[![](https://camo.githubusercontent.com/ce1c195eb2524e4e67a2e74bf6e9619555aa0913/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f446f63732d686d736775696465732d627269676874677265656e)](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/introduction-0000001050040566)

## Contents
 * [Overview](#Overview)
 * [Procedure](#Procedure)
 * [Environment Requirements](#environment-requirement)
 * [License](#License)

## Overview
This demo shows how beacon-based message publishing and subscription work in Nearby Service.
1. It includes demos for both the consumer and merchant, which can be switched as needed after the app is started.
2. Consumers can receive marketing messages from beacons.
3. Merchants can configure nearby beacon attachments, or use the Bluetooth function to simulate a mobile phone as a beacon to publish messages.

For more information about the APIs, please refer to [API Reference](https://developer.huawei.com/consumer/en/doc/development/system-References/overview2-0000001061766323?ha_source=hms1).

## Procedure
* Quick Tutorial
1. Start the app on phone A, and tap Merchants. The merchant screen will display.
<img src=images/Result_1.jpg width = 30% height = 30%> 

2. Tap Set to configure a beacon attachment.
<img src=images/Result_3-english.png width = 30% height = 30%>

3. Enable Local Soft Beacon. A message will display in the notification panel, indicating that the soft beacon is enabled and that the phone is now a beacon.
<img src=images/Result_2.jpg width = 30% height = 30% > 

4. Tap Bluetooth beacon in Unregistered Beacon nearby to register a beacon and configure the beacon attachment.

5. Tap the registered Bluetooth beacon in Registered Beacon in Your Project. You can tap a Bluetooth beacon to view more information about it and perform operations such as beacon attachment configuration.

6. Start the app on phone B, and tap Consumer. You will be able to receive messages published by phone A.
<img src=images/Result_4-english.png width = 30% height = 30%> 

>Note: In this demo, the corresponding account authentication file has been set in the code to facilitate user operations. To update your account authentication file, perform the following steps:
>1. Register as a developer. Register a [HUAWEI ID](https://developer.huawei.com/consumer/en/).
>2. Create an app. Create an app and enable Nearby Service by referring to [Preparations](https://developer.huawei.com/consumer/en/doc/development/system-Guides/config-agc-0000001050040578?ha_source=hms1).
>3. Create a service account key and download the JSON file. Sign in to HUAWEI Developers, go to Console > HMS API Services > Credentials, select your project, move the pointer to Create credentials, and click Service account key. Enter the information about the service account key, and click Create and download JSON to download the JSON file.
>4. Replace the JSON file in the code. The path is NearbyStores\app\src\main\assets.
>5. Use the signing certificate to compile the executable APK. For details , please refer to [Generating a Signing Certificate]( https://developer.huawei.com/consumer/en/codelab/HMSPreparation/index.html#2).

## Environment Requirement
Android Studio 3.0 or later is recommended.

## License
The sample code is licensed under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0).
