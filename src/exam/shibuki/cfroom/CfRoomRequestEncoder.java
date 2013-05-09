package exam.shibuki.cfroom;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class CfRoomRequestEncoder implements ProtocolEncoder{

	@Override
	public void dispose(IoSession ssn) throws Exception {
		//何も書かなくていい
	}

	@Override
	public void encode(IoSession ssn, Object message, ProtocolEncoderOutput out)
			throws Exception {
		// TODO Auto-generated method stub
		byte[] bytes = ((String)message).getBytes();
		System.out.println("encode:" + (String)message);
		IoBuffer buffer = IoBuffer.allocate(bytes.length,false);
		buffer.put(bytes);
		buffer.flip();
		out.write(buffer);
	}
}
