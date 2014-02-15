package com.techventus.timefly;

public class Practice
{

	private String note;
	private String goalName;
	private long date;
	private int secs;
	private int practiceId;
	private int goalId;

	public Practice(String note, long date, int secs, int practice_id, int goal_id, String goal_name)
	{
		this.note = note;
		this.date = date;
		this.secs = secs;
		this.practiceId = practice_id;
		this.goalId = goal_id;
		this.goalName = goal_name;
	}

	public String getNote()
	{
		return note;
	}

	public void setNote(String note)
	{
		this.note = note;
	}

	public long getDate()
	{
		return date;
	}

	public void setDate(long date)
	{
		this.date = date;
	}

	public int getSecs()
	{
		return secs;
	}

	public void setSecs(int secs)
	{
		this.secs = secs;
	}

	public String getGoalName()
	{
		return goalName;
	}

	public void setGoalName(String goalName)
	{
		this.goalName = goalName;
	}

	public int getPracticeId()
	{
		return practiceId;
	}

	public void setPracticeId(int practiceId)
	{
		this.practiceId = practiceId;
	}

	public int getGoalId()
	{
		return goalId;
	}

	public void setGoalId(int goalId)
	{
		this.goalId = goalId;
	}

}
