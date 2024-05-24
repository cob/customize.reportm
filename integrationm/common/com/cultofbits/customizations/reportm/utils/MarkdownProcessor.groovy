package com.cultofbits.customizations.reportm.utils

import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.ext.typographic.TypographicExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.parser.ParserEmulationProfile
import com.vladsch.flexmark.util.ast.Document
import com.vladsch.flexmark.util.data.MutableDataSet

class MarkdownProcessor {

    private MutableDataSet options;
    private Parser parser;
    private HtmlRenderer renderer;


    MarkdownProcessor() {
        options = new MutableDataSet()
        options.setFrom(ParserEmulationProfile.GITHUB)
        options.set(Parser.EXTENSIONS, Arrays.asList(
                TablesExtension.create(),
                TypographicExtension.create()
        ));

        options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");

        parser = Parser.builder(options).build();
        renderer = HtmlRenderer.builder(options).build();
    }

    String toHtml(String markdown) {

        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        // You can re-use parser and renderer instances
        Document document = parser.parse(markdown);
        return renderer.render(document);
    }
}
