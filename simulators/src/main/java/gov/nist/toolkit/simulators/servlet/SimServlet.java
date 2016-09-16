package gov.nist.toolkit.simulators.servlet;

import gov.nist.toolkit.actorfactory.GenericSimulatorFactory;
import gov.nist.toolkit.actorfactory.PatientIdentityFeedServlet;
import gov.nist.toolkit.actorfactory.RuntimeManager;
import gov.nist.toolkit.actorfactory.SimDb;
import gov.nist.toolkit.actorfactory.client.NoSimException;
import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.simulators.sim.reg.store.RegIndex;
import gov.nist.toolkit.simulators.sim.rep.RepIndex;
import gov.nist.toolkit.simulators.support.DsBaseActorSimulator;
import gov.nist.toolkit.simulators.support.DsSimCommon;
import gov.nist.toolkit.valsupport.client.MessageValidationResults;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SimServlet  extends HttpServlet {
	static Logger logger = Logger.getLogger(SimServlet.class);

	private static final long serialVersionUID = 1L;

	static ServletConfig config;
//	File simDbDir;
	MessageValidationResults mvr;
	PatientIdentityFeedServlet patientIdentityFeedServlet;


	@Override
   public void init(ServletConfig sConfig) throws ServletException {
		super.init(sConfig);
		config = sConfig;
		logger.info("Initializing toolkit in SimServlet");
		File warHome = new File(config.getServletContext().getRealPath("/"));
		logger.info("...warHome is " + warHome);
		Installation.installation().warHome(warHome);
        logger.info("...warHome initialized to " + Installation.installation().warHome());

		Installation.installation().setServletContextName(getServletContext().getContextPath());


		patientIdentityFeedServlet = new PatientIdentityFeedServlet();
		patientIdentityFeedServlet.init(config);

		onServiceStart();

		logger.info("SimServlet initialized");
	}

	@Override
   public void destroy() {
		onServiceStop();
	}


	public MessageValidationResults getMessageValidationResults() {
		return mvr;
	}


	private HttpServletRequest request;
	private HttpServletResponse response;


	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		new DoGet(config).doGet(request, response);
	}

	@Override
   public void doPost(HttpServletRequest request, HttpServletResponse response) {
		new DoPost(config).doPost(request, response);
	}

	public static void onServiceStart()  {
		try {
			SimDb db = new SimDb();
			List<SimId> simIds = db.getAllSimIds();
			for (SimId simId : simIds) {
				DsBaseActorSimulator sim = (DsBaseActorSimulator) RuntimeManager.getSimulatorRuntime(simId);

				DsSimCommon dsSimCommon = null;
				SimulatorConfig asc = GenericSimulatorFactory.getSimConfig(db.getRoot(), simId);
				sim.init(dsSimCommon, asc);
				sim.onServiceStart(asc);
			}
		} catch (Exception e) {
			logger.fatal(ExceptionUtil.exception_details(e));
		}
	}

	public static void onServiceStop() {
		try {
			SimDb db = new SimDb();
			List<SimId> simIds = db.getAllSimIds();
			for (SimId simId : simIds) {
				DsBaseActorSimulator sim = (DsBaseActorSimulator) RuntimeManager.getSimulatorRuntime(simId);

				DsSimCommon dsSimCommon = null;
				SimulatorConfig asc = GenericSimulatorFactory.getSimConfig(db.getRoot(), simId);
				sim.init(dsSimCommon, asc);
				sim.onServiceStop(asc);
			}
		} catch (Exception e) {
			logger.fatal(ExceptionUtil.exception_details(e));
		}
	}

//	public static DsBaseActorSimulator getSimulatorRuntime(String simId) throws NoSimException, IOException, ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException {
//		SimDb db = new SimDb();
//		SimulatorConfig config = GenericSimulatorFactory.getSimConfig(db.getRoot(), simId);
//		String actorTypeName = config.getActorType();
//		ActorType actorType = ActorType.findActor(actorTypeName);
//		String actorSimClassName = actorType.getSimulatorClassName();
//		logger.info("Starting sim " + simId + " of class " + actorSimClassName);
//		Class<?> clas = Class.forName(actorSimClassName);
//
//		// find correct constructor - no parameters
//		Constructor<?>[] constructors = clas.getConstructors();
//		Constructor<?> constructor = null;
//		for (int i=0; i<constructors.length; i++) {
//			Constructor<?> cons = constructors[i];
//			Class<?>[] parmTypes = cons.getParameterTypes();
//			if (parmTypes.length != 0) continue;
////				if (!parmTypes[0].getSimpleName().equals(dsSimCommon.getClass().getSimpleName())) continue;
////				if (!parmTypes[1].getSimpleName().equals(asc.getClass().getSimpleName())) continue;
//			constructor = cons;
//		}
//		if (constructor == null)
//			throw new ToolkitRuntimeException("Cannot find correct constructor for " + actorSimClassName);
//		Object obj = constructor.newInstance();
//		if (!(obj instanceof DsBaseActorSimulator)) {
//			throw new ToolkitRuntimeException("Received message for actor type " + actorTypeName + " which has a handler/simulator that does not extend AbstractDsActorSimulator");
//		}
//		return (DsBaseActorSimulator) obj;
//	}



	// remove the index(s)
	static public void deleteSim(SimId simId) {
        if (config == null) return;
		ServletContext servletContext = config.getServletContext();
		servletContext.removeAttribute("Reg_" + simId);
		servletContext.removeAttribute("Rep_" + simId);
	}


	static public RegIndex getRegIndex(SimId simid) throws IOException, NoSimException {
		SimDb db = new SimDb(simid);
		ServletContext servletContext = config.getServletContext();
		String registryIndexFile = db.getRegistryIndexFile().toString();
		RegIndex regIndex;

		logger.info("GetRegIndex");
		synchronized(config) {
			regIndex = (RegIndex) servletContext.getAttribute("Reg_" + simid);
			if (regIndex == null) {
				logger.debug("Creating new RegIndex for " + simid);
				regIndex = new RegIndex(registryIndexFile, simid);
				regIndex.setSimDb(db);
				servletContext.setAttribute("Reg_" + simid, regIndex);
			} else
				logger.debug("Using cached RegIndex: " + simid + " db loc:" + regIndex.getSimDb().getRegistryIndexFile().toString());

			regIndex.cacheExpires = getNewExpiration();
		}

		return regIndex;
	}

	static public RepIndex getRepIndex(SimId simid) throws IOException, NoSimException {
		SimDb db = new SimDb(simid);
		ServletContext servletContext = config.getServletContext();
		String repositoryIndexFile = db.getRepositoryIndexFile().toString();
		RepIndex repIndex;

		logger.info("GetRepIndex");
		synchronized(config) {
			repIndex = (RepIndex) servletContext.getAttribute("Rep_" + simid);
			if (repIndex == null) {
				repIndex = new RepIndex(repositoryIndexFile, simid);
				servletContext.setAttribute("Rep_" + simid, repIndex);
			}

			repIndex.cacheExpires = getNewExpiration();
		}
		return repIndex;
	}

	static Calendar getNewExpiration() {
		// establish expiration for newly touched cache elements
		Date now = new Date();
		Calendar newExpiration = Calendar.getInstance();
		newExpiration.setTime(now);
		newExpiration.add(Calendar.MINUTE, 15);
		return newExpiration;
	}



}
