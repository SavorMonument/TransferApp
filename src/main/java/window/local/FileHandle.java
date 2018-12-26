package window.local;

import model.LocalFileHandle;

public class FileHandle implements LocalFileHandle
{
	private LocalFileInfo fileInfo;

	@Override
	public String getFullPath()
	{
		return fileInfo.getLocation();
	}

	@Override
	public void setUploadSpeed(long bytePerSecond)
	{
		fileInfo.setUploadSpeed(bytePerSecond);
	}
}
