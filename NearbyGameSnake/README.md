# NearbyGameSnake

## Table of Contents

 * [Introduction](#introduction)
 * [Getting Started](#Getting-Started)
 * [Supported Environments](#supported-environments)
 * [Procedure](#procedure)
 * [Result](#result)
 * [License](#license)
 
## Introduction
The NearbyGameSnake program demonstrates a simple online snake game with Nearby Connection. Through Nearby Connection, it is easy for two smart phones to establish connection, and then play the online game together.

## Getting Started

1. Register as a developer.
Register a [HUAWEI account](https://developer.huawei.com/consumer/en/).
2. Create an app.
Create an app and enable Nearby Service by referring the [Nearby Service Preparations](https://developer.huawei.com/consumer/en/doc/development/HMS-Guides/nearby-service-preparation).
3. Build the demo.
(1)To build this demo, please first import the demo in the Android Studio (3.x+). 
(2)Then download the file "agconnect-services.json" of the app on AGC, and add the file to the app root directory(\app) of the demo. Please refer to the Chapter [Integrating HMS SDK](https://developer.huawei.com/consumer/en/doc/development/HMS-Guides/nearby-service-integratesdk) of the Development Guide.
(3)Change the value of applicationid in the app-level build.gradle file of the sample project to the package name of your app.
(4)Prepare two Huawei phones, and install this app by adb command to phones.

## Supported Environments
Android Studio 3.X or a later version is recommended.

## Procedure

1. open the app on both phones, one side clicks "Create Game", and another side clicks "Join Game" to start discovering each other.
2. After discovery is successful, the host clicks "Go" to start the game.

## Result
Game Lobby:
<img src="result_1.jpg">
In the game:
<img src="result_2.jpg">

## License
Nearby Service Connection sample is licensed under the [Apache License, version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

