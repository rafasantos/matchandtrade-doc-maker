package com.github.rafasantos.matchandtrade.doc.maker.rest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.core.MediaType;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import com.github.rafasantos.matchandtrade.doc.executable.PropertiesProvider;
import com.github.rafasantos.matchandtrade.doc.maker.OutputMaker;
import com.github.rafasantos.matchandtrade.doc.util.AssertUtil;
import com.github.rafasantos.matchandtrade.doc.util.GetSnippetMaker;
import com.github.rafasantos.matchandtrade.doc.util.JsonUtil;
import com.github.rafasantos.matchandtrade.doc.util.RequestResponseHolder;
import com.github.rafasantos.matchandtrade.doc.util.TemplateUtil;
import com.github.rafasantos.matchandtrade.exception.DocMakerException;
import com.matchandtrade.rest.v1.json.TradeJson;
import com.matchandtrade.rest.v1.json.TradeMembershipJson;


public class RestTradeMembershipsMaker implements OutputMaker {
	
	public static final String TRADES_MEMBERSHIP_POST_SNIPPET = "TRADES_MEMBERSHIP_POST_SNIPPET";
	public static final String TRADES_MEMBERSHIP_GET_SNIPPET = "TRADES_MEMBERSHIP_GET_SNIPPET";
	public static final String TRADES_MEMBERSHIP_GET_ALL_SNIPPET = "TRADES_MEMBERSHIP_GET_ALL_SNIPPET";
	public static final String TRADES_MEMBERSHIP_SEARCH_SNIPPET = "TRADES_MEMBERSHIP_SEARCH_SNIPPET";
	
	public RequestResponseHolder buildPostRequestResponse(TradeMembershipJson json) {
		HttpClient httpClient = HttpClients.createDefault();
		HttpPost httpRequest = new HttpPost(PropertiesProvider.getServerUrl() + "/rest/v1/trade-memberships/");
		httpRequest.addHeader(RestUtil.getAuthenticationHeader());
		httpRequest.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
		
		StringEntity requestBody = new StringEntity(JsonUtil.toJson(json), StandardCharsets.UTF_8);
		httpRequest.setEntity(requestBody);

		HttpResponse httpResponse;
		try {
			httpResponse = httpClient.execute(httpRequest);
		} catch (IOException e) {
			throw new DocMakerException(this, e);
		}

		// Assert if status is 200
		AssertUtil.isEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
		
		return new RequestResponseHolder(httpRequest, httpResponse);
	}
	
	@Override
	public String buildDocContent() {
		String template = TemplateUtil.buildTemplate(getDocLocation());
		
		// Create a new trade
		TradeJson trade = new TradeJson();
		trade.setName("Used DVDs");
		RequestResponseHolder tradeRRH = RestTradesMaker.buildPostRequestResponse(trade);
		trade = JsonUtil.fromString(RestUtil.buildResponseBodyString(tradeRRH.getHttpResponse()), TradeJson.class);

		// Set authentication header as null to force to authenticate as a new user because the previous user is already the owner of the previous trade
		RestUtil.setAuthenticationHeader(null);
		
		// Become member of one of the created trade
		TradeMembershipJson postJson = new TradeMembershipJson();
		postJson.setUserId(RestUtil.getAuthenticatedUser().getUserId());
		postJson.setTradeId(trade.getTradeId());
		RequestResponseHolder post = buildPostRequestResponse(postJson);
		String postSnippet = TemplateUtil.buildSnippet(post.getHttpRequest(), post.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, TRADES_MEMBERSHIP_POST_SNIPPET, postSnippet);
		
		TradeMembershipJson postResponseJson = JsonUtil.fromString(RestUtil.buildResponseBodyString(post.getHttpResponse()), TradeMembershipJson.class);
		RequestResponseHolder get = GetSnippetMaker.buildGetRequestResponse("/rest/v1/trade-memberships/" + postResponseJson.getTradeMembershipId());
		String getSnippet = TemplateUtil.buildSnippet(get.getHttpRequest(), get.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, TRADES_MEMBERSHIP_GET_SNIPPET, getSnippet);

		RequestResponseHolder getAll = GetSnippetMaker.buildGetRequestResponse("/rest/v1/trade-memberships/");
		String getAllSnippet = TemplateUtil.buildSnippet(getAll.getHttpRequest(), getAll.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, TRADES_MEMBERSHIP_GET_ALL_SNIPPET, getAllSnippet);

		RequestResponseHolder search = GetSnippetMaker.buildGetRequestResponse(
			"/rest/v1/trade-memberships?userId="
			+ RestUtil.getAuthenticatedUser().getUserId()
			+ "&tradeId="+postJson.getTradeId()+"&_pageNumber=1&_pageSize=10");
		String searchSnippet = TemplateUtil.buildSnippet(search.getHttpRequest(), search.getHttpResponse());
		template = TemplateUtil.replacePlaceholder(template, TRADES_MEMBERSHIP_SEARCH_SNIPPET, searchSnippet);
		
		return template;
	}

	@Override
	public String getDocLocation() {
		return "doc/rest/trade-memberships.md";
	}

}