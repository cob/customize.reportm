package com.cultofbits.customizations.reportm.utils

import io.github.gitbucket.markedj.Marked
import io.github.gitbucket.markedj.Options;

class MarkdownProcessor {

    private Options options

    MarkdownProcessor() {
        options = new Options();
        options.setGfm(true);
        options.setTables(true);
        options.setBreaks(true);
    }

    String toHtml(markdown) {
        // Specify options
        return Marked.marked(markdown, options);
    }
}
