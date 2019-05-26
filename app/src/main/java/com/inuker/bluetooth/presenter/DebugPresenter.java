package com.inuker.bluetooth.presenter;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.inuker.bluetooth.ClientManager;
import com.inuker.bluetooth.CommonUtils;
import com.inuker.bluetooth.R;
import com.inuker.bluetooth.adapter.WidgetsAdapter;
import com.inuker.bluetooth.command.Bus;
import com.inuker.bluetooth.command.CmdReturn;
import com.inuker.bluetooth.command.Execute;
import com.inuker.bluetooth.concurrency.MyHandler;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.model.Temp;

import java.util.UUID;
import java.util.logging.Logger;

import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;

public class DebugPresenter extends BasePresenter implements View.OnClickListener {

    private Activity activity;
    private final String TITLE = "调试";

    private String mMac;
    private String mName;
    private UUID mService;
    private UUID mCharacter;

    private EditText distanceET;


    public DebugPresenter(Activity activity,
                          String mac,
                          UUID serviceUUID,
                          UUID characterUUID) {
        this.activity = activity;
        this.mMac = mac;
        this.mService = serviceUUID;
        this.mCharacter = characterUUID;
    }

    @Override
    public View getView() {
        View view = LayoutInflater.from(activity).inflate(R.layout.view_presenter_debug, null);
        initView(view);
        //自动开启蓝牙接收
        ClientManager.getClient().notify(mMac, mService, mCharacter, mNotifyRsp);
        return view;
    }

    private void initView(View view) {
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
        }
    }

    private final BleWriteResponse mWriteRsp = new BleWriteResponse() {
        @Override
        public void onResponse(int code) {
            if (code == REQUEST_SUCCESS) {
                CommonUtils.toast("success");
            } else {
                CommonUtils.toast("failed");
            }
        }
    };

    private final BleNotifyResponse mNotifyRsp = new BleNotifyResponse() {
        @Override
        public void onNotify(UUID service, UUID character, byte[] value) {
            if (service.equals(mService) && character.equals(mCharacter)) {
                Bus.receive(value);
            }
        }

        @Override
        public void onResponse(int code) {
            if (code == REQUEST_SUCCESS) {
                CommonUtils.toast("success");
            } else {
                CommonUtils.toast("failed");
            }
        }
    };
}
