/***********************************************/
/* Alyssa Myers
/* 3460:435 Algorithms
/* Project 1 — RSA & Digital Signature
/* February 13, 2017
/*
/* RSA encryption applied to a digital signature.
/***********************************************/

import java.io.*;
import java.math.BigInteger;
import java.util.Random;

public class rsa435
{
    public static void main(String[] args)
    {
        PublicKey pub = null;
        PrivateKey priv = null;

        KeyPair pair = new KeyPair(pub,priv);
        pair.generateKeys();

    }
}

class PublicKey
{
    private BigInteger e;
    private BigInteger n;

    public PublicKey(BigInteger e, BigInteger n)
    {
        this.e = e;
        this.n = n;
    }

    public BigInteger getE() { return e; }
    public BigInteger getN() { return n; }
}

class PrivateKey
{
    private BigInteger n;
    private BigInteger d;

    public PrivateKey(BigInteger d, BigInteger n)
    {
        this.d = d;
        this.n = n;
    }

    public BigInteger getD() { return d; }
    public BigInteger getN() { return n; }
}

class KeyPair
{
    private final PublicKey pub;
    private final PrivateKey priv;

    public KeyPair()
    {
        pub = null;
        priv = null;
    }

    public KeyPair(PublicKey pub, PrivateKey priv)
    {
        this.pub = pub;
        this.priv = priv;
    }

    public KeyPair generateKeys()
    {
        BigInteger p, q, n, t, e, d;
        PublicKey pub; PrivateKey priv;
        KeyPair pair;

        // use Fermat's test to generate two large prime numbers (p,q),
        // each should have a size ~512 bits.
        p = getPrime();
        do {
            q = getPrime();
        } while (p.equals(q));

        // save p and q in a file named "p_q.txt" (one integer per line).
        writeFile("p_q.txt",p,q);

        n = p.multiply(q);
        t = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
        e = n.subtract(BigInteger.ONE);
        for (BigInteger a = e; a.compareTo(BigInteger.ZERO) > 0; a = a.subtract(BigInteger.ONE))
        {
            if (Euclid(a,t).equals(BigInteger.ONE)) { e = a; break; }
        }
        d = inverse(e,t);

        // save the two pairs of keys in two separate files:
        // e_n.txt and d_n.txt (one integer per line).
        writeFile("e_n.txt",e,n);
        writeFile("d_n.txt",d,n);

        pub = new PublicKey(e,n);
        priv = new PrivateKey(d,n);

        pair = new KeyPair(pub,priv);

        return pair;
    }
    public PublicKey getPublicKey() { return pub; }
    public PrivateKey getPrivateKey() { return priv; }

    private static void writeFile(String f, BigInteger a, BigInteger b)
    {
        BufferedWriter out;
        try {
            File file = new File(f);
            out = new BufferedWriter(new FileWriter(file));
            out.write(a.toString() + "\n" + b.toString());
            out.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static BigInteger getPrime()
    {
        Random r = new Random();

        boolean foundPrime = false;
        BigInteger bigInt;

        // searching for a prime
        bigInt = BigInteger.probablePrime(512, r);
        do {
            if (isPrime(bigInt)) {
                foundPrime = true;
            } else {
                bigInt = bigInt.nextProbablePrime();
            }
        } while (!foundPrime);
        return bigInt;
    }

    // test to see if the number is prime using Fermat's Little Theorem.
    // for every 1 <= a < p
    private static boolean isPrime(BigInteger p)
    {
        BigInteger a = p.divide(BigInteger.ONE.add(BigInteger.ONE));

        if (!(fermat(a, p.subtract(BigInteger.ONE), p) .equals(BigInteger.ONE)))
        {
            return false;
        }

        return true;
    }

    public static BigInteger fermat (BigInteger x, BigInteger y, BigInteger n)
    {
        return (x.modPow(y,n));
    }

    private static BigInteger inverse(BigInteger a, BigInteger n)
    {
        BigInteger[] bigInts = ExtendedEuclid(a,n);
        if (!bigInts[0].equals(BigInteger.ONE))
        {
            System.out.println("Error: Are not relatively prime.");
        }

        if (bigInts[1].compareTo(BigInteger.ZERO) == 1)
        {
            return bigInts[1];
        }
        else
            return bigInts[1].add(n);
    }

    private static BigInteger Euclid(BigInteger a, BigInteger b)
    {
        if (b.equals(BigInteger.ZERO))
        {
            return a;
        }
        else
            return Euclid(b, a.mod(b));
    }

    private static BigInteger[] ExtendedEuclid(BigInteger a, BigInteger b)
    {
        BigInteger x, y;
        BigInteger[] bigInts = new BigInteger[3];

        if (b.equals(BigInteger.ZERO))
        {
            bigInts[0] = a;
            bigInts[1] = BigInteger.ONE;
            bigInts[2] = BigInteger.ZERO;
            return bigInts;
        }
        bigInts = ExtendedEuclid(b, a.mod(b));

        x = bigInts[1];
        y = bigInts[2];
        bigInts[1] = y;
        bigInts[2] = x.subtract(y.multiply(a.divide(b)));

        return bigInts;
    }
}
