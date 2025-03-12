package com.guudint.clickargo.clictruck.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.StreamReaderDelegate;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

import com.dsv.edi.xml.DSV_ShipmentMessage_v1.DSVShipmentMessage;
import com.guudint.clickargo.clictruck.dsv.service.impl.DsvServiceImpl;

public class CkXmlUtil {

	private static Logger log = Logger.getLogger(DsvServiceImpl.class);

	public static boolean validateXMLSchema(String xsdPath, String xmlPath) throws IOException, SAXException {

		return validateXMLSchema(new File(xsdPath), new File(xmlPath));
	}

	/**
	 * 
	 * @param xsdFile
	 * @param xmlFile
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 */
	public static boolean validateXMLSchema(File xsdFile, File xmlFile) throws IOException, SAXException {
		try {
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

			Schema schema = factory.newSchema(xsdFile);

			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(xmlFile));

		} catch (IOException e) {
			log.error("Exception: ", e);
			throw e;
		} catch (SAXException e1) {
			log.error("Exception: ", e1);
			throw e1;
		}

		return true;

	}

	/**
	 * 
	 * @param xmlFile
	 * @return
	 * @throws IOException
	 * @throws JAXBException
	 * @throws XMLStreamException
	 */
	public Optional<DSVShipmentMessage> unmarshal(File xmlFile) throws IOException, JAXBException, XMLStreamException {

		try (InputStream is = new FileInputStream(xmlFile)) {

			JAXBContext jaxbContext = JAXBContext.newInstance(DSVShipmentMessage.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			XMLStreamReader xsr = XMLInputFactory.newFactory().createXMLStreamReader(is);
			XMLReaderWithoutNamespace xr = new XMLReaderWithoutNamespace(xsr);

			DSVShipmentMessage msg = (DSVShipmentMessage) jaxbUnmarshaller.unmarshal(xr);

			return Optional.ofNullable(msg);
		} catch (IOException | JAXBException | XMLStreamException e) {
			log.error("", e);
			throw e;
		}
	}

	/**
	 * Ignore NameSpace
	 * 
	 * @author zhangji
	 *
	 */
	public static class XMLReaderWithoutNamespace extends StreamReaderDelegate {
		public XMLReaderWithoutNamespace(XMLStreamReader reader) {
			super(reader);
		}

		@Override
		public String getAttributeNamespace(int arg0) {
			return "";
		}

		@Override
		public String getNamespaceURI() {
			return "";
		}
	}

	///////////// ///////////////////
	public Map<String, String> xmlElements2Map(File xmlFile) throws Exception {
		SAXReader sax = new SAXReader();
		Document document = sax.read(xmlFile);
		Element root = document.getRootElement();
		return this.getNodes(root, "");
	}

	/**
	 * recursive function
	 * 
	 * @param node
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, String> getNodes(Element node, String superKey) {

		Map<String, String> map = new HashMap<>();
		
		// compute key
		String key = StringUtils.isBlank(superKey) ? "" : (superKey + "_");
		key = key + node.getName() + this.getAttributeStr(node);

		// current node
		if (StringUtils.isNoneBlank(node.getTextTrim())) {
			map.put(key, node.getTextTrim());
		}
		// sub node
		List<Element> listElement = node.elements();//
		for (Element element : listElement) {//
			map.putAll(this.getNodes(element, key));// recursive
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	private String getAttributeStr(Element node) {

		List<Attribute> listAttr = node.attributes();
		if (listAttr == null || listAttr.size() == 0) {
			return "";
		}

		String attrs = "";
		for (Attribute attr : listAttr) {
			attrs += String.format("_%s$%s$", attr.getName(), attr.getValue());
		}
		return attrs;
	}
	///////////// ///////////////////

	public static void main(String[] argv) throws Exception {

		String xsdFile = "/Users/zhangji/git/GUUDID_CKI2_ClicTruck/clictruck/src/main/resources/xsd/dsv/DSV_ShipmentMessage_v1.xsd";
		String xmlFile = "/Users/zhangji/home/vcc/thirdParty/dsv/20231115/DSV_GUUDEDIID_SHIPMENT_20220604_1155_4872276774.xml";
		// validateXMLSchema(xsdFile, xmlFile);
		File file = new File(xmlFile);
		Map<?,?> map = new CkXmlUtil().xmlElements2Map(file);
		log.info(map);
	}
}
