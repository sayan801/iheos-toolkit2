package gov.nist.toolkit.valregmsg.service;

import gov.nist.toolkit.commondatatypes.MetadataSupport;

import java.util.HashMap;
import java.util.Map;

public class SoapActionFactory {

	public final static String pnr_b_async_action = "urn:ihe:iti:2007:ProvideAndRegisterDocumentSet-b";
	public final static String pnr_b_action       = "urn:ihe:iti:2007:ProvideAndRegisterDocumentSet-b";
	public final static String r_b_action         = "urn:ihe:iti:2007:RegisterDocumentSet-b";
	public final static String ret_b_action       = "urn:ihe:iti:2007:RetrieveDocumentSet";
	public final static String ret_b_async_action = "urn:ihe:iti:2007:RetrieveDocumentSet";
	public final static String anon_action        = "urn:anonOutInOp";
	public final static String rad_69_action      = "urn:ihe:rad:2009:RetrieveImagingDocumentSet";
	public final static String rad_75_action      = "urn:ihe:rad:2011:CrossGatewayRetrieveImagingDocumentSet";

	public final static String epsos_xcqr_action = "urn:epsos:xcqr";

	public final static String xcpd = "urn:hl7-org:v3:PRPA_IN201305UV02:CrossGatewayPatientDiscovery";
	public final static String iti_80_transaction = "urn:ihe:iti:2015:CrossGatewayDocumentProvide";

	public final static String r_odde_action = "urn:ihe:iti:2010:RegisterOnDemandDocumentEntry";

	private static final Map<String, String> actions =
		new HashMap<String, String>()
		{

		     /**
			 *
			 */
			private static final long serialVersionUID = 1L;

			{
				// this is temp value for epsos
				put(epsos_xcqr_action, "urn:epsos:xcqrResponse");

		    	 put(pnr_b_action,                                       "urn:ihe:iti:2007:ProvideAndRegisterDocumentSet-bResponse");

				 put("urn:ihe:iti:2018:RestrictedUpdateDocumentSet",      "urn:ihe:iti:2018:RestrictedUpdateDocumentSetResponse");
				put("urn:ihe:iti:2017:RemoveDocuments",                   "urn:ihe:iti:2017:RemoveDocumentsResponse");
		    	 put("urn:ihe:iti:2010:UpdateDocumentSet",               "urn:ihe:iti:2010:UpdateDocumentSetResponse");
				put("urn:ihe:iti:2010:DeleteDocumentSet",               "urn:ihe:iti:2010:DeleteDocumentSetResponse");
		    	 put(r_b_action,                                         "urn:ihe:iti:2007:RegisterDocumentSet-bResponse");
		    	 put(ret_b_action,                                        "urn:ihe:iti:2007:RetrieveDocumentSetResponse");
		    	 put(rad_69_action,                                      "urn:ihe:iti:2007:RetrieveDocumentSetResponse");
		    	 put(rad_75_action,                                      "urn:ihe:rad:2011:CrossGatewayRetrieveImagingDocumentSetResponse");
		    	 put(MetadataSupport.SQ_action,                          MetadataSupport.SQ_response_action);
		    	 put("urn:ihe:iti:2007:CrossGatewayRetrieve",            "urn:ihe:iti:2007:CrossGatewayRetrieveResponse");
		    	 put("urn:ihe:iti:2007:CrossGatewayQuery",               "urn:ihe:iti:2007:CrossGatewayQueryResponse");
		    	 put(iti_80_transaction,                                 "urn:ihe:iti:2015:CrossGatewayDocumentProvideResponse");
		    	 put(MetadataSupport.dsub_subscribe_action,              MetadataSupport.dsub_subscribe_response_action);
		    	 put(MetadataSupport.MPQ_action,                         "urn:ihe:iti:2009:MultiPatientStoredQueryResponse");


		    	 put(pnr_b_async_action,                                 "urn:ihe:iti:2007:ProvideAndRegisterDocumentSet-bResponse");
		    	 put(ret_b_async_action,                                 "urn:ihe:iti:2007:RetrieveDocumentSetResponse");
		    	 //put("urn:ihe:iti:2007:RegistryStoredQueryAsync",        "urn:ihe:iti:2007:RegistryStoredQueryResponse");
		    	 //put("urn:ihe:iti:2007:CrossGatewayQueryAsync",          "urn:ihe:iti:2007:CrossGatewayQueryResponse");
		    	 put("urn:hl7-org:v3:PRPA_IN201305UV02:CrossGatewayPatientDiscovery", "urn:hl7-org:v3:PRPA_IN201306UV02:CrossGatewayPatientDiscovery");
				 put(r_odde_action,										 "urn:ihe:iti:2010:RegisterOnDemandDocumentResponse");
			}

		};

		// I think perhaps this could better be a method in TransactionType. rm.
	static public String getResponseAction(String requestAction) {
		if (requestAction == null) return null;
		return actions.get(requestAction);
	}
}
