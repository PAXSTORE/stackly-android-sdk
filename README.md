# Stackly集成指南

## 概述

Stackly是PAX面向Android开发提供专业的 Crash 监控、崩溃分析等质量跟踪服务。Bugly 能帮助移动互联网开发者更及时地发现掌控异常，更全面的了解定位异常，更高效的修复解决异常 
Stackly 系统由后台服务器和Android SDK两部分组成

## 编写目的

本文档介绍Stackly SDK提供的模块的基本功能及配置方法，为开发人员提供编程参考。
Stackly SDK提供的函数接口，作用是为APP提供方法，用于上报crash、自定义异常，ANR以及上送自定义Event事件等操作。目的是让app开发者不必关心app异常的上报逻辑，只需要关心异常本身。

## 支持功能

- java crash 上报
- 自定义字段
- 支持上报策略配置
- 支持Anr上报
- 支持Native crash 上报
- 支持自定义Event 上送

 创建应用程序时，请注意由PAXSTORE系统生成的AppKey和AppSecret。
<br>请参阅以下步骤进行集成。

## 要求
**Android SDK 版本**

>SDK 19或更高版本，取决于终端的paydroid版本。

**Gradle's 和 Gradle plugin 的版本**

>Gradle 4.1或更高版本  
>Gradle插件版本3.0.0+或更高

## 下载
Gradle:

```groovy
implementation 'com.pax.vas.stacklytics:stacklytics-android-sdk:1.3.0'
```


## 权限
Stackly 需要以下权限,  请把他们添加到 AndroidManifest.xml.

```JAVA
<uses-permission android:name="android.permission.INTERNET" />
<permission android:name="android.permission.READ_LOGS" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

## API  说明

### 第一步:获取AppKey 和 AppSecret
在PAXSTORE中创建一个新应用，并从开发人员中心的应用详细信息页面获取**AppKey** **和** **AppSecret 。**

### 第二步:初始化
配置应用程序元素，编辑AndroidManifest.xml，它将拥有一个应用程序元素。 您需要配置android：name属性以指向您的应用程序类（如果应用程序类的包与清单的声明根元素声明的包不同，则使用包的全名）

配置Stackly Access Key，key为在平台申请的Access Key

```JAVA
<application
    android:name=".BaseApplication"
    android:allowBackup="true"
    android:icon="@mipmap/ic_launcher"
    android:label="@string/app_name"
    android:theme="@style/AppTheme">
        
   <meta-data android:name="Stackly_Access_Key" android:value="xxx"/>
```

初始化AppSecret和ALIAS
>请注意，请确保已正确放置自己的应用程序的AppSecret, 并设置Alias， 该alias 属性为可识别设备的序列号、androidId或者其他唯一标识。

```JAVA
public class BaseApplication extends Application implements PackReportData {
    private static final String TAG = BaseApplication.class.getSimpleName();

    //todo make sure to replace with your own app's appSecret and alias
    private String APP_SECRET = "Your APPSECRET";
    private String ALIAS = Build.SERIAL;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Stacklytics.I.install(this)
                .setReportSenderListener(new ReportSenderListener<CrashReportData>() {
                    @Override
                    public void onSendStart() {
                        Log.d(TAG, "onSendStart");
                    }

                    @Override
                    public boolean bypass(CrashReportData crashReportData) {
                        Log.d(TAG, "bypass");
                        return false;
                    }


                    @Override
                    public void onSendCompleted() {
                        Log.d(TAG, "onSendCompleted");
                    }

                    @Override
                    public void onSendError(Throwable throwable) {
                        Log.d(TAG, "onSendError");
                    }
                })
                .setSecret(APP_SECRET)//set your secret
                .setAlias(ALIAS)//set your alias
                .init();
    }
    
    //获取异常报告数据，可以对其进行自定义处理、返回json字符串
    @Override
    public String getReportContent(CrashReportData crashReportData) {
        return "{\"REPORT_ID\":\"1234\"}";
    }
}
```
### 第三步:使用案例

```java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        handleSingleEvent();
        handleMultipleEvent();
    }

    //上送单个事件
    private void handleSingleEvent() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("key1", "value1");
        EventInfo eventInfo = EventInfo.newBuilder().setEventId("eventId1").setEventTime(System.currentTimeMillis()).setParam(paramMap).build();
        try {
            Stacklytics.handleEvent(eventInfo);
        } catch (EventFailedException e) {
            e.printStackTrace();
        }
    }

    //上送多个事件
    private void handleMultipleEvent() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("key1", "value1");
        EventInfo eventInfo = EventInfo.newBuilder().setEventId("eventId1").setEventTime(System.currentTimeMillis()).setParam(paramMap).build();

        Map<String, String> secondParamMap = new HashMap<>();
        secondParamMap.put("key2", "value2");
        EventInfo secondEventInfo = EventInfo.newBuilder().setEventId("eventId2").setEventTime(System.currentTimeMillis()).setParam(secondParamMap).build();

        List<EventInfo> eventInfoList = new ArrayList<>();
        eventInfoList.add(eventInfo);
        eventInfoList.add(secondEventInfo);

        try {
            Stacklytics.handleEvent(eventInfoList);
        } catch (EventFailedException e) {
            e.printStackTrace();
        }
    }
}

```



## Apis

com.pax.vas.stacklytics.reporter.Stacklytics

### 获取实例

```java
Stacklytics.I
```

### 设置 Stacklytics

```java
public Stacklytics install(Application application) {}
```

| 参数        | 类型        | 描述             |
| ----------- | ----------- | ---------------- |
| application | Application | Application 对象 |

### 设置Secret

```JAVA
public Stacklytics setSecret(String secret) {}
```

| 参数   | 类型   | 描述   |
| ------ | ------ | ------ |
| secret | String | Secret |

### 设置Alias

```JAVA
public Stacklytics setAlias(String alias) {}
```

| 参数  | 类型   | 描述                                 |
| ----- | ------ | ------------------------------------ |
| alias | String | 设备的别名或者可识别设备的唯一标识符 |

### 初始化  初始化操作，最后调用

```
public void init()
```

### 设置AnrCount

```java
 public void setAnrCount(int anrCount) {}
```

| 参数     | 类型 | 描述                     |
| -------- | ---- | ------------------------ |
| anrCount | Int  | 发生错误时收集的日志行数 |

### 设置formUri

REQUEST_URI :   /pdc/stacklytics/api/app_client/crash_report

```java
public Stacklytics setFormUri(String formUri) {}
```

| 参数    | 类型   | 描述                                                     |
| ------- | ------ | -------------------------------------------------------- |
| formUri | String | 自定义的日志收集的uri将与REQUEST_URI合并以形成完整的地址 |

### 设置是否跳过Init

```java
public Stacklytics bypassInit(boolean bypassInit) {}
```

| 参数       | 类型    | 描述                                                 |
| ---------- | ------- | ---------------------------------------------------- |
| bypassInit | Boolean | 设置为跳过init，这意味着崩溃将不会上传，通常用于调试 |

### 启用DevLog

```java
public Stacklytics enableDevLog(boolean enable) {}
```

| 参数   | 类型    | 描述         |
| ------ | ------- | ------------ |
| enable | Boolean | 是否打印日志 |

### 观察ANR

```java
public Stacklytics watchANR(boolean watchANR) {}
```

| 参数     | 类型    | 描述          |
| -------- | ------- | ------------- |
| watchANR | Boolean | 是否收集  Anr |

### 观察Native

```java
public Stacklytics watchNative(boolean watchNative) {}
```

| 参数        | 类型    | 描述                  |
| ----------- | ------- | --------------------- |
| watchNative | Boolean | 是否收集 native crash |

### 设置SpFileName

```java
public Stacklytics setSpFileName(String spFileName) {}
```

| 参数       | 类型   | 描述                 |
| ---------- | ------ | -------------------- |
| spFileName | String | 设置文件名以保存配置 |

### 放置自定义错误报告数据

```java
public Stacklytics putCustomErrorReportData(String key, String value) {}
```

| 参数  | 类型   | 描述       |
| ----- | ------ | ---------- |
| key   | String | Data key   |
| value | String | Data value |

### 包含自定义数据密钥

```java
public boolean containsCustomDataKey(String key) {}
```

| 参数 | 类型   | 描述               |
| ---- | ------ | ------------------ |
| key  | String | 自定义数据存在此键 |

### 设置自定义包装数据

```java
public Stacklytics setCustomPackData(PackReportData customPackData) {}
```

| 参数           | 类型           | 描述                                     |
| -------------- | -------------- | ---------------------------------------- |
| customPackData | PackReportData | 自定义组合数据，实现PackReportData的实例 |

### PackReportData

```java
public interface PackReportData {
    public String getReportContent(CrashReportData report);
}
```

| 参数   | 类型            | 描述                                |
| ------ | --------------- | ----------------------------------- |
| report | CrashReportData | 根据CrashReportData组合成一个字符串 |

### 设置报告监听

```java
public Stacklytics setReportSenderListener(ReportSenderListener<CrashReportData> senderListener) {}
```

| 参数           | 类型                                                 | 描述                 |
| -------------- | ---------------------------------------------------- | -------------------- |
| senderListener | ReportSenderListener<CrashReportData> senderListener | 崩溃报告数据发送监听 |

```java
setReportSenderListener(new ReportSenderListener<CrashReportData>() {
    @Override
    public void onSendStart() {
        Log.d(TAG, "onSendStart");
    }

    @Override
    public boolean bypass(CrashReportData crashReportData) {
        Log.d(TAG, "bypass");
        return false;
    }


    @Override
    public void onSendCompleted() {
        Log.d(TAG, "onSendCompleted");

    }

    @Override
    public void onSendError(Throwable throwable) {
        Log.d(TAG, "onSendError");
    }
})
```

### 上送单个自定义事件

```java
public static void handleEvent(EventInfo eventInfo) throws EventFailedException {
}
```

| 参数      | 类型      | 描述          |
| --------- | --------- | ------------- |
| eventInfo | EventInfo | EventInfo事件 |

### 上送多个自定义事件

```java
public static void handleEvent(List<EventInfo> eventInfoList) throws EventFailedException {
}
```

| 参数          | 类型            | 描述              |
| ------------- | --------------- | ----------------- |
| eventInfoList | List<EventInfo> | EventInfo事件列表 |

### EventInfo

```java
new EventInfo.Builder().setEventId(eventid).setEventTime(eventtime).setParam(param).build()
```

| 参数      | 类型                | 描述                                      |
| --------- | ------------------- | ----------------------------------------- |
| eventId   | String              | 上送事件的id, 必须与平台申请的eventid一致 |
| eventTime | long                | 事件发生的时间（ms）                      |
| param     | Map<String, String> | 需要上送的参数键值对                      |

## 迁移至Android 8.0

由于Android 8.0进行了大量更改，这些更改将影响您应用的行为，因此我们建议您按照该指南进行迁移
到Android 8.0。 有关更多信息，请参阅https://developer.android.google.cn/about/versions/oreo/android-8.0-migration


## FAQ

#### 1. 如何解决依赖冲突？

发生依赖关系冲突时，错误消息可能如下所示：

    Program type already present: xxx.xxx.xxx

#### 解决:

您可以使用**exclude**方法按组或模块或两者都排除冲突依赖项。

e.g. To exclude 'com.google.code.gson:gson:2.8.5' in SDK, you can use below:

    implementation ('com.pax.vas.stacklytics:stacklytics-android-sdk:x.xx.xx'){
        exclude group: 'com.google.code.gson', module: 'gson'
    }

#### 2.如何解决属性冲突？

发生属性冲突时，错误消息可能如下所示：

    Manifest merger failed : Attribute application@allowBackup value=(false) from 
    AndroidManifest.xml...
    is also present at [stacklytics:x.xx.xx] 
    AndroidManifest.xml...
    Suggestion: add 'tools:replace="android:allowBackup"' to <application> element
    at AndroidManifest.xml:..

#### 解决:

在清单标题中添加**xmlns：tools =“ http：// schemas.android.com/tools“**

       <manifest xmlns:android="http://schemas.android.com/apk/res/android"
            package="com.yourpackage"
            xmlns:tools="http://schemas.android.com/tools">

在您的应用程序标签中添加 **tools：replace =“冲突属性”** ：

        <application
            ...
            tools:replace="allowBackup"/>

## License

See the [Apache 2.0 license](https://github.com/PAXSTORE/paxstore-3rd-app-android-sdk/blob/master/LICENSE) file for details.

    Copyright 2018 PAX Computer Technology(Shenzhen) CO., LTD ("PAX")
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at following link.
    
         http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
