package com.brianreber.cokerewards;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

/**
 * Login/Main activity for the app
 * 
 * @author breber
 */
public class RegisterActivity extends Activity {

	/**
	 * Google Analytics tracker
	 */
	private GoogleAnalyticsTracker tracker;

	/**
	 * A Runnable that will update the UI with values stored
	 * in SharedPreferences
	 */
	private Runnable updateUIRunnable = new Runnable() {
		@Override
		public void run() {
			if (CokeRewardsActivity.isLoggedIn(RegisterActivity.this)) {
				RegisterActivity.this.finish();
			}
		}
	};


	/**
	 * Set up the basic UI elements
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.login);

		tracker = GoogleAnalyticsTracker.getInstance();

		Button login = (Button) findViewById(R.id.performLogin);
		login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getNumberOfPoints();
			}
		});
	}

	/**
	 * Get the number of points from the MyCokeRewards server
	 */
	private void getNumberOfPoints() {
		EditText txt = (EditText) findViewById(R.id.username);
		final String emailAddress = txt.getText().toString();

		txt = (EditText) findViewById(R.id.password);
		final String password = txt.getText().toString();

		SharedPreferences prefs = getSharedPreferences(CokeRewardsActivity.COKE_REWARDS, Context.MODE_WORLD_WRITEABLE);
		Editor edit = prefs.edit();

		edit.putString(CokeRewardsActivity.EMAIL_ADDRESS, emailAddress);
		edit.putString(CokeRewardsActivity.PASSWORD, password);
		edit.commit();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					tracker.trackEvent("Logon", "Logon", "Logging on", 0);

					CokeRewardsActivity.getData(RegisterActivity.this,
							CokeRewardsRequest.createLoginRequestBody(RegisterActivity.this),
							updateUIRunnable);
				} catch (Exception e) {
					tracker.trackEvent("Exception", "ExceptionLogon", "Exception when trying to log on: " + e.getMessage(), 0);
					e.printStackTrace();
				}
			}
		}).start();
	}
}