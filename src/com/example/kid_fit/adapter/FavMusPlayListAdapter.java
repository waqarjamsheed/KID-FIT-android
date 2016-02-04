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
import com.example.kid_fit.model.FavMusVid;

public class FavMusPlayListAdapter extends BaseAdapter {

	// Declare Variables
	private Context mContext;
	private List<FavMusVid> favmuslist = null;
	private ArrayList<FavMusVid> arraylist;
	private LayoutInflater mInflater;

	public FavMusPlayListAdapter(Context context, List<FavMusVid> favmuslist) {
		mContext = context;
		this.favmuslist = favmuslist;
		this.arraylist = new ArrayList<FavMusVid>();
		this.arraylist.addAll(favmuslist);
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return favmuslist.size();
	}

	@Override
	public FavMusVid getItem(int position) {
		return favmuslist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {
		TextView tv_musname;
		TextView tv_musartistname;
		ImageView iv_musimage;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint({ "InflateParams", "ViewHolder" })
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		final FavMusVid rowItem = getItem(position);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.inflatepremixmuslist_ui,
					parent, false);
			holder = new ViewHolder();
			holder.tv_musname = (TextView) convertView
					.findViewById(R.id.tvmusname);
			holder.tv_musartistname = (TextView) convertView
					.findViewById(R.id.tvmusartistname);
			holder.iv_musimage = (ImageView) convertView
					.findViewById(R.id.ivmusimage);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_musname.setText(rowItem.getFavMusVidtitle());
		holder.tv_musartistname.setText("by "
				+ rowItem.getFavMusVidartistname());
		if (rowItem.getFavMusVidPathMatch().equals("Match")) {
			holder.iv_musimage.setImageDrawable(mContext.getResources()
					.getDrawable(R.drawable.playicon));
		} else if (rowItem.getFavMusVidPathMatch().equals("Not Match")) {
			holder.iv_musimage.setImageDrawable(mContext.getResources()
					.getDrawable(R.drawable.downloads));
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
		favmuslist.clear();
		if (charText.length() == 0) {
			favmuslist.addAll(arraylist);
		} else {
			for (FavMusVid c : arraylist) {
				if (c.getFavMusVidtitle().toLowerCase(Locale.getDefault())
						.contains(charText)
						|| c.getFavMusVidartistname()
								.toLowerCase(Locale.getDefault())
								.contains(charText)) {
					favmuslist.add(c);
				}
			}
		}
		notifyDataSetChanged();
	}

}
