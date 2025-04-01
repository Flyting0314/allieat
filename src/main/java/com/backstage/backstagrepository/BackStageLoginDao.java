package com.backstage.backstagrepository;




import com.entity.AdminVO;

import java.util.List;


public interface BackStageLoginDao {

     List<AdminVO>  findByAccount(AdminVO admin);
}
