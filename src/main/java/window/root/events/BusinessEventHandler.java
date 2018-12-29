package window.root.events;

import logic.BusinessEvents;
import logic.FileHandle;
import model.FileInfo;
import window.AppLogger;
import window.UIEvents;
import window.connection.ConnectionController;
import window.local.LocalController;
import window.remote.RemoteController;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BusinessEventHandler implements BusinessEvents
{
	private static final Logger LOGGER = AppLogger.getInstance();
	private static final BusinessEventHandler INSTANCE = new BusinessEventHandler();

	private List<ConnectionStateEvent> connectionStateHandlers = new ArrayList<>();
	private List<DisplayEvent> displayHandlers = new ArrayList<>();

	private ConnectionRequestEvent requestHandler;
	private LocalInformationEvent localInformationHandler;
	private RemoteInformationEvent remoteInformationHandler;


	private BusinessEventHandler()
	{
	}

	public static BusinessEventHandler getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void setFileEventHandler(UIEvents.FileEvents fileEvents)
	{
		LocalController.setFileEvents(fileEvents);
		RemoteController.setFileEvents(fileEvents);
	}

	@Override
	public void setConnectionEventHandler(UIEvents.ConnectionEvents connectionHandler)
	{
		ConnectionController.setConnectionEventHandler(connectionHandler);
	}

	public void addConnectionStateHandler(ConnectionStateEvent stateHandler)
	{
		connectionStateHandlers.add(stateHandler);
	}

	public void setRemoteInformationHandler(RemoteInformationEvent informationHandler)
	{
		remoteInformationHandler = informationHandler;
	}

	public void addDisplayHandler(DisplayEvent displayHandler)
	{
		displayHandlers.add(displayHandler);
	}

	public void setConnectionRequestHandler(ConnectionRequestEvent handler)
	{
		requestHandler = handler;
	}

	public void setLocalInformationHandler(LocalInformationEvent localHandler)
	{
		localInformationHandler = localHandler;
	}

	@Override
	public void updateRemoteFileList(List<FileInfo> filesInfo)
	{
		LOGGER.log(Level.ALL, String.format(
				"Business event: %s with %s", this.getClass().getEnclosingMethod(), filesInfo.toString()));
		if (null != remoteInformationHandler)
			remoteInformationHandler.updateFileList(filesInfo);
	}

	@Override
	public void setDownloadingState(boolean isDownloading)
	{
		LOGGER.log(Level.ALL, String.format(
				"Business event: isDownloading with %s", isDownloading));
		if (null != remoteInformationHandler)
			remoteInformationHandler.setDownloadDisabled(isDownloading);
	}

	@Override
	public void setConnectionState(String state)
	{
		for (ConnectionStateEvent event: connectionStateHandlers)
		{
			event.updateConnectionState(state);
		}
	}

	@Override
	public void printMessageOnDisplay(String message)
	{
		LOGGER.log(Level.ALL, String.format(
				"Business event: printMessageOnDisplay, message: %s", message));
		for (DisplayEvent event: displayHandlers)
		{
			event.printMessageOnDisplay(message);
		}
	}

	@Override
	public void printUploadSpeed(long speed)
	{
		for (DisplayEvent event: displayHandlers)
		{
			event.printUploadSpeed(speed);
		}
	}

	@Override
	public void printDownloadSpeed(long speed)
	{
		for (DisplayEvent event: displayHandlers)
		{
			event.printDownloadSpeed(speed);
		}
	}

	@Override
	public boolean confirmConnectionRequest(String url)
	{
		LOGGER.log(Level.ALL, String.format(
				"Business event: %s with %s", this.getClass().getEnclosingMethod(), url));
		if (null != requestHandler)
			return requestHandler.confirmConnectionRequest(url);
		return false;
	}

	@Override
	public FileHandle getLocalFileHandle(FileInfo fileInfo)
	{
		LOGGER.log(Level.ALL, String.format(
				"Business event: %s with %s", this.getClass().getEnclosingMethod(), fileInfo.getName()));
		if (null != localInformationHandler)
			return localInformationHandler.getLocalHandler(fileInfo);
		else
			return null;
	}

	@Override
	public FileHandle getRemoteFileHandle(FileInfo fileInfo)
	{
		LOGGER.log(Level.ALL, String.format(
				"Business event: %s with %s", this.getClass().getEnclosingMethod(), fileInfo.getName()));
		if (null != remoteInformationHandler)
			return remoteInformationHandler.getRemoteFileHandle(fileInfo);
		else
			return null;
	}

	@Override
	public String getDownloadLocation()
	{
		LOGGER.log(Level.ALL, String.format(
				"Business event: %s", this.getClass().getEnclosingMethod()));
		if (null != remoteInformationHandler)
			return remoteInformationHandler.getDownloadLocation();
		else
			return null;
	}

	@Override
	public String getLocalFilePath(FileInfo fileInfo)
	{
		if (null != localInformationHandler)
			return localInformationHandler.getLocalFilePath(fileInfo);
		else
			return null;
	}
}
