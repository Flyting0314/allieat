package com.fortest;
import org.hibernate.Session;

import com.entity.RankVO;

import util.HibernateUtil;





public class RankTest {
	public static void main(String[] args) {
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			
			
			RankVO test = session.get(RankVO.class, 2);
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
