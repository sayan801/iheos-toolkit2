package gov.nist.toolkit.toolkitServicesCommon.resource;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 */
@XmlRootElement
public class ErrorMappingResource {
    String transaction;
    String error;
    String pid;

    public ErrorMappingResource() {}

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }
    public String toString() {
        return String.format("%s :  %s ==> %s", transaction, pid, error);
    }
}
