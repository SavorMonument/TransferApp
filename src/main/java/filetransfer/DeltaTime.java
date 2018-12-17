package filetransfer;

public class DeltaTime
{
	private long startTime;

	public DeltaTime()
	{
		startTime = System.nanoTime();
	}

	public double getElapsedTimeSeconds()
	{
		return (getElapsedTimeNano() / 1e+9);
	}

	public int getElapsedTimeMillis()
	{
		return (int) (getElapsedTimeNano() / (long) 1e+6);
	}

	public long getElapsedTimeNano()
	{
		return System.nanoTime() - startTime;
	}

	public void reset()
	{
		startTime = System.nanoTime();
	}
}
