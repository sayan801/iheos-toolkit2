package gov.nist.toolkit.soap.wsseToolkitAdapter;

import java.security.KeyStoreException;

import org.apache.soap.providers.com.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.TextErrorRecorder;
import gov.nist.toolkit.soap.wsseToolkitAdapter.log4jToErrorRecorder.AppenderForErrorRecorder;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.MessageValidator;
import gov.nist.toolkit.wsseToolkit.api.WsseHeaderValidator;
import gov.nist.toolkit.wsseToolkit.generation.GenerationException;

/**
 * Temporary adapter between toolkit legacy validation code and the wsse module
 * validation code.
 * 
 * TODO: check with Bill. In my own opinion, the design of the message validator
 * interface is flawed. As a first shot and since the goal is to enforce an
 * contract, an interface Validator with a run() method seems more appropriate.
 * ValidationContext could be push as a parameter of this method.
 * 
 * NOTE : CustomLogger is a quick way to log stuff from the wsse module without
 * having to define an object model of what is "logging"!
 * 
 * TODO clarify what vc , err, mvc are doing! How comes the element to validate 
 * on in not part of the run() params?
 * 
 * TODO why should we pass the envelope in the constructor? Confusing.
 * 
 * TODO field er in MessageValidator is not initialized!
 * 
 * @author gerardin
 * 
 */

public class WsseHeaderValidatorAdapter extends MessageValidator {

	private static Logger log = LoggerFactory.getLogger(WsseHeaderValidatorAdapter.class);
	
	/**
	 *Validate our own generated message!
	 */
	public static void main(String[] args) throws KeyStoreException, GenerationException {
		String store = "/Users/gerardin/IHE-Testing/xdstools2_environment/environment/AEGIS_env/keystore/keystore";
		String sPass = "changeit";
		String kPass = "changeit";
		String alias = "hit-testing.nist.gov";
		Element wsseHeader = WsseHeaderGeneratorAdapter.buildHeader(store, sPass, alias, kPass);
		
		WsseHeaderValidatorAdapter validator = new WsseHeaderValidatorAdapter(new ValidationContext(), wsseHeader);
		ErrorRecorder er = new TextErrorRecorder();
		MessageValidatorEngine mvc = new MessageValidatorEngine();
		validator.run(er, mvc);
	}

	private WsseHeaderValidator val;
	private Element header;

	public WsseHeaderValidatorAdapter(ValidationContext vc, Element wsseHeader) {
		super(vc);
		val = new WsseHeaderValidator();
		this.header = wsseHeader;
	}

	@Override
	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		//We use a special appender to record message coming from the wsse module in the error recorder framework
		AppenderForErrorRecorder wsseLogApp = new AppenderForErrorRecorder(vc, er, mvc);	
		
		//those are the logs we are interested in
		org.apache.log4j.Logger logMainVal = org.apache.log4j.Logger.getLogger(WsseHeaderValidator.class);
		org.apache.log4j.Logger logVal = org.apache.log4j.Logger.getLogger("gov.nist.toolkit.wsseToolkit.validation");
		logVal.addAppender(wsseLogApp);
		logMainVal.addAppender(wsseLogApp);
		
		val.validate(header);
		
		log.info("\n" +
				 "================================================" +
				 "Error summary generated by the error recorder : " +
				 "================================================");
		er.showErrorInfo();
	}
}