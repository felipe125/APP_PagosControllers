/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.polijic.APP_Pagos;

import co.edu.polijic.APP_Pagos.controllers.exceptions.NonexistentEntityException;
import co.edu.polijic.APP_Pagos.controllers.exceptions.PreexistingEntityException;
import co.edu.polijic.app_pagos.model.Tarjeta;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import co.edu.polijic.app_pagos.model.Transaccion;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author felipe
 */
public class TarjetaJpaController implements Serializable {

    public TarjetaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Tarjeta tarjeta) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Transaccion cdtransaccion = tarjeta.getCdtransaccion();
            if (cdtransaccion != null) {
                cdtransaccion = em.getReference(cdtransaccion.getClass(), cdtransaccion.getCdtransaccion());
                tarjeta.setCdtransaccion(cdtransaccion);
            }
            em.persist(tarjeta);
            if (cdtransaccion != null) {
                cdtransaccion.getTarjetaList().add(tarjeta);
                cdtransaccion = em.merge(cdtransaccion);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTarjeta(tarjeta.getCdtarjeta()) != null) {
                throw new PreexistingEntityException("Tarjeta " + tarjeta + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Tarjeta tarjeta) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Tarjeta persistentTarjeta = em.find(Tarjeta.class, tarjeta.getCdtarjeta());
            Transaccion cdtransaccionOld = persistentTarjeta.getCdtransaccion();
            Transaccion cdtransaccionNew = tarjeta.getCdtransaccion();
            if (cdtransaccionNew != null) {
                cdtransaccionNew = em.getReference(cdtransaccionNew.getClass(), cdtransaccionNew.getCdtransaccion());
                tarjeta.setCdtransaccion(cdtransaccionNew);
            }
            tarjeta = em.merge(tarjeta);
            if (cdtransaccionOld != null && !cdtransaccionOld.equals(cdtransaccionNew)) {
                cdtransaccionOld.getTarjetaList().remove(tarjeta);
                cdtransaccionOld = em.merge(cdtransaccionOld);
            }
            if (cdtransaccionNew != null && !cdtransaccionNew.equals(cdtransaccionOld)) {
                cdtransaccionNew.getTarjetaList().add(tarjeta);
                cdtransaccionNew = em.merge(cdtransaccionNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = tarjeta.getCdtarjeta();
                if (findTarjeta(id) == null) {
                    throw new NonexistentEntityException("The tarjeta with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Tarjeta tarjeta;
            try {
                tarjeta = em.getReference(Tarjeta.class, id);
                tarjeta.getCdtarjeta();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tarjeta with id " + id + " no longer exists.", enfe);
            }
            Transaccion cdtransaccion = tarjeta.getCdtransaccion();
            if (cdtransaccion != null) {
                cdtransaccion.getTarjetaList().remove(tarjeta);
                cdtransaccion = em.merge(cdtransaccion);
            }
            em.remove(tarjeta);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Tarjeta> findTarjetaEntities() {
        return findTarjetaEntities(true, -1, -1);
    }

    public List<Tarjeta> findTarjetaEntities(int maxResults, int firstResult) {
        return findTarjetaEntities(false, maxResults, firstResult);
    }

    private List<Tarjeta> findTarjetaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Tarjeta.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Tarjeta findTarjeta(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Tarjeta.class, id);
        } finally {
            em.close();
        }
    }

    public int getTarjetaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Tarjeta> rt = cq.from(Tarjeta.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
