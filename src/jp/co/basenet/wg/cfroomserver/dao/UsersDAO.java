package jp.co.basenet.wg.cfroomserver.dao;

import jp.co.basenet.wg.cfroomserver.mapper.UsersMapper;
import jp.co.basenet.wg.cfroomserver.model.UserInfo;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class UsersDAO {
	private SqlSessionFactory sqlSessionFactory = null;
	
	public UsersDAO(SqlSessionFactory sqlSessionFaactory) {
		this.sqlSessionFactory = sqlSessionFaactory;
	}
	
	public int selectCountByUserId(UserInfo userInfo) {
		int result = 0;
		SqlSession session = sqlSessionFactory.openSession();
		
		try {
			UsersMapper mapper = session.getMapper(UsersMapper.class);
			result = mapper.selectCountByUserIdPassword(userInfo);
		}finally {
			session.close();
		}
		
		return result;
	}
	
	public UserInfo selectInfoByUserId(String userId){
		UserInfo userInfo = null;
		SqlSession session = sqlSessionFactory.openSession();
		
		try {
			UsersMapper mapper = session.getMapper(UsersMapper.class);
			userInfo = mapper.selectInfoByUserId(userId);
		}finally {
			session.close();
		}
		
		return userInfo;
	} 
}
