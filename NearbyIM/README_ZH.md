# ��Ϊ������ͨ�ŷ���ʾ�����루NearbyIM��
���� | [English](README.md)

## Ŀ¼

 * [���](#���)
 * [��������](#��������)
 * [����](#����)
 * [����Ҫ��](#����Ҫ��)
 * [���](#���)
 * [��Ȩ���](#��Ȩ���)

## ���
����������ͨ�ŷ������Nearby Connection��[Nearby Message](https://developer.huawei.com/consumer/cn/doc/development/system-Guides/message-preparations-0000001050042561)��
��ʾ������չʾ���ʹ�������ַ������е���Ӧ�ó���������
�������罻�����ڽ�����ͨ�ż������ٶ�λ�������˼�����Χ�˷�����Ϣ�������Ƭ����Ƶ�ļ��ȡ�

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

<img src="process.png">

��4������adb���demo��װ����̨��Ϊ�ֻ��ϡ�

## ����
1������̨��Ϊ�ֻ�������demo�����ͬһ���ܡ�
2�������People nearby��Ѱ�Ҹ������ˡ�
3�������Join private group�����븽��Ⱥ�ģ�����ǰ��������λ���롣
4�������Group chat with nearby people�����븽������Ⱥ�ġ�
5�������Private chat��ͨ��[Nearby Connection](https://developer.huawei.com/consumer/cn/doc/development/system-Guides/connection-preparations-0000001050040586)������Ϣ���ļ���
�븽�����˻�������ɣ�

## ����Ҫ��
����ʹ��Android Studio 3.0�����ϰ汾��

## ���
<img src="result01.jpg" width = 30% height = 30%>
<img src="result02.jpg" width = 30% height = 30%>
<img src="result03.jpg" width = 30% height = 30%>
<img src="result04.jpg" width = 30% height = 30%>
<img src="result05.jpg" width = 30% height = 30%>

## ��Ȩ���
��ʾ�����뾭��[Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)��Ȩ��ɡ�
