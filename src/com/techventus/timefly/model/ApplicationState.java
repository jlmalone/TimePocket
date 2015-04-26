package com.techventus.timefly.model;

/**
 * Created by Joseph on 26.04.15.
 */
public class ApplicationState
{
	private STATE applicationState = STATE.NOT_SET;
	private String goalName;
	private int goalId;
	private long initialStartingTime;
	private long practiseTime;//length of intended practice in millis

	public STATE getApplicationState()
	{
		return applicationState;
	}

	public void setApplicationState(STATE applicationState)
	{
		this.applicationState = applicationState;
	}

	public String getGoalName()
	{
		return goalName;
	}

	public void setGoalName(String goalName)
	{
		this.goalName = goalName;
	}

	public int getGoalId()
	{
		return goalId;
	}

	public void setGoalId(int goalId)
	{
		this.goalId = goalId;
	}

	public long getInitialStartingTime()
	{
		return initialStartingTime;
	}

	public void setInitialStartingTime(long initialStartingTime)
	{
		this.initialStartingTime = initialStartingTime;
	}

	public long getPractiseTime()
	{
		return practiseTime;
	}

	public void setPractiseTime(long practiseTime)
	{
		this.practiseTime = practiseTime;
	}
}
