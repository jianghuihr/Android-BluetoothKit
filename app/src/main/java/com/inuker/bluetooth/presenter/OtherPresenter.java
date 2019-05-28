package com.inuker.bluetooth.presenter;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.inuker.bluetooth.R;
import com.inuker.bluetooth.command.Bus;
import com.inuker.bluetooth.command.CmdReturn;
import com.inuker.bluetooth.concurrency.MyHandler;
import com.inuker.bluetooth.model.Param;
import com.inuker.bluetooth.model.Temp;

public class OtherPresenter extends BasePresenter implements View.OnClickListener {

    private Activity activity;
    private final String TITLE = "其他";

    private EditText data1ET;
    private EditText data2ET;

    private MyHandler handler = MyHandler.get();

    public OtherPresenter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public View getView() {
        View view = LayoutInflater.from(activity).inflate(R.layout.view_presenter_other, null);
        initView(view);

        handler.setCallback(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case MyHandler.MSG_WHAT_OTHER:
                        Param param = (Param) msg.obj;
                        if (null != param) {
                            data1ET.setText(String.valueOf(param.val1));
                            data2ET.setText(String.valueOf(param.val2));
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
        Button readBTN = (Button) view.findViewById(R.id.btn_read);
        readBTN.setOnClickListener(this);

        Button setBTN = (Button) view.findViewById(R.id.btn_set);
        setBTN.setOnClickListener(this);

        Button wrapBTN = (Button) view.findViewById(R.id.btn_wrap);
        wrapBTN.setOnClickListener(this);

        data1ET = (EditText) view.findViewById(R.id.et_data1);

        data2ET = (EditText) view.findViewById(R.id.et_data2);

    }

    @Override
    public void onClick(View v) {
        if (Bus.cmd != 0x00) {
            Toast.makeText(
                    activity.getApplicationContext(),
                    "指令忙",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        switch (v.getId()) {
            case R.id.btn_read:
                //读取点击事件
                Bus.op = 6;
                Bus.cmd = 0xE3;
            case R.id.btn_set:
                //设置点击事件
            case R.id.btn_wrap:
                //打包卡位点击事件
                break;
        }
    }
}
