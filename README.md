## Official Anti-Captcha.com Java library ##

Official anti-captcha.com Java library for solving images with text, Recaptcha v2/v3 Enterprise/non-Enterprise, Funcaptcha, GeeTest, HCaptcha Enterprise/non-Enterprise, Turnstile, Amazon WAF, Prosopo, Friendly Captcha and Altcha.

[Anti-captcha](https://anti-captcha.com) is an oldest and cheapest web service dedicated to solving captchas by human workers from around the world. By solving captchas with us you help people in poorest regions of the world to earn money, which not only cover their basic needs, but also gives them ability to financially help their families, study and avoid jobs where they're simply not happy.

To use the service you need to [register](https://anti-captcha.com/clients/) and topup your balance. Prices start from $0.0005 per image captcha and $0.002 for Recaptcha. That's $0.5 per 1000 for images and $2 for 1000 Recaptchas.

For more technical information and articles visit our [documentation](https://anti-captcha.com/apidoc) page.

### Install

**Maven**:
```xml
<dependency>
  <groupId>com.anti-captcha</groupId>
  <artifactId>anticaptcha</artifactId>
  <version>1.0.0</version>
</dependency>
```

**Gradle**:
```groovy
implementation "com.anti-captcha:anticaptcha:1.0.0"
```

Requires **Java 11 or newer**. The only dependency is [org.json](https://github.com/stleary/JSON-java) — HTTP comes from the JDK itself.

**Examples how to solve:**

- [Image Captcha](#solve-image-captcha)
- [Recaptcha V2](#solve-recaptcha-v2)
- [Recaptcha V2 Enterprise](#solve-recaptcha-v2-enterprise)
- [Recaptcha V3](#solve-recaptcha-v3)
- [hCaptcha](#solve-hcaptcha)
- [FunCaptcha](#solve-funcaptcha)
- [GeeTest](#solve-geetest)
- [Turnstile](#solve-turnstile)
- [Image to coordinates](#image-to-coordinates)
- [AntiGate (custom tasks)](#solve-antigate-custom-tasks)
- [AntiBot cookies](#get-antibot-cookies)
- [Prosopo](#solve-prosopo)
- [Friendly Captcha](#solve-friendly-captcha)
- [Amazon WAF](#solve-amazon-waf)
- [Altcha](#solve-altcha)

Every task follows the same three steps: fill the parameters, `createTask()`, `waitForResult()`.

### Solve image captcha
```java
import com.anti_captcha.Api.ImageToText;
import com.anti_captcha.Helper.DebugHelper;

public class Main {
    public static void main(String[] args) {
        // Set to false to turn the debug output off
        DebugHelper.setVerboseMode(true);

        ImageToText api = new ImageToText();
        api.setClientKey("API_KEY_HERE");

        // Specify softId to earn 10% commission with your app.
        // Get your softId here: https://anti-captcha.com/clients/tools/devcenter
        api.setSoftId(0);

        api.setFilePath("captcha.jpg");

        // Optional settings, see https://anti-captcha.com/apidoc/task-types/ImageToTextTask
        // api.setPhrase(true);           // the image has 2 or more words
        // api.setCase(true);             // the answer is case sensitive
        // api.setNumeric(ImageToText.NumericOption.NUMBERS_ONLY);
        // api.setMath(1);                // the answer is the result of 50+5
        // api.setMinLength(1);
        // api.setMaxLength(10);
        api.setLanguagePool("en");        // "en" or "rn"

        // Make sure the API key funds balance is positive
        Double balance = api.getBalance();
        if (balance == null || balance <= 0) {
            // Stop here to make sure you don't DDoS the API while having empty balance
            System.out.println("Balance error: " + api.getErrorMessage());
            return;
        }
        System.out.println("Balance: " + balance);

        if (!api.createTask()) {
            System.out.println("Could not create the task: " + api.getErrorMessage());
        } else if (!api.waitForResult()) {
            System.out.println("Could not solve the captcha: " + api.getErrorMessage());
        } else {
            System.out.println("Captcha Solution: " + api.getTaskSolution().getText());

            // If the answer turns out to be wrong:
            // api.reportIncorrectImageCaptcha();
        }
    }
}
```
&nbsp;

### Solve Recaptcha V2
```java
import com.anti_captcha.Api.RecaptchaV2Proxyless;

RecaptchaV2Proxyless api = new RecaptchaV2Proxyless();
api.setClientKey("API_KEY_HERE");
api.setWebsiteUrl("https://www.website.com/");
api.setWebsiteKey("6Lcyu8UZAAAAACwSh6Xf58WrNXTu0LLu4F85xf20");
// api.setWebsiteSToken("...");     // the "data-s" parameter, typical for google.com

if (!api.createTask()) {
    System.out.println("Could not create the task: " + api.getErrorMessage());
} else if (!api.waitForResult()) {
    System.out.println("Could not solve the captcha: " + api.getErrorMessage());
} else {
    System.out.println("Recaptcha g-response token: " + api.getTaskSolution().getGRecaptchaResponse());
    // In case you need the worker's user-agent
    System.out.println("User-Agent: " + api.getTaskSolution().getUserAgent());
}
```
Also with [proxy](https://anti-captcha.com/apidoc/task-types/RecaptchaV2Task):
```java
import com.anti_captcha.Api.RecaptchaV2;
import com.anti_captcha.AnticaptchaBase;

RecaptchaV2 api = new RecaptchaV2();
api.setClientKey("API_KEY_HERE");
api.setWebsiteUrl("https://www.website.com/");
api.setWebsiteKey("6Lcyu8UZAAAAACwSh6Xf58WrNXTu0LLu4F85xf20");
api.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
        + "(KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");
api.setProxyType(AnticaptchaBase.ProxyTypeOption.HTTP);   // HTTP, SOCKS4 or SOCKS5
api.setProxyAddress("1.2.3.4");
api.setProxyPort(1234);
api.setProxyLogin("login-optional");
api.setProxyPassword("pass-optional");
```
&nbsp;

### Solve Recaptcha V2 Enterprise
```java
import com.anti_captcha.Api.RecaptchaV2EnterpriseProxyless;
import org.json.JSONObject;

RecaptchaV2EnterpriseProxyless api = new RecaptchaV2EnterpriseProxyless();
api.setClientKey("API_KEY_HERE");
api.setWebsiteUrl("https://store.steampowered.com/join");
api.setWebsiteKey("6LdIFr0ZAAAAAO3vz0O0OQrtAefzdJcWQM2TMYQH");

// Additional parameters passed to the grecaptcha.enterprise.render call
api.setEnterprisePayload(new JSONObject().put("s", "SOME_ADDITIONAL_TOKEN"));

System.out.println(api.getTaskSolution().getGRecaptchaResponse());
```
The proxy-on version is `RecaptchaV2Enterprise` and takes the same proxy setters as `RecaptchaV2`.

&nbsp;

### Solve Recaptcha V3
```java
import com.anti_captcha.Api.RecaptchaV3Proxyless;

RecaptchaV3Proxyless api = new RecaptchaV3Proxyless();
api.setClientKey("API_KEY_HERE");
api.setWebsiteUrl("https://www.website.com/");
api.setWebsiteKey("6LcvNcwdAAAAAMWAuNRXH74u3QePsEzTm6GEjx0J");
api.setPageAction("somefun");
api.setMinScore(0.9);    // one of 0.3, 0.7, 0.9

System.out.println(api.getTaskSolution().getGRecaptchaResponse());
```
For Recaptcha V3 Enterprise use `RecaptchaV3EnterpriseProxyless`. Recaptcha V3 has no proxy-on version.

&nbsp;

### Solve Hcaptcha
```java
import com.anti_captcha.Api.HCaptchaProxyless;
import org.json.JSONObject;

HCaptchaProxyless api = new HCaptchaProxyless();
api.setClientKey("API_KEY_HERE");
api.setWebsiteUrl("https://www.website.com/");
api.setWebsiteKey("00000000-1111-2222-3333-444444444444");
// api.setIsInvisible(true);
// api.setIsEnterprise(true);
// hCaptcha Enterprise parameters like rqdata, sentry, apiEndpoint, endpoint, reportapi, assethost, imghost
// api.setEnterprisePayload(new JSONObject().put("rqdata", "rqdata value from the target website"));

System.out.println("Hcaptcha Token: " + api.getTaskSolution().getGRecaptchaResponse());
// Use this user-agent for the form submission
System.out.println("User-Agent: " + api.getTaskSolution().getUserAgent());
// Optional "respkey" value, you may need it too
System.out.println("respkey: " + api.getTaskSolution().getRespKey());
```
Also with [proxy](https://anti-captcha.com/apidoc/task-types/HCaptchaTask) — the `HCaptcha` class with the same proxy setters.

&nbsp;

### Solve FunCaptcha
```java
import com.anti_captcha.Api.FunCaptchaProxyless;

FunCaptchaProxyless api = new FunCaptchaProxyless();
api.setClientKey("API_KEY_HERE");
api.setWebsiteUrl("https://www.website.com/");
api.setWebsitePublicKey("00000000-1111-2222-3333-444444444444");
// Make sure to find and set this correctly, look for a URL like
// https://somewebsite-api.arkoselabs.com/v2/00000000-1111-2222-3333-444444444444/api.js
api.setApiSubdomain("somewebsite-api.arkoselabs.com");
api.setDataBlob("{\"blob\":\"HERE_COMES_THE_blob_VALUE\"}");

System.out.println("Funcaptcha Token: " + api.getTaskSolution().getToken());
```
Also with [proxy](https://anti-captcha.com/apidoc/task-types/FunCaptchaTask) — the `FunCaptcha` class.

&nbsp;

### Solve Turnstile
```java
import com.anti_captcha.Api.TurnstileProxyless;

TurnstileProxyless api = new TurnstileProxyless();
api.setClientKey("API_KEY_HERE");
api.setWebsiteUrl("https://www.website.com/");
api.setWebsiteKey("0x4AAAAAAABD2Inoxs-yJ8bz");
// api.setAction("optional page action");
// api.setCData("cdata token for cloudflare");
// api.setChlPageData("chlPageData token for cloudflare");

System.out.println("Turnstile Token: " + api.getTaskSolution().getToken());
```
Also with [proxy](https://anti-captcha.com/apidoc/task-types/TurnstileTask) — the `Turnstile` class.

&nbsp;

### Solve GeeTest
GeeTest has 2 versions, number 3 and 4. Number 3 requires the parameter "challenge". Number 4 has the optional setting `initParameters`.

GeeTest v3:
```java
import com.anti_captcha.Api.GeeTestProxyless;

GeeTestProxyless api = new GeeTestProxyless();
api.setClientKey("API_KEY_HERE");
api.setWebsiteUrl("https://www.website.com/");
api.setWebsiteKey("b6e21f90a91a3c2d4a31fe84e10d0442");
// The challenge is one-time, grab a fresh one for every task
api.setWebsiteChallenge("169acd4a58f2c99770322dfa5270c221");

System.out.println("challenge: " + api.getTaskSolution().getChallenge());
System.out.println("seccode: " + api.getTaskSolution().getSeccode());
System.out.println("validate: " + api.getTaskSolution().getValidate());
```

GeeTest v4:
```java
import com.anti_captcha.Api.GeeTestV4Proxyless;
import org.json.JSONObject;

GeeTestV4Proxyless api = new GeeTestV4Proxyless();
api.setClientKey("API_KEY_HERE");
api.setWebsiteUrl("https://bitget.com/");
api.setWebsiteKey("e9ca9c9ca19ad540a8017f5c107b2d0f");
api.setInitParameters(new JSONObject().put("riskType", "slide"));

System.out.println("captcha_id: " + api.getTaskSolution().getCaptchaId());
System.out.println("lot_number: " + api.getTaskSolution().getLotNumber());
System.out.println("pass_token: " + api.getTaskSolution().getPassToken());
System.out.println("gen_time: " + api.getTaskSolution().getGenTime());
System.out.println("captcha_output: " + api.getTaskSolution().getCaptchaOutput());
```
Also with [proxy](https://anti-captcha.com/apidoc/task-types/GeeTestTask) — `GeeTest` for v3 and `GeeTestV4` for v4.

&nbsp;

### Image to coordinates
```java
import com.anti_captcha.Api.ImageToCoordinates;

ImageToCoordinates api = new ImageToCoordinates();
api.setClientKey("API_KEY_HERE");
api.setFilePath("coordinates.jpg");
// OR api.setBodyBase64("image-encoded-in-base64");
api.setMode("points");    // "points" or "rectangles"
api.setComment("Select objects in the specified order");

System.out.println("Objects X,Y coordinates: " + api.getTaskSolution().getCoordinates());
```
&nbsp;

### Solve AntiGate (custom tasks)
```java
import com.anti_captcha.Api.AntiGateTask;
import org.json.JSONObject;

AntiGateTask api = new AntiGateTask();
api.setClientKey("API_KEY_HERE");
api.setWebsiteUrl("http://antigate.com/logintest.php");
api.setTemplateName("Sign-in and wait for control text");
api.setVariables(new JSONObject()
        .put("login_input_css", "#login")
        .put("login_input_value", "the login")
        .put("password_input_css", "#password")
        .put("password_input_value", "the password")
        .put("control_text", "You have been logged successfully"));

// The proxy is optional for AntiGate tasks
api.setProxyAddress("1.2.3.4");
api.setProxyPort(1234);
api.setProxyLogin("login-optional");
api.setProxyPassword("pass-optional");

System.out.println("cookies: " + api.getTaskSolution().getCookies());
System.out.println("localStorage: " + api.getTaskSolution().getLocalStorage());
System.out.println("fingerprint: " + api.getTaskSolution().getFingerprint());
System.out.println("url: " + api.getTaskSolution().getUrl());
```
&nbsp;

### Get AntiBot cookies
Makes a worker open the page through your proxy and hands you back the anti-bot cookies, so you can reuse them in your own requests. The proxy is required — the cookies are only valid for the IP address they were issued to.
```java
import com.anti_captcha.Api.AntiBotCookie;

AntiBotCookie api = new AntiBotCookie();
api.setClientKey("API_KEY_HERE");
api.setWebsiteUrl("https://www.somewebsite.com/");
api.setProxyAddress("1.2.3.4");
api.setProxyPort(3128);
api.setProxyLogin("login");
api.setProxyPassword("password");

// Ready to be sent as a Cookie header
System.out.println("Cookie: " + api.getTaskSolution().getCookieHeader());
System.out.println("User-Agent: "
        + api.getTaskSolution().getFingerprint().optString("self.navigator.userAgent"));
```
&nbsp;

### Solve Prosopo
```java
import com.anti_captcha.Api.ProsopoProxyless;

ProsopoProxyless api = new ProsopoProxyless();
api.setClientKey("API_KEY_HERE");
api.setWebsiteUrl("https://www.website.com/");
api.setWebsiteKey("sitekey-here");

System.out.println("Prosopo Token: " + api.getTaskSolution().getToken());
```
Also with [proxy](https://anti-captcha.com/apidoc/task-types/ProsopoTask) — the `Prosopo` class.

&nbsp;

### Solve Friendly Captcha
```java
import com.anti_captcha.Api.FriendlyCaptchaProxyless;

FriendlyCaptchaProxyless api = new FriendlyCaptchaProxyless();
api.setClientKey("API_KEY_HERE");
api.setWebsiteUrl("https://www.website.com/");
api.setWebsiteKey("sitekey-here");

System.out.println("Friendly Captcha Token: " + api.getTaskSolution().getToken());
```
Also with [proxy](https://anti-captcha.com/apidoc/task-types/FriendlyCaptchaTask) — the `FriendlyCaptcha` class.

&nbsp;

### Solve Amazon WAF
Two options here:

1. When the captcha is at the bot filtering page and you need the `aws-waf-token` cookie:
```java
import com.anti_captcha.Api.AmazonProxyless;

AmazonProxyless api = new AmazonProxyless();
api.setClientKey("API_KEY_HERE");
api.setWebsiteUrl("https://www.website.com/");
api.setWebsiteKey("key_value_from_window.gokuProps_object");
api.setIv("iv_value_from_window.gokuProps_object");
api.setContext("context_value_from_window.gokuProps_object");

System.out.println("aws-waf-token: " + api.getTaskSolution().getToken());
```

2. When the captcha is a standalone widget triggered by a user's action:
```java
AmazonProxyless api = new AmazonProxyless();
api.setClientKey("API_KEY_HERE");
api.setWebsiteUrl("https://www.website.com/");
// Captcha widget's API key from the AwsWafCaptcha.renderCaptcha function
api.setWebsiteKey("captcha_key_value");
api.setWafType("widget");
// Full URL to jsapi.js
api.setJsapiScript("https://164cb210e333.edge.captcha-sdk.awswaf.com/164cb210e333/jsapi.js");
```
Both options have a [proxy-on](https://anti-captcha.com/apidoc/task-types/AmazonTask) version — the `Amazon` class.

&nbsp;

### Solve Altcha
```java
import com.anti_captcha.Api.AltchaProxyless;

AltchaProxyless api = new AltchaProxyless();
api.setClientKey("API_KEY_HERE");
api.setWebsiteUrl("https://www.website.com/");
api.setChallengeURL("/some/path/to/challenge/url");

System.out.println("Altcha Token: " + api.getTaskSolution().getToken());
```
Also with [proxy](https://anti-captcha.com/apidoc/task-types/AltchaTask) — the `Altcha` class.

&nbsp;

### Reading the solution
`getTaskSolution()` returns the API's `solution` object. Getters for fields a task type does not fill return `null`:

```java
solution.getText();                  // image captchas
solution.getToken();                 // FunCaptcha, Turnstile, Prosopo, Friendly Captcha, Altcha, Amazon
solution.getGRecaptchaResponse();    // Recaptcha, hCaptcha
solution.getRespKey();               // hCaptcha
solution.getUserAgent();             // worker's user-agent
solution.getCookies();               // AntiGate, AntiBotCookie
solution.getCookieHeader();          // the same cookies as a ready "name=value; ..." string
solution.getCoordinates();           // ImageToCoordinates
solution.getLastRequestHeaders();    // AntiBotCookie
```

### Other settings
```java
api.setClientKey("API_KEY_HERE");
api.setSoftId(1187);              // earn 10% commission with your app
api.setConnectionTimeout(30_000); // milliseconds per HTTP call to the API

api.waitForResult(300);           // seconds to keep polling, 120 by default

api.getTaskId();                  // id of the created task
api.getErrorMessage();            // why the last call failed

DebugHelper.setVerboseMode(false); // silence the library
```

### A note on TLS
Versions of this library before 1.0.0 installed a trust-all `X509TrustManager` and called `SSLContext.setDefault()`, which disabled certificate validation **for the whole JVM** — not just for calls to this API. That is gone: certificates are validated normally now. If you were relying on the old behaviour for some other part of your application, configure that explicitly on your own client.

### Building and running the tests
```bash
git clone https://github.com/anti-captcha/anticaptcha-java.git
cd anticaptcha-java

# unit tests, they need no API key and no network
mvn test

# build the jar
mvn package
```

The examples live in [src/com/anti_captcha/Main.java](src/com/anti_captcha/Main.java). It is excluded from the published jar; run it from your IDE after putting your API key in.

Publishing to Maven Central is described in [maven_instructions.md](maven_instructions.md) (in Russian).
