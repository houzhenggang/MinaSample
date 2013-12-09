package jp.co.basenet.wg.cfroomserver.mapper;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Param;

import jp.co.basenet.wg.cfroomserver.model.FileDetailInfo;

public interface FilesMapper {
	public ArrayList<FileDetailInfo> selectFileListByMettingId(@Param("meetingId") int meetingId);
}
