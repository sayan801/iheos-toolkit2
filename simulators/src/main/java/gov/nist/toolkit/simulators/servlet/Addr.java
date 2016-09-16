package gov.nist.toolkit.simulators.servlet;

import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.actortransaction.client.ATFactory;
import gov.nist.toolkit.configDatatypes.client.TransactionType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Endpoint parsing
 * endpoint looks like
 * http://host:port/xdstools2/sim/simid/actor/transaction[/validation]
 * where
 *   simid is a uniqueID for a simulator
 *   actor
 *   transaction
 *   validation - name of a validation to be performed
 */
class Addr {
    private HttpServletRequest request;
    private HttpServletResponse response;
    SimId simid = null;
    String actor = null;
    String transaction = null;
    String validation = null;
    String uri;
    private String toolkitServletName;
    private String endpointFormat;
    TransactionType transactionType;
    private boolean isSim = false;

    Addr(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    Addr parse(String uri) {
        int simIndex;
        String[] uriParts = uri.split("\\/");

        toolkitServletName = (uriParts.length < 2) ? "" : uriParts[1];

        for (simIndex = 0; simIndex < uriParts.length; simIndex++) {
            if ("sim".equals(uriParts[simIndex])) {
                isSim = true;
                break;
            }
        }

        endpointFormat = " - Endpoint format is http://" + request.getLocalName() + ":" + request.getLocalPort() + "/" + toolkitServletName + "/sim/simid/actor/transaction[/validation] " +
                "where simid, actor and transaction are variables for simulators. " +
                "If validation is included, then this validation must be performed successfully for the transaction to be successful. " +
                " Validations are documented as part of tests that use them.";

        if (simIndex >= uriParts.length) {
            if (isHTTP())
                SoapFaultSender.sendSoapFault(response, "Simulator: Do not understand endpoint http://" + request.getLocalName() + ":" + request.getLocalPort() + uri + endpointFormat);
            return null;
        }

        try {
            simid = new SimId(uriParts[simIndex + 1]);
            actor = uriParts[simIndex + 2];
            transaction = uriParts[simIndex + 3];
        } catch (Exception e) {
            if (isHTTP())
                SoapFaultSender.sendSoapFault(response, "Simulator: Do not understand endpoint http://" + request.getLocalName() + ":" + request.getLocalPort() + uri + endpointFormat + " - " + e.getClass().getName() + ": " + e.getMessage());
            return null;
        }

        try {
            validation = uriParts[simIndex + 4];
        } catch (Exception e) {
            // ignore - null value will signal no validation
        }

        transactionType = ATFactory.findTransactionByShortName(transaction);
        SimServlet.logger.debug("Incoming transaction is " + transaction);
        SimServlet.logger.debug("... which is " + transactionType);
        if (transactionType == null) {
            if (isHTTP())
                SoapFaultSender.sendSoapFault(response, "Simulator: Do not understand the transaction requested by this endpoint (" + transaction + ") in http://" + request.getLocalName() + ":" + request.getLocalPort() + uri + endpointFormat);
            return null;
        }

        return this;
    }

    boolean isHTTP() {
        return transactionType.isHTTP();
    }

    public boolean isSim() {
        return isSim;
    }
}
