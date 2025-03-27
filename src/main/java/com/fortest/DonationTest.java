package com.fortest;
import org.hibernate.Session;

import com.entity.DonationVO;

import util.HibernateUtil;





public class DonationTest {
	public static void main(String[] args) {
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			
			
			DonationVO test = session.get(DonationVO.class, 1);
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
