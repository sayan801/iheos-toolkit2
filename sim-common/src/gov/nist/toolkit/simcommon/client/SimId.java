package gov.nist.toolkit.simcommon.client;

import gov.nist.toolkit.installation.Installation;

import java.io.File;
import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SimId implements IsSerializable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5119201016599189870L;
	String id;
	
	public SimId(String id) {
		setId(id);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = asDirectoryName(id);
	}

	String asDirectoryName(String id) {
		return id.replaceAll("\\.", "_");
	}
	
	public File getRoot() {
		return new File(getDbRoot().toString()  /*.getAbsolutePath()*/ + File.separatorChar + id);
	}
	
	static File getDbRoot() {
		return Installation.installation().simDbFile();
	}

	public String toString() {
		return id;
	}
	
	public boolean equals(SimId otherId) {
		return otherId != null && otherId.id != null && otherId.id.equals(id);
	}
}
