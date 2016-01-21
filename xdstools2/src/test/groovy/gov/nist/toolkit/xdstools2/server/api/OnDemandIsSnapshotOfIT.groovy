package gov.nist.toolkit.xdstools2.server.api
import gov.nist.toolkit.actorfactory.SimManager
import gov.nist.toolkit.actorfactory.client.SimId
import gov.nist.toolkit.actorfactory.client.Simulator
import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.services.server.ClientApi
import gov.nist.toolkit.services.server.SimulatorApi
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.sitemanagement.client.Site
import gov.nist.toolkit.testengine.transactions.CallType
import spock.lang.Specification
/**
 * Runs all Registry tests.
 * To run:
 *    Start toolkit from IntelliJ.  This will establish the EC at a location something like
 *       /Users/bill/dev/toolkit2/xdstools2/target/test-classes/external_cache
 *    On startup this will be echoed in the log window
 *    Using the toolkit [Toolkit Configuration] tool, save this location as the external cache location
 *    Shutdown toolkit
 *    Start toolkit - this will recognize the EC location change
 *    Open Simulation Manager
 *    Select test session named mike (create it if it doesn't exist)
 *    Create a Registry simulator named reg - the full id will be mike__reg
 *    Come back to this file in IntelliJ and click right on the class name and select Run RegistrySelfTestIT
 *    All the self tests will run
 */
class OnDemandIsSnapshotOfIT extends Specification {
    ClientApi client
    ToolkitApi api
    SimulatorApi simApi
    String patientId = 'OD14^^^&1.2.460&ISO'

    String reg = 'reg'
    String regrep = 'rr'
    String odds = 'odds'

    SimId simIdReg = new SimId(reg)
    SimId simIdRepReg = new SimId(regrep)
    SimId simIdOdds = new SimId(odds)

    boolean tls = false

    // String testSession = 'sunil';
    Session testSession

    def setup() {
        when:
        client = new ClientApi()
        testSession = client.getSession()
        simApi = new SimulatorApi(testSession)

        //testSession = UnitTestEnvironmentManager.setupLocalToolkit()

//        SimulatorApi simApi = new SimulatorApi(testSession)

    }


    // submits the patient id configured above to the registry in a Patient Identity Feed transaction
    def 'Submit Pid to Reg sim'() {
        setup:
        simApi.delete(simIdReg)
        Simulator sim = simApi.create(ActorType.REGISTRY.name, simIdReg)
        TestInstance testId = new TestInstance("15804")

        when: 'Create site for simulator'
        Site site = SimManager.getSite(sim.configs.get(0))

        then: 'site exists'
        site

        when: 'Send transaction'
        Map<String, String> parms  = new HashMap<String, String>();
        parms.put('$patientid$', patientId);

        boolean status = client.runTest(testId, site, tls, parms, false, CallType.SOAP)

        then:
        status
    }


    def 'Run all tests'() {
        setup:
        simApi.delete(simIdRepReg)
        Simulator sim = simApi.create(ActorType.REPOSITORY_REGISTRY.name, simIdRepReg)
        TestInstance testId = new TestInstance("15806")

        when:
        Site site = SimManager.getSite(sim.configs.get(0))

        then:
        site

        when:
        Map<String, String> parms  = new HashMap<String, String>();
        parms.put('$patientid$', patientId);

        boolean status = client.runTest(testId, site, tls, parms, true, CallType.SOAP)

        then:
        status

    }

    /*
    def setup() {
        api = new ToolkitApi()
        println "EC is ${Installation.installation().externalCache().toString()}"

        api.createTestSession(testSession)
        api.deleteSimulatorIfItExists(simId)

        if (!api.simulatorExists(simId)) {
            println "Creating sim ${simId}"
            api.createSimulator(ActorType.REGISTRY, simId)
        }

        api.deleteSimulatorIfItExists(simRepReg)
        if (!api.simulatorExists(simRepReg)) {
            println "Creating sim ${simRepReg}"
            api.createSimulator(ActorType.REPOSITORY_REGISTRY, simRepReg)
        }

        api.deleteSimulatorIfItExists(simOdds)
        if (!api.simulatorExists(simOdds)) {
            println "Creating sim ${simOdds}"
            api.createSimulator(ActorType.ONDEMAND_DOCUMENT_SOURCE, simOdds)
        }
    }
    */


    // submits the patient id configured above to the registry in a Patient Identity Feed transaction
    /*
    def 'Submit Pid transaction to Registry simulator'() {
        when:
        String siteName = 'sunil__rr'
        TestInstance testId = new TestInstance("15804")
        List<String> sections = new ArrayList<>()
        sections.add("section")
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', patientId)
        boolean stopOnFirstError = true

        and: 'Run pid transaction test'
        List<Result> results = api.runTest(testSession, siteName, testId, sections, params, stopOnFirstError)

        then:
        true
        results.size() == 1
        results.get(0).passed()
    }

    def 'Run all tests'() {
        when:
        String siteName = 'sunil__rr'
        TestInstance testId = new TestInstance("15806")
        List<String> sections = new ArrayList<>()
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', patientId)
        boolean stopOnFirstError = true

        and: 'Run'
        List<Result> results = api.runTest(testSession, siteName, testId, sections, params, stopOnFirstError)

        then:
        true
        results.size() == 1
        results.get(0).passed()
    }
    */

    /**
     * This section is here, with the other reg/rep tests, because the Retrieve needs the document entry id and the repository id from the previous PnR section.
     * @return
     */
    /*
    def 'Run retrieve tests'() {
        when:
        String siteName = 'sunil__odds'
        TestInstance testId = new TestInstance("15806")
        List<String> sections = ["Retrieve"]
        Map<String, String> params = new HashMap<>()
        params.put('$patientid$', patientId)
        boolean stopOnFirstError = true

        and: 'Run'
        List<Result> results = api.runTest(testSession, siteName, testId, sections, params, stopOnFirstError)

        then:
        true
        results.size() == 1
        results.get(0).passed()
    }
    */

    /*
    def 'Run SQ test'() {

    }
    */

}
