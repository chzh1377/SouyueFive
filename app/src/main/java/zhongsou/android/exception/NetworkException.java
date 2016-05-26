package zhongsou.android.exception;
public class NetworkException extends Exception {
	private static final long serialVersionUID = 4252028396541683002L;
	public NetworkException() {
		super();
	}
	public NetworkException(String detailMessage) {
		super(detailMessage);
	}
	public NetworkException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}
	public NetworkException(Throwable throwable) {
		super(throwable);
	}
}
