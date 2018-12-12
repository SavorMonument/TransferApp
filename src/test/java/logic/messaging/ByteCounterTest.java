package logic.messaging;

import logic.connection.ByteCounter;
import org.junit.Test;

public class ByteCounterTest
{
	int count = 0;

	@Test
	public void countTest() throws InterruptedException
	{
		ByteCounter byteCounter = new ByteCounter(
				System.out::println, 1000);


		while(true)
		{
			if (Math.random() > 0.5)
			{
				byteCounter.addToCount(1000);
			}
//			System.out.println(1);
			Thread.sleep(100);
		}

//		assertEquals(5, count);
	}

}