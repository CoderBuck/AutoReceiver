

# AutoReceiver

利用编译时注解自动生成广播接收器，从而减少 BroadcastReceiver 样板代码的编写。

这里提供了两个注解`LocalAction`和`GlobalAction`用来接收本地广播和全局广播。用这两个注解标记的方法必须带有一个`Intent`参数并且不能是`private`方法。使用 `AutoReceiver.bind()`和`AutoReceiver.unbind()`方法来注册/取消注册广播。

## 添加依赖

```
implementation 'me.buck:auto-receiver:0.2.0'
annotationProcessor 'me.buck:auto-receiver-compiler:0.2.0'
```

## 使用
```java
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AutoReceiver.bindLocal(this);
        AutoReceiver.bindGlobal(this);

        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("action-1"));
        sendBroadcast(new Intent("action-2"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AutoReceiver.unbindLocal(this);
        AutoReceiver.unbindGlobal(this);
    }

    @LocalAction("action-1")
    void onAction1(Intent intent) {
        Log.d(TAG, "onAction1: " + intent.getAction());
    }

    @GlobalAction("action-2")
    void onAction2(Intent intent) {
        Log.d(TAG, "onAction2: " + intent.getAction());
    }
}
```
