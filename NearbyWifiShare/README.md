# HMS Core Nearby Service Sample Code (Nearby Wi-Fi Sharing)
English | [中文](README_ZH.md)
## Contents

 * [Introduction](#Introduction)
 * [Procedure](#Procedure)
 * [Running the Demo](#Running-the-Demo)
 * [Environment Requirements](#Environment-Requirements)
 * [Result](#Result)
 * [License](#License)

## Introduction
The sample code demonstrates how to build the nearby Wi-Fi sharing feature for an Android app.

## Procedure
1. Prepare for the development. For details, please refer to [Preparations](https://developer.huawei.com/consumer/en/doc/development/system-Guides/config-agc-0000001050040578?ha_source=hms1).
                                 <img src="process01.png">
                                 

(1) Register as a developer and complete identity verification on [HUAWEI Developers](https://developer.huawei.com/consumer/en/). For details, please refer to [HUAWEI ID Registration](https://developer.huawei.com/consumer/en/doc/start/registration-and-verification-0000001053628148).  

(2) Create an app by referring to [Creating a Project](https://developer.huawei.com/consumer/en/doc/distribution/app/agc-help-createproject-0000001100334664) and [Creating an App](https://developer.huawei.com/consumer/en/doc/distribution/app/agc-help-createapp-0000001146718717).

(3) Create a signing certificate and generate an SHA-256 fingerprint of the certificate.
<img src="process02.png">

(4) Configure the signing certificate fingerprint in AppGallery Connect as follows:

  (a) Sign in to AppGallery Connect and click **My projects**.

  (b) Find your project from the project list and click the desired app on the project card.

  (c) On the **Project settings** page, set **SHA-256 certificate fingerprint** to the certificate fingerprint you have generated.
<img src="process03.png">

2. Build the demo.

(1) Import the demo to Android Studio (3.0 or later).

(2) In the app-level **build.gradle** file, set **applicationid** to the app package name.

(3) Download the **agconnect-services.json** file from AppGallery Connect and add the file to the app-level directory of the demo project. For details, please refer to [Integrating the HMS Core SDK](https://developer.huawei.com/consumer/en/doc/development/system-Guides/android-integrating-sdk-0000001050126093?ha_source=hms1).

(4) Install the demo app on two Huawei phones by running adb commands.

## Running the Demo
1. Open the app on both phones (named A and B) and connect A to a Wi-Fi network.
2. Tap **Share Wi-Fi** on A to share the Wi-Fi to nearby devices.
3. Tap **Connect Wi-Fi** on B to connect to the Wi-Fi.
4. A found B and received a connection request. Tap **OK** on A to share the Wi-Fi to B.

## Environment Requirements
   Android Studio 3.0 or later

## Result
<img src="deviceA.jpg" width = 30% height = 30%>
<img src="deviceB.jpg" width = 30% height = 30%>

## License
The sample code is licensed under [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0).

