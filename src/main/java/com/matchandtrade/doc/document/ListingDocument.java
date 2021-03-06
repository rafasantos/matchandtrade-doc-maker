package com.matchandtrade.doc.document;

import com.github.rafasantos.restapidoc.SpecificationParser;
import com.matchandtrade.doc.clientapi.MatchAndTradeClient;
import com.matchandtrade.doc.util.TemplateUtil;
import com.matchandtrade.rest.v1.json.ArticleJson;
import com.matchandtrade.rest.v1.json.ListingJson;
import com.matchandtrade.rest.v1.json.MembershipJson;
import com.matchandtrade.rest.v1.json.TradeJson;

public class ListingDocument implements Document {
	
	private static final String LISTING_POST_PLACEHOLDER = "LISTING_POST_PLACEHOLDER";
	private static final String LISTING_DELETE_PLACEHOLDER = "LISTING_DELETE_PLACEHOLDER";

	@Override
	public String contentFilePath() {
		return "listing.html";
	}

	private final MatchAndTradeClient clientApi;
	private String template;

	public ListingDocument() {
		clientApi = new MatchAndTradeClient();
		template = TemplateUtil.buildTemplate(contentFilePath());
	}

	@Override
	public String content() {
		TradeJson trade = createTrade();
		MembershipJson membership = clientApi.findMembershipByUserIdAndTradeIdAsMembership(clientApi.getUserId(), trade.getTradeId());
		ArticleJson article = createArticle();

		// LISTING_POST_PLACEHOLDER
		ListingJson listing = buildListing(membership, article);
		SpecificationParser listingPostParser = clientApi.create(listing);
		template = TemplateUtil.replacePlaceholder(template, LISTING_POST_PLACEHOLDER, listingPostParser.asHtmlSnippet());

		// LISTING_DELETE_PLACEHOLDER
		SpecificationParser listingDeleteParser = clientApi.deleteListing(listing);
		template = TemplateUtil.replacePlaceholder(template, LISTING_DELETE_PLACEHOLDER, listingDeleteParser.asHtmlSnippet());

		return TemplateUtil.appendHeaderAndFooter(template);
	}

	private ArticleJson createArticle() {
		ArticleJson article = new ArticleJson();
		article.setName("Love Letter");
		article.setDescription("First edition in great condition");
		SpecificationParser parser = clientApi.create(article);
		return parser.getResponse().as(ArticleJson.class);
	}

	private ListingJson buildListing(MembershipJson membership, ArticleJson article) {
		ListingJson listing = new ListingJson();
		listing.setMembershipId(membership.getMembershipId());
		listing.setArticleId(article.getArticleId());
		return listing;
	}

	private TradeJson createTrade() {
		TradeJson trade = new TradeJson();
		trade.setName("Books in Buffalo - " + System.currentTimeMillis());
		SpecificationParser tradeParser = clientApi.create(trade);
		return tradeParser.getResponse().as(TradeJson.class);
	}

}
