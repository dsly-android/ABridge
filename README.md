### 导入
implementation 'com.android.dsly:abridge:latestVersion'

### 初始化
```java
  public class MainApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        //注意这里的packagename，需要通信的多个app只能使用一个packagename
        //ABridge初始化有两种方式
        //方式一:基于Messenger
        ABridge.init(this, "com.sjtu.yifei.aidlserver", IBridge.AbridgeType.MESSENGER);
        //方式二:基于自定义的AIDL
        ABridge.init(this, "com.sjtu.yifei.aidlserver", IBridge.AbridgeType.AIDL);
        //log开关
        ABridge.setLogDebug(BuildConfig.DEBUG);
    }

    @Override
    public void onTerminate() {
        //注意释放
        ABridge.recycle();
        super.onTerminate();
    }
  }
```
  
### 通信使用
#### 基于Messenger IPC
```java
 // 1 注册回调
 ABridge.registerMessengerCallBack(callBack = new AbridgeMessengerCallBack() {
            @Override
            public void receiveMessage(Message message) {
                if (message.arg1 == ACTIVITYID) {
                    //todo客户端接受服务端传来的消息
                    
                }
            }
        });
        
// 2 反注册回调，避免内存泄漏
ABridge.uRegisterMessengerCallBack(callBack);

// 3 发送消息
 Message message = Message.obtain();
 message.arg1 = ACTIVITYID;
 //注意这里，把`Activity`的`Messenger`赋值给了`message`中，当然可能你已经发现这个就是`Service`中我们调用的`msg.replyTo`了。
 Bundle bundle = new Bundle();
 bundle.putString("content", messageStr);
 message.setData(bundle);
 ABridge.sendMessengerMessage(message);
```
#### 基于AIDL IPC
```java
 // 1 注册回调
 ABridge.registerAIDLCallBack(callBack = new AbridgeCallBack() {
            @Override
            public void receiveMessage(String message) {
                //todo客户端接受服务端传来的消息
            }
        });
        
// 2 反注册回调，避免内存泄漏
ABridge.uRegisterAIDLCallBack(callBack);

// 3 发送消息
String message = "待发送消息";
ABridge.sendAIDLMessage(message);
```