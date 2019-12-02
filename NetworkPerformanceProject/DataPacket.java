package NetworkPerformanceProject;

import java.io.*;

public class DataPacket implements Serializable {
	public DataPacket(int seq, byte[] data, long time) {
		this.seq = seq;
		this.data = data;
		this.time = time;
	}

	int seq;
	long time;
	byte[] data;
}