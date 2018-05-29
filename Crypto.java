import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by amazing on 2018/5/25.
 */
public class Crypto {
    //encrption and decryption
    BigInteger private_key;
    Point public_key;
    public BigInteger CreatePrivateKey(BigInteger order)
    {
        int min = 1;
        int max = order.intValue() - 2;
//        int x_a = RandInt(min,max);
        int x_a = 17;
        this.private_key = Point.IntToBig(x_a);
        System.out.println("私钥为"+this.private_key.toString());
        return this.private_key;
    }
    public Point CreatePublicKey(BigInteger a, BigInteger b, BigInteger modulus, BigInteger private_key, Point generator)
    {
        this.public_key = Point.Multiply(a,b,modulus,private_key,generator);
        System.out.println("公钥为 ：("+public_key.x.toString()+","+public_key.y.toString()+")");
        return this.public_key;
    }
    public static char[] Encryption(BigInteger a, BigInteger b, BigInteger modulus, BigInteger order, Point generator, String message, Point public_key) throws IOException {
        Point[] encrped = new Point[3];
        char[] M = Transfer.TransBytesToBits(message.getBytes());
        char[] t;
        char[] K_PA_X_bits;
        char[] K_PA_Y_bits;
        byte[] c1_byte;
        char[] c1_bits;
        while (true)
        {
//            System.out.println("请输入k的值");
//            Scanner str = new Scanner(System.in);
//            //System.out.println("k is :" + str.next());
//            //int s = RandInt(1,order.intValue()-1);
//            //int s = 3;
//            BigInteger k = new BigInteger(str.next());
            BigInteger k = new BigInteger(modulus.bitLength(),new Random());
           //BigInteger k =  IntToBig(88);
            if(k.subtract(modulus).signum()>=0 || k.mod(order).signum()==0)
                continue;
            //BigInteger k = IntToBig(12);
            System.out.println("用户选择的k为："+k.toString());
            encrped[0] = Point.Multiply(a,b,modulus,k,generator);
            System.out.println(encrped[0].isInfinite);
            //encode c1 into byte string
            c1_byte = Transfer.TransPointToBytes(encrped[0],modulus);
            c1_bits = Transfer.TransBytesToBits(c1_byte);
            //encode K_PA
            System.out.println(233);
            Point K_PA = Point.Multiply(a,b,modulus,k,public_key);
            System.out.println(466);
            BigInteger K_PA_X = K_PA.x;
            BigInteger K_PA_Y = K_PA.y;
            byte[] K_PA_X_byte = Transfer.TransDomainElementToBytes(K_PA_X,modulus);
            byte[] K_PA_Y_byte = Transfer.TransDomainElementToBytes(K_PA_Y,modulus);
            K_PA_X_bits = Transfer.TransBytesToBits(K_PA_X_byte);
            K_PA_Y_bits = Transfer.TransBytesToBits(K_PA_Y_byte);
            //A5
            char[] XAndY_bits = Transfer.addBits(K_PA_X_bits,K_PA_Y_bits,false);
            t = Transfer.KDF(XAndY_bits,M.length);
            if(new String(t).contains("1"))
                break;
        }
        System.out.println("原始点坐标为("+encrped[0].x.toString()+","+encrped[0].y.toString()+")");
        System.out.println("加密时c1是："+Transfer.byte2hex(c1_byte));
        char[] c2_bits = Transfer.XOR(M,t);
        char[] arg1 = Transfer.addBits(K_PA_X_bits,M,false);
        char[] arg = Transfer.addBits(arg1,K_PA_Y_bits,false);
        char[] c3_bits = Transfer.TransBytesToBits(SM3.hash(new String(arg)).getBytes());
        char[] encrypted = Transfer.addBits(Transfer.addBits(c1_bits,c2_bits,true),c3_bits,true);
        return encrypted;
    }
    public static String Decryption(BigInteger a, BigInteger b, BigInteger modulus, Point infinite, char[] encryted, BigInteger privae_key) throws IOException {
        //Trans c1 to x
        char[] c1_bits = new String(encryted).split("z")[0].toCharArray();
        char[] c2_bits = new String(encryted).split("z")[1].toCharArray();
        char[] c3_bits = new String(encryted).split("z")[2].toCharArray();

        byte[] c1_bytes = Transfer.TransBitsToBytes(c1_bits);
        System.out.println("解密时c1结果是："+Transfer.byte2hex(c1_bytes));
        Point c1_point = Transfer.TransBytesToPoint(a,b,modulus,infinite,c1_bytes);
        System.out.println("解密的点坐标为("+c1_point.x.toString()+","+c1_point.y.toString()+")");
        //B3
        System.out.println(555);
        Point second = Point.Multiply(a,b,modulus,privae_key,c1_point);
        System.out.println(556);
        BigInteger second_x = second.x;
        BigInteger second_y = second.y;
        char[] second_x_bits = Transfer.TransBytesToBits(Transfer.TransDomainElementToBytes(second_x,modulus));
        char[] second_y_bits = Transfer.TransBytesToBits(Transfer.TransDomainElementToBytes(second_y,modulus));
        //B4
        char[] t = Transfer.KDF(Transfer.addBits(second_x_bits,second_y_bits,false),c2_bits.length);
        //B5
        char[] M_1 = Transfer.XOR(c2_bits,t);
        //B6
        char[] u = Transfer.TransBytesToBits(SM3.hash(new String(Transfer.addBits(Transfer.addBits(second_x_bits,M_1,false),second_y_bits,false))).getBytes());
        if (!new String(u).equals(new String(c3_bits)))
        {
            System.out.println("前后结果不一致");
        }

        String message = new String(Transfer.TransBitsToBytes(M_1),"UTF-8");
        System.out.println("message = "+message);
        return message;
    }
    public static BigInteger IntToBig(int intValue)
    {
        return new BigInteger(String.valueOf(intValue));
    }
    public static int RandInt(int min, int max)
    {
        Random random = new Random();
        int s = random.nextInt(max)%(max-min+1) + min;
        return s;
    }

}
