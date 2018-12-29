package window.remote;

import window.ByteMultipleFormatter;

public class RemoteFileInfo
{
	private String name;
	private long size;
	private long speed;
	private double progress = -1;

	public RemoteFileInfo(String name, long size, long speed, double progress)
	{
		this.name = name;
		this.size = size;
		this.speed = speed;
		this.progress = progress;
	}

	public RemoteFileInfo(String name, long size)
	{
		this.name = name;
		this.size = size;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getSize()
	{
		return ByteMultipleFormatter.getFormattedBytes(size);
	}

	public long getSizeInBytes()
	{
		return size;
	}

	public void setSize(long size)
	{
		this.size = size;
	}

	public String getSpeed()
	{
		if (speed == 0)
			return "";
		else
			return ByteMultipleFormatter.getFormattedBytes(speed);
	}

	public void setSpeed(long speed)
	{
		this.speed = speed;
	}

	public double getProgress()
	{
		return progress;
	}

	public void setProgress(double progress)
	{
		this.progress = progress;
	}

	public RemoteFileInfo clone()
	{
		return new RemoteFileInfo(
				this.name,
				this.size,
				this.speed,
				this.progress);
	}

	public void setTransferSpeed(long bytesPerSecond)
	{
		speed = bytesPerSecond;
	}

	public void setTransferProgress(double progress)
	{
		this.progress = progress;
	}
}
