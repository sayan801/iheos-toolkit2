package gov.nist.toolkit.registrymetadata.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;

public class OMComparitor {
	private static final OMComparitor me = new OMComparitor();

	public static boolean isEqual(OMElement a, OMElement b) {
		if (!me.deepCompare(a, b)) return false;
		if (!me.deepCompare(b, a)) return false;
		return true;
	}

	private boolean deepCompare(OMElement a, OMElement b) {
		if (!me.shallowCompare(a, b)) return false;
		@SuppressWarnings("rawtypes")
		Iterator aChildElements = a.getChildElements();
		while (aChildElements.hasNext()) {
			OMElement aChild = (OMElement) aChildElements.next();
			boolean found = false;
			@SuppressWarnings("rawtypes")
			Iterator bChildElements = b.getChildElements();
			while (bChildElements.hasNext()) {
				OMElement bChild = (OMElement) bChildElements.next();
				if (deepCompare(aChild, bChild)) {
					found = true;
					break;
				}
			}
			if (!found) return false;
		}
		return true;
	}

	private boolean shallowCompare(OMElement a, OMElement b) {
		if (a == null || b == null) return false;
		if (!a.getLocalName().equals(b.getLocalName())) return false;
		if (a.getNamespaceURI() == null && b.getNamespaceURI() == null)
			;
		else {
			if (a.getNamespaceURI() == null || b.getNamespaceURI() == null)
				return false;
			if (!a.getNamespaceURI().equals(b.getNamespaceURI())) return false;
		}

		List<String> aAttNames = attNames(a);
		List<String> bAttNames = attNames(b);

		if (!unorderedListEquals(aAttNames, bAttNames)) return false;
		for (String attName : aAttNames) {
			OMAttribute aAtt = findAttributeByName(a, attName);
			OMAttribute bAtt = findAttributeByName(b, attName);
			String aNamespaceURI =  aAtt.getNamespaceURI();
			String bNamespaceURI = bAtt.getNamespaceURI();
			if (aNamespaceURI == null && bNamespaceURI == null)
				;
			else if (aNamespaceURI == null || bNamespaceURI == null) 
				return false;
			else if (!aNamespaceURI.equals(bNamespaceURI)) return false;
			String aValue = aAtt.getAttributeValue();
			String bValue = bAtt.getAttributeValue();
			if (!aValue.equals(bValue)) return false;
		}

		return true;

	}

	private boolean unorderedListEquals(List<String> a, List<String> b) {
		if (a.size() != b.size()) return false;
		for (String s : a) 
			if (!b.contains(s)) return false;
		for (String s : b)
			if (!a.contains(s)) return false;
		return true;
	}

	// This assumes no attribute name duplicates (different namespaces?)
	private List<String> attNames(OMElement ele) {
		List<String> names = new ArrayList<String>();
		@SuppressWarnings("rawtypes")
		Iterator it = ele.getAllAttributes();
		while (it.hasNext()) {
			OMAttribute a = (OMAttribute) it.next();
			names.add(a.getLocalName());
		}
		return names;
	}

	private OMAttribute findAttributeByName(OMElement ele, String attributeName) {
		@SuppressWarnings("rawtypes")
		Iterator it = ele.getAllAttributes();
		while (it.hasNext()) {
			OMAttribute a = (OMAttribute) it.next();
			if (a.getLocalName().equals(attributeName))
				return a;
		}
		return null;
	}

}
