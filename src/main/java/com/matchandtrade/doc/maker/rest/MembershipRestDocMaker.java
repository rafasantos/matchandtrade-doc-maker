package com.matchandtrade.doc.maker.rest;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Date;

import com.github.rafasantos.restdocmaker.RestDocMaker;
import com.github.rafasantos.restdocmaker.template.Snippet;
import com.github.rafasantos.restdocmaker.template.SnippetFactory;
import com.github.rafasantos.restdocmaker.template.TemplateUtil;
import com.github.rafasantos.restdocmaker.util.JsonUtil;
import com.matchandtrade.doc.util.MatchAndTradeApiFacade;
import com.matchandtrade.doc.util.MatchAndTradeRestUtil;
import com.matchandtrade.doc.util.PaginationTemplateUtil;
import com.matchandtrade.rest.v1.json.TradeJson;
import com.matchandtrade.rest.v1.json.MembershipJson;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.specification.RequestSpecification;


public class MembershipRestDocMaker implements RestDocMaker {
	
	private static final String TRADES_MEMBERSHIP_POST_PLACEHOLDER = "TRADES_MEMBERSHIP_POST_PLACEHOLDER";
	private static final String TRADES_MEMBERSHIP_GET_PLACEHOLDER = "TRADES_MEMBERSHIP_GET_PLACEHOLDER";
	private static final String TRADES_MEMBERSHIP_GET_ALL_PLACEHOLDER = "TRADES_MEMBERSHIP_GET_ALL_PLACEHOLDER";
	private static final String TRADES_MEMBERSHIP_SEARCH_PLACEHOLDER = "TRADES_MEMBERSHIP_SEARCH_PLACEHOLDER";
	private static final String TRADES_MEMBERSHIP_DELETE_PLACEHOLDER = "TRADES_MEMBERSHIP_DELETE_PLACEHOLDER";

	@Override
	public String contentFilePath() {
		return "trade-memberships.html";
	}

	@Override
	public String content() {
		String template = TemplateUtil.buildTemplate(contentFilePath());
		MatchAndTradeApiFacade apiFacade = new MatchAndTradeApiFacade();
		SnippetFactory snippetFactory = new SnippetFactory(ContentType.JSON, MatchAndTradeRestUtil.nextAuthorizationHeader());

		// TRADES_MEMBERSHIP_POST_PLACEHOLDER
		TradeJson tradeJson = apiFacade.createTrade("Board games in Vancouver - " + new Date().getTime() + hashCode());
		MembershipJson membership = new MembershipJson();
		membership.setTradeId(tradeJson.getTradeId());
		membership.setUserId(MatchAndTradeRestUtil.getLastAuthenticatedUserId());
		
		Snippet postSnippet = snippetFactory.makeSnippet(Method.POST, membership, MatchAndTradeRestUtil.membershipsUrl() + "/");
		postSnippet.getResponse().then().statusCode(201).and().body("membershipId", notNullValue());
		template = TemplateUtil.replacePlaceholder(template, TRADES_MEMBERSHIP_POST_PLACEHOLDER, postSnippet.asHtml());
		membership = JsonUtil.fromResponse(postSnippet.getResponse(), MembershipJson.class);

		// TRADES_MEMBERSHIP_GET_PLACEHOLDER
		Snippet getSnippet = snippetFactory.makeSnippet(MatchAndTradeRestUtil.membershipsUrl(membership.getMembershipId()));
		getSnippet.getResponse().then().statusCode(200).and().body("membershipId", equalTo(membership.getMembershipId()));
		template = TemplateUtil.replacePlaceholder(template, TRADES_MEMBERSHIP_GET_PLACEHOLDER, getSnippet.asHtml());

		// TRADES_MEMBERSHIP_GET_ALL_PLACEHOLDER
		Snippet getAllSnippet = snippetFactory.makeSnippet(MatchAndTradeRestUtil.membershipsUrl());
		getAllSnippet.getResponse().then().statusCode(200).and().header("X-Pagination-Total-Count", notNullValue());
		template = TemplateUtil.replacePlaceholder(template, TRADES_MEMBERSHIP_GET_ALL_PLACEHOLDER, getAllSnippet.asHtml());
		
		// TRADES_MEMBERSHIP_SEARCH_PLACEHOLDER
		RequestSpecification searchRequest = new RequestSpecBuilder()
				.addHeaders(MatchAndTradeRestUtil.getLastAuthorizationHeaderAsMap())
				.addParam("userId", MatchAndTradeRestUtil.getLastAuthenticatedUserId())
				.addParam("_pageNumber", "1")
				.addParam("_pageSize", "3")
				.build();
		Snippet searchSnippet = SnippetFactory.makeSnippet(Method.GET, searchRequest, MatchAndTradeRestUtil.membershipsUrl()); 
		searchSnippet.getResponse().then().statusCode(200).and().headers("X-Pagination-Total-Count", notNullValue());
		template = TemplateUtil.replacePlaceholder(template, TRADES_MEMBERSHIP_SEARCH_PLACEHOLDER, searchSnippet.asHtml());

		// TRADES_MEMBERSHIP_DELETE_PLACEHOLDER
		Snippet deleteSnippet = snippetFactory.makeSnippet(Method.DELETE, MatchAndTradeRestUtil.membershipsUrl(membership.getMembershipId()));
		deleteSnippet.getResponse().then().statusCode(204);
		template = TemplateUtil.replacePlaceholder(template, TRADES_MEMBERSHIP_DELETE_PLACEHOLDER, deleteSnippet.asHtml());

		template = PaginationTemplateUtil.replacePaginationRows(template);
		return TemplateUtil.appendHeaderAndFooter(template);
	}

}