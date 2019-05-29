package com.inuker.bluetooth.presenter;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.inuker.bluetooth.R;
import com.inuker.bluetooth.command.Bus;
import com.inuker.bluetooth.concurrency.MyHandler;
import com.inuker.bluetooth.model.Param;

public class DebugPresenter extends BasePresenter implements View.OnClickListener {

    private final String TAG = DebugPresenter.class.getSimpleName();
    private Activity activity;
    private final String TITLE = "调试";

    private EditText distanceET;
    private RadioButton xZeroRB;
    private RadioButton yZeroRB;
    private TextView resultTV;
    private EditText dataAET;
    private EditText dataBET;

    private MyHandler handler = MyHandler.get();

    public DebugPresenter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public View getView() {
        View view = LayoutInflater.from(activity).inflate(R.layout.view_presenter_debug, null);
        initView(view);

        handler.addSendCallback(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case MyHandler.MSG_WHAT_OTHER_E5:
                        Param paramE5 = (Param) msg.obj;
                        if (null != paramE5) {
                            resultTV.setText(String.valueOf(paramE5.val1));
                        }
                        break;
                }
                return false;
            }
        });
        return view;
    }

    private void initView(View view) {
        dataAET = (EditText) view.findViewById(R.id.et_data_a);

        dataBET = (EditText) view.findViewById(R.id.et_data_b);

        distanceET = (EditText) view.findViewById(R.id.et_distance);

        TextView resetXYTV = (TextView) view.findViewById(R.id.tv_reset_xy);
        resetXYTV.setOnClickListener(this);

        RadioButton relativeRB = (RadioButton) view.findViewById(R.id.rb_relative);
        relativeRB.setChecked(true);
        relativeRB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                if (isCheck) {
                    //相对点击
                    Bus.op = 0;
                }
            }
        });

        RadioButton directRB = (RadioButton) view.findViewById(R.id.rb_direct);
        directRB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                if (isCheck) {
                    //直接点击
                    Bus.op = 1;
                }
            }
        });

        Button xToZeroBTN = (Button) view.findViewById(R.id.btn_x_to_zero);
        xToZeroBTN.setOnClickListener(this);

        Button xOutZeroBTN = (Button) view.findViewById(R.id.btn_x_out_zero);
        xOutZeroBTN.setOnClickListener(this);

        Button yToZeroBTN = (Button) view.findViewById(R.id.btn_y_to_zero);
        yToZeroBTN.setOnClickListener(this);

        Button yOutZeroBTN = (Button) view.findViewById(R.id.btn_y_out_zero);
        yOutZeroBTN.setOnClickListener(this);

        Button tiredBTN = (Button) view.findViewById(R.id.btn_tired);
        tiredBTN.setOnClickListener(this);

        RadioGroup chooseRG = (RadioGroup) view.findViewById(R.id.rg_choose);
        chooseRG.check(R.id.rb_1);
        chooseRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                switch (id) {
                    case R.id.rb_1:
                        Log.i(TITLE, "通道一光源");
                        break;
                    case R.id.rb_2:
                        break;
                    case R.id.rb_3:
                        break;
                    case R.id.rb_4:
                        break;
                    case R.id.rb_5:
                        break;
                }
            }
        });

        xZeroRB = (RadioButton) view.findViewById(R.id.rb_x_zero);
        xZeroRB.setChecked(true);

        yZeroRB = (RadioButton) view.findViewById(R.id.rb_y_zero);

        resultTV = (TextView) view.findViewById(R.id.tv_result);

        Button readBTN = (Button) view.findViewById(R.id.btn_read);
        readBTN.setOnClickListener(this);
    }

    @Override
    public String getTitle() {
        return TITLE;
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
            case R.id.tv_reset_xy:
                Bus.op = 1;
                Bus.cmd = 0xE1;
                break;
            case R.id.btn_x_to_zero:
                //X向零位点击事件
                Bus.val1 = -Integer.parseInt(distanceET.getText().toString());
                if (Bus.op == 0) // 相对
                    Bus.op = 0;  //
                else
                    Bus.op = 2;
                Bus.cmd = 0xE2;
                break;
            case R.id.btn_x_out_zero:
                //X离零位点击事件
                Bus.val1 = Integer.parseInt(distanceET.getText().toString());
                if (Bus.op == 0) // 相对
                    Bus.op = 0;
                else
                    Bus.op = 2;
                Bus.cmd = 0xE2;
                break;
            case R.id.btn_y_to_zero:
                //Y向零位点击事件
                Bus.val1 = -Integer.parseInt(distanceET.getText().toString());
                if (Bus.op == 0) // 相对
                    Bus.op = 3;
                else
                    Bus.op = 5;
                Bus.cmd = 0xE2;
                break;
            case R.id.btn_y_out_zero:
                //Y离零位点击事件
                Bus.val1 = Integer.parseInt(distanceET.getText().toString());
                if (Bus.op == 0) // 相对
                    Bus.op = 3;
                else
                    Bus.op = 5;
                Bus.cmd = 0xE2;
                break;
            case R.id.btn_tired:
                //传动老化点击事件
                Bus.op = 0x08;
                Bus.cmd = 0xE7;
                break;
            case R.id.btn_read:
                if (xZeroRB.isChecked()) {
                    Bus.val1 = 0x40;
                } else if (yZeroRB.isChecked()) {
                    Bus.val1 = 0x42;
                }
                Bus.cmd = 0xE5;
                break;
        }
    }
}
