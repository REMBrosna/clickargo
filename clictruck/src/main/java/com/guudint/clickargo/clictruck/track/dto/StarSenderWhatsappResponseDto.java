package com.guudint.clickargo.clictruck.track.dto;

import java.io.Serializable;

public class StarSenderWhatsappResponseDto implements Serializable{

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = -1L;

	// Attributes
	/////////////
    private boolean status;
    private Data data;
    private String message;
    
	// Constructor
    public StarSenderWhatsappResponseDto() {
		super();
	}
    
    
	@Override
	public String toString() {
		return "StarSenderWhatsappResponseDto [status=" + status + ", data=" + data + ", message=" + message + "]";
	}


	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public Data getData() {
		return data;
	}
	public void setData(Data data) {
		this.data = data;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	///////////////  ///////////////  ///////////////

	public static class Data implements Serializable{

		// Static Attributes
		////////////////////
		private static final long serialVersionUID = -1L;

		// Attributes
		/////////////
        public int message_id;

    	// Constructor
		public Data() {
			super();
		}

		@Override
		public String toString() {
			return "Data [message_id=" + message_id + "]";
		}

		// Properties
		/////////////
		public int getMessage_id() {
			return message_id;
		}

		public void setMessage_id(int message_id) {
			this.message_id = message_id;
		}
    }
}
