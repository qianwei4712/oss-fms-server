package cn.shiva.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

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

    /**
     * 编码转换，
     */
    public static File reEncode2UTF8(File sourceFile) throws Exception {
        //开始拿到文件要转码了
        String fileCharset = LocalCharsetUtil.getFileCharset(sourceFile);
        //目标编码
        String systemCharset = "UTF-8";
        if (StrUtil.isNotEmpty(fileCharset) && !systemCharset.equals(fileCharset)) {
            sourceFile = CharsetUtil.convert(sourceFile, Charset.forName(fileCharset), Charset.forName(systemCharset));

            //转换编码后的文件内容
            String fileContent = FileUtil.readUtf8String(sourceFile);
            //创建文件输出流,以覆盖方式打开文件
            FileOutputStream fos = new FileOutputStream(sourceFile, false);
            //指定UTF-8编码的输出流写出内容
            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            osw.write(fileContent);
            //关闭输出流
            osw.close();
            fos.close();
        }
        return sourceFile;
    }

    public static MultipartFile fileToMultipartFile(File file, String fieldName) throws IOException {
        try {
            if (file == null || !file.exists()) {
                throw new FileNotFoundException("文件未找到：" + file);
            }
            byte[] content = Files.readAllBytes(file.toPath());
            return new ByteArrayMultipartFile(content, file.getName(), fieldName, Files.probeContentType(file.toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            // 删除临时文件
            file.delete();
        }
    }


    /**
     * 内置一个简单的 MultipartFile 实现类，用于File转换
     */
    public static class ByteArrayMultipartFile implements MultipartFile {
        private final byte[] content;
        private final String name;
        private final String originalFilename;
        private final String contentType;

        /**
         * 构造函数
         *
         * @param content          文件内容
         * @param originalFilename 文件原始名字
         * @param name             字段名
         * @param contentType      文件类型
         */
        public ByteArrayMultipartFile(byte[] content, String originalFilename, String name, String contentType) {
            this.content = content;
            this.originalFilename = originalFilename;
            this.name = name;
            this.contentType = contentType;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getOriginalFilename() {
            return this.originalFilename;
        }

        @Override
        public String getContentType() {
            return this.contentType;
        }

        @Override
        public boolean isEmpty() {
            return (this.content == null || this.content.length == 0);
        }

        @Override
        public long getSize() {
            return this.content.length;
        }

        @Override
        public byte[] getBytes() {
            return this.content;
        }

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(this.content);
        }

        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {
            try (OutputStream os = new FileOutputStream(dest)) {
                os.write(this.content);
            }
        }
    }

    public static class LocalCharsetUtil {

        /**
         * 7位ASCII字符，也叫作ISO646-US、Unicode字符集的基本拉丁块
         */
        public static final String US_ASCII = "US-ASCII";

        /**
         * ISO 拉丁字母表 No.1，也叫作 ISO-LATIN-1
         */
        public static final String ISO_8859_1 = "ISO-8859-1";

        /**
         * 8 位 UCS 转换格式
         */
        public static final String UTF_8 = "UTF-8";

        /**
         * 16 位 UCS 转换格式，Big Endian（最低地址存放高位字节）字节顺序
         */
        public static final String UTF_16BE = "UTF-16BE";

        /**
         * 16 位 UCS 转换格式，Little-endian（最高地址存放低位字节）字节顺序
         */
        public static final String UTF_16LE = "UTF-16LE";

        /**
         * 16 位 UCS 转换格式，字节顺序由可选的字节顺序标记来标识
         */
        public static final String UTF_16 = "UTF-16";

        /**
         * 中文超大字符集
         */
        public static final String GBK = "GBK";

        /**
         * 将字符编码转换成US-ASCII码
         */
        public String toASCII(String str) throws UnsupportedEncodingException {
            return this.changeCharset(str, US_ASCII);
        }

        /**
         * 将字符编码转换成ISO-8859-1码
         */
        public String toISO_8859_1(String str) throws UnsupportedEncodingException {
            return this.changeCharset(str, ISO_8859_1);
        }

        /**
         * 将字符编码转换成UTF-8码
         */
        public String toUTF_8(String str) throws UnsupportedEncodingException {
            return this.changeCharset(str, UTF_8);
        }

        /**
         * 将字符编码转换成UTF-16BE码
         */
        public String toUTF_16BE(String str) throws UnsupportedEncodingException {
            return this.changeCharset(str, UTF_16BE);
        }

        /**
         * 将字符编码转换成UTF-16LE码
         */
        public String toUTF_16LE(String str) throws UnsupportedEncodingException {
            return this.changeCharset(str, UTF_16LE);
        }

        /**
         * 将字符编码转换成UTF-16码
         */
        public String toUTF_16(String str) throws UnsupportedEncodingException {
            return this.changeCharset(str, UTF_16);
        }

        /**
         * 将字符编码转换成GBK码
         */
        public String toGBK(String str) throws UnsupportedEncodingException {
            return this.changeCharset(str, GBK);
        }

        /**
         * 字符串编码转换的实现方法
         *
         * @param str        待转换编码的字符串
         * @param newCharset 目标编码
         * @return
         * @throws UnsupportedEncodingException
         */
        public String changeCharset(String str, String newCharset)
                throws UnsupportedEncodingException {
            if (str != null) {
                //用默认字符编码解码字符串。
                byte[] bs = str.getBytes();
                //用新的字符编码生成字符串
                return new String(bs, newCharset);
            }
            return null;
        }

        /**
         * 字符串编码转换的实现方法
         *
         * @param str        待转换编码的字符串
         * @param oldCharset 原编码
         * @param newCharset 目标编码
         * @return
         * @throws UnsupportedEncodingException
         */
        public String changeCharset(String str, String oldCharset, String newCharset)
                throws UnsupportedEncodingException {
            if (str != null) {
                //用旧的字符编码解码字符串。解码可能会出现异常。
                byte[] bs = str.getBytes(oldCharset);
                //用新的字符编码生成字符串
                return new String(bs, newCharset);
            }
            return null;
        }


        public static String getStrCharset(String str) {
            String encode = "UTF-8";
            try {
                if (str.equals(new String(str.getBytes(encode), encode))) {
                    String s = encode;
                    return s;
                }
            } catch (Exception exception) {
            }
            encode = "GBK";
            try {
                if (str.equals(new String(str.getBytes(encode), encode))) {
                    String s1 = encode;
                    return s1;
                }
            } catch (Exception exception1) {
            }
            encode = "GB2312";
            try {
                if (str.equals(new String(str.getBytes(encode), encode))) {
                    String s2 = encode;
                    return s2;
                }
            } catch (Exception exception2) {
            }
            encode = "ISO-8859-1";
            try {
                if (str.equals(new String(str.getBytes(encode), encode))) {
                    String s3 = encode;
                    return s3;
                }
            } catch (Exception exception3) {
            }
            return "";
        }


        /**
         * 判断文本文件的字符集，文件开头三个字节表明编码格式。
         *
         * @param file
         * @return
         * @throws Exception
         * @throws Exception
         */
        public static String getFileCharset(File file) {
            String charset = "GBK";
            byte[] first3Bytes = new byte[3];
            try {
                boolean checked = false;
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                bis.mark(100);
                int read = bis.read(first3Bytes, 0, 3);
                if (read == -1) {
                    bis.close();
                    return charset; // 文件编码为 ANSI
                } else if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                    charset = "UTF-16LE"; // 文件编码为 Unicode
                    checked = true;
                } else if (first3Bytes[0] == (byte) 0xFE && first3Bytes[1] == (byte) 0xFF) {
                    charset = "UTF-16BE"; // 文件编码为 Unicode big endian
                    checked = true;
                } else if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB
                        && first3Bytes[2] == (byte) 0xBF) {
                    charset = "UTF-8"; // 文件编码为 UTF-8
                    checked = true;
                }
                bis.reset();
                if (!checked) {
                    while ((read = bis.read()) != -1) {
                        if (read >= 0xF0) {
                            break;
                        }
                        if (0x80 <= read && read <= 0xBF) {// 单独出现BF以下的，也算是GBK
                            break;
                        }
                        if (0xC0 <= read && read <= 0xDF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {// 双字节 (0xC0 - 0xDF)
                                // (0x80 - 0xBF),也可能在GB编码内
                                continue;
                            } else {
                                break;
                            }
                        } else if (0xE0 <= read && read <= 0xEF) { // 也有可能出错，但是几率较小
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                read = bis.read();
                                if (0x80 <= read && read <= 0xBF) {
                                    charset = "UTF-8";
                                    break;
                                } else {
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                    }
                }
                bis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return charset;
        }

        public static String changeFileCharset(String inPath, String outPath, String oldCharset, String newCharset) throws Exception {
            // 以GBK格式读取文件
            FileInputStream fis = new FileInputStream(inPath);
            InputStreamReader isr = new InputStreamReader(fis, oldCharset);
            BufferedReader br = new BufferedReader(isr);
            String str = null;
            // 创建StringBuffer字符串缓存区
            StringBuffer sb = new StringBuffer();
            // 通过readLine()方法遍历读取文件
            while ((str = br.readLine()) != null) {
                // 使用readLine()方法无法进行换行,需要手动在原本输出的字符串后面加"\n"或"\r"
                str += "\n";
                sb.append(str);
            }
            // 以UTF-8文件写入
            String str2 = sb.toString();
            FileOutputStream fos = new FileOutputStream(outPath, false);
            OutputStreamWriter osw = new OutputStreamWriter(fos, newCharset);
            osw.write(str2);
            osw.flush();
            osw.close();
            fos.close();
            br.close();
            isr.close();
            fis.close();
            return "文件生成成功";
        }
    }

}
