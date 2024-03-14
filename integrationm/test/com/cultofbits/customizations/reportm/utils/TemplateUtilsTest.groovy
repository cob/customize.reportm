package com.cultofbits.customizations.reportm.utils

import spock.lang.Specification

class TemplateUtilsTest extends Specification {

    def "apply variables to template"() {

        given: "template and variables"

        def template = "<ul>{{#list}}<li>{{.}}{{/list}}</ul>"
        def variables = ["list": [1, 2, 3]]

        when:
        def result = TemplateUtils.apply(template, variables)

        then:
        result == "<ul><li>1<li>2<li>3</ul>"
    }

    def "emails"() {

        given: "template and variables"

        def template = "{{emails list}}"
        def variables = ["list": [1, 2, 3]]

        when:
        def result = TemplateUtils.apply(template, variables)

        then:
        result == "1;2;3;"
    }

    def "join values"() {

        given: "template and variables"

        def template = "{{join list}}"
        def variables = ["list": [1, 2, 3]]

        when:
        def result = TemplateUtils.apply(template, variables)

        then:
        result == "1,2,3"
    }

    def "join values with custom separator"() {

        given: "template and variables"

        def template = "{{join list '/'}}"
        def variables = ["list": [1, 2, 3]]

        when:
        def result = TemplateUtils.apply(template, variables)

        then:
        result == "1/2/3"
    }

    def "iterate"() {
        given: "template and variables"

        def template = "<ul>{{#iterate items ','}}<li>{{.}}</li>{{/iterate}}</ul>"
        def variables = ["items": "1,2,3"]

        when:
        def result = TemplateUtils.apply(template, variables)

        then:
        result == "<ul><li>1</li><li>2</li><li>3</li></ul>"
    }

    def "isEqual"() {
        given: "template and variables"

        def template = "<ul>{{#isEqual item \"abc\"}}equal{{/isEqual}}</ul>"
        def variables = ["item": "abc"]

        when:
        def result = TemplateUtils.apply(template, variables)

        then:
        result == "<ul>equal</ul>"
    }

}
