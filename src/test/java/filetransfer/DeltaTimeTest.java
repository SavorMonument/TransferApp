package filetransfer;

import org.junit.Test;

import java.util.Timer;

import static org.junit.Assert.*;

public class DeltaTimeTest
{
	@Test
	public void getElapsedTimeMillis() throws InterruptedException
	{
		int timeMilli = 1000;
		DeltaTime dt = new DeltaTime();

		Thread.sleep(timeMilli);

		assertEquals(1, (double)timeMilli / (double) dt.getElapsedTimeMillis(), 0.01);
	}

	@Test
	public void getElapsedTimeNano() throws InterruptedException
	{
		long timeNano = 1_000_000_000L;
		int timeMilli = 1000;
		DeltaTime dt = new DeltaTime();

		Thread.sleep(timeMilli);

		assertEquals(1, (double)timeNano / (double) dt.getElapsedTimeNano(), 0.01);
	}
}