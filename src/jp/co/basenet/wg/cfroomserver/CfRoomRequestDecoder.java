package jp.co.basenet.wg.cfroomserver;

import jp.co.basenet.wg.cfroomserver.model.NorRequestObj;
import jp.co.basenet.wg.cfroomserver.model.UserInfo;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public class CfRoomRequestDecoder extends CumulativeProtocolDecoder {

	@Override
	protected boolean doDecode(IoSession ssn, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		if(in.remaining() >= 8) {
			int status = in.getInt();
			switch(status) {
			case CfRoomConst.LOGIN:
				//ログイン処理
				UserInfo userInfo = loginDoDecode(in);
				if(userInfo == null) {
					return false;
				} else {
					out.write(userInfo);
					return true;
				}
			//case CfRoomConst.GIVE_ME_LOBBY_INFO:	
				//会議室一覧取得請求
			//case CfRoomConst.MARKER:
				//マーカー処理
			default:
				System.out.println("GIVE_ME_LOBBY_INFO start..:");
				NorRequestObj nro = broadcast(status, in);
				if(nro == null) {
					System.out.println("GIVE_ME_LOBBY_INFO false..:");
					return false;
				} else {
					System.out.println("GIVE_ME_LOBBY_INFO success..:");
					out.write(nro);
					return true;
				}
			}
		} else {
			return false;
		}
	}
	
	private UserInfo loginDoDecode(IoBuffer in) throws Exception {
		int length = in.getInt();
		if(in.remaining() >= length) {
			UserInfo userInfo = new UserInfo();
			int memberLength = in.getInt();
			userInfo.setUserName(readString(in, memberLength));
			memberLength = in.getInt();
			userInfo.setPassword(readString(in, memberLength));
			return userInfo;
		} else {
			in.rewind();
			return null;
		}
	}
	
	private NorRequestObj broadcast(int status, IoBuffer in) throws Exception {
		int length = in.getInt();
		System.out.println("GIVE_ME_LOBBY_INFO length..:" + length);
		System.out.println("GIVE_ME_LOBBY_INFO remaining..:" + in.remaining());
		if(in.remaining() >= length) {
			//int bufLength = 8 + length;
			//in.rewind();
			return new NorRequestObj(status, readString(in, length));
		} else {
			in.rewind();
			return null;
		}
	}
	
	private String readString(IoBuffer in, int length) throws Exception {
		byte[] bytes = new byte[length];
		in.get(bytes);
		return new String(bytes, "UTF-8");
	}
}
