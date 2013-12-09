package jp.co.basenet.wg.cfroomserver;

import java.nio.charset.Charset;

import jp.co.basenet.wg.cfroomserver.model.NorResponseObj;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class CfRoomRequestEncoder implements ProtocolEncoder{

	@Override
	public void dispose(IoSession ssn) throws Exception {
		//âΩÇ‡èëÇ©Ç»Ç≠ÇƒÇ¢Ç¢
	}

	@Override
	public void encode(IoSession ssn, Object message, ProtocolEncoderOutput out)
			throws Exception {
		System.out.println("response message: " + ((NorResponseObj)message).getMessage() );
		int status = ((NorResponseObj)message).getStatus();
		int currentSize = ((NorResponseObj)message).getCurrentSize();
		int fullSize = ((NorResponseObj)message).getFullSize();
		int seqNo = ((NorResponseObj)message).getSeqNo();
		int recordCount = ((NorResponseObj)message).getRecordCount();
		byte[] msg = new byte[512];
		if(((NorResponseObj)message).getMessage() != null) {
			byte[] tempMsg = ((NorResponseObj)message).getMessage().getBytes(Charset.forName("UTF-8"));
			System.arraycopy(tempMsg, 0, msg, 0, tempMsg.length);
		} else {
			msg =  ((NorResponseObj)message).getMessageB();
		}
		if(currentSize == -1) {
			currentSize = msg.length;
			fullSize = msg.length;
		}
		// STATUS CURRENT_SIZE FULL_SIZE SEQNO RECORDCOUNT MESSAGE
		// 4      4            4         4     4           512
		IoBuffer buffer = IoBuffer.allocate(20 + 512);
		buffer.putInt(status);
		buffer.putInt(currentSize);
		buffer.putInt(fullSize);
		buffer.putInt(seqNo);
		buffer.putInt(recordCount);
		buffer.put(msg);
		buffer.flip();
		out.write(buffer);
	}
}
