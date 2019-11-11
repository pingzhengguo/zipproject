package com.pzg.code.zipproject.utils;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 通用工具类
 */
public class CommonUtils {

    /**
     * 随机生成一定长度得16进制随机数
     *
     * @param length 随机数长度
     * @return
     */
    public static String randomHexString(int length) {
        try {
            StringBuffer result = new StringBuffer();
            for (int i = 0; i < length; i++) {
                result.append(Integer.toHexString(new Random().nextInt(16)));
            }
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 随机生成一定位数的小写字母
     *
     * @param length
     * @return
     */
    public static String randomLowString(int length) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append((char) (97 + random.nextInt(26)));
        }
        return sb.toString();
    }

    /**
     * 随机生成一定长度字符串和数字组合
     *
     * @param length
     * @return
     */
    public static String getRandomString(int length) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            if ("char".equals(charOrNum)) {
                // 取得大写字母还是小写字母，a=97,A=65如果是97则为小写字母
                int choice = random.nextInt(2) % 2 == 0 ? 65 : 97;
                sb.append((char) (choice + random.nextInt(26)));
            } else if ("num".equals(charOrNum)) {
                sb.append(String.valueOf(random.nextInt(10)));
            }
        }
        return sb.toString();
    }

    /**
     * 去除尾部特殊字符
     *
     * @param target    目标字符串
     * @param character 特殊字符
     * @return
     */
    public static String removeTailSpecialCharacter(String target, String character) {
        if (!StringUtils.hasText(target)) return null;
        target = target.substring(0, target.lastIndexOf(character));
        return target;
    }

    /**
     * 获取uuid，去除"-"
     *
     * @return
     */
    public static String getUUIDString() {
        return UUID.randomUUID().toString().replace("-", "");
    }


    /**
     * 路径处理，如果不是以 "/" 开头则添加
     *
     * @param path
     * @return
     */
    public static String formatPath(String path) {
        if (StringUtils.hasText(path) && !path.startsWith("/")) {
            path = "/" + path;
        }
        return path;
    }

    public static void main(String[] args) {
        String target = getRandomString(4);
        System.out.println(target);
        System.out.println((int) 'a');
        System.out.println((char) 100);
        System.out.println(getUUIDString());
        System.out.println(randomLowString(6));
    }

    /**
     * 判断是否为json字符串
     *
     * @param test
     * @return
     */
    public static boolean isJSONValid(String test) {
        try {
            JSONObject.parseObject(test);
        } catch (JSONException ex) {
            try {
                JSONObject.parseArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    /**
     * 移除map中的value空值
     *
     * @param paramMap
     * @return
     */
    public static Map<String, String> removeMapEmptyValue(Map<String, String> paramMap) {
        Set<String> set = paramMap.keySet();
        Iterator<String> it = set.iterator();
        List<String> listKey = new ArrayList<String>();
        while (it.hasNext()) {
            String str = it.next();
            if (paramMap.get(str) == null || "".equals(paramMap.get(str))) {
                listKey.add(str);
            }
        }
        for (String key : listKey) {
            paramMap.remove(key);
        }
        return paramMap;
    }
}

