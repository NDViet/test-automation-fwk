package com.ndviet.libary.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileHelpers {
    public static String getPath(String path) {
        if (path != null) {
            File file = new File(path);
            return file.getPath();
        } else {
            return path;
        }
    }

    public static boolean isDirectory(String path) {
        return isDirectory(path, true);
    }

    public static boolean isDirectory(String path, boolean createIfNotExist) {
        File file = new File(path);
        if (!file.exists() && createIfNotExist) {
            return file.mkdirs();
        }
        return file.isDirectory();
    }

    public static List<String> recursiveGetListFiles(String directory, List<String> allFiles) {
        File loadFile = new File(directory);
        File[] files = loadFile.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    recursiveGetListFiles(directory, allFiles);
                } else {
                    allFiles.add(file.getPath());
                }
            }
        } else {
            allFiles.add(loadFile.getPath());
        }
        return allFiles;
    }

    public static List<String> recursiveGetListFiles(String directory, List<String> allFiles, String filterFileType) {
        recursiveGetListFiles(directory, allFiles);
        List<String> filteredFiles = new ArrayList<>();
        for (String filePath : allFiles) {
            if (filePath.lastIndexOf(".") > 0) {
                String fileExtension = filePath.substring(filePath.lastIndexOf(".") + 1);
                if (fileExtension.toLowerCase().matches(filterFileType.toLowerCase())) {
                    filteredFiles.add(filePath);
                }
            } else {
                if (filePath.matches(filterFileType)) filteredFiles.add(filePath);
            }
        }
        return filteredFiles;
    }
}
