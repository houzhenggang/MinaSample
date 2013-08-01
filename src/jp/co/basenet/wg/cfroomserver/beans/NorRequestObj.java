package jp.co.basenet.wg.cfroomserver.beans;

public class NorRequestObj {
	private int status;
	private String message;
	
	public NorRequestObj(int status, String message) {
		this.status = status;
		this.message = message;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	public int getStatus() {
		return status;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
}
