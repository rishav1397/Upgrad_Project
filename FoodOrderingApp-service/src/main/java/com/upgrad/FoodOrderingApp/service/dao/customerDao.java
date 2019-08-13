package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.io.Serializable;

@Repository
public class customerDao implements Serializable {
    @PersistenceContext
    private EntityManager entityManager;
    @PersistenceUnit
    private EntityManagerFactory emf;
    @Transactional
    public CustomerEntity createUser(CustomerEntity userEntity) {
        EntityTransaction tx= entityManager.getTransaction();
        try{
            tx.begin();
            entityManager.persist(userEntity);
            tx.commit();

        }catch (Exception e){
            tx.rollback();
            System.out.println(e);
        }
        return userEntity;
    }
    public boolean checkContact(String contact_number){
        EntityManager em=emf.createEntityManager();
        CustomerEntity s=em.find(CustomerEntity.class,contact_number);
        em.close();
        //System.out.println(s);
        if(s==null)
            return false;
        else
            return true;


    }
    public CustomerEntity getUserByContact(String username) {
        try {
            CustomerEntity userEntity = entityManager.createNamedQuery("userByContact", CustomerEntity.class)
                    .setParameter("contact_number", username).getSingleResult();
            return userEntity;
        } catch (NoResultException e) {
            return null;
        }
    }

    public void updateCustomer(CustomerEntity updatedUser) {
        entityManager.merge(updatedUser);
    }

    public CustomerEntity getCustomer(final String access_token) {
        try {
            CustomerEntity userEntity = entityManager.createNamedQuery("userByToken", CustomerEntity.class)
                    .setParameter("access_token", access_token).getSingleResult();
            return userEntity;
        }catch (NoResultException e){
            return null;
        }
    }
    public CustomerEntity getCustomerByUUid(String id){
        EntityManager em=emf.createEntityManager();
        TypedQuery<CustomerEntity> query = em.createQuery("SELECT p FROM CustomerEntity p WHERE p.uuid =:uid", CustomerEntity.class).setParameter("uid",id);
        CustomerEntity s=query.getSingleResult();
        em.close();
        return s;
    }
}

