package gov.nist.toolkit.simcommon.server

import gov.nist.toolkit.installation.server.ExternalCacheManager
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException

/**
 * Init EC for testing
 */
class InitEC {

    static init() {
//        org.apache.log4j.BasicConfigurator.configure()
//        System.out.println("*** pr classpath " + System.getProperty("java.class.path") )
//        System.out.println("*** getClass() ***" + InitEC.class.getClassLoader().getResource("/").toString())
//        System.out.println("*** getClass() 2 ***" + InitEC.class.getResource("/").toString())
        URL externalCacheMarker = InitEC.class.getResource('/external_cache/external_cache.txt')
        if (externalCacheMarker == null) {
            throw new ToolkitRuntimeException("Cannot locate external cache for test environment")
        }
        File externalCache = new File(externalCacheMarker.toURI().path).parentFile

        // Important to set this before war home since it is overriding contents of toolkit.properties
        if (!externalCache || !externalCache.isDirectory())throw new ToolkitRuntimeException('External Cache not found')
        ExternalCacheManager.reinitialize(externalCache)

    }
}
