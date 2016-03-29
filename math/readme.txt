This is the directory that contains all purely mathematical code.

If you are adding on experimental or assigned code (e.g.
you are a student) then create a directory named after
yourself and put your math code inside there.

Suppose your name is John Doe and you create a Java class
called NumberTheory.  Then you should do as follows:

1. Create a directory called JohnDoe
2. Put the file NumberTheory.java inside JohnDoe.  The path
   of the file is webcrypt/math/JohnDoe/NumberTheory.java
3. Make NumberTheory part of the webcrypt.math.JohnDoe package with
   the statement
package webcrypt.math.JohnDoe;
   as the first line of code of NumberTheory.java
4. From inside webcrypt/math/JohnDoe/ compile with the command:
java -classpath fullpath_to_webcrypt NumberTheory.java

For UNIX assuming you put webcrypt in your home directory
fullpath_to_webcrypt may look like /home/JohnDoe

For WINDOWS assuming you put webcrypt in a directory called "programs"
inside of the C drive, fullpath_to_webcrypt would be C:\programs

Zeph Grunschlag (zeph@cs.columbia.edu)