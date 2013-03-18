package org.tookscms.digitalectoplasm;

import android.app.Application;
import android.content.Context;

public class DigitalEctoplasm extends Application {
	private static Context context;
	
	public void onCreate() {
		super.onCreate();
		DigitalEctoplasm.context = getApplicationContext();
	}
	
	public static Context getAppContext() {
		return DigitalEctoplasm.context;
	}
}
