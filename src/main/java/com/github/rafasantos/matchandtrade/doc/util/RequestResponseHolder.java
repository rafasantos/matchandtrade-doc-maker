package com.github.rafasantos.matchandtrade.doc.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

public class RequestResponseHolder {
	private HttpGet httpRequest;
	private HttpResponse httpResponse;

	public RequestResponseHolder(HttpGet httpRequest, HttpResponse httpResponse) {
		this.httpRequest = httpRequest;
		this.httpResponse = httpResponse;
	}

	public HttpGet getHttpRequest() {
		return httpRequest;
	}

	public void setHttpRequest(HttpGet httpRequest) {
		this.httpRequest = httpRequest;
	}

	public HttpResponse getHttpResponse() {
		return httpResponse;
	}

	public void setHttpResponse(HttpResponse httpResponse) {
		this.httpResponse = httpResponse;
	}
	
}