package window.local;

import window.ByteMultipleFormatter;

public class LocalFileInfo
{
	private String name;
	private long size;
	private String location;
	private long uploadSpeed;

	public LocalFileInfo(String name, long size, String location)
	{
		this.name = name;
		this.size = size;
		this.location = location;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public long getSize()
	{
		return size;
	}

	public void setSize(long size)
	{
		this.size = size;
	}

	public String getLocation()
	{
		return location;
	}

	public void setLocation(String location)
	{
		this.location = location;
	}

	public String getUploadSpeed()
	{
		return ByteMultipleFormatter.getFormattedBytes(uploadSpeed);
	}

	public long getUploadSpeedAsBytes()
	{
		return uploadSpeed;
	}

	public void setUploadSpeed(long uploadSpeed)
	{
		this.uploadSpeed = uploadSpeed;
	}
}
