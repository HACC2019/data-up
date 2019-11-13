package us.hacc.ev.application;

public class TimeDiff
{
	private int day, hour, min, sec;
	
	public TimeDiff(long diff)
	{
		sec = (int) ((diff / 1000) % 60);
		min = (int) ((diff / (1000 * 60)) % 60);
		hour = (int) ((diff / (1000 * 60 * 60)) % 24);
		day = (int) (diff / (1000 * 60 * 60 * 24));
	}

	public int getDay() {
		return (int) day;
	}

	public int getHour() {
		return (int) hour;
	}

	public int getMin() {
		return (int) min;
	}

	public int getSec() {
		return (int) sec;
	}
	
	public String toString()
	{
		return day + " day, " + hour + " hour, " + min + " min, " + sec + " sec";
	}	
}
