package org.keycloak.models.cache;

import org.keycloak.models.RealmModel;
import org.keycloak.models.UserTypeContainerModel;
import org.keycloak.models.UserTypeModel;
import org.keycloak.models.cache.entities.CachedUserType;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class UserTypeAdapter implements UserTypeModel {

    protected UserTypeModel updated;
    protected CachedUserType cached;
    protected RealmCache cache;
    protected CacheRealmProvider cacheSession;
    protected RealmModel realm;

    public UserTypeAdapter(CachedUserType cached, RealmCache cache, CacheRealmProvider session, RealmModel realm) {
        this.cached = cached;
        this.cache = cache;
        this.cacheSession = session;
        this.realm = realm;
    }

    protected void getDelegateForUpdate() {
        if (updated == null) {
            cacheSession.registerUserTypeInvalidation(getId());
            updated = cacheSession.getDelegate().getUserTypeById(getId(), realm);
            if (updated == null) throw new IllegalStateException("Not found in database");
        }
    }

    @Override
    public String getName() {
        if (updated != null) return updated.getName();
        return cached.getName();
    }

    @Override
    public void setName(String name) {
        getDelegateForUpdate();
        updated.setName(name);
        realm.refreshRealmUserTypesCache(getId(), name);
    }
    
	@Override
	public byte[] getTncContent() {
		if (updated != null) return updated.getTncContent();
	    return cached.getTncContent();
	}

	@Override
	public String getUserTypeRole() {
		if (updated != null) return updated.getUserTypeRole();
		return cached.getUserTypeRole();
	}

	@Override
	public void setTncContent(byte[] tncContent) {
		getDelegateForUpdate();
        updated.setTncContent(tncContent);
	}

	@Override
	public void setUserTypeRole(String userTypeRole) {
		getDelegateForUpdate();
		updated.setUserTypeRole(userTypeRole);
	}
	
	@Override
	public String getRedirectUrl() {
		if (updated != null) return updated.getRedirectUrl();
		return cached.getRedirectUrl();
	}

	@Override
	public void setRedirectUrl(String redirectUrl) {
		getDelegateForUpdate();
		updated.setRedirectUrl(redirectUrl);
	}
	
    @Override
    public String getId() {
        if (updated != null) return updated.getId();
        return cached.getId();
    }

    @Override
    public UserTypeContainerModel getContainer() {
        return realm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof UserTypeModel)) return false;

        UserTypeModel that = (UserTypeModel) o;
        return that.getId().equals(getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }


}
