package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Repository
public class addressDao implements Serializable {
        @PersistenceContext
        private EntityManager entityManager;
        @PersistenceUnit
        private EntityManagerFactory emf;
        @Transactional
        public AddressEntity addAddress(AddressEntity userEntity,CustomerEntity c) {
            EntityManager em=emf.createEntityManager();
            CustomerAddressEntity ca=new CustomerAddressEntity();
            EntityTransaction tx= em.getTransaction();
            try{
                tx.begin();
                em.persist(userEntity);
                ca.setAddress_id(userEntity.getId());
                ca.setCustomer_id(c.getId());
                em.persist(ca);
                tx.commit();

            }catch (Exception e){
                tx.rollback();
                System.out.println(e);
            }

            return userEntity;
        }
        public StateEntity getState(String uuid){


            EntityManager em=emf.createEntityManager();
            TypedQuery<StateEntity> query=em.createQuery("From StateEntity p where p.uuid=:uuid",StateEntity.class).setParameter("uuid",uuid);
            StateEntity s=query.getSingleResult();
            em.close();
            return s;
        }
        public List<AddressEntity> getAllAddress(CustomerEntity c){

            EntityManager em=emf.createEntityManager();
            TypedQuery<CustomerAddressEntity> query=em.createQuery("Select p from CustomerAddressEntity p where p.customer_id =:uuid",CustomerAddressEntity.class).setParameter("uuid",c.getId());
            List<CustomerAddressEntity> resultList=query.getResultList();
            List<AddressEntity>p=new ArrayList<>();
            for(int i=0;i<resultList.size();i++) {
                TypedQuery<AddressEntity> q = em.createQuery("From AddressEntity p where p.id=:uid",AddressEntity.class).setParameter("uid",resultList.get(i).getAddress_id());
                AddressEntity we=q.getSingleResult();
                p.add(we);
            }
            return p;
        }
        @Transactional
        @Modifying
        public AddressEntity delete(AddressEntity id)throws AddressNotFoundException{
            AddressEntity s=null;
            CustomerAddressEntity sq=null;
            AddressEntity q=null;
            EntityManager em=emf.createEntityManager();
            EntityTransaction tx= em.getTransaction();
            try{
                tx.begin();
                s=em.find(AddressEntity.class,id.getId());
                q=s;
                if(s==null)
                    throw new AddressNotFoundException("ANF-003","No address by this id");
                em.remove(s);

                Query query=em.createNativeQuery("Delete From customer_address AS p where p.address_id=:iid").setParameter("iid",id.getId());
                int del=query.executeUpdate();
                System.out.println("tattiu "+id.getId()+" "+del);
                em.close();
                tx.commit();
        }catch (Exception e){
            tx.rollback();
            System.out.println(e);
        }
            return q;
        }
        public AddressEntity getAddress(String id)throws AddressNotFoundException{

            EntityManager em=emf.createEntityManager();
            TypedQuery<AddressEntity>query=em.createQuery("From AddressEntity p where p.uuid=:id",AddressEntity.class).setParameter("id",id);
            AddressEntity s=query.getSingleResult();
            if(s==null)
                throw new AddressNotFoundException("ANF-003","No address by this id");
            em.close();
            return s;
        }
    }
