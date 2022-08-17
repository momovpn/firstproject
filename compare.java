import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class compare {

    public static void main(String[] args) {
        File file = new File("C:/Users/Lenovo/Desktop/DesktopCentral/java/compare/ApplicationResources_en_US.properties");
        File file2 = new File("C:/Users/Lenovo/Desktop/DesktopCentral/java/compare/ApplicationResources_zh_CN.properties");
        File writeName = new File("C:/Users/Lenovo/Desktop/DesktopCentral/java/compare/new/en.properties");
        File writeName2 = new File("C:/Users/Lenovo/Desktop/DesktopCentral/java/compare/new/zh.properties");
        BufferedWriter out = null;
        BufferedWriter out2 = null;
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "utf-8");
            BufferedReader br = new BufferedReader(isr);

            InputStreamReader isr2 = new InputStreamReader(new FileInputStream(file2), "utf-8");
            BufferedReader br2 = new BufferedReader(isr2);
            
            out = new BufferedWriter(new FileWriter(writeName));
            out2 = new BufferedWriter(new FileWriter(writeName2));

            String line = null;
            String line2 = null;
            List<String> s1 = new ArrayList<String>();
            List<String> s2 = new ArrayList<String>();
            while ((line = br.readLine()) != null) {
//              System.out.println(line);
                s1.add(line.split("=")[0]);
            }
            while ((line2 = br2.readLine()) != null) {
//              System.out.println(line);
                s2.add(line2.split("=")[0]);
            }

            for(int i=0; i<s1.size(); i++) {
                out.write(s1.get(i) + "\n");
            }

            for(int i=0; i<s2.size(); i++) {
                out2.write(s2.get(i) + "\n");
            }

            br.close();
            br2.close();
            System.out.println(s1.size());
            System.out.println(s2.size());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.flush();
                out2.flush();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            try {
                out.close();
                out2.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
