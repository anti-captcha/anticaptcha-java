package com.anti_captcha;

import com.anti_captcha.ApiResponse.BalanceResponse;
import com.anti_captcha.ApiResponse.CreateTaskResponse;
import com.anti_captcha.ApiResponse.TaskResultResponse;
import com.anti_captcha.Helper.DebugHelper;
import com.anti_captcha.Helper.HttpHelper;
import com.anti_captcha.Helper.JsonHelper;
import com.anti_captcha.Helper.StringHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public abstract class AnticaptchaBase {
    protected TaskResultResponse taskInfo;
    private final String host = "api.anti-captcha.com";

    /** Timeout of a single HTTP call to the API, in milliseconds. */
    private int connectionTimeout = 30_000;
    private final SchemeType scheme = SchemeType.HTTPS;
    private String errorMessage;
    private Integer taskId;
    private String clientKey;

    /**
     *  Specify softId to earn 10% commission with your app.
     *  Get your softId here:
     *  <a href="https://anti-captcha.com/clients/tools/devcenter">https://anti-captcha.com/clients/tools/devcenter</a>
     */
    private Integer softId;

    /**
     * Timeout of a single HTTP call to the API, in milliseconds. Does not limit
     * how long waitForResult() polls.
     */
    public void setConnectionTimeout(int milliseconds) {
        this.connectionTimeout = milliseconds;
    }

    public enum ProxyTypeOption {
        HTTP,
        SOCKS4,
        SOCKS5
    }

    private JSONObject jsonPostRequest(ApiMethod methodName, JSONObject jsonPostData) {

        String url = scheme + "://" + host + "/" + StringHelper.toCamelCase(methodName.toString());
        String rawJson;

        try {
            rawJson = HttpHelper.post(url, JsonHelper.asString(jsonPostData), connectionTimeout);
        } catch (InterruptedException e) {
            // never swallow an interrupt
            Thread.currentThread().interrupt();
            errorMessage = "interrupted while waiting for the API";
            DebugHelper.out(errorMessage, DebugHelper.Type.ERROR);

            return null;
        } catch (Exception e) {
            errorMessage = e.getMessage();
            DebugHelper.out("HTTP problem: " + e.getMessage(), DebugHelper.Type.ERROR);

            return null;
        }

        try {
            return new JSONObject(rawJson);
        } catch (Exception e) {
            errorMessage = e.getMessage();
            DebugHelper.out("JSON parse problem: " + e.getMessage(), DebugHelper.Type.ERROR);

            return null;
        }
    }

    public abstract JSONObject getPostData();

    @SuppressWarnings("WeakerAccess")
    public Boolean createTask() {
        JSONObject taskJson = getPostData();

        if (taskJson == null) {
            DebugHelper.out("JSON error", DebugHelper.Type.ERROR);

            return false;
        }

        JSONObject jsonPostData = new JSONObject();

        try {
            jsonPostData.put("clientKey", clientKey);
            jsonPostData.put("softId", softId);
            jsonPostData.put("task", taskJson);
        } catch (JSONException e) {
            errorMessage = e.getMessage();
            DebugHelper.out("JSON compilation error: " + e.getMessage(), DebugHelper.Type.ERROR);

            return false;
        }

        DebugHelper.out("Connecting to " + host, DebugHelper.Type.INFO);
        JSONObject postResult = jsonPostRequest(ApiMethod.CREATE_TASK, jsonPostData);

        if (postResult == null) {
            DebugHelper.out("API error", DebugHelper.Type.ERROR);

            return false;
        }

        CreateTaskResponse response = new CreateTaskResponse(postResult);

        if (response.getErrorId() == null || !response.getErrorId().equals(0)) {
            errorMessage = response.getErrorDescription();
            String errorId = response.getErrorId() == null ? "" : response.getErrorId().toString();

            DebugHelper.out(
                    "API error " + errorId + ": " + response.getErrorDescription(),
                    DebugHelper.Type.ERROR
            );

            return false;
        }

        if (response.getTaskId() == null) {
            DebugHelper.jsonFieldParseError("taskId", postResult);

            return false;
        }

        taskId = response.getTaskId();
        DebugHelper.out("Task ID: " + taskId, DebugHelper.Type.SUCCESS);

        return true;
    }

    @SuppressWarnings("WeakerAccess")
    public Double getBalance() {
        JSONObject jsonPostData = new JSONObject();

        try {
            jsonPostData.put("clientKey", clientKey);
        } catch (JSONException e) {
            errorMessage = e.getMessage();
            DebugHelper.out("JSON compilation error: " + e.getMessage(), DebugHelper.Type.ERROR);

            return null;
        }

        JSONObject postResult = jsonPostRequest(ApiMethod.GET_BALANCE, jsonPostData);

        if (postResult == null) {
            DebugHelper.out("API error", DebugHelper.Type.ERROR);

            return null;
        }

        BalanceResponse balanceResponse = new BalanceResponse(postResult);

        if (balanceResponse.getErrorId() == null || !balanceResponse.getErrorId().equals(0)) {
            errorMessage = balanceResponse.getErrorDescription();
            String errorId = balanceResponse.getErrorId() == null ? "" : balanceResponse.getErrorId().toString();

            DebugHelper.out(
                    "API error " + errorId + ": " + balanceResponse.getErrorDescription(),
                    DebugHelper.Type.ERROR
            );

            return null;
        }

        return balanceResponse.getBalance();
    }

    @SuppressWarnings("WeakerAccess")
    public Double getCreditsBalance() {
        JSONObject jsonPostData = new JSONObject();

        try {
            jsonPostData.put("clientKey", clientKey);
        } catch (JSONException e) {
            errorMessage = e.getMessage();
            DebugHelper.out("JSON compilation error: " + e.getMessage(), DebugHelper.Type.ERROR);

            return null;
        }

        JSONObject postResult = jsonPostRequest(ApiMethod.GET_BALANCE, jsonPostData);

        if (postResult == null) {
            DebugHelper.out("API error", DebugHelper.Type.ERROR);

            return null;
        }

        BalanceResponse balanceResponse = new BalanceResponse(postResult);

        if (balanceResponse.getErrorId() == null || !balanceResponse.getErrorId().equals(0)) {
            errorMessage = balanceResponse.getErrorDescription();
            String errorId = balanceResponse.getErrorId() == null ? "" : balanceResponse.getErrorId().toString();

            DebugHelper.out(
                    "API error " + errorId + ": " + balanceResponse.getErrorDescription(),
                    DebugHelper.Type.ERROR
            );

            return null;
        }

        return balanceResponse.getCreditsBalance();
    }

    @SuppressWarnings("WeakerAccess")
    public Boolean waitForResult(Integer maxSeconds, Integer currentSecond) throws InterruptedException {
        if (currentSecond >= maxSeconds) {
            DebugHelper.out("Time's out.", DebugHelper.Type.ERROR);

            return false;
        }

        if (currentSecond.equals(0)) {
            DebugHelper.out("Waiting for 3 seconds...", DebugHelper.Type.INFO);
            TimeUnit.SECONDS.sleep(3);
        } else {
            TimeUnit.SECONDS.sleep(1);
        }

        DebugHelper.out("Requesting the task status", DebugHelper.Type.INFO);
        JSONObject jsonPostData = new JSONObject();

        try {
            jsonPostData.put("clientKey", clientKey);
            jsonPostData.put("taskId", taskId);
        } catch (JSONException e) {
            errorMessage = e.getMessage();
            DebugHelper.out("JSON compilation error: " + e.getMessage(), DebugHelper.Type.ERROR);

            return false;
        }

        JSONObject postResult = jsonPostRequest(ApiMethod.GET_TASK_RESULT, jsonPostData);

        if (postResult == null) {
            DebugHelper.out("API error", DebugHelper.Type.ERROR);

            return false;
        }

        taskInfo = new TaskResultResponse(postResult);

        if (taskInfo.getErrorId() == null || !taskInfo.getErrorId().equals(0)) {
            errorMessage = taskInfo.getErrorDescription();
            String errorId = taskInfo.getErrorId() == null ? "" : taskInfo.getErrorId().toString();

            DebugHelper.out(
                    "API error " + errorId + ": " + errorMessage,
                    DebugHelper.Type.ERROR
            );

            return false;
        }

        TaskResultResponse.StatusType status = taskInfo.getStatus();
        TaskResultResponse.SolutionData solution = taskInfo.getSolution();

        if (status != null && status.equals(TaskResultResponse.StatusType.PROCESSING)) {
            DebugHelper.out("The task is still processing...", DebugHelper.Type.INFO);

            return waitForResult(maxSeconds, currentSecond + 1);
        } else if (status != null && status.equals(TaskResultResponse.StatusType.READY)) {
            if (solution.getGRecaptchaResponse() == null &&
                solution.getText() == null &&
                solution.getToken() == null &&
                solution.getChallenge() == null &&
                solution.getSeccode() == null &&
                solution.getValidate() == null &&
                solution.getCaptchaId() == null &&
                solution.getCookies() == null) {
                DebugHelper.out("Got no 'solution' field from API", DebugHelper.Type.ERROR);

                return false;
            }

            DebugHelper.out("The task is complete!", DebugHelper.Type.SUCCESS);

            return true;
        }

        errorMessage = "An unknown API status, please update your software";
        DebugHelper.out(errorMessage, DebugHelper.Type.ERROR);

        return false;
    }

    @SuppressWarnings("WeakerAccess")
    public Boolean waitForResult() throws InterruptedException {
        return waitForResult(120, 0);
    }

    public Boolean waitForResult(Integer maxSeconds) throws InterruptedException {
        return waitForResult(maxSeconds, 0);
    }

    @SuppressWarnings("WeakerAccess")
    public void setClientKey(String clientKey_) {
        clientKey = clientKey_;
    }

    public Integer getSoftId() {
        return softId;
    }

    /**
     *  Specify softId to earn 10% commission with your app.
     *  Get your softId here:
     *  <a href="https://anti-captcha.com/clients/tools/devcenter">https://anti-captcha.com/clients/tools/devcenter</a>
     */
    public void setSoftId(Integer softId_) {
        softId = softId_;
    }

    @SuppressWarnings("WeakerAccess")
    public String getErrorMessage() {
        return errorMessage == null ? "no error message" : errorMessage;
    }

    /**
     * Id of the task created by createTask().
     */
    public Integer getTaskId() {
        return taskId;
    }

    /**
     * Reports the solved image captcha as answered incorrectly.
     *
     * @return true when the API accepted the report
     */
    public boolean reportIncorrectImageCaptcha() {
        return report(ApiMethod.REPORT_INCORRECT_IMAGE_CAPTCHA);
    }

    /**
     * Reports the solved Recaptcha as answered incorrectly.
     */
    public boolean reportIncorrectRecaptcha() {
        return report(ApiMethod.REPORT_INCORRECT_RECAPTCHA);
    }

    /**
     * Reports the solved Recaptcha as answered correctly. Helps us pick better workers.
     */
    public boolean reportCorrectRecaptcha() {
        return report(ApiMethod.REPORT_CORRECT_RECAPTCHA);
    }

    /**
     * Reports the solved hCaptcha as answered incorrectly.
     */
    public boolean reportIncorrectHcaptcha() {
        return report(ApiMethod.REPORT_INCORRECT_HCAPTCHA);
    }

    private boolean report(ApiMethod method) {
        if (taskId == null) {
            DebugHelper.out("There is no solved task to report", DebugHelper.Type.ERROR);

            return false;
        }

        JSONObject jsonPostData = new JSONObject();

        try {
            jsonPostData.put("clientKey", clientKey);
            jsonPostData.put("taskId", taskId);
        } catch (JSONException e) {
            errorMessage = e.getMessage();
            DebugHelper.out("JSON compilation error: " + e.getMessage(), DebugHelper.Type.ERROR);

            return false;
        }

        JSONObject postResult = jsonPostRequest(method, jsonPostData);

        if (postResult == null) {
            return false;
        }

        Integer reportErrorId = JsonHelper.extractInt(postResult, "errorId");

        if (reportErrorId == null || !reportErrorId.equals(0)) {
            errorMessage = JsonHelper.extractStr(postResult, "errorDescription", true);
            DebugHelper.out("API error: " + errorMessage, DebugHelper.Type.ERROR);

            return false;
        }

        return true;
    }

    private enum SchemeType {
        HTTP,
        HTTPS
    }

    private enum ApiMethod {
        CREATE_TASK,
        GET_TASK_RESULT,
        GET_BALANCE,
        REPORT_INCORRECT_IMAGE_CAPTCHA,
        REPORT_INCORRECT_RECAPTCHA,
        REPORT_CORRECT_RECAPTCHA,
        REPORT_INCORRECT_HCAPTCHA
    }
}
