import java.io.*;
import org.osbot.rs07.script.Script;

public class Filer {
	
	//private static final String tradeFile = "C:\\Users\\kaisu\\Desktop\\trade.txt";
	//private static final String mulerFile = "C:\\Users\\kaisu\\OSBot\\Data\\muler.txt";
	private static final String mulerFile = "/root/OSBot/Data/muler.txt";
	
	public static String getMuler(){
		try {
			FileReader fr = new FileReader(new File(mulerFile));
			BufferedReader br = new BufferedReader(fr);
			
			String mulerStr = br.readLine().trim();
			
			br.close();
			fr.close();
			
			return mulerStr;
			
			
		} catch (IOException e) {
			return "failed to get";
		}
	}
	
	/*public static boolean writeTradeFile(boolean trade){
		try {
			FileWriter fw = new FileWriter(new File(tradeFile));
			PrintWriter pw = new PrintWriter(fw);
			
			if(trade)
				pw.print("1");
			else
				pw.println("0");
			
			pw.close();
			fw.close();
		} catch (IOException e) {
			return false;
		}
		
		return true;
	}
	
	
	public static boolean readTradeFile(){
		try {
			FileReader fr = new FileReader(new File(tradeFile));
			BufferedReader br = new BufferedReader(fr);
			
			String result = br.readLine().trim();
			
			br.close();
			fr.close();
			
			if(result == "1")
				return true;
			else
				return false;
			
		} catch (IOException e) {
			return false;
		}
	}*/
}
