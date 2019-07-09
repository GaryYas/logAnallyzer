import javafx.util.Pair;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class FunctionUtils {

    //convert string to date
    public static Date covertToDate(String unParsedDate) throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss,SSS");
        Date date = null;

            date = format.parse(unParsedDate);


        return date;
    }


    public static Pair<String,Long>getFunctionNameAndId(String unParsedNameAndId){

        try{
            int columnLocation = unParsedNameAndId.indexOf(':');
            String functionName = unParsedNameAndId.substring(1,columnLocation);
            Long id = Long.valueOf(unParsedNameAndId.substring(columnLocation+1,unParsedNameAndId.length()-1));
            return new Pair<>(functionName,id);

        }
        catch (Exception e){
        throw new NullPointerException("line is not in correct format ,missing ':'");

        }
    }
    public static String getFinalMessage(Long min, Long max, Long average, Long id, int count, String functionName){

        StringBuilder finalValues = new StringBuilder("OperationsImpl:");
        finalValues.append(functionName)
                .append(" min ")
                .append(min)
                .append(", max ")
                .append(max)
                .append(", avg ")
                .append(average)
                .append(", max id ")
                .append(id)
                .append(", count ")
                .append(count);
        return finalValues.toString();
    }


}
