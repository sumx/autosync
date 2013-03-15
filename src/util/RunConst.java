package util;

/**
 * 常量的单例
 * 
 * @author wlstyle
 * 
 */
public class RunConst {

	private static RunConst instance = null;

	protected RunConst() {
	}

	public static RunConst getInstance() {
		if (instance == null) {
			instance = new RunConst();
		}
		return instance;
	}

	/**
	 * 操作文件的根目录
	 */
	private String fileRoot;

	public void setFileRoot(String fileroot) {
		fileRoot = fileroot;
	}

	public String getFileRoot() {
		return fileRoot;
	}

	/**
	 * 文件更新的时间
	 */
	private int fileUpdateInterval = 10000;

	public void setFileUpdateInterval(String fileupdateinterval) {
		try {
			int interval = Integer.parseInt(fileupdateinterval);
			fileUpdateInterval = interval;
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
		}

	}

	public int getFileUpdateInterval() {
		return fileUpdateInterval;
	}

	/**
	 * bin文件名称
	 */
	private String binFileName = "md5Index.bin";

	public void setBinFileName(String binfilename) {
		binFileName = binfilename;
	}

	public String getBinFileName() {
		return binFileName;
	}

	/**
	 * 连接到远程主机的名称
	 * 
	 */
	private String hostName = "";

	public void setHostName(String hostname) {
		hostName = hostname;
	}

	public String getHostName() {
		return hostName;
	}

	/**
	 * 远程主机的登陆用户名称
	 */
	private String userName = "";

	public void setUserName(String username) {
		userName = username;
	}

	public String getUserName() {
		return userName;
	}

	/**
	 * 远程主机的登陆用户登陆密码
	 */
	private String passWord = "";

	public void setPassWord(String password) {
		passWord = password;
	}

	public String getPassWord() {
		return passWord;
	}

	/**
	 * 远程服务器的对应的目录
	 */
	private String remoteFold = "";

	public void setRemoteFold(String remotefold) {
		remoteFold = remotefold;
	}

	public String getRemoteFold() {
		return remoteFold;
	}

	/**
	 * 更新md5值线程的休眠时间
	 */
	private int upDateMd5Interval = 600000;

	public void setUpDateMd5Interval(String updatemd5interval) {
		try {
			int interval = Integer.parseInt(updatemd5interval);
			upDateMd5Interval = interval;
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
		}
	}

	public int getUpDateMd5Interval() {
		return upDateMd5Interval;
	}
	
	/**
	 * 是否启用保存一份原始文件
	 */
	private boolean isSaveLocalFile = true;
	
	public void setIsSaveLocalFile(String issavelocalfile) {
		try {
			//文件地址
			if(issavelocalfile=="true"){
				isSaveLocalFile=true;
			}else{
				isSaveLocalFile=false;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public boolean getIsSaveLocalFile() {
		return isSaveLocalFile;
	}
	
	
	/**
	 * 是否启用保存一份原始文件
	 */
	private boolean isGetFile = true;
	
	public void setIsGetFile(String issavelocalfile) {
		try {
			//文件地址
			if(issavelocalfile=="true"){
				isGetFile=true;
			}else{
				isGetFile=false;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public boolean getIsGetFile() {
		return isGetFile;
	}
	
	
	

}
