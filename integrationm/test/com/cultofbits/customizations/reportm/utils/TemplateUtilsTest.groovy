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

}
