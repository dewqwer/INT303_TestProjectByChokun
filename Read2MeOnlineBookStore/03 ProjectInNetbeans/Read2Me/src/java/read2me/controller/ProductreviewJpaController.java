/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package read2me.controller;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;
import read2me.controller.exceptions.NonexistentEntityException;
import read2me.controller.exceptions.RollbackFailureException;
import read2me.model.Book;
import read2me.model.Customer;
import read2me.model.Productreview;

/**
 *
 * @author Dew2018
 */
public class ProductreviewJpaController implements Serializable {

    public ProductreviewJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Productreview productreview) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Book isbn = productreview.getIsbn();
            if (isbn != null) {
                isbn = em.getReference(isbn.getClass(), isbn.getIsbn());
                productreview.setIsbn(isbn);
            }
            Customer customerid = productreview.getCustomerid();
            if (customerid != null) {
                customerid = em.getReference(customerid.getClass(), customerid.getCustomerid());
                productreview.setCustomerid(customerid);
            }
            em.persist(productreview);
            if (isbn != null) {
                isbn.getProductreviewList().add(productreview);
                isbn = em.merge(isbn);
            }
            if (customerid != null) {
                customerid.getProductreviewList().add(productreview);
                customerid = em.merge(customerid);
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

    public void edit(Productreview productreview) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Productreview persistentProductreview = em.find(Productreview.class, productreview.getReviewid());
            Book isbnOld = persistentProductreview.getIsbn();
            Book isbnNew = productreview.getIsbn();
            Customer customeridOld = persistentProductreview.getCustomerid();
            Customer customeridNew = productreview.getCustomerid();
            if (isbnNew != null) {
                isbnNew = em.getReference(isbnNew.getClass(), isbnNew.getIsbn());
                productreview.setIsbn(isbnNew);
            }
            if (customeridNew != null) {
                customeridNew = em.getReference(customeridNew.getClass(), customeridNew.getCustomerid());
                productreview.setCustomerid(customeridNew);
            }
            productreview = em.merge(productreview);
            if (isbnOld != null && !isbnOld.equals(isbnNew)) {
                isbnOld.getProductreviewList().remove(productreview);
                isbnOld = em.merge(isbnOld);
            }
            if (isbnNew != null && !isbnNew.equals(isbnOld)) {
                isbnNew.getProductreviewList().add(productreview);
                isbnNew = em.merge(isbnNew);
            }
            if (customeridOld != null && !customeridOld.equals(customeridNew)) {
                customeridOld.getProductreviewList().remove(productreview);
                customeridOld = em.merge(customeridOld);
            }
            if (customeridNew != null && !customeridNew.equals(customeridOld)) {
                customeridNew.getProductreviewList().add(productreview);
                customeridNew = em.merge(customeridNew);
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
                Long id = productreview.getReviewid();
                if (findProductreview(id) == null) {
                    throw new NonexistentEntityException("The productreview with id " + id + " no longer exists.");
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
            Productreview productreview;
            try {
                productreview = em.getReference(Productreview.class, id);
                productreview.getReviewid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The productreview with id " + id + " no longer exists.", enfe);
            }
            Book isbn = productreview.getIsbn();
            if (isbn != null) {
                isbn.getProductreviewList().remove(productreview);
                isbn = em.merge(isbn);
            }
            Customer customerid = productreview.getCustomerid();
            if (customerid != null) {
                customerid.getProductreviewList().remove(productreview);
                customerid = em.merge(customerid);
            }
            em.remove(productreview);
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

    public List<Productreview> findProductreviewEntities() {
        return findProductreviewEntities(true, -1, -1);
    }

    public List<Productreview> findProductreviewEntities(int maxResults, int firstResult) {
        return findProductreviewEntities(false, maxResults, firstResult);
    }

    private List<Productreview> findProductreviewEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Productreview.class));
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

    public Productreview findProductreview(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Productreview.class, id);
        } finally {
            em.close();
        }
    }

    public int getProductreviewCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Productreview> rt = cq.from(Productreview.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
