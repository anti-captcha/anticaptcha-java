package com.anti_captcha.Api;

import com.anti_captcha.AnticaptchaBase;
import com.anti_captcha.ApiResponse.TaskResultResponse;
import com.anti_captcha.Helper.DebugHelper;
import com.anti_captcha.IAnticaptchaTaskProtocol;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

public class AltchaProxyless extends AnticaptchaBase implements IAnticaptchaTaskProtocol {
    private String websiteUrl;
    private String challengeURL;

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

    public void setChallengeURL(String challengeURL) {
        this.challengeURL = challengeURL;
    }

    @Override
    public JSONObject getPostData() {
        JSONObject postData = new JSONObject();

        try {
            postData.put("type", "AltchaTaskProxyless");
            postData.put("websiteURL", websiteUrl);
            postData.put("challengeURL", challengeURL);
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
