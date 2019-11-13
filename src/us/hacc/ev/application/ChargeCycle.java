package us.hacc.ev.application;

import java.util.Calendar;
import java.util.Date;

import us.hacc.ev.util.Tools;

public class ChargeCycle
{
	private String station, sessionInitiator, portType, paymentMode;
	private Date startTime, endTime;
	private double duration, energy, sessionCost, theoCost;
	private long sessionId;
	
	public ChargeCycle(String station, String sessionInitiator, String startTime,
			String endTime, String energy, String sessionCost,
							String sessionId, String portType, String paymentMode)
	{
		this.station = station;
		this.sessionInitiator = sessionInitiator;
		this.startTime = Tools.convertStringToDate(startTime);
		this.endTime = Tools.convertStringToDate(endTime);
		this.energy = Double.parseDouble(energy);		
		this.sessionCost = Double.parseDouble(sessionCost);
		this.sessionId = Long.parseLong(sessionId);
		this.portType = portType;
		this.paymentMode = paymentMode;		
		this.duration = this.endTime.getTime() - this.startTime.getTime();
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(this.startTime);
		
		if(cal.get(Calendar.HOUR_OF_DAY) >= 17 && cal.get(Calendar.HOUR_OF_DAY) < 22)
		{
			this.theoCost = this.energy * Tools.onPeak;
		}
		else if (cal.get(Calendar.HOUR_OF_DAY) >= 9 && cal.get(Calendar.HOUR_OF_DAY) < 17)
		{
			this.theoCost = this.energy * Tools.midDay;
		}
		else
		{
			this.theoCost = this.energy * Tools.offPeak;
		}
	}

	public String getStation() {
		return station;
	}

	public String getSessionInitiator() {
		return sessionInitiator;
	}

	public Date getStartTime() {
		return startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public double getDuration() {
		return duration;
	}

	public double getEnergy() {
		return energy;
	}

	public double getSessionCost() {
		return sessionCost;
	}
	
	public double getTheoCost() {
		return theoCost;
	}

	public long getSessionId() {
		return sessionId;
	}

	public String getPortType() {
		return portType;
	}

	public String getPaymentMode() {
		return paymentMode;
	}	
}
