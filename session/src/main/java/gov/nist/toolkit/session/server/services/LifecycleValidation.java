package gov.nist.toolkit.session.server.services;

import gov.nist.toolkit.results.CommonService;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.xdsexception.client.XdsException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LifecycleValidation extends CommonService {
	Session session;
	
	public LifecycleValidation(Session session) throws XdsException {
		this.session = session;
	}
	
	public List<Result> run(SiteSpec site, String pid) {
		try {
			session.setSiteSpec(site);
			session.transactionSettings.assignPatientId = false;
			TestInstance testName = new TestInstance("tc:lifecycle", session.getTestSession());
			List<String> sections = null;
			Map<String, String> params = new HashMap<String, String>();
			params.put("$patientid$", pid);

			List<Result> results = asList(session.xdsTestServiceManager().xdstest(testName, sections, params, null, null, false));
			return results;
		} catch (Exception e) {
			return buildExtendedResultList(e);
		} finally {
			session.clear();
		}
	}


}
