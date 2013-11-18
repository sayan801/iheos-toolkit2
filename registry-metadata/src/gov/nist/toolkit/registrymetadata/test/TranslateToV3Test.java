package gov.nist.toolkit.registrymetadata.test;

import static org.junit.Assert.assertTrue;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymetadata.TranslateToV3;
import gov.nist.toolkit.registrymetadata.util.OMComparitor;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import org.apache.axiom.om.OMElement;
import org.junit.Test;

public class TranslateToV3Test {
	
	
	@Test
	public void translateTest() throws XdsInternalException {
		TranslateToV3 tr = new TranslateToV3();
		OMElement testdata = testData();
		OMElement translated;
		
		translated = tr.translate(testdata, true);
		System.out.println("\norig: " + testdata.toString());
		System.out.println("\ntran: " + translated.toString());
		assertTrue(OMComparitor.isEqual(testdata, translated));
	}

	OMElement testData() {
		Metadata m = new Metadata();
		m.setVersion3();
		
		OMElement de = m.mkExtrinsicObject("Doc1", "text/plain");
		m.addSlot(de, "size", "123");
		return de;
	}

}
