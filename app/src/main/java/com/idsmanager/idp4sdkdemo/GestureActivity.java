package com.idsmanager.idp4sdkdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.idsmanager.verificationtypelibrary.gesture.domain.LockMode;
import com.idsmanager.verificationtypelibrary.gesture.service.OnCompleteListener;
import com.idsmanager.verificationtypelibrary.gesture.view.GestureLockView;
import com.idsmanager.verificationtypelibrary.gesture.view.GestureParam;
import com.summer.commons.viewannotation.ContentView;
import com.summer.commons.viewannotation.InjectHandler;
import com.summer.commons.viewannotation.OnClick;

@ContentView(R.layout.activity_gesture)
public class GestureActivity extends AppCompatActivity {
    private static final String TAG = "GestureActivity";
    private GestureLockView customLockView;
    private TextView tvInfo;
    private GestureParam gestureParam;
    private String gesturePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectHandler.inject(this);
        customLockView = findViewById(R.id.gesture_view);
        tvInfo = findViewById(R.id.tv_info);
        gestureParam = new GestureParam()
                //错误次数，开启远程验证可以忽略
                .errorNumber(3)
                //手势点的最少个数
                .passwordMinLength(4)
                //账户唯一ID，用来支持单APP多账户情况
                .userId("111")
                //AES加密手势秘钥key
                .aesKey("1234567812345678")
                //开启远程验证
                .remoteVerify(true);
        //设置当前是设置手势密码模式，在设置手势之前，调用自己服务器端获取手势是否已经初始化，未初始化再进行初始化
        gestureParam.mode(LockMode.SETTING_PASSWORD);
        customLockView.setGestureParam(gestureParam);
        customLockView.setCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(String password, int[] indexs) {
                Log.d(TAG, "onComplete: password=" + password + ";indexs=" + indexs);
                gesturePassword = password;
//                byte[] decode = Base64.decode(password);
//                for (int i = 0; i < decode.length; i++) {
//                    Log.d(TAG, "onComplete:" + i + "：" + decode[i]);
//                }

                LockMode mode = customLockView.getGestureParam().mode();
                if (LockMode.SETTING_PASSWORD.equals(mode)) {
                    tvInfo.setText("手势设置成功");
                    //开启远程验证，这里需要将手势初始化给自己APP的服务端。
                }
                if ((LockMode.EDIT_PASSWORD.equals(mode) && !gestureParam.remoteVerify()) || (LockMode.EDIT_PASSWORD.equals(mode) && gestureParam.remoteVerify() && gestureParam.remoteVerified())) {
                    tvInfo.setText("手势设置成功");
                }

                if (LockMode.VERIFY_PASSWORD.equals(mode) && gestureParam.remoteVerify()) {
                    //开启远程验证，这里需要将手势给自己APP的服务端进行验证。验证成功后设置 gestureParam.remoteVerified(true);

                }

                if (LockMode.EDIT_PASSWORD.equals(mode) && gestureParam.remoteVerify()) {
                    //开启远程验证，这里需要将手势给自己APP的服务端进行修改。
                }
            }

            @Override
            public void onErrorNumber(int errorTimes) {
                //-1标示没有设置旧密码
                Log.d(TAG, "onErrorNumber: " + errorTimes);
                tvInfo.setText("手势错误" + errorTimes + "次");
            }

            @Override
            public void onPasswordIsShort(int passwordMinLength) {
                Log.d(TAG, "onPasswordIsShort: passwordMinLength=" + passwordMinLength);
                tvInfo.setText("手势过短，请设置大于等于" + passwordMinLength + "个点。");
            }

            @Override
            public void onAgainInputPassword(LockMode mode, String password, int[] indexs) {
                Log.d(TAG, "onAgainInputPassword: mode=" + mode + ";password=" + password + ";indexs=" + indexs);
                tvInfo.setText("请再次设置手势");
            }

            @Override
            public void onInputNewPassword() {
                Log.d(TAG, "onInputNewPassword: ");
                tvInfo.setText("请输入新手势");
            }

            @Override
            public void onEnteredPasswordsDiffer() {
                Log.d(TAG, "onEnteredPasswordsDiffer: ");
                tvInfo.setText("两次手势不一致");
            }

            @Override
            public void onErrorNumberMany() {
                Log.d(TAG, "onErrorNumberMany: ");
                tvInfo.setText("错误次数太多");
            }

            @Override
            public void onVerifySuccess() {
                Log.d(TAG, "onVerifySuccess: ");
                tvInfo.setText("手势验证成功");
                //开启远程验证时，无需关心该回调
            }

            @Override
            public void onErrorMessage(String errorMessage) {
                Log.d(TAG, "errorMessage: " + errorMessage);
                tvInfo.setText(errorMessage);
            }
        });
    }

    @OnClick({R.id.btn_set_pwd, R.id.btn_modify_pwd, R.id.btn_verify_pwd, R.id.btn_remove_pwd,
            R.id.btn_verify_remote_success, R.id.btn_verify_remote_fail})
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            //设置密码：开始就输手势，然后重复上次手势，两次一样即通过。
            case R.id.btn_set_pwd:
                gestureParam.mode(LockMode.SETTING_PASSWORD);
                customLockView.setGestureParam(gestureParam);
                tvInfo.setText("设置手势");
                break;
            case R.id.btn_modify_pwd:
                //修改密码：开始验证手势，通过后，输入两次一样的手势即通过。
                gestureParam.mode(LockMode.EDIT_PASSWORD);
                customLockView.setGestureParam(gestureParam);
                tvInfo.setText("修改手势");
                break;
            case R.id.btn_verify_pwd:
                //验证手势：验证设置后的手势。
                gestureParam.mode(LockMode.VERIFY_PASSWORD);
                customLockView.setGestureParam(gestureParam);
                tvInfo.setText("验证手势");
                break;
            case R.id.btn_remove_pwd:
                //清除：清除密码控件的缓存。
                gestureParam.mode(LockMode.CLEAR_PASSWORD);
                gestureParam.errorNumber(3);
                customLockView.setGestureParam(gestureParam);
                tvInfo.setText("清除手势");
                break;
            case R.id.btn_verify_remote_success:
//                customLockView.drawPassWordErrorRemoteVerify(gesturePassword);
                break;
            case R.id.btn_verify_remote_fail:
                customLockView.drawPassWordErrorRemoteVerify(gesturePassword);
                break;
            default:
                break;

        }
    }
}