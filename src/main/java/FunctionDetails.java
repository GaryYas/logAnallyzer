import java.math.BigInteger;
import java.util.Date;


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
        duration = endDate.getTime()- entryDate.getTime();
    }

    public Long getDuration() {
        return duration;
    }
}
