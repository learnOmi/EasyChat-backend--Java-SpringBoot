package com.easychat.utils;

import com.easychat.entity.constants.Constants;
import com.easychat.enums.UserContactTypeEnum;
import com.easychat.exception.BusinessException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public class StringTools {
    public static void checkParam(Object param) {
        try {
            Field[] fields = param.getClass().getDeclaredFields();
            boolean notEmpty = false;
            for (Field field : fields) {
                String methodName = "get" + StringTools.upperCaseFirst(field.getName());
                Method method = param.getClass().getMethod(methodName);
                Object object = method.invoke(param);
                if (object != null && object instanceof java.lang.String && !StringTools.isEmpty(object.toString())
                    || object != null && !(object instanceof  java.lang.String)) {
                    notEmpty = true;
                    break;
                }
            }
            if (!notEmpty) {
                throw new BusinessException("参数不能为空");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("参数校验异常");
        }
    }

    public static boolean isEmpty(String str) {
        if (null == str || "".equals(str) || "null".equals(str) || "\u0000".equals(str)) {
            return true;
        } else if ("".equals(str.trim())) {
            return true;
        } else {
            return false;
        }
    }

    public static String upperCaseFirst(String str) {
        if (isEmpty(str)) {
            return str;
        }
        if (str.length() > 1 && Character.isUpperCase(str.charAt(1))) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String getUserId() {
        return UserContactTypeEnum.USER.getPrefix() + getRandomNumber(Constants.LENGTH_11);
    }

    public static String getGroupId() {
        return UserContactTypeEnum.GROUP.getPrefix() + getRandomNumber(Constants.LENGTH_11);
    }

    public static String getRandomNumber(Integer count) {
        return RandomStringUtils.random(count, false, true);
    }

    public static String getRandomString(Integer count) {
        return RandomStringUtils.random(count, true, false);
    }

    public static final String encodeMd5(String originStr) {
        return StringTools.isEmpty(originStr) ? null : DigestUtils.md5Hex(originStr);
    }

    public static String cleanHtmlTag(String content) {
        if (isEmpty(content)) {
            return content;
        }
        content = content.replace("<", "&lt;").replace("\r\n", "<br/>").replace("\n", "<br/>");
        return content;
    }

    public static String getChatSessionId4User(String[] userIds) {
        Arrays.sort(userIds);
        return encodeMd5(StringUtils.join(userIds, ""));
    }

    public static String getChatSessionId4Group(String groupId) {
        return encodeMd5(groupId);
    }

    public static String getFileSuffix(String fileName) {
        if (isEmpty(fileName)) return null;
        return fileName.substring(fileName.lastIndexOf("."));
    }

    public static boolean isNumber(String str) {
        String checkNumber = "^[0-9]+$";
        if (null == str) {
            return false;
        }
        if (!str.matches(checkNumber)) {
            return false;
        }

        return true;
    }
}
