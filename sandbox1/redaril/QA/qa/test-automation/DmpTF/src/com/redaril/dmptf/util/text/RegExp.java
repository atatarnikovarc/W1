package com.redaril.dmptf.util.text;

import com.redaril.dmptf.tests.support.etl.model.Types;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExp {

    private String getRexExpDATE() {
        return "((20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])\\s([01][0-9]|[2][0-3]):?([0-5]\\d):?([0-5]\\d)";
    }

    private String getRexExpInteger() {
        return "(\\d){1,}";
    }

    private String getRexExpJSON() {
        return ".*";
    }

    private String getRegExpByType(String str) {
        Types type = Types.valueOf(str.toUpperCase());
        String regexp = "";
        switch (type) {
            case STRING:
                break;
            case INTEGER:
                regexp = getRexExpInteger();
                break;
            case DATE:
                regexp = getRexExpDATE();
                break;
            case BOOLEAN:
                break;
            case JSON:
                regexp = getRexExpJSON();
                break;
        }
        return regexp;
    }

    public Boolean check(String str, String type) {
        String regexp = getRegExpByType(type);
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    public static Boolean checkValueByRegex(String regex, String value) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();

    }
}
