package jp.co.basenet.wg.cfroomserver.dao;

import jp.co.basenet.wg.cfroomserver.mapper.ParticipantMapper;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class ParticipantDAO {
	private SqlSessionFactory sqlSessionFactory = null;
	
	public ParticipantDAO(SqlSessionFactory sqlSessionFaactory) {
		this.sqlSessionFactory = sqlSessionFaactory;
	}
	
	public int selectCountByUserIdMettingId(String userId, int MeetingId) {
		int result = 0;
		SqlSession session = sqlSessionFactory.openSession();
		
		try {
			ParticipantMapper mapper = session.getMapper(ParticipantMapper.class);
			result = mapper.selectCountByUserIdMettingId(userId, MeetingId);
		}finally {
			session.close();
		}
		
		return result;
	}
}
