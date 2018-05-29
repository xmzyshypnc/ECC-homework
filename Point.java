import java.math.BigInteger;
import java.util.Random;

/**
 * Created by amazing on 2018/5/15.
 */
public class Point {
    BigInteger x = BigInteger.ZERO;
    BigInteger y = BigInteger.ZERO;
    boolean isInfinite;
    public Point(BigInteger x, BigInteger y)
    {
        this.x = x;
        this.y = y;
        isInfinite = false;
    }
    public static boolean IsOnCurve(BigInteger a, BigInteger b, BigInteger modulus, Point point)
    {
        //judge if the point is on the elliptic curve
        //y^2 = x^3 + a*x + b (mod p)
        if (point.isInfinite)
            return true;
        BigInteger CalcRes;
        CalcRes = point.x.multiply(point.x).multiply(point.x).add(a.multiply(point.x)).add(b).mod(modulus);
        if (CalcRes.toString().equals(point.y.multiply(point.y).mod(modulus).toString()))
        {
            return true;
        }
        else
        {
            return false;
        }

    }
    public static Point Add(BigInteger a, BigInteger b, BigInteger modulus,Point p, Point q)
    {
        if((!IsOnCurve(a,b,modulus,p) || !IsOnCurve(a,b,modulus,q)) && (p.isInfinite == false) && (q.isInfinite ==false))
        {
            System.out.println("选取点不在曲线上");
            Point WrongAns = new Point(BigInteger.ZERO.subtract(BigInteger.ZERO),BigInteger.ZERO.subtract(BigInteger.ZERO));
            return WrongAns;
        }

        BigInteger x1 = p.x.mod(modulus);
        BigInteger y1 = p.y.mod(modulus);
        BigInteger x2 = q.x.mod(modulus);
        BigInteger y2 = q.y.mod(modulus);
        BigInteger x3,y3;

        //calc slope
        BigInteger slope = BigInteger.ZERO;
        Point AddRes = new Point(BigInteger.ZERO,BigInteger.ZERO);
        //slope = CalcSlope(p, q);
        //p = 0
        if (p.isInfinite == true)
            return q;
        //q = 0
        else if(q.isInfinite == true)
            return p;
        //p + q = 0
        else if(x1.toString().equals(x2.toString()) && y1.add(y2).mod(modulus) == BigInteger.ZERO)
        {
            AddRes.isInfinite = true;

        }
        else
        {
            //System.out.println("666");
            slope = CalcSlope(a, b, modulus, p, q);
        }
        //System.out.println("斜率是：" + slope.toString());
        //calc x3 && y3
        x3 = slope.multiply(slope).subtract(x1).subtract(x2).mod(modulus);
        y3 = slope.multiply(x1.subtract(x3)).subtract(y1).mod(modulus);
        AddRes.x = x3;
        AddRes.y = y3;
        return AddRes;
    }

    public static Point Multiply(BigInteger a, BigInteger b, BigInteger modulus, BigInteger x_a, Point p)
    {
        Point MulRes = new Point(IntToBig(-1),IntToBig(-1));
        if(!IsOnCurve(a,b,modulus,p) && p.isInfinite == false)
        {
            System.out.println("点不在曲线上，请重新选取点");
            return MulRes;
        }
        BigInteger x = p.x.mod(modulus);
        BigInteger y = p.y.mod(modulus);
        Point q = new Point(x,y);
        BigInteger slope;
        for (BigInteger i = BigInteger.ZERO; i.subtract(x_a.subtract(BigInteger.ONE)).signum() < 0; i=i.add(BigInteger.ONE)){
            Point temp = Add(a,b,modulus,p,q);
            if(temp.isInfinite == false)
                q = temp;
            else
            {
                q = temp;
                q.isInfinite = true;
            }
        }
        return q;

    }

    public static BigInteger CalcSlope(BigInteger a, BigInteger b, BigInteger modulus,Point p, Point q)
    {
        BigInteger slope;
        BigInteger x1 = p.x.mod(modulus);
        BigInteger y1 = p.y.mod(modulus);
        BigInteger x2 = q.x.mod(modulus);
        BigInteger y2 = q.y.mod(modulus);
        //
        BigInteger Molecular;
        BigInteger Denominator;
        if (x1.toString().equals(x2.toString()) && y1.toString().equals(y2.toString()) )
        {

            Molecular = x1.multiply(x1).multiply(new BigInteger(new String("3"))).add(a).mod(modulus);
            Denominator = y1.multiply(new BigInteger(new String("2"))).mod(modulus);

        }
        else
        {
            //System.out.println(233);
            Molecular = y2.subtract(y1).mod(modulus);
            Denominator = x2.subtract(x1).mod(modulus);
        }
        //求逆
//        System.out.println("分子是：" + Molecular);
//        System.out.println("分母是：" + Denominator);
        slope = Inverse(Molecular, Denominator, modulus);
        return slope;
    }
    public static BigInteger Inverse(BigInteger a, BigInteger b, BigInteger modulus)
    {
        //b * res = a(mod p)
        for (BigInteger i = BigInteger.ZERO; i.subtract(modulus).signum()<0; i=i.add(BigInteger.ONE))
        {
            if(b.multiply(new BigInteger(String.valueOf(i))).subtract(a).mod(modulus) == BigInteger.ZERO)
            {
                return i;
            }
        }
        return new BigInteger(String.valueOf(-1));
    }
    public static BigInteger CalcOrder(BigInteger a, BigInteger b, BigInteger modulus, Point generator)
    {
        BigInteger Order = BigInteger.ZERO;
        Random random = new Random();
        for (BigInteger i = IntToBig(2); i.bitLength()<=modulus.bitLength(); i = i.add(BigInteger.ONE))
        {
            Point mul = Point.Multiply(a,b,modulus,i,generator);
            if(mul.isInfinite == true)
            {
                System.out.println("无穷远点");
                System.out.println("i为"+i.toString() + "时，点坐标为("+mul.x.toString()+","+mul.y.toString()+")");
                Order = i;
                break;
            }
            System.out.println("计算阶次数："+i.toString());
        }
        return Order;
    }
    public static BigInteger IntToBig(int intValue)
    {
        return new BigInteger(String.valueOf(intValue));
    }
}
