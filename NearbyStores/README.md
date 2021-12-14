# HMS Core Nearby Service Sample Code (Nearby Stores)
English | [中文](README_ZH.md)

[![](https://camo.githubusercontent.com/ce1c195eb2524e4e67a2e74bf6e9619555aa0913/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f446f63732d686d736775696465732d627269676874677265656e)](https://developer.huawei.com/consumer/en/doc/development/system-Guides/introduction-nearby-0000001060363166)

## Contents
 * [Introduction](#Introduction)
 * [Procedure](#Procedure)
 * [Environment Requirements](#Environment-Requirements)
 * [License](#License)

## Introduction
This demo shows how to use the beacon-based message publishing and subscription capabilities of Nearby Service.
1. Sample code for both the user side and the merchant side is provided. After starting the demo app, you can switch between the user side and the merchant side.
2. On the user side, you can receive marketing messages published by nearby beacons.
3. On the merchant side, you can publish messages by configuring message attachments for a nearby beacon or using the phone as a Bluetooth beacon.

For details, please refer to [API Reference](https://developer.huawei.com/consumer/en/doc/development/system-References/overview2-0000001061766323?ha_source=hms1).

## Procedure
* Quick Start
1. Start the demo app on phone A and tap **Merchants**.
<img src=images/Result_1.jpg width = 30% height = 30%> 

2. Tap **Set** to configure message attachments for your phone.
<img src=images/Result_3-chinese.jpg width = 30% height = 30%>

3. Enable **Local Soft Beacon**. You will receive a notification indicating that soft beacon is enabled and your phone now functions as a beacon.
<img src=images/Result_2.jpg width = 30% height = 30% > 

4. Tap a Bluetooth beacon listed under **Unregistered Beacon nearby** to register the beacon and configure message attachments for the beacon.

5. Tap a Bluetooth beacon listed under **Registered Beacon in Your Project** to view the beacon details and configure message attachments for it.

6. Start the demo app on phone B and tap **Consumer**. You will be able to receive messages published by nearby beacons.
<img src=images/Result_4-chinese.jpg width = 30% height = 30% /> 

>Note: A service account authentication file is preset in the demo. To update it, perform the following operations:
>1. Register as a developer on [HUAWEI Developers](https://developer.huawei.com/consumer/en/).
>2. Create an app and enable Nearby Service by referring to [Preparations](https://developer.huawei.com/consumer/en/doc/development/system-Guides/config-agc-0000001050040578?ha_source=hms1).
>3. Create a service account key and download the JSON file. Sign in to HUAWEI Developers and go to **Console** > **HMS API Services** > **Credentials**. Select an existing project. Move your cursor to **Create credential** and click **Service account key**. Complete the required information and click **Create and download JSON** to download the JSON file.
>4. Replace the JSON file in the demo in **NearbyStores\app\src\main\assets** with the JSON file you have downloaded.
>5. Generate a signing certificate for the APK. For details, please refer to [Generating a Signing Certificate](https://developer.huawei.com/consumer/en/codelab/HMSPreparation/index.html#2).

## Environment Requirements
Android Studio 3.0 or later is recommended.

## License
The sample code is licensed under [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0).

