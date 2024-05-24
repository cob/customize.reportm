package com.cultofbits.customizations.reportm.utils

import spock.lang.Specification

class MarkdownProcessorTest extends Specification {

    def "can generate html from markdown"() {

        given:

        String md = "### header 3"

        when:
        def result = new MarkdownProcessor().toHtml(md)

        then:
        result == "<h3>header 3</h3>\n"
    }

    def "can generate tables from markdown"() {

        given:

        String md = """
            | First Header  | 
            | ------------- | 
            | Content Cell  | 
        """.stripMargin()

        when:
        def result = new MarkdownProcessor().toHtml(md).replaceAll("\n", "")

        then:
        result == """
            <table>
            <thead>
            <tr><th>First Header</th></tr>
            </thead>
            <tbody>
            <tr><td>Content Cell</td></tr>
            </tbody>
            </table>""".stripMargin().replaceAll("\\s+<", "<")
    }

}
