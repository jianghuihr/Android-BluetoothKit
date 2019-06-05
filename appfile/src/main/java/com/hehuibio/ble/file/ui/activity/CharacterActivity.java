package com.hehuibio.ble.file.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.hehuibio.ble.file.R;
import com.hehuibio.ble.file.ui.adapter.BleToolPagerAdapter;
import com.hehuibio.ble.file.command.Bus;
import com.hehuibio.ble.file.command.Execute;
import com.hehuibio.ble.file.common.helper.ClientManager;
import com.hehuibio.ble.file.common.utils.CommonUtils;
import com.hehuibio.ble.file.ui.presenter.DebugPresenter;
import com.hehuibio.ble.file.ui.presenter.OtherPresenter;
import com.hehuibio.ble.file.ui.presenter.TempPresenter;
import com.hehuibio.ble.file.ui.view.PageView;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;

/**
 * Created by dingjikerbo on 2016/9/6.
 */
public class CharacterActivity extends Activity {

    private final String TAG = CharacterActivity.class.getSimpleName();
    private String mMac;
    private String mName;
    private UUID mService;
    private UUID mCharacter;

    private TextView mTvTitle;
    private ViewPager mBlePager;
    private BleToolPagerAdapter bleToolPagerAdapter;
    private TabLayout tabLayout;
    private TextView importFileTV;

    private TempPresenter tempPresenter;
    private DebugPresenter debugPresenter;
    private OtherPresenter otherPresenter;
    private List<PageView> bleToolPageList = new ArrayList<>();

    public static void open(Activity activity,
                            String name,
                            String mac,
                            UUID service,
                            UUID character) {
        Intent intent = new Intent(activity, CharacterActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("mac", mac);
        intent.putExtra("service", service);
        intent.putExtra("character", character);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.character_activity);

        Intent intent = getIntent();
        mMac = intent.getStringExtra("mac");
        mName = intent.getStringExtra("name");
        mService = (UUID) intent.getSerializableExtra("service");
        mCharacter = (UUID) intent.getSerializableExtra("character");

        Bus.initConfig(new Execute(mMac, mService, mCharacter));
        //自动开启蓝牙接收
        ClientManager.getClient().notify(mMac, mService, mCharacter, mNotifyRsp);

        mTvTitle = (TextView) findViewById(R.id.title);
        mTvTitle.setText(String.format("%s", mName));

        importFileTV = (TextView) findViewById(R.id.tv_import_file);
        importFileTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.openFileManager(CharacterActivity.this);
            }
        });

        mBlePager = (ViewPager) findViewById(R.id.pager_ble);
        mBlePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        bleToolPagerAdapter = new BleToolPagerAdapter();
        mBlePager.setAdapter(bleToolPagerAdapter);

        tempPresenter = new TempPresenter(this);
        debugPresenter = new DebugPresenter(this);
        otherPresenter = new OtherPresenter(this);
        bleToolPageList.add(new PageView(tempPresenter.getView(), tempPresenter.getTitle()));
        bleToolPageList.add(new PageView(debugPresenter.getView(), debugPresenter.getTitle()));
        bleToolPageList.add(new PageView(otherPresenter.getView(), otherPresenter.getTitle()));
        bleToolPagerAdapter.refresh(bleToolPageList);
        mBlePager.setCurrentItem(1);

        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(mBlePager);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ClientManager.getClient().disconnect(mMac);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                break;
        }
    }
}
