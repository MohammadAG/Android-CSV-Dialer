package com.mohammadag.csvdialer;

import java.util.Set;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class DialerService extends Service {

	String[] mNumbers;
	int mCurrentPos = 0;
	Handler mHandler = new Handler();
	SharedPreferences mPreferences;

	PhoneStateListener mListener = new PhoneStateListener() {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				dialNextNumber();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		Set<String> mNumbersSet = mPreferences.getStringSet("numbers", null);
		if (mNumbersSet != null) {
			mNumbers = mNumbersSet.toArray(new String[mNumbersSet.size()]);
		}

		((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).listen(mListener,
				PhoneStateListener.LISTEN_CALL_STATE);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void dialNextNumber() {
		if (mCurrentPos >= mNumbers.length) {
			Toast.makeText(getApplicationContext(), "No more numbers to dial", Toast.LENGTH_SHORT).show();
			mPreferences.edit().remove("numbers").commit();
			stopSelf();
			return;
		}

		final String number = mNumbers[mCurrentPos];
		Log.d("DialerThingy", "Dialing number: " + number);

		mHandler.postDelayed(new Runnable() {	
			@Override
			public void run() {
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:" + number));
				callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(callIntent);
			}
		}, 2000);

		mCurrentPos ++;
	}
}
