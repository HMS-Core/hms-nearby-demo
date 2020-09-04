# SimpleNearbyDemo
English | [中文]()

[![](https://camo.githubusercontent.com/ce1c195eb2524e4e67a2e74bf6e9619555aa0913/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f446f63732d686d736775696465732d627269676874677265656e)](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/introduction-0000001050040566)

## Table of Contents

 * [Introduction](#introduction)
 * [More Scenarios](#more-scenarios)
 * [Getting Started](#Getting-Started)
 * [Procedure](#procedure)
 * [Supported Environment](#supported-environment)
 * [License](#license)
 
## Introduction
The SimpleNearbyDemo program demonstrates how to complete an Android app providing nearby file sharing function using NearbyAgent.

<img src="result.jpg" width = 30% height = 30%> 

## More Scenarios

## Procedure
* Installation

1. Register as a developer.
Register a [HUAWEI account](https://developer.huawei.com/consumer/en/).
2. Create an app.
Create an app and enable Nearby Service by referring the [Nearby Service Preparations](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/config-agc-0000001050040578).
3. Build the demo.
To build this demo, please first import the demo to Android Studio (3.X or later). Then download the agconnect-services.json file of the app from AppGallery Connect, and add the file to the app directory (\app) of the demo. For details, please refer to [Nearby Service Preparations](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/config-agc-0000001050040578).

* Getting Started

1. Open the app on both phones.
2. Tap "SEND FILE" and select the file to be sent.
3. Tap "RECEIVE FILE" on the other phone.

P.S: If error code 907135701 appears during running, please check the file "agconnect-services.json". You should use the corresponding "agconnect-services.json" of your project.

## Supported Environments
   Android Studio 3.X.

## License
Nearby Service Connection sample is licensed under the [Apache License, version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
