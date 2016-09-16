package gov.nist.toolkit.simulators.servlet;

import gov.nist.toolkit.actorfactory.GenericSimulatorFactory;
import gov.nist.toolkit.actorfactory.RuntimeManager;
import gov.nist.toolkit.actorfactory.SimDb;
import gov.nist.toolkit.actorfactory.SimManager;
import gov.nist.toolkit.actorfactory.client.NoSimException;
import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.GwtErrorRecorderBuilder;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.simulators.sim.reg.store.MetadataCollection;
import gov.nist.toolkit.simulators.sim.reg.store.RegIndex;
import gov.nist.toolkit.simulators.sim.rep.RepIndex;
import gov.nist.toolkit.simulators.support.DsBaseActorSimulator;
import gov.nist.toolkit.simulators.support.SimCommon;
import gov.nist.toolkit.sitemanagement.SeparateSiteLoader;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.utilities.xml.OMFormatter;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.DefaultValidationContextFactory;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static gov.nist.toolkit.simulators.servlet.SimServlet.getRegIndex;
import static gov.nist.toolkit.simulators.servlet.SimServlet.getRepIndex;

/**
 *
 */
public class DoGet extends DoCommon {
    private static Logger logger = Logger.getLogger(DoPost.class);

    DoGet(ServletConfig config) {
        super(config);
    }

    void doGet(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;

        String uri = request.getRequestURI().toLowerCase();

        Addr addr = new Addr(request, response).parse(uri);
        if (addr == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (addr.isSim()) {
            MessageValidatorEngine mvc = new MessageValidatorEngine();
            try {

                // DB space for this simulator
                SimDb db = new SimDb(Installation.installation().simDbFile(), addr.simid, addr.actor, addr.transaction);
                request.setAttribute("SimDb", db);

                logRequest(request, db, addr.actor, addr.transaction);

                ValidationContext vc = DefaultValidationContextFactory.validationContext();

                SimulatorConfig asc = GenericSimulatorFactory.getSimConfig(Installation.installation().simDbFile(), addr.simid);

                SimCommon common = new SimCommon(db, uri.startsWith("https"), vc, mvc, response);

                ErrorRecorder er = new GwtErrorRecorderBuilder().buildNewErrorRecorder();
                er.sectionHeading("Endpoint");
                er.detail("Endpoint is " + uri);
                mvc.addErrorRecorder("**** HTTP Service: " + uri, er);

                DsBaseActorSimulator sim = (DsBaseActorSimulator) RuntimeManager.getSimulatorRuntime(addr.simid);

                sim.init(common, asc);
                sim.onTransactionBegin(asc);
                sim.run(addr.transactionType, mvc, addr.validation);
                sim.onTransactionEnd(asc);

            } catch (Exception e) {
                SoapFaultSender.sendSoapFault(response, ExceptionUtil.exception_details(e));
                logger.error(ExceptionUtil.exception_details(e));
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
            return;
        }

//        String uri = request.getRequestURI();
        logger.info("SIMSERVLET GET " + uri);
        String[] parts;
        try {
            int in = uri.indexOf("/del/");
            if (in != -1) {
                parts = uri.substring(in + "/del/".length()).split("\\/");
                handleDelete(response, parts);
                return;
            }
            in = uri.indexOf("/index/");
            if (in != -1) {
                parts = uri.substring(in + "/index/".length()).split("\\/");
                handleIndex(response, parts);
                return;
            }
            in = uri.indexOf("/message/");
            if (in != -1) {
                parts = uri.substring(in + "/message/".length()).split("\\/");
                handleMsgDownload(response, parts);
                return;
            }
            in = uri.indexOf("/siteconfig/");
            logger.info("siteconfig in is " + in);
            if (in != -1) {
                logger.info("working on siteconfig");
                parts = uri.substring(in + "/siteconfig/".length()).split("\\/");
                handleSiteDownload(response, parts);
                return;
            }
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        } catch (Exception e) {
            logger.error(ExceptionUtil.exception_details(e));
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
    }
	private void handleDelete(HttpServletResponse response, String[] parts) {
		String simid;
		String actor;
		String transaction;
		String message;

		try {
			simid = parts[0];
			actor = parts[1];
			transaction = parts[2];
			message = parts[3];
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		if (actor == null || actor.equals("null")) {
			try {
				SimDb sdb = new SimDb(Installation.installation().simDbFile(), new SimId(simid), null, null);
				actor = sdb.getActorsForSimulator().get(0);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NoSimException e) {
				e.printStackTrace();
			}
		}

		if (actor == null || actor.equals("null")) {
			logger.debug("No actor name found");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		SimDb db;
		try {
			db = new SimDb(Installation.installation().simDbFile(), new SimId(simid), actor, transaction);
		}
		catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		List<String> registryFilenames = db.getRegistryIds(simid, actor, transaction, message);
		List<String> registryUUIDs = new ArrayList<String>();
		for (String filename : registryFilenames) {
			registryUUIDs.add("urn:uuid:" + filename);
		}


		RegIndex regIndex = null;
		try {
			regIndex = getRegIndex(new SimId(simid));
		}
		catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		MetadataCollection mc = regIndex.mc;
		List<String> docUids = new ArrayList<String>();
		for (String id : registryUUIDs) {
			String uid = mc.deleteRo(id);
			if (uid != null)
				docUids.add(uid);
		}

		if (docUids.size() > 0) {
			RepIndex repIndex = null;
			try {
				repIndex = getRepIndex(new SimId(simid));
			}
			catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}

			for (String uid : docUids) {
				logger.debug("Delete document from index " + uid);
				repIndex.dc.delete(uid);
			}

		}


		logger.debug("Delete event " + simid + "/" + actor + "/" + transaction + "/" + message);
		File transEventFile = db.getTransactionEvent(simid, actor, transaction, message);
		db.delete(transEventFile);


		response.setStatus(HttpServletResponse.SC_OK);
	}

    // handle simulator message download
    private void handleMsgDownload(HttpServletResponse response, String[] parts) {
        String simid;
        String actor;
        String transaction;
        String message;


        try {
            simid       = parts[0];
            actor       = parts[1];
            transaction = parts[2];
            message     = parts[3];
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (actor == null || actor.equals("null")) {
            try {
                SimDb sdb = new SimDb(Installation.installation().simDbFile(), new SimId(simid), null, null);
                actor = sdb.getActorsForSimulator().get(0);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSimException e) {
                e.printStackTrace();
            }
        }

        if (actor == null || actor.equals("null")) {
            logger.debug("No actor name found");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        SimDb db;
        try {
            db = new SimDb(Installation.installation().simDbFile(), new SimId(simid), actor, transaction);
            response.setContentType("application/zip");
            db.getMessageLogZip(response.getOutputStream(), message);
            response.getOutputStream().close();
            response.addHeader("Content-Disposition", "attachment; filename=" + message + ".zip");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }

    // handle simulator message download
    private void handleSiteDownload(HttpServletResponse response, String[] parts) {
        String simid;

        try {
            simid       = parts[0];
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        logger.debug("site download of " + simid);

        if (simid == null || simid.trim().equals("")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        SimDb db;
        try {
            SimId simId = new SimId(simid);
            db = new SimDb(simId);
            SimulatorConfig config = db.getSimulator(simId);
            Site site = SimManager.getSite(config);
            OMElement siteEle = new SeparateSiteLoader().siteToXML(site);
            String siteString = new OMFormatter(siteEle).toString();
            logger.debug(siteString);

            response.setContentType("text/xml");
            response.getOutputStream().print(siteString);
            response.getOutputStream().close();
            response.addHeader("Content-Disposition", "attachment; filename=" + site.getName() + ".xml");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }


	// clean up index of an actor
	private void handleIndex(HttpServletResponse response, String[] parts) {
		String simid;
		String actor;
		String transaction;

		try {
			simid = parts[0];
			actor = parts[1];
			transaction = parts[2];
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		if (actor == null || actor.equals("null")) {
			try {
				SimDb sdb = new SimDb(Installation.installation().simDbFile(), new SimId(simid), null, null);
				actor = sdb.getActorsForSimulator().get(0);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NoSimException e) {
				e.printStackTrace();
			}
		}

		if (actor == null || actor.equals("null")) {
			logger.debug("No actor name found");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		SimDb db;
		try {
			db = new SimDb(Installation.installation().simDbFile(), new SimId(simid), actor, transaction);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		if (actor.equals("registry")) {
			RegIndex regIndex;
			try {
				regIndex = getRegIndex(new SimId(simid));
			}
			catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			MetadataCollection mc = regIndex.getMetadataCollection();

			// purge model in the index that are no longer present behind the index
			mc.purge();
		}

		response.setStatus(HttpServletResponse.SC_OK);
	}
}
