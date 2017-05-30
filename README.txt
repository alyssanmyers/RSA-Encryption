3460:435 Algorithms Project 1 — RSA & Digital Signature 02/13/2017
Alyssa Myers

General Description
———————————————————
RSA encryption applied to a digital signature.

Implementation
——————————————
The program is composed of 5 classes:
* Encryptor - Handles encryption and decryption of a file using SHA-256 using the keys obtained by the RSA class.
* RSA - Class that runs the main function that creates a KeyPair to be used in encryption.
* KeyPair - Holds the PublicKey and PrivateKey.
* PublicKey - A structure to hold (e, n).
* PrivateKey - A structure to hold (d, n).

Compilation
———————————
1) Go to the project directory via command line.
2) RSA is to be ran to create the keys necessary for encryption.
3) Then Encryptor can be ran if there are public and private keys saved in the appropriate files.
4) The first command line argument is either -s (decryption) or -v (encryption).
5) The second command line argument is for the name of the file.

Part I: RSA key generation
——————————————————————————
o Implement Fermat test;
o Use Fermat’s test to generate two large prime numbers (p,q), each should have a size ~512 bits;
o Save p and q in a file named p_q.txt (one integer per line);
o Use extended Euclidean algorithm to generate two pairs of keys: (e,n), (d,n), where n MUST be at least 1024 bit long;
o Save the two pairs of keys in two separate files: e_n.txt and d_n.txt (one integer per line);
 
Part II: Generate and verify digital signatures using a SHA-256 hash
————————————————————————————————————————————————————————————————————
o Sign a given file
o Generate a SHA-256 hash of the content of the file (e.g., "file.txt", “file.exe”);
o Sign/"decrypt" this hash value using the private key stored in d_n.txt;
o Save the signed version of the file to "file.txt.signed"
o Verify the signed file
o Read the signed file;
o Generate a SHA-256 hash of the content portion of the signed file;
o "encrypt" saved hash value using the public key stored in e_n.txt;
o Compare the hash value and the encrypted value;
o Report whether the document is authentic or modified.
