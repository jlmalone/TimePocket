package com.techventus.timefly;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import com.techventus.timefly.model.ApplicationState;
import com.techventus.timefly.model.STATE;

/**
 * Created by Joseph on 26.04.15.
 */
public class ApplicationStatePersistenceManager
{
	private static final String APPLICATION_STATE_SHARED_PREFERENCES = "APPLICATION_STATE_SHARED_PREFERENCES";

	private class PREFERENCES_KEY
	{
		private static final String APPLICATION_STATE = "APPLICATION_STATE";
		private static final String GOAL_NAME = "GOAL_NAME";
		private static final String GOAL_ID = "GOAL_ID";
		private static final String INITIAL_STARTING_TIME = "INITIAL_STARTING_TIME";
		private static final String PRACTISE_TIME = "PRACTISE_TIME";
	}

	private Context mContext;

	private SharedPreferences mSharedPreferences;

	public ApplicationStatePersistenceManager(Context context)
	{
		mContext = context;
		mSharedPreferences = mContext.getSharedPreferences(APPLICATION_STATE_SHARED_PREFERENCES,0);
	}

	public ApplicationState getSavedState()
	{
		ApplicationState ret = new ApplicationState();
		ret.setApplicationState( STATE.values()[mSharedPreferences.getInt(PREFERENCES_KEY.APPLICATION_STATE,0)]);
		ret.setGoalId(mSharedPreferences.getInt(PREFERENCES_KEY.GOAL_ID, -1));
		ret.setGoalName(mSharedPreferences.getString(PREFERENCES_KEY.GOAL_NAME, null));
		ret.setPractiseTime(mSharedPreferences.getLong(PREFERENCES_KEY.PRACTISE_TIME, -1));
		ret.setInitialStartingTime(mSharedPreferences.getLong(PREFERENCES_KEY.INITIAL_STARTING_TIME, -1));
		return ret;
	}

	@SuppressLint("NewApi")
	public void setSavedState(ApplicationState applicationState)
	{
		SharedPreferences.Editor editor =mSharedPreferences.edit() ;
		editor.putInt(PREFERENCES_KEY.APPLICATION_STATE, applicationState.getApplicationState().ordinal());
		editor.putString(PREFERENCES_KEY.GOAL_NAME, applicationState.getGoalName());
		editor.putInt(PREFERENCES_KEY.GOAL_ID, applicationState.getGoalId());
		editor.putLong(PREFERENCES_KEY.PRACTISE_TIME, applicationState.getPractiseTime());
		editor.putLong(PREFERENCES_KEY.INITIAL_STARTING_TIME, applicationState.getInitialStartingTime());
		if  (Integer.valueOf(android.os.Build.VERSION.SDK_INT)>8)
		{
			editor.apply();
		}
		else
		{
			editor.commit();
		}
	}
}
