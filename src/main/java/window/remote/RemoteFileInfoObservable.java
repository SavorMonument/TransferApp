package window.remote;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import logic.FileHandle;

import java.util.ArrayList;
import java.util.List;

public class RemoteFileInfoObservable extends RemoteFileInfo implements ObservableValue<RemoteFileInfo>, FileHandle
{
	private List<InvalidationListener> invalListeners = new ArrayList<>();

	public RemoteFileInfoObservable(String name, long size)
	{
		super(name, size);
	}

	@Override
	public void addListener(ChangeListener<? super RemoteFileInfo> listener)
	{
		//Apparently this is not used with a when putting the elem in an observable list
		//It is so apparent when you look at the billions of nested classes that the
		//javafx collections is made of
	}

	@Override
	public void removeListener(ChangeListener<? super RemoteFileInfo> listener)
	{
	}

	@Override
	public void addListener(InvalidationListener listener)
	{
		invalListeners.add(listener);
	}

	@Override
	public void removeListener(InvalidationListener listener)
	{
		invalListeners.remove(listener);
	}

	@Override
	public RemoteFileInfo getValue()
	{
		return this;
	}

	@Override
	public void setTransferSpeed(long bytesPerSecond)
	{
		super.setTransferSpeed(bytesPerSecond);
		notifyListeners();
	}

	@Override
	public void setTransferProgress(double progress)
	{
		super.setTransferProgress(progress);
		notifyListeners();
	}

	private void notifyListeners()
	{
		for(InvalidationListener listener: invalListeners)
		{
			listener.invalidated(this);
		}
	}
}
