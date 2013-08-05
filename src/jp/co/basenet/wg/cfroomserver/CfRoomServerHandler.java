package jp.co.basenet.wg.cfroomserver;

import static org.apache.mina.statemachine.event.IoHandlerEvents.MESSAGE_RECEIVED;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

import jp.co.basenet.wg.cfroomserver.beans.FileDetailInfo;
import jp.co.basenet.wg.cfroomserver.beans.NorRequestObj;
import jp.co.basenet.wg.cfroomserver.beans.NorResponseObj;
import jp.co.basenet.wg.cfroomserver.beans.RoomButtonInfo;
import jp.co.basenet.wg.cfroomserver.beans.RoomDetailInfo;
import jp.co.basenet.wg.cfroomserver.beans.UserInfo;

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
		UserInfo userInfo = (UserInfo)message;
		System.out.println("login main process start..:");
		System.out.println("UserName: " + userInfo.getUserName());
		System.out.println("Password: " + userInfo.getPassword());
		
		String result;
		if("base".equals(userInfo.getUserName()) && "base".equals(userInfo.getPassword())) {
			System.out.println("login success..");
			result = "SUCCESS";
		} else {
			System.out.println("login failure..");
			result = "FAILURE";
		}
		ssn.setAttribute("roomId", "-1");
		ssn.setAttribute("status", "inactive");
		
		ssn.write(new NorResponseObj(0001, -1, -1, 1, 1, result));
	}

	@IoHandlerTransition(on = MESSAGE_RECEIVED, in = CONNECTED)
	public void notFirstConnect(ConfRoomContext myContext, IoSession ssn, Object message) throws Exception {
		NorRequestObj nreo = (NorRequestObj)message;
		System.out.println("Message read:");
		System.out.println(nreo.getMessage());
		int roomId;
		
		//sessionID取得
		long id = ssn.getId();
		Gson gson = new Gson();
		switch(nreo.getStatus()) {
			case 1101:
				//ロビーの会議室一覧を返す
				//TODO
				System.out.println("1101 main process start..:");
				ArrayList<RoomButtonInfo> roomButtonList = new ArrayList<RoomButtonInfo>();
				for(int i = 0; i < 6; i++) {
		            RoomButtonInfo rbi = new RoomButtonInfo();
		            rbi.setId(i);
		            rbi.setRoomName("ダミルーム" + i);
		            rbi.setStatus("進行中");
		            roomButtonList.add(rbi);
		        }
				String roomButtonListJson = gson.toJson(roomButtonList);
				ssn.write(new NorResponseObj(1201, -1, -1, 1, 1, roomButtonListJson));				
				break;
			case 1102:
				//会議室詳細情報を返す
				//TODO
				System.out.println("1102 main process start..:");
				RoomDetailInfo roomDetailInfo = new RoomDetailInfo();
				roomDetailInfo.setId(Integer.parseInt(nreo.getMessage()));
				roomDetailInfo.setMeetingName("会議室-" + nreo.getMessage());
				roomDetailInfo.setLocate("本社");
				roomDetailInfo.setStartTime("9999/99/99 99:99:99");
				roomDetailInfo.setEndTime("9999/99/99 99:99:99");
				roomDetailInfo.setChairManName("ベース　太郎");
				roomDetailInfo.setChairManUserId("001");
				String roomDetailInfoJson = gson.toJson(roomDetailInfo);
				ssn.write(new NorResponseObj(1202, -1, -1, 1, 1, roomDetailInfoJson));	
				break;
			case 1103:
				//入室
				System.out.println("1103 main process start..:");
				roomId = Integer.parseInt(nreo.getMessage());
				String result;
				if(roomId == 5) {
					System.out.println("enter success..");
					result = "SUCCESS";
				} else {
					System.out.println("enter failure..");
					result = "FAILURE";
				}
				ssn.setAttribute("roomId", roomId);
				ssn.write(new NorResponseObj(1203, -1, -1, 1, 1, result));	
				break;
			case 2101:
				//ファイルリスト情報リクエスト
				System.out.println("2101 main process start..:");
				System.out.println("roomID: " + ssn.getAttribute("roomId"));
				ArrayList<FileDetailInfo> fileDetailInfoList = new ArrayList<FileDetailInfo>();
				
				//ダミーファイル１
				for(int i = 0; i < 2; i++) {
		            FileDetailInfo fdi = new FileDetailInfo();
		            fdi.setFileId(1);
		            fdi.setPageId(i);
		            fdi.setFileName("picture.png" );
		            fdi.setFileSize(10000);
		            fdi.setPageSize(100);
		            fileDetailInfoList.add(fdi);
		        }
				
				//ダミーファイル２
				for(int i = 0; i < 2; i++) {
		            FileDetailInfo fdi = new FileDetailInfo();
		            fdi.setFileId(2);
		            fdi.setPageId(i);
		            fdi.setFileName("picture2.png" );
		            fdi.setFileSize(20000);
		            fdi.setPageSize(200);
		            fileDetailInfoList.add(fdi);
		        }
				String fileDetailInfoListJson = gson.toJson(fileDetailInfoList);
				ssn.write(new NorResponseObj(2201, -1, -1, 1, 1, fileDetailInfoListJson));	
				break;
			case 2102:
				//ファイル転送
				//TODO DBから取得するように修正する予定
				System.out.println("2102 main process start..:");
				Type listType = new TypeToken<FileDetailInfo>(){}.getType();
				FileDetailInfo fileDetailInfo = new Gson().fromJson(nreo.getMessage(), listType);
				
				//String filePath = "picture.png";
				FileInputStream fis = new FileInputStream(new File(fileDetailInfo.getFileName()));
				//ファイルのサイズ
				int fileSize = fis.available();
				//当レコードのサイズ
				int currentSize;
				//連番、1から
				int seqNo = 1;
				//レコード件数
				int recordCount = fileSize / 512 + 1;
				byte[] a = new byte[512];
				while((currentSize = fis.read(a, 0, a.length)) != -1) {
					ssn.write(new NorResponseObj(2202, currentSize, fileSize, seqNo++, recordCount, a));
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