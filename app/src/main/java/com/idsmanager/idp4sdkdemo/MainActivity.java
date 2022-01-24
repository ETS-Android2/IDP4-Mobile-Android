package com.idsmanager.idp4sdkdemo;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.security.realidentity.RPEventListener;
import com.alibaba.security.realidentity.RPResult;
import com.alibaba.security.realidentity.RPVerify;
import com.idsmanager.idp4zerotrustlibrary.CertificateLogin;
import com.idsmanager.idp4zerotrustlibrary.service.CertLoginService;
import com.idsmanager.idp4zerotrustlibrary.service.CheckUserRealPersonService;
import com.idsmanager.idp4zerotrustlibrary.service.DownloadCertService;
import com.idsmanager.idp4zerotrustlibrary.service.EndRealPersonService;
import com.idsmanager.idp4zerotrustlibrary.service.LoadAppAccountService;
import com.idsmanager.idp4zerotrustlibrary.service.LoadAppsService;
import com.idsmanager.idp4zerotrustlibrary.service.ObtainSmsVerificationCodeService;
import com.idsmanager.idp4zerotrustlibrary.service.OneTimeLoginService;
import com.idsmanager.idp4zerotrustlibrary.service.PwdLoginService;
import com.idsmanager.idp4zerotrustlibrary.service.RealPersonConfigurationCheckService;
import com.idsmanager.idp4zerotrustlibrary.service.RefreshUserAccessTokenService;
import com.idsmanager.idp4zerotrustlibrary.service.SmsVerificationCodeLoginService;
import com.idsmanager.idp4zerotrustlibrary.service.StartFaceAuthService;
import com.idsmanager.idp4zerotrustlibrary.service.VerifyTokenService;
import com.idsmanager.verificationtypelibrary.biometric.BiometricManager;
import com.idsmanager.verificationtypelibrary.biometric.service.OnBiometricIdentifyService;
import com.summer.commons.authoritymanagement.PermissionCallback;
import com.summer.commons.authoritymanagement.PermissionGranted;
import com.summer.commons.authoritymanagement.PermissionsManager;
import com.summer.commons.viewannotation.ContentView;
import com.summer.commons.viewannotation.InjectHandler;
import com.summer.commons.viewannotation.OnClick;
import com.summer.commons.viewannotation.ViewInject;

import java.util.List;


@ContentView(R.layout.activity_main)
public class MainActivity extends AppCompatActivity implements PermissionCallback {


    private static final String TAG = "MainActivity";

    private static final java.lang.String HOST_URL = "https://lin1206.idp4.idsmanager.com/";

    private static final int WRITE_EXTERNAL_STORAGE = 111;
    @ViewInject(R.id.et_username)
    EditText etUsername;
    @ViewInject(R.id.et_password)
    EditText etPassword;
    @ViewInject(R.id.et_captcha)
    EditText etCaptcha;
    @ViewInject(R.id.et_phonenumber)
    EditText etPhonenumber;

    private String username;
    private String captcha;
    private String phonenumber;
    private String password;
    //账户密码登录后获取
    private static String accessToken = null;
    private static String refreshToken = null;
    //账户密码登录后获取userId,建议保存
    private String userId = "8625781909536561499";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectHandler.inject(this);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @OnClick({R.id.login, R.id.certificate_login, R.id.load_sms_verification_code, R.id.sms_verification_code_login,
            R.id.load_otp_code, R.id.decrypt_qr_content, R.id.exist_certificate,  R.id.test_finger_print,
            R.id.download_cert, R.id.load_apps, R.id.scan_login_idp,R.id.btn_start_real_person, R.id.btn_check_config, R.id.btn_start_face_auth,
            R.id.load_app_account, R.id.btn_refreshToken,R.id.gesture})
    public void onClick(View v) {
        username = etUsername.getText().toString();
        password = etPassword.getText().toString();
        captcha = etCaptcha.getText().toString();
        phonenumber = etPhonenumber.getText().toString();
        switch (v.getId()) {
            case R.id.login:
                pwdLogin();
                break;
            case R.id.btn_refreshToken:
                refreshToken();
                break;
            case R.id.btn_start_real_person:
                loadVerifyToken();
                break;
            case R.id.btn_start_face_auth:
                startFaceAuth();
                break;
            case R.id.btn_check_config:
                checkConfig();
                break;
            case R.id.certificate_login:
                certLogin();
                break;
            case R.id.load_sms_verification_code:
                loadSmsVerificationCode();
                break;
            case R.id.sms_verification_code_login:
                smsVerificationCodeLogin();
                break;
            case R.id.load_otp_code:
                String otp = CertificateLogin.getInstance(this).getOTPNumber(30, "7SY445RWS3LLI27K");
                showMessage("Load opt code : otp code = [" + otp + "]");
                break;
            case R.id.decrypt_qr_content:
                String qr = "{\"authKey\":\"9KE7bBa3bKkQqeZV\",\"info\":\"HoAFoViqbrQ2x3y6SyqEbMwYqhYZqDZ7xPpRoEF1F+BAZ/By9GlMvB8SyPvTmFVHA3EpSRigPumYm3AsTeMGHY987dyNbQs/KCtdYXJn00o=\"}";
                String decryptQRCode = null;
                try {
                    decryptQRCode = CertificateLogin.getInstance(MainActivity.this).decryptQRCode(qr);
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(e.getMessage());
                }
                showMessage(decryptQRCode);
                break;
            case R.id.exist_certificate:
                boolean existence = CertificateLogin.getInstance(this).certExistence();
                if (!existence) {
                    Toast.makeText(this, "证书不存在", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "证书存在", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.load_apps:
                CertificateLogin.getInstance(this).loadApps(accessToken, new LoadAppsService() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d(TAG, "onSuccess() called with: result = [" + result + "]");
                        showMessage("onSuccess() called with: result = [" + result + "]");
                        //[{"success":true,"code":"200","message":null,"requestId":"25CCAACF-AD0D-40DF-AB45-8F48E6A530D6","data":{"applicationList":[{"uuid":"50662f4043374b016866deb2d8bcbceftKkETTXAGrp","createTime":"2020-10-27 19:16","archived":false,"name":"OIDCNativeApp Plugin","applicationId":"plugin_oidc_native_app","applicationUuid":"ac1a4caa3ad47b868e4859612275d157zmloOwxfaYz","logoUuid":"","startUrl":"http://30.43.115.48:8084//enduser/application/plugin_oidc_native_app/sso_ac1a4caa3ad47b868e4859612275d157zmloOwxfaYz","authorizedTime":1603797405000,"enabled":true,"supportDeviceTypes":["MOBILE"],"logoUrl":"http://30.43.115.48:8084/public/app_image/plugin_oidc_native_app","nativeApp":false,"oidcNativeApp":true,"androidAppId":"AndroidOIDCNative","iosAppId":"iOSOIDCNative","androidFacetId":null,"iosFacetId":null,"androidPackageName":null,"startupClassName":null,"supportSSO":true,"vpnFlag":false,"internetFlag":false,"specialFlag":false,"downloadAppUrl":"https://www.idsmanager.com"}]}}]
                    }

                    @Override
                    public void onFail(String errorCode, String errors) {
                        Log.d(TAG, "onFail() called with: errorCode = [" + errorCode + "], errors = [" + errors + "]");
                        showMessage("onFail() called with: errorCode = [" + errorCode + "], errors = [" + errors + "]");
                    }
                });

                break;

            case R.id.load_app_account:
                CertificateLogin.getInstance(this).loadAppAccount(accessToken, "http://30.43.115.48:8084//api/bff/v1.2/enduser/plugin_oidc_native_app/sso_ac1a4caa3ad47b868e4859612275d157zmloOwxfaYz", new LoadAppAccountService() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d(TAG, "onSuccess() called with: result = [" + result + "]");
                        showMessage("onSuccess() called with: result = [" + result + "]");
                    }

                    @Override
                    public void onFail(String errorCode, String errors) {
                        Log.d(TAG, "onFail() called with: errorCode = [" + errorCode + "], errors = [" + errors + "]");
                        showMessage("onFail() called with: errorCode = [" + errorCode + "], errors = [" + errors + "]");
                    }
                });
                break;
            case R.id.download_cert:
                downloadCert();
                break;
            case R.id.test_finger_print:
                BiometricManager biometricManager = BiometricManager.getInstance(this, "e2nwh51mq4demZBM");
                boolean biometricPromptEnable = biometricManager.isBiometricPromptEnable();
                if (biometricPromptEnable) {
                    biometricManager.authenticate(new OnBiometricIdentifyService() {
                        @Override
                        public void onUsePassword() {
                            Log.d(TAG, "onUsePassword() called");
                            showMessage("onUsePassword() called");
                        }

                        @Override
                        public void onSucceeded(String fingerIds) {
                            Log.d(TAG, "onSucceeded() called with: fingerIds = [" + fingerIds + "]");
                            showMessage("onSucceeded() called with: fingerIds = [" + fingerIds + "]");
                        }

                        @Override
                        public void onFailed() {
                            Log.d(TAG, "onFailed() called");
                            showMessage("onFailed() called");
                        }

                        @Override
                        public void onError(int code, String reason) {
                            Log.d(TAG, "onError() called with: code = [" + code + "], reason = [" + reason + "]");
                            showMessage("onError() called with: code = [" + code + "], reason = [" + reason + "]");
                        }

                        @Override
                        public void onCancel() {
                            Log.d(TAG, "onCancel() called");
                            showMessage("onCancel() called");
                        }
                    }, new MyBiometricPromptDialog());
                }
                break;
            case R.id.gesture:
                Intent intent = new Intent(this, GestureActivity.class);
                startActivity(intent);
                break;
            case R.id.scan_login_idp:

                /**
                 * IDP登录 二维码 内容
                 * {"authKey":"kNHP55OPeZSzj1mJ","info":"QMAeDk6g7kgVgWBz4KsqQkAeDPJ8CGwE8mwhPEgSgdxKuHifoMhKZ+R2GbuLpQ/JpS+aMHJAPRpamr93wDvvU+sp0VbJqFdllVBfUEWl/jE="}
                 */
                CertificateLogin.getInstance(this).oneTimeLogin("{\"authKey\":\"nZbysQLBs5sl460n\",\"info\":\"RMirtOsmjaFOjxUBcZS1dS16J0ElaPcL43wgMMhKlcHui5JJo/VRqSln/3bUiOMditnCrMMQ0u7vJ8JcdQ/mZp/+f0sUPVBam6ycRvTLCoU=\"}", accessToken, new OneTimeLoginService() {
                    @Override
                    public void onSuccess(final String result) {
                        Log.d(TAG, "onSuccess: result=" + result);
                        showMessage("onSuccess: result=" + result);
                        //{"success":true,"code":"200","message":"","requestId":"B81429E0-5182-4961-B877-EE6296803778","data":null}
                    }

                    @Override
                    public void onFail(String errorCode, String errors) {
                        Log.d(TAG, "onFail: errorCode=" + errorCode + ";errors=" + errors);
                        showMessage("onFail: errorCode=" + errorCode + ";errors=" + errors);
                    }
                });
                break;
            default:
                break;
        }

    }

    /**
     * 获取短信验证码
     */
    private void loadSmsVerificationCode() {
        CertificateLogin.getInstance(MainActivity.this).obtainSmsVerificationCodeLogin(username,phonenumber, new ObtainSmsVerificationCodeService() {
            @Override
            public void onSuccess(final String result) {
                //result = [{"success":true,"code":"200","message":null,"requestId":null,"data":{"uuid":"23a34246c75879d76501ef89d88a0addd2dnsH4Q5H5","createTime":"2020-10-16 12:04","archived":false,"enterpriseUuid":"7c2cec4d2408d16e024db37542997ad8knZ65rWW2ZC","udAccountUuid":"4ae77e1fdec58c9bbc5dfcec2e94c9fdt3v40U8kCmu","username":"方***","useVendorAuth":"ALI_CLOUD","authResult":200,"authResultInfo":"认证通过","frontImgFileUuid":"c5ea1c5dd9710fe***f9b6d15gfyQjzRjeUL","backImgFileUuid":"e1ea803c20946***585ewH2RAS00Hdh","faceImgFileUuid":"91a474b53aff85***bd7612PNnJ2uKMcf2","idCardNumber":"34***90","sex":"Male","nationality":"汉","address":"安徽省***","startDate":"20120809","endDate":"20220809","authority":"某某公安局"}}]
                Log.d(TAG, "loadSmsVerificationCode onSuccess() called with: result = [" + result + "]");
                showMessage("loadSmsVerificationCode onSuccess() called with: result = [" + result + "]");
            }

            @Override
            public void onFail(String errorCode, String errors) {
                Log.d(TAG, "loadSmsVerificationCode onFail() called with: errorCode = [" + errorCode + "], errors = [" + errors + "]");
                showMessage("loadSmsVerificationCode onFail() called with: errorCode = [" + errorCode + "], errors = [" + errors + "]");
            }
        });
    }

    /**
     * 短信验证码登录
     */
    private void smsVerificationCodeLogin() {
        CertificateLogin.getInstance(MainActivity.this).smsVerificationCodeLogin(username, captcha, new SmsVerificationCodeLoginService() {
            @Override
            public void onSuccess(final String result) {
                //result = [{"success":true,"code":"200","message":null,"requestId":null,"data":{"uuid":"23a34246c75879d76501ef89d88a0addd2dnsH4Q5H5","createTime":"2020-10-16 12:04","archived":false,"enterpriseUuid":"7c2cec4d2408d16e024db37542997ad8knZ65rWW2ZC","udAccountUuid":"4ae77e1fdec58c9bbc5dfcec2e94c9fdt3v40U8kCmu","username":"方***","useVendorAuth":"ALI_CLOUD","authResult":200,"authResultInfo":"认证通过","frontImgFileUuid":"c5ea1c5dd9710fe***f9b6d15gfyQjzRjeUL","backImgFileUuid":"e1ea803c20946***585ewH2RAS00Hdh","faceImgFileUuid":"91a474b53aff85***bd7612PNnJ2uKMcf2","idCardNumber":"34***90","sex":"Male","nationality":"汉","address":"安徽省***","startDate":"20120809","endDate":"20220809","authority":"某某公安局"}}]
                Log.d(TAG, "smsVerificationCodeLogin onSuccess() called with: result = [" + result + "]");
                showMessage("smsVerificationCodeLogin onSuccess() called with: result = [" + result + "]");
            }

            @Override
            public void onFail(String errorCode, String errors) {
                Log.d(TAG, "smsVerificationCodeLogin onFail() called with: errorCode = [" + errorCode + "], errors = [" + errors + "]");
                showMessage("smsVerificationCodeLogin onFail() called with: errorCode = [" + errorCode + "], errors = [" + errors + "]");
            }
        });
    }


    private void refreshToken() {
        //刷新Token
        CertificateLogin.getInstance(MainActivity.this).refreshUserAccessToken(refreshToken, new RefreshUserAccessTokenService() {
            @Override
            public void onSuccess(final String result) {
                //result = [{"success":true,"code":"200","message":null,"requestId":null,"data":{"uuid":"23a34246c75879d76501ef89d88a0addd2dnsH4Q5H5","createTime":"2020-10-16 12:04","archived":false,"enterpriseUuid":"7c2cec4d2408d16e024db37542997ad8knZ65rWW2ZC","udAccountUuid":"4ae77e1fdec58c9bbc5dfcec2e94c9fdt3v40U8kCmu","username":"方***","useVendorAuth":"ALI_CLOUD","authResult":200,"authResultInfo":"认证通过","frontImgFileUuid":"c5ea1c5dd9710fe***f9b6d15gfyQjzRjeUL","backImgFileUuid":"e1ea803c20946***585ewH2RAS00Hdh","faceImgFileUuid":"91a474b53aff85***bd7612PNnJ2uKMcf2","idCardNumber":"34***90","sex":"Male","nationality":"汉","address":"安徽省***","startDate":"20120809","endDate":"20220809","authority":"某某公安局"}}]
                Log.d(TAG, "checkUserRealPerson onSuccess() called with: result = [" + result + "]");
                showMessage("checkUserRealPerson onSuccess() called with: result = [" + result + "]");
            }

            @Override
            public void onFail(String errorCode, String errors) {
                Log.d(TAG, "checkUserRealPerson onFail() called with: errorCode = [" + errorCode + "], errors = [" + errors + "]");
                showMessage("checkUserRealPerson onFail() called with: errorCode = [" + errorCode + "], errors = [" + errors + "]");
            }
        });
    }

    private void startFaceAuth() {
        CertificateLogin.getInstance(MainActivity.this).startFaceAuth(userId, new StartFaceAuthService() {
            @Override
            public void onSuccess(String result) {
                //result = [{"success":true,"code":"200","message":null,"requestId":"2211C865-4B8A-4904-B425-BB66F672F227","data":{"bizId":"a6dcaa2a70a94bb0078d654a028e10bbX83EJb9ZTsH","verifyToken":"d7202615824247b88c7de98b763ede25"}}]
                Log.d(TAG, "onSuccess() called with: result = [" + result + "]");
                JSONObject jsonObject = (JSONObject) JSONObject.parse(result);
                Boolean success = jsonObject.getBoolean("success");
                if (success) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    String verifyToken = data.getString("verifyToken");
                    String bizId = data.getString("bizId");
                    startFaceVerify(verifyToken, bizId);
                }
                showMessage("onSuccess() called with: result = [" + result + "]");
            }

            @Override
            public void onFail(String errorCode, String errors) {
                Log.d(TAG, "onFail() called with: errorCode = [" + errorCode + "], errors = [" + errors + "]");
                showMessage("onFail() called with: errorCode = [" + errorCode + "], errors = [" + errors + "]");
            }
        });
    }

    private void startFaceVerify(String verifyToken, String bizId) {
        RPVerify.startByNative(MainActivity.this, verifyToken, new RPEventListener() {
            @Override
            public void onFinish(RPResult auditResult, String code, String msg) {
                Toast.makeText(MainActivity.this, auditResult + "", Toast.LENGTH_LONG).show();
                Log.d(TAG, "onFinish() called with: auditResult = [" + auditResult + "], code = [" + code + "], msg = [" + msg + "]");
                //auditResult = [AUDIT_PASS], code = [1], msg = []
                if (auditResult == RPResult.AUDIT_PASS) {
                    // 认证通过。建议接入方调用实人认证服务端接口DescribeVerifyResult来获取最终的认证状态，并以此为准进行业务上的判断和处理。
                    // do something
                } else if (auditResult == RPResult.AUDIT_FAIL) {
                    // 认证不通过。建议接入方调用实人认证服务端接口DescribeVerifyResult来获取最终的认证状态，并以此为准进行业务上的判断和处理。
                    // do something
                } else if (auditResult == RPResult.AUDIT_NOT) {
                    // 未认证，具体原因可通过code来区分（code取值参见错误码说明），通常是用户主动退出或者姓名身份证号实名校验不匹配等原因，导致未完成认证流程。
                    // do something
                }
                showMessage("onFinish() called with: auditResult = [" + auditResult + "], code = [" + code + "], msg = [" + msg + "]");
            }
        });
    }

    private void checkConfig() {
        CertificateLogin.getInstance(MainActivity.this).checkRealPersonConfiguration(accessToken, new RealPersonConfigurationCheckService() {
            @Override
            public void onSuccess(String result) {
                //result = [实人认证已开启。]
                Log.d(TAG, "onSuccess() called with: result = [" + result + "]");
                showMessage("onSuccess() called with: result = [" + result + "]");
            }

            @Override
            public void onFail(String errorCode, String errors) {
                Log.d(TAG, "onFail() called with: errorCode = [" + errorCode + "], errors = [" + errors + "]");
                showMessage("onFail() called with: errorCode = [" + errorCode + "], errors = [" + errors + "]");
            }
        });
        //检查是否实人
        CertificateLogin.getInstance(MainActivity.this).checkUserRealPerson(accessToken, new CheckUserRealPersonService() {
            @Override
            public void onSuccess(String result) {
                //result = [{"success":true,"code":"200","message":null,"requestId":null,"data":{"uuid":"23a34246c75879d76501ef89d88a0addd2dnsH4Q5H5","createTime":"2020-10-16 12:04","archived":false,"enterpriseUuid":"7c2cec4d2408d16e024db37542997ad8knZ65rWW2ZC","udAccountUuid":"4ae77e1fdec58c9bbc5dfcec2e94c9fdt3v40U8kCmu","username":"方***","useVendorAuth":"ALI_CLOUD","authResult":200,"authResultInfo":"认证通过","frontImgFileUuid":"c5ea1c5dd9710fe***f9b6d15gfyQjzRjeUL","backImgFileUuid":"e1ea803c20946***585ewH2RAS00Hdh","faceImgFileUuid":"91a474b53aff85***bd7612PNnJ2uKMcf2","idCardNumber":"34***90","sex":"Male","nationality":"汉","address":"安徽省***","startDate":"20120809","endDate":"20220809","authority":"某某公安局"}}]
                Log.d(TAG, "checkUserRealPerson onSuccess() called with: result = [" + result + "]");
                showMessage("checkUserRealPerson onSuccess() called with: result = [" + result + "]");
            }

            @Override
            public void onFail(String errorCode, String errors) {
                Log.d(TAG, "checkUserRealPerson onFail() called with: errorCode = [" + errorCode + "], errors = [" + errors + "]");
                showMessage("checkUserRealPerson onFail() called with: errorCode = [" + errorCode + "], errors = [" + errors + "]");
            }
        });
    }

    private void loadVerifyToken() {
        CertificateLogin.getInstance(MainActivity.this).startRealPerson(accessToken, username, new VerifyTokenService() {
            @Override
            public void onSuccess(String result) {
                //result = [{"success":true,"code":"200","message":null,"requestId":"1CC63C03-BBBD-4BBC-82F9-B875451F71F3","data":{"bizId":"8e566a514ee4b974cbb0ae1e11b4e4cfp6PQpj8dlEH","verifyToken":"e1410994df974a96bf733734e7f1ffa0"}}]
                Log.d(TAG, "onSuccess() called with: result = [" + result + "]");
                JSONObject jsonObject = (JSONObject) JSONObject.parse(result);
                Boolean success = jsonObject.getBoolean("success");
                if (success) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    String verifyToken = data.getString("verifyToken");
                    String bizId = data.getString("bizId");
                    startVerify(verifyToken, bizId);
                }
                showMessage("onSuccess() called with: result = [" + result + "]");
            }

            @Override
            public void onFail(String errorCode, String errors) {
                Log.d(TAG, "onFail() called with: errorCode = [" + errorCode + "], errors = [" + errors + "]");
                showMessage("onFail() called with: errorCode = [" + errorCode + "], errors = [" + errors + "]");
            }
        });
    }

    private void startVerify(String verifyToken, final String bizId) {
        RPVerify.start(MainActivity.this, verifyToken, new RPEventListener() {
            @Override
            public void onFinish(RPResult auditResult, String code, String msg) {
                Log.d(TAG, "onFinish() called with: auditResult = [" + auditResult + "], code = [" + code + "], msg = [" + msg + "]");
                showMessage("onFinish() called with: auditResult = [" + auditResult + "], code = [" + code + "], msg = [" + msg + "]");
                if (auditResult == RPResult.AUDIT_PASS) {
                    // 认证通过。建议接入方调用实人认证服务端接口DescribeVerifyResult来获取最终的认证状态，并以此为准进行业务上的判断和处理。
                    // do something
                    CertificateLogin.getInstance(MainActivity.this).endRealPerson(accessToken, username, bizId, new EndRealPersonService() {
                        @Override
                        public void onSuccess(String result) {
                            Log.d(TAG, "onSuccess() called with: result = [" + result + "]");
                        }

                        @Override
                        public void onFail(String errorCode, String errors) {
                            Log.d(TAG, "onFail() called with: errorCode = [" + errorCode + "], errors = [" + errors + "]");
                        }
                    });
                } else if (auditResult == RPResult.AUDIT_FAIL) {
                    // 认证不通过。建议接入方调用实人认证服务端接口DescribeVerifyResult来获取最终的认证状态，并以此为准进行业务上的判断和处理。
                    // do something
                } else if (auditResult == RPResult.AUDIT_NOT) {
                    // 未认证，具体原因可通过code来区分（code取值参见错误码说明），通常是用户主动退出或者姓名身份证号实名校验不匹配等原因，导致未完成认证流程。
                    // do something
                }
            }
        });
    }

    private void pwdLogin() {
        Log.d(TAG, "login() called");
        CertificateLogin.getInstance(this).pwdLogin(username, password, new PwdLoginService() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "onSuccess() called with: result = [" + result + "]");
                //{"success":true,"code":"200","message":null,"requestId":"4645D1E3-B5E9-4635-A568-AEAF46793241","data":{"accessTokenDto":{"errorDescription":null,"error":null,"errors":[],"accessToken":"2f308def-cc27-49b4-b62a-93355cc470f9","tokenType":"bearer","refreshToken":"3ef89139-7884-4d44-908b-8728e825cccc","scope":"read","expiresIn":32528,"includeError":false},"secret":"inputYourCodeHere",...,"serverTime":1588056647852,"admin":false,"authKeyDto":null,"needModifyPwd":false,"expiredReminder":true,"idToken":null}}
                JSONObject jsonObject = (JSONObject) JSONObject.parse(result);
                Boolean success = jsonObject.getBoolean("success");
                if (success) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    JSONObject accessTokenDto = data.getJSONObject("accessTokenDto");
                    String refreshTokenTmp = accessTokenDto.getString("refreshToken");
                    String accessTokenTmp = accessTokenDto.getString("accessToken");
                    accessToken = accessTokenTmp;
                    refreshToken = refreshTokenTmp;
                    /*CertificateLogin.getInstance(MainActivity.this).refreshUserAccessToken(refreshToken, new RefreshUserAccessTokenService() {
                        @Override
                        public void onSuccess(String result) {
                            //{"success":true,"code":"200","message":null,"requestId":null,"data":{"accessTokenDto":{"errorDescription":null,"error":null,"errors":[],"accessToken":"e586902a-9885-45dd-8771-f1f230b3ce60","tokenType":"bearer","refreshToken":"05feb7ef-ff59-47fa-89ce-77c3654f6aaf","scope":"read","expiresIn":43199,"includeError":false}}}
                            Log.d(TAG, "onSuccess() called with: result = [" + result + "]");
                        }

                        @Override
                        public void onFail(String errorCode, String errors) {
                            Log.d(TAG, "onFail() called with: errorCode = [" + errorCode + "], errors = [" + errors + "]");
                        }
                    });*/
                }

                showMessage("onSuccess() called with: result = [" + result + "]");
            }

            @Override
            public void onFail(String errorCode, String errors) {
                Log.d(TAG, "onFail() called with: errorCode = [" + errorCode + "], errors = [" + errors + "]");
                showMessage("onFail() called with: errorCode = [" + errorCode + "], errors = [" + errors + "]");
            }
        });

    }

    private void certLogin() {
        Log.d(TAG, "login() called");
        CertificateLogin.getInstance(this).certLogin(new CertLoginService() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "onSuccess() called with: result = [" + result + "]");
                showMessage("onSuccess() called with: result = [" + result + "]");
                //{"success":true,"code":"200","message":null,"requestId":"8AD0646D-8850-4958-9A2D-3A6D8DB8476D","data":{"accessTokenDto":{"errorDescription":null,"error":null,"errors":[],"accessToken":"2f308def-cc27-49b4-b62a-93355cc470f9","tokenType":"bearer","refreshToken":"3ef89139-7884-4d44-908b-8728e825cccc","scope":"read","expiresIn":43199,"includeError":false},"secret":"inputYourCodeHere",...,"serverTime":1588045976209,"admin":false,"authKeyDto":null,"needModifyPwd":false,"expiredReminder":false,"idToken":null}}
            }

            @Override
            public void onFail(String errorCode, String errors) {
                Log.d(TAG, "onFail() called with: errorCode = [" + errorCode + "], errors = [" + errors + "]");
                showMessage("onFail() called with: errorCode = [" + errorCode + "], errors = [" + errors + "]");
            }
        });

    }

    @PermissionGranted(WRITE_EXTERNAL_STORAGE)
    private void downloadCert() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (PermissionsManager.hasPermissions(this, perms)) {
            CertificateLogin.getInstance(this).downloadCert(username, password, new DownloadCertService() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, "onSuccess() called with: result = [" + result + "]");
                    showMessage("onSuccess() called with: result = [" + result + "]");
                }

                @Override
                public void onFail(String errorCode, String errors) {
                    Log.d(TAG, "onFail() called with: errorCode = [" + errorCode + "], errors = [" + errors + "]");
                    showMessage("onFail() called with: errorCode = [" + errorCode + "], errors = [" + errors + "]");
                }
            });
        } else {
            PermissionsManager.requestPermissions(this, WRITE_EXTERNAL_STORAGE, perms);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> permissions) {
        Log.d(TAG, "onPermissionsGranted() called with: requestCode = [" + requestCode + "], permissions = [" + permissions + "]");
        showMessage("onPermissionsGranted() called with: requestCode = [" + requestCode + "], permissions = [" + permissions + "]");
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> permissions) {
        Log.d(TAG, "onPermissionsDenied() called with: requestCode = [" + requestCode + "], permissions = [" + permissions + "]");
        showMessage("onPermissionsDenied() called with: requestCode = [" + requestCode + "], permissions = [" + permissions + "]");
    }

    private void showMessage(String result) {
        final String resultTmp = result;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, resultTmp + "", Toast.LENGTH_LONG).show();
            }
        });
    }
}
