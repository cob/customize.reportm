package com.cultofbits.customizations.reportm.utils

import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Template

class TemplateUtils {

    private static Handlebars handlebars

    static {
        handlebars = new Handlebars();
    }

    static String apply(String templateMsg, Map<String, Object> variables) {
        Template template = handlebars.compileInline(templateMsg);
        return template.apply(variables)
    }
}
