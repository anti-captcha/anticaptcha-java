package com.anti_captcha.Api;

import com.anti_captcha.Helper.DebugHelper;
import org.json.JSONException;
import org.json.JSONObject;

public class Altcha extends AltchaProxyless {
    private String proxyLogin;
    private String proxyPassword;
    private Integer proxyPort;
    private ProxyTypeOption proxyType;
    private String proxyAddress;

    public void setProxyLogin(String proxyLogin) {
        this.proxyLogin = proxyLogin;
    }

    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    public void setProxyPort(Integer proxyPort) {
        this.proxyPort = proxyPort;
    }

    public void setProxyType(ProxyTypeOption proxyType) {
        this.proxyType = proxyType;
    }

    public void setProxyAddress(String proxyAddress) {
        this.proxyAddress = proxyAddress;
    }

    @Override
    public JSONObject getPostData() {
        JSONObject postData = super.getPostData();

        if (proxyType == null || proxyPort == null || proxyPort < 1 || proxyPort > 65535
                || proxyAddress == null || proxyAddress.length() == 0) {
            DebugHelper.out("Proxy data is incorrect!", DebugHelper.Type.ERROR);

            return null;
        }

        try {
            postData.put("type", "AltchaTask");
            postData.put("proxyType", proxyType.toString().toLowerCase());
            postData.put("proxyAddress", proxyAddress);
            postData.put("proxyPort", proxyPort);
            postData.put("proxyLogin", proxyLogin);
            postData.put("proxyPassword", proxyPassword);
        } catch (JSONException e) {
            DebugHelper.out("JSON compilation error: " + e.getMessage(), DebugHelper.Type.ERROR);

            return null;
        }

        return postData;
    }
}
