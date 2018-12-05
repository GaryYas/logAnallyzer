import java.io.File;

/**
 * Created by Jary on 12/5/2018.
 */
public class Main {

    public static void main(String[] args){
        try{
        //    String filePath = args[0];
        //    File file = new File("path/to/file.txt");//.isFile()
        //    if (file.isFile()) {
                // do the logic
           // LogAnallyazerList logAnallyzer = new LogAnallyazerList();
            LogAnallyzer logAnallyzerr = new LogAnallyzer();
            logAnallyzerr.analyze("C:\\Users\\Jary\\Desktop\\tinkoff\\testlog.log");
         //   }
            System.out.println("log file does not exist");
        }
        catch (Exception e){}

    }
}
