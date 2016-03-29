This is the directory that contains all cryptographic code.

If you are adding on experimental or assigned code (e.g.
you are a student) then create a directory named after
yourself and put your crypto code inside there.

Suppose your name is John Doe and you create a Java class
called CleverCrypto.  Then you should do as follows:

1. Create a directory called JohnDoe inside this directory
2. Put the file CleverCrypto.java inside JohnDoe.  The path
   of the file is webcrypt/crypto/JohnDoe/CleverCrypto.java
3. Make CleverCrypto part of the webcrypt.crypto.JohnDoe package with
   the statement
package webcrypt.crypto.JohnDoe;
   as the first line of code of CleverCrypto.java
4. It's a good idea to import classes you'll be using with commands
   such as
		import webcrypt.crypto.*;
5. From inside webcrypt/crypto/JohnDoe/ compile with the command:
java -classpath fullpath_to_webcrypt CleverCrypto.java

For UNIX assuming you put webcrypt in your home directory
fullpath_to_webcrypt may look like /home/JohnDoe

For WINDOWS assuming you put webcrypt in a directory called "programs"
inside of the C drive, fullpath_to_webcrypt would be C:\programs

----
Adding new cryptographic algorithms:

o You will need to extend the Kernel and Key classes. 
o If implementing a public key algorithm you will need to implement
  the Kernel subclass PublicKeyKernel and the Key subclass AsymmetricKey.
o Then you will need to modify the code in webcrypt/gui/WebBrowser.java
  as follows:
  *  Search for the section with the declaration
  final static Kernel[] KERNELS = new Kernel[NUMKERNELS];
  *  Change the number of kernels to the number of kernels you wish to display.
  *  Install your kernel into the KERNELS array with a line such as:
    KERNELS[5] = new webcrypt.crypto.JohnDoe.YourKernel();
     if you wish YourKernel to be the 6th displayed kernel in the crypto
     menu of the web browser.  Note that you should give the full path
     of YourKernel which explains the ugly looking line above.
o Compile as instructed above including the modified webcrypt.gui.WebBrowser
----
Zeph Grunschlag (zeph@cs.columbia.edu)



