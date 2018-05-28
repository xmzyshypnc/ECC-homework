import java.math.BigInteger;
import java.util.Random;
/**
 * Created by amazing on 2018/5/25.
 */
public class Curve {
    //y^2 == x^3 + a*x + b (mod modulus)
    BigInteger a,b,modulus,order;
    Point generator;
    public Curve(BigInteger a, BigInteger b, BigInteger modulus)
    {
        this.a = a;
        this.b = b;
        this.modulus = modulus;
    }
    public static boolean IsLegal(BigInteger a,BigInteger b,BigInteger modulus)
    {
        //test if the parameters satisfy the requirment
        if(IntToBig(4).multiply(a.pow(3)).add(IntToBig(27).multiply(b.multiply(b))).intValue() != 0)
            return true;
        else
            return false;

    }
    public static Point GetGenerator(BigInteger a, BigInteger b, BigInteger modulus)
    {
        //find generator
        BigInteger g;
        BigInteger x,y;
        Point generator = new Point(BigInteger.ZERO,BigInteger.ZERO);
        Random random = new Random();
//        int min = 0;
//        int max = modulus.intValue() - 1;
        BigInteger calc_times = BigInteger.ZERO;
        while (true)
        {
            //int s = random.nextInt(max)%(max-min+1) + min;
            while (true)
            {
                x = new BigInteger(modulus.bitLength(),random);
                if (x.subtract(modulus).signum()<0)
                    break;
            }
            //x = IntToBig(8);
            System.out.println("选择的x是："+x.toString());
            g = x.modPow(IntToBig(3),modulus).add(a.multiply(x)).add(b).mod(modulus);
            System.out.println(2333);
            y = CalcSquareRoot(g,modulus);
            System.out.println("寻找生成元已达"+calc_times.toString()+"次");
            //if(calc_times >= (int)Math.pow(2,modulus.bitLength()))
            if(calc_times.bitLength() > modulus.bitLength())
            {
                System.out.println("找不到生成元");
                return generator;
            }
            if((y.intValue() == -1) && (calc_times.bitLength() <= modulus.bitLength()))
            {
                calc_times = calc_times.add(BigInteger.ONE);
                continue;
            }
            else
            {
                System.out.println("生成元是：(" + x.toString()+","+y.toString()+")");
                generator.x = x;
                generator.y = y;
                return generator;
            }

        }
    }
    public static BigInteger CalcSquareRoot(BigInteger g, BigInteger modulus)
    {
        //y^2 == g (mod modulus)
        BigInteger u,y,z;
        u = BigInteger.ZERO;
        y = BigInteger.ZERO;
        z = BigInteger.ZERO;
        BigInteger WrongAns = IntToBig(-1);
        if(modulus.mod(IntToBig(4)).intValue() == 3)
        {
            //p == 3 (mod 4)
            u = modulus.subtract(IntToBig(3)).divide(IntToBig(4));
            y = g.modPow(u.add(BigInteger.ONE),modulus);
            z = y.modPow(IntToBig(2),modulus);
            if(z.toString().equals(g.toString()))
            {
                return y;
            }
            else
            {
                System.out.println("不存在平方根");
                return WrongAns;
            }
        }
        else if (modulus.mod(IntToBig(8)).intValue() == 5)
        {
            u = modulus.subtract(IntToBig(5)).divide(IntToBig(8));
            z = g.modPow(u.multiply(IntToBig(2)).add(BigInteger.ONE), modulus);
            if(z.mod(modulus).intValue() == 1)
            {
                //z = 1 mod p
                y = g.modPow(u.add(BigInteger.ONE),modulus);
            }
            else if(z.mod(modulus).toString().equals(modulus.subtract(BigInteger.ONE).toString()))
            {
                //z = -1 mod p
                BigInteger first = g.multiply(IntToBig(2)).mod(modulus);
                BigInteger second = g.multiply(IntToBig(4)).modPow(u,modulus);
                y = first.multiply(second).mod(modulus);
            }
            else
            {
                System.out.println("不存在平方根");
                return WrongAns;
            }
        }
        else if(modulus.mod(IntToBig(8)).intValue() == 1)
        {
            //p = 1 mod 8
            BigInteger X,Y,U,V;
            Random random = new Random();

            u = modulus.subtract(BigInteger.ONE).divide(IntToBig(8));
            Y = g;

            while (true)
            {
                System.out.println("in loop!!!!!");
                X = new BigInteger(modulus.bitLength(),new Random());
                if (X.subtract(modulus).signum() >= 0)
                    continue;
                int k = u.multiply(IntToBig(4)).add(BigInteger.ONE).intValue();
                //get Lucas
                Point LucasPoint = GenerateLucas(modulus, X, Y, k);
                U = LucasPoint.x;
                V = LucasPoint.y;
                System.out.println("要计算的值为："+U.mod(modulus).intValue());
                if(V.multiply(V).mod(modulus).toString().equals(IntToBig(4).multiply(Y).toString()))
                {
                    System.out.println("6931");
                    if(V.divide(IntToBig(2)).intValue() == 0)
                        y = V.divide(IntToBig(2)).mod(modulus);
                    else
                        y = Point.Inverse(V,IntToBig(2),modulus);
                    return y;
                }

                else if(U.mod(modulus).intValue() != 1 && !U.mod(modulus).toString().equals(modulus.subtract(BigInteger.ONE).toString()))
                {
                    System.out.println("不存在平方根");
                    return WrongAns;
                }
                else
                {
                    continue;
                }
            }

        }
        return y;
    }
    public static Point GenerateLucas(BigInteger p, BigInteger X, BigInteger Y, int k)
    {
        Point point = new Point(BigInteger.ZERO,BigInteger.ZERO);
        BigInteger delta, U, V;
        delta = X.multiply(X).subtract(Y.multiply(IntToBig(4)));
        char[] k_char = Integer.toBinaryString(k).toCharArray();
        U = BigInteger.ONE;
        V = X;
        for (int i = k_char.length-2; i >= 0; i--)
        {
            U = U.multiply(V).mod(p);
            BigInteger U_first, U_second;
            BigInteger Molecular,Denominator;
            Molecular = V.multiply(V).add(delta.multiply(U.multiply(U)));
            Denominator = IntToBig(2);
            if(Molecular.mod(Denominator).intValue() == 0)
                V = Molecular.divide(Denominator).mod(p);
            else
                V = Point.Inverse(Molecular,Denominator,p);

//            BigInteger V_first = V.multiply(V);
//            BigInteger V_second = delta.multiply(U.multiply(U));
//            if (V_first.add(V_second).mod(IntToBig(2)).intValue() == 0)
//                V = V_first.add(V_second).divide(IntToBig(2)).mod(p);
//            else
//                V = Point.Inverse(V_first.add(V_second),IntToBig(2),p);
            if(k_char[i] == '1')
            {
                //new U
                Molecular = X.multiply(U).add(V);
                Denominator = IntToBig(2);
                if(Molecular.mod(Denominator).intValue() == 0)
                    U = Molecular.divide(Denominator).mod(p);
                else
                    U = Point.Inverse(Molecular,Denominator,p);
                //new V
                Molecular = X.multiply(V).add(delta.multiply(U));
                Denominator = IntToBig(2);
                if(Molecular.mod(Denominator).intValue() == 0)
                    V = Molecular.divide(Denominator).mod(p);
                else
                    V = Point.Inverse(Molecular,Denominator,p);
            }
        }
        point.x = U;
        point.y = V;
        return point;

    }
    public static BigInteger IntToBig(int intValue)
    {
        return new BigInteger(String.valueOf(intValue));
    }

}
