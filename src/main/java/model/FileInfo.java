package model;

public class FileInfo
{
	private String name;
	private long sizeInBytes;

	public FileInfo(String name, long sizeInBytes)
	{
		this.name = name;
		this.sizeInBytes = sizeInBytes;
	}

	public String getName()
	{
		return name;
	}

	public long getSizeInBytes()
	{
		return sizeInBytes;
	}

	@Override
	public String toString()
	{
		return String.format("FileInfo: [name: \"%s\", sizeInBytes: \"%s\"]", name, sizeInBytes);
	}
}
