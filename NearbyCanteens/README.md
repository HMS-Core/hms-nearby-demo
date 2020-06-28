# NearbyCanteens
English|[中文]()

[![](https://camo.githubusercontent.com/ce1c195eb2524e4e67a2e74bf6e9619555aa0913/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f446f63732d686d736775696465732d627269676874677265656e)](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/introduction-0000001050040566)

## Table of Contents
 * [Introduction](#introduction)
 * [More Scenarios](#more-scenarios)
 * [Procedure](#procedure)
 * [Supported Environment](#supported-environment)
 * [License](#license)

## Introduction
This demo demonstrates a use case for beacon-based proximity messages in Nearby Service:
Beacon-based proximity messages enable devices near places such as stores, scenic spots, and airports to subscribe to beacon messages containing information including merchandise introduction, preference information, news, and notifications. This demo shows a scenario as follow: A user walks into a canteen, once he/she is nearby the beacon deployed by the canteen, the user can receive the promotional infomation such as updated menu and discount notifications.

<img src="Result_1.jpg" width = 30% height = 30%> <img src="Result_2.jpg" width = 30% height = 30%>

## More Scenarios


## Procedure
1.Prepare at least one BLE beacon device, and then refer to the [Codelab](https://developer.huawei.com/consumer/en/codelab/HUAWEINearbyMessageKit/index.html). Some major steps are outlined as below.

2.Login in the Console dashboard, Creating a service account for your project.

3.Register the beacon device by using the demo app of BeaconManager. 

4.Configure message attachments for the beacon by using the demo app of BeaconManager. 
>Note: The attachment content in this demo is suggested to be a JSON string.
>For example, for namespaceType canteen, the attachment content references {"canteenDesc":"This is the description of Canteen A.","canteenName":"Canteen A"}; and for namepaceType notice, the attachment content references {"canteenName":"Canteen A","notice":"Huawei employees dinning here can get a 20% discount and a free fruit platter."}.

5.Before running the NearbyCanteens, please ensure the HMS Core is installed.

6.Open the app on the phone, enjoy the interesting messages!


## Supported Environment
Android Studio 3.X or a later version is recommended.

## License
 NearbyCanteens sample is licensed under the [Apache License, version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
