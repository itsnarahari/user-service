package com.user.utils;

import org.apache.commons.lang3.StringUtils;

public class NullCheck {

    public static boolean isStrNullOrEmpty(String str) {
        if(str != null && !str.isEmpty())
            return false;
        return true;
    }

    public static boolean isNotEmpty(String str) {
        if(str != null && !StringUtils.isEmpty(str.trim()))
            return true;
        return false;
    }

}
