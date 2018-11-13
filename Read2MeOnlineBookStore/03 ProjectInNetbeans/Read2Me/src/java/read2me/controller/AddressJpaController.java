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
import read2me.model.Customer;
import read2me.model.Shipping;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import read2me.controller.exceptions.IllegalOrphanException;
import read2me.controller.exceptions.NonexistentEntityException;
import read2me.controller.exceptions.RollbackFailureException;
import read2me.model.Address;

/**
 *
 * @author Dew2018
 */
public class AddressJpaController implements Serializable {

    public AddressJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Address address) throws RollbackFailureException, Exception {
        if (address.getShippingList() == null) {
            address.setShippingList(new ArrayList<Shipping>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Customer customerid = address.getCustomerid();
            if (customerid != null) {
                customerid = em.getReference(customerid.getClass(), customerid.getCustomerid());
                address.setCustomerid(customerid);
            }
            List<Shipping> attachedShippingList = new ArrayList<Shipping>();
            for (Shipping shippingListShippingToAttach : address.getShippingList()) {
                shippingListShippingToAttach = em.getReference(shippingListShippingToAttach.getClass(), shippingListShippingToAttach.getShippingid());
                attachedShippingList.add(shippingListShippingToAttach);
            }
            address.setShippingList(attachedShippingList);
            em.persist(address);
            if (customerid != null) {
                customerid.getAddressList().add(address);
                customerid = em.merge(customerid);
            }
            for (Shipping shippingListShipping : address.getShippingList()) {
                Address oldAddressidOfShippingListShipping = shippingListShipping.getAddressid();
                shippingListShipping.setAddressid(address);
                shippingListShipping = em.merge(shippingListShipping);
                if (oldAddressidOfShippingListShipping != null) {
                    oldAddressidOfShippingListShipping.getShippingList().remove(shippingListShipping);
                    oldAddressidOfShippingListShipping = em.merge(oldAddressidOfShippingListShipping);
                }
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

    public void edit(Address address) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Address persistentAddress = em.find(Address.class, address.getAddressid());
            Customer customeridOld = persistentAddress.getCustomerid();
            Customer customeridNew = address.getCustomerid();
            List<Shipping> shippingListOld = persistentAddress.getShippingList();
            List<Shipping> shippingListNew = address.getShippingList();
            List<String> illegalOrphanMessages = null;
            for (Shipping shippingListOldShipping : shippingListOld) {
                if (!shippingListNew.contains(shippingListOldShipping)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Shipping " + shippingListOldShipping + " since its addressid field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (customeridNew != null) {
                customeridNew = em.getReference(customeridNew.getClass(), customeridNew.getCustomerid());
                address.setCustomerid(customeridNew);
            }
            List<Shipping> attachedShippingListNew = new ArrayList<Shipping>();
            for (Shipping shippingListNewShippingToAttach : shippingListNew) {
                shippingListNewShippingToAttach = em.getReference(shippingListNewShippingToAttach.getClass(), shippingListNewShippingToAttach.getShippingid());
                attachedShippingListNew.add(shippingListNewShippingToAttach);
            }
            shippingListNew = attachedShippingListNew;
            address.setShippingList(shippingListNew);
            address = em.merge(address);
            if (customeridOld != null && !customeridOld.equals(customeridNew)) {
                customeridOld.getAddressList().remove(address);
                customeridOld = em.merge(customeridOld);
            }
            if (customeridNew != null && !customeridNew.equals(customeridOld)) {
                customeridNew.getAddressList().add(address);
                customeridNew = em.merge(customeridNew);
            }
            for (Shipping shippingListNewShipping : shippingListNew) {
                if (!shippingListOld.contains(shippingListNewShipping)) {
                    Address oldAddressidOfShippingListNewShipping = shippingListNewShipping.getAddressid();
                    shippingListNewShipping.setAddressid(address);
                    shippingListNewShipping = em.merge(shippingListNewShipping);
                    if (oldAddressidOfShippingListNewShipping != null && !oldAddressidOfShippingListNewShipping.equals(address)) {
                        oldAddressidOfShippingListNewShipping.getShippingList().remove(shippingListNewShipping);
                        oldAddressidOfShippingListNewShipping = em.merge(oldAddressidOfShippingListNewShipping);
                    }
                }
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
                Long id = address.getAddressid();
                if (findAddress(id) == null) {
                    throw new NonexistentEntityException("The address with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Long id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Address address;
            try {
                address = em.getReference(Address.class, id);
                address.getAddressid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The address with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Shipping> shippingListOrphanCheck = address.getShippingList();
            for (Shipping shippingListOrphanCheckShipping : shippingListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Address (" + address + ") cannot be destroyed since the Shipping " + shippingListOrphanCheckShipping + " in its shippingList field has a non-nullable addressid field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Customer customerid = address.getCustomerid();
            if (customerid != null) {
                customerid.getAddressList().remove(address);
                customerid = em.merge(customerid);
            }
            em.remove(address);
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

    public List<Address> findAddressEntities() {
        return findAddressEntities(true, -1, -1);
    }

    public List<Address> findAddressEntities(int maxResults, int firstResult) {
        return findAddressEntities(false, maxResults, firstResult);
    }

    private List<Address> findAddressEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Address.class));
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

    public Address findAddress(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Address.class, id);
        } finally {
            em.close();
        }
    }

    public int getAddressCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Address> rt = cq.from(Address.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
