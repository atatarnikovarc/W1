package com.redaril.dmptf.util.jmx;

import java.util.ArrayList;
import java.util.List;

public class JmxResponse {
	private boolean state;
	private List<String> message = new ArrayList<String>();
	
	public JmxResponse () {
		this.state = false;
		this.message.clear();
	}
	
	public JmxResponse (boolean state) {
		this.state = state;
		this.message = null;
	}

	public JmxResponse (String msg) {
		this.state = false;
		this.message.clear();
		this.message.add(msg);
	}

	public JmxResponse (boolean state, String msg) {
		this.state = state;
		this.message.clear();
		this.message.add(msg);
	}

	public void setMessage (String msg) {
		this.message.clear();
		this.message.add(msg);
	}
	
	public void setState (boolean state) {
		this.state = state;
	}
	
	public List<String> getMessage () {
		return this.message;
	}
	
	public boolean getState () {
		return this.state;
	}
	
	public void pushMessage (String msg) {
		this.message.add(msg);
	}

	public void pushMessage (Object msg) {
		/*************************************/
		/* can be unpredictable result       */
		/* because in use .toString() method */
		/*************************************/
		if ( msg != null ) {
			try {
				this.message.add(msg.toString());
//				System.out.println("DEBUG: jmxResponse.pushMessage (Object msg): "
//						+ "message add");
			} catch (Exception e) {
				System.out.println("Exception: jmxResponse.pushMessage (Object msg): "
						+ e.getMessage());
			}
		} else {
//			System.out.println("DEBUG: jmxResponse.pushMessage (Object msg): "
//					+ "skip message");
		}
	}

	public void clean () {
		this.state = false;
		this.message.clear();
	}
	
	public boolean contains (String str) {
		boolean result = false;
		int i = 0;
		String line = "";
		while ( i < this.message.size() ) {
			line = message.get(i);
			if (line.contains(str))
				result = true;
			i++;
		}
		return result;
	}
}
