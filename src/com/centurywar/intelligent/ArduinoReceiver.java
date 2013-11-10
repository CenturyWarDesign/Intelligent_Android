package com.centurywar.intelligent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
	 * ArduinoReceiver is responsible for catching broadcasted Amarino
	 * events.
	 * 
	 * It extracts data from the intent and updates the graph accordingly.
	 */
	public class ArduinoReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("get it from arduino");
			if (intent != null) {
				String data = intent.getStringExtra("data");
				if (data != null){
					System.out.println(data);
					try {
//						final int sensorReading = Integer.parseInt(data);
//						mGraph.addDataPoint(sensorReading);
					} catch (NumberFormatException e) {
						// data was not an integer
						//e.printStackTrace();
					}
				}
			}
		}
	}