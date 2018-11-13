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
import read2me.model.Productreview;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import read2me.controller.exceptions.IllegalOrphanException;
import read2me.controller.exceptions.NonexistentEntityException;
import read2me.controller.exceptions.PreexistingEntityException;
import read2me.controller.exceptions.RollbackFailureException;
import read2me.model.Book;
import read2me.model.Lineitem;

/**
 *
 * @author Dew2018
 */
public class BookJpaController implements Serializable {

    public BookJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Book book) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (book.getProductreviewList() == null) {
            book.setProductreviewList(new ArrayList<Productreview>());
        }
        if (book.getLineitemList() == null) {
            book.setLineitemList(new ArrayList<Lineitem>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Productreview> attachedProductreviewList = new ArrayList<Productreview>();
            for (Productreview productreviewListProductreviewToAttach : book.getProductreviewList()) {
                productreviewListProductreviewToAttach = em.getReference(productreviewListProductreviewToAttach.getClass(), productreviewListProductreviewToAttach.getReviewid());
                attachedProductreviewList.add(productreviewListProductreviewToAttach);
            }
            book.setProductreviewList(attachedProductreviewList);
            List<Lineitem> attachedLineitemList = new ArrayList<Lineitem>();
            for (Lineitem lineitemListLineitemToAttach : book.getLineitemList()) {
                lineitemListLineitemToAttach = em.getReference(lineitemListLineitemToAttach.getClass(), lineitemListLineitemToAttach.getLineitemPK());
                attachedLineitemList.add(lineitemListLineitemToAttach);
            }
            book.setLineitemList(attachedLineitemList);
            em.persist(book);
            for (Productreview productreviewListProductreview : book.getProductreviewList()) {
                Book oldIsbnOfProductreviewListProductreview = productreviewListProductreview.getIsbn();
                productreviewListProductreview.setIsbn(book);
                productreviewListProductreview = em.merge(productreviewListProductreview);
                if (oldIsbnOfProductreviewListProductreview != null) {
                    oldIsbnOfProductreviewListProductreview.getProductreviewList().remove(productreviewListProductreview);
                    oldIsbnOfProductreviewListProductreview = em.merge(oldIsbnOfProductreviewListProductreview);
                }
            }
            for (Lineitem lineitemListLineitem : book.getLineitemList()) {
                Book oldBookOfLineitemListLineitem = lineitemListLineitem.getBook();
                lineitemListLineitem.setBook(book);
                lineitemListLineitem = em.merge(lineitemListLineitem);
                if (oldBookOfLineitemListLineitem != null) {
                    oldBookOfLineitemListLineitem.getLineitemList().remove(lineitemListLineitem);
                    oldBookOfLineitemListLineitem = em.merge(oldBookOfLineitemListLineitem);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findBook(book.getIsbn()) != null) {
                throw new PreexistingEntityException("Book " + book + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Book book) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Book persistentBook = em.find(Book.class, book.getIsbn());
            List<Productreview> productreviewListOld = persistentBook.getProductreviewList();
            List<Productreview> productreviewListNew = book.getProductreviewList();
            List<Lineitem> lineitemListOld = persistentBook.getLineitemList();
            List<Lineitem> lineitemListNew = book.getLineitemList();
            List<String> illegalOrphanMessages = null;
            for (Productreview productreviewListOldProductreview : productreviewListOld) {
                if (!productreviewListNew.contains(productreviewListOldProductreview)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Productreview " + productreviewListOldProductreview + " since its isbn field is not nullable.");
                }
            }
            for (Lineitem lineitemListOldLineitem : lineitemListOld) {
                if (!lineitemListNew.contains(lineitemListOldLineitem)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Lineitem " + lineitemListOldLineitem + " since its book field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Productreview> attachedProductreviewListNew = new ArrayList<Productreview>();
            for (Productreview productreviewListNewProductreviewToAttach : productreviewListNew) {
                productreviewListNewProductreviewToAttach = em.getReference(productreviewListNewProductreviewToAttach.getClass(), productreviewListNewProductreviewToAttach.getReviewid());
                attachedProductreviewListNew.add(productreviewListNewProductreviewToAttach);
            }
            productreviewListNew = attachedProductreviewListNew;
            book.setProductreviewList(productreviewListNew);
            List<Lineitem> attachedLineitemListNew = new ArrayList<Lineitem>();
            for (Lineitem lineitemListNewLineitemToAttach : lineitemListNew) {
                lineitemListNewLineitemToAttach = em.getReference(lineitemListNewLineitemToAttach.getClass(), lineitemListNewLineitemToAttach.getLineitemPK());
                attachedLineitemListNew.add(lineitemListNewLineitemToAttach);
            }
            lineitemListNew = attachedLineitemListNew;
            book.setLineitemList(lineitemListNew);
            book = em.merge(book);
            for (Productreview productreviewListNewProductreview : productreviewListNew) {
                if (!productreviewListOld.contains(productreviewListNewProductreview)) {
                    Book oldIsbnOfProductreviewListNewProductreview = productreviewListNewProductreview.getIsbn();
                    productreviewListNewProductreview.setIsbn(book);
                    productreviewListNewProductreview = em.merge(productreviewListNewProductreview);
                    if (oldIsbnOfProductreviewListNewProductreview != null && !oldIsbnOfProductreviewListNewProductreview.equals(book)) {
                        oldIsbnOfProductreviewListNewProductreview.getProductreviewList().remove(productreviewListNewProductreview);
                        oldIsbnOfProductreviewListNewProductreview = em.merge(oldIsbnOfProductreviewListNewProductreview);
                    }
                }
            }
            for (Lineitem lineitemListNewLineitem : lineitemListNew) {
                if (!lineitemListOld.contains(lineitemListNewLineitem)) {
                    Book oldBookOfLineitemListNewLineitem = lineitemListNewLineitem.getBook();
                    lineitemListNewLineitem.setBook(book);
                    lineitemListNewLineitem = em.merge(lineitemListNewLineitem);
                    if (oldBookOfLineitemListNewLineitem != null && !oldBookOfLineitemListNewLineitem.equals(book)) {
                        oldBookOfLineitemListNewLineitem.getLineitemList().remove(lineitemListNewLineitem);
                        oldBookOfLineitemListNewLineitem = em.merge(oldBookOfLineitemListNewLineitem);
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
                Long id = book.getIsbn();
                if (findBook(id) == null) {
                    throw new NonexistentEntityException("The book with id " + id + " no longer exists.");
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
            Book book;
            try {
                book = em.getReference(Book.class, id);
                book.getIsbn();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The book with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Productreview> productreviewListOrphanCheck = book.getProductreviewList();
            for (Productreview productreviewListOrphanCheckProductreview : productreviewListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Book (" + book + ") cannot be destroyed since the Productreview " + productreviewListOrphanCheckProductreview + " in its productreviewList field has a non-nullable isbn field.");
            }
            List<Lineitem> lineitemListOrphanCheck = book.getLineitemList();
            for (Lineitem lineitemListOrphanCheckLineitem : lineitemListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Book (" + book + ") cannot be destroyed since the Lineitem " + lineitemListOrphanCheckLineitem + " in its lineitemList field has a non-nullable book field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(book);
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

    public List<Book> findBookEntities() {
        return findBookEntities(true, -1, -1);
    }

    public List<Book> findBookEntities(int maxResults, int firstResult) {
        return findBookEntities(false, maxResults, firstResult);
    }

    private List<Book> findBookEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Book.class));
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

    public Book findBook(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Book.class, id);
        } finally {
            em.close();
        }
    }

    public int getBookCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Book> rt = cq.from(Book.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
