package com.techventus.timefly;


import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

/**
 * The <code>PagerAdapter</code> serves the fragments when paging.
 *
 * @author mwho
 */
public class PagerAdapter extends FragmentPagerAdapter
{

	private List<Fragment> fragments;

	/**
	 * @param fm
	 * @param fragments
	 */
	public PagerAdapter(FragmentManager fm, List<Fragment> fragments)
	{

		super(fm);
		Log.v("HELP", "CREATING PAGER ADAPTER "+(fm==null)+" f "+(fragments==null));
		this.fragments = fragments;
		Log.v("HELP", "CREATING PAGER ADAPTER "+(fm==null)+" f "+(fragments==null)+" ");
	}

	/* (non-Javadoc)
		* @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
		*/
	@Override
	public Fragment getItem(int position)
	{
		return this.fragments.get(position);
		//asdfasdfsdffdfs  fds aaf asf af f


	}

	/* (non-Javadoc)
		* @see android.support.v4.view.PagerAdapter#getCount()
		*/
	@Override
	public int getCount()
	{
		return this.fragments.size();
	}
}
