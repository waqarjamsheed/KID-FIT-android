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
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import com.example.kid_fit.adapter.PreMixMusAdapter;
import com.example.kid_fit.jsonparser.JSONParser;
import com.example.kid_fit.model.FavMusVid;
import com.example.kid_fit.model.PreMixMus;
import com.example.kid_fit.url.UrlClass;
import com.example.kid_fit.utility.SPVariablesClass;

@SuppressLint({ "ClickableViewAccessibility", "InflateParams" })
@SuppressWarnings("deprecation")
public class PreMixMusActivity extends Activity {

	public static final String PREFS_NAME = "LoginPrefs";
	public EditText et_searchpremixmus;
	public LinearLayout ll_menulayout, ll_windowlayout, ll_windowsublayout;
	public Animation menuanimation1 = null, menuanimation2 = null,
			windowanimation1 = null, windowanimation2 = null;
	public ListView lv_listpremixmus;
	private static String url_get_audio = UrlClass.url + "audio.php";
	private static final String TAG_STATUS = "status";
	private static final String TAG_DATA = "data";
	public int status = -1;
	public JSONParser jsonParser = new JSONParser();
	private ProgressDialog pDialog;
	public PreMixMusAdapter ListViewAdapter;
	public ArrayList<PreMixMus> json_mus_list = new ArrayList<PreMixMus>();
	public String playid = "";
	public int favstatus = 0;
	private static String url_get_fav = UrlClass.url + "favourites.php";
	private static final String TAG_STATUS2 = "status";
	private static final String TAG_DATA2 = "data";
	public int status2 = -1;
	public JSONParser jsonParser2 = new JSONParser();
	public ArrayList<FavMusVid> json_fav_list = new ArrayList<FavMusVid>();
	public TextView et_username;
	public File KidFitFile;
	public File KidFitFileArray[];
	public String filepath, filename;
	public ProgressBar pb;
	public Dialog dialog;
	public int downloadedSize = 0;
	public int totalSize = 0;
	public TextView cur_val;
	public Intent premixmusintent2;
	public AlertDialog alertDialog;
	public LayoutInflater dialogaskdownloadli;
	public View dialogaskdownloadView;
	public Button btn_diaclose;
	public TextView tv_diayes, tv_diano;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.premixmuslist_ui);

		playid = getIntent().getStringExtra("PLAY_ID");
		pDialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);

		et_username = (TextView) findViewById(R.id.etusername);
		et_searchpremixmus = (EditText) findViewById(R.id.etsearchpremixmus);
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

		KidFitFile = new File(Environment.getExternalStorageDirectory()
				+ "/KID-FIT/Musics");
		if (!KidFitFile.isDirectory()) {
			KidFitFile.mkdirs();
		}
		KidFitFileArray = KidFitFile.listFiles();
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

		et_searchpremixmus.setOnTouchListener(new OnTouchListener() {
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
			pDialog.setMessage("Searching Music...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("ap_id", playid));
			params.add(new BasicNameValuePair("music_type", "1"));
			JSONObject json = jsonParser.makeHttpRequest(url_get_audio, "POST",
					params);
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
							String tracksubtitle = mJsonObject
									.getString("track_subtitle");
							String trackduration = mJsonObject
									.getString("track_duration");
							String trackstatus = mJsonObject
									.getString("track_status");
							String trackcreatedate = mJsonObject
									.getString("track_create_date");
							String artistname = mJsonObject
									.getString("artist_name");
							String tracktitleidartistname = tracktitle
									+ trackid + artistname + ".mp3";

							PreMixMus mPreMixMus = new PreMixMus();
							mPreMixMus.setPreMixMusid(trackid);
							mPreMixMus.setPreMixMustitle(tracktitle);
							mPreMixMus.setPreMixMuspath(trackpath);
							mPreMixMus.setPreMixMusprice(trackprice);
							mPreMixMus.setPreMixMussubtitle(tracksubtitle);
							mPreMixMus.setPreMixMusduration(trackduration);
							mPreMixMus.setPreMixMusstatus(trackstatus);
							mPreMixMus.setPreMixMuscreatedate(trackcreatedate);
							mPreMixMus.setPreMixMusartistname(artistname);

							if (KidFitFileArray.length > 0) {
								for (int j = 0; j < KidFitFileArray.length; j++) {
									if (tracktitleidartistname
											.equals(KidFitFileArray[j]
													.getName())) {
										mPreMixMus.setPreMixPathMatch("Match");
										break;
									} else if (!tracktitleidartistname
											.equals(KidFitFileArray[j]
													.getName())) {
										mPreMixMus
												.setPreMixPathMatch("Not Match");
									}

								}
							} else if (KidFitFileArray.length <= 0) {
								mPreMixMus.setPreMixPathMatch("Not Match");
							}

							json_mus_list.add(mPreMixMus);

						}
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			List<NameValuePair> params2 = new ArrayList<NameValuePair>();
			params2.add(new BasicNameValuePair("user_id", SPVariablesClass.id));
			params2.add(new BasicNameValuePair("save", "2"));
			JSONObject json2 = jsonParser2.makeHttpRequest(url_get_fav, "POST",
					params2);
			if (json2 != null) {
				try {
					// Checking for SUCCESS TAG
					status2 = json2.getInt(TAG_STATUS2);
					if (status2 == 1) {
						JSONArray mJsonArray2 = json2.getJSONArray(TAG_DATA2);
						for (int i = 0; i < mJsonArray2.length(); i++) {
							JSONObject mJsonObject2 = mJsonArray2
									.getJSONObject(i);
							String trackid2 = mJsonObject2
									.getString("track_id");

							FavMusVid mFavMusVid = new FavMusVid();
							mFavMusVid.setFavMusVidid(trackid2);
							json_fav_list.add(mFavMusVid);

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
			if (json_mus_list.size() == 0) {
				Toast.makeText(getApplicationContext(), "No record found",
						Toast.LENGTH_SHORT).show();
			}
			if (file_url.equals("1")) {
				Toast.makeText(getApplicationContext(), "Pre-Mixed Music",
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

		lv_listpremixmus = (ListView) findViewById(R.id.lvlistpremixmus);
		lv_listpremixmus.setFadingEdgeLength(0);

		ListViewAdapter = new PreMixMusAdapter(this, json_mus_list);
		lv_listpremixmus.setAdapter(ListViewAdapter);

		// Capture Text in EditText
		et_searchpremixmus.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				String text = et_searchpremixmus.getText().toString().trim()
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

		lv_listpremixmus.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

				if (ll_menulayout.getVisibility() == View.VISIBLE) {
					ll_menulayout.startAnimation(menuanimation1);
					ll_menulayout.setVisibility(View.GONE);
					ll_windowsublayout.startAnimation(windowanimation2);
					ll_windowsublayout.setAlpha(1);
				} else if (ll_menulayout.getVisibility() == View.GONE) {
					PreMixMus mPreMixMus = new PreMixMus();
					mPreMixMus = json_mus_list.get(position);

					for (int f = 0; f < json_fav_list.size(); f++) {
						FavMusVid mFavMusVid = new FavMusVid();
						mFavMusVid = json_fav_list.get(f);
						if (mPreMixMus.getPreMixMusid().equals(
								mFavMusVid.getFavMusVidid())) {
							favstatus = 1;
							break;
						} else if (!mPreMixMus.getPreMixMusid().equals(
								mFavMusVid.getFavMusVidid())) {
							favstatus = 0;
						}
					}

					// Show Alert
					Toast.makeText(getApplicationContext(),
							mPreMixMus.getPreMixMustitle(), Toast.LENGTH_LONG)
							.show();
					filename = mPreMixMus.getPreMixMustitle()
							+ mPreMixMus.getPreMixMusid()
							+ mPreMixMus.getPreMixMusartistname() + ".mp3";
					filepath = mPreMixMus.getPreMixMuspath();
					if (mPreMixMus.getPreMixPathMatch().equals("Match")) {
						Intent premixmusintent = new Intent(
								PreMixMusActivity.this,
								PreMixMusNowPlayActivity.class);
						premixmusintent.putExtra("FAV_STATUS", favstatus);
						premixmusintent.putExtra("TRACK_PATH",
								mPreMixMus.getPreMixMuspath());
						premixmusintent.putExtra("TRACK_ID",
								mPreMixMus.getPreMixMusid());
						premixmusintent.putExtra("FILE_NAME",
								KidFitFile.getPath() + "/" + filename);
						finish();
						startActivity(premixmusintent);
						overridePendingTransition(R.anim.slide_in_activity,
								R.anim.slide_out_activity);
					} else if (mPreMixMus.getPreMixPathMatch().equals(
							"Not Match")) {
						premixmusintent2 = new Intent(PreMixMusActivity.this,
								PreMixMusNowPlayActivity.class);
						premixmusintent2.putExtra("FAV_STATUS", favstatus);
						premixmusintent2.putExtra("TRACK_PATH",
								mPreMixMus.getPreMixMuspath());
						premixmusintent2.putExtra("TRACK_ID",
								mPreMixMus.getPreMixMusid());
						dialogaskdownloadli = LayoutInflater
								.from(PreMixMusActivity.this);
						dialogaskdownloadView = dialogaskdownloadli.inflate(
								R.layout.dialogaskdownload_ui, null);

						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
								PreMixMusActivity.this);

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
			// create a new file, to save the downloaded file
			File file = new File(KidFitFile, filename1);

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
					premixmusintent2.putExtra("FILE_NAME", KidFitFile.getPath()
							+ "/" + filename);
					finish();
					startActivity(premixmusintent2);
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
			Intent homeintent = new Intent(PreMixMusActivity.this,
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
