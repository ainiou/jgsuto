package com.common;

import com.pro.jgsu.utils.MailUtils;
import jdk.nashorn.internal.runtime.regexp.RegExp;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyTest {
    Integer i = new Integer(1000);
    @Test
    public void test1(){
        String regex = "^\\w+(\\w|[.]\\w+)+@\\w+([.]\\w+){1,3}";

        System.out.println("1873138022@qq.com".matches(regex));
    }

    @Test
    public void test2(){
        System.out.println(i.toString());
        System.out.println("hashCode:"+i.hashCode());
    }
}
