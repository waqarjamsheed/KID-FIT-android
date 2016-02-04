package com.example.kid_fit;

import com.example.kid_fit.utility.SPVariablesClass;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

@SuppressLint({ "ClickableViewAccessibility", "InflateParams" })
@SuppressWarnings("deprecation")
public class AllTracksNowPlayActivity extends Activity {

	public static final String PREFS_NAME = "LoginPrefs";
	public LinearLayout ll_menulayout, ll_windowlayout, ll_windowsublayout;
	public Animation menuanimation1 = null, menuanimation2 = null,
			windowanimation1 = null, windowanimation2 = null;
	public String filetype = "", filename = "";
	public ProgressDialog pDialog;
	public VideoView vv_allnowplay;
	public boolean videoplay = true;
	public TextView tv_allnowplayall, et_username;
	public ImageView iv_allnowplayall;
	public MediaController mediacontroller;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alltracksnowplay_ui);

		filetype = getIntent().getStringExtra("FILE_TYPE");
		filename = getIntent().getStringExtra("FILE_NAME");

		et_username = (TextView) findViewById(R.id.etusername);
		tv_allnowplayall = (TextView) findViewById(R.id.tvallnowplayall);
		iv_allnowplayall = (ImageView) findViewById(R.id.ivallnowplayall);
		vv_allnowplay = (VideoView) findViewById(R.id.vvallnowplay);
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

		if (filetype.equals("audio")) {
			iv_allnowplayall.setImageDrawable(getResources().getDrawable(
					R.drawable.musicimgall));
			tv_allnowplayall.setText("Music");
			vv_allnowplay.setBackground(getResources().getDrawable(
					R.drawable.musicbg));
		} else if (filetype.equals("video")) {
			iv_allnowplayall.setImageDrawable(getResources().getDrawable(
					R.drawable.videoimgall));
			tv_allnowplayall.setText("Video");
		}

		vv_allnowplay.setOnTouchListener(new OnTouchListener() {
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
						vv_allnowplay.start();
					} else {
						mediacontroller.show();
						if (videoplay) {
							vv_allnowplay.pause();
							videoplay = false;
						} else if (videoplay == false) {
							vv_allnowplay.start();
							videoplay = true;
						}
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

		pDialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);
		pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pDialog.setTitle("Now Playing Music");
		pDialog.setMessage("Buffering...");
		pDialog.setIcon(R.drawable.playlisticonblue);
		pDialog.setIndeterminate(false);
		pDialog.show();

		try {
			// Start the MediaController
			mediacontroller = new MediaController(
					AllTracksNowPlayActivity.this);
			mediacontroller.setAnchorView(vv_allnowplay);
			// Get the URL from String VideoURL
			Uri video = Uri.parse(filename);
			vv_allnowplay.setMediaController(mediacontroller);
			vv_allnowplay.setVideoURI(video);

		} catch (Exception e) {
			Log.e("Error", e.getMessage());
			e.printStackTrace();
		}

		vv_allnowplay.requestFocus();
		vv_allnowplay.setOnPreparedListener(new OnPreparedListener() {
			// Close the progress bar and play the video
			public void onPrepared(MediaPlayer mp) {
				pDialog.dismiss();
				vv_allnowplay.start();
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
		Toast.makeText(getApplicationContext(), "Data Settings",
				Toast.LENGTH_LONG).show();
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
			vv_allnowplay.start();
		} else if (ll_menulayout.getVisibility() == View.GONE) {
			vv_allnowplay.pause();
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
			vv_allnowplay.start();
		}
	}

	public void clickhome(View v) {
		if (ll_menulayout.getVisibility() == View.VISIBLE) {
			ll_menulayout.startAnimation(menuanimation1);
			ll_menulayout.setVisibility(View.GONE);
			ll_windowsublayout.startAnimation(windowanimation2);
			ll_windowsublayout.setAlpha(1);
			vv_allnowplay.start();
		} else {
			Toast.makeText(getApplicationContext(), "Home", Toast.LENGTH_LONG)
					.show();
			finish();
			Intent homeintent = new Intent(AllTracksNowPlayActivity.this,
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
			vv_allnowplay.start();
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
			vv_allnowplay.start();
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
			vv_allnowplay.start();
		} else {
			Toast.makeText(getApplicationContext(), "Settings",
					Toast.LENGTH_LONG).show();
			Intent settingintent = new Intent(this, SettingsActivity.class);
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
			vv_allnowplay.start();
		} else if (ll_menulayout.getVisibility() == View.GONE) {
			finish();
			overridePendingTransition(R.anim.slide_in_activity,
					R.anim.slide_out_activity);
		}

	}

}
