/**
 * 
 */
package gov.nist.toolkit.simcommon.server.factories;

import gov.nist.toolkit.actortransaction.shared.ActorType;
import gov.nist.toolkit.actortransaction.client.ParamType;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.client.Simulator;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.server.AbstractActorFactory;
import gov.nist.toolkit.simcommon.server.IActorFactory;
import gov.nist.toolkit.simcommon.server.SimManager;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.TransactionBean;
import gov.nist.toolkit.sitemanagement.client.TransactionBean.RepositoryType;
import gov.nist.toolkit.xdsexception.NoSimulatorException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Creates Responding Imaging Gateway Simulator
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
public class RigActorFactory extends AbstractActorFactory implements IActorFactory {
   SimId newID = null;

   static final String homeCommunityIdBase = "urn:oid:1.1.4567334.101.";
   static int homeCommunityIdIncr = 1;

   static String getNewHomeCommunityId() {
      return homeCommunityIdBase + homeCommunityIdIncr++ ;
   }

   static final List <TransactionType> incomingTransactions =
      Arrays.asList(TransactionType.XC_RET_IMG_DOC_SET);

   @Override
   public Simulator buildNew(SimManager simm, @SuppressWarnings("hiding") SimId newID, String environment,
                             boolean configureBase)
           throws Exception {
      this.newID = newID;
      ActorType actorType = ActorType.RESPONDING_IMAGING_GATEWAY;
      SimulatorConfig sc;
      if (configureBase) sc = configureBaseElements(actorType, newID, newID.getTestSession(), environment);
      else sc = new SimulatorConfig();

      addEditableConfig(sc, SimulatorProperties.homeCommunityId, ParamType.TEXT,
         getNewHomeCommunityId());

      addFixedEndpoint(sc, SimulatorProperties.xcirEndpoint, actorType,
         TransactionType.XC_RET_IMG_DOC_SET, false);
      addFixedEndpoint(sc, SimulatorProperties.xcirTlsEndpoint, actorType,
         TransactionType.XC_RET_IMG_DOC_SET, true);
      addEditableConfig(sc, SimulatorProperties.imagingDocumentSources, 
         ParamType.SELECTION, new ArrayList<String>(), true);

      return new Simulator(sc);
   }

   @Override
   protected void verifyActorConfigurationOptions(SimulatorConfig sc) {
   }

   @Override
   public Site buildActorSite(SimulatorConfig sc, Site site)
      throws NoSimulatorException {

      if (sc == null || sc.isExpired())
         throw new NoSimulatorException("Expired");

      try {
         String siteName = sc.getDefaultName();

         if (site == null) site = new Site(siteName, sc.getId().getTestSession());
         site.setTestSession(sc.getId().getTestSession()); // labels this site as coming from a sim

         boolean isAsync = false;

         site.addTransaction(new TransactionBean(
            TransactionType.XC_RET_IMG_DOC_SET.getCode(), RepositoryType.NONE,
            sc.get(SimulatorProperties.xcirEndpoint).asString(), false,
            isAsync));
         site.addTransaction(new TransactionBean(
            TransactionType.XC_RET_IMG_DOC_SET.getCode(), RepositoryType.NONE,
            sc.get(SimulatorProperties.xcirTlsEndpoint).asString(), true,
            isAsync));

         site.setHome(sc.get(SimulatorProperties.homeCommunityId).asString());
         return site;
      } catch (Throwable t) {
         sc.isExpired(true);
         throw new NoSimulatorException("Not Defined", t);
      }
   }

   @Override
   public List <TransactionType> getIncomingTransactions() {
      return incomingTransactions;
   }

   @Override
   public ActorType getActorType() {
      return ActorType.RESPONDING_IMAGING_GATEWAY;
   }
}
