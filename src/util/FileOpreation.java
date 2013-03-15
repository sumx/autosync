package util;

import java.io.*;
import java.util.*;

import com.twmacinta.util.MD5;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import ch.ethz.ssh2.SCPClient;

/**
 * 文件操作的实现类
 * 
 * @author wlstyle
 * 
 */
public class FileOpreation implements FileOpreationInterface {

	// md5Index的管理对象

	private ManageMd5Index md5Index = ManageMd5Index.getInstance();
	// 运行时常量对象
	private RunConst runConst = RunConst.getInstance();
	
	//索引文件是否存在，存在的化强制写一遍
	
	private boolean everSysc=false;

	/**
	 * 构造函数从文件
	 */
	public FileOpreation() {
		// 得到整个文件的配置对象
		md5Index.readFileMd5Index();
		if(md5Index.getMd5Hash().size()>0){
			everSysc=true;
		}
	}
	
	public ManageMd5Index getMd5Index(){
		return md5Index;
	}

	/**
	 * 有一个专门到线程来从服务器拉取数据服务器拉去数据
	 */
	public void getFileFromServer() throws IOException {
		// 拉取服务器端的文件
		System.out.println("begin to getRemote File------");
		doGetRemoteFile();
		System.out.println("begin to getRemote File end------");
	}

	/**
	 * 通过一个定时器，同步文件到服务器端。 当定时器运行的时候检查文件是否已经变更， 如果变更的化通过scp命令同步到服务器,没有变更不做文件的提交
	 */
	public void syscFileToServer() {
		System.out.println("begin to sysclocal File------");
		doSyscFile();
		System.out.println("begin to sysclocal File end------");
	}

	private void doSyscFile() {
		// 递归当前目录，如果当前的文件有变更那么，那么scp提交该文件
		String hostname = runConst.getHostName();
		String username = runConst.getUserName();
		String password = runConst.getPassWord();
		String localFold = runConst.getFileRoot();
		String remoteFold = runConst.getRemoteFold();

		File localFoldFile = new File(localFold);

		ArrayList<String> filesList = new ArrayList<String>();

		// 得到文件夹的文件的平行结构
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
						// 当前文件md5值
						File file = new File(filesList.get(i));
						if (!file.isHidden()) {
							String hash = MD5.asHex(MD5.getHash(file));
							String fileName = filesList.get(i);
							// 如果文件有改变
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
						// 当前文件md5值
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
							// 如果文件有改变
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
	 * 执行ssh命令
	 */
	private void doGetRemoteFile() {
		/**
		 * 得到服务器信息
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

						// 服务器上的文件存放的临时目录
						String localFileFold = localFold + File.separator.toString() + "temp";

						String remoteFileWay = md5AndFileWay[1].trim();

						// 本地文件的文件全路径
						String localFileName = localFold + remoteFileWay.replace(remoteFold, "");

						String fileMd5 = md5Index.getMd5ByFileName(localFileName);

						// 如果远程文件改变了
						boolean isFileChange;

						if (fileMd5.equals(md5AndFileWay[0].trim())) {
							isFileChange = false;
						} else {
							isFileChange = true;
						}

						// 创建临时存放服务器文件的目录
						File tmp = new File(localFileFold);
						if (!tmp.isDirectory()) {
							tmp.mkdir();
						}
						// 本地实际要存放的文件
						File localFile = new File(localFileName);
						
						if (!localFile.exists() || isFileChange) {
							System.out.println("     -----get remote filename:" + localFileName);
							// 这里是将服务器端的文件下载到本地的目录下
							client.get(md5AndFileWay[1].trim(), localFileFold);
							
							// 将文件名称切分为字符数组
							String[] fileUrl = line.split(File.separator.toString());

							// 临时文件
							File localTempFile = new File(localFileFold + File.separator + fileUrl[fileUrl.length - 1]);
							
							
							
							// 如果文件父目录不存在，那么创建该目录
							if (!localFile.getParentFile().exists()) {
								localFile.getParentFile().mkdirs();
							}
							// 创建文件
							localFile.createNewFile();
							
							//如果远程文件改变那么默认保存一份本地当时的文件并且配置了保存原文件
							if(isFileChange&&isSaveLocalSourceFile){
								//给原文件加一个source的开头xx.json 变成 xx_source.json
								String keepedFileFix=localFileName.substring(localFileName.lastIndexOf(File.separator)+1);
								String keepedFileName;
								String[] keepedFileFixArr;
								//如果文件名中包含.那么按照.来处理
								if(keepedFileFix.indexOf(".")!=-1){
									keepedFileFixArr=keepedFileFix.split("\\.");
									String keepedFirstStr=keepedFileFixArr[0];
									String keepedFileAddName=keepedFileFix.replaceFirst(keepedFirstStr,keepedFirstStr+".source");
									keepedFileName=localFileName.substring(0,localFileName.lastIndexOf(File.separator)+1)+keepedFileAddName;
								}else{
									keepedFileName=localFileName.substring(0,localFileName.lastIndexOf(File.separator)+1)+keepedFileFix+"_source";
								}
								//本地存储的上一份文件
								File localkeepedFile=new File(keepedFileName);
								//本地的保存的文件副本
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
							
							// 输入的文件流输出的文件流
							FileInputStream inFile = new FileInputStream(localTempFile);
							FileOutputStream outFile = new FileOutputStream(localFile);
							// 读取缓存
							byte[] buffer = new byte[1024];
							int i = 0;
							while ((i = inFile.read(buffer)) != -1) {
								outFile.write(buffer, 0, i);
							}

							// 关闭文件流
							inFile.close();
							outFile.close();

							// 保存文件对应的md5值
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

	//递归目录中的文件
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
