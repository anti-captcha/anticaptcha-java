package com.anti_captcha;

import com.anti_captcha.Api.Altcha;
import com.anti_captcha.Api.AltchaProxyless;
import com.anti_captcha.Api.Amazon;
import com.anti_captcha.Api.AmazonProxyless;
import com.anti_captcha.Api.AntiBotCookie;
import com.anti_captcha.Api.FriendlyCaptchaProxyless;
import com.anti_captcha.Api.FunCaptchaProxyless;
import com.anti_captcha.Api.GeeTestProxyless;
import com.anti_captcha.Api.GeeTestV4Proxyless;
import com.anti_captcha.Api.HCaptchaProxyless;
import com.anti_captcha.Api.ImageToCoordinates;
import com.anti_captcha.Api.ImageToText;
import com.anti_captcha.Api.ProsopoProxyless;
import com.anti_captcha.Api.RecaptchaV2;
import com.anti_captcha.Api.RecaptchaV2EnterpriseProxyless;
import com.anti_captcha.Api.RecaptchaV2Proxyless;
import com.anti_captcha.Api.RecaptchaV3Proxyless;
import com.anti_captcha.Api.Turnstile;
import com.anti_captcha.Api.TurnstileProxyless;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Offline tests: they check the JSON we send to the API. No network, no API key.
 */
class TaskBuildingTest {

    @Test
    @DisplayName("image task carries the flags the API expects")
    void imageTask() {
        ImageToText api = new ImageToText();
        api.setBodyBase64("QUFB");
        api.setLanguagePool("en");
        api.setNumeric(ImageToText.NumericOption.NUMBERS_ONLY);

        JSONObject task = api.getPostData();

        assertEquals("ImageToTextTask", task.getString("type"));
        assertEquals("QUFB", task.getString("body"));
        assertEquals(1, task.getInt("numeric"));
        assertEquals("en", task.getString("languagePool"));
    }

    @Test
    @DisplayName("image task without a body is refused")
    void imageTaskWithoutBody() {
        assertNull(new ImageToText().getPostData());
    }

    @Test
    @DisplayName("image to coordinates task")
    void imageToCoordinates() {
        ImageToCoordinates api = new ImageToCoordinates();
        api.setBodyBase64("QUFB");
        api.setMode("rectangles");
        api.setComment("Select all elephants");

        JSONObject task = api.getPostData();

        assertEquals("ImageToCoordinatesTask", task.getString("type"));
        assertEquals("rectangles", task.getString("mode"));
        assertEquals("Select all elephants", task.getString("comment"));

        assertNull(new ImageToCoordinates().getPostData(), "no image means no task");
    }

    @Test
    @DisplayName("recaptcha v2 proxyless")
    void recaptchaV2Proxyless() {
        RecaptchaV2Proxyless api = new RecaptchaV2Proxyless();
        api.setWebsiteUrl("https://website.com/");
        api.setWebsiteKey("KEY");

        JSONObject task = api.getPostData();

        assertEquals("RecaptchaV2TaskProxyless", task.getString("type"));
        assertEquals("https://website.com/", task.getString("websiteURL"));
        assertEquals("KEY", task.getString("websiteKey"));
    }

    @Test
    @DisplayName("recaptcha v2 with a proxy")
    void recaptchaV2ProxyOn() {
        RecaptchaV2 api = new RecaptchaV2();
        api.setWebsiteUrl("https://website.com/");
        api.setWebsiteKey("KEY");
        api.setUserAgent("UA");
        api.setProxyType(AnticaptchaBase.ProxyTypeOption.SOCKS5);
        api.setProxyAddress("1.2.3.4");
        api.setProxyPort(1234);
        api.setProxyLogin("login");
        api.setProxyPassword("password");

        JSONObject task = api.getPostData();

        assertEquals("RecaptchaV2Task", task.getString("type"));
        assertEquals("socks5", task.getString("proxyType"));
        assertEquals("1.2.3.4", task.getString("proxyAddress"));
        assertEquals(1234, task.getInt("proxyPort"));
        assertEquals("UA", task.getString("userAgent"));
    }

    @Test
    @DisplayName("recaptcha v2 with a broken proxy is refused")
    void recaptchaV2WithoutProxy() {
        RecaptchaV2 api = new RecaptchaV2();
        api.setWebsiteUrl("https://website.com/");
        api.setWebsiteKey("KEY");

        assertNull(api.getPostData());
    }

    @Test
    @DisplayName("recaptcha v2 enterprise carries the payload")
    void recaptchaV2Enterprise() {
        RecaptchaV2EnterpriseProxyless api = new RecaptchaV2EnterpriseProxyless();
        api.setWebsiteUrl("https://website.com/");
        api.setWebsiteKey("KEY");
        api.setEnterprisePayload(new JSONObject().put("s", "TOKEN"));

        JSONObject task = api.getPostData();

        assertEquals("RecaptchaV2EnterpriseTaskProxyless", task.getString("type"));
        assertEquals("TOKEN", task.getJSONObject("enterprisePayload").getString("s"));
    }

    @Test
    @DisplayName("recaptcha v3")
    void recaptchaV3() {
        RecaptchaV3Proxyless api = new RecaptchaV3Proxyless();
        api.setWebsiteUrl("https://website.com/");
        api.setWebsiteKey("KEY");
        api.setPageAction("login");
        api.setMinScore(0.7);

        JSONObject task = api.getPostData();

        assertEquals("RecaptchaV3TaskProxyless", task.getString("type"));
        assertEquals("login", task.getString("pageAction"));
        assertEquals(0.7, task.getDouble("minScore"), 1e-9);
    }

    @Test
    @DisplayName("hcaptcha sends isEnterprise, not IsEnterprise")
    void hcaptcha() {
        HCaptchaProxyless api = new HCaptchaProxyless();
        api.setWebsiteUrl("https://website.com/");
        api.setWebsiteKey("KEY");
        api.setIsEnterprise(true);

        JSONObject task = api.getPostData();

        assertEquals("HCaptchaTaskProxyless", task.getString("type"));
        assertTrue(task.getBoolean("isEnterprise"));
        assertFalse(task.has("IsEnterprise"));
    }

    @Test
    @DisplayName("funcaptcha keeps the data blob a string")
    void funcaptcha() {
        FunCaptchaProxyless api = new FunCaptchaProxyless();
        api.setWebsiteUrl("https://website.com/");
        api.setWebsitePublicKey("KEY");
        api.setApiSubdomain("x.arkoselabs.com");
        api.setDataBlob("{\"blob\":\"B\"}");

        JSONObject task = api.getPostData();

        assertEquals("FunCaptchaTaskProxyless", task.getString("type"));
        assertEquals("{\"blob\":\"B\"}", task.getString("data"));
        assertEquals("x.arkoselabs.com", task.getString("funcaptchaApiJSSubdomain"));
    }

    @Test
    @DisplayName("geetest v3 and v4")
    void geetest() {
        GeeTestProxyless v3 = new GeeTestProxyless();
        v3.setWebsiteUrl("https://website.com/");
        v3.setWebsiteKey("GT");
        v3.setWebsiteChallenge("CHALLENGE");

        JSONObject task3 = v3.getPostData();

        assertEquals("GeeTestTaskProxyless", task3.getString("type"));
        assertEquals("GT", task3.getString("gt"));
        assertEquals("CHALLENGE", task3.getString("challenge"));

        GeeTestV4Proxyless v4 = new GeeTestV4Proxyless();
        v4.setWebsiteUrl("https://website.com/");
        v4.setWebsiteKey("GT");
        v4.setInitParameters(new JSONObject().put("riskType", "slide"));

        JSONObject task4 = v4.getPostData();

        assertEquals(4, task4.getInt("version"));
        assertEquals("slide", task4.getJSONObject("initParameters").getString("riskType"));
    }

    @Test
    @DisplayName("turnstile setAction actually sets the action")
    void turnstileAction() {
        TurnstileProxyless api = new TurnstileProxyless();
        api.setWebsiteUrl("https://website.com/");
        api.setWebsiteKey("0x4");
        api.setAction("login");

        JSONObject task = api.getPostData();

        assertEquals("TurnstileTaskProxyless", task.getString("type"));
        assertEquals("login", task.getString("action"), "setAction() used to be a no-op");
    }

    @Test
    @DisplayName("turnstile with a proxy")
    void turnstileProxyOn() {
        Turnstile api = new Turnstile();
        api.setWebsiteUrl("https://website.com/");
        api.setWebsiteKey("0x4");
        api.setProxyType(AnticaptchaBase.ProxyTypeOption.HTTP);
        api.setProxyAddress("1.2.3.4");
        api.setProxyPort(8080);

        JSONObject task = api.getPostData();

        assertEquals("TurnstileTask", task.getString("type"));
        assertEquals("http", task.getString("proxyType"));
    }

    @Test
    @DisplayName("prosopo and friendly captcha")
    void sitekeyTasks() {
        ProsopoProxyless prosopo = new ProsopoProxyless();
        prosopo.setWebsiteUrl("https://website.com/");
        prosopo.setWebsiteKey("KEY");

        assertEquals("ProsopoTaskProxyless", prosopo.getPostData().getString("type"));

        FriendlyCaptchaProxyless friendly = new FriendlyCaptchaProxyless();
        friendly.setWebsiteUrl("https://website.com/");
        friendly.setWebsiteKey("KEY");

        assertEquals("FriendlyCaptchaTaskProxyless", friendly.getPostData().getString("type"));
    }

    @Test
    @DisplayName("altcha")
    void altcha() {
        AltchaProxyless api = new AltchaProxyless();
        api.setWebsiteUrl("https://website.com/");
        api.setChallengeURL("/challenge");

        JSONObject task = api.getPostData();

        assertEquals("AltchaTaskProxyless", task.getString("type"));
        assertEquals("/challenge", task.getString("challengeURL"));

        Altcha withProxy = new Altcha();
        withProxy.setWebsiteUrl("https://website.com/");
        withProxy.setChallengeURL("/challenge");
        withProxy.setProxyType(AnticaptchaBase.ProxyTypeOption.HTTP);
        withProxy.setProxyAddress("1.2.3.4");
        withProxy.setProxyPort(8080);

        assertEquals("AltchaTask", withProxy.getPostData().getString("type"));
    }

    @Test
    @DisplayName("amazon waf widget")
    void amazonWidget() {
        AmazonProxyless api = new AmazonProxyless();
        api.setWebsiteUrl("https://website.com/");
        api.setWebsiteKey("KEY");
        api.setWafType("widget");
        api.setJsapiScript("https://x/jsapi.js");

        JSONObject task = api.getPostData();

        assertEquals("AmazonTaskProxyless", task.getString("type"));
        assertEquals("widget", task.getString("wafType"));
        assertEquals("https://x/jsapi.js", task.getString("jsapiScript"));

        Amazon withProxy = new Amazon();
        withProxy.setWebsiteUrl("https://website.com/");
        withProxy.setWebsiteKey("KEY");
        withProxy.setProxyType(AnticaptchaBase.ProxyTypeOption.HTTP);
        withProxy.setProxyAddress("1.2.3.4");
        withProxy.setProxyPort(8080);

        assertEquals("AmazonTask", withProxy.getPostData().getString("type"));
    }

    @Test
    @DisplayName("antibot cookie task carries no proxyType")
    void antibotCookie() {
        AntiBotCookie api = new AntiBotCookie();
        api.setWebsiteUrl("https://website.com/");
        api.setProxyAddress("1.2.3.4");
        api.setProxyPort(3128);
        api.setProxyLogin("login");
        api.setProxyPassword("password");

        JSONObject task = api.getPostData();

        assertEquals("AntiBotCookieTask", task.getString("type"));
        assertEquals("1.2.3.4", task.getString("proxyAddress"));
        assertEquals(3128, task.getInt("proxyPort"));
        assertEquals("login", task.getString("proxyLogin"));
        assertFalse(task.has("proxyType"), "this task type takes no proxyType");
    }

    @Test
    @DisplayName("antibot cookie without a proxy is refused")
    void antibotCookieWithoutProxy() {
        AntiBotCookie api = new AntiBotCookie();
        api.setWebsiteUrl("https://website.com/");

        assertNull(api.getPostData());
    }

    @Test
    @DisplayName("both the URL and the String overload work")
    void websiteUrlOverloads() throws Exception {
        RecaptchaV2Proxyless fromString = new RecaptchaV2Proxyless();
        fromString.setWebsiteUrl("https://website.com/path");
        fromString.setWebsiteKey("KEY");

        RecaptchaV2Proxyless fromUrl = new RecaptchaV2Proxyless();
        fromUrl.setWebsiteUrl(java.net.URI.create("https://website.com/path").toURL());
        fromUrl.setWebsiteKey("KEY");

        assertEquals(
                fromString.getPostData().getString("websiteURL"),
                fromUrl.getPostData().getString("websiteURL")
        );
    }

    @Test
    @DisplayName("base64 of a known string")
    void base64() {
        assertEquals("Zm9vYmFy", com.anti_captcha.Helper.StringHelper.toBase64("foobar".getBytes()));
        assertEquals("Zg==", com.anti_captcha.Helper.StringHelper.toBase64("f".getBytes()));
        assertEquals("//79", com.anti_captcha.Helper.StringHelper.toBase64(
                new byte[]{(byte) 0xFF, (byte) 0xFE, (byte) 0xFD}));
    }

    @Test
    @DisplayName("camelCase of the API method names")
    void camelCase() {
        assertEquals("createTask", com.anti_captcha.Helper.StringHelper.toCamelCase("CREATE_TASK"));
        assertEquals("getTaskResult", com.anti_captcha.Helper.StringHelper.toCamelCase("GET_TASK_RESULT"));
        assertEquals("getBalance", com.anti_captcha.Helper.StringHelper.toCamelCase("GET_BALANCE"));
    }

    @Test
    @DisplayName("solution reads the fields added for the new task types")
    void solutionFields() {
        JSONObject response = new JSONObject(
                "{\"errorId\":0,\"status\":\"ready\",\"cost\":\"0.001\",\"solution\":{"
                        + "\"gRecaptchaResponse\":\"TOKEN\",\"userAgent\":\"UA\",\"respKey\":\"RK\","
                        + "\"cookies\":{\"a\":\"1\"},\"coordinates\":[[10,20]]}}"
        );

        com.anti_captcha.ApiResponse.TaskResultResponse parsed =
                new com.anti_captcha.ApiResponse.TaskResultResponse(response);

        assertNotNull(parsed.getSolution());
        assertEquals("TOKEN", parsed.getSolution().getGRecaptchaResponse());
        assertEquals("UA", parsed.getSolution().getUserAgent());
        assertEquals("RK", parsed.getSolution().getRespKey());
        assertEquals(1, parsed.getSolution().getCoordinates().length());
        assertEquals("a=1", parsed.getSolution().getCookieHeader());
    }
}
