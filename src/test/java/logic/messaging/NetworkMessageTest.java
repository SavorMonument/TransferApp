package logic.messaging;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

public class NetworkMessageTest
{

	@Test
	public void collectionCoder()
	{
		List<FileInformation> originalList = new ArrayList<>();

		originalList.add(new FileInformation("Test1", 10L));
		originalList.add(new FileInformation("Test2", 15L));
		originalList.add(new FileInformation("Test3", 3453454L));

		String codedMessage = NetworkMessage.listCoder(originalList);
		Collection<FileInformation> resultingList = NetworkMessage.listDecoder(codedMessage);

		assertArrayEquals(originalList.toArray(), resultingList.toArray());
	}

	@Test
	public void collectionCoderNoElem()
	{
		List<FileInformation> originalList = new ArrayList<>();

		String codedMessage = NetworkMessage.listCoder(originalList);
		Collection<FileInformation> resultingList = NetworkMessage.listDecoder(codedMessage);

		assertArrayEquals(originalList.toArray(), resultingList.toArray());
	}
}