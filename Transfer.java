import java.io.IOException;
import java.math.BigInteger;

/**
 * Created by amazing on 2018/5/26.
 */
public class Transfer {
    public static char[] TransBytesToBits(byte[] bytes){
        int len = bytes.length * 8;//final length
        int radix = 2;
        int already_len = new BigInteger(1, bytes).toString(radix).length();
        if(len > already_len)
        {
            //fill with zero
            char[] zeros = new char[len-already_len];
            for (int i = 0; i < len-already_len; i++)
            {
                zeros[i] = '0';
            }
            String zero = new String(zeros);
            System.out.println(zero);
            return (zero + new BigInteger(1, bytes).toString(radix)).toCharArray();// 这里的1代表正数
        }
        else
        {
            return new BigInteger(1, bytes).toString(radix).toCharArray();
        }
    }
    public static byte[] TransBitsToBytes(char[] bits)
    {
        String binary = new String(bits);
        int length = bits.length;
        int k = (int)Math.ceil(length/8.0);
        byte[] bin_bytes = new byte[k];
        for (int i = 0;i < k; i++)
        {
            String sub_bin = binary.substring(i*8,i*8+8);
            bin_bytes[i] = TransBinToByte(sub_bin);
        }
        return bin_bytes;
    }
    public static byte TransBinToByte(String str)
    {
        int res = 0;
        char[] chars = str.toCharArray();
        for (int i = 0; i < 8; i++)
        {
            res += (chars[i]-'0') * (int)Math.pow(2,7-i);
        }
        return (byte)res;
    }
    public static byte[] TransPointToBytes(Point p,BigInteger modulus)
    {
        //i = 0为最低位
        BigInteger x = p.x;
        BigInteger y = p.y;
        byte[] byte_x = TransDomainElementToBytes(x,modulus);
        byte[] byte_y = TransDomainElementToBytes(y,modulus);
        byte[] byte_pc = TransIntToBytes(Point.IntToBig(4),1);
        byte[] res = new byte[byte_x.length+byte_y.length+1];
        byte[] res_1 = addBytes(byte_pc,byte_x);
        res = addBytes(res_1,byte_y);
        return res;
    }
    public static Point TransBytesToPoint(BigInteger a, BigInteger b, BigInteger modulus, Point infinite, byte[] bytes)
    {
        Point point = new Point(BigInteger.ZERO,BigInteger.ZERO);
        int byte_len = (int)Math.ceil((double)modulus.bitLength()/8.0);
        //get X
        byte[] x1_byte = new byte[byte_len];
        System.arraycopy(bytes,1,x1_byte,0,byte_len);
        //get Y
        byte[] y1_byte = new byte[byte_len];
        System.arraycopy(bytes,byte_len+1,y1_byte,0,byte_len);
        //get 04
        byte[] first = new byte[1];
        first[0] = bytes[0];
        //Trans to X
        BigInteger x;
        x = TransBytesToDomainElement(x1_byte);
        BigInteger y;
        y = TransBytesToDomainElement(y1_byte);
        point.x = x;
        point.y = y;
        //check
        ////////这里以后要加判断如果是无穷远的点要把isinfinite置为true
        if(point.x.toString().equals(infinite.x.toString()) && point.y.toString().equals(infinite.y.toString()))
        {
            System.out.println("转换得到是无穷远点");
            point.isInfinite = true;
        }
        if(!Point.IsOnCurve(a,b,modulus,point) )
            System.out.println("得到的点不在曲线上！");
        return point;

    }
    public static byte[] TransDomainElementToBytes(BigInteger element,BigInteger modulus)
    {
        //modulus is a Odd Prime Number
        //get length
        int byte_len = (int)Math.ceil((double)modulus.bitLength()/8.0);
        byte[] bytes = new byte[byte_len];
        bytes = TransIntToBytes(element,byte_len);
        return bytes;
    }
    public static BigInteger TransBytesToDomainElement(byte[] bytes)
    {
        BigInteger element;
        element = TransBytesToInt(bytes);
        return element;
    }
    public static byte[] TransIntToBytes(BigInteger iSource, int iArrayLen)
    {
//        //int -> bytes
//        byte[] bLocalArr = new byte[iArrayLen];
//        for ( int i = 0; (i < 4) && (i < iArrayLen); i++) {
//            bLocalArr[i] = (byte)( iSource.intValue()>>8*i & 0xFF );
//        }
//        String hex = byte2hex(bLocalArr);
//        System.out.println("整数转换得到的字节串为："+hex);
//        return bLocalArr;
        byte[] array = new byte[iArrayLen];
//        array = iSource.toByteArray();
//        if (array[0] == 0) {
//            byte[] tmp = new byte[array.length - 1];
//            System.arraycopy(array, 1, tmp, 0, tmp.length);
//            array = tmp;
//        }
        return array;
    }
    public static BigInteger TransBytesToInt(byte[] bytes)
    {
//        int target = 0;
//        for (int i = 0; i < bytes.length; i++) {
//            target += (bytes[i] & 0xff) << 8 * i;
//        }
//        return Point.IntToBig(target);
        return new BigInteger(bytes);
    }
    public static String byte2hex(byte [] buffer){
        String h = "";

        for(int i = 0; i < buffer.length; i++){
            String temp = Integer.toHexString(buffer[i] & 0xFF);
            if(temp.length() == 1){
                temp = "0" + temp;
            }
            h = h + " "+ temp;
        }
        return h;
    }
    public static byte[] addBytes(byte[] data1, byte[] data2) {
        byte[] data3 = new byte[data1.length + data2.length];
        System.arraycopy(data1, 0, data3, 0, data1.length);
        System.arraycopy(data2, 0, data3, data1.length, data2.length);
        return data3;
    }
    public static char[] addBits(char[] data1, char[] data2, boolean isAddZ)
    {
        //the split val is z
        String first = new String(data1);
        String second = new String(data2);
        if (isAddZ == true)
            return (first+'z'+second).toCharArray();
        else
            return (first+second).toCharArray();
    }
    public static char[] KDF(char[] bits, int len) throws IOException {
        String data = new String(bits);
        String hash = SM3.hash(data);
        String res_str = hash.substring(0,len);
        if(res_str.contains("1"))
            return res_str.toCharArray();
        else
        {
            System.out.println("得到全0比特串");
            return res_str.toCharArray();
        }
    }
    public static char[] XOR (char[] data1, char[] data2)
    {
        char[] res = new char[data1.length];
        if(data1.length < data2.length)
        {
            System.out.println("异或两数组长度不同");
        }
        for (int i = 0;i < data1.length; i++)
            res[i] = (char)(data1[i] ^ data2[i]);
        return res;

    }
    public static void Encode(Curve curve, String message)
    {
        BigInteger a = curve.a;
        BigInteger b = curve.b;
        BigInteger modulus = curve.modulus;
        for (long i = 0; i <Long.MAX_VALUE;i++)
        {

        }
    }
}
