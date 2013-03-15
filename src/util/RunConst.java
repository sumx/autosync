package util;

/**
 * �����ĵ���
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
	 * �����ļ��ĸ�Ŀ¼
	 */
	private String fileRoot;

	public void setFileRoot(String fileroot) {
		fileRoot = fileroot;
	}

	public String getFileRoot() {
		return fileRoot;
	}

	/**
	 * �ļ����µ�ʱ��
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
	 * bin�ļ�����
	 */
	private String binFileName = "md5Index.bin";

	public void setBinFileName(String binfilename) {
		binFileName = binfilename;
	}

	public String getBinFileName() {
		return binFileName;
	}

	/**
	 * ���ӵ�Զ������������
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
	 * Զ�������ĵ�½�û�����
	 */
	private String userName = "";

	public void setUserName(String username) {
		userName = username;
	}

	public String getUserName() {
		return userName;
	}

	/**
	 * Զ�������ĵ�½�û���½����
	 */
	private String passWord = "";

	public void setPassWord(String password) {
		passWord = password;
	}

	public String getPassWord() {
		return passWord;
	}

	/**
	 * Զ�̷������Ķ�Ӧ��Ŀ¼
	 */
	private String remoteFold = "";

	public void setRemoteFold(String remotefold) {
		remoteFold = remotefold;
	}

	public String getRemoteFold() {
		return remoteFold;
	}

	/**
	 * ����md5ֵ�̵߳�����ʱ��
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
	 * �Ƿ����ñ���һ��ԭʼ�ļ�
	 */
	private boolean isSaveLocalFile = true;
	
	public void setIsSaveLocalFile(String issavelocalfile) {
		try {
			//�ļ���ַ
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
	 * �Ƿ����ñ���һ��ԭʼ�ļ�
	 */
	private boolean isGetFile = true;
	
	public void setIsGetFile(String issavelocalfile) {
		try {
			//�ļ���ַ
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
