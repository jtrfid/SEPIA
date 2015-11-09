package de.uni.freiburg.iig.telematik.sepia.petrinet.timedNet.concepts;

public interface ITimeContext {
	
	public String getName();
	
	public void setName(String name);
	
	public double getTimeFor(String activity);
	
	public ITimeBehaviour getTimeObjectFor(String activity);

}
