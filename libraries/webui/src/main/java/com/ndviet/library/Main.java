package com.ndviet.library;

import com.ndviet.libary.TestObject.WebElementIdentifier;

import static com.ndviet.libary.TestObject.ObjectRepository.findTestObject;
import static com.ndviet.libary.configuration.Constants.OBJECT_REPOSITORY_DIRECTORY;
import static com.ndviet.libary.configuration.Constants.WEB_IDENTIFIERS_DIRECTORY;

public class Main {
    public static void main(String[] args) throws Exception {
        System.setProperty(OBJECT_REPOSITORY_DIRECTORY, "D:/Code/test-automation-fwk/libraries/utilities/src/main/resources");
        System.setProperty(WEB_IDENTIFIERS_DIRECTORY, "WebIdentifiers");
        WebElementIdentifier.setElementFiles();
        BrowserManagement.getInstance().goToUrl("https://google.com");
        WebUI.setText(findTestObject("Google.Search.Textbox"), "Vietnam");
        WebUI.click(findTestObject("Google.Search.Button"));
        String title = BrowserManagement.getInstance().getDriver().getTitle();
        System.out.println("Page title: " + title);
        System.out.println("Current URL: " + BrowserManagement.getInstance().getDriver().getCurrentUrl());
        //BrowserManagement.getInstance().getDriver().quit();
    }
}
