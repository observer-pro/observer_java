package pro.sky.observer_java.mapper;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class MarkdownAndHtml {
    public static String mdToHtml(String md) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(md);
        HtmlRenderer htmlRenderer = HtmlRenderer.builder().build();

        return htmlRenderer.render(document);
    }
}
