package jp.co.basenet.wg.cfroomserver.mapper;

import jp.co.basenet.wg.cfroomserver.model.UserInfo;

public interface UsersMapper {
	int selectCountByUserIdPassword(UserInfo userInfo);
	UserInfo selectInfoByUserId(String userId);
}
