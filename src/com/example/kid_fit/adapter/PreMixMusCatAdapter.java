package com.example.kid_fit.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.example.kid_fit.R;
import com.example.kid_fit.model.PreMixMusCat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PreMixMusCatAdapter extends BaseAdapter {

	// Declare Variables
	private Context mContext;
	private List<PreMixMusCat> premixmuscatlist = null;
	private ArrayList<PreMixMusCat> arraylist;
	private LayoutInflater mInflater;

	public PreMixMusCatAdapter(Context context,
			List<PreMixMusCat> premixmuscatlist) {
		mContext = context;
		this.premixmuscatlist = premixmuscatlist;
		this.arraylist = new ArrayList<PreMixMusCat>();
		this.arraylist.addAll(premixmuscatlist);
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return premixmuscatlist.size();
	}

	@Override
	public PreMixMusCat getItem(int position) {
		return premixmuscatlist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {
		TextView tv_muscat;
	}

	@SuppressLint({ "InflateParams", "ViewHolder" })
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		final PreMixMusCat rowItem = getItem(position);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.inflatepremixmuscatlist_ui, parent,
					false);
			holder = new ViewHolder();
			holder.tv_muscat = (TextView) convertView
					.findViewById(R.id.tvmuscat);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_muscat.setText(rowItem.getPreMixMusCatname());
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
		premixmuscatlist.clear();
		if (charText.length() == 0) {
			premixmuscatlist.addAll(arraylist);
		} else {
			for (PreMixMusCat c : arraylist) {
				if (c.getPreMixMusCatname().toLowerCase(Locale.getDefault())
						.contains(charText)) {
					premixmuscatlist.add(c);
				}
			}
		}
		notifyDataSetChanged();
	}

}
