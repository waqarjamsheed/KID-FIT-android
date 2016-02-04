package com.example.kid_fit;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.example.kid_fit.adapter.PreMixVidPlayAdapter;
import com.example.kid_fit.jsonparser.JSONParser;
import com.example.kid_fit.model.PreMixMusPlay;
import com.example.kid_fit.url.UrlClass;
import com.example.kid_fit.utility.SPVariablesClass;

@SuppressLint({ "ClickableViewAccessibility", "InflateParams" })
@SuppressWarnings("deprecation")
public class PreMixVidPlayActivity extends Activity {

	public static final String PREFS_NAME = "LoginPrefs";
	public EditText et_searchpremixvidplay;
	public LinearLayout ll_menulayout, ll_windowlayout, ll_windowsublayout;
	public Animation menuanimation1 = null, menuanimation2 = null,
			windowanimation1 = null, windowanimation2 = null;
	public ListView lv_listpremixvidplay;
	private static String url_get_playlists = UrlClass.url + "playlist.php";
	private static final String TAG_STATUS = "status";
	private static final String TAG_DATA = "data";
	public int status = -1;
	public JSONParser jsonParser = new JSONParser();
	private ProgressDialog pDialog;
	public PreMixVidPlayAdapter ListViewAdapter;
	public ArrayList<PreMixMusPlay> json_play_list = new ArrayList<PreMixMusPlay>();
	public String categoryid = "";
	public TextView et_username;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.premixvidplay_ui);

		categoryid = getIntent().getStringExtra("CAT_ID");
		pDialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);

		et_username = (TextView) findViewById(R.id.etusername);
		et_searchpremixvidplay = (EditText) findViewById(R.id.etsearchpremixvidplay);
		ll_menulayout = (LinearLayout) findViewById(R.id.menulayout);
		ll_windowlayout = (LinearLayout) findViewById(R.id.windowlayout);
		ll_windowsublayout = (LinearLayout) findViewById(R.id.windowsublayout);
		menuanimation1 = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.slide_out_menu);
		menuanimation2 = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.slide_in_menu);
		windowanimation1 = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.slide_out_window);
		windowanimation2 = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.slide_in_window);

		if (isConnected()) {
			try {
				new getDataList().execute();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Toast.makeText(getApplicationContext(), "No internet connection",
					Toast.LENGTH_LONG).show();

		}

		et_searchpremixvidplay.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					if (ll_menulayout.getVisibility() == View.VISIBLE) {
						ll_menulayout.startAnimation(menuanimation1);
						ll_menulayout.setVisibility(View.GONE);
						ll_windowsublayout.startAnimation(windowanimation2);
						ll_windowsublayout.setAlpha(1);
					}
					break;

				case MotionEvent.ACTION_UP:

					break;
				}

				// Handle ListView touch events.
				v.onTouchEvent(event);
				return true;
			}
		});

	}

	private class getDataList extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDialog.setMessage("Searching Playlists...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("cat_id", categoryid));
			JSONObject json = jsonParser.makeHttpRequest(url_get_playlists,
					"POST", params);
			if (json != null) {
				try {
					// Checking for SUCCESS TAG
					status = json.getInt(TAG_STATUS);
					if (status == 1) {
						JSONArray mJsonArray = json.getJSONArray(TAG_DATA);
						for (int i = 0; i < mJsonArray.length(); i++) {
							JSONObject mJsonObject = mJsonArray
									.getJSONObject(i);
							String playid = mJsonObject.getString("ap_id");
							String playname = mJsonObject.getString("ap_name");

							PreMixMusPlay mPreMixMusPlay = new PreMixMusPlay();
							mPreMixMusPlay.setPreMixMusPlayid(playid);
							mPreMixMusPlay.setPreMixMusPlayname(playname);
							json_play_list.add(mPreMixMusPlay);

						}
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			return "" + status;
		}

		@Override
		protected void onPostExecute(String file_url) {
			super.onPostExecute(file_url);
			if (json_play_list.size() == 0) {
				Toast.makeText(getApplicationContext(), "No record found",
						Toast.LENGTH_SHORT).show();
			}
			if (file_url.equals("1")) {
				Toast.makeText(getApplicationContext(),
						"Pre-Mixed Video Playlists", Toast.LENGTH_LONG).show();
			} else if (file_url.equals("0")) {
				Toast.makeText(getApplicationContext(), "No record found",
						Toast.LENGTH_LONG).show();
			} else if (file_url.equals("-1")) {
				Toast.makeText(getApplicationContext(),
						"Server is not responding", Toast.LENGTH_LONG).show();
			}
			pDialog.dismiss();
			populateUI();
		}

	}

	public void populateUI() {

		lv_listpremixvidplay = (ListView) findViewById(R.id.lvlistpremixvidplay);
		lv_listpremixvidplay.setFadingEdgeLength(0);

		ListViewAdapter = new PreMixVidPlayAdapter(this, json_play_list);
		lv_listpremixvidplay.setAdapter(ListViewAdapter);

		// Capture Text in EditText
		et_searchpremixvidplay.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				String text = et_searchpremixvidplay.getText().toString()
						.trim().toLowerCase(Locale.getDefault());
				ListViewAdapter.filter(text);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
			}
		});

		lv_listpremixvidplay.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				if (ll_menulayout.getVisibility() == View.VISIBLE) {
					ll_menulayout.startAnimation(menuanimation1);
					ll_menulayout.setVisibility(View.GONE);
					ll_windowsublayout.startAnimation(windowanimation2);
					ll_windowsublayout.setAlpha(1);
				} else if (ll_menulayout.getVisibility() == View.GONE) {
					PreMixMusPlay mPreMixMusPlay = new PreMixMusPlay();
					mPreMixMusPlay = json_play_list.get(position);
					// Show Alert
					Toast.makeText(getApplicationContext(),
							mPreMixMusPlay.getPreMixMusPlayname(),
							Toast.LENGTH_LONG).show();
					Intent premixvidplayintent = new Intent(
							PreMixVidPlayActivity.this, PreMixVidActivity.class);
					premixvidplayintent.putExtra("PLAY_ID",
							mPreMixMusPlay.getPreMixMusPlayid());
					startActivity(premixvidplayintent);
					overridePendingTransition(R.anim.slide_in_activity,
							R.anim.slide_out_activity);
				}
			}
		});

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		if (settings.getString("signin", "").toString().equals("signin")) {
			SPVariablesClass.id = settings.getString("id", "");
			SPVariablesClass.name = settings.getString("name", "");
			SPVariablesClass.email = settings.getString("email", "");
			SPVariablesClass.password = settings.getString("password", "");
			SPVariablesClass.phone = settings.getString("phone", "");
			SPVariablesClass.city = settings.getString("city", "");
			SPVariablesClass.country = settings.getString("country", "");
		}
		et_username.setText(SPVariablesClass.name);
	}

	public void clickuserprofileedit(View v) {
		if (ll_menulayout.getVisibility() == View.VISIBLE) {
			ll_menulayout.startAnimation(menuanimation1);
			ll_menulayout.setVisibility(View.GONE);
			ll_windowsublayout.setAlpha(1);
		}
		Toast.makeText(getApplicationContext(), "Profile", Toast.LENGTH_LONG)
				.show();
		Intent prosintent = new Intent(this, UserProfileActivity.class);
		startActivity(prosintent);
		overridePendingTransition(R.anim.slide_in_activity,
				R.anim.slide_out_activity);
	}

	public void clickdatasettings(View v) {
		if (ll_menulayout.getVisibility() == View.VISIBLE) {
			ll_menulayout.startAnimation(menuanimation1);
			ll_menulayout.setVisibility(View.GONE);
			ll_windowsublayout.setAlpha(1);
		}
		Toast.makeText(getApplicationContext(), "Data Settings", Toast.LENGTH_LONG)
		.show();
		Intent dsintent = new Intent(this, DataSettingsActivity.class);
		startActivity(dsintent);
		overridePendingTransition(R.anim.slide_in_activity,
				R.anim.slide_out_activity);
	}

	public void clickmyplaylists(View v) {
		if (ll_menulayout.getVisibility() == View.VISIBLE) {
			ll_menulayout.startAnimation(menuanimation1);
			ll_menulayout.setVisibility(View.GONE);
			ll_windowsublayout.setAlpha(1);
		}
	}

	public void clickfaqs(View v) {
		if (ll_menulayout.getVisibility() == View.VISIBLE) {
			ll_menulayout.startAnimation(menuanimation1);
			ll_menulayout.setVisibility(View.GONE);
			ll_windowsublayout.setAlpha(1);
		}
		Toast.makeText(getApplicationContext(), "Faq's", Toast.LENGTH_LONG)
				.show();
		Intent faqsintent = new Intent(this, FaqsActivity.class);
		startActivity(faqsintent);
		overridePendingTransition(R.anim.slide_in_activity,
				R.anim.slide_out_activity);
	}

	public void clickabout(View v) {
		if (ll_menulayout.getVisibility() == View.VISIBLE) {
			ll_menulayout.startAnimation(menuanimation1);
			ll_menulayout.setVisibility(View.GONE);
			ll_windowsublayout.setAlpha(1);
		}
		Toast.makeText(getApplicationContext(), "About Us", Toast.LENGTH_LONG)
				.show();
		Intent aboutusintent = new Intent(this, AboutUsActivity.class);
		startActivity(aboutusintent);
		overridePendingTransition(R.anim.slide_in_activity,
				R.anim.slide_out_activity);
	}

	public void clickmenu(View v) {
		if (ll_menulayout.getVisibility() == View.VISIBLE) {
			ll_menulayout.startAnimation(menuanimation1);
			ll_menulayout.setVisibility(View.GONE);
			ll_windowsublayout.startAnimation(windowanimation2);
			ll_windowsublayout.setAlpha(1);
		} else if (ll_menulayout.getVisibility() == View.GONE) {
			ll_windowsublayout.startAnimation(windowanimation1);
			ll_windowsublayout.setAlpha(0);
			ll_menulayout.startAnimation(menuanimation2);
			ll_menulayout.setVisibility(View.VISIBLE);
		}
	}

	public void clickscreen(View v) {
		if (ll_menulayout.getVisibility() == View.VISIBLE) {
			ll_menulayout.startAnimation(menuanimation1);
			ll_menulayout.setVisibility(View.GONE);
			ll_windowsublayout.startAnimation(windowanimation2);
			ll_windowsublayout.setAlpha(1);
		}
	}

	public void clickhome(View v) {
		if (ll_menulayout.getVisibility() == View.VISIBLE) {
			ll_menulayout.startAnimation(menuanimation1);
			ll_menulayout.setVisibility(View.GONE);
			ll_windowsublayout.startAnimation(windowanimation2);
			ll_windowsublayout.setAlpha(1);
		} else {
			Toast.makeText(getApplicationContext(), "Home", Toast.LENGTH_LONG)
					.show();
			finish();
			Intent homeintent = new Intent(PreMixVidPlayActivity.this,
					HomeActivity.class);
			homeintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(homeintent);
			overridePendingTransition(R.anim.slide_in_activity,
					R.anim.slide_out_activity);
		}
	}

	public void clickdownloads(View v) {
		if (ll_menulayout.getVisibility() == View.VISIBLE) {
			ll_menulayout.startAnimation(menuanimation1);
			ll_menulayout.setVisibility(View.GONE);
			ll_windowsublayout.startAnimation(windowanimation2);
			ll_windowsublayout.setAlpha(1);
		} else {
			Toast.makeText(getApplicationContext(), "Downloads",
					Toast.LENGTH_LONG).show();
			Intent downloadsintent = new Intent(this,
					DownloadsActivity.class);
			startActivity(downloadsintent);
			overridePendingTransition(R.anim.slide_in_activity,
					R.anim.slide_out_activity);
		}
	}

	public void clickplaylists(View v) {
		if (ll_menulayout.getVisibility() == View.VISIBLE) {
			ll_menulayout.startAnimation(menuanimation1);
			ll_menulayout.setVisibility(View.GONE);
			ll_windowsublayout.startAnimation(windowanimation2);
			ll_windowsublayout.setAlpha(1);
		} else {
			Toast.makeText(getApplicationContext(), "Playlists",
					Toast.LENGTH_LONG).show();
			Intent alltracksintent = new Intent(this,
					AllTracksActivity.class);
			startActivity(alltracksintent);
			overridePendingTransition(R.anim.slide_in_activity,
					R.anim.slide_out_activity);
		}
	}

	public void clicksettings(View v) {
		if (ll_menulayout.getVisibility() == View.VISIBLE) {
			ll_menulayout.startAnimation(menuanimation1);
			ll_menulayout.setVisibility(View.GONE);
			ll_windowsublayout.startAnimation(windowanimation2);
			ll_windowsublayout.setAlpha(1);
		} else {
			Toast.makeText(getApplicationContext(), "Settings",
					Toast.LENGTH_LONG).show();
			Intent settingintent = new Intent(this,
					SettingsActivity.class);
			startActivity(settingintent);
			overridePendingTransition(R.anim.slide_in_activity,
					R.anim.slide_out_activity);
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (ll_menulayout.getVisibility() == View.VISIBLE) {
			ll_menulayout.startAnimation(menuanimation1);
			ll_menulayout.setVisibility(View.GONE);
			ll_windowsublayout.startAnimation(windowanimation2);
			ll_windowsublayout.setAlpha(1);
		} else if (ll_menulayout.getVisibility() == View.GONE) {
			finish();
			overridePendingTransition(R.anim.slide_in_activity,
					R.anim.slide_out_activity);
		}

	}

	public boolean isConnected() {

		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isConnected())
			return true;
		else
			return false;
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

}
