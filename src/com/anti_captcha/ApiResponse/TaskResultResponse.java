package com.anti_captcha.ApiResponse;

import com.anti_captcha.Helper.DebugHelper;
import com.anti_captcha.Helper.JsonHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskResultResponse {
    private Integer errorId;
    private String errorCode;
    private String errorDescription;
    private StatusType status;
    private Double cost;
    private String ip;

    /**
     * ﻿Task create time in UTC
     */
    private ZonedDateTime createTime;

    /**
     * ﻿Task end time in UTC
     */
    private ZonedDateTime endTime;
    private Integer solveCount;
    private SolutionData solution;

    public TaskResultResponse(JSONObject json) {
        errorId = JsonHelper.extractInt(json, "errorId");

        if (errorId != null) {
            if (errorId.equals(0)) {
                status = parseStatus(JsonHelper.extractStr(json, "status"));

                if (status.equals(StatusType.READY)) {
                    cost = JsonHelper.extractDouble(json, "cost");
                    ip = JsonHelper.extractStr(json, "ip", true);
                    solveCount = JsonHelper.extractInt(json, "solveCount", true);
                    createTime = unixTimeStampToDateTime(JsonHelper.extractDouble(json, "createTime"));
                    endTime = unixTimeStampToDateTime(JsonHelper.extractDouble(json, "endTime"));

                    solution = new SolutionData();
                    solution.gRecaptchaResponse = JsonHelper.extractStr(json, "solution", "gRecaptchaResponse", true);
                    solution.gRecaptchaResponseMd5 = JsonHelper.extractStr(json, "solution", "gRecaptchaResponseMd5", true);
                    solution.text = JsonHelper.extractStr(json, "solution", "text", true);
                    solution.url = JsonHelper.extractStr(json, "solution", "url", true);
                    solution.token = JsonHelper.extractStr(json, "solution", "token", true);
                    solution.challenge = JsonHelper.extractStr(json, "solution", "challenge", true);
                    solution.seccode = JsonHelper.extractStr(json, "solution", "seccode", true);
                    solution.validate = JsonHelper.extractStr(json, "solution", "validate", true);
                    solution.cookies = JsonHelper.extractJSONObject(json,"solution",  "cookies");
                    solution.localStorage = JsonHelper.extractJSONObject(json, "solution", "localStorage");
                    solution.fingerprint = JsonHelper.extractJSONObject(json,"solution",  "fingerprint");
                    solution.domain = JsonHelper.extractStr(json,"solution",  "domain", true);
                    solution.captcha_id = JsonHelper.extractStr(json,"solution",  "captcha_id", true);
                    solution.lot_number = JsonHelper.extractStr(json,"solution",  "lot_number", true);
                    solution.pass_token = JsonHelper.extractStr(json,"solution",  "pass_token", true);
                    solution.gen_time = JsonHelper.extractInt(json,"solution",  "gen_time", true);
                    solution.captcha_output = JsonHelper.extractStr(json,"solution",  "captcha_output", true);
                    solution.respKey = JsonHelper.extractStr(json, "solution", "respKey", true);
                    solution.userAgent = JsonHelper.extractStr(json, "solution", "userAgent", true);
                    solution.coordinates = JsonHelper.extractJSONArray(json, "solution", "coordinates");
                    solution.lastRequestHeaders = JsonHelper.extractJSONArray(json, "solution", "lastRequestHeaders");


                    if (solution.gRecaptchaResponse == null &&
                        solution.text == null &&
                        solution.token == null &&
                        solution.challenge == null &&
                        solution.seccode == null &&
                        solution.validate == null &&
                        solution.cookies == null &&
                        solution.coordinates == null &&
                        solution.captcha_id == null &&
                        solution.captcha_output == null) {
                        DebugHelper.out("2 Got no 'solution' field from API", DebugHelper.Type.ERROR);
                        DebugHelper.out(json.toString(), DebugHelper.Type.ERROR);

                    }
                }
            } else {
                errorCode = JsonHelper.extractStr(json, "errorCode");
                errorDescription = JsonHelper.extractStr(json, "errorDescription");

                DebugHelper.out(errorDescription, DebugHelper.Type.ERROR);
            }
        } else {
            DebugHelper.out("Unknown error", DebugHelper.Type.ERROR);
        }
    }

    private static ZonedDateTime unixTimeStampToDateTime(Double unixTimeStamp) {
        if (unixTimeStamp == null) {
            return null;
        }

        ZonedDateTime epochStart = ZonedDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"));

        return epochStart.plusSeconds((long) (double) unixTimeStamp);
    }

    public Integer getErrorId() {
        return errorId;
    }

    public String getErrorDescription() {
        return errorDescription == null ? "(no error description)" : errorDescription;
    }

    public StatusType getStatus() {
        return status;
    }

    public Double getCost() {
        return cost;
    }

    public String getIp() {
        return ip;
    }

    public ZonedDateTime getCreateTime() {
        return createTime;
    }

    public ZonedDateTime getEndTime() {
        return endTime;
    }

    public Integer getSolveCount() {
        return solveCount;
    }

    public SolutionData getSolution() {
        return solution;
    }

    private StatusType parseStatus(String status) {
        if (status == null || status.length() == 0) {
            return null;
        }

        try {
            return StatusType.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public String getErrorCode() {
        return errorCode;
    }

    public enum StatusType {
        PROCESSING,
        READY
    }

    public class SolutionData {
        private String gRecaptchaResponse; // Will be available for Recaptcha tasks only!
        private String gRecaptchaResponseMd5; // for Recaptcha with isExtended=true property
        private String text; // Will be available for ImageToText tasks only!
        private String url; // Will be available for ImageToText and AntiGate tasks only!
        private String token; // Will be available for FunCaptcha tasks only
        private String challenge; // Will be available for GeeTest tasks only
        private String seccode; // Will be available for GeeTest tasks only
        private String validate; // Will be available for GeeTest tasks only
        private String captcha_id; // Will be available for GeeTest v4 tasks only
        private String lot_number; // Will be available for GeeTest v4 tasks only
        private String pass_token; // Will be available for GeeTest v4 tasks only
        private Integer gen_time; // Will be available for GeeTest v4 tasks only
        private String captcha_output; // Will be available for GeeTest v4 tasks only
        private JSONObject cookies;
        private JSONObject localStorage;
        private JSONObject fingerprint;
        private String domain;
        private String respKey; // Will be available for hCaptcha tasks only
        private String userAgent; // User-agent of the worker who solved the captcha
        private JSONArray coordinates; // Will be available for ImageToCoordinates tasks only
        private JSONArray lastRequestHeaders; // Will be available for AntiBotCookie tasks only

        public String getGRecaptchaResponseMd5() {
            return gRecaptchaResponseMd5;
        }

        public String getChallenge() {
            return challenge;
        }

        public String getSeccode() {
            return seccode;
        }

        public String getValidate() {
            return validate;
        }

        public String getText() {
            return text;
        }

        public String getUrl() {
            return url;
        }

        public String getGRecaptchaResponse() {
            return gRecaptchaResponse;
        }

        public String getToken() {
            return token;
        }

        public JSONObject getCookies() {
            return cookies;
        }

        public JSONObject getLocalStorage() {
            return localStorage;
        }

        public JSONObject getFingerprint() {
            return fingerprint;
        }

        public String getDomain() {
            return domain;
        }

        /**
         * hCaptcha only.
         */
        public String getRespKey() {
            return respKey;
        }

        /**
         * User-agent of the worker who solved the captcha. Submit the form with it.
         */
        public String getUserAgent() {
            return userAgent;
        }

        /**
         * ImageToCoordinates only: a list of [x, y] points or [x1, y1, x2, y2] boxes.
         */
        public JSONArray getCoordinates() {
            return coordinates;
        }

        /**
         * AntiBotCookie only: the headers the worker's browser sent last.
         */
        public JSONArray getLastRequestHeaders() {
            return lastRequestHeaders;
        }

        /**
         * The cookies formatted as a "name1=value1; name2=value2" header value.
         */
        public String getCookieHeader() {
            if (cookies == null) {
                return "";
            }

            StringBuilder header = new StringBuilder();
            // keys() rather than keySet(), so this also builds against older org.json
            java.util.Iterator<String> names = cookies.keys();

            while (names.hasNext()) {
                String name = names.next();

                if (header.length() > 0) {
                    header.append("; ");
                }

                header.append(name).append("=").append(cookies.opt(name));
            }

            return header.toString();
        }

        public String getCaptchaId() {
            return captcha_id;
        }

        public String getLotNumber() {
            return lot_number;
        }

        public String getPassToken() {
            return pass_token;
        }

        public Integer getGenTime() {
            return gen_time;
        }

        public String getCaptchaOutput() {
            return captcha_output;
        }
    }
}
