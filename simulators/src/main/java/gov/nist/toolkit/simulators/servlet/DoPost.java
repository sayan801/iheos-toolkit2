package gov.nist.toolkit.simulators.servlet;

import gov.nist.toolkit.actorfactory.GenericSimulatorFactory;
import gov.nist.toolkit.actorfactory.RuntimeManager;
import gov.nist.toolkit.actorfactory.SimDb;
import gov.nist.toolkit.actorfactory.client.NoSimException;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.configDatatypes.SimulatorProperties;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.GwtErrorRecorderBuilder;
import gov.nist.toolkit.http.HttpHeader;
import gov.nist.toolkit.http.ParseException;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.simulators.sim.reg.store.RegIndex;
import gov.nist.toolkit.simulators.sim.rep.RepIndex;
import gov.nist.toolkit.simulators.support.DsBaseActorSimulator;
import gov.nist.toolkit.simulators.support.DsSimCommon;
import gov.nist.toolkit.simulators.support.SimCommon;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.DefaultValidationContextFactory;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.client.XdsException;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import static gov.nist.toolkit.simulators.servlet.SimServlet.getRegIndex;
import static gov.nist.toolkit.simulators.servlet.SimServlet.getRepIndex;

/**
 *
 */
public class DoPost extends DoCommon {
    static Logger logger = Logger.getLogger(DoPost.class);

    DoPost(ServletConfig config) {
        super(config);
    }

    void doPost(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;

        String uri  = request.getRequestURI().toLowerCase();
        logger.info("+ + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + ");
        logger.info("uri is " + uri);
        logger.info("warHome is " + Installation.installation().warHome());
        RegIndex regIndex = null;
        RepIndex repIndex = null;
        ServletContext servletContext = config.getServletContext();
        boolean responseSent = false;

        Date now = new Date();

        //
        // Parse endpoint to see which simulator/actor/transaction is target
        //
        Addr addr = new Addr(request, response).parse(uri);
        if (addr == null)
            return;

        boolean transactionOk = true;

        MessageValidatorEngine mvc = new MessageValidatorEngine();
        try {

            // DB space for this simulator
            SimDb db = new SimDb(Installation.installation().simDbFile(), addr.simid, addr.actor, addr.transaction);
            request.setAttribute("SimDb", db);

            logRequest(request, db, addr.actor, addr.transaction);

            SimulatorConfig asc = GenericSimulatorFactory.getSimConfig(Installation.installation().simDbFile(), addr.simid);
            request.setAttribute("SimulatorConfig", asc);

            regIndex = getRegIndex(addr.simid);
            repIndex = getRepIndex(addr.simid);

            ValidationContext vc = DefaultValidationContextFactory.validationContext();
            vc.forceMtom = addr.transactionType.isRequiresMtom();

            SimulatorConfigElement asce = asc.get(SimulatorProperties.codesEnvironment);
            if (asce != null)
                vc.setCodesFilename(asce.asString());

            SimCommon common= new SimCommon(db, uri.startsWith("https"), vc, mvc, response);
            DsSimCommon dsSimCommon = new DsSimCommon(common, regIndex, repIndex);

            ErrorRecorder er = new GwtErrorRecorderBuilder().buildNewErrorRecorder();
            er.sectionHeading("Endpoint");
            er.detail("Endpoint is " + uri);
            mvc.addErrorRecorder("**** Web Service: " + uri, er);

            /////////////////////////////////////////////////////////////
            //
            // run the simulator for the requested actor/transaction pair
            //
            //////////////////////////////////////////////////////////////

            response.setContentType("application/soap+xml");

//			DsBaseActorSimulator sim = getSimulatorRuntime(simid);
            DsBaseActorSimulator sim = (DsBaseActorSimulator) RuntimeManager.getSimulatorRuntime(addr.simid);

            sim.init(dsSimCommon, asc);
            if (asc.getConfigEle(SimulatorProperties.FORCE_FAULT).asBoolean()) {
                SoapFaultSender.sendSoapFault(dsSimCommon, "Forced Fault");
                responseSent = true;
            } else {
                sim.onTransactionBegin(asc);
                transactionOk = sim.run(addr.transactionType, mvc, addr.validation);
                sim.onTransactionEnd(asc);
            }
        }
        catch (InvocationTargetException e) {
            SoapFaultSender.sendSoapFault(response, ExceptionUtil.exception_details(e));
            logger.error(ExceptionUtil.exception_details(e));
            responseSent = true;
        }
        catch (IllegalAccessException e) {
            SoapFaultSender.sendSoapFault(response, ExceptionUtil.exception_details(e));
            logger.error(ExceptionUtil.exception_details(e));
            responseSent = true;
        }
        catch (InstantiationException e) {
            SoapFaultSender.sendSoapFault(response, ExceptionUtil.exception_details(e));
            logger.error(ExceptionUtil.exception_details(e));
            responseSent = true;
        }
        catch (RuntimeException e) {
            SoapFaultSender.sendSoapFault(response, ExceptionUtil.exception_details(e));
            logger.error(ExceptionUtil.exception_details(e));
            responseSent = true;
        }
        catch (IOException e) {
            SoapFaultSender.sendSoapFault(response, ExceptionUtil.exception_details(e));
            logger.error(ExceptionUtil.exception_details(e));
            responseSent = true;
        }
        catch (HttpHeader.HttpHeaderParseException e) {
            SoapFaultSender.sendSoapFault(response, ExceptionUtil.exception_details(e));
            logger.error(ExceptionUtil.exception_details(e));
            responseSent = true;
        } catch (ClassNotFoundException e) {
            SoapFaultSender.sendSoapFault(response, ExceptionUtil.exception_details(e));
            logger.error(ExceptionUtil.exception_details(e));
            responseSent = true;
        } catch (XdsException e) {
            SoapFaultSender.sendSoapFault(response, ExceptionUtil.exception_details(e));
            logger.error(ExceptionUtil.exception_details(e));
            responseSent = true;
        } catch (NoSimException e) {
            SoapFaultSender.sendSoapFault(response, ExceptionUtil.exception_details(e));
            logger.error(ExceptionUtil.exception_details(e));
            responseSent = true;
        } catch (ParseException e) {
            SoapFaultSender.sendSoapFault(response, ExceptionUtil.exception_details(e));
            logger.error(ExceptionUtil.exception_details(e));
            responseSent = true;
        }
        finally {
            mvc.run();
            closeOut(response);
        }

        mvc.run();


        // this should go away after repository code made to use deltas
        if (!transactionOk) {
            synchronized(this) {
                // delete memory copy of indexes so they don't getRetrievedDocumentsModel written out
                servletContext.setAttribute("Rep_" + addr.simid, null);
                repIndex = null;
            }
        }

        List<String> flushed = new ArrayList<String>();
        int regCacheCount = 0;
        int repCacheCount = 0;
        try {

            // Update disk copy of indexes
            if (repIndex != null) {
                repIndex.save();
            }

            logger.info("Starting Reg/Rep Cache cleanout");
            synchronized(this) {

                // check for indexes that are old enough they should be removed from cache
                for (@SuppressWarnings("unchecked")
                     Enumeration<String> en = (Enumeration<String>) servletContext.getAttributeNames(); en.hasMoreElements(); ) {
                    String name = en.nextElement();
                    if (name.startsWith("Reg_")) {
                        RegIndex ri = (RegIndex) servletContext.getAttribute(name);
                        if (ri.cacheExpires.before(now)) {
                            logger.info("Unloading " + name);
                            servletContext.removeAttribute(name);
                            flushed.add(name);
                        } else
                            regCacheCount++;
                    }
                    if (name.startsWith("Rep_")) {
                        RepIndex ri = (RepIndex) servletContext.getAttribute(name);
                        if (ri.cacheExpires.before(now)) {
                            logger.info("Unloading " + name);
                            servletContext.removeAttribute(name);
                            flushed.add(name);
                        } else
                            repCacheCount++;
                    }
                }

            }
            logger.info("Done with Reg/Rep Cache cleanout");
        } catch (IOException e) {
            logger.info("Done with Reg/Rep Cache cleanout");
            if (!responseSent)
                SoapFaultSender.sendSoapFault(response, ExceptionUtil.exception_details(e));
            e.printStackTrace();
        }

        logger.debug(regCacheCount + " items left in the Registry Index cache");
        logger.debug(repCacheCount + " items left in the Repository Index cache");

    } // EO doPost method}



}