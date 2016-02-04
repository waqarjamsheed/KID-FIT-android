package com.example.kid_fit;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.kid_fit.jsonparser.JSONParser;
import com.example.kid_fit.url.UrlClass;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
public class SignUpActivity extends Activity {

	public static final String PREFS_NAME = "LoginPrefs";
	public ProgressDialog pDialog;
	public JSONParser jsonParser = new JSONParser();
	public int status = -1;
	public static final String TAG_STATUS = "status";
	public String url_signup = UrlClass.url + "signup.php";
	public EditText et_name, et_email, et_pass, et_phone, et_country, et_city;
	public String inputname, inputemail, inputpassword, inputphone, inputcity,
			inputcountry;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup_ui);

		pDialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);
		et_name = (EditText) findViewById(R.id.etname);
		et_email = (EditText) findViewById(R.id.etemail);
		et_pass = (EditText) findViewById(R.id.etpass);
		et_phone = (EditText) findViewById(R.id.etphone);
		et_country = (EditText) findViewById(R.id.etcountry);
		et_city = (EditText) findViewById(R.id.etcity);
	}

	public void clicksubmitsignup(View v) {
		if (!et_name.getText().toString().trim().equals("")
				&& !et_name.getText().toString().trim().equals("")
				&& !et_email.getText().toString().trim().equals("")
				&& !et_pass.getText().toString().trim().equals("")
				&& !et_phone.getText().toString().trim().equals("")
				&& !et_country.getText().toString().trim().equals("")
				&& !et_city.getText().toString().trim().equals("")) {
			if (isConnected()) {
				try {
					new SignUp().execute();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				Toast.makeText(getApplicationContext(),
						"No internet connection", Toast.LENGTH_LONG).show();

			}
		} else {
			Toast.makeText(getApplicationContext(),
					"Please fill all TextFields", Toast.LENGTH_LONG).show();

		}
	}

	class SignUp extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDialog.setMessage("SignUp in process...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * Creating product
		 * */

		protected String doInBackground(String... args) {
			inputname = et_name.getText().toString().trim();
			inputemail = et_email.getText().toString().trim();
			inputpassword = et_pass.getText().toString().trim();
			inputphone = et_phone.getText().toString().trim();
			inputcity = et_city.getText().toString().trim();
			inputcountry = et_country.getText().toString().trim();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("name", inputname));
			params.add(new BasicNameValuePair("email", inputemail));
			params.add(new BasicNameValuePair("pwd", inputpassword));
			params.add(new BasicNameValuePair("phone", inputphone));
			params.add(new BasicNameValuePair("city", inputcity));
			params.add(new BasicNameValuePair("country", inputcountry));

			JSONObject json = jsonParser.makeHttpRequest(url_signup, "POST",
					params);
			if (json != null) {
				try {
					// Checking for SUCCESS TAG
					status = json.getInt(TAG_STATUS);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return "" + status;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String response) {
			// dismiss the dialog once done
			pDialog.dismiss();
			if (response.equals("2")) {
				Toast.makeText(getApplicationContext(), "Email Already Exists",
						Toast.LENGTH_LONG).show();
			} else if (response.equals("-1")) {
				Toast.makeText(getApplicationContext(),
						"Server is not responding", Toast.LENGTH_LONG).show();
			} else if (response.equals("1")) {
				Toast.makeText(getApplicationContext(),
						inputname + " has been registered", Toast.LENGTH_LONG)
						.show();
				finish();
				overridePendingTransition(R.anim.slide_in_activity,
						R.anim.slide_out_activity);
			} else if (response.equals("0")) {
				Toast.makeText(getApplicationContext(), "An error occurred",
						Toast.LENGTH_LONG).show();
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

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.slide_in_activity,
				R.anim.slide_out_activity);
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
