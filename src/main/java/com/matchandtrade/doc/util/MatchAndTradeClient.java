package com.matchandtrade.doc.util;

import com.github.rafasantos.restapidoc.SpecificationFilter;
import com.github.rafasantos.restapidoc.SpecificationParser;
import com.matchandtrade.rest.v1.json.MembershipJson;
import com.matchandtrade.rest.v1.json.TradeJson;
import com.matchandtrade.rest.v1.json.UserJson;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static org.hamcrest.Matchers.*;

public class MatchAndTradeClient {

	private final Header authorizationHeader;
	private Integer userId;
//	private final String cookie;

	public MatchAndTradeClient() {
		SpecificationParser parser = authenticate();
		String authorizationHeaderName = "Authorization";
		String authorizationHeaderValue = parser.getResponse().getHeader(authorizationHeaderName);
//		this.cookie = parser.getResponse().getCookie("MTSESSION");
		this.authorizationHeader = new Header(authorizationHeaderName, authorizationHeaderValue);

		SpecificationParser authenticationsParser = findAuthentications();
		userId = authenticationsParser.getResponse().body().path("userId");
	}

	public MatchAndTradeClient(Header authorizationHeader) {
		this.authorizationHeader = authorizationHeader;
		SpecificationParser authenticationsParser = findAuthentications();
		userId = authenticationsParser.getResponse().body().path("userId");
	}

	public static SpecificationParser authenticate() {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
				.filter(filter)
				.get(MatchAndTradeRestUtil.authenticateUrl());
		// Assert status is redirect
		parser.getResponse().then().statusCode(302);
		return parser;
	}

	public SpecificationParser create(TradeJson trade) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured
			.given()
			.filter(filter)
			.header(getAuthorizationHeader())
			.contentType(ContentType.JSON)
			.body(trade)
			.when()
			.post(MatchAndTradeRestUtil.tradesUrl() + "/");
		return parser;
	}

	public SpecificationParser create(MembershipJson membership) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
				.filter(filter)
				.header(authorizationHeader)
				.contentType(ContentType.JSON)
				.body(membership)
				.post(MatchAndTradeRestUtil.membershipsUrl() + "/");
		parser.getResponse().then().statusCode(201);
		return parser;
	}

	/**
	 * Need to keep the same cookie between "authenticate" and "authenticate info"
	 *
	 * @param cookie
	 * @return
	 */
	public SpecificationParser findAuthenticationInfo(String cookie) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
				.header("cookie", cookie)
				.filter(filter)
				.get(MatchAndTradeRestUtil.authenticateInfoUrl());
		parser.getResponse().then().statusCode(200);
		return parser;
	}

	public SpecificationParser findAuthentications() {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
			.filter(filter)
			.header(getAuthorizationHeader())
			.get(MatchAndTradeRestUtil.authenticationsUrl() + "/");
		parser.getResponse().then().statusCode(200).and().body("", hasKey("userId"));
		return parser;
	}

	public SpecificationParser findMembership(Integer membershipId) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
				.filter(filter)
				.header(MatchAndTradeRestUtil.getLastAuthorizationHeader())
				.get(MatchAndTradeRestUtil.membershipsUrl(membershipId));
		parser.getResponse().then().statusCode(200);
		return parser;
	}

	public SpecificationParser findMemberships(Integer pageNumber, Integer pageSize) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
				.filter(filter)
				.header(authorizationHeader)
				.contentType(ContentType.JSON)
				.param("_pageNumber", pageNumber)
				.param("_pageSize", pageSize)
				.get(MatchAndTradeRestUtil.membershipsUrl());
		parser.getResponse().then().statusCode(200);
		return parser;
	}

	public SpecificationParser findMembershipByUserIdOrTradeId(Integer userId, Integer tradeId) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RequestSpecification request = RestAssured.given()
				.filter(filter)
				.header(authorizationHeader)
				.contentType(ContentType.JSON);
		if (userId != null) {
			request.queryParam("userId", userId);
		}
		if (userId != null) {
			request.queryParam("tradeId", tradeId);
		}
		request.get(MatchAndTradeRestUtil.membershipsUrl());

		parser.getResponse().then().statusCode(200);
		return parser;
	}

	public SpecificationParser findTrade(Integer tradeId) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
				.filter(filter)
				.header(authorizationHeader)
				.get(MatchAndTradeRestUtil.tradesUrl(tradeId));
		parser.getResponse().then().statusCode(200).and().body("tradeId", equalTo(tradeId));
		return parser;
	}

	public SpecificationParser deleteMembership(Integer membershipId) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
				.filter(filter)
				.header(authorizationHeader)
				.delete(MatchAndTradeRestUtil.membershipsUrl(membershipId));
		parser.getResponse().then().statusCode(204);
		return parser;
	}

	public SpecificationParser deleteTrade(Integer tradeId) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
				.filter(filter)
				.header(authorizationHeader)
				.delete(MatchAndTradeRestUtil.tradesUrl(tradeId));
		parser.getResponse().then().statusCode(204);
		return parser;
	}

	public static SpecificationParser findTrades(Integer pageNumber, Integer pageSize) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		Response response = RestAssured.given()
				.filter(filter)
				.contentType(ContentType.JSON)
				.param("_pageNumber", pageNumber)
				.param("_pageSize", pageSize)
				.get(MatchAndTradeRestUtil.tradesUrl());
		response.then().statusCode(200).and().body("[0].tradeId", notNullValue());
		return parser;
	}

	public static SpecificationParser findTrades() {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		Response response = RestAssured.given()
				.filter(filter)
				.contentType(ContentType.JSON)
				.get(MatchAndTradeRestUtil.tradesUrl());
		response.then().statusCode(200);
		return parser;
	}

	public SpecificationParser findUser() {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured
			.given()
			.filter(filter)
			.header(authorizationHeader)
			.get(MatchAndTradeRestUtil.usersUrl(userId));
		return parser;
	}

	public Header getAuthorizationHeader() {
		return authorizationHeader;
	}

	public Integer getUserId() {
		return userId;
	}

	public SpecificationParser update(TradeJson trade) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured
				.given()
				.filter(filter)
				.header(getAuthorizationHeader())
				.contentType(ContentType.JSON)
				.body(trade)
				.put(MatchAndTradeRestUtil.tradesUrl(trade.getTradeId()));
		parser.getResponse().then().statusCode(200);
		return parser;
	}

	public SpecificationParser updateUser(UserJson user) {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
				.filter(filter)
				.header(MatchAndTradeRestUtil.getLastAuthorizationHeader())
				.contentType(ContentType.JSON)
				.body(user)
				.put(MatchAndTradeRestUtil.usersUrl(userId));
		parser.getResponse().then().statusCode(200).and().body("name", equalTo(user.getName()));
		return parser;
	}

	public SpecificationParser singOff() {
		SpecificationFilter filter = new SpecificationFilter();
		SpecificationParser parser = new SpecificationParser(filter);
		RestAssured.given()
				.filter(filter)
				.header(MatchAndTradeRestUtil.getLastAuthorizationHeader())
				.get(MatchAndTradeRestUtil.signOffUrl());
		parser.getResponse().then().statusCode(205).header("Authorization", nullValue());
		return parser;
	}

}