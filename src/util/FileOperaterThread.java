package util;

import java.io.IOException;

/**
 * �ļ�������߳�
 * @author wlstyle
 *
 */
public class FileOperaterThread extends Thread {
	
	//�ļ���������
	private FileOpreation fo=new FileOpreation();
	//����ʱ��������
	private RunConst runConst=RunConst.getInstance();
	
	public void run(){
		while(true){
			try {
				//��������˴�Զ��ȡ������ȥȡ
				if(runConst.getIsGetFile()){
					//���ȵ�����ȥ���ݵĲ���
					fo.getFileFromServer();
				}
				//Ȼ����ø������ݵ��������Ĳ���
				fo.syscFileToServer();
				
			} catch (IOException ioerror) {
				System.out.print("--------��ȥ�ļ��͸����ļ������쳣---------");
				ioerror.printStackTrace();
			}
			try {
				//�߳�����һ��ʱ��
				sleep(runConst.getFileUpdateInterval());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
