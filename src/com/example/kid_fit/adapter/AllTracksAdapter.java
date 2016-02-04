package com.example.kid_fit.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kid_fit.R;
import com.example.kid_fit.model.AllTracks;

public class AllTracksAdapter extends BaseAdapter {

	// Declare Variables
	private Context mContext;
	private List<AllTracks> alltrackslist = null;
	private ArrayList<AllTracks> arraylist;
	private LayoutInflater mInflater;

	public AllTracksAdapter(Context context, List<AllTracks> alltrackslist) {
		mContext = context;
		this.alltrackslist = alltrackslist;
		this.arraylist = new ArrayList<AllTracks>();
		this.arraylist.addAll(alltrackslist);
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return alltrackslist.size();
	}

	@Override
	public AllTracks getItem(int position) {
		return alltrackslist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {
		TextView tv_musname;
		TextView tv_musartistname;
		ImageView iv_musimage, iv_musicon;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint({ "InflateParams", "ViewHolder" })
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		final AllTracks rowItem = getItem(position);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.inflatealltracks_ui,
					parent, false);
			holder = new ViewHolder();
			holder.tv_musname = (TextView) convertView
					.findViewById(R.id.tvmusname);
			holder.tv_musartistname = (TextView) convertView
					.findViewById(R.id.tvmusartistname);
			holder.iv_musimage = (ImageView) convertView
					.findViewById(R.id.ivmusimage);
			holder.iv_musicon = (ImageView) convertView
					.findViewById(R.id.ivmusicon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_musname.setText(rowItem.getAllTrackstitle());
		holder.tv_musartistname.setText("by "
				+ rowItem.getAllTracksartistname()+"("+rowItem.getAllTracksType()+")");
		if (rowItem.getAllTracksPathMatch().equals("Match")) {
			holder.iv_musimage.setImageDrawable(mContext.getResources()
					.getDrawable(R.drawable.playicon));
		} else if (rowItem.getAllTracksPathMatch().equals("Not Match")) {
			holder.iv_musimage.setImageDrawable(mContext.getResources()
					.getDrawable(R.drawable.downloads));
		}
		if (rowItem.getAllTracksType().equals("audio")) {
			holder.iv_musicon.setImageDrawable(mContext.getResources()
					.getDrawable(R.drawable.musicimgmed));
		} else if (rowItem.getAllTracksType().equals("video")) {
			holder.iv_musicon.setImageDrawable(mContext.getResources()
					.getDrawable(R.drawable.videoimgmed));
		}
		// Picasso.with(mContext).load("http://192.168.0.102:8080/ECSJsonAPI/Image/RGU.jpg").error(R.drawable.ic_launcher).into(ivimage);
		Animation animation = null;
		animation = AnimationUtils.loadAnimation(mContext, R.anim.push_left_in);
		convertView.startAnimation(animation);
		animation = null;
		return convertView;
	}

	// Filter Class
	public void filter(String charText) {
		charText = charText.toLowerCase(Locale.getDefault());
		alltrackslist.clear();
		if (charText.length() == 0) {
			alltrackslist.addAll(arraylist);
		} else {
			for (AllTracks c : arraylist) {
				if (c.getAllTrackstitle().toLowerCase(Locale.getDefault())
						.contains(charText)
						|| c.getAllTracksartistname()
								.toLowerCase(Locale.getDefault())
								.contains(charText)) {
					alltrackslist.add(c);
				}
			}
		}
		notifyDataSetChanged();
	}

}
