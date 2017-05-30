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
import java.security.MessageDigest;

public class sign
{
    public static void main(String[] args)
    {
        String identifier, file;
        BigInteger[] k;
        BigInteger e, d, n;
        PublicKey pub;
        PrivateKey priv;
        KeyPair pair;
        Encryptor encryptor;

        /**************************/
        /* reading keys from file */
        /**************************/
        k = getKeys("e_n.txt");
        e = k[0];
        n = k[1];
        k = getKeys("d_n.txt");
        d = k[0];

        pub = new PublicKey(e, n);
        priv = new PrivateKey(d, n);
        pair = new KeyPair(pub, priv);

        /*****************************/
        /* creating encryptor object */
        /*****************************/
        encryptor = new Encryptor(pair);

        /*****************************/
        /****** command handling *****/
        /*****************************/
        identifier = args[0];
        file = args[1];

        switch (identifier) {
            case "s": {
                encryptor.decrypt(file);
                break;
            }
            case "v": {
                encryptor.encrypt(file);
                break;
            }
        }
    }

    public static BigInteger[] getKeys(String f)
    {
        BigInteger[] rtn = { BigInteger.ZERO, BigInteger.ONE };
        File file;
        FileInputStream fis;
        BufferedReader br;
        String l;

        try {
            file = new File(f);
            fis = new FileInputStream(file);
            br = new BufferedReader(new InputStreamReader(fis));

            l = br.readLine();
            rtn[0] = new BigInteger(l);
            l = br.readLine();
            rtn[1] = new BigInteger(l);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rtn;
    }
}

class Encryptor
{
    private final KeyPair keyPair;

    // creates an encryptor object
    public Encryptor (KeyPair keyPair)
    {
        this.keyPair = keyPair;
    }

    public void decrypt (String m)
    {
        FileReader f;
        BufferedReader br;
        byte[] digest;
        BigInteger d, n, message, signature;
        String text = "", line;
        PrivateKey key;

        File file = new File(m);
        File newFile = new File(m + ".signed");
        
        int msgSize = (int) file.length();
        byte[] msg = new byte[msgSize];
        
        // getting private key
        key = keyPair.getPrivateKey();
        d = key.getD();
        n = key.getN();
        
        try
        {
            /*******************************************/
            /******** Generate a SHA256 hash of ********/
            /********* the content of the file *********/
            /*******************************************/
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(msg, 0, msg.length);
            buf.close();
            digest = generateHash(msg);

            /*******************************************/
            /*** Sign / decrypt the hash value using ***/
            /**** the private key stored in d_n.txt ****/
            /*******************************************/
            message = new BigInteger(1, digest); // M
            signature = message.modPow(d, n);
        
            /*******************************************/
            /*** Save the signed version of the file ***/
            /*********** to file.txt.signed ************/
            /*******************************************/
            FileOutputStream fos = new FileOutputStream(newFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            
            oos.writeObject(msg);
            oos.writeObject(signature.toByteArray());
            
            fos.close();
            oos.close();
        } catch (Exception ex) {
                ex.printStackTrace();
        }
    }

    public void encrypt (String m)
    {
        String original, data, s;
        PrintWriter f;
        byte[] digest;
        BigInteger e, n, signature, message, encrypted;
        PublicKey pubKey;
        
        // getting public key
        pubKey = keyPair.getPublicKey();
        e = pubKey.getE();
        n = pubKey.getN();
        original = m.replace(".signed", "");
        
        File file = new File(m);
        File ogFile = new File(original);
        
        int msgSize = (int) file.length() - 128;
        byte[] msg = new byte[msgSize];
        
        int sigSize = (int) file.length() - msgSize;
        byte[] sig = new byte[148];
        
        int end = msgSize + sigSize;
        
        String valid = "The document is authentic.";
        String invalid = "The document has been modified.";

        // testing if valid signature
        try
        {
            /*******************************************/
            /*********** Read the signed file **********/
            /*******************************************/
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            
            msg = (byte[]) ois.readObject();
            sig = (byte[]) ois.readObject();
            
            fis.close();
            ois.close();
            
            /*******************************************/
            /******** Generate a SHA256 hash of ********/
            /********* the content of the file *********/
            /*******************************************/
            digest = generateHash(msg);

            /*******************************************/
            /****** Encrypt saved hash value using *****/
            /***** the public key stored in e_n.txt ****/
            /*******************************************/
            message = new BigInteger(1,digest); // M
            signature = new BigInteger(sig);
            encrypted = signature.modPow(e, n); // (M^d%n)^e%n

            /********************************************/
            /*** Compare the hash and encrypted value ***/
            /********************************************/
            if (message.compareTo(encrypted) == 0)
            {
                try
                {
                    f = new PrintWriter(original);
                    f.print(new String(msg));
                    f.close();
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                }
                System.out.println(valid);

            } else
            {

                System.out.println(invalid);
            }
        } catch (StreamCorruptedException ex)
        {
            System.out.println(invalid);
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static byte[] generateHash(byte[] data)
    {
        MessageDigest md;
        byte[] digest = null;

        try
        {
            // create class instance to create SHA-256 hash
            md = MessageDigest.getInstance("SHA-256");

            // process the file
            md.update(data);

            // generate a hash of the file
            digest = md.digest();

        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return digest;
    }
}
