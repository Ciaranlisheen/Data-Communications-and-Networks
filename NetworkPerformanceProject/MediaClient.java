package NetworkPerformanceProject;

import java.io.*;
import java.net.*;
import java.util.*;


public class MediaClient {

public static DataPacket convert(byte[] buf) throws Exception{

		ByteArrayInputStream byteStream = new ByteArrayInputStream(buf);
		ObjectInputStream is = new ObjectInputStream(new
				BufferedInputStream(byteStream));
				DataPacket pk = (DataPacket)is.readObject();
		is.close();
		return pk;
	}

	public static void main(String[] args) throws Exception {
		
		DatagramSocket socket = new DatagramSocket();
		int currentSequenceNo = 0;
		int prevSequenceNo = 0;
		int tests = 0;
		int loss = 0;
		int successful = 0;
		double alpha = 0.125d;
		double beta = 0.25d;
		double throughput = 0d;
		double lossrate = 0d;
		double packetdelay = 0d;
		double prevpacketdelay = 0d;
		double jitter = 0d;
		
		
		
		// send request
		byte[] buf = new byte[1000];
		InetAddress address = InetAddress.getByName("localhost");
		DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 5500);
		socket.send(packet);
		long statInterval = 1000; // 1s
		long lastTime = System.currentTimeMillis();
		
		
		
		// get response
		while(currentSequenceNo < 100000){
			packet = new DatagramPacket(buf, buf.length);
			socket.receive(packet);
			//System.out.println("got packet");
			successful++;	//i added
			DataPacket pk = convert(packet.getData());
			long currentTime = System.currentTimeMillis();
			if(pk.seq < prevSequenceNo) prevSequenceNo=pk.seq;
			currentSequenceNo = pk.seq;
			if(currentTime > lastTime + statInterval)
			{
		//Compute and display network parameters (throughput, loss, delay, jitter)
				loss = (currentSequenceNo-prevSequenceNo) - successful;
				if(tests == 0) {
					throughput = successful;
					lossrate = loss;
					packetdelay = (currentTime - pk.time);
					jitter = Math.abs(packetdelay-prevpacketdelay);
		}
		else {
			throughput = (1-alpha)*throughput + alpha*(successful);
			lossrate = (1-alpha)*lossrate + alpha*(loss);
			packetdelay = (1-alpha)*packetdelay + alpha*(currentTime - pk.time);
			jitter = (1-beta)*jitter + beta*(Math.abs(packetdelay-prevpacketdelay));
		}
		lastTime = currentTime;
		prevSequenceNo = currentSequenceNo;
		System.out.println("Throughput = "+throughput+" packets per second");
		System.out.println("Lossrate = "+lossrate+" packets per second");
		System.out.println("Packet delay = "+packetdelay+"ms");
		System.out.println("Jitter = "+jitter+"ms");
		successful = 0;
		tests++;
	}
	else prevpacketdelay = (currentTime-pk.time);
	}
socket.close();
}
}
