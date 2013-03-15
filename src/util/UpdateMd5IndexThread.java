package util;

/**
 * 将当前运行时的md5值写入到文件中
 * @author wlstyle
 *
 */
public class UpdateMd5IndexThread extends Thread {
	
	//md5索引对象
	private ManageMd5Index md5Index = ManageMd5Index.getInstance();
	//运行时常量对象
	private RunConst runConst=RunConst.getInstance();
	
	public void run(){
		while (!this.isInterrupted()) {
			//将当前对象中的值写入存储的文件中
			md5Index.writeFileMd5Index();
			try {
				//线程休眠一定时间
				sleep(runConst.getUpDateMd5Interval());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
