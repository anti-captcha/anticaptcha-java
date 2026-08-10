package com.anti_captcha.Api;

import com.anti_captcha.AnticaptchaBase;
import com.anti_captcha.ApiResponse.TaskResultResponse;
import com.anti_captcha.Helper.DebugHelper;
import com.anti_captcha.IAnticaptchaTaskProtocol;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

public class GeeTestProxyless extends AnticaptchaBase implements IAnticaptchaTaskProtocol {
    private String websiteUrl;
    private String websiteKey;
    private String websiteChallenge;
    private String geetestApiServerSubdomain;
    private String geetestLib;

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public String getWebsiteKey() {
        return websiteKey;
    }

    public String getWebsiteChallenge() {
        return websiteChallenge;
    }

    public String getGeetestApiServerSubdomain() {
        return geetestApiServerSubdomain;
    }

    public void setGeetestApiServerSubdomain(String geetestApiServerSubdomain) {
        this.geetestApiServerSubdomain = geetestApiServerSubdomain;
    }

    public void setGeetestLib(String geetestLib) {
        this.geetestLib = geetestLib;
    }

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

    public void setWebsiteKey(String websiteKey) {
        this.websiteKey = websiteKey;
    }

    public void setWebsiteChallenge(String websiteChallenge) {
        this.websiteChallenge = websiteChallenge;
    }

    @Override
    public JSONObject getPostData() {
        JSONObject postData = new JSONObject();

        try {
            postData.put("type", "GeeTestTaskProxyless");
            postData.put("websiteURL", websiteUrl);
            postData.put("gt", websiteKey);
            postData.put("challenge", websiteChallenge);

            if (geetestApiServerSubdomain != null && geetestApiServerSubdomain.length() > 0) {
                postData.put("geetestApiServerSubdomain", geetestApiServerSubdomain);
            }
            if (geetestLib != null) {
                postData.put("geetestGetLib", geetestLib);
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
