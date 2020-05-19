package cn.edu.scujcc.model;

public class Result {
	public static final int OK = 1;
	public static final int DUPLICATED = -1;
	public static final int ERROR = 0;
	
	private int status;
	private String message;
	private User data;
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public User getData() {
		return data;
	}
	public void setData(User data) {
		this.data = data;
	}
	
}
