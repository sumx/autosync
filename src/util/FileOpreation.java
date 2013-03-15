package util;

import java.io.*;
import java.util.*;

import com.twmacinta.util.MD5;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import ch.ethz.ssh2.SCPClient;

/**
 * �ļ�������ʵ����
 * 
 * @author wlstyle
 * 
 */
public class FileOpreation implements FileOpreationInterface {

	// md5Index�Ĺ������

	private ManageMd5Index md5Index = ManageMd5Index.getInstance();
	// ����ʱ��������
	private RunConst runConst = RunConst.getInstance();
	
	//�����ļ��Ƿ���ڣ����ڵĻ�ǿ��дһ��
	
	private boolean everSysc=false;

	/**
	 * ���캯�����ļ�
	 */
	public FileOpreation() {
		// �õ������ļ������ö���
		md5Index.readFileMd5Index();
		if(md5Index.getMd5Hash().size()>0){
			everSysc=true;
		}
	}
	
	public ManageMd5Index getMd5Index(){
		return md5Index;
	}

	/**
	 * ��һ��ר�ŵ��߳����ӷ�������ȡ���ݷ�������ȥ����
	 */
	public void getFileFromServer() throws IOException {
		// ��ȡ�������˵��ļ�
		System.out.println("begin to getRemote File------");
		doGetRemoteFile();
		System.out.println("begin to getRemote File end------");
	}

	/**
	 * ͨ��һ����ʱ����ͬ���ļ����������ˡ� ����ʱ�����е�ʱ�����ļ��Ƿ��Ѿ������ �������Ļ�ͨ��scp����ͬ����������,û�б�������ļ����ύ
	 */
	public void syscFileToServer() {
		System.out.println("begin to sysclocal File------");
		doSyscFile();
		System.out.println("begin to sysclocal File end------");
	}

	private void doSyscFile() {
		// �ݹ鵱ǰĿ¼�������ǰ���ļ��б����ô����ôscp�ύ���ļ�
		String hostname = runConst.getHostName();
		String username = runConst.getUserName();
		String password = runConst.getPassWord();
		String localFold = runConst.getFileRoot();
		String remoteFold = runConst.getRemoteFold();

		File localFoldFile = new File(localFold);

		ArrayList<String> filesList = new ArrayList<String>();

		// �õ��ļ��е��ļ���ƽ�нṹ
		tree(localFoldFile, filesList);

		int i = 0;
		int fileListLength = filesList.size();
		try {
			/* Create a connection instance */
			Connection conn = new Connection(hostname);
			/* Now connect */
			conn.connect();
			boolean isAuthenticated = conn.authenticateWithPassword(username, password);
			if (isAuthenticated == false) {
				throw new IOException("Authentication failed.");
			}

			SCPClient client = new SCPClient(conn);
			
			if(everSysc==true){
				for (; i < fileListLength; i++) {
					try {
						// ��ǰ�ļ�md5ֵ
						File file = new File(filesList.get(i));
						if (!file.isHidden()) {
							String hash = MD5.asHex(MD5.getHash(file));
							String fileName = filesList.get(i);
							// ����ļ��иı�
							String[] fileTureName = fileName.split(File.separator);
							String targetFold = remoteFold + fileName.replace(localFold, "").replace(fileTureName[fileTureName.length - 1], "");
						
							System.out.println("     local filename-----" + fileName);
							System.out.println("     upload to targetFold-----" + targetFold);
							client.put(fileName, targetFold);
							Date de=new Date();
							System.out.println("     uploaded Time-----" +de.toString());
							md5Index.insertFileMd5(fileName, hash);
	
	
						}
	
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				everSysc=false;
			}else{
				for (; i < fileListLength; i++) {
					try {
						// ��ǰ�ļ�md5ֵ
						File file = new File(filesList.get(i));
						if (!file.isHidden()) {
							String hash = MD5.asHex(MD5.getHash(file));
							String fileName = filesList.get(i);
							boolean fileIsChange;
							if (md5Index.getMd5ByFileName(fileName).equals(hash)) {
								fileIsChange = false;
							} else {
								fileIsChange = true;
							}
							// ����ļ��иı�
							if (fileIsChange) {
								String[] fileTureName = fileName.split(File.separator);
								String targetFold = remoteFold + fileName.replace(localFold, "").replace(fileTureName[fileTureName.length - 1], "");
							
								System.out.println("     changed filename-----" + fileName);
								System.out.println("     cnanged File upload to targetFold-----" + targetFold);
								client.put(fileName, targetFold);
								Date de=new Date();
								System.out.println("     uploaded Time-----" +de.toString());
								md5Index.insertFileMd5(fileName, hash);
	
							}
	
						}
	
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			/* Close the connection */
			conn.close();
		} catch (IOException e) {
			e.printStackTrace(System.err);
			System.exit(2);
		}
	}

	/*
	 * ִ��ssh����
	 */
	private void doGetRemoteFile() {
		/**
		 * �õ���������Ϣ
		 */
		String hostname = runConst.getHostName();
		String username = runConst.getUserName();
		String password = runConst.getPassWord();
		String localFold = runConst.getFileRoot();
		String remoteFold = runConst.getRemoteFold();
		boolean isSaveLocalSourceFile=runConst.getIsSaveLocalFile();
		try {
			/* Create a connection instance */
			Connection conn = new Connection(hostname);
			/* Now connect */
			conn.connect();
			/*
			 * Authenticate. If you get an IOException saying something like
			 * "Authentication method password not supported by the server at this stage."
			 * then please check the FAQ.
			 */
			boolean isAuthenticated = conn.authenticateWithPassword(username, password);
			if (isAuthenticated == false) {
				throw new IOException("Authentication failed.");
			}

			/* Create a session */

			Session sess = conn.openSession();

			sess.execCommand("find " + remoteFold + " -type f | xargs md5sum {}");

			System.out.println("     Here is some information about the remote host when get Files:");

			/*
			 * This basic example does not handle stderr, which is sometimes
			 * dangerous (please read the FAQ).
			 */

			InputStream stdout = new StreamGobbler(sess.getStdout());

			BufferedReader br = new BufferedReader(new InputStreamReader(stdout));

			SCPClient client = new SCPClient(conn);

			while (true) {
				String line = br.readLine();
				if (line == null) {
					break;
				}
				if (!line.contains(".svn")) {
					try {

						String[] md5AndFileWay = line.split("  ");

						// �������ϵ��ļ���ŵ���ʱĿ¼
						String localFileFold = localFold + File.separator.toString() + "temp";

						String remoteFileWay = md5AndFileWay[1].trim();

						// �����ļ����ļ�ȫ·��
						String localFileName = localFold + remoteFileWay.replace(remoteFold, "");

						String fileMd5 = md5Index.getMd5ByFileName(localFileName);

						// ���Զ���ļ��ı���
						boolean isFileChange;

						if (fileMd5.equals(md5AndFileWay[0].trim())) {
							isFileChange = false;
						} else {
							isFileChange = true;
						}

						// ������ʱ��ŷ������ļ���Ŀ¼
						File tmp = new File(localFileFold);
						if (!tmp.isDirectory()) {
							tmp.mkdir();
						}
						// ����ʵ��Ҫ��ŵ��ļ�
						File localFile = new File(localFileName);
						
						if (!localFile.exists() || isFileChange) {
							System.out.println("     -----get remote filename:" + localFileName);
							// �����ǽ��������˵��ļ����ص����ص�Ŀ¼��
							client.get(md5AndFileWay[1].trim(), localFileFold);
							
							// ���ļ������з�Ϊ�ַ�����
							String[] fileUrl = line.split(File.separator.toString());

							// ��ʱ�ļ�
							File localTempFile = new File(localFileFold + File.separator + fileUrl[fileUrl.length - 1]);
							
							
							
							// ����ļ���Ŀ¼�����ڣ���ô������Ŀ¼
							if (!localFile.getParentFile().exists()) {
								localFile.getParentFile().mkdirs();
							}
							// �����ļ�
							localFile.createNewFile();
							
							//���Զ���ļ��ı���ôĬ�ϱ���һ�ݱ��ص�ʱ���ļ����������˱���ԭ�ļ�
							if(isFileChange&&isSaveLocalSourceFile){
								//��ԭ�ļ���һ��source�Ŀ�ͷxx.json ��� xx_source.json
								String keepedFileFix=localFileName.substring(localFileName.lastIndexOf(File.separator)+1);
								String keepedFileName;
								String[] keepedFileFixArr;
								//����ļ����а���.��ô����.������
								if(keepedFileFix.indexOf(".")!=-1){
									keepedFileFixArr=keepedFileFix.split("\\.");
									String keepedFirstStr=keepedFileFixArr[0];
									String keepedFileAddName=keepedFileFix.replaceFirst(keepedFirstStr,keepedFirstStr+".source");
									keepedFileName=localFileName.substring(0,localFileName.lastIndexOf(File.separator)+1)+keepedFileAddName;
								}else{
									keepedFileName=localFileName.substring(0,localFileName.lastIndexOf(File.separator)+1)+keepedFileFix+"_source";
								}
								//���ش洢����һ���ļ�
								File localkeepedFile=new File(keepedFileName);
								//���صı�����ļ�����
								localkeepedFile.createNewFile();
								FileInputStream localFileInputStream = new FileInputStream(localFile);
								FileOutputStream localkeepedFileOutputStream = new FileOutputStream(localkeepedFile);
								byte[] localKeepedBuffer=new byte[1024];
								int j=0;
								while ((j = localFileInputStream.read(localKeepedBuffer)) != -1) {
									localkeepedFileOutputStream.write(localKeepedBuffer, 0, j);
								}
								localFileInputStream.close();
								localkeepedFileOutputStream.close();

							}
							
							// ������ļ���������ļ���
							FileInputStream inFile = new FileInputStream(localTempFile);
							FileOutputStream outFile = new FileOutputStream(localFile);
							// ��ȡ����
							byte[] buffer = new byte[1024];
							int i = 0;
							while ((i = inFile.read(buffer)) != -1) {
								outFile.write(buffer, 0, i);
							}

							// �ر��ļ���
							inFile.close();
							outFile.close();

							// �����ļ���Ӧ��md5ֵ
							md5Index.insertFileMd5(localFileName, md5AndFileWay[0].trim());
							localTempFile.delete();
						}
						

					} catch (IOException e) {
						e.printStackTrace(System.err);
						System.exit(2);
					}
				}
			}

			/* Show exit status, if available (otherwise "null") */

			System.out.println("ExitCode: " + sess.getExitStatus());

			/* Close this session */

			sess.close();

			/* Close the connection */
			conn.close();
		} catch (IOException e) {
			e.printStackTrace(System.err);
			System.exit(2);
		}
	}

	//�ݹ�Ŀ¼�е��ļ�
	private void tree(File f, ArrayList<String> fileNameList) {
		File[] childs = f.listFiles(new TxtFilenameFilter());
		for (int i = 0; i < childs.length; i++) {
			String filename=childs[i].getName();
			if (childs[i].isDirectory()) {
				if(!filename.contains(".svn")&&!filename.contains(".git")){
					tree(childs[i], fileNameList);
				}
			} else {
				fileNameList.add(childs[i].getAbsolutePath());
			}
		}
		
	}
	
}
