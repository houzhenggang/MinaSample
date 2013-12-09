package jp.co.basenet.wg.cfroomserver;

import static org.apache.mina.statemachine.event.IoHandlerEvents.MESSAGE_RECEIVED;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;

import jp.co.basenet.wg.cfroomserver.dao.FilesDAO;
import jp.co.basenet.wg.cfroomserver.dao.MeetingsDAO;
import jp.co.basenet.wg.cfroomserver.dao.ParticipantDAO;
import jp.co.basenet.wg.cfroomserver.dao.UsersDAO;
import jp.co.basenet.wg.cfroomserver.mapper.ParticipantMapper;
import jp.co.basenet.wg.cfroomserver.model.FileDetailInfo;
import jp.co.basenet.wg.cfroomserver.model.NorRequestObj;
import jp.co.basenet.wg.cfroomserver.model.NorResponseObj;
import jp.co.basenet.wg.cfroomserver.model.RoomButtonInfo;
import jp.co.basenet.wg.cfroomserver.model.RoomDetailInfo;
import jp.co.basenet.wg.cfroomserver.model.UserInfo;
import jp.co.basenet.wg.cfroomserver.mybatis.MyBatisConnectionFactory;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.statemachine.annotation.IoHandlerTransition;
import org.apache.mina.statemachine.annotation.State;
import org.apache.mina.statemachine.context.AbstractStateContext;
import org.apache.mina.statemachine.event.Event;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class CfRoomServerHandler extends IoHandlerAdapter {
	@State public static final String ROOT = "root";
	@State(ROOT) public static final String NOT_CONNECTED = "NotConnected";
	@State(ROOT) public static final String CONNECTED = "Connected";
	
	@IoHandlerTransition(in = ROOT, weight = 100) 
	public void unhandledEvent(Event event) { 
		System.out.println("Warning..");
	} 
	
	@Override
	public void exceptionCaught(IoSession ssn, Throwable cause) throws Exception {
		cause.printStackTrace();
	}
	
	@IoHandlerTransition(on = MESSAGE_RECEIVED, in = NOT_CONNECTED, next = CONNECTED)
	public void Login(ConfRoomContext context, IoSession ssn, Object message) throws Exception {
		UsersDAO usersDAO = new UsersDAO(MyBatisConnectionFactory.getSqlSessionFactory());
		UserInfo userInfo = (UserInfo)message;
		
		System.out.println("login main process start..:");
		System.out.println("UserId: " + userInfo.getUserId());
		System.out.println("Password: " + userInfo.getPassword());
		
		String result;
		if("base".equals(userInfo.getUserId()) && "base".equals(userInfo.getPassword())) {
			System.out.println("login success..");
			//TODO
			//テスト用
			ssn.setAttribute("TEST", "1");
			result = "SUCCESS";
		} else if (usersDAO.selectCountByUserIdPassword(userInfo) > 0) {
			System.out.println("login success..");
			//TODO
			//テスト用
			ssn.setAttribute("TEST", "0");
			result = "SUCCESS";
		} else {
			System.out.println("login failure..");
			result = "FAILURE";
		}
		
		//ユーザID
		ssn.setAttribute("userId", userInfo.getUserId());
		//ルーム番号
		//-1 入室しない
		ssn.setAttribute("roomId", "-1"); 
		//状態(入室しない場合"")
		//inactive 同期しない
		//active 同期中
		ssn.setAttribute("status", "");
		//身分(入室しない場合"")
		//sponsor 主催者
		//member 一般参加者
		ssn.setAttribute("position", "");
		
		ssn.write(new NorResponseObj(0001, -1, -1, 1, 1, result));
	}

	@IoHandlerTransition(on = MESSAGE_RECEIVED, in = CONNECTED)
	public void notFirstConnect(ConfRoomContext myContext, IoSession ssn, Object message) throws Exception {
		NorRequestObj nreo = (NorRequestObj)message;
		System.out.println("Status read:");
		System.out.println(nreo.getStatus());
		int roomId;
		Collection<IoSession> ssns;
		MeetingsDAO meetingsDAO = null;
		ParticipantDAO participantDAO = null;
		FilesDAO filesDAO = null;
		byte[] tempMsg;
		int length;
		
		//当レコードのサイズ
		int currentSize;
		//連番、1から
		int seqNo;
		//レコード件数
		int recordCount;
		byte[] a;
		
		//sessionID取得
		long id = ssn.getId();
		Gson gson = new Gson();
		switch(nreo.getStatus()) {
			case 1101:
				//ロビーの会議室一覧を返す
				//TODO
				System.out.println("1101 main process start..:");
				meetingsDAO = new MeetingsDAO(MyBatisConnectionFactory.getSqlSessionFactory());
				
				ArrayList<RoomButtonInfo> roomButtonList = meetingsDAO.selectAllInfoByNow();
				String roomButtonListJson = gson.toJson(roomButtonList);
				//連番、1から
				seqNo = 1;
				//byte変換
				tempMsg = roomButtonListJson.getBytes(Charset.forName("UTF-8"));
				//長さ
				length = tempMsg.length;
				//レコード件数
				recordCount = length / 512 + 1;
				while(length >= 512) {
					a = new byte[512];
					System.arraycopy(tempMsg, tempMsg.length - length, a, 0, 512);
					ssn.write(new NorResponseObj(1201, 512, tempMsg.length, seqNo++, recordCount, a));
					length -= 512;
				}
				a = new byte[512];
				System.arraycopy(tempMsg, tempMsg.length - length, a, 0, length);
				ssn.write(new NorResponseObj(1201, 512, tempMsg.length, seqNo++, recordCount, a));
				//ssn.write(new NorResponseObj(1201, -1, -1, 1, 1, roomButtonListJson));				
				break;
			case 1102:
				//会議室詳細情報を返す
				//TODO
				System.out.println("1102 main process start..:");
				meetingsDAO = new MeetingsDAO(MyBatisConnectionFactory.getSqlSessionFactory());
				participantDAO = new ParticipantDAO(MyBatisConnectionFactory.getSqlSessionFactory());
				
				RoomDetailInfo roomDetailInfo = meetingsDAO.selectMeetingInfoById(Integer.parseInt(nreo.getMessage()));
				String roomDetailInfoJson = gson.toJson(roomDetailInfo);
				ssn.write(new NorResponseObj(1202, -1, -1, 1, 1, roomDetailInfoJson));	
				break;
			case 1103:
				//入室
				System.out.println("1103 main process start..:");
				meetingsDAO = new MeetingsDAO(MyBatisConnectionFactory.getSqlSessionFactory());
				participantDAO = new ParticipantDAO(MyBatisConnectionFactory.getSqlSessionFactory());
				roomId = Integer.parseInt(nreo.getMessage());
				String result;
				
				if(participantDAO.selectCountByUserIdMettingId((String)ssn.getAttribute("userId"), roomId) > 0) {
					System.out.println("enter success..");
					if(meetingsDAO.selectCountByIdUserId(roomId, (String)ssn.getAttribute("userId")) > 0) {
						//TODO
						//ステータスを通知
						//主催者
						System.out.println("sponser");
						result = "sponser";
					} else {
						System.out.println("member");
						result = "member";
					}
					ssn.setAttribute("roomId", roomId);
					ssn.setAttribute("status", "active");
					ssn.setAttribute("position", result);
				} else {
					System.out.println("enter failure..");
					result = "FAILURE";
				}
				ssn.write(new NorResponseObj(1203, -1, -1, 1, 1, result));	
				break;
			case 2101:
				//ファイルリスト情報リクエスト
				System.out.println("2101 main process start..:");
				System.out.println("roomID: " + ssn.getAttribute("roomId"));
				filesDAO = new FilesDAO(MyBatisConnectionFactory.getSqlSessionFactory());
				ArrayList<FileDetailInfo> fileDetailInfoList = 
				filesDAO.selectFileListByMettingId((Integer)ssn.getAttribute("roomId"));
				/*
				//ダミーファイル１
				for(int i = 0; i < 3; i++) {
		            FileDetailInfo fdi = new FileDetailInfo();
		            fdi.setFileId(1);
		            fdi.setPageId(i);
		            fdi.setFileName("picture.png" );
		            fdi.setFileSize(10000);
		            fdi.setPageSize(100);
		            fileDetailInfoList.add(fdi);
		        }
				
				//ダミーファイル２
				for(int i = 0; i < 3; i++) {
		            FileDetailInfo fdi = new FileDetailInfo();
		            fdi.setFileId(2);
		            fdi.setPageId(i);
		            fdi.setFileName("picture2.png" );
		            fdi.setFileSize(20000);
		            fdi.setPageSize(200);
		            fileDetailInfoList.add(fdi);
		        }
				*/
				String fileDetailInfoListJson = gson.toJson(fileDetailInfoList);
				//連番、1から
				seqNo = 1;
				//byte変換
				tempMsg = fileDetailInfoListJson.getBytes(Charset.forName("UTF-8"));
				//長さ
				length = tempMsg.length;
				//レコード件数
				recordCount = length / 512 + 1;
				while(length >= 512) {
					a = new byte[512];
					System.arraycopy(tempMsg, tempMsg.length - length, a, 0, 512);
					ssn.write(new NorResponseObj(2201, 512, tempMsg.length, seqNo++, recordCount, a));
					length -= 512;
				}
				a = new byte[512];
				System.arraycopy(tempMsg, tempMsg.length - length, a, 0, length);
				ssn.write(new NorResponseObj(2201, 512, tempMsg.length, seqNo++, recordCount, a));

				//ssn.write(new NorResponseObj(2201, -1, -1, 1, 1, fileDetailInfoListJson));	
				break;
			case 2102:
				//ファイル転送
				//TODO DBから取得するように修正する予定
				System.out.println("2102 main process start..:");
				Type listType = new TypeToken<FileDetailInfo>(){}.getType();
				FileDetailInfo fileDetailInfo = new Gson().fromJson(nreo.getMessage(), listType);
				
				FileInputStream fis = new FileInputStream(new File(fileDetailInfo.getPath() + fileDetailInfo.getName()));
				//ファイルのサイズ
				int fileSize = fis.available();
				//連番、1から
				seqNo = 1;
				//レコード件数
				recordCount = fileSize / 512 + 1;
				a = new byte[512];
				while((currentSize = fis.read(a, 0, a.length)) != -1) {
					ssn.write(new NorResponseObj(2202, currentSize, fileSize, seqNo++, recordCount, a));
				}
				break;
			case 3101:
				//ページ情報転送
				System.out.println("3101 main process start..:");
				System.out.println("ページ：" + nreo.getMessage());
				//ルーム番号
				roomId = (Integer)ssn.getAttribute("roomId");
				ssns = ssn.getService().getManagedSessions().values();
				for(IoSession ossn : ssns) {
					//自分以外の同じルームにいるしかも同期中のメンバのみに送信
					if(ossn.getId() != ssn.getId() 
							&& roomId == (Integer)ssn.getAttribute("roomId")
							&& !"inactive".equals((String)ossn.getAttribute("status"))) {
						ossn.write(new NorResponseObj(3102, -1, -1, 1, 1, nreo.getMessage()));	
					}
				}
				break;
			case 3201:
				//マーカ転送
				System.out.println("3201 main process start..:");
				System.out.println("マーカ：" + nreo.getMessage());
				//ほかの端末に転送
				//ルーム番号
				roomId = (Integer)ssn.getAttribute("roomId");
				ssns = ssn.getService().getManagedSessions().values();
				for(IoSession ossn : ssns) {
					//自分以外の同じルームにいるしかも同期中のメンバのみに送信
					if(ossn.getId() != ssn.getId() 
							&& roomId == (Integer)ssn.getAttribute("roomId")
							&& !"inactive".equals((String)ossn.getAttribute("status"))) {
						//ossn.write(new NorResponseObj(3202, -1, -1, 1, 1, nreo.getMessage()));	
					}
				}
				break;
			default:
				System.out.println("??? main process start..:");
				//Collection<IoSession> ssns = ssn.getService().getManagedSessions().values();
				//for(IoSession issns : ssns) {
				//	if(issns.getId() != id)
				//		issns.write((String)message);
				//}
		}
	}
	
	@Override
	public void sessionIdle(IoSession ssn, IdleStatus status) throws Exception{
		//System.out.println("IDLE " + ssn.getIdleCount(0));
	}
	
	static class ConfRoomContext extends AbstractStateContext {   
	    public String contextName;   
	}
}