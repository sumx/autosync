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
		 * ���ʣ� 1.��ȡ����(�ӷ���������ȡjson�ļ�)֧������ֻ����ĳ��Ŀ¼���ļ�
		 * 2.�ύ����(�ѱ��ص��ļ��ύ��������)֧��ֻ����ĳ��Ŀ¼���ļ� �������е�ģʽ
		 * 1:�����������ȡ���ݣ���ʱ����л�ȡ����(ֻ�Ա���û�е����ݽ��л�ȡ
		 * �����������)��Ҳ�����ύ���ݡ�(����:���ػ�û��һ�����������ݣ��Ժ���ʱ�������Ϻ����µ�ͬ��)
		 * 2:���������󲻻�ȡ����,�˺�ֻ�����ύ���ݡ�(����:�����Ѿ�����һ������,ֻ����ѱ���Ҫ��ע�������ύ�������������)
		 * ��ͬģʽ��Ҫ���� 1:��������ȡ���������ݵ�ʱ��Ϊÿ����ȡ��������һ��md5ժҪ���ٴ���ȡ��ʱ�����,û�е����ݲŽ��и��£�
		 * �����µ�����������������ʱ��ֻ��ժҪ�б仯���ļ������ύ����ʱ��Ҳͬʱ���¸��ļ���Ӧ��ժҪֵ��
		 * 2:ֻ�ǽ��и��²�������ʱ�����ݵ�ժҪ�ļ��Ǵ��ڵģ�ֻҪÿ�ζ�ʱ���񴥷���ʱ�򣬿�һ����Ӧ�ļ��Ƿ�ժҪ��������������ô�ύ�ļ��� ����:
		 * 1:���ж��˶�ͬĿ¼�µ�ͬ���ļ������ύ��ʱ�򣬳����ļ������ǵ�����������ԭ�򣺶���������ļ����ƾ������ص㣬��ô������˺����ƣ�
		 * 2:��һ��ģʽ
		 * �����ڵ�һ����ȡ֮��ֻ�ǶԷ������¼ӵ����ݽ��и��£���ô����õ�һ�����������µõ������ݣ�ֻ�ܰѱ����ļ�ɾ����Ȼ��ȴ���ȡ�Ĳ�����
		 */
		 
		// ��ȡ����
		RunConst runConst =RunConst.getInstance();
		Document doc = null;
		try {
			FileInputStream confiInputStream=null;
			//�������Ŀ¼δ���ҵ������ļ�ֱ������
			try{
				confiInputStream = new FileInputStream("Config.xml");
			}catch(FileNotFoundException e){
				System.out.println("     Error----:Config��File��Config.xml��Not Exist");
				System.exit(-1);
			}
			//�����ȡ���ó���Ҳֱ����������
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
					//д������
					System.out.println("     name----:"+propName);
					System.out.println("     value---:"+propValue);
					//�������
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
			//�ͷ��ļ�
			confiInputStream.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// ��ӳ����������,��������ʱ���md5ժҪ�洢���ļ���
		Runtime.getRuntime().addShutdownHook(new ShutDownWork());
		
		// �����߳�ͬ���ļ�
		Thread FileOperaterThread = new FileOperaterThread();
		FileOperaterThread.start();

		// ���ļ���md5ֵͬ�����ļ����߳�
		Thread updateMd5IndexThread = new UpdateMd5IndexThread();
		updateMd5IndexThread.start();

	}
}
