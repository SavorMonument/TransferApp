package filetransfer;

import filesistem.FileException;
import filetransfer.api.TransferFileInput;
import filetransfer.api.TransferInput;
import filetransfer.api.TransferOutput;
import network.ConnectionException;
import org.jetbrains.annotations.NotNull;

public class ObservableFileTransmitter extends FileTransmitter
{
	TransferObserver observer;

	public ObservableFileTransmitter(@NotNull TransferOutput transferOutput,
									 @NotNull TransferInput transferInput,
									 @NotNull TransferFileInput fileInput,
									 @NotNull TransferObserver observer)
	{
		super(transferOutput, transferInput, fileInput);
		assert (null != observer) : "Observer is null";

		this.observer = observer;
	}

	@Override
	protected int transferChunk(byte[] buffer)
			throws FileException, ConnectionException
	{
		int amount = super.transferChunk(buffer);
		observer.addBytesToCount(amount);
		return amount;
	}
}
