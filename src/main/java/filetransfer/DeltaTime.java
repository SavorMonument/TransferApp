package filetransfer;

public class DeltaTime
{
	private long startTime;

	public DeltaTime()
	{
		startTime = System.nanoTime();
	}

	public int getElapsedTimeMillis()
	{
		return (int) (getElapsedTimeNano() / (long) 1e+6);
	}

	public long getElapsedTimeNano()
	{
		return System.nanoTime() - startTime;
	}
}
