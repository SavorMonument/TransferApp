package filetransfer;

import filesistem.FileException;
import filetransfer.api.TransferFileOutput;
import filetransfer.api.TransferInput;
import filetransfer.api.TransferOutput;
import network.ConnectionException;
import org.jetbrains.annotations.NotNull;

public class ObservabileFileReceiver extends FileReceiver
{
	private TransferObserver observer;

	public ObservabileFileReceiver(@NotNull TransferInput transferInput,
								   @NotNull TransferOutput transferOutput,
								   @NotNull TransferFileOutput fileOutput,
								   @NotNull TransferObserver observer,
								   long fileSizeBytes)
	{
		super(transferInput, transferOutput, fileOutput, fileSizeBytes);
		assert (null != observer) : "Observer is null";

		this.observer = observer;
	}

	@Override
	protected int transferChunk(byte[] buffer, int maxAmountToRead) throws ConnectionException, FileException
	{
		int amount = super.transferChunk(buffer, maxAmountToRead);
		observer.addBytesToCount(amount);
		return amount;
	}
}
