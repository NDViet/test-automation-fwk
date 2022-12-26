package com.ndviet.library;

import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

import static com.ndviet.libary.TestObject.ObjectRepository.findTestObject;
import static com.ndviet.libary.configuration.Constants.OBJECT_REPOSITORY_DIRECTORY;
import static com.ndviet.libary.configuration.Constants.WEB_IDENTIFIERS_DIRECTORY;

public class Main {
    public static void main(String[] args) throws Exception {
        System.setProperty(OBJECT_REPOSITORY_DIRECTORY, "D:/Code/test-automation-fwk/libraries/utilities/src/main/resources");
        System.setProperty(WEB_IDENTIFIERS_DIRECTORY, "WebIdentifiers");
        //General_LineChart();
        //General_BarChart();
        //Repayment_ColumnChart();
        //Repayment_BarChart();
        //Disbursement_PieChart();
        //Disbursement_BarChart();
        //BrowserManagement.getInstance().getDriver().quit();
    }

    public static void Repayment_BarChart() throws Exception {
        BrowserManagement.getInstance().goToUrl("https://fundingsocieties.com");
        WebUI.click(findTestObject("Menu.Statistics"));
        WebUI.click(findTestObject("Statistics.Tab.Repayment"));
        WebUI.verifyElementPresent(findTestObject("Charts.Bar Chart Paths"));
        List<WebElement> paths = WebUI.findWebElements(findTestObject("Charts.Bar Chart Paths"));
        for (int i = 1; i <= paths.size(); i++) {
            WebUI.moveToElement(findTestObject("Charts.Bar Chart Path", Map.of("index", i)));
            WebUI.verifyElementPresent(findTestObject("Charts.Label Details"));
            List<WebElement> labels = WebUI.findWebElements(findTestObject("Charts.Label Details"));
            for (int j = 1; j <= labels.size(); j++) {
                String text = WebUI.getText(findTestObject("Charts.Label Detail", Map.of("index", j)));
                System.out.println(text);
            }
        }
    }

    public static void Repayment_ColumnChart() throws Exception {
        BrowserManagement.getInstance().goToUrl("https://fundingsocieties.com");
        WebUI.click(findTestObject("Menu.Statistics"));
        WebUI.click(findTestObject("Statistics.Tab.Repayment"));
        WebUI.verifyElementPresent(findTestObject("Charts.Column Chart Paths"));
        List<WebElement> paths = WebUI.findWebElements(findTestObject("Charts.Column Chart Paths"));
        for (int i = 1; i <= paths.size(); i++) {
            WebUI.moveToElement(findTestObject("Charts.Column Chart Path", Map.of("index", i)));
            WebUI.verifyElementPresent(findTestObject("Charts.Label Details"));
            List<WebElement> labels = WebUI.findWebElements(findTestObject("Charts.Label Details"));
            for (int j = 1; j <= labels.size(); j++) {
                String text = WebUI.getText(findTestObject("Charts.Label Detail", Map.of("index", j)));
                System.out.println(text);
            }
        }
    }

    public static void General_BarChart() throws Exception {
        BrowserManagement.getInstance().goToUrl("https://fundingsocieties.com");
        WebUI.click(findTestObject("Menu.Statistics"));
        WebUI.verifyElementPresent(findTestObject("Charts.Bar Chart Paths"));
        List<WebElement> paths = WebUI.findWebElements(findTestObject("Charts.Bar Chart Paths"));
        for (int i = 1; i <= paths.size(); i++) {
            WebUI.moveToElement(findTestObject("Charts.Bar Chart Path", Map.of("index", i)));
            WebUI.verifyElementPresent(findTestObject("Charts.Label Details"));
            List<WebElement> labels = WebUI.findWebElements(findTestObject("Charts.Label Details"));
            for (int j = 1; j <= labels.size(); j++) {
                String text = WebUI.getText(findTestObject("Charts.Label Detail", Map.of("index", j)));
                System.out.println(text);
            }
        }
    }

    public static void General_LineChart() throws Exception {
        BrowserManagement.getInstance().goToUrl("https://fundingsocieties.com");
        WebUI.click(findTestObject("Menu.Statistics"));
        WebUI.verifyElementPresent(findTestObject("Charts.Line Chart Paths"));
        List<WebElement> paths = WebUI.findWebElements(findTestObject("Charts.Line Chart Paths"));
        for (int i = 1; i <= paths.size(); i++) {
            WebUI.moveToElement(findTestObject("Charts.Line Chart Path", Map.of("index", i)));
            WebUI.moveToElement(findTestObject("Charts.Line Chart Path", Map.of("index", i)));
            WebUI.verifyElementPresent(findTestObject("Charts.Label Details"));
            List<WebElement> labels = WebUI.findWebElements(findTestObject("Charts.Label Details"));
            for (int j = 1; j <= labels.size(); j++) {
                String text = WebUI.getText(findTestObject("Charts.Label Detail", Map.of("index", j)));
                System.out.println(text);
            }
        }
    }

    public static void Disbursement_PieChart() throws Exception {
        BrowserManagement.getInstance().goToUrl("https://fundingsocieties.com");
        WebUI.click(findTestObject("Menu.Statistics"));
        WebUI.click(findTestObject("Statistics.Tab.Disbursement"));
        WebUI.verifyElementPresent(findTestObject("Charts.Pie Chart Paths"));
        List<WebElement> paths = WebUI.findWebElements(findTestObject("Charts.Pie Chart Paths"));
        for (int i = 1; i <= paths.size(); i++) {
            WebUI.moveToElement(findTestObject("Charts.Pie Chart Path", Map.of("index", i)));
            WebUI.verifyElementPresent(findTestObject("Charts.Label Details"));
            List<WebElement> labels = WebUI.findWebElements(findTestObject("Charts.Label Details"));
            for (int j = 1; j <= labels.size(); j++) {
                String text = WebUI.getText(findTestObject("Charts.Label Detail", Map.of("index", j)));
                System.out.println(text);
            }
        }
    }

    public static void Disbursement_BarChart() throws Exception {
        BrowserManagement.getInstance().goToUrl("https://fundingsocieties.com");
        WebUI.click(findTestObject("Menu.Statistics"));
        WebUI.click(findTestObject("Statistics.Tab.Disbursement"));
        WebUI.verifyElementPresent(findTestObject("Charts.Bar Chart Paths"));
        List<WebElement> paths = WebUI.findWebElements(findTestObject("Charts.Bar Chart Paths"));
        for (int i = 1; i <= paths.size(); i++) {
            WebUI.moveToElement(findTestObject("Charts.Bar Chart Path", Map.of("index", i)));
            WebUI.verifyElementPresent(findTestObject("Charts.Label Details"));
            List<WebElement> labels = WebUI.findWebElements(findTestObject("Charts.Label Details"));
            for (int j = 1; j <= labels.size(); j++) {
                String text = WebUI.getText(findTestObject("Charts.Label Detail", Map.of("index", j)));
                System.out.println(text);
            }
        }
    }

}
