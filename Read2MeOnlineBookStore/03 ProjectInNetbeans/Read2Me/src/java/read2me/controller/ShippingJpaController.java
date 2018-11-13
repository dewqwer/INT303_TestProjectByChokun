/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package read2me.controller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import read2me.model.Address;
import read2me.model.Orders;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import read2me.controller.exceptions.IllegalOrphanException;
import read2me.controller.exceptions.NonexistentEntityException;
import read2me.controller.exceptions.RollbackFailureException;
import read2me.model.Shipping;

/**
 *
 * @author Dew2018
 */
public class ShippingJpaController implements Serializable {

    public ShippingJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Shipping shipping) throws IllegalOrphanException, RollbackFailureException, Exception {
        List<String> illegalOrphanMessages = null;
        Orders orderidOrphanCheck = shipping.getOrderid();
        if (orderidOrphanCheck != null) {
            Shipping oldShippingOfOrderid = orderidOrphanCheck.getShipping();
            if (oldShippingOfOrderid != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The Orders " + orderidOrphanCheck + " already has an item of type Shipping whose orderid column cannot be null. Please make another selection for the orderid field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Address addressid = shipping.getAddressid();
            if (addressid != null) {
                addressid = em.getReference(addressid.getClass(), addressid.getAddressid());
                shipping.setAddressid(addressid);
            }
            Orders orderid = shipping.getOrderid();
            if (orderid != null) {
                orderid = em.getReference(orderid.getClass(), orderid.getOrderid());
                shipping.setOrderid(orderid);
            }
            em.persist(shipping);
            if (addressid != null) {
                addressid.getShippingList().add(shipping);
                addressid = em.merge(addressid);
            }
            if (orderid != null) {
                orderid.setShipping(shipping);
                orderid = em.merge(orderid);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Shipping shipping) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Shipping persistentShipping = em.find(Shipping.class, shipping.getShippingid());
            Address addressidOld = persistentShipping.getAddressid();
            Address addressidNew = shipping.getAddressid();
            Orders orderidOld = persistentShipping.getOrderid();
            Orders orderidNew = shipping.getOrderid();
            List<String> illegalOrphanMessages = null;
            if (orderidNew != null && !orderidNew.equals(orderidOld)) {
                Shipping oldShippingOfOrderid = orderidNew.getShipping();
                if (oldShippingOfOrderid != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The Orders " + orderidNew + " already has an item of type Shipping whose orderid column cannot be null. Please make another selection for the orderid field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (addressidNew != null) {
                addressidNew = em.getReference(addressidNew.getClass(), addressidNew.getAddressid());
                shipping.setAddressid(addressidNew);
            }
            if (orderidNew != null) {
                orderidNew = em.getReference(orderidNew.getClass(), orderidNew.getOrderid());
                shipping.setOrderid(orderidNew);
            }
            shipping = em.merge(shipping);
            if (addressidOld != null && !addressidOld.equals(addressidNew)) {
                addressidOld.getShippingList().remove(shipping);
                addressidOld = em.merge(addressidOld);
            }
            if (addressidNew != null && !addressidNew.equals(addressidOld)) {
                addressidNew.getShippingList().add(shipping);
                addressidNew = em.merge(addressidNew);
            }
            if (orderidOld != null && !orderidOld.equals(orderidNew)) {
                orderidOld.setShipping(null);
                orderidOld = em.merge(orderidOld);
            }
            if (orderidNew != null && !orderidNew.equals(orderidOld)) {
                orderidNew.setShipping(shipping);
                orderidNew = em.merge(orderidNew);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = shipping.getShippingid();
                if (findShipping(id) == null) {
                    throw new NonexistentEntityException("The shipping with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Long id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Shipping shipping;
            try {
                shipping = em.getReference(Shipping.class, id);
                shipping.getShippingid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The shipping with id " + id + " no longer exists.", enfe);
            }
            Address addressid = shipping.getAddressid();
            if (addressid != null) {
                addressid.getShippingList().remove(shipping);
                addressid = em.merge(addressid);
            }
            Orders orderid = shipping.getOrderid();
            if (orderid != null) {
                orderid.setShipping(null);
                orderid = em.merge(orderid);
            }
            em.remove(shipping);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Shipping> findShippingEntities() {
        return findShippingEntities(true, -1, -1);
    }

    public List<Shipping> findShippingEntities(int maxResults, int firstResult) {
        return findShippingEntities(false, maxResults, firstResult);
    }

    private List<Shipping> findShippingEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Shipping.class));
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

    public Shipping findShipping(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Shipping.class, id);
        } finally {
            em.close();
        }
    }

    public int getShippingCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Shipping> rt = cq.from(Shipping.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
