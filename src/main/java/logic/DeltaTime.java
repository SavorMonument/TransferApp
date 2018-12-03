package logic;

public class DeltaTime
{
	private long startTime;

	public DeltaTime()
	{
		startTime = System.nanoTime();
	}

	public int getElapsedTimeMillis()
	{
		return (int) getElapsedTimeNano() / (int) 1e+6;
	}

	public long getElapsedTimeNano()
	{
		return System.nanoTime() - startTime;
	}
}
