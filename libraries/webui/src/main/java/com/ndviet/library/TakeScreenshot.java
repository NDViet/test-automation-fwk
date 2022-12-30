package com.ndviet.library;

import com.ndviet.libary.configuration.ConfigurationFactory;
import com.ndviet.libary.file.FileHelpers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.remote.RemoteWebDriver;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import javax.imageio.ImageIO;
import java.io.File;

import static com.ndviet.libary.configuration.Constants.CURRENT_WORKING_DIR;
import static com.ndviet.libary.configuration.Constants.DEFAULT_SCREENSHOT_COUNT;
import static com.ndviet.libary.configuration.Constants.SCREENSHOT_DIR;
import static com.ndviet.libary.configuration.Constants.SELENIUM_SCREENSHOT_FILE_TYPE;
import static com.ndviet.libary.configuration.Constants.TARGET_DIR;

public class TakeScreenshot {
    private static final String m_directory = System.getProperty(CURRENT_WORKING_DIR) + File.separator + TARGET_DIR + File.separator + SCREENSHOT_DIR;
    private static final String m_fileType = getScreenshotFileType();
    private static final Logger LOGGER = LogManager.getLogger(TakeScreenshot.class);
    private static int m_count = DEFAULT_SCREENSHOT_COUNT;

    public static void resetScreenshotCount() {
        m_count = DEFAULT_SCREENSHOT_COUNT;
    }

    private static String getScreenshotFileType() {
        String fileType = ConfigurationFactory.getInstance().getValue(SELENIUM_SCREENSHOT_FILE_TYPE);
        if (fileType != null && !fileType.isEmpty()) {
            return fileType;
        } else {
            return "png";
        }
    }

    public static String captureFullPageScreenshot(String fileName) throws Exception {
        RemoteWebDriver driver = (RemoteWebDriver) BrowserManagement.getInstance().getDriver();
        Screenshot screenshot = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(1000)).takeScreenshot(driver);
        File targetFile = getTargetFile(fileName);
        ImageIO.write(screenshot.getImage(), m_fileType, targetFile);
        LOGGER.info("Screenshot is available in location: " + targetFile.getPath());
        return targetFile.getPath();
    }

    public static String capturePageScreenshot(String fileName) throws Exception {
        RemoteWebDriver driver = (RemoteWebDriver) BrowserManagement.getInstance().getDriver();
        File source = driver.getScreenshotAs(OutputType.FILE);
        File targetFile = getTargetFile(fileName);
        FileHandler.copy(source, targetFile);
        LOGGER.info("Screenshot is available in location: " + targetFile.getPath());
        return targetFile.getPath();
    }

    private static File getTargetFile(String fileName) throws Exception {
        FileHelpers.isDirectory(m_directory, true);
        File targetFile;
        if (fileName == null) {
            targetFile = File.createTempFile("screenshot_", String.format("_SS.%s", m_fileType), new File(m_directory));
        } else {
            targetFile = new File(m_directory + File.separator + String.format("%s_SS_%s.%s", fileName, m_count, m_fileType));
            m_count++;
        }
        return targetFile;
    }
}
