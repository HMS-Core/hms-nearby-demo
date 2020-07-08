# BeaconManager
[English](https://github.com/HMS-Core/hms-nearby-demo/tree/master/BeaconManager)|中文

[![](https://camo.githubusercontent.com/ce1c195eb2524e4e67a2e74bf6e9619555aa0913/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f446f63732d686d736775696465732d627269676874677265656e)](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides/introduction-0000001050040566)

## 目录
 * [简介](#简介)
 * [更多场景](#更多场景)
 * [操作流程](#操作流程)
 * [环境要求](#环境要求)
 * [授权许可](#授权许可)

## 简介
本Demo介绍如何使用API管理云上的信标。更多API信息，请参阅[API参考](https://developer.huawei.com/consumer/cn/doc/HMSCore-References/common-interface-0000001050151532).

<img src="https://github.com/HMS-Core/hms-nearby-demo/blob/master/BeaconManager/Result_1.jpg" width = 30% height = 30% /> <img src="https://github.com/HMS-Core/hms-nearby-demo/blob/master/BeaconManager/Result_2.jpg" width = 30% height = 30% /> <img src="https://github.com/HMS-Core/hms-nearby-demo/blob/master/BeaconManager/Result_3.jpg" width = 30% height = 30% />
<img src="https://github.com/HMS-Core/hms-nearby-demo/blob/master/BeaconManager/Result_4.jpg" width = 30% height = 30% /> <img src="https://github.com/HMS-Core/hms-nearby-demo/blob/master/BeaconManager/Result_5.jpg" width = 30% height = 30% />

## 更多场景


## 操作流程
* 安装
1. 成为开发者，注册[华为帐号](https://developer.huawei.com/consumer/en/)。

2. 创建应用。
参考[开发准备](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides/config-agc-0000001050040578)创建应用并开启Nearby Service。

3. 构建App。
在Android Studio 3.0及以上版本中导入Demo工程，然后在\app\build.gradle文件中设置MESSAGE_HOST参数。设置MESSAGE_HOST的方法请参见[api-call-process](https://developer.huawei.com/consumer/cn/doc/HMSCore-References/common-interface-0000001050151532)。准备一部华为手机和至少一个蓝牙信标设备，通过adb命令安装编译好的APK。

* 快速入门
1. 创建服务账号秘钥，并下载JSON文件。登录[开发者联盟](https://developer.huawei.com/consumer/cn/)，单击“管理中心”，选择“HMS API服务”>“凭证”，选择您创建的App工程。将鼠标移至“创建凭证”，单击“服务账号秘钥”。输入服务账号秘钥信息，然后单击“创建并下载JSON”下载JSON文件。

2. 推送JSON文件到SD卡。例如，使用ADB推送JSON文件到/sdcard/Download目录下。

3. 准备至少一个蓝牙信标设备。

4. 打开手机App，单击“My Center”，选择JSON文件进行登录。

5. 单击“未注册”，刷新页面，找到蓝牙信标。单击蓝牙信标进行注册，并进行信标附件配置等操作。

6. 单击“已注册”，查看已注册的蓝牙信标。单击某个蓝牙信标可查看更多信息，并进行信标附件配置等操作。

7. 接下来，就尽情使用App吧。

## 环境要求
推荐使用Android Studio 3.0及以上版本。

## 授权许可
BeaconManager示例代码经过 [Apache 2.0 授权许可](http://www.apache.org/licenses/LICENSE-2.0)。
