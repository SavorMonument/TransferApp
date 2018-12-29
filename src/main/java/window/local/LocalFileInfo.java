package window.local;

import model.FileInfo;
import window.ByteMultipleFormatter;

import java.util.Objects;

public class LocalFileInfo
{
	private String name;
	private long size;
	private String path;
	private long uploadSpeed;

	public LocalFileInfo(String name, long size, String path)
	{
		this.name = name;
		this.size = size;
		this.path = path;
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

	public void setSize(long size)
	{
		this.size = size;
	}

	public String getPath()
	{
		return path;
	}

	public void setPath(String path)
	{
		this.path = path;
	}

	public String getUploadSpeed()
	{
		if (uploadSpeed == 0)
			return "";
		else
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

	public FileInfo getFileInfo()
	{
		return new FileInfo(name, size);
	}

	public String getFullPath()
	{
		return path;
	}

	public void setTransferSpeed(long bytesPerSecond)
	{
		uploadSpeed = bytesPerSecond;
	}

	public void setTransferProgress(double progress)
	{
	}

	public long getSizeInBytes()
	{
		return size;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		LocalFileInfo fileInfo = (LocalFileInfo) o;
		return size == fileInfo.size &&
				Objects.equals(name, fileInfo.name);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(name, size);
	}
}
