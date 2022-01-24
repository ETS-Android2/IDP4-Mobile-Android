package com.idsmanager.idp4sdkdemo.service.dto;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * @author summer
 * @DATE 2019/8/2
 * @Describe
 */
public class UserLoginDto implements Parcelable {
    private String appKey;
    private String appSecret;
    private String username;
    private String password;
    private String deviceId;
    private String deviceName;
    private String type;
    private String enterpriseId;
    private String iosUuid;
    private String macAddress;
    private String longitude;
    private String latitude;

    public UserLoginDto() {
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getIosUuid() {
        return iosUuid;
    }

    public void setIosUuid(String iosUuid) {
        this.iosUuid = iosUuid;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.appKey);
        dest.writeString(this.appSecret);
        dest.writeString(this.username);
        dest.writeString(this.password);
        dest.writeString(this.deviceId);
        dest.writeString(this.deviceName);
        dest.writeString(this.type);
        dest.writeString(this.enterpriseId);
        dest.writeString(this.iosUuid);
        dest.writeString(this.macAddress);
        dest.writeString(this.longitude);
        dest.writeString(this.latitude);
    }

    protected UserLoginDto(Parcel in) {
        this.appKey = in.readString();
        this.appSecret = in.readString();
        this.username = in.readString();
        this.password = in.readString();
        this.deviceId = in.readString();
        this.deviceName = in.readString();
        this.type = in.readString();
        this.enterpriseId = in.readString();
        this.iosUuid = in.readString();
        this.macAddress = in.readString();
        this.longitude = in.readString();
        this.latitude = in.readString();
    }

    public static final Parcelable.Creator<UserLoginDto> CREATOR = new Parcelable.Creator<UserLoginDto>() {
        @Override
        public UserLoginDto createFromParcel(Parcel source) {
            return new UserLoginDto(source);
        }

        @Override
        public UserLoginDto[] newArray(int size) {
            return new UserLoginDto[size];
        }
    };
}
