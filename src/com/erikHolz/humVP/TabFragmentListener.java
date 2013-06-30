package com.erikHolz.humVP;

import android.app.Activity;
import android.support.v4.app.FragmentTransaction;
  
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
  
public class TabFragmentListener<T extends SherlockFragment> implements ActionBar.TabListener {
	private SherlockFragment mFragment;
	private final SherlockFragmentActivity mActivity;
	private final String mTag;
	private final Class<T> mClass;

	public TabFragmentListener(Activity activity, String tag, Class<T> clz) {
		mActivity = (SherlockFragmentActivity) activity;
		mTag = tag;
		mClass = clz;
	}

	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		SherlockFragment  preInitializedFragment = (SherlockFragment) mActivity.getSupportFragmentManager().findFragmentByTag(mTag);
		
		if (mFragment != null) {
			ft.attach(mFragment);
		} else if (preInitializedFragment != null) {
			mFragment = preInitializedFragment;
			ft.attach(mFragment);
		} else {
			mFragment = (SherlockFragment) SherlockFragment.instantiate(mActivity, mClass.getName());
			ft.add(R.id.fragment_container, mFragment, mTag);
		}
	}

	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		if (mFragment != null) {
			ft.detach(mFragment);
		}
	}

	public void onTabReselected(Tab tab, FragmentTransaction ft) {

	}
}