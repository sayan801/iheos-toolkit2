package gov.nist.toolkit.valregmsg.registry.storedquery.generic;

import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrysupport.logging.LoggerException;
import gov.nist.toolkit.valregmsg.registry.BasicQuery;
import gov.nist.toolkit.valregmsg.registry.storedquery.support.StoredQuerySupport;
import gov.nist.toolkit.xdsexception.client.MetadataValidationException;
import gov.nist.toolkit.xdsexception.XDSRegistryOutOfResourcesException;
import gov.nist.toolkit.xdsexception.client.XdsException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author bill
 *
 * This is the base class for the implementation of all Stored Queries. This is the base of a 
 * three-level hierarchy of child classes for implementing specific Stored Queries against specific
 * underlying databases. The hierarchy for the FindDocuments Stored Query looks like:
 *   StoredQuery - this base class. This is the base layer. 
 *   FindDocuments - generic implementation of the FindDocuments Stored Query. It contains details of
 *   the stored query specification in the profile (required parameters etc.). This is called the query 
 *   specific layer. This class inherits from StoredQuery. 
 *   EbXMLFindDocuments - specific implementation for the ebXML Registry 2.1 backend registry implementation
 *       (this class if found in the package xds and not here in common). This is the
 *       implementation specific layer. This class inherits from FindDocuments.
 *       
 *   Processing
 *   The processing begins with a Factory class specialized for the implementation that first creates an 
 *   instance of StoredQuerySupport to hold
 *   all the parameters and control variables. 
 *   Next an instance of the implementation specific class is created.  Its two
 *   super classes, the query specific layer and the base layer, are naturally included because of 
 *   the inheritance. 
 *   
 *   Execution begins with the Factory calling the run() 
 *   method defined in the base StoredQuery class. The run() method calls runSpecific(), a
 *   method in the query specific layer.  
 *   
 *   Each stored query requires/accepts a different collection of parameters.  The runSpecific() method 
 *   parses the stored query input into a collection of Java variables for later processing and then 
 *   validates that the correct combination of parameters were specified. If successful it calls the
 *   runImplementation() method which is defined in the implementation specific layer to execute the 
 *   Stored Query.
 *   
 *   The linkages between layers is specified through abstract methods:
 *      StoredQuery declares: abstract public Metadata runSpecific();
 *      FindDocuments declares: abstract protected Metadata runImplementation();
 */
abstract public class StoredQuery extends BasicQuery  {

	// Run specific Stored Query (defined in Stored Query specific subclass)
	abstract public Metadata runSpecific() throws XdsException, LoggerException, XDSRegistryOutOfResourcesException;
	abstract public void validateParameters() throws MetadataValidationException;

	// Generic Stored Query parameters (returnType etc)
	protected StoredQuerySupport sqs;
	protected boolean metadataLevel2 = false;

	static protected final String stableDocumentType = "urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1";
	static protected final String onDemandDocumentType = "urn:uuid:34268e47-fdf5-41a6-ba33-82133c465248";
	static protected List<String> documentTypes;

	static {
		documentTypes = new ArrayList<>();
		documentTypes.add(stableDocumentType);
		documentTypes.add(onDemandDocumentType);
	}

	protected void parseParameters() throws XdsInternalException, XdsException, LoggerException {
		String value = sqs.params.getIntParm("$MetadataLevel");
		metadataLevel2 = (value != null) && value.equals("2");
	}

	protected String validDocumentTypesString() { return "[" + stableDocumentType + ", " + onDemandDocumentType + "]"; }

	static protected String QueryParmsErrorPresentErrMsg = "Query aborted, errors found parsing parameters";

	public StoredQuery(StoredQuerySupport storedQuerySupport) {
		sqs = storedQuerySupport;
	}
	
	public StoredQuerySupport getStoredQuerySupport() {
		return sqs;
	}

	/**
	 * Run an arbitrary stored query. It calls the abstract method runSpecific() to engage the parsing
	 * of parameters and execution of the query.
	 * @return List of OMElements to be returned from the query
	 * @throws XdsException
	 * @throws LoggerException
	 * @throws XDSRegistryOutOfResourcesException
	 */
	public Metadata run() throws XdsException, LoggerException, XDSRegistryOutOfResourcesException {
		Metadata metadata;

		metadata = runSpecific();

		if (sqs.log_message != null) {
			if (metadata == null) 
				sqs.log_message.addOtherParam("Metadata Structure", "null");
			else {
				sqs.log_message.addOtherParam("Metadata Structure", metadata.structure());
				sqs.log_message.addOtherParam("Patient IDs", metadata.getPatientIds().toString());
			}
		}


		if (sqs.runEndProcessing) {
			if (metadata == null)
				return null;
			if (sqs.returnType == QueryReturnType.LEAFCLASS || sqs.returnType == QueryReturnType.LEAFCLASSWITHDOCUMENT) {
				if (sqs.is_secure) secure_URI(metadata);
				return new Metadata().addToMetadata(metadata.getV3(), true);
			}
			else {
				logger.info("StoredQuery#run refs = " + metadata.getMajorObjects());
				return new Metadata().
				addToMetadata(metadata.getObjectRefs(metadata.getMajorObjects(), false /* v3 */),
						true  /* discard_duplicates */);
			}
		} else {
			return metadata;
		}
	}


}

