package com.anti_captcha.Api;

import com.anti_captcha.AnticaptchaBase;
import com.anti_captcha.ApiResponse.TaskResultResponse;
import com.anti_captcha.Helper.DebugHelper;
import com.anti_captcha.Helper.StringHelper;
import com.anti_captcha.IAnticaptchaTaskProtocol;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;

/**
 * Image where the worker clicks on objects and returns their coordinates.
 *
 * @see <a href="https://anti-captcha.com/apidoc/task-types/ImageToCoordinatesTask">ImageToCoordinatesTask</a>
 */
public class ImageToCoordinates extends AnticaptchaBase implements IAnticaptchaTaskProtocol {
    private String bodyBase64;
    private String mode = "points";
    private String comment;
    private String websiteUrl;

    /**
     * Captcha image encoded in base64.
     */
    public void setBodyBase64(String bodyBase64) {
        this.bodyBase64 = bodyBase64;
    }

    /**
     * Path to the captcha image file. Fills the base64 body for you.
     */
    public void setFilePath(String filePath) {
        File file = new File(filePath);

        if (!file.exists() || file.isDirectory()) {
            DebugHelper.out("File " + filePath + " not found", DebugHelper.Type.ERROR);

            return;
        }

        bodyBase64 = StringHelper.imageFileToBase64String(filePath);
    }

    /**
     * "points" to get single click coordinates, "rectangles" to get selection boxes.
     */
    public void setMode(String mode) {
        this.mode = mode;
    }

    /**
     * Instruction for the worker, for example "select objects in the specified order".
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Optional, lets you group the dashboard statistics by website.
     */
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

    @Override
    public JSONObject getPostData() {
        if (bodyBase64 == null || bodyBase64.isEmpty()) {
            DebugHelper.out("Captcha image is not set, use setFilePath() or setBodyBase64()",
                    DebugHelper.Type.ERROR);

            return null;
        }

        JSONObject postData = new JSONObject();

        try {
            postData.put("type", "ImageToCoordinatesTask");
            postData.put("body", bodyBase64);
            postData.put("mode", mode);

            if (comment != null) {
                postData.put("comment", comment);
            }

            if (websiteUrl != null) {
                postData.put("websiteURL", websiteUrl);
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
