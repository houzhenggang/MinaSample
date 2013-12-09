package jp.co.basenet.wg.cfroomserver.mapper;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Param;

import jp.co.basenet.wg.cfroomserver.model.RoomButtonInfo;
import jp.co.basenet.wg.cfroomserver.model.RoomDetailInfo;

public interface MeetingsMapper {

	ArrayList<RoomButtonInfo> selectAllMeetingByNow();
	RoomDetailInfo selectMeetingInfoById(int id);
	int selectCountByIdUserId(@Param("id") int id, @Param("userId") String userId);
}
