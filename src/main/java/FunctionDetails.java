import java.math.BigInteger;
import java.util.Date;

/**
 * Created by Jary on 12/5/2018.
 */
public class FunctionDetails {

    Date entryDate;
    Date endDate;
    Long duration;
    Long id;
    String action;
    String functionName;

    public FunctionDetails(Date entryDate,String action,String functionName,Long id){
        this.entryDate=entryDate;
        this.action=action;
        this.id=id;
        this.functionName=functionName;
    }

    public String getFunctionName() {
        return functionName;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public Long getId() {
        return id;
    }

    public String getAction() {
        return action;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void calculateDuration(){
        if(endDate.before(entryDate))
            System.out.println("hahaha");

    //    BigInteger end =  BigInteger.valueOf(endDate.getTime());
      //  BigInteger start = BigInteger.valueOf(entryDate.getTime());
       // Long start = entryDate.getTime();
        duration = endDate.getTime()- entryDate.getTime();
    //duration =  end.subtract(start).longValue() ;
    }

    public Long getDuration() {
        return duration;
    }
}
