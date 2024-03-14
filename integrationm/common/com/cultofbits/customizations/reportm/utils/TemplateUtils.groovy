package com.cultofbits.customizations.reportm.utils

import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Helper
import com.github.jknack.handlebars.Options
import com.github.jknack.handlebars.Template

class TemplateUtils {

    private static Handlebars handlebars

    static {
        handlebars = new Handlebars();

        handlebars.registerHelper("emails", new Helper<List>() {
            @Override
            CharSequence apply(List list, Options options) throws IOException {
                return (list != null ? list.join(";") + ";" : "")
            }
        })

        handlebars.registerHelper("join", new Helper<List>() {
            @Override
            CharSequence apply(List list, Options options) throws IOException {
                return list != null ? list.join(options.params.length > 0 ? options.param(0) : "," as String) : ""
            }
        })

        handlebars.registerHelper("iterate", new Helper<String>() {
            @Override
            CharSequence apply(String text, Options options) throws IOException {
                def values = text.split(options.params.length > 0 ? options.param(0) : "," as String)

                StringBuffer ret = new StringBuffer("");
                for (String value : values) {
                    ret.append(options.fn(value))
                }

                return new Handlebars.SafeString(ret);
            }
        })

        handlebars.registerHelper("isEqual", new Helper<Object>() {
            @Override
            CharSequence apply(Object value, Options options) throws IOException {
                return Objects.equals(value, options.param(0)) ? options.fn(value) : ""
            }
        })

        handlebars.registerHelper("isNotEqual", new Helper<Object>() {
            @Override
            CharSequence apply(Object value, Options options) throws IOException {
                return !Objects.equals(value, options.param(0)) ? options.fn(value) : ""
            }
        })


    }

    static String apply(String templateMsg, Map<String, Object> variables) {
        Template template = handlebars.compileInline(templateMsg);
        return template.apply(variables)
    }
}
