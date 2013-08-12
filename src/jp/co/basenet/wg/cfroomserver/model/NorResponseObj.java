package jp.co.basenet.wg.cfroomserver.model;

import java.nio.charset.Charset;

public class NorResponseObj {
	private int status;
	private int currentSize;
	private int fullSize;
	private int seqNo;
	private int recordCount;
	private String message;
	private byte[] messageB;
	
	public NorResponseObj(int status, int currentSize, 
			              int fullSize, int seqNo, int recordCount,
			              String message) {
		this.status = status;
		this.currentSize = currentSize;
		this.fullSize = fullSize;
		this.seqNo = seqNo;
		this.recordCount = recordCount;
		this.message = message;
		this.message = message;
		this.messageB = null;
	}
	
	public NorResponseObj(int status, int currentSize, 
			              int fullSize, int seqNo, int recordCount,
                          byte[] messageB) {
		this.status = status;
		this.currentSize = currentSize;
		this.fullSize = fullSize;
		this.seqNo = seqNo;
		this.recordCount = recordCount;
		this.message = null;
		this.messageB = messageB;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getCurrentSize() {
		return currentSize;
	}

	public void setCurrentSize(int currentSize) {
		this.currentSize = currentSize;
	}

	public int getFullSize() {
		return fullSize;
	}

	public void setFullSize(int fullSize) {
		this.fullSize = fullSize;
	}

	public int getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}

	public int getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public byte[] getMessageB() {
		return messageB;
	}

	public void setMessageB(byte[] messageB) {
		this.messageB = messageB;
	}
}
