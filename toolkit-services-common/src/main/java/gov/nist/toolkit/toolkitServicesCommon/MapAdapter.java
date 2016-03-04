package gov.nist.toolkit.toolkitServicesCommon;

import gov.nist.toolkit.toolkitServicesCommon.resource.PatientErrorListResource;
import gov.nist.toolkit.toolkitServicesCommon.resource.PatientErrorResource;
import org.w3c.dom.*;
import org.w3c.dom.Document;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class MapAdapter extends XmlAdapter<Element, Map<String, PatientErrorListResource>> {

    private DocumentBuilder documentBuilder;

    public MapAdapter() throws Exception {
        documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    }

    @Override
    public Element marshal(Map<String, PatientErrorListResource> map) throws Exception {
        Document document = documentBuilder.newDocument();

        Element mapElement = document.createElement("map");
        document.appendChild(mapElement);

        for(Map.Entry<String,PatientErrorListResource> entry : map.entrySet()) {
            Element transactionElement = document.createElement("transaction");
            mapElement.appendChild(transactionElement);

            Attr transAtt = document.createAttribute("trans");
            transAtt.setTextContent(entry.getKey());
            transactionElement.setAttributeNode(transAtt);

            for (PatientErrorResource per : entry.getValue().values()) {
                Element perElement = document.createElement("error");
                transactionElement.appendChild(perElement);

                perElement.setAttribute("pid", per.getPatientId());
                perElement.setAttribute("err", per.getErrorCode());
            }
        }
        return mapElement;
    }

    @Override
    public Map<String, PatientErrorListResource> unmarshal(Element rootElement) throws Exception {
        NodeList nodeList = rootElement.getChildNodes();
        Map<String,PatientErrorListResource> map = new HashMap<>(nodeList.getLength());

        for (int i=0; i<nodeList.getLength(); i++) {
            Node transactionNode = nodeList.item(i);
            if (transactionNode.getNodeType() != Node.ELEMENT_NODE) continue;
            Element transactionElement = (Element) transactionNode;
            String transactionName = transactionElement.getAttribute("trans");

            PatientErrorListResource pelResource = new PatientErrorListResource();
            map.put(transactionName, pelResource);
            NodeList pelNodeList = transactionElement.getChildNodes();
            for (int j=0; j<pelNodeList.getLength(); j++) {
                Node pelNode = nodeList.item(i);
                if (pelNode.getNodeType() != Node.ELEMENT_NODE) continue;
                Element pelElement = (Element) pelNode;
                String pid = pelElement.getAttribute("pid");
                String err = pelElement.getAttribute("err");

                PatientErrorResource pe = new PatientErrorResource();
                pe.setErrorCode(err);
                pe.setPatientId(pid);
                pelResource.add(pe);
            }
        }

        return map;
    }

}