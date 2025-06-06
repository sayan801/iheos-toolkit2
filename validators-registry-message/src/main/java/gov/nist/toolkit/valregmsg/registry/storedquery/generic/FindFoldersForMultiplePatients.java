package gov.nist.toolkit.valregmsg.registry.storedquery.generic;

import gov.nist.toolkit.docref.SqDocRef;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrysupport.logging.LoggerException;
import gov.nist.toolkit.valregmsg.registry.storedquery.support.StoredQuerySupport;
import gov.nist.toolkit.xdsexception.client.MetadataException;
import gov.nist.toolkit.xdsexception.client.MetadataValidationException;
import gov.nist.toolkit.xdsexception.client.XdsException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;

import java.util.List;

/**
Generic implementation of FindFolders Stored Query. This class knows how to parse a 
 * FindFolders Stored Query request producing a collection of instance variables describing
 * the request.  A sub-class must provide the runImplementation() method that uses the pre-parsed
 * information about the stored query and queries a metadata database.
 * @author bill
 *
 */
abstract public class FindFoldersForMultiplePatients extends StoredQuery {

	/**
	 * Method required in subclasses (implementation specific class) to define specific
	 * linkage to local database
	 * @return matching metadata
	 * @throws MetadataException
	 * @throws XdsException
	 * @throws LoggerException
	 */
	abstract protected Metadata runImplementation() throws MetadataException, XdsException, LoggerException;


	public FindFoldersForMultiplePatients(StoredQuerySupport sqs) {
		super(sqs);
	}

	/**
	 * Implementation of Stored Query specific logic including parsing and validating parameters.
	 * @throws XdsException
	 * @throws LoggerException
	 */
	public Metadata runSpecific() throws XdsException, LoggerException {
		validateParameters();

		parseParameters();

		return runImplementation();
	}

	public void validateParameters() throws MetadataValidationException {
		//                    param name,                      required?, multiple?, is string?,   same size as,                        alternative
		sqs.validate_parm("$XDSFolderPatientId",             false,      true,     true,         null,                                  (String[])null);
		sqs.validate_parm("$XDSFolderLastUpdateTimeFrom",    false,     false,     false,        null,                                  (String[])null);
		sqs.validate_parm("$XDSFolderLastUpdateTimeTo",      false,     false,     false,        null,                                  (String[])null);
		sqs.validate_parm("$XDSFolderCodeList",              true,     true,      true,         "$XDSFolderCodeListScheme",            (String[])null);
		sqs.validate_parm("$XDSFolderCodeListScheme",        false,     true,      true,         "$XDSFolderCodeList",                  (String[])null);
		sqs.validate_parm("$XDSFolderStatus",                true,      true,      true,         null,                                  (String[])null);

		if (sqs.has_validation_errors) 
			throw new MetadataValidationException(QueryParmsErrorPresentErrMsg, SqDocRef.Individual_query_parms);
	}

	protected List<String> patient_id;
	protected String update_time_from;
	protected String update_time_to;
	protected List<String> codes;
	protected List<String> code_schemes;
	protected List<String> status;

	protected void parseParameters() throws XdsInternalException, MetadataException, XdsException, LoggerException {
		super.parseParameters();
		patient_id              = sqs.params.getListParm("$XDSFolderPatientId");
		update_time_from        = sqs.params.getIntParm("$XDSFolderLastUpdateTimeFrom");
		update_time_to          = sqs.params.getIntParm("$XDSFolderLastUpdateTimeTo");
		codes        			= sqs.params.getListParm("$XDSFolderCodeList");
		code_schemes 			= sqs.params.getListParm("$XDSFolderCodeListScheme");
		status       			= sqs.params.getListParm("$XDSFolderStatus");

	}
}
