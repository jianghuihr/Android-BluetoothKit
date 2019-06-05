package com.hehuibio.ble.file.ui.presenter;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hehuibio.ble.file.R;
import com.hehuibio.ble.file.command.Bus;
import com.hehuibio.ble.file.concurrency.MyHandler;
import com.hehuibio.ble.file.model.Param;

public class OtherPresenter extends BasePresenter implements View.OnClickListener {

    private Activity activity;
    private final String TITLE = "其他";

    private EditText dataAET;
    private EditText dataBET;
    private TextView versionTV;
    private TextView codeTV;
    private TextView buildDateTV;

    public OtherPresenter(Activity activity) {
        this.activity = activity;
    }

    private MyHandler handler = MyHandler.get();

    @Override
    public View getView() {
        View view = LayoutInflater.from(activity).inflate(R.layout.view_presenter_other, null);
        initView(view);

        handler.addSendCallback(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Param param = null;
                switch (msg.what) {
                    case MyHandler.MSG_WHAT_OTHER_E3:
                        param = (Param) msg.obj;
                        if (null != param) {
                            dataAET.setText(String.valueOf(param.val2));
                            dataBET.setText(String.valueOf(param.val1));
                        }
                        break;
                    case MyHandler.MSG_WHAT_OTHER_EF:
                        param = (Param) msg.obj;
                        if (null != param) {
                            versionTV.setText("V" + param.val1);
                            codeTV.setText("M" + param.val2);
                            buildDateTV.setText(param.s1);
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

        Button requestVersionBTN = (Button) view.findViewById(R.id.btn_request_version);
        requestVersionBTN.setOnClickListener(this);

        Button yOldBTN = (Button) view.findViewById(R.id.btn_y_old);
        yOldBTN.setOnClickListener(this);

        versionTV = (TextView) view.findViewById(R.id.tv_version);
        codeTV = (TextView) view.findViewById(R.id.tv_code);
        buildDateTV = (TextView) view.findViewById(R.id.tv_build_date);

        dataAET = (EditText) view.findViewById(R.id.et_data_a);
        dataBET = (EditText) view.findViewById(R.id.et_data_b);
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
                break;
            case R.id.btn_set:
                //设置点击事件
                Bus.val1 = Integer.parseInt(dataBET.getText().toString());
                Bus.val2 = Integer.parseInt(dataAET.getText().toString());
                Bus.op = 6;
                Bus.cmd = 0xE7;
                break;
            case R.id.btn_wrap:
                //打包卡位点击事件
                Bus.op = 8;
                Bus.cmd = 0xE1;
                break;
            case R.id.btn_request_version:
                Bus.op = 0xFE;
                Bus.cmd = 0xEF;
                break;
            case R.id.btn_y_old:
                Bus.op=9;
                Bus.cmd= 0xE7;
                break;
        }
    }
}
