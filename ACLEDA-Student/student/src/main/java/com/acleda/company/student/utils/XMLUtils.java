package com.acleda.company.student.utils;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;

public class XMLUtils {
    public static String convertNodeToString(Node node) {
        try {
            StringWriter writer = new StringWriter();

            Transformer trans = TransformerFactory.newInstance().newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
            trans.transform(new DOMSource(node), new StreamResult(writer));

            return writer.toString();
        } catch (TransformerException te) {
            te.printStackTrace();
        }

        return "";
    }

    public static String nodeListToString(NodeList nodes) throws TransformerException {
        StringBuilder result = new StringBuilder();
        int len = nodes.getLength();
        for (int i = 0; i < len; ++i) {
            result.append(nodeToString(nodes.item(i)));
        }
        return result.toString();
    }

    public static String nodeToString(Node node) throws TransformerException {
        StringWriter buf = new StringWriter();
        Transformer xform = TransformerFactory.newInstance().newTransformer();
        xform.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        xform.transform(new DOMSource(node), new StreamResult(buf));
        return (buf.toString());
    }


    public static <T> T convertXMLStr(String strXML, T clazz) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(clazz.getClass());
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        StringReader strReader = new StringReader(strXML);
        return (T) jaxbUnmarshaller.unmarshal(strReader);
    }
}
