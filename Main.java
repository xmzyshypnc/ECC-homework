import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

/**
 * Created by amazing on 2018/5/25.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        //test for generator
        Curve curve = CreateCurve(8);
        BigInteger a,b,modulus,order;
        Point generator;
        a = curve.a;
        b = curve.b;
        modulus = curve.modulus;
        generator = curve.generator;
        order = curve.order;
  //      Curve curve = new Curve(IntToBig(1),IntToBig(1),IntToBig(23));
//        Point generator = new Point(IntToBig(18),IntToBig(3));
//        BigInteger order = IntToBig(28);
        System.out.println("生成元的阶是："+order.toString());
        //get private key
        Crypto crypto = new Crypto();
        BigInteger private_key = crypto.CreatePrivateKey(order);
        //get public key
        Point public_key = crypto.CreatePublicKey(curve.a,curve.b,curve.modulus,private_key,generator);
        //Encryption

        //Point message = new Point(IntToBig(4),IntToBig(0));
        String message = "thisisatest";
        //Point[] crypted = new Point[2];
        char[] crypted;
        crypted = Crypto.Encryption(curve.a,curve.b,curve.modulus,order,generator,message,public_key);
        //Decryption
        Crypto.Decryption(curve.a,curve.b,curve.modulus,crypted,private_key);

//        Point p = new Point(IntToBig(1),IntToBig(7));
//        Point q = new Point(IntToBig(9),IntToBig(16));
        //test add
//        Point add = Point.Add(IntToBig(1),IntToBig(1),IntToBig(23),p,q);
//        System.out.println(add.x.toString());
//        System.out.println(add.y.toString());
        //test multiply
//        for (int i = 27; i < 29; i++){
//
//            Point mul = Point.Multiply(IntToBig(1),IntToBig(1),IntToBig(23),IntToBig(i),p);
//            if(mul.isInfinite == true)
//                System.out.println("无穷远点");
//            System.out.println("i为"+String.valueOf(i) + "时，点坐标为("+mul.x.toString()+","+mul.y.toString()+")");
//            //System.out.println();
//        }

    }
    public static Curve CreateCurve(int len)
    {
        Curve curve = new Curve(BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO);
        BigInteger a,b,modulus;
        Random random = new Random();
        Point generator;
        BigInteger order;
        while (true)
        {
//            modulus = new BigInteger("211");
//            a = new BigInteger("22");
//            b = new BigInteger("167");

            modulus = new BigInteger(len,100,random).nextProbablePrime();
            a = new BigInteger(len,100,random).mod(modulus);
            b = new BigInteger(len,100,random).mod(modulus);
            curve.a = a;
            curve.b = b;
            curve.modulus = modulus;
            if(!Curve.IsLegal(a,b,modulus))
                continue;
            //check if there is legal generator
            System.out.println("a,b,modules="+a.toString()+","+b.toString()+","+modulus.toString());
            generator = Curve.GetGenerator(curve.a,curve.b,curve.modulus);
            if (generator.x.intValue() == 0 && generator.y.intValue() == 0)
            {
                continue;
            }
            order = Point.CalcOrder(curve.a,curve.b,curve.modulus,generator);
            if(order.subtract(IntToBig(3)).signum() < 0)
                continue;
            else
            {
                curve.generator = generator;
                curve.order = order;
                break;
            }

        }

        return curve;
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
