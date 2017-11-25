package com.matchandtrade.doc.maker.rest;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import com.github.rafasantos.restdocmaker.RestDocMaker;
import com.github.rafasantos.restdocmaker.template.Snippet;
import com.github.rafasantos.restdocmaker.template.SnippetFactory;
import com.github.rafasantos.restdocmaker.template.TemplateUtil;
import com.matchandtrade.doc.util.MatchAndTradeRestUtil;

public class AuthenticateRestDocMaker implements RestDocMaker {

	private static final String AUTHENTICATE_SNIPPET = "AUTHENTICATE_SNIPPET";
	private static final String SIGN_OUT_SNIPPET = "SIGN_OUT_SNIPPET";

	@Override
	public String contentFilePath() {
		return "authenticate.html";
	}

	@Override
	public String content() {
		String template = TemplateUtil.buildTemplate(contentFilePath());
		SnippetFactory snippetFactory = new SnippetFactory();
		
		// AUTHENTICATE_SNIPPET
		Snippet authenticateSnippet = snippetFactory.makeSnippet(MatchAndTradeRestUtil.authenticateUrl());
		template = TemplateUtil.replacePlaceholder(template, AUTHENTICATE_SNIPPET, authenticateSnippet.asHtml());
		// Asserts that statusCode = 200 and header "Authorization" exists
		authenticateSnippet.getResponse().then().statusCode(200).and().header("Authorization", notNullValue());

		// SIGN_OUT_SNIPPET
		Snippet signOffSnippet = snippetFactory.makeSnippet(MatchAndTradeRestUtil.signOffUrl());
		template = TemplateUtil.replacePlaceholder(template, SIGN_OUT_SNIPPET, signOffSnippet.asHtml());
		// Asserts that statusCode = 205 and header "Authorization" does not exists
		signOffSnippet.getResponse().then().statusCode(205).and().header("Authorization", nullValue());

		return TemplateUtil.appendHeaderAndFooter(template);
	}

}