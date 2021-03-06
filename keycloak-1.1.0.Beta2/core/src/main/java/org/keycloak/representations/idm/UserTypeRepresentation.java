package org.keycloak.representations.idm;


public class UserTypeRepresentation {
	protected String id;
    protected String name;
    protected byte[] tncContent;
    protected String userTypeRole;
    protected String redirectUrl;
    
    public UserTypeRepresentation() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
	public byte[] getTncContent() {
		return tncContent;
	}

	public void setTncContent(byte[] tncContent) {
		this.tncContent = tncContent;
	}
	
	public String getUserTypeRole() {
		return userTypeRole;
	}

	public void setUserTypeRole(String userTypeRole) {
		this.userTypeRole = userTypeRole;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	@Override
    public String toString() {
        return name;
    }
}
