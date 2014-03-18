package com.mohammadag.csvdialer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MainActivity extends Activity {

	private SharedPreferences mPreferences;
	private TextView mTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		mTextView = (TextView) findViewById(R.id.textView1);
		findViewById(R.id.start_button).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startService(new Intent(getApplicationContext(), DialerService.class));
				finish();
			}
		});

		findViewById(R.id.browse_button).setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				onButtonClicked();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_about) {
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
			builder.setTitle(R.string.app_name);
			builder.setMessage(R.string.copyright);
			builder.show();
		}
		return super.onOptionsItemSelected(item);
	}

	private void onButtonClicked() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("file/*");
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {
			Uri csvPath = data.getData();
			Log.d("DialerThingy", csvPath.getEncodedPath());
			File file = new File(csvPath.getEncodedPath());
			FileInputStream stream;
			try {
				stream = new FileInputStream(file);
				int content;
				String sb = "";
				while ((content = stream.read()) != -1) {
					sb += (char) content;
					System.out.print((char) content);
				}

				String csv = sb.toString();
				mTextView.setText(csv);
				String[] numbers = csv.split(",");
				HashSet<String> numbersSet = new HashSet<String>();
				for (String number : numbers) {
					if (!TextUtils.isEmpty(number)) {
						numbersSet.add(number);
						Log.d("DialerThingy", "Got number " + number);
					}
				}

				mPreferences.edit().putStringSet("numbers", numbersSet).commit();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		super.onActivityResult(requestCode, resultCode, data);
	}
}
