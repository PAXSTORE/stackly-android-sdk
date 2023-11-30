#  Stackly

## OverView

Stackly is a real-time exception reporter that helps developers track, prioritize, analyze, and fix stability issues to improve the App quality. 

## Features

- Java crash report
- Native crash report
- ANR report
- Upload custom event
- Customer fileds
- Report policy configuration
- User operation data upload
- Upload duration and frequency of use

 Please take care of your AppKey and AppSecret that generated by MAXSTORE system when you create an app.
<br>Refer to the following steps for integration.

## Requirements
**Android SDK version**

>SDK 19 or higher, depending on the terminal's paydroid version.

**Gradle's and Gradle plugin's version**
>Gradle version 4.1 or higher  
>Gradle plugin version 3.0.0+ or higher

## Download
Gradle:

```groovy
implementation 'com.whatspos.sdk:stackly-android-sdk:1.8.1'
```


## Permissions
Stackly need the following permissions, please add them in AndroidManifest.xml.

```JAVA
<uses-permission android:name="android.permission.INTERNET" />
<permission android:name="android.permission.READ_LOGS" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

## API Usage

### Step 1: Get Application Key and Secret
Create a new app in MAXSTORE, and get **AppKey** and **AppSecret** from app detail page in developer center.

### Step 2: Initialization
Configuring the application element, edit AndroidManifest.xml, it will have an application element. You need to configure the android:name attribute to point to your Application class (put the full name with package if the application class package is not the same as manifest root element declared package)

Configure Stackly Access Key, the key is the Access Key applied for on the maxstore.


```JAVA
<application
    android:name=".BaseApplication"
    android:allowBackup="true"
    android:icon="@mipmap/ic_launcher"
    android:label="@string/app_name"
    android:theme="@style/AppTheme">
        
   <meta-data android:name="Stackly_Access_Key" android:value="xxx"/>
```

Initializing AppSecret and Alias
>Please note, make sure that you have correctly placed the alias and AppSecret of your own application and replaced it with your sn or this other device identifier
>
>Alias cannot be Null or an empty string, otherwise Stackly cannot be used normally
>
>**The initialization of Stackly should be placed in the onCreate method, otherwise a null pointer exception will occur**

```JAVA
public class BaseApplication extends Application {
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
### Step 3: Use case

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

    //Send a single event
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

    //Send multiple events
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
    
    //If customStartAnalysisStrategy is set, you can call this method to upload startup information
     private void handleStartInfo() {
        Stackly.handleStartInfo();
    }
}

```

## Apis

com.pax.vas.stackly.Stackly

### Get   instance

```java
Stackly.I
```

### Set application

```java
public Stackly install(Application application) {}
```

| Parameter   | Type        | Description |
| ----------- | ----------- | ----------- |
| application | Application | Application |

### Set  secret

```JAVA
public Stackly setSecret(String secret) {}
```

| Parameter | Type   | Description |
| --------- | ------ | ----------- |
| secret    | String | Secret      |

### Set   alias

```java
public Stackly setAlias(String alias) {}
```

| Parameter | Type   | Description             |
| --------- | ------ | ----------------------- |
| serialNo  | String | The alias of the device |

### Set report flags

Since uploading crashes frequently will be limited, it is recommended to set the uploading strategy locally.

Flags can be set in combination, See ReportFlags for predefined flags, 

```java
public Stackly setFlags(int flags) {}
//Eg:
setFlags(ReportFlags.FLAG_ANR_WIFI_ONLY | ReportFlags.FLAG_JAVA_NOT_UPLOAD_TWO_HOURS)
```

### ReportFlags 

| Parameter                        | Description                                                  |
| -------------------------------- | ------------------------------------------------------------ |
| FLAG_JAVA_DISABLE                | Do not send java crash reports                               |
| FLAG_JAVA_WIFI_ONLY              | Only send java crash reports under wifi or ethernet          |
| FLAG_JAVA_NOT_UPLOAD_TWO_HOURS   | Java crash reports that occur repeatedly within two hours are not sent |
| FLAG_NATIVE_DISABLE              | Do not send native crash reports                             |
| FLAG_NATIVE_WIFI_ONLY            | Only send native crash reports under wifi or ethernet        |
| FLAG_NATIVE_NOT_UPLOAD_TWO_HOURS | Native crash reports that occur repeatedly within two hours are not sent |
| FLAG_ANR_DISABLE                 | Do not send anr reports                                      |
| FLAG_ANR_WIFI_ONLY               | Only send  anr reports under wifi or ethernet                |
| FLAG_ANR_NOT_UPLOAD_TWO_HOURS    | ANR reports that occur repeatedly within two hours are not sent |

### Set report filter class

​	If the predefined flags cannot meet your needs, you can customize the report filter class

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

### Init   Initialize operation, and finally call

```
public void init()
```

### Set form uri

```java
public Stackly setFormUri(String formUri) {}
```

| Parameter | Type   | Description |
| --------- | ------ | ----------- |
| formUri   | String | Uri used    |

### Set to skip init

```java
public Stackly bypassInit(boolean bypassInit) {}
```

| Parameter  | Type    | Description                                                  |
| ---------- | ------- | ------------------------------------------------------------ |
| bypassInit | Boolean | Set to skip init, which means that the crash will not be uploaded and generally used for debugging |

### Enable dev log

```java
public Stackly enableDevLog(boolean enable) {}
```

| Parameter | Type    | Description          |
| --------- | ------- | -------------------- |
| enable    | Boolean | Whether to print log |

### Watch ANR

```java
public Stackly watchANR(boolean watchANR) {}
```

| Parameter | Type    | Description            |
| --------- | ------- | ---------------------- |
| watchANR  | Boolean | Whether to collect Anr |

### Watch native

```java
public Stackly watchNative(boolean watchNative) {}
```

| Parameter   | Type    | Description                     |
| ----------- | ------- | ------------------------------- |
| watchNative | Boolean | Whether to collect native crash |

### Set Sp file name

```java
public Stackly setSpFileName(String spFileName) {}
```

| Parameter  | Type   | Description                                |
| ---------- | ------ | ------------------------------------------ |
| spFileName | String | Set the file name for saving configuration |

### Put  custom  error  report  data

```java
public Stackly putCustomErrorReportData(String key, String value) {}
```

| Parameter | Type   | Description |
| --------- | ------ | ----------- |
| key       | String | Data key    |
| value     | String | Data value  |

### Contains  custom data key

```java
public boolean containsCustomDataKey(String key) {}
```

| Parameter | Type   | Description                |
| --------- | ------ | -------------------------- |
| key       | String | Custom data exist this key |

### Send a single custom event

```java
public static void handleEvent(EventInfo eventInfo) throws EventFailedException {
}
```

| Parameter | Type      | Description  |
| --------- | --------- | ------------ |
| eventInfo | EventInfo | Single event |

### Send multiple custom events

```java
public static void handleEvent(List<EventInfo> eventInfoList) throws EventFailedException {
}
```

| Parameter     | Type            | Description |
| ------------- | --------------- | ----------- |
| eventInfoList | List<EventInfo> | Event list  |

### EventInfo

```java
new EventInfo.Builder().setEventId(eventid).setEventTime(eventtime).setParam(param).build()
```

| Parameter | Type                | Description                                                  |
| --------- | ------------------- | ------------------------------------------------------------ |
| eventId   | String              | The id of event                                              |
| eventTime | long                | Time when the event occurred (ms)                            |
| param     | Map<String, String> | Parameter key-value pairs that need to be sent, The maximum number of params is 100, the maximum length of key is 64, and the maximum length of value is 256 |

### Prohibition of starting operational data analysis

```java
 public Stackly disableStartAnalysis(boolean disable) {
 }
```

| Parameter    | Type    | Description                                                         |
| ------- | ------- | ------------------------------------------------------------ |
| disable | boolean | Whether to prohibit analysis of operational data such as application startup, the default is to upload |

### Custom start analysis strategy

```java
 public Stackly customStartAnalysisStrategy(boolean customStrategy) {}
```

| Parameter           | Type    | Description                                                         |
| -------------- | ------- | ------------------------------------------------------------ |
| customStrategy | boolean | Whether to customize the startup analysis strategy, the default strategy is to determine that the application is active when it is opened |

### Upload application startup information

```JAVA
  public static void handleStartInfo() {}
```

tips：Only by using a custom startup analysis strategy, can you call and upload the application startup information normally, otherwise it will be invalid.

### Upload the number and duration of application usage

tips：Call the following code in the onResume and onPause methods of BaseActivity

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



## Migrating to Android 8.0

Since Android 8.0 has lots of changes that will affect your app's behavior, we recommand you to follow the guide to migrate
to Android 8.0. For further information, you can refer to https://developer.android.google.cn/about/versions/oreo/android-8.0-migration

## Migrate to Androidx

This project uses androidx. If there is a conflict with your application, it is recommended to use this guide for migration

Please refer to https://developer.android.google.cn/jetpack/androidx/migrate


## FAQ

#### 1. How to resolve dependencies conflict?

When dependencies conflict occur, the error message may like below:

    Program type already present: xxx.xxx.xxx

**Solution:**

You can use **exclude()** method to exclude the conflict dependencies by **group** or **module** or **both**.

e.g. To exclude 'com.google.code.gson:gson:2.8.5' in SDK, you can use below:

    implementation ('com.whatspos.sdk:stackly-android-sdk:x.xx.xx'){
        exclude group: 'com.google.code.gson', module: 'gson'
    }

#### 2. How to resolve attribute conflict?

When attribute conflict occur, the error message may like below:

    Manifest merger failed : Attribute application@allowBackup value=(false) from 
    AndroidManifest.xml...
    is also present at [stackly:x.xx.xx]
    AndroidManifest.xml...
    Suggestion: add 'tools:replace="android:allowBackup"' to <application> element
    at AndroidManifest.xml:..

**Solution:**

Add **xmlns:tools="http\://<span></span>schemas.android.com/tools"** in your manifest header

       <manifest xmlns:android="http://schemas.android.com/apk/res/android"
            package="com.yourpackage"
            xmlns:tools="http://schemas.android.com/tools">

Add **tools:replace = "the confilct attribute"** to your application tag:

        <application
            ...
            tools:replace="allowBackup"/>

## License

See the [Apache 2.0 license](https://github.com/PAXSTORE/paxstore-3rd-app-android-sdk/blob/master/LICENSE) file for details.

    Copyright © 2019 Shenzhen Zolon Technology Co., Ltd. All Rights Reserved.
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at following link.
    
         http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
