package com.anti_captcha.Api;

import com.anti_captcha.AnticaptchaBase;
import com.anti_captcha.ApiResponse.TaskResultResponse;
import com.anti_captcha.Helper.DebugHelper;
import com.anti_captcha.IAnticaptchaTaskProtocol;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

public class AmazonProxyless extends AnticaptchaBase implements IAnticaptchaTaskProtocol {
    private URL websiteUrl;
    private String websiteKey;
    private String wafType;
    private String iv;
    private String context;
    private String captchaScript;
    private String challengeScript;
    private String jsapiScript;

    public void setWebsiteUrl(URL websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public void setWebsiteKey(String websiteKey) {
        this.websiteKey = websiteKey;
    }

    public void setWafType(String wafType) {
        this.wafType = wafType;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public void setCaptchaScript(String captchaScript) {
        this.captchaScript = captchaScript;
    }

    public void setChallengeScript(String challengeScript) {
        this.challengeScript = challengeScript;
    }

    public void setJsapiScript(String jsapiScript) {
        this.jsapiScript = jsapiScript;
    }

    @Override
    public JSONObject getPostData() {
        JSONObject postData = new JSONObject();

        try {
            postData.put("type", "AmazonTaskProxyless");
            postData.put("websiteURL", websiteUrl.toString());
            postData.put("websiteKey", websiteKey);
            postData.put("wafType", wafType);
            postData.put("iv", iv);
            postData.put("context", context);
            postData.put("jsapiScript", jsapiScript);
            if (captchaScript != null && !captchaScript.isEmpty()) {
                postData.put("captchaScript", captchaScript);
            }
            if (challengeScript != null && !challengeScript.isEmpty()) {
                postData.put("challengeScript", challengeScript);
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
