package com.github.rafasantos.matchandtrade.doc.executable;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.matchandtrade.WebserviceApplication;


/**
 * If no arguments are provided, then it will attribute the following values:
 * <code>
 * -cf src/config/matchandtrade.properties --destination-folder target/doc-maker
 * </code>
 */
public class DocMakerExecutable {
	
	private static final Logger logger = LoggerFactory.getLogger(DocMakerExecutable.class);

	public static void main(String[] arguments) {
		// TODO Better arguments handling
		if (arguments.length == 0) {
			arguments = new String[4];
			arguments[0] = "-cf";
			arguments[1] = "src/config/matchandtrade.properties";
			arguments[2] = "--destination-folder";
			arguments[3] = "target/doc-maker";
		}
		logger.info("Starting Match and Trade web server.");
		String destinationFolder = ArgumentBuilder.obtainDestinationFolder(arguments);
		String[] webArgumentsArray = ArgumentBuilder.buildWebServerArguments(arguments);
		try {
			startMatchAndTradeWebServer(webArgumentsArray);
		} catch (IOException e) {
			logger.error("Not able to start Match And Trade web server. Exception message: {}", e.getMessage());
		}
		
		DocContentMaker docContentMaker = new DocContentMaker(destinationFolder);
		try {
			logger.info("Making documentation.");
			docContentMaker.makeContent();
			logger.info("Document generation complete.");
			logger.info(docContentMaker.getReport());		
			System.exit(0);
		} catch (Exception e) {
			logger.error("Error when making the documentation. Exception message: {}", e.getMessage(), e);
			logger.info(docContentMaker.getReport());
			System.exit(-1);
		} finally {
			System.exit(0);
		}
	}
	
//	public void execute(String destinationFolder) throws IOException {
//		// TODO Scan all files instead of instantiate one by one manually
//		List<OutputMaker> docMakers = new ArrayList<OutputMaker>();
//		docMakers.add(new ReadmeMaker());
//		docMakers.add(new DevelopmentGuide());
//		docMakers.add(new RestGuideMaker());
//		docMakers.add(new RestAuthenticateMaker());
//		docMakers.add(new RestAuthenticationsMaker());
//		docMakers.add(new RestTradesMaker());
//		docMakers.add(new RestUsersMaker());
//
//		HtmlParser htmlParser = new HtmlParser();
//		for(OutputMaker t : docMakers) {
//			// Generate Markdown documents
//			String docContent = t.buildDocContent();
//			String docLocation = t.getDocLocation();
//			File docFile = new File(destinationFolder + File.separator + docLocation);
//			FileUtils.write(docFile, docContent, StandardCharsets.UTF_8);
//			// Also generate HTML files derived from MarkDown files for easier visualization
//			File htmlFile = HtmlParser.optainHtmlFile(docFile);
//			String docContentAsHtml = htmlParser.parseMarkDownToHTML(docContent);
//			String styleHead = HtmlParser.getGitHubStyleHead();
//			String styleTail = HtmlParser.getGitHubStyleTail();
//			String htmlContetn = styleHead + docContentAsHtml + styleTail;
//			FileUtils.write(htmlFile, htmlContetn, StandardCharsets.UTF_8);
//		}
//	}

	private static void startMatchAndTradeWebServer(String[] arguments) throws IOException {
		PropertiesProvider.buildAppProperties(arguments);
		WebserviceApplication.main(arguments);
	}

}
