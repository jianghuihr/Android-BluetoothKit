package com.inuker.bluetooth.presenter;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.inuker.bluetooth.R;
import com.inuker.bluetooth.command.Bus;
import com.inuker.bluetooth.concurrency.MyHandler;
import com.inuker.bluetooth.model.Temp;

public class TempPresenter extends BasePresenter implements View.OnClickListener {

    private final String TAG = TempPresenter.class.getSimpleName();
    private Activity activity;
    private final String TITLE = "温度";

    private EditText tempET;
    private TextView topTV;
    private TextView modTV;
    private TextView envTV;

    private MyHandler handler = MyHandler.get();

    public TempPresenter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public View getView() {
        View view = LayoutInflater.from(activity).inflate(R.layout.view_presenter_temp, null);
        initView(view);

        handler.setReceiveCallback(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case MyHandler.MSG_WHAT_TEMP:
                        Temp temp = (Temp) msg.obj;
                        if (null != temp) {
                            topTV.setText(String.valueOf(temp.getTop()));
                            modTV.setText(String.valueOf(temp.getMod()));
                            envTV.setText(String.valueOf(temp.getEnv()));
                        }
                        break;
                }
                return false;
            }
        });
        return view;
    }

    @Override
    public String getTitle() {
        return TITLE;
    }

    private void initView(View view) {
        Button runBTN = (Button) view.findViewById(R.id.btn_run);
        runBTN.setOnClickListener(this);

        Button stopBTN = (Button) view.findViewById(R.id.btn_stop);
        stopBTN.setOnClickListener(this);

        Button executeBTN = (Button) view.findViewById(R.id.btn_execute);
        executeBTN.setOnClickListener(this);

        tempET = (EditText) view.findViewById(R.id.et_temp);

        topTV = (TextView) view.findViewById(R.id.tv_top);
        modTV = (TextView) view.findViewById(R.id.tv_mod);
        envTV = (TextView) view.findViewById(R.id.tv_env);

    }

    @Override
    public void onClick(View view) {
        if (Bus.cmd != 0x00) {
            Toast.makeText(
                    activity.getApplicationContext(),
                    "指令忙",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        switch (view.getId()) {
            case R.id.btn_run:
                //运行点击事件
                Bus.op = 1;
                Bus.cmd = 0xA1;
                break;
            case R.id.btn_stop:
                //停止点击事件
                Bus.op = 0;
                Bus.cmd = 0xA1;
                break;
            case R.id.btn_execute:
                //执行点击事件
                Bus.f1 = Float.parseFloat(tempET.getText().toString());
                Bus.cmd = 0xA8;
                break;
        }
    }
}
