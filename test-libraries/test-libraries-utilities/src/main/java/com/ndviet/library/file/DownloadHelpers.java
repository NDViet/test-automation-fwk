package com.ndviet.library.file;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URL;

public class DownloadHelpers {

    public static String downloadFile(String url, String targetPath) throws Exception {
        String targetFilePath;
        if (FileHelpers.isDirectory(targetPath)) {
            targetFilePath = targetPath + File.separator + getFileNameFromUrl(url);
        } else {
            targetFilePath = targetPath;
        }
        FileUtils.copyURLToFile(new URL(url), new File(targetFilePath), 20000, 20000);
        return targetFilePath;
    }

    public static String getFileNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }
}
