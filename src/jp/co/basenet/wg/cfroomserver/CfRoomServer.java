package jp.co.basenet.wg.cfroomserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.statemachine.StateMachine;
import org.apache.mina.statemachine.StateMachineFactory;
import org.apache.mina.statemachine.StateMachineProxyBuilder;
import org.apache.mina.statemachine.annotation.IoHandlerTransition;
import org.apache.mina.statemachine.context.IoSessionStateContextLookup;
import org.apache.mina.statemachine.context.StateContext;
import org.apache.mina.statemachine.context.StateContextFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;


public class CfRoomServer {

	private static final int PORT = 40005;
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		IoAcceptor acceptor = new NioSocketAcceptor();
		
		acceptor.getFilterChain().addLast("logger", new LoggingFilter());
		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new CfRoomCodecFactory()));
		acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(Executors.newCachedThreadPool()));  
		
		acceptor.setHandler(createIoHandler());
		acceptor.getSessionConfig().setReadBufferSize(2048);
		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
		if(args.length == 1) {
			acceptor.bind(new InetSocketAddress(Integer.parseInt(args[0])));
		} else {
			acceptor.bind(new InetSocketAddress(PORT));
		}
	}
	
	private static IoHandler createIoHandler() {
		StateMachine sm = StateMachineFactory.getInstance(
				IoHandlerTransition.class).create(CfRoomServerHandler.NOT_CONNECTED, new CfRoomServerHandler());
		return new StateMachineProxyBuilder().setStateContextLookup(
				new IoSessionStateContextLookup(new StateContextFactory() {
					
					@Override
					public StateContext create() {
						// TODO Auto-generated method stub
						return new CfRoomServerHandler.ConfRoomContext();
					}
				})).create(IoHandler.class, sm);
	}
}


