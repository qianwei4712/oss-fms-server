package cn.shiva.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @author shiva   2023-12-22 13:18
 */
public class CommonUtil {

    /**
     * 将文件全路径，解析出文件名
     */
    public static String getNameFromPath(String path) {
        if (StringUtils.isBlank(path)) {
            return null;
        }
        return path.substring(path.lastIndexOf("/") + 1);
    }

    /**
     * 将文件夹全路径，解析出文件名
     */
    public static String getNameFromFolder(String path) {
        if (StringUtils.isBlank(path)) {
            return null;
        }
        path = path.substring(0, path.length() - 1);
        return path.substring(path.lastIndexOf("/") + 1);
    }

    public static long calcFileSize(Long size) {
        if (size == null) {
            return 0L;
        }
        return size / 1024;
    }

}
