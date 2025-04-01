package com.backstage.backstagerowmapper;

import com.entity.AdminVO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminRowMapper implements RowMapper<AdminVO> {


    @Override
    public AdminVO mapRow(ResultSet rs, int rowNum) throws SQLException {
        String account =rs.getString("account");
        String password = rs.getString("password");
        AdminVO ad = new AdminVO();
        ad.setAccount(account);
        ad.setPassword(password);
        return ad;
    }
}