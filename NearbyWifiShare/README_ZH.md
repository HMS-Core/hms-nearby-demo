# 近距离通信服务示例代码（近距离Wi-Fi分享）
中文 | [English](README.md)
## 目录

 * [简介](#简介)
 * [开发过程](#开发过程)
 * [运行](#运行)
 * [环境要求](#环境要求)
 * [结果](#结果)
 * [授权许可](#授权许可)

## 简介
本示例代码展示如何为安卓应用构建近距离Wi-Fi分享功能。

## 开发过程
1、开发准备。详情请参阅[开发准备](https://developer.huawei.com/consumer/cn/doc/development/system-Guides/config-agc-0000001050040578?ha_source=hms1)。
                                 <img src="process01.png">
                                 
（1）在[开发者联盟]( https://developer.huawei.com/consumer/cn/)注册成为开发者并完成身份认证。详情请参阅[注册认证](https://developer.huawei.com/consumer/cn/doc/start/registration-and-verification-0000001053628148)。

（2）创建应用。详情请参阅[创建项目](https://developer.huawei.com/consumer/cn/doc/distribution/app/agc-help-createproject-0000001100334664)和[创建应用](https://developer.huawei.com/consumer/cn/doc/distribution/app/agc-help-createapp-0000001146718717)。

（3）创建签名证书并生成SHA-256证书指纹。
<img src="process02.png">

（4）在AppGallery Connect上按如下步骤配置证书指纹：

  （a）登录AppGallery Connect。点击“我的项目”。
  （b）从项目列表中找到需要的项目。在项目卡片上点击目标应用。
  （c）在“项目设置”页面，在“SHA256证书指纹”处填入之前生成的证书指纹。

<img src="process03.png">

2、构建demo。

（1）将示例代码导入Android Studio（3.0及以上版本）。

（2）在应用级build.gradle文件中，将applicationid设置为应用包名。

（3）从AppGallery Connect获取agconnect-services.json文件并添加到项目的应用级根目录下。详情请参阅[集成HMS Core SDK](https://developer.huawei.com/consumer/cn/doc/development/system-Guides/android-integrating-sdk-0000001050126093?ha_source=hms1)。

（4）执行adb命令将demo安装到两台华为手机（A、B）上。

## 运行
1、在A、B上运行demo，并将A连接到Wi-Fi网络。
2、在A上点击“Share Wi-Fi”，将Wi-Fi分享给附近设备。
3、在B上点击“Connect Wi-Fi”连接到该Wi-Fi网络。
4、A发现B并受到一条连接请求。在A上点击“OK”把该Wi-Fi分享给B。

## 环境要求
   Android Studio 3.0及以上版本

## 结果
<img src="deviceA.jpg" width = 30% height = 30%>
<img src="deviceB.jpg" width = 30% height = 30%>

## 授权许可
本示例代码经过[Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)授权许可。

