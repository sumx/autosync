package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 * 文件md5值的管理类
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
	
	// 运行时常量对象
	private RunConst runConst = RunConst.getInstance();
	
	//所有本地文件在md5的索引map
	private HashMap fileMd5Index=new HashMap();
	
	public HashMap getMd5Hash(){
		return  fileMd5Index;
	}
	
	/**
	 * 设置某个文件的md5值
	 * @param filename
	 * @param md5value
	 */
	public void insertFileMd5(String filename,String md5value){
		fileMd5Index.put(filename, md5value);
	}
	
	/**
	 * 通过文件名得到文件的md5值
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
	 * 读取存有md5配置的文件，若没有则创建一个
	 */
	public void readFileMd5Index() {
		File file = new File(runConst.getFileRoot() + File.separator
				+ runConst.getBinFileName());
		if (file.exists()) {
			try {
				// 读入bin文件
				ObjectInputStream input = new ObjectInputStream(
						new FileInputStream(file));
				// 将bin文件里面的对象转化为java对象
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
	 * 将md5索引的对象输出到文件目录中
	 */
	public void writeFileMd5Index() {
		// 存储索引的文件
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
