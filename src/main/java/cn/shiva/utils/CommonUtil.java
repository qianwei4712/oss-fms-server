package cn.shiva.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @author shiva   2023-12-22 13:18
 */
public class CommonUtil {

    /**
     * 将全路径，解析出文件名
     */
    public static String getNameFromPath(String path) {
        if (StringUtils.isBlank(path)) {
            return null;
        }
        return path.substring(path.lastIndexOf("/") + 1);
    }

    public static long calcFileSize(Long size) {
        if (size == null) {
            return 0L;
        }
        return size / 1024;
    }

}
