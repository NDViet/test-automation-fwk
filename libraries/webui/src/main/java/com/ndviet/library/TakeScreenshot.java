package com.ndviet.library;

import com.ndviet.libary.file.FileHelpers;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;

public class TakeScreenshot {
    public static String captureFullPageScreenshot() throws Exception {
        RemoteWebDriver driver = (RemoteWebDriver) BrowserManagement.getInstance().getDriver();
        File source = driver.getScreenshotAs(OutputType.FILE);
        String targetFile = System.getProperty("user.dir") + File.separator + "target" + File.separator + "Screenshots" + File.separator + FileHelpers.getFileName(source.getPath());
        FileHelpers.isDirectory(System.getProperty("user.dir") + File.separator + "target" + File.separator + "Screenshots", true);
        FileHandler.copy(source, new File(targetFile));
        return targetFile;
    }
}
