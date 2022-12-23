package com.ndviet.library;

import com.ndviet.libary.TestObject.TestObject;

public class WebUI {
    public static void click(TestObject testObject) throws Exception {
        Element.click(testObject);
    }

    public static void setText(TestObject testObject, String text) throws Exception {
        Element.setText(testObject, text);
    }

}
