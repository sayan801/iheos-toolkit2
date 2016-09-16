package gov.nist.toolkit.simulators.servlet;

import gov.nist.toolkit.simulators.support.DsSimCommon;
import gov.nist.toolkit.simulators.support.SimCommon;
import gov.nist.toolkit.soap.http.SoapFault;
import gov.nist.toolkit.utilities.xml.OMFormatter;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletResponse;

/**
 *
 */
public class SoapFaultSender {
    static Logger logger = Logger.getLogger(SoapFaultSender.class);

    static void sendSoapFault(HttpServletResponse response, String message) {
        try {
            SoapFault sf = new SoapFault(SoapFault.FaultCodes.Sender, message);
            SimCommon c = new SimCommon(response);
            DsSimCommon dsSimCommon = new DsSimCommon(c);
            OMElement faultEle = sf.getXML();
            logger.info("Sending SOAP Fault:\n" + new OMFormatter(faultEle).toString());
            OMElement soapEnv = dsSimCommon.wrapResponseInSoapEnvelope(faultEle);
            dsSimCommon.sendHttpResponse(soapEnv, SimCommon.getUnconnectedErrorRecorder(), false);
        } catch (Exception e) {
            logger.error(ExceptionUtil.exception_details(e));
        }
    }

    static void sendSoapFault(DsSimCommon dsSimCommon, String message) {
        SoapFault sf = new SoapFault(SoapFault.FaultCodes.Sender, message);
        dsSimCommon.sendFault(sf);
    }

}
