package com.backstage.fortest;

import com.backstage.backstagrepository.*;

import com.entity.OrderDetailId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/backStage")
public class TestController {


    @GetMapping("/test")
    public List<Map<String,String>> test(){
        System.out.println("--------------------------------");
        List<Map<String,String>> testList = new ArrayList<>();
        Map<String,String> TestMap = new HashMap<>();
        TestMap.put("name","新北市政府社會局");
        TestMap.put("type","政府機構");
        TestMap.put("createdTime","2022-08-15");
        TestMap.put("status","True");
        for(int i=0;i<100;i++){
            testList.add(TestMap);
        }
        testList.add(TestMap);
        return testList;
    }


    @Autowired
    private AdminRepository ar;
    @Autowired
    private AttachedRepository at;
    @Autowired
    private DonorRepository d;
    @Autowired
    private DonorRepository da;
    @Autowired
    private FoodRepository f;
    @Autowired
    private FoodTypeRepository ft;
    @Autowired
    private MemberRepository mr;
    @Autowired
    private OrderDetailRepository od;
    @Autowired
    private OrderFoodRepository of;
    @Autowired
    OrganizationRepository og;
    @Autowired
    PayDetailRepository pd;
    @Autowired
    PayRecordRepository pr;
    @Autowired
    PhotoRepository photo;
    @Autowired
    RankRepository r;
    @Autowired
    StoreRepository store;
    @Autowired
    TotalAmountRepository totalAmount;


    @GetMapping("/test2")
    public void findTest(){
        System.out.println(ar.findById(1).orElse(null));
        System.out.println("----------");
        System.out.println(at.findById(1).orElse(null));
        System.out.println("----------");
        System.out.println(d.findById(1).orElse(null));
        System.out.println("----------");
        System.out.println(da.findById(1).orElse(null));
        System.out.println("----------");
        System.out.println(f.findById(1).orElse(null));
        System.out.println("----------");
        System.out.println(ft.findById(1).orElse(null));
        System.out.println("----------");
        System.out.println(mr.findById(1).orElse(null));
        System.out.println("----------");
        System.out.println(od.findById(new OrderDetailId(1, 1)).orElse(null));
        System.out.println("----------");
        System.out.println(of.findById(1).orElse(null));
        System.out.println("----------");
        System.out.println(og.findById(1).orElse(null));
        System.out.println("----------");
        System.out.println(pd.findById(1).orElse(null));
        System.out.println("----------");
        System.out.println(pr.findById(1).orElse(null));
        System.out.println("----------");
        System.out.println(photo.findById(1).orElse(null));
        System.out.println("----------");
        System.out.println(r.findById(1).orElse(null));
        System.out.println("----------");
        System.out.println(store.findById(1).orElse(null));
        System.out.println("----------");
        System.out.println(totalAmount.findById(1).orElse(null));
        System.out.println("----------");


    }

}
