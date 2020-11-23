package com.mysite.core.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.post.JSONResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysite.core.bean.MeterBean;
import com.mysite.core.bean.TimerBean;

@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=Custom Metric Servlet",
		ServletResolverConstants.SLING_SERVLET_METHODS + "=GET",
		ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/custmetric",
		ServletResolverConstants.SLING_SERVLET_EXTENSIONS + "=json" })
public class CustomMetricServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 1L;

	/** The Constant log. */
	private static final Logger log = LoggerFactory.getLogger(CustomMetricServlet.class);

	@Override
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws ServletException, IOException {

		log.info("inside the custom metrics servlet");

		JSONObject jsonFromJmx = new JSONObject();
		try {
			String keyword = request.getParameter("query");
			jsonFromJmx = fetchJsonFromJConsole(keyword);

			WriteJsonToResponse(response, jsonFromJmx.toString());

		} catch (MalformedObjectNameException e1) {
			e1.printStackTrace();
		}

	}

	private JSONObject fetchJsonFromJConsole(String keyword) throws MalformedObjectNameException {

		JSONObject finalJson = new JSONObject();
		try {
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			ObjectName countObjName = new ObjectName("unsplash.core:name=responseCounter15,type=Metrics");
			Object count = mbs.getAttribute(countObjName, "Count");

			ObjectName timerObjName = new ObjectName("unsplash.core:name=responseTimer15,type=Metrics");
			Object countTimer = mbs.getAttribute(timerObjName, "Count");
			Object max = mbs.getAttribute(timerObjName, "Max");
			Object min = mbs.getAttribute(timerObjName, "Min");
			Object stdDev = mbs.getAttribute(timerObjName, "StdDev");
			Object rateUnit = mbs.getAttribute(timerObjName, "RateUnit");
			Object mean = mbs.getAttribute(timerObjName, "Mean");
			Object _75thPercentile = mbs.getAttribute(timerObjName, "75thPercentile");
			Object _95thPercentile = mbs.getAttribute(timerObjName, "95thPercentile");
			Object _98thPercentile = mbs.getAttribute(timerObjName, "98thPercentile");
			Object _99thPercentile = mbs.getAttribute(timerObjName, "99thPercentile");
			Object _999thPercentile = mbs.getAttribute(timerObjName, "999thPercentile");
			Object _50thPercentile = mbs.getAttribute(timerObjName, "50thPercentile");
			Object durationUnit = mbs.getAttribute(timerObjName, "DurationUnit");
			Object meanRate = mbs.getAttribute(timerObjName, "MeanRate");
			Object oneMinuteRate = mbs.getAttribute(timerObjName, "OneMinuteRate");
			Object fiveMinuteRate = mbs.getAttribute(timerObjName, "FiveMinuteRate");
			Object fifteenMinuterate = mbs.getAttribute(timerObjName, "FifteenMinuteRate");

			TimerBean timerBean = new TimerBean((double) max, (double) mean, (double) min, (double) _50thPercentile,
					(double) _75thPercentile, (double) _95thPercentile, (double) _98thPercentile,
					(double) _99thPercentile, (double) _999thPercentile, (double) stdDev, (double) fifteenMinuterate,
					(double) oneMinuteRate, (double) fiveMinuteRate, (double) meanRate, (String) durationUnit,
					(String) rateUnit, (long) countTimer);

			ObjectName meterObjName = new ObjectName("unsplash.core:name=responseMeter15,type=Metrics");
			Object countMeter = mbs.getAttribute(meterObjName, "Count");
			Object m15_rate_Meter = mbs.getAttribute(meterObjName, "FifteenMinuteRate");
			Object m1_rate_Meter = mbs.getAttribute(meterObjName, "OneMinuteRate");
			Object m5_rate_Meter = mbs.getAttribute(meterObjName, "FiveMinuteRate");
			Object mean_rate_Meter = mbs.getAttribute(meterObjName, "MeanRate");
			Object unitMeter = mbs.getAttribute(meterObjName, "RateUnit");

			MeterBean meterBean = new MeterBean((double) m15_rate_Meter, (double) m1_rate_Meter, (double) m5_rate_Meter,
					(double) mean_rate_Meter, (long) countMeter, (String) unitMeter);

			if (keyword.equals("counter")) {
				finalJson = fetchCounterJson((long) count);
			} else if (keyword.equals("timer")) {
				finalJson = fetchTimerJson(timerBean);
			} else if (keyword.equals("meter")) {
				finalJson = fetchMeterJson(meterBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return finalJson;

	}

	private JSONObject fetchMeterJson(MeterBean meterBean) {

		JSONObject meterJson = new JSONObject();
		JSONObject meterJsonA = new JSONObject();
		try {
			meterJsonA.put("count", meterBean.getCount());
			meterJsonA.put("m15_rate", meterBean.getM15_rate());
			meterJsonA.put("m1_rate", meterBean.getM1_rate());
			meterJsonA.put("m5_rate", meterBean.getM5_rate());
			meterJsonA.put("mean_rate", meterBean.getMean_rate());
			meterJsonA.put("units", meterBean.getUnits());
			JSONObject json = new JSONObject();
			json.put("meter.test.metric.a", meterJsonA);

			meterJson.put("version", "3.0.0");
			meterJson.put("meters", json);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return meterJson;
	}

	private JSONObject fetchTimerJson(TimerBean bean) {
		JSONObject timerJson = new JSONObject();
		JSONObject timerJsonA = new JSONObject();
		try {
			timerJsonA.put("count", bean.getCount());
			timerJsonA.put("max", bean.getMax());
			timerJsonA.put("mean", bean.getMean());
			timerJsonA.put("min", bean.getMin());
			timerJsonA.put("p50", bean.getP50());
			timerJsonA.put("p75", bean.getP75());
			timerJsonA.put("p95", bean.getP95());
			timerJsonA.put("p98", bean.getP98());
			timerJsonA.put("p99", bean.getP98());
			timerJsonA.put("p999", bean.getP99());
			timerJsonA.put("stddev", bean.getStddev());
			timerJsonA.put("m15_rate", bean.getM15_rate());
			timerJsonA.put("m1_rate", bean.getM1_rate());
			timerJsonA.put("m5_rate", bean.getM5_rate());
			timerJsonA.put("mean_rate", bean.getMean_rate());
			timerJsonA.put("duration_units", bean.getDuration_units());
			timerJsonA.put("rate_units", bean.getRate_units());

			JSONObject json = new JSONObject();
			json.put("counter.test.metric.a", timerJsonA);

			timerJson.put("version", "3.0.0");
			timerJson.put("timers", json);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return timerJson;
	}

	private JSONObject fetchCounterJson(long count) {

		JSONObject CounterJson = new JSONObject();
		JSONObject countJsonA = new JSONObject();
		try {
			countJsonA.put("count", count);
			JSONObject countJsonB = new JSONObject();
			countJsonB.put("count", count + 1);
			JSONObject json = new JSONObject();
			json.put("counter.test.metric.a", countJsonA);
			json.put("counter.test.metric.b", countJsonB);

			CounterJson.put("version", "3.0.0");
			CounterJson.put("counters", json);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return CounterJson;
	}

	protected final void WriteJsonToResponse(final SlingHttpServletResponse response, final String jsonString)
			throws IOException {
		response.setCharacterEncoding((StandardCharsets.UTF_8).toString());
		response.setContentType(JSONResponse.RESPONSE_CONTENT_TYPE);
		final PrintWriter printWriter = response.getWriter();
		printWriter.write(jsonString);
	}
}
