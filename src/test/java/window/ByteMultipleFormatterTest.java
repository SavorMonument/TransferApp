package window;

import org.junit.Test;

import static org.junit.Assert.*;

public class ByteMultipleFormatterTest
{

	@Test
	public void getFormattedBytesTake1()
	{
		assertEquals("1.0 MIB", ByteMultipleFormatter.getFormattedBytes(1_048_581));
	}

	@Test
	public void getFormattedBytesTake2()
	{
		assertEquals("1.0 KIB", ByteMultipleFormatter.getFormattedBytes(1024));
	}

	@Test
	public void getFormattedBytesTake3()
	{
		assertEquals("12.0 bytes", ByteMultipleFormatter.getFormattedBytes(12));
	}

	@Test
	public void getFormattedBytesZero()
	{
		assertEquals("0.0 bytes", ByteMultipleFormatter.getFormattedBytes(0));
	}
}