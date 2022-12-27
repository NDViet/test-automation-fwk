package com.ndviet.library;

import com.ndviet.libary.file.FileHelpers;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;

import static com.ndviet.libary.configuration.Constants.CURRENT_WORKING_DIR;
import static com.ndviet.libary.configuration.Constants.SCREENSHOT_DIR;
import static com.ndviet.libary.configuration.Constants.TARGET_DIR;

public class TakeScreenshot {
    public static String captureFullPageScreenshot() throws Exception {
        RemoteWebDriver driver = (RemoteWebDriver) BrowserManagement.getInstance().getDriver();
        File source = driver.getScreenshotAs(OutputType.FILE);
        String targetFile = System.getProperty(CURRENT_WORKING_DIR) + File.separator + TARGET_DIR + File.separator + SCREENSHOT_DIR + File.separator + FileHelpers.getFileName(source.getPath());
        FileHelpers.isDirectory(System.getProperty(CURRENT_WORKING_DIR) + File.separator + TARGET_DIR + File.separator + SCREENSHOT_DIR, true);
        FileHandler.copy(source, new File(targetFile));
        return targetFile;
    }
}
