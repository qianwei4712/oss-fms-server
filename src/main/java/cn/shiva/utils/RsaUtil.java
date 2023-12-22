package cn.shiva.utils;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


/**
 * RSA工具类
 */
@Slf4j
public class RsaUtil {

    //签名算法名称
    private static final String RSA_KEY_ALGORITHM = "RSA";

    //标准签名算法名称
    private static final String RSA_SIGNATURE_ALGORITHM = "SHA1withRSA";
    private static final String RSA2_SIGNATURE_ALGORITHM = "SHA256withRSA";

    public static void main(String[] args) throws Exception {
//        String token = "GFta2yphboClSpmUctaVL7seUd4T1GVL+gu8SGhFf5J6nGtnQ9ZXAhtkAtpRGQ/HJ2/8ScGGMZpbxyEIDpVw4vZyUVfMNEQcv1Rk+7oO1zuz3uIz+fFha1YsciLKndCDJMuMfnrmHvEBhxRHuc3WWr6CfswYc0g1mrFOUPtF9NkFy05liPjQ+8Dr3ZZuJkBBNFAh2sp3l8731LsZMtCBjc8Befd+Qv/dTvsnH9KuwBVOvSOtob2n3xDiUkYbCBNCJId4KO71Hp7mY0w8Ly95uYthpZUNJNNtqTKyojQSUpNPQPa5TCuC3GJwElWkXX97nn0Nc6mTYGYtO1WJNMeaHQ==";
//        String privateKey = "MIIEowIBAAKCAQEApGSw4voPCFI6jnJmMsuDZzfSbqsdcdvq/94LnImxLHNX/w1iKYuz0F2RHs+c8EbXzz3Bn5Z2yLdeCwWkPViuuQbv3KPIFmeq2+NFCajP/TEENSognTkgWczhsa/qczTgPM7+tgn84M+3DKETlcDwy0Ci3zW/sZDCQsgrA1dqYZgBHepXZXW0vwbRZQ9rHWGJNCBU3JaNROOI7M6jBcrrt0oPqYeX9BtEOKeNm9lAS6jwR0GudrLYKqBMOooSVauPvzNgJ1LJhiVj/VqiSVyagL2EcPRxVhqJj+OAictws5M1MPTW4Xon0KtJhkkQrE4Shn5oM0PwspTu1AIoBz1TaQIDAQABAoIBAHMl+GS0RO1OM6ftZ7pypNMa3bIY96H2KfAZR0bZh5mcHWoQpM53BXeQ4oKTZ9lBtg56snInQYKsNhpAVF+IC7Kcskx5CEDxUlN9KLFwZmdx+wGGRZv7FLbx9LCyiOUzh1Hwp6c47cFGnXAiL4a4w7GKz8rLaj9lJz0B2mcXxvMeFg5rYXixtZ8DAAD7wGiB6rdfN87HJDBX8OAS/e0Q7dduXQH8LEQriVP5W2tbhfJJiOqAaPklUTwoWEDNy1p2wKY+m2OBFr7+1sFFdEQTA0KAxu2jKZp2XxgygENDXq1D1mtn8g21X5PcdNmiVFKF9Xxvx/VOtv75sRSiZyt684UCgYEA4bS1UetiognZjfFG/EnvR7647RzuY8wkkZ0UtHnKzetU8hTIk4n4OLpbGVWAutMiK4CRPP6HzJPTmeFchO4ZLD1XKSAM2a73fweTWoOqtid5fwe66zFM2XjLOcTJ4ZezyYm/mUARoBlKGprx4Z5ipHUssbQUFpjecnQH38738YMCgYEAunVF0bJK5Pl3TkKUrbrmuummvT7fppPR/h7brTh+yAH+saWgPONmBMmKrV2LmkGgWoROuwpIVQgxlUU6zUMnuMxAntr9wun1XUnlkIpEhOqvNvgbEAIgohKdBy3q7GMrHYQjsZswGPpRJW1cctABUvwdbcUninpA4LWvDZTCr6MCgYAqAl51PuB+1GJ/vta9gm1c5yy4RVhBju+Hgrsl1D1hElofLKsget+Of3ERSA7Ltxx3CFkAB0fzVdhencmAnGhnCYu9hc+efKhoJlrQ0AzoFnTQpfmuaOCq4YbB29TPflLiQhc9SOV+7PYT+Z7npXkqrvyAnsnWYuGUX/BPQdB4TQKBgQCoZQltXQxuxy6Mq20Dieyfmi1QdNwf4Rih3NShGIjHsaWIYwDYfLbCVBK+h+FwqxxbI2rHWX4B7ah6G1AD2UaiizVBQp4zzoAehIKji3xeXmVny6MFTiaSuSTAOyQT4Wwq+BAUcwd5R3jJyH7Z4imP/MnHPWt8kPTO/MbgCR3a6QKBgDwtVQ/E7zQjLUTSCUVar7ENx/jPwWj1cE0FZT1GTGL+IFDIFkPmy0kAGMdhqYfyqTdH2fCYqVwoCOJP9e/W+YhsWWDfFWyFyq8HWjFBkJAG7U8j2yPIpqCI2UNT1gIdnpucf2wqMKBKeH0JdsX3t4wHlp/UaiuMcVgr2PzZ+izG";
//        System.out.println(decryptByPrivateKey(token, privateKey));
//        generateKey()
        Map<String, String> map = generateKey(2048);
        System.out.println(map.get("publicKey"));
        System.out.println(map.get("privateKey"));
    }

    /**
     * 生成密钥对
     *
     * @param keySize RSA密钥长度,默认密钥长度是1024,密钥长度必须是64的倍数，在512到65536位之间,不管是RSA还是RSA2长度推荐使用2048
     * @return 返回包含公私钥的map
     */
    public static Map<String, String> generateKey(int keySize) {
        KeyPairGenerator keygen;
        try {
            keygen = KeyPairGenerator.getInstance(RSA_KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("RSA初始化密钥出现错误,算法异常");
        }
        SecureRandom secrand = new SecureRandom();
        //初始化随机产生器
        secrand.setSeed("Alian".getBytes());
        //初始化密钥生成器
        keygen.initialize(keySize, secrand);
        KeyPair keyPair = keygen.genKeyPair();
        //获取公钥并转成base64编码
        byte[] pub_key = keyPair.getPublic().getEncoded();
        String publicKeyStr = Base64.getEncoder().encodeToString(pub_key);
        //获取私钥并转成base64编码
        byte[] pri_key = keyPair.getPrivate().getEncoded();
        String privateKeyStr = Base64.getEncoder().encodeToString(pri_key);
        //创建一个Map返回结果
        Map<String, String> keyPairMap = new HashMap<>();
        keyPairMap.put("publicKey", publicKeyStr);
        keyPairMap.put("privateKey", privateKeyStr);
        return keyPairMap;
    }

    /**
     * 公钥加密(用于数据加密)
     *
     * @param data         加密前的字符串
     * @param publicKeyStr base64编码后的公钥
     * @return base64编码后的字符串
     */
    public static String encryptByPublicKey(String data, String publicKeyStr) throws Exception {
        //Java原生base64解码
        byte[] pubKey = Base64.getDecoder().decode(publicKeyStr);
        //创建X509编码密钥规范
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(pubKey);
        //返回转换指定算法的KeyFactory对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
        //根据X509编码密钥规范产生公钥对象
        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
        //根据转换的名称获取密码对象Cipher（转换的名称：算法/工作模式/填充模式）
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        //用公钥初始化此Cipher对象（加密模式）
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        //对数据加密
        byte[] encrypt = cipher.doFinal(data.getBytes());
        //返回base64编码后的字符串
        return Base64.getEncoder().encodeToString(encrypt);
    }

    /**
     * 私钥解密(用于数据解密)
     *
     * @param data          解密前的字符串
     * @param privateKeyStr 私钥
     * @return 解密后的字符串
     */
    public static String decryptByPrivateKey(String data, String privateKeyStr) throws Exception {
        //Java原生base64解码
        byte[] priKey = Base64.getDecoder().decode(privateKeyStr);
        //创建PKCS8编码密钥规范
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(priKey);
        //返回转换指定算法的KeyFactory对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
        //根据PKCS8编码密钥规范产生私钥对象
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        //根据转换的名称获取密码对象Cipher（转换的名称：算法/工作模式/填充模式）
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        //用私钥初始化此Cipher对象（解密模式）
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        //对数据解密
        byte[] decrypt = cipher.doFinal(Base64.getDecoder().decode(data));
        //返回字符串
        return new String(decrypt);
    }

    /**
     * 私钥加密(用于数据签名)
     *
     * @param data          加密前的字符串
     * @param privateKeyStr base64编码后的私钥
     * @return base64编码后后的字符串
     */
    public static String encryptByPrivateKey(String data, String privateKeyStr) throws Exception {
        //Java原生base64解码
        byte[] priKey = Base64.getDecoder().decode(privateKeyStr);
        //创建PKCS8编码密钥规范
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(priKey);
        //返回转换指定算法的KeyFactory对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
        //根据PKCS8编码密钥规范产生私钥对象
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        //根据转换的名称获取密码对象Cipher（转换的名称：算法/工作模式/填充模式）
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        //用私钥初始化此Cipher对象（加密模式）
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        //对数据加密
        byte[] encrypt = cipher.doFinal(data.getBytes());
        //返回base64编码后的字符串
        return Base64.getEncoder().encodeToString(encrypt);
    }

    /**
     * 公钥解密(用于数据验签)
     *
     * @param data         解密前的字符串
     * @param publicKeyStr base64编码后的公钥
     * @return 解密后的字符串
     * @throws Exception
     */
    public static String decryptByPublicKey(String data, String publicKeyStr) throws Exception {
        //Java原生base64解码
        byte[] pubKey = Base64.getDecoder().decode(publicKeyStr);
        //创建X509编码密钥规范
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(pubKey);
        //返回转换指定算法的KeyFactory对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
        //根据X509编码密钥规范产生公钥对象
        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
        //根据转换的名称获取密码对象Cipher（转换的名称：算法/工作模式/填充模式）
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        //用公钥初始化此Cipher对象（解密模式）
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        //对数据解密
        byte[] decrypt = cipher.doFinal(Base64.getDecoder().decode(data));
        //返回字符串
        return new String(decrypt);
    }

    /**
     * RSA签名
     *
     * @param data     待签名数据
     * @param priKey   私钥
     * @param signType RSA或RSA2
     * @return 签名
     * @throws Exception
     */
    public static String sign(byte[] data, byte[] priKey, String signType) throws Exception {
        //创建PKCS8编码密钥规范
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(priKey);
        //返回转换指定算法的KeyFactory对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
        //根据PKCS8编码密钥规范产生私钥对象
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        //标准签名算法名称(RSA还是RSA2)
        String algorithm = RSA_KEY_ALGORITHM.equals(signType) ? RSA_SIGNATURE_ALGORITHM : RSA2_SIGNATURE_ALGORITHM;
        //用指定算法产生签名对象Signature
        Signature signature = Signature.getInstance(algorithm);
        //用私钥初始化签名对象Signature
        signature.initSign(privateKey);
        //将待签名的数据传送给签名对象(须在初始化之后)
        signature.update(data);
        //返回签名结果字节数组
        byte[] sign = signature.sign();
        //返回Base64编码后的字符串
        return Base64.getEncoder().encodeToString(sign);
    }

    /**
     * RSA校验数字签名
     *
     * @param data     待校验数据
     * @param sign     数字签名
     * @param pubKey   公钥
     * @param signType RSA或RSA2
     * @return boolean 校验成功返回true，失败返回false
     */
    public static boolean verify(byte[] data, byte[] sign, byte[] pubKey, String signType) throws Exception {
        //返回转换指定算法的KeyFactory对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
        //创建X509编码密钥规范
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(pubKey);
        //根据X509编码密钥规范产生公钥对象
        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
        //标准签名算法名称(RSA还是RSA2)
        String algorithm = RSA_KEY_ALGORITHM.equals(signType) ? RSA_SIGNATURE_ALGORITHM : RSA2_SIGNATURE_ALGORITHM;
        //用指定算法产生签名对象Signature
        Signature signature = Signature.getInstance(algorithm);
        //用公钥初始化签名对象,用于验证签名
        signature.initVerify(publicKey);
        //更新签名内容
        signature.update(data);
        //得到验证结果
        return signature.verify(sign);
    }
}
