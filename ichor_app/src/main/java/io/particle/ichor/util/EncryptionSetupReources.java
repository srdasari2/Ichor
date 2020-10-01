package io.particle.hydroalert.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Base64;

import com.cimosys.common.encryption.Encryption;
import com.cimosys.common.encryption.Encryption.CipherInitializationException;
import com.cimosys.common.encryption.SimpleCipher;


public class EncryptionSetupReources{

	private static EncryptionSetupReources singleton;
	protected Resources resources;
	protected android.content.res.Configuration conf;
	public SimpleCipher aesCiph;
	private static Encryption encryption;
	private static String pwordSP;
	private static String saltSP ;
	private static String iVectorSP;

	
	private EncryptionSetupReources(Context mContext) {
		
		 EncryptionUtil encryptUtil =  new EncryptionUtil();
			encryptUtil.init(mContext);
            SharedPreferences encryptPref = mContext.getSharedPreferences("encryption", Context.MODE_PRIVATE);
			pwordSP = encryptPref.getString("pWord", null);
			saltSP  = encryptPref.getString("salt", null);
			iVectorSP = encryptPref.getString("iVector", null);
			try {
				encryption = Encryption.newInstance();
                aesCiph = encryption.createCipher(pwordSP, Base64.decode(saltSP, Base64.DEFAULT), Base64.decode(iVectorSP, Base64.DEFAULT));

			} catch (CipherInitializationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
	}	
	public static EncryptionSetupReources getInstance(Context mContext) {

		if ( singleton == null ) {
			singleton = new EncryptionSetupReources(mContext);
		}
		
		return singleton;
	}
	
	
}




