/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.polijic.APP_Pagos;

import co.edu.polijic.APP_Pagos.controllers.exceptions.IllegalOrphanException;
import co.edu.polijic.APP_Pagos.controllers.exceptions.NonexistentEntityException;
import co.edu.polijic.APP_Pagos.controllers.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import co.edu.polijic.app_pagos.model.TipoPago;
import co.edu.polijic.app_pagos.model.RegistroTransaccion;
import java.util.ArrayList;
import java.util.List;
import co.edu.polijic.app_pagos.model.Tarjeta;
import co.edu.polijic.app_pagos.model.Transaccion;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author felipe
 */
public class TransaccionJpaController implements Serializable {

    public TransaccionJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Transaccion transaccion) throws PreexistingEntityException, Exception {
        if (transaccion.getRegistroTransaccionList() == null) {
            transaccion.setRegistroTransaccionList(new ArrayList<RegistroTransaccion>());
        }
        if (transaccion.getTarjetaList() == null) {
            transaccion.setTarjetaList(new ArrayList<Tarjeta>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TipoPago cdtipopago = transaccion.getCdtipopago();
                if (cdtipopago != null) {
                cdtipopago = em.getReference(cdtipopago.getClass(), cdtipopago.getCdtipopago());
                transaccion.setCdtipopago(cdtipopago);
            }
            List<RegistroTransaccion> attachedRegistroTransaccionList = new ArrayList<RegistroTransaccion>();
            for (RegistroTransaccion registroTransaccionListRegistroTransaccionToAttach : transaccion.getRegistroTransaccionList()) {
                registroTransaccionListRegistroTransaccionToAttach = em.getReference(registroTransaccionListRegistroTransaccionToAttach.getClass(), registroTransaccionListRegistroTransaccionToAttach.getCdregistro());
                attachedRegistroTransaccionList.add(registroTransaccionListRegistroTransaccionToAttach);
            }
            transaccion.setRegistroTransaccionList(attachedRegistroTransaccionList);
            List<Tarjeta> attachedTarjetaList = new ArrayList<Tarjeta>();
            for (Tarjeta tarjetaListTarjetaToAttach : transaccion.getTarjetaList()) {
                tarjetaListTarjetaToAttach = em.getReference(tarjetaListTarjetaToAttach.getClass(), tarjetaListTarjetaToAttach.getCdtarjeta());
                attachedTarjetaList.add(tarjetaListTarjetaToAttach);
            }
            transaccion.setTarjetaList(attachedTarjetaList);
            em.persist(transaccion);
            if (cdtipopago != null) {
                cdtipopago.getTransaccionList().add(transaccion);
                cdtipopago = em.merge(cdtipopago);
            }
            for (RegistroTransaccion registroTransaccionListRegistroTransaccion : transaccion.getRegistroTransaccionList()) {
                Transaccion oldCdtransaccionOfRegistroTransaccionListRegistroTransaccion = registroTransaccionListRegistroTransaccion.getCdtransaccion();
                registroTransaccionListRegistroTransaccion.setCdtransaccion(transaccion);
                registroTransaccionListRegistroTransaccion = em.merge(registroTransaccionListRegistroTransaccion);
                if (oldCdtransaccionOfRegistroTransaccionListRegistroTransaccion != null) {
                    oldCdtransaccionOfRegistroTransaccionListRegistroTransaccion.getRegistroTransaccionList().remove(registroTransaccionListRegistroTransaccion);
                    oldCdtransaccionOfRegistroTransaccionListRegistroTransaccion = em.merge(oldCdtransaccionOfRegistroTransaccionListRegistroTransaccion);
                }
            }
            for (Tarjeta tarjetaListTarjeta : transaccion.getTarjetaList()) {
                Transaccion oldCdtransaccionOfTarjetaListTarjeta = tarjetaListTarjeta.getCdtransaccion();
                tarjetaListTarjeta.setCdtransaccion(transaccion);
                tarjetaListTarjeta = em.merge(tarjetaListTarjeta);
                if (oldCdtransaccionOfTarjetaListTarjeta != null) {
                    oldCdtransaccionOfTarjetaListTarjeta.getTarjetaList().remove(tarjetaListTarjeta);
                    oldCdtransaccionOfTarjetaListTarjeta = em.merge(oldCdtransaccionOfTarjetaListTarjeta);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTransaccion(transaccion.getCdtransaccion()) != null) {
                throw new PreexistingEntityException("Transaccion " + transaccion + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Transaccion transaccion) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Transaccion persistentTransaccion = em.find(Transaccion.class, transaccion.getCdtransaccion());
            TipoPago cdtipopagoOld = persistentTransaccion.getCdtipopago();
            TipoPago cdtipopagoNew = transaccion.getCdtipopago();
            List<RegistroTransaccion> registroTransaccionListOld = persistentTransaccion.getRegistroTransaccionList();
            List<RegistroTransaccion> registroTransaccionListNew = transaccion.getRegistroTransaccionList();
            List<Tarjeta> tarjetaListOld = persistentTransaccion.getTarjetaList();
            List<Tarjeta> tarjetaListNew = transaccion.getTarjetaList();
            List<String> illegalOrphanMessages = null;
            for (RegistroTransaccion registroTransaccionListOldRegistroTransaccion : registroTransaccionListOld) {
                if (!registroTransaccionListNew.contains(registroTransaccionListOldRegistroTransaccion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain RegistroTransaccion " + registroTransaccionListOldRegistroTransaccion + " since its cdtransaccion field is not nullable.");
                }
            }
            for (Tarjeta tarjetaListOldTarjeta : tarjetaListOld) {
                if (!tarjetaListNew.contains(tarjetaListOldTarjeta)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Tarjeta " + tarjetaListOldTarjeta + " since its cdtransaccion field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (cdtipopagoNew != null) {
                cdtipopagoNew = em.getReference(cdtipopagoNew.getClass(), cdtipopagoNew.getCdtipopago());
                transaccion.setCdtipopago(cdtipopagoNew);
            }
            List<RegistroTransaccion> attachedRegistroTransaccionListNew = new ArrayList<RegistroTransaccion>();
            for (RegistroTransaccion registroTransaccionListNewRegistroTransaccionToAttach : registroTransaccionListNew) {
                registroTransaccionListNewRegistroTransaccionToAttach = em.getReference(registroTransaccionListNewRegistroTransaccionToAttach.getClass(), registroTransaccionListNewRegistroTransaccionToAttach.getCdregistro());
                attachedRegistroTransaccionListNew.add(registroTransaccionListNewRegistroTransaccionToAttach);
            }
            registroTransaccionListNew = attachedRegistroTransaccionListNew;
            transaccion.setRegistroTransaccionList(registroTransaccionListNew);
            List<Tarjeta> attachedTarjetaListNew = new ArrayList<Tarjeta>();
            for (Tarjeta tarjetaListNewTarjetaToAttach : tarjetaListNew) {
                tarjetaListNewTarjetaToAttach = em.getReference(tarjetaListNewTarjetaToAttach.getClass(), tarjetaListNewTarjetaToAttach.getCdtarjeta());
                attachedTarjetaListNew.add(tarjetaListNewTarjetaToAttach);
            }
            tarjetaListNew = attachedTarjetaListNew;
            transaccion.setTarjetaList(tarjetaListNew);
            transaccion = em.merge(transaccion);
            if (cdtipopagoOld != null && !cdtipopagoOld.equals(cdtipopagoNew)) {
                cdtipopagoOld.getTransaccionList().remove(transaccion);
                cdtipopagoOld = em.merge(cdtipopagoOld);
            }
            if (cdtipopagoNew != null && !cdtipopagoNew.equals(cdtipopagoOld)) {
                cdtipopagoNew.getTransaccionList().add(transaccion);
                cdtipopagoNew = em.merge(cdtipopagoNew);
            }
            for (RegistroTransaccion registroTransaccionListNewRegistroTransaccion : registroTransaccionListNew) {
                if (!registroTransaccionListOld.contains(registroTransaccionListNewRegistroTransaccion)) {
                    Transaccion oldCdtransaccionOfRegistroTransaccionListNewRegistroTransaccion = registroTransaccionListNewRegistroTransaccion.getCdtransaccion();
                    registroTransaccionListNewRegistroTransaccion.setCdtransaccion(transaccion);
                    registroTransaccionListNewRegistroTransaccion = em.merge(registroTransaccionListNewRegistroTransaccion);
                    if (oldCdtransaccionOfRegistroTransaccionListNewRegistroTransaccion != null && !oldCdtransaccionOfRegistroTransaccionListNewRegistroTransaccion.equals(transaccion)) {
                        oldCdtransaccionOfRegistroTransaccionListNewRegistroTransaccion.getRegistroTransaccionList().remove(registroTransaccionListNewRegistroTransaccion);
                        oldCdtransaccionOfRegistroTransaccionListNewRegistroTransaccion = em.merge(oldCdtransaccionOfRegistroTransaccionListNewRegistroTransaccion);
                    }
                }
            }
            for (Tarjeta tarjetaListNewTarjeta : tarjetaListNew) {
                if (!tarjetaListOld.contains(tarjetaListNewTarjeta)) {
                    Transaccion oldCdtransaccionOfTarjetaListNewTarjeta = tarjetaListNewTarjeta.getCdtransaccion();
                    tarjetaListNewTarjeta.setCdtransaccion(transaccion);
                    tarjetaListNewTarjeta = em.merge(tarjetaListNewTarjeta);
                    if (oldCdtransaccionOfTarjetaListNewTarjeta != null && !oldCdtransaccionOfTarjetaListNewTarjeta.equals(transaccion)) {
                        oldCdtransaccionOfTarjetaListNewTarjeta.getTarjetaList().remove(tarjetaListNewTarjeta);
                        oldCdtransaccionOfTarjetaListNewTarjeta = em.merge(oldCdtransaccionOfTarjetaListNewTarjeta);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = transaccion.getCdtransaccion();
                if (findTransaccion(id) == null) {
                    throw new NonexistentEntityException("The transaccion with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Transaccion transaccion;
            try {
                transaccion = em.getReference(Transaccion.class, id);
                transaccion.getCdtransaccion();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The transaccion with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<RegistroTransaccion> registroTransaccionListOrphanCheck = transaccion.getRegistroTransaccionList();
            for (RegistroTransaccion registroTransaccionListOrphanCheckRegistroTransaccion : registroTransaccionListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Transaccion (" + transaccion + ") cannot be destroyed since the RegistroTransaccion " + registroTransaccionListOrphanCheckRegistroTransaccion + " in its registroTransaccionList field has a non-nullable cdtransaccion field.");
            }
            List<Tarjeta> tarjetaListOrphanCheck = transaccion.getTarjetaList();
            for (Tarjeta tarjetaListOrphanCheckTarjeta : tarjetaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Transaccion (" + transaccion + ") cannot be destroyed since the Tarjeta " + tarjetaListOrphanCheckTarjeta + " in its tarjetaList field has a non-nullable cdtransaccion field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            TipoPago cdtipopago = transaccion.getCdtipopago();
            if (cdtipopago != null) {
                cdtipopago.getTransaccionList().remove(transaccion);
                cdtipopago = em.merge(cdtipopago);
            }
            em.remove(transaccion);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Transaccion> findTransaccionEntities() {
        return findTransaccionEntities(true, -1, -1);
    }

    public List<Transaccion> findTransaccionEntities(int maxResults, int firstResult) {
        return findTransaccionEntities(false, maxResults, firstResult);
    }

    private List<Transaccion> findTransaccionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Transaccion.class));
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

    public Transaccion findTransaccion(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Transaccion.class, id);
        } finally {
            em.close();
        }
    }

    public int getTransaccionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Transaccion> rt = cq.from(Transaccion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
