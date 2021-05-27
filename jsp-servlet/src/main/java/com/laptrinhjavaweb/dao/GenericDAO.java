package com.laptrinhjavaweb.dao;

import java.util.List;

import com.laptrinhjavaweb.mapper.RowMapper;
import com.laptrinhjavaweb.model.NewModel;

public interface GenericDAO<T> {
	<T> List<T> query(String sql,RowMapper<T> rowMapper,Object... paramesters);
	void update(String sql,Object... paramesters);
	Long insert(String sql,Object... paramesters);
}
