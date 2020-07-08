# HMS Nearby Demo
[![](https://camo.githubusercontent.com/ce1c195eb2524e4e67a2e74bf6e9619555aa0913/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f446f63732d686d736775696465732d627269676874677265656e)](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides/introduction-0000001050040566) ![](https://github.com/HMS-Core/hms-ml-demo/workflows/Android%20CI/badge.svg)

[English](https://github.com/HMS-Core/hms-nearby-demo) | 中文

## Introduction
我们提供了示例代码，介绍如何使用Nearby Service的API。工程目录如下：
1. [BeaconManager](https://github.com/HMS-Core/hms-nearby-demo/tree/master/BeaconManager): 该包中包含一个Android Studio工程，提供了Beacon Management示例。
2. [NearbyCanteens](https://github.com/HMS-Core/hms-nearby-demo/tree/master/NearbyCanteens): 该包中包含一个Android Studio工程，提供了基于Beacon的消息发布和订阅的示例。
3. [NearbyCardExchange](https://github.com/HMS-Core/hms-nearby-demo/tree/master/NearbyCardExchange): 该包中包含了一个Android Studio工程，介绍如何使用Message API开发名片交换功能。
4. [NearbyConnection](https://github.com/HMS-Core/hms-nearby-demo/tree/master/NearbyConnection): 该包中包含一个Android Studio工程，提供了Connection示例。
5. [NearbyFriends](https://github.com/HMS-Core/hms-nearby-demo/tree/master/NearbyFriends): 该包中包含了一个Android Studio工程，提供了基于应用的消息发布和订阅的示例。
6. [NearbyGameSnake](https://github.com/HMS-Core/hms-nearby-demo/tree/master/NearbyGameSnake): 该包中包含一个Android Studio工程，介绍如何使用Connection API开发一个简单的在线贪吃蛇游戏。

## Precautions
工程中包含了多个独立的工程。下载代码后，您可以在Android Studio中打开一个工程来构建您的应用，或者将多个应用添加到一个工程中。您不需要为每个应用创建单独的项目，您可以通过打开setting.gradle文件来选择要构建的工程。
