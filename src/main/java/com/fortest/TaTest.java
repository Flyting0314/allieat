package com.fortest;
import org.hibernate.Session;

import com.entity.TotalAmountVO;

import util.HibernateUtil;





public class TaTest {
	public static void main(String[] args) {
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			
			
			TotalAmountVO test = session.get(TotalAmountVO.class, 1);
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
