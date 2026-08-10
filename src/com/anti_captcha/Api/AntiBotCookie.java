package com.anti_captcha.Api;

import com.anti_captcha.AnticaptchaBase;
import com.anti_captcha.ApiResponse.TaskResultResponse;
import com.anti_captcha.Helper.DebugHelper;
import com.anti_captcha.IAnticaptchaTaskProtocol;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

/**
 * Makes a worker open the target page through your proxy and returns the anti-bot
 * cookies, localStorage and browser fingerprint collected there, so you can reuse
 * them in your own requests.
 *
 * <p>The proxy is required: the cookies are only valid for the IP address they were
 * issued to, so it has to be the same proxy you use later. This task type takes no
 * proxyType, only http proxies are supported.
 *
 * @see <a href="https://anti-captcha.com/apidoc/task-types/AntiBotCookieTask">AntiBotCookieTask</a>
 */
public class AntiBotCookie extends AnticaptchaBase implements IAnticaptchaTaskProtocol {
    private String websiteUrl;
    private String proxyAddress;
    private Integer proxyPort;
    private String proxyLogin;
    private String proxyPassword;

    public void setWebsiteUrl(URL websiteUrl) {
        this.websiteUrl = websiteUrl.toString();
    }

    /**
     * Convenience overload: takes the address as a plain string, so you do not
     * have to build a URL and catch MalformedURLException yourself.
     */
    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public void setProxyAddress(String proxyAddress) {
        this.proxyAddress = proxyAddress;
    }

    public void setProxyPort(Integer proxyPort) {
        this.proxyPort = proxyPort;
    }

    public void setProxyLogin(String proxyLogin) {
        this.proxyLogin = proxyLogin;
    }

    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    @Override
    public JSONObject getPostData() {
        if (proxyAddress == null || proxyAddress.isEmpty()
                || proxyPort == null || proxyPort < 1 || proxyPort > 65535) {
            DebugHelper.out("Proxy data is incorrect!", DebugHelper.Type.ERROR);

            return null;
        }

        JSONObject postData = new JSONObject();

        try {
            postData.put("type", "AntiBotCookieTask");
            postData.put("websiteURL", websiteUrl);
            postData.put("proxyAddress", proxyAddress);
            postData.put("proxyPort", proxyPort);

            if (proxyLogin != null && !proxyLogin.isEmpty()) {
                postData.put("proxyLogin", proxyLogin);
                postData.put("proxyPassword", proxyPassword);
            }
        } catch (JSONException e) {
            DebugHelper.out("JSON compilation error: " + e.getMessage(), DebugHelper.Type.ERROR);

            return null;
        }

        return postData;
    }

    @Override
    public TaskResultResponse.SolutionData getTaskSolution() {
        return taskInfo.getSolution();
    }
}
