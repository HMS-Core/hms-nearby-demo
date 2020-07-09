# NearbyCanteens
[English](https://github.com/HMS-Core/hms-nearby-demo/tree/master/NearbyCanteens)|中文

[![](https://camo.githubusercontent.com/ce1c195eb2524e4e67a2e74bf6e9619555aa0913/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f446f63732d686d736775696465732d627269676874677265656e)](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides/introduction-0000001050040566)

## 目录
 * [简介](#简介)
 * [更多场景](#更多场景)
 * [操作流程](#操作流程)
 * [环境要求](#环境要求)
 * [授权许可](#授权许可)

## 简介
本Demo介绍了Nearby Service中基于信标的近距离消息。通过该消息，商店、景区、机场等附近的设备可以订阅包含商品介绍、优惠信息、新闻、通知等信息的信标消息。本Demo演示的场景为：用户走进食堂，只要在食堂部署的信标附近，就可以收到更新的菜单、折扣通知等促销信息。

<img src="Result_1.jpg" width = 30% height = 30%> <img src="Result_2.jpg" width = 30% height = 30%>

## 更多场景
本Demo使用华为Nearby Service的蓝牙信标消息订阅功能。基于Nearby Beacon Message能力，您不仅可以开发广告推送功能，还可以实现以下功能：
1. 汽车或生活类应用可以集成识别用户是否靠近汽车的能力，从而判断是否开启无钥匙访问、记录汽车行驶轨迹。
2. 商务类应用可以集成该能力，精确记录员工上班打卡的位置。
3. 旅行或展览类应用可以集成该能力，当用户接近展品时，向用户介绍展品。
4. 游戏类应用可以集成游戏与现实世界的交互能力，例如通过物理对象解锁游戏等级、对参与线下活动的玩家进行奖励等。

## 操作流程
* 安装
1. 成为开发者，注册[华为帐号](https://developer.huawei.com/consumer/cn/)。

2. 创建应用。参考[开发准备](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides/config-agc-0000001050040578)创建应用并开启Nearby Service。

3. 构建App。请先在Android Studio（3.x或以上版本）中导入该Demo，然后在AGC下载应用的agconnect-services.json文件，并且将该文件添加到应用的根目录（\app）下。详细请参考开发指南中的[集成HMS Core SDK](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides/android-integrating-sdk-0000001050126093)章节。准备一部华为手机和至少一个蓝牙信标设备，执行adb命令将编译好的APK安装到手机。

* 快速入门
1. 准备至少一个蓝牙信标设备，然后参考[Codelab](https://developer.huawei.com/consumer/cn/codelab/HUAWEINearbyMessageKit/index.html)。以下介绍一些主要步骤。

2. 登录控制台看板，为项目创建服务帐户。
3. 通过BeaconManager的Demo应用注册信标设备。
4. 通过BeaconManager的Demo应用，为信标配置消息附件。
> 说明: 本Demo中的附件内容建议为JSON字符串。例如：对于namespaceType的食堂，附件内容引用{"canteenDesc":"这是食堂A的描述.","canteenName":"食堂A"}；对于namepaceType的通知，附件内容引用{"canteenName":"食堂A","notice":"华为员工在此就餐可享受8折优惠和免费水果拼盘。"}。

5. 运行NearbyCanteens前，请确保HMS Core已经安装。
6. 接下来，您就可以打开手机上的应用，体验有趣的消息了！


## 环境要求
推荐使用Android Studio 3.X及以上版本。

## 授权许可
NearbyCanteens示例代码经过[Apache2.0授权许可](http://www.apache.org/licenses/LICENSE-2.0)。
