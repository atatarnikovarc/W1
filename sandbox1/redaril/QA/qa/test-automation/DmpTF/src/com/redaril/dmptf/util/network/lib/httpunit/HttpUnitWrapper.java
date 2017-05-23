package com.redaril.dmptf.util.network.lib.httpunit;

import com.meterware.httpunit.*;
import com.meterware.httpunit.cookies.CookieProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.junit.Assert.fail;

public class HttpUnitWrapper {
    private Logger LOG;
    private static WebConversation wc;

    public HttpUnitWrapper() {
        LOG = LoggerFactory.getLogger(HttpUnitWrapper.class);
        wc = new WebConversation();
        HttpUnitOptions.setScriptingEnabled(true);
        ClientProperties clientProperties = wc.getClientProperties();
        clientProperties.setAcceptCookies(true);
        clientProperties.setAutoRedirect(true);
        clientProperties.setAutoRefresh(true);
        clientProperties.setIframeSupported(true);
        clientProperties.setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:12.0) Gecko/20100101 Firefox/12.0");
        CookieProperties.setDomainMatchingStrict(false);
        CookieProperties.setPathMatchingStrict(false);
        HttpUnitOptions.setExceptionsThrownOnScriptError(false);
        HttpUnitOptions.setCheckContentLength(false);
        HttpUnitOptions.setExceptionsThrownOnErrorStatus(false);
        HttpUnitOptions.setJavaScriptOptimizationLevel(-1);
        //add to approve untrusted sites and have ability to test by https
//        com.sun.net.ssl.internal.www.protocol.https.HttpsURLConnectionOldImpl.setDefaultHostnameVerifier(new HostnameVerifier() {
//            public boolean verify(String urlHostname, String certHostname) {
//                return true;
        //           }
        //      }
        //       );
    }

    public void deleteAllCookies() {
        wc.clearContents();
        LOG.debug("DELETE cookies");
    }

    public void createCookie(String name, String value) {
        wc.putCookie(name, value);
    }

    public String getCookieValueByName(String name) {
        return wc.getCookieValue(name);
    }

    public void goToUrl(String url) {
        try {
            wc.getResponse(url);
            LOG.debug("Go to URL = " + url);
        } catch (SAXException e) {
            LOG.debug("Can't go to URL = " + url);
            LOG.debug("Exception = " + e.getLocalizedMessage());
        } catch (IOException e) {
            LOG.debug("Can't go to URL = " + url);
            LOG.debug("Exception = " + e.getLocalizedMessage());
        }
    }

    public WebResponse sendPostRequest(String url, String body, String contentType, @Nullable Map<String, String> params, @Nullable Map<String, String> headers) {
        InputStream in = new ByteArrayInputStream(body.getBytes());
        WebRequest request = new PostMethodWebRequest(url, in, contentType);
        if (params != null) updateRequestWithParameters(request, params);
        if (headers != null) updateRequestWithHeaders(request, headers);
        try {
            return wc.getResponse(request);
        } catch (IOException io) {
            LOG.debug("Can't send request: " + io.toString());
        } catch (SAXException se) {
            LOG.debug("Can't send request: " + se.toString());
        }
        return null;
    }

    public WebResponse sendGetRequest(String url, @Nullable Map params) {
        WebRequest request = new GetMethodWebRequest(url);
        if (params != null) updateRequestWithParameters(request, params);
        try {
            return wc.sendRequest(request);
        } catch (IOException io) {
            LOG.debug("Can't send POST request: " + io.toString());
        } catch (SAXException se) {
            LOG.debug("Can't send request: " + se.toString());
        }
        return null;
    }

    public void setHeader(String header, String value) {
        wc.setHeaderField(header, value);
    }

    private void updateRequestWithParameters(WebRequest request, Map<String, String> params) {
        for (Map.Entry<String, String> entry : params.entrySet()) {
            request.setParameter(entry.getKey(), entry.getValue());
        }
    }

    private void updateRequestWithHeaders(WebRequest request, Map<String, String> headers) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            request.setHeaderField(entry.getKey(), entry.getValue());
        }
    }

    public boolean isPageAvailable(String url) {
        try {
            wc.getResponse(url);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getResponsePage(String url) {
        try {
            this.goToUrl(url);
            return wc.getCurrentPage().getText();
        } catch (IOException e) {
            LOG.error("Can't get text of current page. URL = " + url);
            fail("Can't get text of current page. URL = " + url);
            return null;
        }
    }

    public WebResponse getResponse(String url) {
        WebResponse response = null;
        try {
            response = wc.getResponse(url);
        } catch (IOException e) {
            LOG.error("Can't get response from " + url);
        } catch (SAXException e) {
            LOG.error("Can't get response from " + url);
        }
        return response;
    }

    public String getLocation(String url) {
        try {
            WebResponse resp = wc.getResponse(url);
            return resp.getURL().toString();
        } catch (SAXException e) {
            LOG.error("Can't get current URL after request to " + url);
            fail("Can't get current URL after request to " + url);
            return null;
        } catch (IOException e) {
            LOG.error("Can't get current URL after request to " + url);
            fail("Can't get current URL after request to " + url);
            return null;
        }
    }

    public String getCurrentUrl() {
        if (wc.getCurrentPage().getURL() == null) return "";
        else return wc.getCurrentPage().getURL().toString();
    }

    public WebForm getFormByAction(WebResponse response, String formActionName) {
        WebForm[] forms = null;
        try {
            forms = response.getForms();
        } catch (SAXException e) {
            LOG.error("Can't find form with name = " + formActionName + ". Exception = " + e.getLocalizedMessage());
        }
        if (forms == null) return null;
        else {
            for (WebForm form : forms) {
                if (form.getAction().contains(formActionName)) {
                    return form;
                }
            }
            LOG.error("Can't find form with name = " + formActionName);
            return null;
        }
    }

    public void submitForm(WebForm form) {
        try {
            form.submit();
        } catch (SAXException e) {
            LOG.error("Can't submit form . Exception = " + e.getLocalizedMessage());
        } catch (IOException e) {
            LOG.error("Can't submit form . Exception = " + e.getLocalizedMessage());
        }

    }

    public void submitFormByButton(WebForm form, String buttonId) {
        try {
            SubmitButton button = (SubmitButton) form.getButtonWithID(buttonId);
            form.submit(button);
        } catch (SAXException e) {
            LOG.error("Can't submit form . Exception = " + e.getLocalizedMessage());
        } catch (IOException e) {
            LOG.error("Can't submit form . Exception = " + e.getLocalizedMessage());
        }

    }

    public void submitFormWithData(String url, String formName, String inputName, String inputValue) {
        WebForm form = getFormByAction(getResponse(url), formName);
        form.setParameter(inputName, inputValue);
        submitForm(form);
    }

    public void submitFormWithData(String url, String formName, String inputName, String inputValue, String buttonId) {
        WebForm form = getFormByAction(getResponse(url), formName);
        form.setParameter(inputName, inputValue);
        submitFormByButton(form, buttonId);
    }
}

