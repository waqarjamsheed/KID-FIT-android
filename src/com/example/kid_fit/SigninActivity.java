package com.example.kid_fit;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.kid_fit.broadcastreceiver.WIFIBroadcastReceiver;
import com.example.kid_fit.jsonparser.JSONParser;
import com.example.kid_fit.url.UrlClass;
import com.example.kid_fit.utility.SPVariablesClass;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

@SuppressLint({ "ClickableViewAccessibility", "InflateParams" })
@SuppressWarnings("deprecation")
public class SigninActivity extends Activity {

	//public TextView tv_forgotpass;
	public static final String PREFS_NAME = "LoginPrefs";
	public ProgressDialog pDialog;
	public JSONParser jsonParser = new JSONParser();
	public String status = "-1";
	public static final String TAG_STATUS = "status";
	public String url_signin = UrlClass.url + "login.php";
	public EditText et_email, et_pass;
	public String id, name, email, password, phone, city, country;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signin_ui);

		PackageManager pm = this
				.getPackageManager();
		ComponentName componentName = new ComponentName(
				this,
				WIFIBroadcastReceiver.class);
		pm.setComponentEnabledSetting(
				componentName,
				PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
				PackageManager.DONT_KILL_APP);
		
		pDialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);
		et_email = (EditText) findViewById(R.id.etemail);
		et_pass = (EditText) findViewById(R.id.etpass);
/*		tv_forgotpass = (TextView) findViewById(R.id.tvforgotpass);

		tv_forgotpass.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					tv_forgotpass.setTextColor(Color.rgb(150, 150, 150));
					break;

				case MotionEvent.ACTION_UP:
					tv_forgotpass.setTextColor(Color.WHITE);
					break;
				}

				// Handle ListView touch events.
				v.onTouchEvent(event);
				return true;
			}
		});*/
	}

	public void clicksignup(View v) {
		Intent signupintent = new Intent(SigninActivity.this,
				SignUpActivity.class);
		startActivity(signupintent);
		overridePendingTransition(R.anim.slide_in_activity, R.anim.slide_out_activity);
	}

	public void clicksignin(View v) {
		if (isConnected()) {
			try {
				new Authentication().execute();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Toast.makeText(getApplicationContext(), "No internet connection",
					Toast.LENGTH_LONG).show();

		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	}

	class Authentication extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDialog.setMessage("Authentication in process...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * Creating product
		 * */

		protected String doInBackground(String... args) {
			String inputemail = et_email.getText().toString().trim();
			String inputpassword = et_pass.getText().toString().trim();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("email", inputemail));
			params.add(new BasicNameValuePair("pwd", inputpassword));

			JSONObject json = jsonParser.makeHttpRequest(url_signin, "POST",
					params);
			if (json != null) {
				try {
					// Checking for SUCCESS TAG
					status = json.getString(TAG_STATUS);
					if (status.equals("1")) {

						// Storing each json item in variable
						id = json.getString("id");
						name = json.getString("name");
						email = json.getString("email");
						password = json.getString("password");
						phone = json.getString("phone");
						city = json.getString("city");
						country = json.getString("country");
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return status;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String response) {
			// dismiss the dialog once done

			pDialog.dismiss();
			// Toast.makeText(getApplicationContext(),"Done"+file_url,
			// Toast.LENGTH_LONG).show();
			if (response.equals("-1")) {
				Toast.makeText(getApplicationContext(),
						"Server is not responding", Toast.LENGTH_LONG).show();
			} else if (response.equals("0")) {
				Toast.makeText(getApplicationContext(),
						"Invalid UserName or Password", Toast.LENGTH_LONG)
						.show();
			} else if (response.equals("1")) {

				SPVariablesClass.id = id;
				SPVariablesClass.name = name;
				SPVariablesClass.email = email;
				SPVariablesClass.password = password;
				SPVariablesClass.phone = phone;
				SPVariablesClass.city = city;
				SPVariablesClass.country = country;
				SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("signin", "signin");
				editor.putString("id", id);
				editor.putString("name", name);
				editor.putString("email", email);
				editor.putString("password", password);
				editor.putString("phone", phone);
				editor.putString("city", city);
				editor.putString("country", country);
				editor.commit();
				finish();
				Intent signinintent = new Intent(SigninActivity.this,
						HomeActivity.class);
				startActivity(signinintent);
				overridePendingTransition(R.anim.slide_in_activity, R.anim.slide_out_activity);

				Toast.makeText(
						getApplicationContext(),
						"Welcome "+ name, Toast.LENGTH_LONG)
						.show();
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		InputMethodManager inputMethodManager = (InputMethodManager) this
				.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus()
				.getWindowToken(), 0);
		return super.onTouchEvent(event);
	}

	public boolean isConnected() {

		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isConnected())
			return true;
		else
			return false;
	}

}
