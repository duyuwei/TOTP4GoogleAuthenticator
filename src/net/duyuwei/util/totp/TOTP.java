package net.duyuwei.util.totp;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;

/**
 * HOTP(K,C) = Truncate(HMAC-SHA-1(K,C))
 * Created by duyuwei on 16/7/28.
 */
public class TOTP {

    private Mac mac;


    public TOTP() {
        try {
            //HMAC: H(K XOR opad, H(K XOR ipad, text))
            mac = Mac.getInstance("HmacSHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

    /**
     * @param key        base32加密后私钥
     * @param systemTime 当前时间
     * @param invertal   时间差
     * @return GoogleAuthenticator Number Code
     * @throws Exception
     */
    public String getDynamicCode(String key, long systemTime, int invertal) throws Exception {
        //用密钥和当前step计算
        byte[] hash = getHmacSHA1(key, (systemTime - invertal) / 30000);
        //将最后一个字节的低4位二进制作为索引,索引范围为0-15
        int offset = hash[19] & 0xf;
        //然后计算索引指向的连续4字节空间生成int整型数据。
        int binary = ((hash[offset] & 0x7f) << 24) | ((hash[offset + 1] & 0xff) << 16) | ((hash[offset + 2] & 0xff) << 8) | (hash[offset + 3] & 0xff);
        //对获取到的整型数据进行模运算(取后六位)
        int otp = binary % 1000000;
        //对结果进行补全（长度不够6位，在首位补零）
        return addZeros(Integer.toString(otp));
    }

    /**
     * 计算HMAC-SHA-1
     *
     * @param secret 秘钥
     * @param msg    需要加密内容
     * @return
     * @throws Exception
     */
    private byte[] getHmacSHA1(String secret, long msg) throws Exception {
        SecretKey secretKey = new SecretKeySpec(Base32String.decode(secret), "RAW");
        mac.reset();
        mac.init(secretKey);
        byte[] value = ByteBuffer.allocate(8).putLong(msg).array();
        return mac.doFinal(value);
    }

    /**
     * 以"0"补足6位
     *
     * @param s
     * @return
     */
    private String addZeros(String s) {
        if (s.length() < 6) {
            s = "0" + s;
            return addZeros(s);
        }
        return s;
    }

    public static void main(String[] args) {
        TOTP totp = new TOTP();
        try {
            String dynamicCode = totp.getDynamicCode("GEZDGNBTGI2DINBU", System.currentTimeMillis(), 0);
            System.out.println(dynamicCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
