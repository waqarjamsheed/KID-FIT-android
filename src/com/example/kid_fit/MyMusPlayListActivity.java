package com.example.kid_fit;

import com.example.kid_fit.utility.SPVariablesClass;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint("ClickableViewAccessibility")
public class MyMusPlayListActivity extends Activity {

	public static final String PREFS_NAME = "LoginPrefs";
	public EditText et_searchmymusplay;
	public LinearLayout ll_menulayout, ll_windowlayout, ll_windowsublayout;
	public Animation menuanimation1 = null, menuanimation2 = null,
			windowanimation1 = null, windowanimation2 = null;
	public ListView lv_listmymusplay;
	public String[] values = new String[] { "Playlist 01", "Playlist 02",
			"Playlist 03", "Playlist 04", "Playlist 05", "Playlist 06",
			"Playlist 07", "Playlist 08", "Playlist 09", "Playlist 10",
			"Playlist 11", "Playlist 12", "Playlist 13", "Playlist 14",
			"Playlist 15", "Playlist 16", "Playlist 17", "Playlist 18",
			"Playlist 19", "Playlist 20" };
	public TextView et_username;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mymusplaylist_ui);

		et_username = (TextView) findViewById(R.id.etusername);
		et_searchmymusplay = (EditText) findViewById(R.id.etsearchmymusplay);
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
		lv_listmymusplay = (ListView) findViewById(R.id.lvlistmymusplay);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, values);

		// Assign adapter to ListView
		lv_listmymusplay.setAdapter(adapter);

		// ListView Item Click Listener
		lv_listmymusplay.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (ll_menulayout.getVisibility() == View.VISIBLE) {
					ll_menulayout.startAnimation(menuanimation1);
					ll_menulayout.setVisibility(View.GONE);
					ll_windowsublayout.startAnimation(windowanimation2);
					ll_windowsublayout.setAlpha(1);
				} else if (ll_menulayout.getVisibility() == View.GONE) {
					// ListView Clicked item index
					int itemPosition = position;

					// ListView Clicked item value
					String itemValue = (String) lv_listmymusplay
							.getItemAtPosition(position);

					// Show Alert
					Toast.makeText(
							getApplicationContext(),
							"Position :" + itemPosition + "  ListItem : "
									+ itemValue, Toast.LENGTH_LONG).show();
				}
			}

		});
		et_searchmymusplay.setOnTouchListener(new OnTouchListener() {
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
			Intent homeintent = new Intent(MyMusPlayListActivity.this,
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
