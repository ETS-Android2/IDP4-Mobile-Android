package com.idsmanager.idp4sdkdemo;

import android.app.Application;

import com.alibaba.security.realidentity.RPVerify;
import com.idsmanager.idp4zerotrustlibrary.CertificateLogin;

/**
 * @author summer
 * @DATE 2020/4/27
 * @Describe
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        String appKey = "0030**********************************5t4mh";
        String appSecret = "xALF**********************************GGy9";
        String enterpriseId = "*****";
        String host = "http://**************/";
        CertificateLogin.getInstance(this)
                .appKey(appKey)
                .appSecret(appSecret)
                .enterpriseId(enterpriseId)
                .host(host);
        //初始化实人认证SDK。
        RPVerify.init(getApplicationContext());
    }
}
