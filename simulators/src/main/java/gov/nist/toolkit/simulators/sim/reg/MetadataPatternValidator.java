package gov.nist.toolkit.simulators.sim.reg;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.simulators.support.SimCommon;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.AbstractMessageValidator;

public class MetadataPatternValidator extends AbstractMessageValidator {
	SimCommon common;
	String validation;

	public MetadataPatternValidator(SimCommon common, String validation) {
		super(common.vc);
		this.common = common;
		this.validation = validation;
	}


	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		this.er = er;
		er.registerValidator(this);
		
		if (validation != null) {
			if (!common.vc.hasMetadataPattern(validation))
				er.err(Code.XDSRegistryError, "Validation " + validation + " failed", this, null);
		}
		er.unRegisterValidator(this);
	}

}
