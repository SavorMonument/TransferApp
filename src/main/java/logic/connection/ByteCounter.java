package logic.connection;

import filetransfer.DeltaTime;
import network.streaming.Counter;

import java.util.concurrent.atomic.AtomicInteger;

public class ByteCounter extends Thread implements Counter
{
	private static final int MILLIS_iN_A_SECOND = 1_000;

	volatile private AtomicInteger count;
	private int amountPerSecond;

	private int eventUpdateFrequency;
	private ByteCounterEvent event;



	public ByteCounter(ByteCounterEvent event, int eventUpdateFrequencyMilli)
	{
		this.event = event;
		this.count = new AtomicInteger();

		eventUpdateFrequency = eventUpdateFrequencyMilli;

		setDaemon(true);
		start();
	}

	@Override
	public void run()
	{
		DeltaTime secondTimer = new DeltaTime();
		DeltaTime eventTimer = new DeltaTime();

		while (!isInterrupted())
		{
			int timeToSecond = MILLIS_iN_A_SECOND - secondTimer.getElapsedTimeMillis();
			if (timeToSecond <= 0)
			{
				int amountThisSecond = count.getAndSet(0);
				amountPerSecond = (amountThisSecond + amountPerSecond) / 2;
				secondTimer.reset();
				timeToSecond = MILLIS_iN_A_SECOND;
			}

			int timeToEvent = eventUpdateFrequency - eventTimer.getElapsedTimeMillis();
			if (timeToEvent <= 0)
			{
				int temp = amountPerSecond;
				new Thread(() -> event.updateOnBytes(temp)).start();
				eventTimer.reset();
				timeToEvent = eventUpdateFrequency;
			}

			try
			{
				int sleepTime = Math.min(timeToEvent, timeToSecond) - 1;
				if (sleepTime > 0)
					Thread.sleep(sleepTime);
			} catch (InterruptedException e)
			{
			}
		}
	}

	private int calculateBytePerSecond(int bytes, int millisPassed)
	{
		float ratio = (float) MILLIS_iN_A_SECOND / millisPassed;
		return (int) (bytes * ratio);
	}

	public void addToCount(int amount)
	{
		count.addAndGet(amount);
	}

	public int getAmountPerSecond()
	{
		return amountPerSecond;
	}

	public interface ByteCounterEvent
	{
		void updateOnBytes(long bytes);
	}
}
