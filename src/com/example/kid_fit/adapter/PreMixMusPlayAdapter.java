package com.example.kid_fit.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.example.kid_fit.R;
import com.example.kid_fit.model.PreMixMusPlay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PreMixMusPlayAdapter extends BaseAdapter {

	// Declare Variables
	private Context mContext;
	private List<PreMixMusPlay> premixmusplaylist = null;
	private ArrayList<PreMixMusPlay> arraylist;
	private LayoutInflater mInflater;

	public PreMixMusPlayAdapter(Context context,
			List<PreMixMusPlay> premixmusplaylist) {
		mContext = context;
		this.premixmusplaylist = premixmusplaylist;
		this.arraylist = new ArrayList<PreMixMusPlay>();
		this.arraylist.addAll(premixmusplaylist);
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return premixmusplaylist.size();
	}

	@Override
	public PreMixMusPlay getItem(int position) {
		return premixmusplaylist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {
		TextView tv_musplay;
	}

	@SuppressLint({ "InflateParams", "ViewHolder" })
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		final PreMixMusPlay rowItem = getItem(position);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.inflatepremixmusplaylist_ui, parent,
					false);
			holder = new ViewHolder();
			holder.tv_musplay = (TextView) convertView
					.findViewById(R.id.tvmusplay);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_musplay.setText(rowItem.getPreMixMusPlayname());
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
		premixmusplaylist.clear();
		if (charText.length() == 0) {
			premixmusplaylist.addAll(arraylist);
		} else {
			for (PreMixMusPlay c : arraylist) {
				if (c.getPreMixMusPlayname().toLowerCase(Locale.getDefault())
						.contains(charText)) {
					premixmusplaylist.add(c);
				}
			}
		}
		notifyDataSetChanged();
	}

}
