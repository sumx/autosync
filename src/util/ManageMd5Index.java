package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 * �ļ�md5ֵ�Ĺ�����
 * @author wlstyle
 *
 */
public class ManageMd5Index {
	
	private static ManageMd5Index instance=null;
	
	protected ManageMd5Index(){}
	
	public static ManageMd5Index getInstance(){
		if(instance==null){
			instance=new ManageMd5Index();
		}
		return instance;
	}
	
	// ����ʱ��������
	private RunConst runConst = RunConst.getInstance();
	
	//���б����ļ���md5������map
	private HashMap fileMd5Index=new HashMap();
	
	public HashMap getMd5Hash(){
		return  fileMd5Index;
	}
	
	/**
	 * ����ĳ���ļ���md5ֵ
	 * @param filename
	 * @param md5value
	 */
	public void insertFileMd5(String filename,String md5value){
		fileMd5Index.put(filename, md5value);
	}
	
	/**
	 * ͨ���ļ����õ��ļ���md5ֵ
	 * @param filename
	 * @return
	 */
	public String getMd5ByFileName(String filename){
		if(fileMd5Index.containsKey(filename)){
			return (String) fileMd5Index.get(filename);
		}else{
			return "";
		}
	}
	
	/**
	 * ��ȡ����md5���õ��ļ�����û���򴴽�һ��
	 */
	public void readFileMd5Index() {
		File file = new File(runConst.getFileRoot() + File.separator
				+ runConst.getBinFileName());
		if (file.exists()) {
			try {
				// ����bin�ļ�
				ObjectInputStream input = new ObjectInputStream(
						new FileInputStream(file));
				// ��bin�ļ�����Ķ���ת��Ϊjava����
				fileMd5Index = (HashMap) input.readObject();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			if(!file.getParentFile().exists()){
				file.getParentFile().mkdirs();
			}
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	

	/**
	 * ��md5�����Ķ���������ļ�Ŀ¼��
	 */
	public void writeFileMd5Index() {
		// �洢�������ļ�
		File file = new File(runConst.getFileRoot() + File.separator
				+ runConst.getBinFileName());
		try {
			ObjectOutputStream output = new ObjectOutputStream(
					new FileOutputStream(file));
			output.writeObject(fileMd5Index);
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
