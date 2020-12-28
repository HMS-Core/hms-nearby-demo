# NearbyFileTransfer
## Table of Contents

 * [Introduction](#introduction)
 * [Supported Environments](#supported-environments)
 * [Procedure](#procedure)
 * [Result](#result)
 * [License](#license)
 
## Introduction
The NearbyFileTransfer program demonstrates how to complete an Android app providing nearby file sharing function using NearbyAgent.

## Installation
1. Register as a developer.
Register a [HUAWEI account](https://developer.huawei.com/consumer/en/).
2. Create an app.
Create an app and enable Nearby Service by referring the [Nearby Service Preparations](https://developer.huawei.com/consumer/en/doc/development/HMS-Guides/nearby-service-preparation).
3. Build the demo.
To build this demo, please first import the demo to Android Studio (3.X or later). Then download the agconnect-services.json file of the app from AppGallery Connect, and add the file to the app directory (\app) of the demo. For details, please refer to [Nearby Service Preparations](https://developer.huawei.com/consumer/en/doc/development/HMS-Guides/nearby-service-preparation).
Prepare two Huawei phones, and install the compiled APK by running the adb command to these phones.

## Supported Environments
   Android Studio 3.X.

## Procedure
1. Open the app on both phones.
2. Tap "SEND FILE" and select the file to be sent.
3. Tap "RECEIVE FILE" on the other phone.

## Result
<img src="result.jpg">

## License
NearbyFileTransfer is licensed under the [Apache License, version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

##P.S
If error code 907135701 appears during running, please check the file "agconnect-services.json". You should use the corresponding "agconnect-services.json" of your project.
