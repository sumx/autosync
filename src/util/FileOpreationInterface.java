package util;

import java.io.IOException;
/**
 * 文件操作的接口
 * @author wlstyle
 *
 */
public interface FileOpreationInterface {
	//从服务器得到文件
	void getFileFromServer() throws IOException;
	//同步本地文件到服务器
	void syscFileToServer();
}
