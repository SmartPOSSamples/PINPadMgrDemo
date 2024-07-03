package com.cloudpos.aidl.test;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.cloudpos.utils.Logger;
import com.cloudpos.utils.TextViewUtil;
import com.wizarpos.wizarviewagentassistant.aidl.IPINPadManagerService;


public class MainActivity extends AbstractActivity implements OnClickListener, ServiceConnection {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_run3 = (Button) this.findViewById(R.id.btn_run3);
        Button btn_run4 = (Button) this.findViewById(R.id.btn_run4);
        log_text = (TextView) this.findViewById(R.id.text_result);
        log_text.setMovementMethod(ScrollingMovementMethod.getInstance());

        findViewById(R.id.settings).setOnClickListener(this);
        btn_run3.setOnClickListener(this);
        btn_run4.setOnClickListener(this);


        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == R.id.log_default) {
                    log_text.append("\t" + msg.obj + "\n");
                } else if (msg.what == R.id.log_success) {
                    String str = "\t" + msg.obj + "\n";
                    TextViewUtil.infoBlueTextView(log_text, str);
                } else if (msg.what == R.id.log_failed) {
                    String str = "\t" + msg.obj + "\n";
                    TextViewUtil.infoRedTextView(log_text, str);
                } else if (msg.what == R.id.log_clear) {
                    log_text.setText("");
                }
            }
        };
        bindPINPadMgrService();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unbindService(this);
    }


    public void bindPINPadMgrService() {
        try {
            boolean result = startConnectService(MainActivity.this,
                    "com.wizarpos.wizarviewagentassistant",
                    "com.wizarpos.wizarviewagentassistant.PINPadMgrService", this);

            writerInLog("bind service result: " + result, R.id.log_default);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected boolean startConnectService(Context mContext, String packageName, String className, ServiceConnection connection) {
        boolean isSuccess = startConnectService(mContext, new ComponentName(packageName, className), connection);
        return isSuccess;
    }

    protected boolean startConnectService(Context context, ComponentName comp, ServiceConnection connection) {
        Intent intent = new Intent();
        intent.setPackage(comp.getPackageName());
        intent.setComponent(comp);
        boolean isSuccess = context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        Logger.debug("bind service (%s, %s)", isSuccess, comp.getPackageName(), comp.getClassName());
        return isSuccess;
    }

    @Override
    public void onClick(View arg0) {
        int index = arg0.getId();
        if (index == R.id.btn_run3) {
            resetTransferKey();
        } else if (index == R.id.btn_run4) {
            resetMasterKey();
        } else if (index == R.id.settings) {
            log_text.setText("");
        }
    }

    private void resetTransferKey() {
        try {
            boolean result = pinPadManagerService.resetTransferKey(0);
            writerInLog("resetTransferKey(0): result = " + result, R.id.log_default);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resetMasterKey() {
        try {
            boolean result = pinPadManagerService.resetMasterKey(0);
            writerInLog("resetMasterKey(0): result = " + result, R.id.log_default);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        try {
            writerInLog("onServiceConnected: " + service.getInterfaceDescriptor(), R.id.log_success);
            pinPadManagerService = IPINPadManagerService.Stub.asInterface(service);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    IPINPadManagerService pinPadManagerService;
}
