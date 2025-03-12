package com.guudint.clickargo.clictruck.track.dto;

import java.io.Serializable;
import java.util.ArrayList;

public class DistanceMatrixDto implements Serializable {

	// Static Attributes
	////////////////////
	private static final long serialVersionUID = -1L;

	// Attributes
	/////////////
	public ArrayList<String> destination_addresses;
	public ArrayList<String> origin_addresses;
	public ArrayList<Row> rows;
	public String error_message;
	public String status;

	@Override
	public String toString() {
		return "DistanceMatrixDto [destination_addresses=" + destination_addresses + ", origin_addresses="
				+ origin_addresses + ", rows=" + rows + ", error_message=" + error_message + ", status=" + status + "]";
	}
	//
	public ArrayList<String> getDestination_addresses() {
		return destination_addresses;
	}

	public void setDestination_addresses(ArrayList<String> destination_addresses) {
		this.destination_addresses = destination_addresses;
	}

	public ArrayList<String> getOrigin_addresses() {
		return origin_addresses;
	}

	public void setOrigin_addresses(ArrayList<String> origin_addresses) {
		this.origin_addresses = origin_addresses;
	}

	public ArrayList<Row> getRows() {
		return rows;
	}

	public void setRows(ArrayList<Row> rows) {
		this.rows = rows;
	}

	public String getError_message() {
		return error_message;
	}

	public void setError_message(String error_message) {
		this.error_message = error_message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	///////////////////////////////////////////////
	
	public static class Row {

		public ArrayList<Element> elements;
		
		public ArrayList<Element> getElements() {
			return elements;
		}

		public void setElements(ArrayList<Element> elements) {
			this.elements = elements;
		}
	}

	///////////////////////////////////////////////
	public static class Element {
		public Distance distance;
		public Duration duration;
		public String status;

		@Override
		public String toString() {
			return "Element [distance=" + distance + ", duration=" + duration + ", status=" + status + "]";
		}

		public Distance getDistance() {
			return distance;
		}

		public void setDistance(Distance distance) {
			this.distance = distance;
		}

		public Duration getDuration() {
			return duration;
		}

		public void setDuration(Duration duration) {
			this.duration = duration;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}
		
	}

	///////////////////////////////////////////////
	public static class Distance {
		public String text;
		public int value;

		@Override
		public String toString() {
			return "Distance [text=" + text + ", value=" + value + "]";
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}
		
	}

	///////////////////////////////////////////////
	public static class Duration {
		public String text;
		public int value;

		@Override
		public String toString() {
			return "Duration [text=" + text + ", value=" + value + "]";
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}
		
	}
}
