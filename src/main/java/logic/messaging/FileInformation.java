package logic.messaging;

import java.util.Objects;

public class FileInformation
{
	String name;
	long sizeInBytes;

	public FileInformation(String name, long byteSize)
	{
		this.name = name;
		this.sizeInBytes = byteSize;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		FileInformation that = (FileInformation) o;
		return sizeInBytes == that.sizeInBytes &&
				Objects.equals(name, that.name);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(name, sizeInBytes);
	}
}
