package com.anti_captcha.Api;

import com.anti_captcha.AnticaptchaBase;
import com.anti_captcha.ApiResponse.TaskResultResponse;
import com.anti_captcha.Helper.DebugHelper;
import com.anti_captcha.IAnticaptchaTaskProtocol;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

public class GeeTestV4Proxyless extends AnticaptchaBase implements IAnticaptchaTaskProtocol {
    private String websiteUrl;
    private String websiteKey;
    private String geetestApiServerSubdomain;
    private JSONObject initParameters;

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public String getWebsiteKey() {
        return websiteKey;
    }

    public String getGeetestApiServerSubdomain() {
        return geetestApiServerSubdomain;
    }

    public void setGeetestApiServerSubdomain(String geetestApiServerSubdomain) {
        this.geetestApiServerSubdomain = geetestApiServerSubdomain;
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

    public void setInitParameters(JSONObject value) {
        this.initParameters = value;
    }

    @Override
    public JSONObject getPostData() {
        JSONObject postData = new JSONObject();

        try {
            postData.put("type", "GeeTestTaskProxyless");
            postData.put("websiteURL", websiteUrl);
            postData.put("gt", websiteKey);
            postData.put("version", 4);
            if (initParameters != null) postData.put("initParameters", initParameters);

            if (geetestApiServerSubdomain != null && !geetestApiServerSubdomain.isEmpty()) {
                postData.put("geetestApiServerSubdomain", geetestApiServerSubdomain);
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
