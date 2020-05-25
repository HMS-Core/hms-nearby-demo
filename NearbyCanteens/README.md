# NearbyMessageDemo-NearbyCanteens
## Table of Contents
 * [Introduction](#introduction)
 * [Installation](#installation)
 * [Supported Environments](#supported-environments)
 * [Procedure](#procedure)
 * [Result](#result)
 * [License](#license)

## Introduction
This demo demonstrates a use case for beacon-based proximity messages in Nearby Service:
Beacon-based proximity messages enable devices near places such as stores, scenic spots, and airports to subscribe to beacon messages containing information including merchandise introduction, preference information, news, and notifications. This demo shows a scenario as follow: A user walks into a canteen, once he/she is nearby the beacon deployed by the canteen, the user can receive the promotional infomation such as updated menu and discount notifications.

## Installation
1. Register as a developer.
Register a [HUAWEI account](https://developer.huawei.com/consumer/en/).
2. Create an app.
Create an app and enable Nearby Service by referring the [Nearby Service Preparations](https://developer.huawei.com/consumer/en/doc/development/HMS-Guides/nearby-service-preparation).
3. Build
To build this demo, please first import the demo in the Android Studio (3.x+). Then download the file "agconnect-services.json" of the app on AGC, and add the file to the app root directory(\app) of the demo. Please refer to the Chapter [Integrating HMS SDK](https://developer.huawei.com/consumer/en/doc/development/HMS-Guides/nearby-service-integratesdk) of the Development Guide.
Prepare a Huawei phone and at least one BLE beacon device, then install the compiled apk by adb command to phone.

## Supported Environments
Android Studio 3.X or a later version is recommended.

## Procedure
1.Prepare at least one BLE beacon device, and then refer to the [Codelab](https://developer.huawei.com/consumer/en/codelab/HUAWEINearbyMessageKit/index.html). Some major steps are outlined as below.

2.Login in the Console dashboard, Creating a service account for your project.

3.Register the beacon device by using the demo app of BeaconManager. 

4.Configure message attachments for the beacon by using the demo app of BeaconManager.
Note: The attachment content in this demo is suggested to be a JSON string.
For example, for namespaceType canteen, the attachment content references {"canteenDesc":"This is the description of Canteen A.","canteenName":"Canteen A"}; and for namepaceType notice, the attachment content references {"canteenName":"Canteen A","notice":"Huawei employees dinning here can get a 20% discount and a free fruit platter."}.

5.Before running the NearbyCanteens, please ensure the HMS Core is installed.

6.Open the app on the phone, enjoy the interesting messages!


## Result
<img src="Result_1.jpg" width = 30% height = 30%> <img src="Result_2.jpg" width = 30% height = 30%>

## License
 NearbyCanteens sample is licensed under the [Apache License, version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
