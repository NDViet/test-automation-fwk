package com.ndviet.library.string;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringHelpers {
    public static String replaceStringUsingRegex(String input, String regex, String replace) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.replaceAll(replace);
    }

    public static List<String> replaceListStringUsingRegex(List<String> listInput, String regex, String replace) {
        List<String> returnList = new ArrayList<>();
        for (String input : listInput) {
            returnList.add(replaceStringUsingRegex(input, regex, replace));
        }
        return returnList;
    }

    public static String getStringMatchesRegex(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        List<String> listMatches = new ArrayList<>();
        while (matcher.find()) {
            listMatches.add(matcher.group(0));
        }
        return (listMatches.size() == 0) ? "" : listMatches.get(0);
    }

    public static List<String> getListStringMatchesRegex(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        List<String> listMatches = new ArrayList<>();
        while (matcher.find()) {
            listMatches.add(matcher.group(0));
        }
        return listMatches;
    }

    public static List<String> getListStringMatchesListRegex(String input, List<String> listRegex) {
        List<String> listMatches = new ArrayList<>();
        for (String regex : listRegex) {
            String value = getStringMatchesRegex(input, regex);
            value = replaceStringUsingRegex(value, ":|●|\\(|\\)", "").trim();
            listMatches.add(value);
        }
        return listMatches;
    }


}
