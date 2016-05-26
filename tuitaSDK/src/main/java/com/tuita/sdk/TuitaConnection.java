package com.tuita.sdk;

import com.tuita.sdk.TuitaSDKManager.*;
import com.zhongsou.souyue.log.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * @author fangxm@zhongsou.com
 *
 */
class TuitaConnection {
	protected static final int TUITA_CONNECT_DELAY_SECOND = 10;
	protected static final int TUITA_CONNECT_TIMEOUT = 8000;
	private static final int LEN_PACK_LENGTH_SHORT = 2;
	private static final int LEN_PACK_LENGTH = 4;
	private static final int BUFFER_SIZE = 256;
	private TuitaSDKManager manager;
	private TuitaIMManager imManager;
	private Socket client;
	private Thread wThread;
	private Thread rThread;
	private ReentrantLock lock = new ReentrantLock();
	private final BlockingQueue<TuitaPacket> writeQueue = new ArrayBlockingQueue<TuitaPacket>(10, true);
	
	protected TuitaConnection(TuitaSDKManager manager) {
		this.manager = manager;
		this.imManager=TuitaIMManager.getInstance(manager);
	}

	private void afterConnect() {
		/*if (manager.getTuitaState() != TuitaSDKManager.TUITA_STATE_CONNECT) {
			manager.setTuitaState(TuitaSDKManager.TUITA_STATE_CONNECT);
		}*/
//		manager.setTuitaLiveTime(System.currentTimeMillis());
		for (ConnectListener listener : manager.getConnectListeners()) {
			if (! listener.connect(this)) {
				break;
			}
		}
	}
	
	private void afterRead(TuitaPacket packet) {
		/*if (manager.getTuitaState() != TuitaSDKManager.TUITA_STATE_CONNECT) {
			manager.setTuitaState(TuitaSDKManager.TUITA_STATE_CONNECT);
		}*/
//		manager.setTuitaLiveTime(System.currentTimeMillis());
		Log.i("","----->afterRead");
		for (ReadListener listener : manager.getReadListeners()) {
			if (! listener.read(this, packet)) {
				break;
			}
		}
	}
	
	private void afterWrite(TuitaPacket packet) {
		/*if (manager.getTuitaState() != TuitaSDKManager.TUITA_STATE_CONNECT) {
			manager.setTuitaState(TuitaSDKManager.TUITA_STATE_CONNECT);
		}*/
//		manager.setTuitaLiveTime(System.currentTimeMillis());
		Log.i("","----->afterWrite");
		for (WriteListener listener : manager.getWriteListeners()) {
			if (! listener.write(this, packet)) {
				break;
			}
		}
	}
	
	private void catchError(Throwable e, Operations op, String message) {
		for (ErrorListener listener : manager.getErrorListeners()) {
			if (! listener.error(this, e, op, message)) {
				break;
			}
		}
	}

	protected boolean connect() {
		boolean result = false;
        long connectStartTime = System.currentTimeMillis();
		String[] tmp = manager.getNodeHost().split(":");
		String host = null;
		int port = 0;
		try {
			host = tmp[0];
			port = Integer.parseInt(tmp[1]);
		} catch (Throwable e) {
            manager.saveInfo(TuitaSDKManager.TUITA_HOST,"");
			Log.i(TuitaSDKManager.TAG, "Node Host String Pattern Error..." + manager.getNodeHost());
			Logger.e("tuita", "TuitaConnection.connect", "connect Host String Pattern Error..." + manager.getNodeHost(), e);
			return result;
		}
		if (manager.getTuitaState() == TuitaSDKManager.TUITA_STATE_CONNECT) {
			Log.i(TuitaSDKManager.TAG, "already tuita connect");
			Logger.i("tuita","TuitaConnection.connect", "tuita already connect");
			return true;
		}
		if (manager.getConnState() == TuitaSDKManager.CONN_STATE_CONNECTING) {
			Log.i(TuitaSDKManager.TAG, "tuita connecting return");
			Logger.i("tuita","TuitaConnection.connect", "tuita state = connecting");
			return true;
		}
		
	    try {
	    	//Log.i(TuitaSDKManager.TAG, "connect state ..." + manager.getTuitaState());
	    	lock.lock();
	    	//重连之前先释放资源
			disconnect();
	    	Log.i(TuitaSDKManager.TAG, "try connect to ..." + manager.getNodeHost());
			Logger.i("tuita","TuitaConnection.connect", "TuitaConnection connect method try connect to ..." + manager.getNodeHost());
	    	client = new Socket();
			client.connect(new InetSocketAddress(host, port), TUITA_CONNECT_TIMEOUT);
//			manager.setConnState(TuitaSDKManager.CONN_STATE_CONNECTING);
			
			afterConnect();

			wThread = new Thread(new PacketWriter(new DataOutputStream(client.getOutputStream())));
			rThread = new Thread(new PacketReader(new DataInputStream(client.getInputStream())));
			wThread.start();
			rThread.start();
			result = true;
            manager.saveInfo(TuitaSDKManager.TUITA_HOST,host+":"+port);
            Logger.i("tuita", "TuitaConnection.connect", "IM Connection Success");
            Logger.i("tuita","TuitaConnection.connect","IM Connection try time is--------->"+(System.currentTimeMillis() - connectStartTime));
		} catch (Throwable e) {
            //可用ip清除
            manager.saveInfo(TuitaSDKManager.TUITA_HOST,"");
            result = false;
			Logger.e("tuita","TuitaConnection.connect","IM Connection fail",e);
            Logger.i("tuita","TuitaConnection.connect","IM Connection catch time is--------->"+(System.currentTimeMillis() - connectStartTime));
			e.printStackTrace();
		} finally {
            Logger.i("tuita","TuitaConnection.connect","IM Connection finally time is--------->"+(System.currentTimeMillis() - connectStartTime));
//			manager.getScheduler().schedule(new Runnable() {
//				@Override
//				public void run() {
//					if (manager.getConnState() == TuitaSDKManager.CONN_STATE_CONNECTING) {
//						manager.setConnState(TuitaSDKManager.CONN_STATE_NOTCONNECT);
//					}
//				}
//			}, TUITA_CONNECT_DELAY_SECOND, TimeUnit.SECONDS);
			lock.unlock();
		}

		return result;
	}
	
	private void read(TuitaPacket packet) {
		Log.i(TuitaSDKManager.TAG, "read ..." + packet.toString());
		Log.i(TuitaSDKManager.TAG, "read ..." + imManager.getTuitaIMState());
		afterRead(packet);
	}
	
	protected boolean keepAlive() {
		if (wThread.isAlive() && rThread.isAlive()) {
			write(TuitaPacket.createPingPacket());
			manager.getPingNoAckCount().incrementAndGet();
			return true;
		} else {
			Logger.i("tuita","TuitaConnection.keepAlive","keepAlive == false");
//			disconnect();
            manager.setTuitaState(TuitaSDKManager.TUITA_STATE_DISCONNECT);
			return connect();
		}
	}
	
	protected void write(TuitaPacket packet) {
		Log.i(TuitaSDKManager.TAG, "write ..." + packet.toString());
		if(packet!=null){
			Logger.i("tuita","TuitaConnection.write","write  t = "+packet.getType()+" msg = "+new String(packet.toString().toCharArray(),0,packet.toString().length()>200?200:packet.toString().length()));
		}else{
			Logger.i("tuita","TuitaConnection.write","packet == null");
		}
		try {
            if (writeQueue.size() >= 10){
                writeQueue.clear();
            }
			writeQueue.put(packet);
		} catch (InterruptedException e) {
			Logger.e("tuita","TuitaConnection.write","socket write fail t = " + packet.getType(),e,"t",packet.getType()+"","tid",packet.getTid()+"");
			e.printStackTrace();
		}
		
	}
	
	protected void disconnect() {
		/*if (manager.getTuitaState() == TuitaSDKManager.TUITA_STATE_CONNECT) {
			manager.setTuitaState(TuitaSDKManager.TUITA_STATE_DISCONNECT);
		}*/
		if (client != null) {
			try {
				client.close();
			} catch (Exception e) {
				catchError(e, Operations.DISCONNECT, "disconnect");
			}
		}
		if (wThread != null) {
			wThread.interrupt();
		}
		if (rThread != null) {
			rThread.interrupt();
		}
	}

    private class PacketWriter implements Runnable {
        private OutputStream stream_out;
        private PacketWriter(OutputStream out) {
            this.stream_out = out;
        }
        @Override
        public void run() {
            TuitaPacket pack = null;
            while (true) {
                try {
                    if (Thread.interrupted()) {
                        throw new InterruptedException();
                    }
                    pack = writeQueue.take();
                    stream_out.write(pack.toBytes());
                    stream_out.flush();
                    afterWrite(pack);
                } catch (InterruptedException e) {
                    Logger.e("tuita","PacketWriter.run","package write InterruptedException",e);
                    if (manager.getTuitaState() == TuitaSDKManager.TUITA_STATE_CONNECT) {
                        manager.setTuitaState(TuitaSDKManager.TUITA_STATE_DISCONNECT);
                        manager.start();  //直接重连
                    }
                    break;
                } catch (Throwable e) {
                    Logger.e("tuita","PacketWriter.run","package write error",e);
                    if (manager.getTuitaState() == TuitaSDKManager.TUITA_STATE_CONNECT) {
                        manager.setTuitaState(TuitaSDKManager.TUITA_STATE_DISCONNECT);
                        manager.start();  //直接重连
                    }
                    catchError(e, Operations.PACK_WRITE, "packetWrite");
                    break;
                }
            }
        }
    }

	private class PacketReader implements Runnable {
		private InputStream stream_in;
		
		private PacketReader(InputStream in) {
			this.stream_in = in;
		}
		
		@Override
		public void run() {
			int oneReadCount = 0;
			int readOffset = 0;
			int packReadCount = 0;
			int dataLen = 0;
			int copyOffset = 0;
			int dataLenOffset = 0;
			int len_pack_length = LEN_PACK_LENGTH;
			byte[] buffer = new byte[BUFFER_SIZE];
			byte[] tmpShortLen = new byte[LEN_PACK_LENGTH_SHORT];
			byte[] tmpLen = new byte[LEN_PACK_LENGTH];
			byte[] tmpDate = null;		
			TuitaPacket packet = null;
			boolean nextRead = true;
			boolean nextPack = true;
			while (true) {
				try {
					if (nextRead) {
                        oneReadCount = stream_in.read(buffer);
						if (oneReadCount <= 0) break;
						readOffset = 0;
						packReadCount += oneReadCount;
					} else {
						packReadCount = oneReadCount - readOffset;
					}
					if (Thread.interrupted()) {
						throw new InterruptedException();
					}
					
					if (dataLen == 0) {
						if (packReadCount < len_pack_length) {
							if (len_pack_length == LEN_PACK_LENGTH_SHORT) {
								System.arraycopy(buffer, readOffset, tmpShortLen, dataLenOffset, packReadCount);
							} else {
								System.arraycopy(buffer, readOffset, tmpLen, dataLenOffset, packReadCount);
							}
							
							dataLenOffset += packReadCount;
							nextRead = true;
							continue;
						} else {
							if (len_pack_length == LEN_PACK_LENGTH_SHORT) {
								System.arraycopy(buffer, readOffset, tmpShortLen, dataLenOffset, len_pack_length-dataLenOffset);
								readOffset += len_pack_length-dataLenOffset;
								dataLen = TuitaPacket.byteToShort(tmpShortLen);
							} else {
								System.arraycopy(buffer, readOffset, tmpLen, dataLenOffset, len_pack_length-dataLenOffset);
								readOffset += len_pack_length-dataLenOffset;
								dataLen = TuitaPacket.byteToInt(tmpLen);
							}

							dataLenOffset = 0;
							tmpDate = new byte[dataLen];
						}
					}
					if (packReadCount > dataLen+len_pack_length) {
						System.arraycopy(buffer, readOffset, tmpDate, copyOffset, dataLen-copyOffset);
						readOffset += dataLen-copyOffset;
						
						packet = new TuitaPacket(tmpDate);
						read(packet);
						if (packet.getType() == TuitaPacket.TUITA_MSG_TYPE_CONNECT) {
							len_pack_length = LEN_PACK_LENGTH;
						}
						nextPack = true;
						nextRead = false;
					} else if (packReadCount == dataLen+len_pack_length) {
						System.arraycopy(buffer, readOffset, tmpDate, copyOffset, dataLen-copyOffset);
						
						packet = new TuitaPacket(tmpDate);
						read(packet);
						if (packet.getType() == TuitaPacket.TUITA_MSG_TYPE_CONNECT) {
							len_pack_length = LEN_PACK_LENGTH;
						}
						nextPack = true;
						nextRead = true;
					} else {
						if (oneReadCount > readOffset) {
							System.arraycopy(buffer, readOffset, tmpDate, copyOffset, oneReadCount-readOffset);
							copyOffset += oneReadCount-readOffset;
						}
						nextPack = false;
						nextRead = true;
					}
					if (nextPack) {
						dataLen = 0;
						copyOffset = 0;
						packReadCount = 0;
					}
					if (packet != null) {
						Logger.i("tuita", "PacketReader.run", "read packet = " + new String(packet.toString().toCharArray(), 0, packet.toString().length() > 200 ? 200 : packet.toString().length()), "t",packet.getType()+"","tid",packet.getTid()+"");
					}else{
						Logger.i("tuita", "PacketReader.run", " packet = null");
					}
				} catch (InterruptedException e) {
					Logger.i("tuita", "PacketReader.run", "package read InterruptedException t =" + packet, e,"t",packet+"","tid",packet+"");
					if (manager.getTuitaState() == TuitaSDKManager.TUITA_STATE_CONNECT) {
						manager.setTuitaState(TuitaSDKManager.TUITA_STATE_DISCONNECT);
                        manager.start();  //直接重连
					}
					break;
				} catch (Throwable e) {
					Logger.e("tuita", "PacketReader.run", "package read error  t =" + packet, e,"t",packet+"","tid",packet+"");
					if (manager.getTuitaState() == TuitaSDKManager.TUITA_STATE_CONNECT) {
						manager.setTuitaState(TuitaSDKManager.TUITA_STATE_DISCONNECT);
                        manager.start();  //直接重连
					}
					e.printStackTrace();
					break;
				}
			}
		}
	}

}
