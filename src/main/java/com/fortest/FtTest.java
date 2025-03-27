package com.fortest;
import org.hibernate.Session;

import com.entity.FoodTypeVO;

import util.HibernateUtil;





public class FtTest {
	public static void main(String[] args) {
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			
			
			FoodTypeVO test = session.get(FoodTypeVO.class, 1);
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
