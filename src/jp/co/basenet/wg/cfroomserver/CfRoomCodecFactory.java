package jp.co.basenet.wg.cfroomserver;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class CfRoomCodecFactory implements ProtocolCodecFactory{
	private ProtocolEncoder encoder;
	private ProtocolDecoder decoder;
	
	public CfRoomCodecFactory() {
		encoder = new CfRoomRequestEncoder();
		decoder = new CfRoomRequestDecoder();
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession ssn) throws Exception {
		return decoder;
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession ssn) throws Exception {
		return encoder;
	}

}
