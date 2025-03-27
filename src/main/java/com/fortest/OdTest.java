package com.fortest;
import org.hibernate.Session;

import com.entity.OrderDetailId;
import com.entity.OrderDetailVO;

import util.HibernateUtil;





public class OdTest {
	public static void main(String[] args) {
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			
			
			OrderDetailVO od1 = session.get(OrderDetailVO.class, new OrderDetailId(2,2));
			System.out.println(od1);		
			session.getTransaction().commit();
			
		} catch (Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		} finally {
			HibernateUtil.shutdown();
		}
		
	}

}
