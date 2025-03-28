package com.backstage.backstagrepository;



import com.backstage.backstagerowmapper.AdminRowMapper;
import com.entity.AdminVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class BackStageLoginDaoImpl implements BackStageLoginDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    @Override
    public List<AdminVO> findByAccount(AdminVO admin) {
        String sql = "SELECT account,password FROM administrator WHERE account = :account";
        Map<String, Object> map = new HashMap<>();
        AdminRowMapper adapter = new AdminRowMapper();
        map.put("account", admin.getAccount());
        List<AdminVO> adminList = namedParameterJdbcTemplate.query(sql, map, adapter);
        return adminList;
    }
}
