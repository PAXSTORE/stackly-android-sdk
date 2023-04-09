# Stackly集成指南

## 概述

Stackly 是一个实时异常报告器，可帮助开发人员跟踪、优先级、分析和修复稳定性问题以提高应用程序质量。

## 支持功能

- Java crash 上报
- Native crash 上报
- Anr上报
- 自定义Event 上送
- 自定义字段
- 上报策略配置
- 用户运营数据上送
- 应用使用次数及使用时长收集上送

创建应用程序时，请注意由MAXSTORE系统生成的AppKey和AppSecret。
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
implementation 'com.whatspos.sdk:stackly-android-sdk:1.7.0'
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
在MAXSTORE中创建一个新应用，并从开发人员中心的应用详细信息页面获取**AppKey** **和** **AppSecret 。**

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

初始化App Secret和Alias
>请注意，请确保已正确放置自己的应用程序的AppSecret, 并设置Alias， 该alias 属性为可识别设备的序列号、androidId或者其他唯一标识。
>
>Alias不能为Null 或者空字符串，否则Stackly不能够正常使用
>
>**Stackly 的初始化要放在onCreate 方法中，否则会出现空指针异常**

```JAVA
public class BaseApplication extends Application  {
    private static final String TAG = BaseApplication.class.getSimpleName();

    //todo make sure to replace with your own app's appSecret and alias
    private String APP_SECRET = "Your APPSECRET";
    private String ALIAS = Build.SERIAL;

      @Override
    public void onCreate() {
        super.onCreate();
          Stackly.I.install(this)
                .setSecret(APP_SECRET)//set your secret
            	//Alias cannot be null or empty
                .setAlias(ALIAS)//set your alias
              	.setFlags(ReportFlags.FLAG_ANR_WIFI_ONLY | ReportFlags.FLAG_JAVA_NOT_UPLOAD_TWO_HOURS)
                .init();
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
            Stackly.handleEvent(eventInfo);
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
            Stackly.handleEvent(eventInfoList);
        } catch (EventFailedException e) {
            e.printStackTrace();
        }
    }
    
    //设置了customStartAnalysisStrategy， 可以调用该方法，进行启动信息的上送
     private void handleStartInfo() {
        Stackly.handleStartInfo();
    }
}

```



## Apis

com.pax.vas.Stackly.reporter.Stackly

### 获取实例

```java
Stackly.I
```

### 设置Application 对象

```java
public Stackly install(Application application) {}
```

| 参数        | 类型        | 描述             |
| ----------- | ----------- | ---------------- |
| application | Application | Application 对象 |

### 设置Secret

```JAVA
public Stackly setSecret(String secret) {}
```

| 参数   | 类型   | 描述   |
| ------ | ------ | ------ |
| secret | String | Secret |

### 设置Alias

```JAVA
public Stackly setAlias(String alias) {}
```

| 参数  | 类型   | 描述                                                         |
| ----- | ------ | ------------------------------------------------------------ |
| alias | String | 设备的别名或者可识别设备的唯一标识符， 不能为null 或者empty（可设置android id 作为别名） |

### 设置ReportFlags

由于频繁上传崩溃会被限流，建议在本地设置上传策略。

Flags可以组合设置，预定义的flags见**ReportFlags**.

```java
public Stackly setFlags(int flags) {}
//Eg:
setFlags(ReportFlags.FLAG_ANR_WIFI_ONLY | ReportFlags.FLAG_JAVA_NOT_UPLOAD_TWO_HOURS)
```

### ReportFlags

| Parameter                        | Description                          |
| -------------------------------- | ------------------------------------ |
| FLAG_JAVA_DISABLE                | 不上送java异常                       |
| FLAG_JAVA_WIFI_ONLY              | 仅在wifi或以太网环境下上送java异常   |
| FLAG_JAVA_NOT_UPLOAD_TWO_HOURS   | 2H内重复发生的java异常不上送         |
| FLAG_NATIVE_DISABLE              | 不上送native异常                     |
| FLAG_NATIVE_WIFI_ONLY            | 仅在wifi或以太网环境下上送native异常 |
| FLAG_NATIVE_NOT_UPLOAD_TWO_HOURS | 2H内重复发生的native异常不上送       |
| FLAG_ANR_DISABLE                 | 不上送anr异常                        |
| FLAG_ANR_WIFI_ONLY               | 仅在wifi或以太网环境下上送anr异常    |
| FLAG_ANR_NOT_UPLOAD_TWO_HOURS    | 2H内重复发生的anr异常不上送          |

### 设置自定义的ReportFilter类

​	如果预定义的flags不能满足你的需求，你可以自定义ReportFilter

```java
 public Stackly setReportFilterClass(Class<? extends ReportFilter> reportFilterClass){}

//Eg: Realize not uploading within 1 hour after the last exception occurred
public class OneHourFilter extends ReportFilter {
    
    @Override
    public void doFilter(Context context, CrashReportData crashReportData) {
         boolean oneHour = System.currentTimeMillis() - lastTime <= ONE_HOURS;
        if(oneHour) {
            return;
        }
        next.doFilter(context, crashReportData);
    }
}


setReportFilterClass(OneHourFilter.class)
```

### 初始化  初始化操作，最后调用

```java
public void init()
```

### 设置formUri

```java
public Stackly setFormUri(String formUri) {}
```

| 参数    | 类型   | 描述      |
| ------- | ------ | --------- |
| formUri | String | 使用的uri |

### 设置是否跳过Init

```java
public Stackly bypassInit(boolean bypassInit) {}
```

| 参数       | 类型    | 描述                                                 |
| ---------- | ------- | ---------------------------------------------------- |
| bypassInit | Boolean | 设置为跳过init，这意味着崩溃将不会上传，通常用于调试 |

### 启用DevLog

```java
public Stackly enableDevLog(boolean enable) {}
```

| 参数   | 类型    | 描述         |
| ------ | ------- | ------------ |
| enable | Boolean | 是否打印日志 |

### 观察ANR

```java
public Stackly watchANR(boolean watchANR) {}
```

| 参数     | 类型    | 描述          |
| -------- | ------- | ------------- |
| watchANR | Boolean | 是否收集  Anr |

### 观察Native

```java
public Stackly watchNative(boolean watchNative) {}
```

| 参数        | 类型    | 描述                  |
| ----------- | ------- | --------------------- |
| watchNative | Boolean | 是否收集 native crash |

### 设置SpFileName

```java
public Stackly setSpFileName(String spFileName) {}
```

| 参数       | 类型   | 描述                 |
| ---------- | ------ | -------------------- |
| spFileName | String | 设置文件名以保存配置 |

### 放置自定义错误报告数据

```java
public Stackly putCustomErrorReportData(String key, String value) {}
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

| 参数      | 类型                | 描述                                                         |
| --------- | ------------------- | ------------------------------------------------------------ |
| eventId   | String              | 上送事件的id, 必须与平台申请的eventid一致                    |
| eventTime | long                | 事件发生的时间（ms）                                         |
| param     | Map<String, String> | 需要上送的参数键值对， param的数量最大为100， key最大长度为64，value最大长度为256 |

### 禁止启动运营数据分析

```java
 public Stackly disableStartAnalysis(boolean disable) {
        }
```

| 参数    | 类型    | 描述                                       |
| ------- | ------- | ------------------------------------------ |
| disable | boolean | 是否禁止分析应用启动等运营数据, 默认为上送 |

### 自定义启动分析策略

```java
 public Stackly customStartAnalysisStrategy(boolean customStrategy) {}
```

| 参数           | 类型    | 描述                                                    |
| -------------- | ------- | ------------------------------------------------------- |
| customStrategy | boolean | 是否自定义启动分析策略， 默认策略为判定应用打开即为活跃 |

### 上送应用启动信息

```JAVA
  public static void handleStartInfo() {}
```
  
tips：只有使用自定义启动分析策略， 才可以正常调用上送应用启动信息，反之则无效

### 上送应用使用次数及使用时长

tips：在BaseActivity(即基类Activity)中onResume 与 onPause 方法中调用以下代码

```java
  @Override
protected void onResume() {
        super.onResume();
        Stackly.I.onResume(this);
        }

@Override
protected void onPause() {
        super.onPause();
        Stackly.I.onPause(this);
        }
```



## 迁移至Android 8.0

由于Android 8.0进行了大量更改，这些更改将影响您应用的行为，因此我们建议您按照该指南进行迁移
到Android 8.0。 有关更多信息，请参阅https://developer.android.google.cn/about/versions/oreo/android-8.0-migration

## 迁移至Androidx

该项目使用了androidx， 如果与您应用产生了冲突，建议使用该指南进行迁移

请参阅https://developer.android.google.cn/jetpack/androidx/migrate


## FAQ

#### 1. 如何解决依赖冲突？

发生依赖关系冲突时，错误消息可能如下所示：

    Program type already present: xxx.xxx.xxx

#### 解决:

您可以使用**exclude**方法按组或模块或两者都排除冲突依赖项。

e.g. To exclude 'com.google.code.gson:gson:2.8.5' in SDK, you can use below:

    implementation ('com.whatspos.sdk:stackly-android-sdk:x.xx.xx'){
        exclude group: 'com.google.code.gson', module: 'gson'
    }

#### 2.如何解决属性冲突？

发生属性冲突时，错误消息可能如下所示：

    Manifest merger failed : Attribute application@allowBackup value=(false) from 
    AndroidManifest.xml...
    is also present at [stackly:x.xx.xx]
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
