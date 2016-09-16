package gov.nist.toolkit.simulators.support;

import gov.nist.toolkit.actorfactory.ActorSimulatorHandlers;
import gov.nist.toolkit.actorfactory.SimDb;
import gov.nist.toolkit.errorrecording.ErrorRecorder;

import javax.servlet.http.HttpServletResponse;

/**
 *
 */
public abstract class BaseActorSimulator extends ActorSimulatorHandlers  {
    protected SimDb db;
    protected SimCommon common = null;
    protected ErrorRecorder er = null;
    public HttpServletResponse response;
}
