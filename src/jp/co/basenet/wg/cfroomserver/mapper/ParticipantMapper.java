package jp.co.basenet.wg.cfroomserver.mapper;

import org.apache.ibatis.annotations.Param;

public interface ParticipantMapper {
	int selectCountByUserIdMettingId(@Param("userId") String userId, @Param("meetingId") int meetingId);
}
