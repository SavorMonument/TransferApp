package window.remote;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.beans.Observable;
import javafx.collections.ListChangeListener;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class RemoteFileInfoObservableTest
{
	ObservableListWrapper<RemoteFileInfoObservable> observableList;
	RemoteFileInfoObservable testObservable;
	boolean successful;

	@Before
	public void setUp()
	{
		observableList = new ObservableListWrapper<>(new ArrayList<>(), param -> new Observable[]{param});

		testObservable = new RemoteFileInfoObservable("Test file", 200);

		observableList.add(testObservable);
	}

	@Test
	public void setTransferSpeed() throws InterruptedException
	{
		successful = false;

		observableList.addListener(new ListChangeListener<RemoteFileInfoObservable>()
		{
			@Override
			public void onChanged(Change<? extends RemoteFileInfoObservable> c)
			{
				successful = true;
			}
		});
		testObservable.setTransferSpeed(321);

		assert (successful);
	}

	@Test
	public void setTransferProgress()
	{
		successful = false;

		observableList.addListener((ListChangeListener<RemoteFileInfoObservable>) c -> successful = true);
		testObservable.setTransferProgress(321);

		assert (successful);
	}
}