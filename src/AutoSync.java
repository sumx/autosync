import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import util.FileOperaterThread;
import util.RunConst;
import util.ShutDownWork;
import util.UpdateMd5IndexThread;

public class AutoSync {
	public static void main(String[] args) {
		/**
		 * 名词： 1.获取数据(从服务器上拉取json文件)支持配置只更新某个目录的文件
		 * 2.提交数据(把本地的文件提交到服务器)支持只更新某个目录的文件 程序运行的模式
		 * 1:程序启动后获取数据，此时后进行获取数据(只对本地没有的数据进行获取
		 * ，方便程序处理)，也进行提交数据。(场景:本地还没有一份完整的数据，以后随时保持线上和线下的同步)
		 * 2:程序启动后不获取数据,此后只进行提交数据。(场景:本地已经有了一份数据,只是想把本地要关注的数据提交到服务器看结果)
		 * 不同模式需要处理： 1:当进行拉取服务器数据的时候，为每个拉取的数据做一个md5摘要。再次拉取的时候对于,没有的数据才进行更新，
		 * 当更新到服务器操作触发的时候，只对摘要有变化的文件进行提交，这时候也同时更新该文件对应的摘要值。
		 * 2:只是进行更新操作，这时候数据的摘要文件是存在的，只要每次定时任务触发的时候，看一下相应文件是否摘要变更，若变更，那么提交文件。 问题:
		 * 1:当有多人对同目录下的同名文件进行提交的时候，出现文件被覆盖的情况。（规避原则：定义的数据文件名称尽量有特点，最好带上域账号名称）
		 * 2:第一种模式
		 * ，由于第一次拉取之后，只是对服务器新加的数据进行更新，那么，想得到一个服务器上新得到的数据，只能把本地文件删除，然后等待拉取的操作。
		 */
		 
		// 读取配置
		RunConst runConst =RunConst.getInstance();
		Document doc = null;
		try {
			FileInputStream confiInputStream=null;
			//如果本级目录未有找到配置文件直接跳出
			try{
				confiInputStream = new FileInputStream("Config.xml");
			}catch(FileNotFoundException e){
				System.out.println("     Error----:Config　File（Config.xml）Not Exist");
				System.exit(-1);
			}
			//如果读取配置出错也直接跳出程序
			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				doc = db.parse(confiInputStream);
				NodeList proNList = doc.getElementsByTagName("item");
				System.out.println("read config:"+proNList.getLength());
				for (int i = 0; i < proNList.getLength(); i++) {
					Element configItem = (Element) proNList.item(i);
					String propName=configItem.getAttribute("name").trim();
					String propValue=configItem.getTextContent().trim();
					//写入配置
					System.out.println("     name----:"+propName);
					System.out.println("     value---:"+propValue);
					//如果属性
					if(!propValue.equals("")){
						try{
						 Method method=runConst.getClass().getMethod("set"+propName, new Class[]{String.class});
						 method.invoke(runConst,propValue);
						}catch(Exception ex){
							System.out.println("     invoke method error----:"+propName);
						}
					}
				}
			} catch (Exception ex) {
				
				System.exit(-1);
			}
			//释放文件
			confiInputStream.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// 添加程序结束监听,当结束的时候把md5摘要存储到文件中
		Runtime.getRuntime().addShutdownHook(new ShutDownWork());
		
		// 启动线程同步文件
		Thread FileOperaterThread = new FileOperaterThread();
		FileOperaterThread.start();

		// 将文件的md5值同步到文件的线程
		Thread updateMd5IndexThread = new UpdateMd5IndexThread();
		updateMd5IndexThread.start();

	}
}
