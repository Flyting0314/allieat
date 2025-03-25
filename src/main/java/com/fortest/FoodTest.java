package com.fortest;
import org.hibernate.Session;

import com.entity.OrderFoodVO;

import util.HibernateUtil;

public class FoodTest {
	public static void main(String[] args) {
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			
			OrderFoodVO food1 = session.get(OrderFoodVO.class, 1);
			System.out.println(food1);
			System.out.println("------------------------");
			System.out.println(food1.getOrderId());
			System.out.println(food1.getComment());
			session.getTransaction().commit();
			
		} catch (Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		} finally {
			HibernateUtil.shutdown();
		}
		
	}

}
