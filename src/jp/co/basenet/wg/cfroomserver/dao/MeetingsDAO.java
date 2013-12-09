package jp.co.basenet.wg.cfroomserver.dao;

import java.util.ArrayList;

import jp.co.basenet.wg.cfroomserver.mapper.MeetingsMapper;
import jp.co.basenet.wg.cfroomserver.model.RoomButtonInfo;
import jp.co.basenet.wg.cfroomserver.model.RoomDetailInfo;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class MeetingsDAO {
	private SqlSessionFactory sqlSessionFactory = null;
	
	public MeetingsDAO(SqlSessionFactory sqlSessionFaactory) {
		this.sqlSessionFactory = sqlSessionFaactory;
	}
	
	public ArrayList<RoomButtonInfo> selectAllInfoByNow() {
		ArrayList<RoomButtonInfo> roomDetailInfoList = null;
		SqlSession session = sqlSessionFactory.openSession();
		
		try {
			MeetingsMapper mapper = session.getMapper(MeetingsMapper.class);
			roomDetailInfoList = mapper.selectAllMeetingByNow();
		} finally {
			session.close();
		}
		
		return roomDetailInfoList;
	}

	public RoomDetailInfo selectMeetingInfoById(int id) {
		RoomDetailInfo roomDetailInfo = null;
		SqlSession session = sqlSessionFactory.openSession();
		
		try {
			MeetingsMapper mapper = session.getMapper(MeetingsMapper.class);
			roomDetailInfo = mapper.selectMeetingInfoById(id);
		} finally {
			session.close();
		}
		
		return roomDetailInfo;
	}
	
	public int selectCountByIdUserId(int id, String userId) {
		int result = 0;
		SqlSession session = sqlSessionFactory.openSession();
		
		try {
			MeetingsMapper mapper = session.getMapper(MeetingsMapper.class);
			result = mapper.selectCountByIdUserId(id, userId);
		} finally {
			session.close();
		}
		
		return result;
	}
}
