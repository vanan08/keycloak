package org.keycloak.models.jpa;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.keycloak.models.ApplicationModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ModuleModel;
import org.keycloak.models.OAuthClientModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RealmProvider;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserSubTypeModel;
import org.keycloak.models.UserTypeModel;
import org.keycloak.models.jpa.ApplicationModuleAdapter.ApplicationInfo;
import org.keycloak.models.jpa.entities.ApplicationEntity;
import org.keycloak.models.jpa.entities.ModuleEntity;
import org.keycloak.models.jpa.entities.OAuthClientEntity;
import org.keycloak.models.jpa.entities.RealmEntity;
import org.keycloak.models.jpa.entities.RoleEntity;
import org.keycloak.models.jpa.entities.UserSubTypeEntity;
import org.keycloak.models.jpa.entities.UserTypeEntity;
import org.keycloak.models.utils.KeycloakModelUtils;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JpaRealmProvider implements RealmProvider {
	private final KeycloakSession session;
	protected EntityManager em;

	public JpaRealmProvider(KeycloakSession session, EntityManager em) {
		this.session = session;
		this.em = em;
	}

	@Override
	public RealmModel createRealm(String name) {
		return createRealm(KeycloakModelUtils.generateId(), name);
	}

	@Override
	public RealmModel createRealm(String id, String name) {
		RealmEntity realm = new RealmEntity();
		realm.setName(name);
		realm.setId(id);
		em.persist(realm);
		em.flush();
		return new RealmAdapter(session, em, realm);
	}

	@Override
	public RealmModel getRealm(String id) {
		RealmEntity realm = em.find(RealmEntity.class, id);
		if (realm == null)
			return null;
		return new RealmAdapter(session, em, realm);
	}

	@Override
	public List<RealmModel> getRealms() {
		TypedQuery<RealmEntity> query = em.createNamedQuery("getAllRealms",
				RealmEntity.class);
		List<RealmEntity> entities = query.getResultList();
		List<RealmModel> realms = new ArrayList<RealmModel>();
		for (RealmEntity entity : entities) {
			realms.add(new RealmAdapter(session, em, entity));
		}
		return realms;
	}

	@Override
	public RealmModel getRealmByName(String name) {
		TypedQuery<RealmEntity> query = em.createNamedQuery("getRealmByName",
				RealmEntity.class);
		query.setParameter("name", name);
		List<RealmEntity> entities = query.getResultList();
		if (entities.size() == 0)
			return null;
		if (entities.size() > 1)
			throw new IllegalStateException(
					"Should not be more than one realm with same name");
		RealmEntity realm = query.getResultList().get(0);
		if (realm == null)
			return null;
		return new RealmAdapter(session, em, realm);
	}

	@Override
	public boolean removeRealm(String id) {
		RealmEntity realm = em.find(RealmEntity.class, id);
		if (realm == null) {
			return false;
		}

		RealmAdapter adapter = new RealmAdapter(session, em, realm);
		session.users().preRemove(adapter);
		for (ApplicationEntity a : new LinkedList<ApplicationEntity>(
				realm.getApplications())) {
			adapter.removeApplication(a.getId());
		}

		for (OAuthClientModel oauth : adapter.getOAuthClients()) {
			adapter.removeOAuthClient(oauth.getId());
		}

		em.remove(realm);
		return true;
	}

	@Override
	public void close() {
	}

	@Override
	public RoleModel getRoleById(String id, RealmModel realm) {
		RoleEntity entity = em.find(RoleEntity.class, id);
		if (entity == null)
			return null;
		if (!realm.getId().equals(entity.getRealmId()))
			return null;
		return new RoleAdapter(realm, em, entity);
	}

	@Override
	public ApplicationModel getApplicationById(String id, RealmModel realm) {
		ApplicationEntity app = em.find(ApplicationEntity.class, id);

		// Check if application belongs to this realm
		if (app == null || !realm.getId().equals(app.getRealm().getId()))
			return null;
		return new ApplicationAdapter(realm, em, session, app);
	}

	@Override
	public OAuthClientModel getOAuthClientById(String id, RealmModel realm) {
		OAuthClientEntity client = em.find(OAuthClientEntity.class, id);

		// Check if client belongs to this realm
		if (client == null || !realm.getId().equals(client.getRealm().getId()))
			return null;
		return new OAuthClientAdapter(realm, client, em);
	}

	@Override
	public ModuleModel getModuleByName(String name) {
		TypedQuery<ModuleEntity> query = em.createNamedQuery(
				"selectModuleByName", ModuleEntity.class);
		query.setParameter("name", name);

		List<ModuleEntity> modules = query.getResultList();

		if (modules == null || modules.size() == 0)
			return null;

		ApplicationInfo app = new ApplicationInfo(modules.get(0)
				.getApplication());

		return new ApplicationModuleAdapter(modules.get(0), app);
	}

	@Override
	public UserSubTypeModel getUserSubTypeById(String id, RealmModel realm) {
		UserSubTypeEntity entity = em.find(UserSubTypeEntity.class, id);
		if (entity == null)
			return null;
		return new UserSubTypeAdapter(realm, em, entity);
	}

    @Override
    public List<UserTypeModel> getUserTypes(RealmModel realm, String search, int firstResult, int maxResults) {
        TypedQuery<UserTypeEntity> query;
        
        if(null != search && !search.trim().isEmpty()){
        	query = em.createNamedQuery("searchUserTypesByName", UserTypeEntity.class);
        	query.setParameter("search", "%" + search.trim().toLowerCase() + "%");
        }else{
        	query = em.createNamedQuery("getAllUserType", UserTypeEntity.class);
        }
        
        if (firstResult != -1) {
            query.setFirstResult(firstResult);
        }
        if (maxResults != -1) {
            query.setMaxResults(maxResults);
        }
        
        List<UserTypeEntity> results = query.getResultList();
        List<UserTypeModel> userTypes = new ArrayList<UserTypeModel>();
        for (UserTypeEntity entity : results) userTypes.add(new UserTypeAdapter(realm, em, entity));
        return userTypes;
    }

	@Override
	public UserTypeModel getUserTypeById(String id, RealmModel realm) {
		UserTypeEntity entity = em.find(UserTypeEntity.class, id);
		if (entity == null)
			return null;
		return new UserTypeAdapter(realm, em, entity);
	}
	
    @Override
    public List<UserSubTypeModel> getUserSubTypes(RealmModel realm, String search, int firstResult, int maxResults) {
        TypedQuery<UserSubTypeEntity> query;
        
        if(null != search && !search.trim().isEmpty()){
        	query = em.createNamedQuery("searchUserSubTypesByName", UserSubTypeEntity.class);
        	query.setParameter("search", "%" + search.trim().toLowerCase() + "%");
        }else{
        	query = em.createNamedQuery("getAllUserSubType", UserSubTypeEntity.class);
        }
        
        if (firstResult != -1) {
            query.setFirstResult(firstResult);
        }
        if (maxResults != -1) {
            query.setMaxResults(maxResults);
        }
        
        List<UserSubTypeEntity> results = query.getResultList();
        List<UserSubTypeModel> userSubTypes = new ArrayList<UserSubTypeModel>();
        for (UserSubTypeEntity entity : results) userSubTypes.add(new UserSubTypeAdapter(realm, em, entity));
        return userSubTypes;
    }

}
