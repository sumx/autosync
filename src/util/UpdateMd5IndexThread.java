package util;

/**
 * ����ǰ����ʱ��md5ֵд�뵽�ļ���
 * @author wlstyle
 *
 */
public class UpdateMd5IndexThread extends Thread {
	
	//md5��������
	private ManageMd5Index md5Index = ManageMd5Index.getInstance();
	//����ʱ��������
	private RunConst runConst=RunConst.getInstance();
	
	public void run(){
		while (!this.isInterrupted()) {
			//����ǰ�����е�ֵд��洢���ļ���
			md5Index.writeFileMd5Index();
			try {
				//�߳�����һ��ʱ��
				sleep(runConst.getUpDateMd5Interval());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
