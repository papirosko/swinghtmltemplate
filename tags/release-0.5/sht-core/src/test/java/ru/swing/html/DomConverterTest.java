package ru.swing.html;

import junit.framework.TestCase;
import ru.swing.html.css.CssBlock;
import ru.swing.html.css.SelectorGroup;
import ru.swing.html.tags.Tag;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * User: Penkov Vladimir
 * Date: 22.11.2010
 * Time: 13:21:41
 * </pre>
 */
public class DomConverterTest extends TestCase {

    public void testParseHead() throws Exception {

        DomModel model = new DomModel();
        Tag head = new Tag();
        Tag style = new Tag();
        style.setName("style");
        style.setAttribute(Tag.TAG_CONTENT,
                ".foo { " +
                        "   name1: val1" +
                        "}" +
                        "" +
                        "p.red {" +
                        "  color: red;" +
                        "  font-weight: bold;" +
                        "}");
        head.addChild(style);
        DomConverter.parseHead(model, head);
        assertEquals(2, model.getGlobalStyles().size());

        CssBlock block = model.getGlobalStyles().get(0);
        Tag tag = new Tag();
        tag.setAttribute("class", "foo");
        assertTrue(block.matches(tag));
        assertEquals(1, block.getStyles().size());

    }

    public void testLocalAttributesOverrideGlobal() throws Exception {

        String html = "<html>" +
                "<head>" +
                "<style>" +
                "p {" +
                "   type: text;" +
                "}" +
                "</style>" +
                "</head>" +
                "<body style='display: border;'>" +
                "   <p type='html'>center</p>" +
                "   <p align='top'>top</p>" +
                "   <p align='bottom'>bottom</p>" +
                "   <p align='left'>left</p>" +
                "   <p align='right'>right</p>" +
                "</body>" +
                "</html>";

        DomModel model = DomLoader.loadModel(new ByteArrayInputStream(html.getBytes()));
        DomConverter.toSwing(model);
        Tag body = model.getRootTag().getChildByName("body");

        Tag p1 = body.getChildren().get(0);
        assertEquals("html", p1.getAttribute("type"));

        Tag p2 = body.getChildren().get(1);
        assertEquals("text", p2.getAttribute("type"));

    }

    public void testSubstitutions() throws Exception {

        JLabel rootLabel = new JLabel("Foo");

        String html = "<html>" +
                "<head>" +
                "<style>" +
                "p {" +
                "   type: text;" +
                "}" +
                "</style>" +
                "</head>" +
                "<body style='display: border;'>" +
                "   <p type='html' id='rootLabel'>center</p>" +
                "   <p align='top'>top</p>" +
                "   <p align='bottom'>bottom</p>" +
                "   <p align='left'>left</p>" +
                "   <p align='right'>right</p>" +
                "</body>" +
                "</html>";

        Map<SelectorGroup, JComponent> substitutions = new HashMap<SelectorGroup, JComponent>();
        substitutions.put(new SelectorGroup("#rootLabel"), rootLabel);

        DomModel model = DomLoader.loadModel(new ByteArrayInputStream(html.getBytes()));
        DomConverter.toSwing(model, substitutions);
        Tag body = model.getRootTag().getChildByName("body");

        JPanel rootPanel = (JPanel) body.getComponent();
        BorderLayout l = (BorderLayout) rootPanel.getLayout();
        JLabel centerLabel = (JLabel) l.getLayoutComponent(BorderLayout.CENTER);

        assertEquals(centerLabel, rootLabel);

    }
}
