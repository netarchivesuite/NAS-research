package dk.netarkivet.research.webinterface;

import dk.netarkivet.common.distribute.monitorregistry.MonitorRegistryClient;
import dk.netarkivet.common.utils.JMXUtils;

public class PrintMonitorRegistryClient implements MonitorRegistryClient {
    /**
     * Simply print info given in constructor to stdout.
     *
     * @param hostName Name of host you can monitor this application on.
     * @param jmxPort JMX port you can monitor this application on.
     * @param rmiPort RMI port communication will happen on.
     */
    public void register(String hostName, int jmxPort, int rmiPort) {
        System.out.println("This client may be monitored on '" + JMXUtils.getUrl(hostName, jmxPort, rmiPort) + "'");
    }
}
