import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {	
	private static String doMagic(String filePath) {
		String info = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			String line = "", sum = "";
			while ((line = br.readLine()) != null) {
				sum += line + "\n";
			}
			br.close();

			String regex = "([0-9]+)(\n([0-9]+:[0-9]+:[0-9]+,[0-9]+) --> ([0-9]+:[0-9]+:[0-9]+,[0-9]+)\n(.+?\n)+)";
			Matcher m1 = Pattern.compile(regex).matcher(sum);

			int lastA = 0, lastB = 0;
			while (m1.find()) {
				int a = Integer.parseInt(m1.group(3).replaceAll(":", "").replaceAll(",", ""));
				int b =  Integer.parseInt(m1.group(4).replaceAll(":", "").replaceAll(",", ""));

				if (lastA > a) {
					info += m1.group(1) + " last subtitle starts later than this one" + System.getProperty("line.separator");
					lastA = 0;
					lastB = 0;
					continue;
				}
				if (lastB > b) {
					info += m1.group(1) + " last subtitle ends later than this one ends" + System.getProperty("line.separator");
					lastA = 0;
					lastB = 0;
					continue;
				}
				if (a > b) {
					info += m1.group(1) + " this subtitle starts before it ends" + System.getProperty("line.separator");
					lastA = 0;
					lastB = 0;
					continue;
				}
				
				lastA = a;
				lastB = b;

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (info.length() == 0) info = "No errors found.";
		return info;
	}
	private static void writeFile(String path, String s) {
		try {
			BufferedWriter br = Files.newBufferedWriter(Paths.get(path));
			br.write(s);
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) throws Exception {
		System.setProperty("file.encoding","UTF-8");
		Field charset = Charset.class.getDeclaredField("defaultCharset");
		charset.setAccessible(true);
		charset.set(null,null);
		writeFile(args[1], doMagic(args[0]));
	}
}