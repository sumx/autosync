package util;

import java.io.IOException;

public class ShutDownWork extends Thread{
	// md5Index�Ĺ������

	private ManageMd5Index md5Index = ManageMd5Index.getInstance();
	
	public void run(){
		while (!this.isInterrupted()) {
			md5Index.writeFileMd5Index();
			this.interrupt();
		}

	}

}
