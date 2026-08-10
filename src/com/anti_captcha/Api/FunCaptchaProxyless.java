package com.anti_captcha.Api;

import com.anti_captcha.AnticaptchaBase;
import com.anti_captcha.ApiResponse.TaskResultResponse;
import com.anti_captcha.Helper.DebugHelper;
import com.anti_captcha.IAnticaptchaTaskProtocol;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

public class FunCaptchaProxyless extends AnticaptchaBase implements IAnticaptchaTaskProtocol {

    private String websiteUrl;
    private String websitePublicKey;
    private String apiSubdomain;
    private String dataBlob;


    @Override
    public JSONObject getPostData() {
        JSONObject postData = new JSONObject();


        try {
            postData.put("type", "FunCaptchaTaskProxyless");
            postData.put("websiteURL", websiteUrl);
            postData.put("websitePublicKey", websitePublicKey);
            if (this.apiSubdomain != null) {
                postData.put("funcaptchaApiJSSubdomain", this.apiSubdomain);
            }
            if (this.dataBlob != null) {
                postData.put("data", this.dataBlob);
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

    public void setWebsitePublicKey(String websitePublicKey) {
        this.websitePublicKey = websitePublicKey;
    }

    public void setApiSubdomain(String apiSubdomain) {
        this.apiSubdomain = apiSubdomain;
    }

    public void setDataBlob(String dataBlob) {
        this.dataBlob = dataBlob;
    }
}
