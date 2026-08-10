package com.anti_captcha.Api;

import com.anti_captcha.AnticaptchaBase;
import com.anti_captcha.ApiResponse.TaskResultResponse;
import com.anti_captcha.Helper.DebugHelper;
import com.anti_captcha.IAnticaptchaTaskProtocol;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

public class TurnstileProxyless extends AnticaptchaBase implements IAnticaptchaTaskProtocol {
    private String websiteUrl;
    private String websiteKey;
    private String action;
    private String cData;
    private String chlPageData;

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

    public void setAction(String action) {
        this.action = action;
    }

    public void setCData(String cData) {
        this.cData = cData;
    }

    public void setChlPageData(String chlPageData) {
        this.chlPageData = chlPageData;
    }

    @Override
    public JSONObject getPostData() {
        JSONObject postData = new JSONObject();

        try {
            postData.put("type", "TurnstileTaskProxyless");
            postData.put("websiteURL", websiteUrl);
            postData.put("websiteKey", websiteKey);
            postData.put("action", action);
            postData.put("cData", cData);
            postData.put("chlPageData", chlPageData);
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
