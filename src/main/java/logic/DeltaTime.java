package logic;

import window.AppLogger;

import java.util.logging.Level;

public class DeltaTime
{
	private long elapsedTime;
	private long last;
	private long nsPerTick;

	public DeltaTime(int TicksPerSecond)
	{
		last = System.nanoTime();
		nsPerTick = (long) 1e+9 / TicksPerSecond;
	}

	public void update()
	{
		long now = System.nanoTime();

		elapsedTime += now - last;
		last = now;

//		AppLogger.getInstance().log(Level.FINEST,
//				"Nr of ticks extra(should be 0 else the app can't keep up): " + elapsedTime / nsPerTick);
	}

	/**
	 * Enough time has passed to count as tick
	 */
	public boolean enoughTimePassed()
	{
		if (elapsedTime > nsPerTick)
		{
			return true;
		}
		return false;
	}

	/**
	 * How many ns have to pass in order to count as a tick
	 */
	public long getTimeToTick()
	{
        return nsPerTick - elapsedTime;
	}

	public void subtractOneTick()
	{
		elapsedTime -= nsPerTick;
	}
}

