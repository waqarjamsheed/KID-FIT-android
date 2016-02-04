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
import com.example.kid_fit.model.PreMixMus;

public class PreMixVidAdapter extends BaseAdapter {

	// Declare Variables
	private Context mContext;
	private List<PreMixMus> premixmuslist = null;
	private ArrayList<PreMixMus> arraylist;
	private LayoutInflater mInflater;

	public PreMixVidAdapter(Context context, List<PreMixMus> premixmuslist) {
		mContext = context;
		this.premixmuslist = premixmuslist;
		this.arraylist = new ArrayList<PreMixMus>();
		this.arraylist.addAll(premixmuslist);
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return premixmuslist.size();
	}

	@Override
	public PreMixMus getItem(int position) {
		return premixmuslist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {
		TextView tv_vidname;
		TextView tv_vidartistname;
		ImageView iv_vidimage;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint({ "InflateParams", "ViewHolder" })
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		final PreMixMus rowItem = getItem(position);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.inflatepremixvidlist_ui,
					parent, false);
			holder = new ViewHolder();
			holder.tv_vidname = (TextView) convertView
					.findViewById(R.id.tvvidname);
			holder.tv_vidartistname = (TextView) convertView
					.findViewById(R.id.tvvidartistname);
			holder.iv_vidimage = (ImageView) convertView
					.findViewById(R.id.ivvidimage);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_vidname.setText(rowItem.getPreMixMustitle());
		holder.tv_vidartistname.setText("by "+rowItem.getPreMixMusartistname());
		if(rowItem.getPreMixPathMatch().equals("Match"))
		{
			holder.iv_vidimage.setImageDrawable(mContext.getResources().getDrawable(
					R.drawable.playicon));
		} else if(rowItem.getPreMixPathMatch().equals("Not Match"))
		{
			holder.iv_vidimage.setImageDrawable(mContext.getResources().getDrawable(
					R.drawable.downloads));
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
		premixmuslist.clear();
		if (charText.length() == 0) {
			premixmuslist.addAll(arraylist);
		} else {
			for (PreMixMus c : arraylist) {
				if (c.getPreMixMustitle().toLowerCase(Locale.getDefault())
						.contains(charText)
						|| c.getPreMixMusartistname()
								.toLowerCase(Locale.getDefault())
								.contains(charText)) {
					premixmuslist.add(c);
				}
			}
		}
		notifyDataSetChanged();
	}

}
