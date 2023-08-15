package projects.exception;

public class DbException extends RuntimeException {

	public DbException() {
		// TODO Auto-generated constructor stub
	}

	public DbException(String message) {
		super(message);
	}

	public DbException(Throwable cause) {
		super(cause);
	}

	public DbException(String message, Throwable cause) {
		super(message, cause);
	}

}
