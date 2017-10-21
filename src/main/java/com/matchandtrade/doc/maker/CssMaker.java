package com.matchandtrade.doc.maker;

import com.matchandtrade.doc.util.TemplateUtil;

public class CssMaker extends OutputMaker {

	@Override
	public String buildDocContent() {
		return TemplateUtil.buildTemplate(getDocLocation());
	}

	@Override
	public String getDocLocation() {
		return "css/matchandtrade-doc-style.css";
	}
	
	@Override
	public boolean requiresHeaderAndFooter() {
		return false;
	}
}
