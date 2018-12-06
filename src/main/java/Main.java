import java.io.File;


public class Main {

    public static void main(String[] args){
        try{

            String filePath = args[0];
            File file = new File(filePath);
            if (file.isFile()) {
            LogAnallyzer logAnallyzerr = new LogAnallyzer();
            logAnallyzerr.analyze(filePath);
            }
            else System.out.println("not valid file path!");
        }
        catch (Exception e){
            System.out.println("no arguments for path provided");

        }

    }
}
