package webcrypt.crypto;

/** 
 *  The VigenereKey class implements a simple repeating text based key.
 *  When any text is inputted, the key is created as follows:
 *  1.  Any non-letters are removed
 *  2.  Uppercase vs. lowercase distinctions are ignored
 *  3.  A = 0, B = 1, ... , Z = 25
 *  4.  The effective key is created which is an array of numbers telling
 *      how much to shift by.
 *
 * EG:  Suppose the key text was "Cool Man!"
 *      1.  keep only "CoolMan"
 *      2.  capitalize to "COOLMAN"
 *      3-4.effective key is {2, 14, 14, 11, 12, 0, 13}
 */
public class VigenereKey extends Key{
    private String readableKey;
    private char[] effectiveKey;

    public String getSeedKey(){
	return seedKey;
    }
    public String getReadableKey(){
	return readableKey;
    }
    public char[] getEffectiveKey(){
	return effectiveKey;
    }

    /**
     * Create effective key in accordance to steps 1-4 above
     */
    public VigenereKey(String keytext) throws KeyCreationException{
	super(keytext);
	seedKey = keytext;
	
	//create readable key by capitalizing and removing non-letters
	readableKey = keytext.toUpperCase();
	StringBuffer temp = new StringBuffer(readableKey);
	for(int i = 0; i < temp.length(); ){
	    if( !Character.isLetter(temp.charAt(i)) )
		temp.deleteCharAt(i);
	    else
		i++;
	}
	readableKey = temp.toString();

	//create char-array version with A=0, B=1 ... etc
	effectiveKey = readableKey.toCharArray();
	for (int i=0; i<effectiveKey.length; i++)
	    effectiveKey[i] -= 65;
    }

    public Object clone(){  
	Object x = null;
	try{
	    x = new VigenereKey(seedKey); 
	}catch(KeyCreationException e){
	    // can't happen since key already got created
	}
	return x;
    }
    
    public Key reverseKey(){
	VigenereKey vk = (VigenereKey)clone();
	for (int i = 0; i < vk.effectiveKey.length; i++){
	    vk.effectiveKey[i] = (char)(26-vk.effectiveKey[i]);
	    vk.effectiveKey[i] %= 26;
	}
	vk.setReverseKey(true);
	return vk;
    }

    public static void main(String[] args) throws KeyCreationException{
	VigenereKey vkey = new VigenereKey(args[0]);
	System.out.println("Seed:      "+vkey.getSeedKey());
	System.out.println("Readable:  "+vkey.getReadableKey());
	System.out.println("Effective: ");
	for(int i = 0; i<vkey.getEffectiveKey().length; i++)
	    System.out.print(0+vkey.getEffectiveKey()[i]+",");
	System.out.println("");
	VigenereKey vkclone = (VigenereKey)vkey.clone();
	System.out.println("Clone reverse? "+vkclone.isReverseKey());
	System.out.println("Clone Seed:      "+vkclone.getSeedKey());
	System.out.println("Clone Readable:  "+vkclone.getReadableKey());
	System.out.println("Effective: ");
	for(int i = 0; i<vkclone.getEffectiveKey().length; i++)
	    System.out.print(0+vkclone.getEffectiveKey()[i]+",");
	System.out.println("");
	VigenereKey vkreverse = (VigenereKey)vkey.reverseKey();
	System.out.println("Reverse is reverse? "+vkreverse.isReverseKey());
	System.out.println("Reverse Seed:      "+vkreverse.getSeedKey());
	System.out.println("Reverse Readable:  "+vkreverse.getReadableKey());
	System.out.println("Effective: ");
	for(int i = 0; i<vkreverse.getEffectiveKey().length; i++)
	    System.out.print(0+vkreverse.getEffectiveKey()[i]+",");
	System.out.println("");
    }

}


