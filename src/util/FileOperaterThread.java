package util;

import java.io.IOException;

/**
 * 文件任务的线程
 * @author wlstyle
 *
 */
public class FileOperaterThread extends Thread {
	
	//文件操作对象
	private FileOpreation fo=new FileOpreation();
	//运行时常量对象
	private RunConst runConst=RunConst.getInstance();
	
	public void run(){
		while(true){
			try {
				//如果配置了从远程取数据再去取
				if(runConst.getIsGetFile()){
					//首先调用拉去数据的操作
					fo.getFileFromServer();
				}
				//然后调用更新数据到服务器的操作
				fo.syscFileToServer();
				
			} catch (IOException ioerror) {
				System.out.print("--------拉去文件和更新文件操作异常---------");
				ioerror.printStackTrace();
			}
			try {
				//线程休眠一定时间
				sleep(runConst.getFileUpdateInterval());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
