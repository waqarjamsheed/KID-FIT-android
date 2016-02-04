package com.example.kid_fit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.kid_fit.adapter.AllTracksAdapter;
import com.example.kid_fit.jsonparser.JSONParser;
import com.example.kid_fit.model.AllTracks;
import com.example.kid_fit.url.UrlClass;
import com.example.kid_fit.utility.SPVariablesClass;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint({ "ClickableViewAccessibility", "InflateParams" })
@SuppressWarnings("deprecation")
public class AllTracksActivity extends Activity {

	public static final String PREFS_NAME = "LoginPrefs";
	public EditText et_searchalltracks;
	public LinearLayout ll_menulayout, ll_windowlayout, ll_windowsublayout;
	public Animation menuanimation1 = null, menuanimation2 = null,
			windowanimation1 = null, windowanimation2 = null;
	public ListView lv_listalltracks;
	private static String url_get_all_track = UrlClass.url + "tracks_all.php";
	private static final String TAG_STATUS = "status";
	private static final String TAG_DATA = "data";
	public int status = -1;
	public JSONParser jsonParser = new JSONParser();
	private ProgressDialog pDialog;
	public AllTracksAdapter ListViewAdapter;
	public ArrayList<AllTracks> json_all_list = new ArrayList<AllTracks>();
	public TextView et_username;
	public File KidFitMusFile;
	public File KidFitMusFileArray[];
	public File KidFitVidFile;
	public File KidFitVidFileArray[];
	public String filepath, filename;
	public ProgressBar pb;
	public Dialog dialog;
	public int downloadedSize = 0;
	public int totalSize = 0;
	public TextView cur_val;
	public Intent alltracksintent2;
	public AlertDialog alertDialog;
	public LayoutInflater dialogaskdownloadli;
	public View dialogaskdownloadView;
	public Button btn_diaclose;
	public TextView tv_diayes, tv_diano;
	public String tracktitleidartistname;
	public String musvidtype;
	public File file;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alltracks_ui);

		pDialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);

		et_username = (TextView) findViewById(R.id.etusername);
		et_searchalltracks = (EditText) findViewById(R.id.etsearchalltracks);
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

		KidFitMusFile = new File(Environment.getExternalStorageDirectory()
				+ "/KID-FIT/Musics");
		if (!KidFitMusFile.isDirectory()) {
			KidFitMusFile.mkdirs();
		}
		KidFitMusFileArray = KidFitMusFile.listFiles();
		KidFitVidFile = new File(Environment.getExternalStorageDirectory()
				+ "/KID-FIT/Videos");
		if (!KidFitVidFile.isDirectory()) {
			KidFitVidFile.mkdirs();
		}
		KidFitVidFileArray = KidFitVidFile.listFiles();
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

		et_searchalltracks.setOnTouchListener(new OnTouchListener() {
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
			pDialog.setMessage("Searching All Playlists...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			JSONObject json = jsonParser.makeHttpRequest(url_get_all_track,
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
							String trackid = mJsonObject.getString("track_id");
							String tracktitle = mJsonObject
									.getString("track_title");
							String trackpath = mJsonObject
									.getString("track_path");
							String trackprice = mJsonObject
									.getString("track_price");
							String trackduration = mJsonObject
									.getString("track_duration");
							String artistname = mJsonObject
									.getString("artist_name");
							String tracktype = mJsonObject.getString("type");

							AllTracks mAllTracks = new AllTracks();
							mAllTracks.setAllTracksid(trackid);
							mAllTracks.setAllTrackstitle(tracktitle);
							mAllTracks.setAllTrackspath(trackpath);
							mAllTracks.setAllTracksprice(trackprice);
							mAllTracks.setAllTracksduration(trackduration);
							mAllTracks.setAllTracksartistname(artistname);
							mAllTracks.setAllTracksType(tracktype);

							if (mAllTracks.getAllTracksType().equals("audio")) {
								tracktitleidartistname = tracktitle + trackid
										+ artistname + ".mp3";
								if (KidFitMusFileArray.length > 0) {
									for (int j = 0; j < KidFitMusFileArray.length; j++) {
										if (tracktitleidartistname
												.equals(KidFitMusFileArray[j]
														.getName())) {
											mAllTracks
													.setAllTracksPathMatch("Match");
											break;
										} else if (!tracktitleidartistname
												.equals(KidFitMusFileArray[j]
														.getName())) {
											mAllTracks
													.setAllTracksPathMatch("Not Match");
										}

									}
								} else if (KidFitMusFileArray.length <= 0) {
									mAllTracks
											.setAllTracksPathMatch("Not Match");
								}
							} else if (mAllTracks.getAllTracksType().equals(
									"video")) {
								tracktitleidartistname = tracktitle + trackid
										+ artistname + ".mp4";
								if (KidFitVidFileArray.length > 0) {
									for (int j = 0; j < KidFitVidFileArray.length; j++) {
										if (tracktitleidartistname
												.equals(KidFitVidFileArray[j]
														.getName())) {
											mAllTracks
													.setAllTracksPathMatch("Match");
											break;
										} else if (!tracktitleidartistname
												.equals(KidFitVidFileArray[j]
														.getName())) {
											mAllTracks
													.setAllTracksPathMatch("Not Match");
										}

									}
								} else if (KidFitVidFileArray.length <= 0) {
									mAllTracks
											.setAllTracksPathMatch("Not Match");
								}
							}

							json_all_list.add(mAllTracks);

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
			if (json_all_list.size() == 0) {
				Toast.makeText(getApplicationContext(), "No record found",
						Toast.LENGTH_SHORT).show();
			}
			if (file_url.equals("1")) {
				Toast.makeText(getApplicationContext(), "All Playlists",
						Toast.LENGTH_LONG).show();
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

		lv_listalltracks = (ListView) findViewById(R.id.lvlistalltracks);
		lv_listalltracks.setFadingEdgeLength(0);
		
		ListViewAdapter = new AllTracksAdapter(this, json_all_list);
		lv_listalltracks.setAdapter(ListViewAdapter);

		// Capture Text in EditText
		et_searchalltracks.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				String text = et_searchalltracks.getText().toString().trim()
						.toLowerCase(Locale.getDefault());
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

		lv_listalltracks.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

				if (ll_menulayout.getVisibility() == View.VISIBLE) {
					ll_menulayout.startAnimation(menuanimation1);
					ll_menulayout.setVisibility(View.GONE);
					ll_windowsublayout.startAnimation(windowanimation2);
					ll_windowsublayout.setAlpha(1);
				} else if (ll_menulayout.getVisibility() == View.GONE) {
					AllTracks mAllTracks = new AllTracks();
					mAllTracks = json_all_list.get(position);

					// Show Alert
					Toast.makeText(getApplicationContext(),
							mAllTracks.getAllTrackstitle(), Toast.LENGTH_LONG)
							.show();
					if (mAllTracks.getAllTracksType().equals("audio")) {
						filename = mAllTracks.getAllTrackstitle()
								+ mAllTracks.getAllTracksid()
								+ mAllTracks.getAllTracksartistname() + ".mp3";
						filepath = mAllTracks.getAllTrackspath();
						musvidtype = "audio";
					} else if (mAllTracks.getAllTracksType().equals("video")) {
						filename = mAllTracks.getAllTrackstitle()
								+ mAllTracks.getAllTracksid()
								+ mAllTracks.getAllTracksartistname() + ".mp4";
						filepath = mAllTracks.getAllTrackspath();
						musvidtype = "video";
					}
					if (mAllTracks.getAllTracksPathMatch().equals("Match")) {
						Intent alltracksintent = new Intent(
								AllTracksActivity.this,
								AllTracksNowPlayActivity.class);
						if (musvidtype.equals("audio")) {
							alltracksintent.putExtra("FILE_NAME",
									KidFitMusFile.getPath() + "/" + filename);
							alltracksintent.putExtra("FILE_TYPE", musvidtype);
						} else if (musvidtype.equals("video")) {
							alltracksintent.putExtra("FILE_NAME",
									KidFitVidFile.getPath() + "/" + filename);
							alltracksintent.putExtra("FILE_TYPE", musvidtype);
						}
						finish();
						startActivity(alltracksintent);
						overridePendingTransition(R.anim.slide_in_activity,
								R.anim.slide_out_activity);
					} else if (mAllTracks.getAllTracksPathMatch().equals(
							"Not Match")) {
						alltracksintent2 = new Intent(AllTracksActivity.this,
								AllTracksNowPlayActivity.class);
						dialogaskdownloadli = LayoutInflater
								.from(AllTracksActivity.this);
						dialogaskdownloadView = dialogaskdownloadli.inflate(
								R.layout.dialogaskdownload_ui, null);

						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
								AllTracksActivity.this);

						// set xml to alertdialog builder
						alertDialogBuilder.setView(dialogaskdownloadView);
						btn_diaclose = (Button) dialogaskdownloadView
								.findViewById(R.id.btndiaclose);
						tv_diayes = (TextView) dialogaskdownloadView
								.findViewById(R.id.tvdiaok);
						tv_diano = (TextView) dialogaskdownloadView
								.findViewById(R.id.tvdialogin);

						// create alert dialog
						alertDialog = alertDialogBuilder.create();

						alertDialog
								.requestWindowFeature(Window.FEATURE_NO_TITLE);
						WindowManager.LayoutParams wmlp = alertDialog
								.getWindow().getAttributes();

						wmlp.gravity = Gravity.TOP | Gravity.CENTER;
						wmlp.x = 0; // x position
						wmlp.y = 360; // y position

						// show it
						alertDialog.show();
						alertDialog.setCanceledOnTouchOutside(true);
					}
				}

			}
		});

	}

	public void clickdiaclose(View v) {
		alertDialog.cancel();
	}

	public void clickdiayes(View v) {
		alertDialog.cancel();
		showProgress(filepath);

		new Thread(new Runnable() {
			public void run() {
				downloadFile(filename);
			}
		}).start();

	}

	public void clickdiano(View v) {
		alertDialog.cancel();

	}

	public void downloadFile(String filename1) {

		try {
			URL url = new URL(filepath);
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(true);

			// connect
			urlConnection.connect();
			if (musvidtype.equals("audio")) {
				// create a new file, to save the downloaded file
				file = new File(KidFitMusFile, filename1);
			} else if (musvidtype.equals("video")) {
				// create a new file, to save the downloaded file
				file = new File(KidFitVidFile, filename1);
			}

			FileOutputStream fileOutput = new FileOutputStream(file);

			// Stream used for reading the data from the internet
			InputStream inputStream = urlConnection.getInputStream();

			// this is the total size of the file which we are downloading
			totalSize = urlConnection.getContentLength();

			runOnUiThread(new Runnable() {
				public void run() {
					pb.setMax(totalSize);
				}
			});

			// create a buffer...
			byte[] buffer = new byte[1024];
			int bufferLength = 0;

			while ((bufferLength = inputStream.read(buffer)) > 0) {
				fileOutput.write(buffer, 0, bufferLength);
				downloadedSize += bufferLength;
				// update the progressbar //
				runOnUiThread(new Runnable() {
					public void run() {
						pb.setProgress(downloadedSize);
						float per = ((float) downloadedSize / totalSize) * 100;
						cur_val.setText("Downloaded " + downloadedSize
								+ "KB / " + totalSize + "KB (" + (int) per
								+ "%)");
					}
				});
			}
			// close the output stream when complete //
			fileOutput.close();
			runOnUiThread(new Runnable() {
				public void run() {
					dialog.cancel();
					if (musvidtype.equals("audio")) {
						alltracksintent2.putExtra("FILE_NAME",
								KidFitMusFile.getPath() + "/" + filename);
						alltracksintent2.putExtra("FILE_TYPE", musvidtype);
					} else if (musvidtype.equals("video")) {
						alltracksintent2.putExtra("FILE_NAME",
								KidFitVidFile.getPath() + "/" + filename);
						alltracksintent2.putExtra("FILE_TYPE", musvidtype);
					}
					finish();
					startActivity(alltracksintent2);
					overridePendingTransition(R.anim.slide_in_activity,
							R.anim.slide_out_activity);
				}
			});

		} catch (final MalformedURLException e) {
			showError("Error : MalformedURLException " + e);
			e.printStackTrace();
		} catch (final IOException e) {
			showError("Error : IOException " + e);
			e.printStackTrace();
		} catch (final Exception e) {
			showError("Error : Please check your internet connection " + e);
		}
	}

	void showError(final String err) {
		runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG)
						.show();
			}
		});
	}

	void showProgress(String file_path) {
		dialog = new Dialog(this);
		dialog.setContentView(R.layout.myprogressdialog);
		dialog.setTitle("Download Progress");

		TextView text = (TextView) dialog.findViewById(R.id.tv1);
		text.setText("Downloading file from " + file_path);
		cur_val = (TextView) dialog.findViewById(R.id.cur_pg_tv);
		cur_val.setText("Starting download...");
		dialog.show();
		dialog.setCancelable(false);

		pb = (ProgressBar) dialog.findViewById(R.id.progress_bar);
		pb.setProgress(0);
		pb.setProgressDrawable(getResources().getDrawable(
				R.drawable.green_progress));
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
			Intent homeintent = new Intent(AllTracksActivity.this,
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
