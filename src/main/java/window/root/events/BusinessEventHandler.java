package window.root.events;

import logic.BusinessEvents;
import window.AppLogger;
import window.UIEvents;
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
	private List<RemoteInformationEvent> remoteHandlers = new ArrayList<>();
	private List<DisplayEvent> displayHandlers = new ArrayList<>();

	private ConnectionRequestEvent requestHandler;
	private LocalInformationEvent localInformationHandler;

	private BusinessEventHandler()
	{
	}

	public static BusinessEventHandler getInstance()
	{
		return INSTANCE;
	}

	public void addConnectionStateHandler(ConnectionStateEvent stateHandler)
	{
		connectionStateHandlers.add(stateHandler);
	}

	public void addRemoteInformationHandler(RemoteInformationEvent informationHandler)
	{
		remoteHandlers.add(informationHandler);
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
		for(RemoteInformationEvent event: remoteHandlers)
		{
			event.updateRemoteFileList(filesInfo);
		}
	}

	@Override
	public void setDownloadingState(boolean isDownloading)
	{
		LOGGER.log(Level.ALL, String.format(
				"Business event: isDownloading with %s", isDownloading));
		for(RemoteInformationEvent event: remoteHandlers)
		{
			event.setDownloadDisabled(isDownloading);
		}
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
	public String getLocalFilePath(String fileName)
	{
		LOGGER.log(Level.ALL, String.format(
				"Business event: %s with %s", this.getClass().getEnclosingMethod(), fileName));
		if (null != localInformationHandler)
			return localInformationHandler.getLocalHandler(fileName);
		return "";
	}

	@Override
	public void setFileEventHandler(UIEvents.FileEvents fileEvents)
	{
		LocalController.setFileEvents(fileEvents);
		RemoteController.setFileEvents(fileEvents);
	}
}
