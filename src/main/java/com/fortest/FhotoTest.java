package com.fortest;
import org.hibernate.Session;

import com.entity.PhotoVO;

import util.HibernateUtil;





public class FhotoTest {
	public static void main(String[] args) {
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			
			
			PhotoVO test = session.get(PhotoVO.class, 1);
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
