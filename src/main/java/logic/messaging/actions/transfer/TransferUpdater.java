package logic.messaging.actions.transfer;

import filetransfer.DeltaTime;
import filetransfer.TransferObserver;
import logic.FileHandle;

import java.util.concurrent.atomic.AtomicInteger;

public class TransferUpdater extends Thread implements TransferObserver
{
	private FileHandle fileHandle;
	private volatile AtomicInteger count;

	private double totalSize;
	private double transferred;

	public TransferUpdater(FileHandle fileHandle, long fileSize)
	{
		this.fileHandle = fileHandle;
		this.count = new AtomicInteger(0);

		this.totalSize = fileSize;
		this.transferred = 0;

		setDaemon(true);
	}

	@Override
	public void addBytesToCount(int amount)
	{
		count.getAndAdd(amount);
	}

	@Override
	public void run()
	{
		DeltaTime dt = new DeltaTime();
		while (!isInterrupted())
		{
			try
			{
				int currentCount = count.getAndSet(0);
				transferred += currentCount;
				double progress = transferred / totalSize;

				fileHandle.setTransferSpeed(currentCount);
				fileHandle.setTransferProgress(progress);

				Thread.sleep(1000 - dt.getElapsedTimeMillis());
				dt.reset();
			} catch (InterruptedException e)
			{
				fileHandle.setTransferSpeed(0);
				interrupt();
//					e.printStackTrace();
			}
		}
	}
}
