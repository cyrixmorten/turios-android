package com.turios.modules.utils;

import java.util.HashMap;
import java.util.Map;

import com.parse.ParseAnalytics;

public class TuriosParseAnalytics {

	public static void parseAnalyticsIncrementTurnouts() {
		Map<String, String> turnout = new HashMap<String, String>();
		turnout.put("event", "turnout");
		ParseAnalytics.trackEvent("turnout", turnout);
	}
}
