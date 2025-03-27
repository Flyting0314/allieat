package com.fortest;
import org.hibernate.Session;

import com.entity.DonaVO;

import util.HibernateUtil;





public class DonaTest {
	public static void main(String[] args) {
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			
			
			DonaVO test = session.get(DonaVO.class, 1);
			System.out.println(test);
			System.out.println("------------------------");
			
			session.getTransaction().commit();
			
		} catch (Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		} finally {
			HibernateUtil.shutdown();
		}
		
	}

}
