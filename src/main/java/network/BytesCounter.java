package network;

import com.sun.istack.internal.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

public class BytesCounter extends Thread
{
	private AtomicInteger byteCount;

	private ByteCounterEvent event;
	private int millisecondsBetweenUpdates;

	public BytesCounter(@NotNull ByteCounterEvent event, int millisecondsBetweenUpdates)
	{
		assert (null != event) : "Invalid event";
		assert (millisecondsBetweenUpdates > 0) : "Need positive millis";

		byteCount = new AtomicInteger();

		this.event = event;
		this.millisecondsBetweenUpdates = millisecondsBetweenUpdates;
		setDaemon(true);
	}

	@Override
	public void run()
	{
		while(!isInterrupted())
		{
			try
			{
				Thread.sleep(millisecondsBetweenUpdates);
			} catch (InterruptedException e)
			{
//				e.printStackTrace();
			}
			event.updateOnCounter(byteCount.getAndSet(0));
		}
	}

	public void addToCount(int amount)
	{
		byteCount.addAndGet(amount);
	}

	interface ByteCounterEvent
	{
		void updateOnCounter(int byteCount);
	}
}
