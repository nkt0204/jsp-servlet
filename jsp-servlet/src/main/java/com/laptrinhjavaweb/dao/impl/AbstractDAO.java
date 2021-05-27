package com.laptrinhjavaweb.dao.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.laptrinhjavaweb.dao.GenericDAO;
import com.laptrinhjavaweb.mapper.RowMapper;

public class AbstractDAO<T> implements GenericDAO<T> {

	public Connection getConnection() {

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			String url = "jdbc:mysql://localhost:3306/jspservletjdbc";
			String user = "root";
			String password = "1234";
			return DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException | SQLException e) {
			return null;
		}

	}

	@Override
	public <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... paramesters) {
		List<T> results = new ArrayList<T>();
		Connection connection = null;
		PreparedStatement stament = null;
		ResultSet resultSet = null;
		try {
			connection = getConnection();
			stament = connection.prepareStatement(sql);
			// set parameter
			setParemeter(stament, paramesters);
			resultSet = stament.executeQuery();
			while (resultSet.next()) {
				results.add(rowMapper.mapRow(resultSet));
			}
			return results;
		} catch (SQLException e) {
			return null;
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
				if (stament != null) {
					stament.close();
				}
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (Exception e2) {
				return null;
			}
		}

	}

	private void setParemeter(PreparedStatement stament, Object... paramesters) {
		try {
			for (int i = 0; i < paramesters.length; i++) {
				Object parameter = paramesters[i];
				int index = i + 1;
				if (parameter instanceof Long) {
					stament.setLong(index, (Long) parameter);
				} else if (parameter instanceof String) {
					stament.setString(index, (String) parameter);
				} else if (parameter instanceof Integer) {
					stament.setInt(index, (Integer) parameter);
				} else if (parameter instanceof Timestamp) {
					stament.setTimestamp(index, (Timestamp) parameter);
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void update(String sql, Object... paramesters) {
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = getConnection();
			connection.setAutoCommit(false);
			statement = connection.prepareStatement(sql);
			setParemeter(statement, paramesters);			
			statement.executeUpdate();
			connection.commit();
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}finally {
			try {
				if(connection!=null) {
					connection.close();					
				}
				if(statement!=null) {
					statement.close();				
				}
				
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
		}
		
	}

	@Override
	public Long insert(String sql, Object... paramesters) {
		Long id= null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet= null;
		try {
			connection = getConnection();
			connection.setAutoCommit(false);
			statement = connection.prepareStatement(sql,statement.RETURN_GENERATED_KEYS);
			setParemeter(statement, paramesters);			
			statement.executeUpdate();
			resultSet= statement.getGeneratedKeys();
			if(resultSet.next()){
				id=resultSet.getLong(1);
			}
			connection.commit();
			return id;
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}finally {
			try {
				if(connection!=null) {
					connection.close();				
				}
				if(statement!=null) {
					statement.close();					
				}
				if(resultSet!=null) {
					resultSet.close();					
				}
				
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
		}
		return null;
	}
	
}
