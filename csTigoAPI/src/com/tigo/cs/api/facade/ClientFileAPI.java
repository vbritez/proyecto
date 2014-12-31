package com.tigo.cs.api.facade;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.tigo.cs.commons.jpa.EmptyResultException;
import com.tigo.cs.commons.jpa.MoreThanOneResultException;
import com.tigo.cs.domain.Client;
import com.tigo.cs.domain.ClientFile;

public abstract class ClientFileAPI extends AbstractAPI<ClientFile> {

    public ClientFileAPI() {
        super(ClientFile.class);
    }

    @Override
    public void remove(ClientFile entity) {
        super.remove(find(entity.getClientFileCod(), true));
    }

    public ClientFile getClientLogo(Client client) {
        EntityManager em = null;
        try {
            em = getFacadeContainer().getEntityManager();
            Query q = em.createQuery("SELECT c FROM ClientFile c "
                + "WHERE c.client = :client "
                + "AND c.filenameChr = ( SELECT p.valueChr FROM ClientParameterValue p "
                + "                     WHERE p.clientParameterValuePK.codClient = :codClient "
                + "                     AND p.clientParameterValuePK.codClientParameter = 'client.image.logo' )");
            q.setParameter("client", client);
            q.setParameter("codClient", client.getClientCod());
            return (ClientFile) q.getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            if (em != null && getFacadeContainer().isEntityManagerTransactional()) {
                em.close();
            }
        }

    }

    public ClientFile getClientLogo(Client client, String fileName) {
        ClientFile cf = null;
        try {
            finderParams = prepareParams();
            finderParams.put("client", client);
            finderParams.put("fileName", fileName);
            cf = findEntityWithNamedQuery("ClientFile.findByClientFile",
                    finderParams);

        } catch (EmptyResultException ex) {
            return null;
        } catch (MoreThanOneResultException ex) {
            return null;
        }
        return cf;
    }

    public String getClientLogoDescription(Client client, String fileName) {
        ClientFile cf = null;
        String description = null;
        try {
            finderParams = prepareParams();
            finderParams.put("client", client);
            finderParams.put("fileName", fileName);
            cf = findEntityWithNamedQuery("ClientFile.findByClientFile",
                    finderParams);
            description = cf != null ? cf.getDescriptionChr() : fileName;
        } catch (EmptyResultException ex) {
            return null;
        } catch (MoreThanOneResultException ex) {
            return null;
        }
        return description;
    }
}
