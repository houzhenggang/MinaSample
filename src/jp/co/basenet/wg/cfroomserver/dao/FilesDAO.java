package jp.co.basenet.wg.cfroomserver.dao;

import java.util.ArrayList;

import jp.co.basenet.wg.cfroomserver.mapper.FilesMapper;
import jp.co.basenet.wg.cfroomserver.model.FileDetailInfo;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class FilesDAO {
	private SqlSessionFactory sqlSessionFactory = null;
	
	public FilesDAO(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}
	
	public ArrayList<FileDetailInfo> selectFileListByMettingId(int mettingId) {
		ArrayList<FileDetailInfo> fileDetailInfoList = null;
		SqlSession session = sqlSessionFactory.openSession();
		
		try {
			FilesMapper mapper =  session.getMapper(FilesMapper.class);
			fileDetailInfoList = mapper.selectFileListByMettingId(mettingId);
		} finally {
			session.close();
		}
		
		return fileDetailInfoList;
	}
}
