package window;

/**
 * Transforms 1024 bytes in the string 1.0 KIB, 1_048_576 in 1.0 MIB and so on
 */
public class ByteMultipleFormatter
{
	private enum Units
	{
		bytes,
		KIB,
		MIB,
		GIB,
		TIB;
	}

	public static String getFormattedBytes(long byteAmount)
	{
		int divisionCounter = 0;
		double convertedAmount = byteAmount;

		while (convertedAmount / 1024 >= 1)
		{
			convertedAmount /= 1024;
			divisionCounter++;
		}

		return String.format("%.1f %s", convertedAmount, Units.values()[divisionCounter]);
	}
}
