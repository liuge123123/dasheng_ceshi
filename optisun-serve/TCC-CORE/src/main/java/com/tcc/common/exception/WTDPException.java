

package com.tcc.common.exception;

import com.tcc.common.utils.ResultCode;

/**
 * 自定义异常
 *
 */
public class WTDPException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
    private String msg;
    private int code = 500;
    
    public WTDPException(String msg) {
		super(msg);
		this.msg = msg;
	}

	public WTDPException(ResultCode resultCode) {
		this(resultCode.getMsg(), resultCode.getCode());
	}
	
	public WTDPException(String msg, Throwable e) {
		super(msg, e);
		this.msg = msg;
	}
	
	public WTDPException(String msg, int code) {
		super(msg);
		this.msg = msg;
		this.code = code;
	}
	
	public WTDPException(String msg, int code, Throwable e) {
		super(msg, e);
		this.msg = msg;
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
	
	
}
