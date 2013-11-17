package gov.nist.toolkit.registrymetadata.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.nist.toolkit.registrymetadata.util.OMComparitor;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.junit.Test;

public class OMComparitorTest {
	OMNamespace ns1 = factory().createOMNamespace("http://ns1", "ns1");
	OMNamespace ns2 = factory().createOMNamespace("http://ns2", "ns1");

	@Test
	public void simpleElementTest() {
		OMElement ele1 = factory().createOMElement("Foo", null);
		OMElement ele2 = factory().createOMElement("Foo", null);
		OMElement ele3 = factory().createOMElement("Bar", null);
		assertTrue(OMComparitor.isEqual(ele1, ele1));
		assertTrue(OMComparitor.isEqual(ele1, ele2));
		assertFalse(OMComparitor.isEqual(ele1, ele3));
	}

	@Test
	public void simpleAttributeTest() {
		OMElement ele1 = factory().createOMElement("Foo", null);
		ele1.addAttribute("id", "me", null);
		
		OMElement ele2 = factory().createOMElement("Foo", null);
		ele2.addAttribute("id", "me", null);

		OMElement ele3 = factory().createOMElement("Foo", null);
		ele3.addAttribute("id", "you", null);

		OMElement ele4 = factory().createOMElement("Foo", null);
		ele4.addAttribute("id2", "me", null);

		assertTrue(OMComparitor.isEqual(ele1, ele1));
		assertTrue(OMComparitor.isEqual(ele1, ele2));
		assertFalse(OMComparitor.isEqual(ele1, ele3));
		assertFalse(OMComparitor.isEqual(ele1, ele4));
	}

	@Test
	public void attributeNsTest() {
		OMElement ele1 = factory().createOMElement("Foo", null);
		ele1.addAttribute("id", "me", ns1);
		
		OMElement ele2 = factory().createOMElement("Foo", null);
		ele2.addAttribute("id", "me", ns1);

		OMElement ele3 = factory().createOMElement("Foo", null);
		ele3.addAttribute("id", "me", ns2);

		assertTrue(OMComparitor.isEqual(ele1, ele1));
		assertTrue(OMComparitor.isEqual(ele1, ele2));
		assertFalse(OMComparitor.isEqual(ele1, ele3));
	}

	@Test
	public void simpleNsElementTest() {
		OMElement ele1 = factory().createOMElement("Foo", ns1);
		OMElement ele2 = factory().createOMElement("Foo", ns1);
		OMElement ele3 = factory().createOMElement("Foo", ns2);
		assertTrue(OMComparitor.isEqual(ele1, ele1));
		assertTrue(OMComparitor.isEqual(ele1, ele2));
		assertFalse(OMComparitor.isEqual(ele1, ele3));
	}
	
	@Test
	public void singleChildTest() {
		OMElement ele1 = factory().createOMElement("Foo", ns1);
		ele1.addChild(factory().createOMElement("Bar", ns1));
		OMElement ele2 = factory().createOMElement("Foo", ns1);
		ele2.addChild(factory().createOMElement("Bar", ns1));
		OMElement ele3 = factory().createOMElement("Foo", ns1);
		ele3.addChild(factory().createOMElement("Bar", ns2));
		assertTrue(OMComparitor.isEqual(ele1, ele1));
		assertTrue(OMComparitor.isEqual(ele1, ele2));
		assertFalse(OMComparitor.isEqual(ele1, ele3));
	}
	
	@Test
	public void multiChildTest() {
		OMElement ele1 = factory().createOMElement("Foo", ns1);
		ele1.addChild(factory().createOMElement("Bar", ns1));
		ele1.addChild(factory().createOMElement("Barfu", ns1));

		OMElement ele2 = factory().createOMElement("Foo", ns1);
		ele2.addChild(factory().createOMElement("Bar", ns1));
		ele2.addChild(factory().createOMElement("Barfu", ns1));
		
		OMElement ele2a = factory().createOMElement("Foo", ns1);
		ele2a.addChild(factory().createOMElement("Barfu", ns1));
		ele2a.addChild(factory().createOMElement("Bar", ns1));
		
		OMElement ele3 = factory().createOMElement("Foo", ns1);
		ele3.addChild(factory().createOMElement("Bar", ns1));
		ele3.addChild(factory().createOMElement("Barfu", ns2));
		
		OMElement ele4 = factory().createOMElement("Foo", ns1);
		ele4.addChild(factory().createOMElement("Bar", ns1));
		
		assertTrue(OMComparitor.isEqual(ele1, ele1));
		assertTrue(OMComparitor.isEqual(ele1, ele2));
		assertTrue(OMComparitor.isEqual(ele1, ele2a));
		assertFalse(OMComparitor.isEqual(ele1, ele3));
		assertFalse(OMComparitor.isEqual(ele1, ele4));
	}

	private OMFactory factory() { return OMAbstractFactory.getOMFactory(); }
}
