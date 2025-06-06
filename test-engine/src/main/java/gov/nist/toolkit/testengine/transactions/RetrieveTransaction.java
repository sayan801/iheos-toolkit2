package gov.nist.toolkit.testengine.transactions;

import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymsg.repository.RetrievedDocumentModel;
import gov.nist.toolkit.registrymsg.repository.RetrievedDocumentsModel;
import gov.nist.toolkit.testengine.engine.Linkage;
import gov.nist.toolkit.testengine.engine.RetContext;
import gov.nist.toolkit.testengine.engine.RetrieveB;
import gov.nist.toolkit.testengine.engine.StepContext;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.utilities.io.Sha1Bean;
import gov.nist.toolkit.utilities.xml.OMFormatter;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.XdsPreparsedException;
import gov.nist.toolkit.xdsexception.client.MetadataException;
import gov.nist.toolkit.xdsexception.client.XdsException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.apache.axis2.AxisFault;
import java.util.logging.Logger;
import org.jaxen.JaxenException;

import javax.xml.namespace.QName;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class RetrieveTransaction extends BasicTransaction {
//	String metadata_filename = null;
//	OMElement request_ele = null;
	OMElement expected_contents = null;   // never actually used - should remove
	String expected_mime_type = null;
	String uri = null;
	OMElement uri_ref = null;
	HashMap<String, String> referenced_documents = new HashMap<String, String>();  // uid, filename
	Metadata reference_metadata = null;
	boolean is_xca = false;
	boolean useIG = false;
	boolean removeHomeFromRequest = false;
	boolean clean_params = false;
	static Logger logger = Logger.getLogger(RetrieveTransaction.class.getName());
	@Override
   public String toString() {

		return "RetrieveTransaction: *************" +
		"\nmetadata_filename = " + metadata_filename +
		"\nexpected_contents = " + isNull(expected_contents) +
		"\nexpected_mime_type = " + expected_mime_type +
		"\nuri = " + uri +
		"\nuri_ref = " + isNull(uri_ref) +
		"\nreferenced_documents = " + referenced_documents +
		"\nreference_metadata = " + metadataStructure(reference_metadata) +
		"\nuse_document_unique_id = " + use_repository_unique_id +
		"\nuse_id = " + use_id +
		"\nuse_xpath = " + use_xpath +
		"\nlinkage = " + local_linkage_data +
		"\nendpoint = " + endpoint +
		"\nis_xca = " + is_xca +
		"\nactor config = " + ((testConfig == null) ? "null" : testConfig.site) +
		"\n****************";
	}

	String metadataStructure(Metadata m) {
		try {
			return m.structure();
		} catch (Exception e) { }
		return null;
	}

	String isNull(Object thing) { return (thing == null) ? "null" : "not null"; }

	public void setIsXca(boolean isXca) { is_xca = isXca; xds_version = BasicTransaction.xds_b; }

	public void setUseIG(boolean useIG) { this.useIG = useIG; }

	public RetrieveTransaction(StepContext s_ctx, OMElement instruction, OMElement instruction_output) {
		super(s_ctx, instruction, instruction_output);
		defaultEndpointProcessing = false;
		parse_metadata = false;
		noMetadataProcessing = true;
	}

	@Override
   public void run(OMElement request_ele)
	throws Exception {

        if (request_ele == null)
            fatal("Retrieve transaction - request is null");

		validate_xds_version();

		if (xds_version == BasicTransaction.xds_a) {
			throw new Exception("XDS.a no longer supported");
		}
		{
			applyLinkage(request_ele);

			reportManagerPreRun(request_ele);

			if (repositoryUniqueId == null) {
				// if managed by ReportManager then need to extract it from request
				try {
					AXIOMXPath xpathExpression = new AXIOMXPath ("//*[local-name()='RepositoryUniqueId']");
					repositoryUniqueId = xpathExpression.stringValueOf(request_ele);
				} catch (Exception e) {
					fatal("Error extracting repositoryUniqueId from Retrieve request - " + e.getMessage() + "\nRequest is..." +
                    new OMFormatter(request_ele).toString() + "\n...End of Request");
				}
			}

			if (s_ctx.getPlan().getExtraLinkage() != null)
				testLog.add_name_value(instruction_output, "TemplateParams", s_ctx.getPlan().getExtraLinkage());

			if (clean_params)
				cleanRetParams(request_ele);

			if (is_xca) {
				String homeXPath = "//*[local-name()='RetrieveDocumentSetRequest']/*[local-name()='DocumentRequest'][1]/*[local-name()='HomeCommunityId']";
				String home = null;
				try {
					AXIOMXPath xpathExpression = new AXIOMXPath (homeXPath);
					home = xpathExpression.stringValueOf(request_ele);
				} catch (JaxenException e) {
					fatal ("XGR: " + ExceptionUtil.exception_details(e));
				}

				testLog.add_name_value(instruction_output, "InputMetadata", Util.deep_copy(request_ele));

				testLog.add_name_value(instruction_output, "Linkage", this.local_linkage_data.toString());

				if (this.useIG)
					parseIGREndpoint(home, testConfig.secure);
				else
					parseGatewayEndpoint(home, testConfig.secure);

				if (removeHomeFromRequest) {
					try {
						AXIOMXPath xpathExpression = new AXIOMXPath (homeXPath);
						List<?> nodes = xpathExpression.selectNodes(request_ele);
						for (OMElement node : (List<OMElement>) nodes) {
							node.detach();
						}
					} catch (JaxenException e) {
						fatal ("XGR: " + ExceptionUtil.exception_details(e));
					}

				}

			} else {
				// The above 'compile' steps may have updated critical SECTIONS of the metadata.  repositoryUniqueId is critical here.
				if (repositoryUniqueId == null || repositoryUniqueId.equals("")) {
					String xpath = "//*[local-name()='RetrieveDocumentSetRequest']/*[local-name()='DocumentRequest']/*[local-name()='RepositoryUniqueId']/text()";
					try {
						AXIOMXPath xpathExpression = new AXIOMXPath (xpath);
						String result = xpathExpression.stringValueOf(request_ele);
						repositoryUniqueId = result.trim();
					} catch (JaxenException e) {
						fatal("RetrieveTransaction: run(): XPATH error extracting repositoryUniqueId");
					}

				}

				testLog.add_name_value(instruction_output, "InputMetadata", Util.deep_copy(request_ele));

				testLog.add_name_value(instruction_output, "Linkage", this.local_linkage_data.toString());

				if (useReportManager == null &&
						(repositoryUniqueId == null || repositoryUniqueId.equals(""))) {
					fatal("RetrieveTransaction: no repositoryUniqueId");
				}

				// assign endpoint
				parseRepEndpoint(repositoryUniqueId, testConfig.secure);

			}

			RetContext r_ctx = null;
			try {
				// map from doc uid -> info about doc
				// RetInfo holds size, hash, home etc
				HashMap<String, RetrievedDocumentModel> request_info = build_request_info(request_ele /* retrieve request */);

				// Bean that holds the context of the retrieve operation
				r_ctx = new RetContext();
				r_ctx.setRequestInfo(new RetrievedDocumentsModel().setMap(request_info));
				r_ctx.setRequest(request_ele);
				r_ctx.setExpectedError(s_ctx.getExpectedErrorMessage());

				RetrieveB ret_b = new RetrieveB(this, r_ctx, endpoint);
				ret_b.setUseReportManager(useReportManager);
				ret_b.setReportManager(getReportManager());
				ret_b.setAsync(async);
				ret_b.setUseIG(useIG);
				ret_b.setExpectedMimeType(this.expected_mime_type);
				ret_b.setStepContext(instruction_output);
				ret_b.setIsXca(is_xca);
				ret_b.setSoap12(soap_1_2);
				ret_b.setReferenceMetadata(reference_metadata);
				OMElement result = ret_b.run();
				ret_b.validate();
			}
			catch (XdsPreparsedException e) {
				throw new XdsInternalException("Retrieve Error: endpoint was: " + endpoint + " " + e.getMessage(), e);
			}
			catch (Exception e) {
				if (e.getCause()!=null && (e.getCause() instanceof AxisFault) && ((AxisFault)e.getCause()).getFaultCodeElement()!=null
						&& (this.s_ctx.getExpectedStatus().size()>0) && this.s_ctx.getExpectedStatus().get(0).isFault()) {
					s_ctx.set_error(((AxisFault)(e.getCause())).getMessage());
					s_ctx.resetStatus();
					step_failure = false;
					add_step_status_to_output();
					return;
				} else
					throw new XdsInternalException("Retrieve Error: endpoint was: " + endpoint + " " + e.getMessage(), e);
			}

			add_step_status_to_output();

			// check that status == success
                String status = r_ctx.getRrp().get_registry_response_status();
                eval_expected_status(status, r_ctx.getRrp().get_error_code_contexts());

                String expErrorCode = s_ctx.getExpectedErrorCode();
                if (expErrorCode != null && !expErrorCode.equals("")) {
                    List<String> errCodesReturned = r_ctx.getRrp().get_error_codes();
                    if (!errCodesReturned.contains(expErrorCode)) {
                        s_ctx.set_error("Expected errorCode of " + expErrorCode + "\nDid getRetrievedDocumentsModel errorCodes of " +
                                errCodesReturned);
                        step_failure = true;
                    }
                }
                reportManagerPostRun();
            }
	}

	@SuppressWarnings("unchecked")
	void cleanRetParams(OMElement ele) {
		AXIOMXPath xpathExpression;
		try {
			xpathExpression = new AXIOMXPath ("//*[local-name() = 'DocumentRequest']");
			List<OMElement> documentRequests = (List<OMElement>) xpathExpression.selectNodes(ele);
			if (documentRequests == null)
				return;

			for (OMElement docReq : documentRequests) {
				if (containsVariable(docReq))
					docReq.detach();
			}

		} catch (JaxenException e) {
		}
	}

	@SuppressWarnings("unchecked")
	boolean containsVariable(OMElement ele) {
		String valueStr = ele.getText();
		if (containsVariable(valueStr)) return true;
		for (Iterator<OMElement> it=(Iterator<OMElement>)ele.getChildElements(); it.hasNext(); ) {
			OMElement child = (OMElement) it.next();
			if (containsVariable(child))
				return true;
		}
		return false;
	}

	boolean containsVariable(String str) {
		if (str == null) return false;
		int i = str.indexOf("$");
		if (i == -1)
			return false;
		return true;
	}

	void update_referenced_documents() {
		HashMap<String, String> new_entries = new HashMap<String, String>();
		for (String ref_id : referenced_documents.keySet()) {
			if (local_linkage_data.containsKey(ref_id)) {
				new_entries.put(local_linkage_data.get(ref_id), referenced_documents.get(ref_id));
			}
		}
		referenced_documents.putAll(new_entries);
	}

	HashMap<String, String> parse_rep_request(OMElement rdsr) {
		HashMap<String, String> map = new HashMap<String, String>();  // docuid -> repuid

		for (OMElement document_request : XmlUtil.childrenWithLocalName(rdsr, "DocumentRequest")) {
			OMElement doc_uid_ele = XmlUtil.firstChildWithLocalName(document_request, "DocumentUniqueId");
			String doc_uid = doc_uid_ele.getText();

			OMElement rep_uid_ele = XmlUtil.firstChildWithLocalName(document_request, "RepositoryUniqueId") ;
			String rep_uid = rep_uid_ele.getText();
			map.put(doc_uid, rep_uid);
		}
		return map;
	}


	private HashMap<String, RetrievedDocumentModel> build_request_info(OMElement metadata_ele) throws XdsException {
		HashMap<String, RetrievedDocumentModel> request;
		request = new HashMap<String, RetrievedDocumentModel>();
		for (OMElement document_request : XmlUtil.childrenWithLocalName(metadata_ele, "DocumentRequest")) {
			OMElement doc_uid_ele = XmlUtil.firstChildWithLocalName(document_request, "DocumentUniqueId");
			String doc_uid = doc_uid_ele.getText();

			OMElement rep_uid_ele = XmlUtil.firstChildWithLocalName(document_request, "RepositoryUniqueId") ;
			String rep_uid = rep_uid_ele.getText();

			RetrievedDocumentModel rqst = new RetrievedDocumentModel();
			rqst.setDocUid(doc_uid);
			rqst.setRepUid(rep_uid);

			request.put(doc_uid, rqst);

			// linkage contains symbolic_name => real_name mapping
			// referenced_documents are keyed off symbolic_name
			// add real_name keys to referenced_documents
			for (Iterator<String> it=local_linkage_data.keySet().iterator(); it.hasNext(); ) {
				String symbolic_name = it.next();
				String real_name = local_linkage_data.get(symbolic_name);
				if (referenced_documents.containsKey(symbolic_name)) {
					referenced_documents.put(real_name, referenced_documents.get(symbolic_name));
				}
			}

			if (referenced_documents.containsKey(doc_uid)) {
				String filename = referenced_documents.get(doc_uid);

				FileInputStream fis = null;
				try {
					fis = new FileInputStream(new File(filename));
					rqst.setContents(Io.getBytesFromInputStream(fis));
				} catch (Exception e) {
					throw new XdsInternalException("Cannot read ReferenceDocument: " + filename);
				} finally {
					try {
						if (fis != null)
							fis.close();
					} catch (Exception e){

					}
				}
			}
		}
		return request;
	}


	protected void parseInstruction(OMElement part) throws XdsInternalException, MetadataException {
		String part_name = part.getLocalName();
		if (part_name.equals("MetadataFile")) {
			metadata_filename = testConfig.testplanDir + File.separator + part.getText();
			testLog.add_name_value(instruction_output, "MetadataFile", metadata_filename);
		}
		else if (part_name.equals("ExpectedContents")) {
			expected_contents = part;
			testLog.add_name_value(instruction_output, "ExpectedContents", part);
		}
		else if (part_name.equals("ExpectedMimeType")) {
			expected_mime_type = part.getText();
			testLog.add_name_value(instruction_output, "ExpectedMimeType", part);
		}
		else if (part_name.equals("RemoveHomeFromRequest")) {
			removeHomeFromRequest = true;
		}
		else if (part_name.equals("ReferenceDocument")) {
			String filename = null;
			String uid = null;
			filename =testConfig.testplanDir + File.separator + part.getText();
			uid = part.getAttributeValue(new QName("uid"));
			referenced_documents.put(uid, filename);
			testLog.add_name_value(instruction_output, "ReferenceDocument", part);
		}
		else if (part_name.equals("ReferenceMetadata")) {
			String testdir = part.getAttributeValue(new QName("testdir"));
			String step = part.getAttributeValue(new QName("step"));
			if (testdir == null || testdir.equals("") | step == null || step.equals(""))
				throw new XdsInternalException("ReferenceMetadata instruction: both testdir and step are required attributes");
			reference_metadata = new Linkage(testConfig).getResult(testdir, step);
		}
		else if (part_name.equals("UseId")) {
			use_id.add(part);
			testLog.add_name_value(instruction_output, "UseId", part);
		}
		else if (part_name.equals("UseRepositoryUniqueId")) {
			this.use_repository_unique_id.add(part);
			testLog.add_name_value(instruction_output, "UseRepositoryUniqueId", part);
		}
		else if (part_name.equals("UseXPath")) {
			use_xpath.add(part);
			testLog.add_name_value(instruction_output, "UseXRef", part);
		}
		else if (part_name.equals("XDSb")) {
			xds_version = BasicTransaction.xds_b;
		}
		else if (part_name.equals("SOAP11")) {
			soap_1_2 = false;
			testLog.add_simple_element(this.instruction_output, "SOAP11");
		}
		else if (part_name.equals("URI")) {
			uri = part.getText();
		}
		else if (part_name.equals("URIRef")) {
			uri_ref = part;
		}
		else if (part_name.equals("CleanParams")) {
			clean_params = true;
			testLog.add_name_value(instruction_output, "CleanParams", part);
		}
		else if (part_name.equals("XDSa")) {
			xds_version = BasicTransaction.xds_a;
		} else {
			parseBasicInstruction(part);
		}
	}

	String sha1(byte[] buf) throws Exception {
		Sha1Bean sb = new Sha1Bean();
		sb.setByteStream(buf);
		return sb.getSha1String();
	}

	@Override
	protected String getRequestAction() {
		return null;
	}

	@Override
   protected String getBasicTransactionName() {
		return "ret";
	}

}
