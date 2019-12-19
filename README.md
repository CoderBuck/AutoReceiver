# AutoReceiver
通过注解自动生成广播接收器

```
implementation 'me.buck:auto-receiver:0.2.0'
annotationProcessor 'me.buck:auto-receiver-compiler:0.2.0'
```

```java
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final String ACTION_1 = "action-1";
    private static final String ACTION_2 = "action-2";
    private static final String ACTION_3 = "action-3";

    @BindView(R.id.btn1) Button mBtn1;
    @BindView(R.id.btn2) Button mBtn2;
    @BindView(R.id.btn3) Button mBtn3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        AutoReceiver.bindLocal(this);
        AutoReceiver.bindGlobal(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AutoReceiver.unbindLocal(this);
        AutoReceiver.unbindGlobal(this);
    }

    @LocalAction(ACTION_1)
    public void test1(Intent intent) {
        Log.i(TAG, "test1: " + intent.getAction());
    }
    
    @GlobalAction(ACTION_2)
    public void test5(Intent intent) {
        Log.i(TAG, "test5: " + intent.getAction());
    }

    @OnClick(R.id.btn1)
    public void onMBtn1Clicked() {
        send(ACTION_1);
    }

    @OnClick(R.id.btn2)
    public void onMBtn2Clicked() {
        sendBroadcast(new Intent(ACTION_2));
    }

    void send(String action) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(action));
    }
}
```
