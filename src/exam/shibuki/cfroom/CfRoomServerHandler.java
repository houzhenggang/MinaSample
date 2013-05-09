package exam.shibuki.cfroom;

import static org.apache.mina.statemachine.event.IoHandlerEvents.MESSAGE_RECEIVED;

import java.nio.ByteBuffer;
import java.util.Collection;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.statemachine.annotation.IoHandlerTransition;
import org.apache.mina.statemachine.annotation.State;
import org.apache.mina.statemachine.context.AbstractStateContext;
import org.apache.mina.statemachine.event.Event;


public class CfRoomServerHandler extends IoHandlerAdapter {
	@State public static final String ROOT = "root";
	@State(ROOT) public static final String NOT_CONNECTED = "NotConnected";
	@State(ROOT) public static final String IDLE = "Idle";
	@State(ROOT) public static final String CREATE_ROOM = "CreateRoom";
	@State(ROOT) public static final String MEETING = "Meeting";
	@State(ROOT) public static final String JOIN_ROOM = "JoinRoom";
	
	@IoHandlerTransition(in = ROOT, weight = 100) 
	public void unhandledEvent(Event event) { 
		System.out.println("Warning..");
	} 
	
	@Override
	public void exceptionCaught(IoSession ssn, Throwable cause) throws Exception {
		cause.printStackTrace();
	}
	
	@IoHandlerTransition(on = MESSAGE_RECEIVED, in = NOT_CONNECTED, next = IDLE)
	public void Login(ConfRoomContext context, IoSession ssn, Object message) throws Exception {
		UserInfo userInfo = (UserInfo)message;
		System.out.println("Message read:");
		System.out.println("UserName: " + userInfo.getUserName());
		System.out.println("Password: " + userInfo.getPassword());
		
		String result;
		if("base".equals(userInfo.getUserName()) && "base".equals(userInfo.getPassword())) {
			result = "SUCCESS";
		} else {
			result = "FAILURE";
		}

		ByteBuffer buffer = ByteBuffer.allocate(1024);
		buffer.putInt(0);
		buffer.putInt(result.getBytes("UTF-8").length);
		buffer.put(result.getBytes("UTF-8"));
		buffer.flip();
		byte[] bytes = new byte[buffer.remaining()];
		buffer.get(bytes);
		ssn.write(new String(bytes, "UTF-8"));
	}

	@IoHandlerTransition(on = MESSAGE_RECEIVED, in = IDLE)
	public void notFirstConnect(ConfRoomContext myContext, IoSession ssn, Object message) throws Exception {
		String str = message.toString();
		System.out.println("Message read:");
		System.out.println(str);
		
		//sessionIDŽæ“¾
		long id = ssn.getId();
		
		Collection<IoSession> ssns = ssn.getService().getManagedSessions().values();
		for(IoSession issns : ssns) {
			if(issns.getId() != id)
				issns.write((String)message);
		}
	}
	
	@Override
	public void sessionIdle(IoSession ssn, IdleStatus status) throws Exception{
		System.out.println("IDLE " + ssn.getIdleCount(status));
	}
	
	static class ConfRoomContext extends AbstractStateContext {   
	    public String contextName;   
	}
}