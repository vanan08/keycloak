package org.keycloak.models.cache;

import org.keycloak.models.ApplicationModel;
import org.keycloak.models.CustomUserModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ModuleModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleContainerModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserCredentialValueModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserTypeModel;
import org.keycloak.models.cache.entities.CachedUser;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class UserAdapter implements UserModel {
    protected UserModel updated;
    protected CachedUser cached;
    protected CacheUserProvider userProviderCache;
    protected KeycloakSession keycloakSession;
    protected RealmModel realm;

    public UserAdapter(CachedUser cached, CacheUserProvider userProvider, KeycloakSession keycloakSession, RealmModel realm) {
        this.cached = cached;
        this.userProviderCache = userProvider;
        this.keycloakSession = keycloakSession;
        this.realm = realm;
    }

    protected void getDelegateForUpdate() {
        if (updated == null) {
            userProviderCache.registerUserInvalidation(realm, getId());
            updated = userProviderCache.getDelegate().getUserById(getId(), realm);
            if (updated == null) throw new IllegalStateException("Not found in database");
        }
    }
    @Override
    public String getId() {
        if (updated != null) return updated.getId();
        return cached.getId();
    }

    @Override
    public String getUsername() {
        if (updated != null) return updated.getUsername();
        return cached.getUsername();
    }

    @Override
    public void setUsername(String username) {
        getDelegateForUpdate();
        updated.setUsername(username);
    }

    @Override
    public boolean isEnabled() {
        if (updated != null) return updated.isEnabled();
        return cached.isEnabled();
    }

    @Override
    public boolean isTotp() {
        if (updated != null) return updated.isTotp();
        return cached.isTotp();
    }

    @Override
    public void setEnabled(boolean enabled) {
        getDelegateForUpdate();
        updated.setEnabled(enabled);
    }
    
    @Override
    public String getMobile() {
        getDelegateForUpdate();
        return updated.getMobile();
    }

    @Override
    public void setAttribute(String name, String value) {
        getDelegateForUpdate();
        updated.setAttribute(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        getDelegateForUpdate();
        updated.removeAttribute(name);
    }

    @Override
    public String getAttribute(String name) {
        if (updated != null) return updated.getAttribute(name);
        return cached.getAttributes().get(name);
    }

    @Override
    public Map<String, String> getAttributes() {
        if (updated != null) return updated.getAttributes();
        return cached.getAttributes();
    }

    @Override
    public Set<RequiredAction> getRequiredActions() {
        if (updated != null) return updated.getRequiredActions();
        return cached.getRequiredActions();
    }

    @Override
    public void addRequiredAction(RequiredAction action) {
        getDelegateForUpdate();
        updated.addRequiredAction(action);
    }

    @Override
    public void removeRequiredAction(RequiredAction action) {
        getDelegateForUpdate();
        updated.removeRequiredAction(action);
    }

    @Override
    public String getFirstName() {
        if (updated != null) return updated.getFirstName();
        return cached.getFirstName();
    }

    @Override
    public void setFirstName(String firstName) {
        getDelegateForUpdate();
        updated.setFirstName(firstName);
    }

    @Override
    public String getLastName() {
        if (updated != null) return updated.getLastName();
        return cached.getLastName();
    }

    @Override
    public void setLastName(String lastName) {
        getDelegateForUpdate();
        updated.setLastName(lastName);
    }

    @Override
    public String getEmail() {
        if (updated != null) return updated.getEmail();
        return cached.getEmail();
    }

    @Override
    public void setEmail(String email) {
        getDelegateForUpdate();
        updated.setEmail(email);
    }

    @Override
    public boolean isEmailVerified() {
        if (updated != null) return updated.isEmailVerified();
        return cached.isEmailVerified();
    }

    @Override
    public void setEmailVerified(boolean verified) {
        getDelegateForUpdate();
        updated.setEmailVerified(verified);
    }

    @Override
    public void setTotp(boolean totp) {
        getDelegateForUpdate();
        updated.setTotp(totp);
    }

    @Override
    public void updateCredential(UserCredentialModel cred) {
        getDelegateForUpdate();
        updated.updateCredential(cred);
    }

    @Override
    public List<UserCredentialValueModel> getCredentialsDirectly() {
        if (updated != null) return updated.getCredentialsDirectly();
        return cached.getCredentials();
    }

    @Override
    public void updateCredentialDirectly(UserCredentialValueModel cred) {
        getDelegateForUpdate();
        updated.updateCredentialDirectly(cred);
    }

    @Override
    public String getFederationLink() {
        if (updated != null) return updated.getFederationLink();
        return cached.getFederationLink();
    }

    @Override
    public void setFederationLink(String link) {
        getDelegateForUpdate();
        updated.setFederationLink(link);
   }

    @Override
    public Set<RoleModel> getRealmRoleMappings() {
        if (updated != null) return updated.getRealmRoleMappings();
        Set<RoleModel> roleMappings = getRoleMappings();
        Set<RoleModel> realmMappings = new HashSet<RoleModel>();
        for (RoleModel role : roleMappings) {
            RoleContainerModel container = role.getContainer();
            if (container instanceof RealmModel) {
                if (((RealmModel) container).getId().equals(realm.getId())) {
                    realmMappings.add(role);
                }
            }
        }
        return realmMappings;
    }

    @Override
    public Set<RoleModel> getApplicationRoleMappings(ApplicationModel app) {
        if (updated != null) return updated.getApplicationRoleMappings(app);
        Set<RoleModel> roleMappings = getRoleMappings();
        Set<RoleModel> appMappings = new HashSet<RoleModel>();
        for (RoleModel role : roleMappings) {
            RoleContainerModel container = role.getContainer();
            if (container instanceof ApplicationModel) {
                if (((ApplicationModel) container).getId().equals(app.getId())) {
                    appMappings.add(role);
                }
            }
        }
        return appMappings;
    }
    
    @Override
    public Set<RoleModel> getModuleRoleMappings(ModuleModel module) {
    	if (updated != null) return updated.getModuleRoleMappings(module);
    	Set<RoleModel> modMappings = new HashSet<RoleModel>();
        Set<RoleModel> rolesModule = module.getRoles(cached.getId());
        for (RoleModel role : rolesModule) {
        	if (hasRole(role)) {
        		modMappings.add(role);
        	}
        }
        return modMappings;
    }

    @Override
    public boolean hasRole(RoleModel role) {
        if (updated != null) return updated.hasRole(role);
        if (cached.getRoleMappings().contains(role.getId())) return true;

        Set<RoleModel> mappings = getRoleMappings();
        for (RoleModel mapping: mappings) {
           if (mapping.hasRole(role)) return true;
        }
        return false;
    }

    @Override
    public void grantRole(RoleModel role) {
        getDelegateForUpdate();
        updated.grantRole(role);
    }

    @Override
    public Set<RoleModel> getRoleMappings() {
        if (updated != null) return updated.getRoleMappings();
        Set<RoleModel> roles = new HashSet<RoleModel>();
        for (String id : cached.getRoleMappings()) {
            RoleModel roleById = keycloakSession.realms().getRoleById(id, realm);
            if (roleById == null) {
                // chance that role was removed, so just delete to persistence and get user invalidated
                getDelegateForUpdate();
                return updated.getRoleMappings();
            }
            roles.add(roleById);

        }
        return roles;
    }

    @Override
    public void deleteRoleMapping(RoleModel role) {
        getDelegateForUpdate();
        updated.deleteRoleMapping(role);
    }

	@Override
	public void setMobile(String mobile) {
		getDelegateForUpdate();
		updated.setMobile(mobile);
	}
	
	@Override
	public List<CustomUserModel> getCustomUsers() {
		getDelegateForUpdate();
		return updated.getCustomUsers();
	}

	@Override
	public CustomUserModel addCustomUser(String acceptedTNC) {
		getDelegateForUpdate();
		return updated.addCustomUser(acceptedTNC);
	}

	@Override
	public void updateCustomUser(CustomUserModel customUserModel) {
		getDelegateForUpdate();
		updated.updateCustomUser(customUserModel);
	}


	@Override
	public String getCustomUserSubTypeId() {
		return updated.getCustomUserSubTypeId();
	}

	@Override
	public void setCustomUserSubTypeId(String customUserSubTypeId) {
		updated.setCustomUserSubTypeId(customUserSubTypeId);
	}

	@Override
	public String getNeed2FA() {
		getDelegateForUpdate();
		return updated.getNeed2FA();
	}

	@Override
	public void setNeed2FA(String need2fa) {
		getDelegateForUpdate();
		updated.setNeed2FA(need2fa);
	}

	@Override
	public String getNeedTNC() {
		getDelegateForUpdate();
		return updated.getNeedTNC();
	}

	@Override
	public void setNeedTNC(String needTNC) {
		getDelegateForUpdate();
		updated.setNeedTNC(needTNC);		
	}

	@Override
	public String getAccountStatus() {
		return updated.getAccountStatus();
	}

	@Override
	public void setAccountStatus(String accountStatus) {
		updated.setAccountStatus(accountStatus);
	}

	@Override
	public String getAgentCode() {
		return updated.getAgentCode();
	}

	@Override
	public void setAgentCode(String agentCode) {
		updated.setAgentCode(agentCode);
	}

	@Override
	public String getAgency() {
		return updated.getAgency();
	}

	@Override
	public void setAgency(String agency) {
		updated.setAgency(agency);
	}

	@Override
	public UserTypeModel getCustomUserType() {
		getDelegateForUpdate();
		return updated.getCustomUserType();
	}

	@Override
	public void setCustomUserType(UserTypeModel customUserType) {
		getDelegateForUpdate();
		updated.setCustomUserType(customUserType);
	}
    
}
