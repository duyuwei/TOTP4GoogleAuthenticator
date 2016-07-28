package net.duyuwei.util.totp;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;

/**
 * Created by duyuwei on 16/7/28.
 */
public class TOTP {

    private Mac mac;


    public TOTP() {
        try {
            mac = Mac.getInstance("HmacSHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

    public String getDynamicCode(String key, long systemTime, int invertal) throws Exception {
        byte[] hash = getHmacSHA1(key, (systemTime - invertal) / 30000);
        System.out.println("data: " + hash);
        for (byte b : hash) {
            System.out.println(b);
        }
        System.out.println("data19: " + hash[19]);
        System.out.println(Integer.toBinaryString(hash[19]));
        int offset = hash[19] & 0xf;//通过对最后一个字节的低4位二进制位建立索引，索引范围为  （0-15）+4  ，正好20个字节。
        System.out.println("offset: " + offset);
        System.out.println("offset.bin: " + Integer.toBinaryString(offset));
        int binary = ((hash[offset] & 0x7f) << 24) | ((hash[offset + 1] & 0xff) << 16) | ((hash[offset + 2] & 0xff) << 8) | (hash[offset + 3] & 0xff);
        //然后计算索引指向的连续4字节空间生成int整型数据。
        System.out.println(binary);
        int otp = binary % 1000000;//对获取到的整型数据进行模运算(取后六位)
        return addZeros(Integer.toString(otp));//再对结果进行补全（长度不够6位，在首位补零）得到长度为6的字符串
    }

    private byte[] getHmacSHA1(String secret, long msg) throws Exception {
        SecretKey secretKey = new SecretKeySpec(Base32String.decode(secret), "");
        mac.reset();
        mac.init(secretKey);
        byte[] value = ByteBuffer.allocate(8).putLong(msg).array();
        return mac.doFinal(value);
    }

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
