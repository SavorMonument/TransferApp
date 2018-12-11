package logic.messaging;

import org.junit.Test;

import static org.junit.Assert.*;

public class ByteCounterTest
{
	int count = 0;

	@Test
	public void countTest() throws InterruptedException
	{
		ByteCounter byteCounter = new ByteCounter(
				(bytes) -> System.out.println( bytes), 500);


		while(true)
		{
			byteCounter.addToCount(1000);

//			System.out.println(1);
			Thread.sleep(200);
		}

//		assertEquals(5, count);
	}

}