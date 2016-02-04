package com.example.kid_fit.broadcastreceiver;

import com.example.kid_fit.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.widget.Toast;

public class WIFIBroadcastReceiver extends BroadcastReceiver {

	public WifiManager wifiManager;
	public MediaPlayer mp1;

	public void onReceive(Context context, Intent intent) {

		mp1 = MediaPlayer.create(context, R.raw.wifioff);
		wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);

		if (!wifiManager.isWifiEnabled()) {
			mp1.start();
			Toast.makeText(
					context,
					"Your WIFI connection has been turned off",
					Toast.LENGTH_LONG).show();
		} 
	}

}
