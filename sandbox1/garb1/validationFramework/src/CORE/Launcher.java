
package core;

import java.io.File;


public class Launcher {
	public static final String CE_String = 
		"c:\\projects\\PingIdentity\\conformanceEngine\\ConformanceEngine_24.05.2005\\conformance-engine\\bin";
	public static final String TP_Path = 
		"c:\\worked\\updated";
	//public static final String tp_String = 
		//"c:\\Temp\\ce_tp\\SSO_CE_SP_Artifact_Artifact.jmx";
	public static final long PROCESS_TTL = 60000;
	
	public static void main(String[] args) throws Exception {
		Runtime runtime = Runtime.getRuntime();
		/*for (int i = 0; i < 20; i++) {
			Process cmdProc = runtime.exec(".//data//cmd.exe");
			Thread.sleep(30000);
			cmdProc.destroy();
		}*/
		File tpDir = new File(Launcher.TP_Path);
		String[] jmxsNames = tpDir.list();
		File CE_dir = new File(Launcher.CE_String);
		
		for (int i = 0, max = jmxsNames.length; i < max; i++) {
			if (i == 1)
				break;
			System.out.println("currently processed tp : " + jmxsNames[i] + "  number " + i);
			Process cmdProc = runtime.exec(Launcher.CE_String + "\\cengine.bat -n -t " + Launcher.TP_Path + 
		               "\\" + jmxsNames[i], null, CE_dir);
			Thread.sleep(Launcher.PROCESS_TTL);
			cmdProc.destroy();
			Thread.sleep(10000);
		}
		
		//String[] envp = {"-n -t", Launcher.TP_Path, "SSO_CE_SP_Artifact_Artifact.jmx"};
//		Process cmdProc = runtime.exec(Launcher.CE_String + "cengine.bat -n -t " + Launcher.TP_Path + 
	//					               "SSO_CE_SP_Artifact_Artifact.jmx", null, CE_dir);
		//Thread.sleep(25000);
		//cmdProc.destroy();
		//System.out.println("end");
		
	}
}