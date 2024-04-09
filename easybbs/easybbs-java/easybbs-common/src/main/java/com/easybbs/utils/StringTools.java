package com.easybbs.utils;

import com.easybbs.service.SettingService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;

public class StringTools {
    public static boolean isEmpty(String str){
        if(null==str || "".equals(str.trim()) || "null".equals(str)){
            return true;
        }else{
            return false;
        }
    }
    public static final String getRandomString(Integer count){
        return RandomStringUtils.random(count,true,true);
    }
    public static final String getRandomNumber(Integer count){
        return RandomStringUtils.random(count,false,true);
    }
    public static String encodeMd5(String sourceStr){
        return StringTools.isEmpty(sourceStr)?null: DigestUtils.md5Hex(sourceStr);
    }
    public static String getFileSuffix(String fileName){
        return fileName.substring(fileName.lastIndexOf("."));
    }

    public static String eecpapeHtml(String content){
        if (StringTools.isEmpty(content)) {
            return content;
        }
        content=content.replace("<","&lt;");
        content=content.replace(" ","&nbsp;");
        content=content.replace("\n","<br>");
        return content;
    }

    public static String getFileName(String fileName){
        return fileName.substring(0,fileName.lastIndexOf("."));
    }
}
