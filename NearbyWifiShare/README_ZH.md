# ������ͨ�ŷ���ʾ�����루������Wi-Fi����
���� | [English](README.md)
## Ŀ¼

 * [���](#���)
 * [��������](#��������)
 * [����](#����)
 * [����Ҫ��](#����Ҫ��)
 * [���](#���)
 * [��Ȩ���](#��Ȩ���)

## ���
��ʾ������չʾ���Ϊ��׿Ӧ�ù���������Wi-Fi�����ܡ�

## ��������
1������׼�������������[����׼��](https://developer.huawei.com/consumer/cn/doc/development/system-Guides/config-agc-0000001050040578?ha_source=hms1)��
                                 <img src="process01.png">
                                 
��1����[����������]( https://developer.huawei.com/consumer/cn/)ע���Ϊ�����߲���������֤�����������[ע����֤](https://developer.huawei.com/consumer/cn/doc/start/registration-and-verification-0000001053628148)��

��2������Ӧ�á����������[������Ŀ](https://developer.huawei.com/consumer/cn/doc/distribution/app/agc-help-createproject-0000001100334664)��[����Ӧ��](https://developer.huawei.com/consumer/cn/doc/distribution/app/agc-help-createapp-0000001146718717)��

��3������ǩ��֤�鲢����SHA-256֤��ָ�ơ�
<img src="process02.png">

��4����AppGallery Connect�ϰ����²�������֤��ָ�ƣ�

  ��a����¼AppGallery Connect��������ҵ���Ŀ����
  ��b������Ŀ�б����ҵ���Ҫ����Ŀ������Ŀ��Ƭ�ϵ��Ŀ��Ӧ�á�
  ��c���ڡ���Ŀ���á�ҳ�棬�ڡ�SHA256֤��ָ�ơ�������֮ǰ���ɵ�֤��ָ�ơ�

<img src="process03.png">

2������demo��

��1����ʾ�����뵼��Android Studio��3.0�����ϰ汾����

��2����Ӧ�ü�build.gradle�ļ��У���applicationid����ΪӦ�ð�����

��3����AppGallery Connect��ȡagconnect-services.json�ļ�����ӵ���Ŀ��Ӧ�ü���Ŀ¼�¡����������[����HMS Core SDK](https://developer.huawei.com/consumer/cn/doc/development/system-Guides/android-integrating-sdk-0000001050126093?ha_source=hms1)��

��4��ִ��adb���demo��װ����̨��Ϊ�ֻ���A��B���ϡ�

## ����
1����A��B������demo������A���ӵ�Wi-Fi���硣
2����A�ϵ����Share Wi-Fi������Wi-Fi����������豸��
3����B�ϵ����Connect Wi-Fi�����ӵ���Wi-Fi���硣
4��A����B���ܵ�һ������������A�ϵ����OK���Ѹ�Wi-Fi�����B��

## ����Ҫ��
   Android Studio 3.0�����ϰ汾

## ���
<img src="deviceA.jpg" width = 30% height = 30%>
<img src="deviceB.jpg" width = 30% height = 30%>

## ��Ȩ���
��ʾ�����뾭��[Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)��Ȩ��ɡ�

