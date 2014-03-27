package gov.nist.toolkit.directsim.client;

import java.util.HashSet;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DirectRegistrationData  implements IsSerializable  {
	// From address for Direct requests
	// All the real info is in the Contact, this exists
	// for quick validation that the Direct addr
	// should be accepted on the interface 
	// file exists -> ok
	public String directAddr;
	public HashSet<String> contactAddr;
	
	public DirectRegistrationData() {
		
	}
	
	public DirectRegistrationData(String direct, HashSet<String> contact) {
		directAddr = direct;
		contactAddr = new HashSet<String>();
		contactAddr = contact;
	}
	
}
