package song.HttpDownload;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView mUrl_ET;
    public static Button mAction;
    private View mDropOut;
    public static String mUrl;
    public static SharedPreferences sp;
    private SharedPreferences.Editor mEdit;
    //Boolean B用于描述状态:
    //   是否在刷流量
    //   true:是   false:不是
    public static boolean b = false;
    public static Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initId();
        sp = this.getSharedPreferences("config", MODE_PRIVATE);
        //回显
        mUrl_ET.setText(sp.getString("url",""));
        //用于监听消息
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==100){
                    String str = (String) msg.obj;
                    Toast.makeText(getApplicationContext(),str,Toast.LENGTH_SHORT).show();
                }
            }
        };

    }

    private void initId() {
        mUrl_ET = findViewById(R.id.inputUrl_ET);
        mUrl_ET.setOnClickListener(this);
        mAction = findViewById(R.id.action_Btn);
        mAction.setOnClickListener(this);
        mDropOut = findViewById(R.id.dropOut_Btn);
        mDropOut.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.action_Btn :
                b = !b;
                action();
                break;
            case R.id.dropOut_Btn :
                //似乎可以不写这一行,待测试
                b = !b;
                finish();;break;
            default: break;
        }
    }

    private void action() {
        //检测并保存
        if (saveUrl()) return;
        //取反,用于开关操作
        if(b){
            //开刷
            mAction.setText("暂停");
            Thread_manager();
        }else {
            //暂停
            mAction.setText("继续");
        }
    }
    //线程管理阶段
    private void Thread_manager() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                Download d = Download.getInstance();
                //for循环用于一直监听变量b
                for(;b;){
                    if(SharedData.num<5){
                        //循环new线程
                        new Thread(d,String.valueOf(SharedData.num)).start();
                        SharedData.num++;
                    }
                }
            }
        }.start();
    }

    private boolean saveUrl() {
        mUrl = mUrl_ET.getText().toString().trim();
        if (mUrl ==null || mUrl.isEmpty()) {
            Toast.makeText(this, "无效链接!",Toast.LENGTH_SHORT).show();
            return true;
        }
        //保存url
        mEdit = sp.edit();
        mEdit.putString("url",mUrl);
        mEdit.commit();
        return false;
    }
}
