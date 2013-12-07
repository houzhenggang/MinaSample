package jp.co.basenet.wg.cfroomserver.mapper;

import java.util.ArrayList;

import jp.co.basenet.wg.cfroomserver.model.RoomButtonInfo;
import jp.co.basenet.wg.cfroomserver.model.RoomDetailInfo;

public interface MeetingsMapper {

	ArrayList<RoomButtonInfo> selectAllMeetingByNow();
	
	RoomDetailInfo selectMeetingInfoById(int id);
}
