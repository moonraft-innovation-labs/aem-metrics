package com.mysite.core.servlets;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import com.adobe.reef.siren.Entity;
import com.mysite.core.service.UnsplashService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.metrics.Counter;
import org.apache.sling.commons.metrics.Gauge;
import org.apache.sling.commons.metrics.Histogram;
import org.apache.sling.commons.metrics.Meter;
import org.apache.sling.commons.metrics.MetricsService;
import org.apache.sling.commons.metrics.Timer;
import org.json.JSONException;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.osgi.service.component.annotations.Component;

import com.mysite.core.utils.UnsplashHtmlBuilder;
import org.osgi.service.component.annotations.Reference;

@Component(service = Servlet.class, property = { ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/unsplash",
		ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET })
public class UnsplashServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 1L;

	@Reference
	private UnsplashService service;

	private UnsplashHtmlBuilder htmlBuilder = new UnsplashHtmlBuilder();

	@Reference
	private MetricsService metricsService;

	private Counter counter;

	private Timer timer;

	private Meter meter;

	@Override
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws ServletException, IOException {

		initialiseMetrics();

		final String query = request.getRequestParameter("query").getString().trim();
		final String pageNum = request.getRequestParameter("page").getString();
		String responseHTML = null;
		HttpGet httpGet = new HttpGet(extractUrl(query, pageNum));
		final CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		httpGet.addHeader("Content-Type", "application/json");
		long startTime = System.nanoTime();
		final HttpResponse httpResponse = httpClient.execute(httpGet);
		long elapsedTime = System.nanoTime() - startTime;

		// check the elapsed time
		timer.update(elapsedTime, TimeUnit.NANOSECONDS);
		// increment counter after calling api
		counter.increment();
		// meter measures rate of requests per second
		meter.mark();

		final HttpEntity entity = httpResponse.getEntity();
		String content = EntityUtils.toString(entity);
		if (StringUtils.isNotBlank(query)) {
			final String[] results = content.split("results");
			content = results[1].substring(2, results[1].length() - 1);
		}

		try {
			responseHTML = htmlBuilder.getHTMLResponse(content);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		response.getWriter().write(responseHTML);

	}

	private void initialiseMetrics() {
		counter = metricsService.counter("responseCounter15");
		timer = metricsService.timer("responseTimer15");
		meter = metricsService.meter("responseMeter15");
	}

	/**
	 * Generate the URL
	 * 
	 * @param query
	 *            search query value
	 * @param pageNum
	 *            page number for search/list photos result
	 * @return URL
	 */
	private String extractUrl(final String query, final String pageNum) {
		String url;

		final String unsplashUrl = service.getUnsplashUrl() + "?client_id=" + service.getClientId() + "&per_page="
				+ service.getLimitPerPage() + "&page=";
		final String unsplashSearchUrl = service.getUnsplashSearchUrl() + "?client_id=" + service.getClientId()
				+ "&per_page=" + service.getLimitPerPage() + "&page=";

		if (StringUtils.isNotBlank(query)) {
			url = unsplashSearchUrl + pageNum + "&query=" + query;
		} else {
			url = unsplashUrl + pageNum;
		}
		return url;
	}
}
