package com.backstage.backstageservice;

import com.backstage.backstagedto.ChartDTO;
import com.backstage.backstagrepository.DonorRepository;
import com.backstage.backstagrepository.DonationRepository;
import com.backstage.backstagrepository.OrderFoodRepository;
import com.backstage.backstagrepository.TotalAmountRepository;
import com.backstage.callback.RemoveFromQueueCallback;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.web.context.request.async.DeferredResult;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class BackStageHomePageServiceImpl implements BackStageHomePageService {

	@Autowired
	private DonationRepository accDona;
	@Autowired
	private DonorRepository donor;
	@Autowired
	private TotalAmountRepository totalAmount;
	@Autowired
	private OrderFoodRepository monthlyRecipients;
	@Autowired
	private ObjectMapper objectMapper;
	// 用於長輪詢
	private final List<DeferredResult<String>> waitingQueue = new CopyOnWriteArrayList<>();

	@Override
	public Map<String, Object> getTotalDonations() {
		// 查詢最後一筆捐款總額
		Map<String, Object> result = new HashMap<>();
		result.put("totalDonations", accDona.findTopByOrderByRankIdDesc().getAmount());
		return result;
	}

	@Override
	public Map<String, Object> getTotalDonors() {
		Map<String, Object> result = new HashMap<>();
		//總捐贈者
		result.put("totalDonors", donor.count());
		return result;
	}

	@Override
	public Map<String, Object> getMonthlyDonations() {
		Map<String, Object> result = new HashMap<>();
		//每月的捐贈
		result.put("monthlyDonations", totalAmount.Now() - totalAmount.LastMonth());
		return result;
	}

	@Override
	public Map<String, Object> getNewDonors() {
		Map<String, Object> result = new HashMap<>();
		// 最近新增的捐款人
		result.put("newDonors", donor.countDonorLastMonth());		
		return result;
	}

	@Override
	public Map<String, Object> getDonationChart() {
		Map<String, Object> result = new HashMap<>();

			// 最近12個月累積捐款圖表
			List<Object[]> test = totalAmount.findMonthlyTotalAmounts();
			List<String> labels = new ArrayList<>();
			List<BigDecimal> data = new ArrayList<>();

			for (Object[] it : test) {
				labels.add((String) it[0]);
				data.add((BigDecimal) it[1]);
			}
			// 資料封裝
			ChartDTO chartDTO = new ChartDTO(labels, data);
			result.put("donationChart", chartDTO);	
		return result;
	}

	@Override
	public Map<String, Object> getUsageChart() {
		Map<String, Object> result = new HashMap<>();
	
			// 最近12個月的領用情況
			List<Object[]> test = monthlyRecipients.findMonthlyPickedOrders();
			List<String> labels = new ArrayList<>();
			List<Long> data = new ArrayList<>();
			for (Object[] it : test) {
				labels.add((String) it[0]);
				data.add((Long) it[1]);
			}
			// 資料封裝
			ChartDTO chartDTO1 = new ChartDTO(labels, data);
			result.put("usageChart", chartDTO1);	
		return result;
	}

	// RemoveFromQueueCallback 用於移除等待中的 DeferredResult 狀態，是一個runnable物件
	// onCompletion 與 onTimeout 是 DeferredResult<String> 提供的 callback 註冊方法，
	// 這兩個方法皆需傳入一個 Runnable 物件，當請求完成或逾時時會自動執行該 Runnable。
	@Override
	public void registerListener(DeferredResult<String> dr) {
		waitingQueue.add(dr);
		dr.onCompletion(new RemoveFromQueueCallback(dr, waitingQueue));
		dr.onTimeout(new RemoveFromQueueCallback(dr, waitingQueue));

	}
	
	//thread-safe 型別
	private final AtomicLong lastDonor = new AtomicLong(0);

	@Override
	public void checkForUpdate() {
		// 現在資訊查詢
		Long currentDonor = donor.count();

		// 比對前後資訊，若不同則call。
		if (currentDonor != lastDonor.get()) {
			lastDonor.set(currentDonor);
			// 回傳資料，變動時call送出上述六支api的資訊。
			  Map<String, Object> payload = new HashMap<>();
	          payload.putAll(getTotalDonations());
	          payload.putAll(getTotalDonors());
	          payload.putAll(getMonthlyDonations());
	          payload.putAll(getNewDonors());
	          payload.putAll(getDonationChart());
	          payload.putAll(getUsageChart());
			
			try {
				String json = objectMapper.writeValueAsString(payload);
				for (DeferredResult<String> it : waitingQueue) {
					it.setResult(json);
				}
				waitingQueue.clear();
			} catch (Exception e) {
				e.printStackTrace();
				waitingQueue.clear();
			}
		}
	}

}
