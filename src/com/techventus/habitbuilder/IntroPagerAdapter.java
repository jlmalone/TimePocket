package com.techventus.habitbuilder;

import android.app.Activity;
import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
//import android.widget.TextView;

public class IntroPagerAdapter extends PagerAdapter {
    int size;
    Activity act;
    View layout;
//    TextView pagenumber;
    ImageView image;
    Button click;

    public IntroPagerAdapter(Activity activity, int noofsize) {
        // TODO Auto-generated constructor stub
        size = 5;
        act = activity;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return size;
    }

    @Override
    public Object instantiateItem(View container, int position) {
        // TODO Auto-generated method stub
        LayoutInflater inflater = (LayoutInflater) act
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = inflater.inflate(R.layout.pages, null);
//        pagenumber = (TextView) layout.findViewById(R.id.pagenumber);
        image = (ImageView) layout.findViewById(R.id.image);
//        int pagenumberTxt=position + 1;
        if(position ==0)
        	image.setImageDrawable(act.getResources().getDrawable(R.drawable.img_2947));
        else if(position==1) 
        	image.setImageDrawable(act.getResources().getDrawable(R.drawable.img_2951));
        else if(position==2)
        	image.setImageDrawable(act.getResources().getDrawable(R.drawable.img_2955));
        else if(position==3)
        	image.setImageDrawable(act.getResources().getDrawable(R.drawable.img_2948));
        else if(position==4)
        	image.setImageDrawable(act.getResources().getDrawable(R.drawable.img_2953));
//        pagenumber.setText("Now your in Page No  " +pagenumberTxt );
        ((ViewPager) container).addView(layout, 0);
        return layout;
    }

    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView((View) arg2);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == ((View) arg1);
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    // }

}
