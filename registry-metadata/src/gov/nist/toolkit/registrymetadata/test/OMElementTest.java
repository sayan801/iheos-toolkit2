package gov.nist.toolkit.registrymetadata.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.junit.Test;

public class OMElementTest {

	@Test
	public void equalsTest() {
		OMElement ele1 = OMAbstractFactory.getOMFactory().createOMElement("Foo", null);
		OMElement ele2 = OMAbstractFactory.getOMFactory().createOMElement("Foo", null);
		assertTrue(ele1.equals(ele1));
		assertTrue(ele1.equals(ele2));
	}
}
