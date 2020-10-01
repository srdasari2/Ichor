package io.particle.hydroalert.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

import com.cimosys.common.encryption.Encryption;
import com.cimosys.common.encryption.Encryption.CipherInitializationException;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;


public class EncryptionUtil {
     private String UDID = null;
     private byte[] salt;
     private byte[] iVector;
     private static final String INSTALLATION = "INSTALLATION";

     
    public synchronized void init(Context mContext) {
        if (UDID == null) {  
            File installation = new File(mContext.getFilesDir(), INSTALLATION);
            try {
                if (!installation.exists()){
                    writeInstallationFile(installation);
                    UDID = readInstallationFile(installation);
                    salt = generateSalt();
                    iVector = generateIVector();
                    saveCredentials(mContext);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String readInstallationFile(File installation) throws IOException {
    	RandomAccessFile f = null;
    	try{
         f = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    	}finally{
    	if(f != null){
   			 f.close();
   		 }
    	}
     
    }

    public void writeInstallationFile(File installation) throws IOException {
    	FileOutputStream out = null;
    	try{
        out = new FileOutputStream(installation);
        String id = UUID.randomUUID().toString();
        out.write(id.getBytes());
        out.close();
    	 }finally{
    		 if(out != null){
    			 out.close();
    		 }
    	 }
    }

    public byte[] generateSalt() throws CipherInitializationException{
        Encryption encryption = Encryption.newInstance();
        byte[] salt = encryption.generateSalt();
        return salt;
        
    }
    
    //InitializationVector
    public byte[] generateIVector() throws CipherInitializationException{
        Encryption encryption = Encryption.newInstance();
        byte[] iVector = encryption.generateInitializationVector();
        return iVector;
    }
    
    public void saveCredentials(Context mContext){
    //Store in Shared Preferences
    SharedPreferences SP = mContext.getSharedPreferences("encryption", Context.MODE_PRIVATE);
    
    //Convert to string to store in Shared Preferences
    String strSalt = Base64.encodeToString(salt, Base64.DEFAULT);
    String strInitializationVector = Base64.encodeToString(iVector, Base64.DEFAULT);
    
    SP.edit().putString("pWord", UDID).commit();
    SP.edit().putString("salt", strSalt).commit();
    SP.edit().putString("iVector", strInitializationVector).commit();
    }
}

