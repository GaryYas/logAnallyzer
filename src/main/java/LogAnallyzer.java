import javafx.util.Pair;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


public class LogAnallyzer {
    private Map<String,List<FunctionDetails>> functionsListMap = new HashMap<>();
    private Map<String,Map<Long,FunctionDetails>> functionsMap = new HashMap<>();

    /*
        reads function line record  , stores it in memory , analyzes it ,and reads again next line from the file
        same function call in entry and in exit will have same id
        entry:
            2015-10-26T18:20:19,887 TRACE [OperationsImpl] entry with (getActions:21911)
        exit:
         2015-10-26T18:28:11,138 TRACE [OperationsImpl] exit with (getActions:21911)

    */
    public void analyze(String path) throws IOException {

        //parse the line ans seperate it by space
                Files.lines(Paths.get(path), Charset.defaultCharset()).forEach(line -> {
                      try {

                          String[] splits = line.split("\\s+");


                          Pair<String, Long> nameAndId = FunctionUtils.getFunctionNameAndId(splits[5]);
                          String functionName = nameAndId.getKey();
                          Long id = nameAndId.getValue();
                          Date entryDate = FunctionUtils.covertToDate(splits[0]);
                          String action = splits[3];

                          functionsMap.compute(functionName, (key, value) -> {
                              //new function entry store it in map when function name is the key, and the value is map when key is id of the
                              //function and value is functiondetails
                              if (!functionsMap.containsKey(key)) {
                                  FunctionDetails function = new FunctionDetails(entryDate, action, functionName, id);
                                  Map<Long, FunctionDetails> map = new HashMap<>();
                                  map.put(function.getId(), function);
                                  return map;
                              }
                              //not new function,already appear in the file ,(not first call for it)
                              Map<Long, FunctionDetails> funcMap = functionsMap.get(key);
                              //check whether it is exit call , if so calculate the duration of the call by start and end id of the function
                              if (funcMap.containsKey(id) && action.equals("exit")) {
                                  FunctionDetails functionDetails = funcMap.get(id);
                                  functionDetails.setEndDate(entryDate);
                                  functionDetails.calculateDuration();
                                  return funcMap;
                              }
                              //already in exists in functionsMap but it is not exit of the function,which means another call to the same function
                              FunctionDetails function = new FunctionDetails(entryDate, action, functionName, id);
                              funcMap.put(id, function);
                              return funcMap;

                          });
                      }
                      catch (Exception e){
                          System.out.println("Got Exception while reading or parsing a line: "+e);
                      }

                    });

        filterInternalList();
        calculateAndPrintResults();

    }
    //remove functions with no end date
    private void filterInternalList(){

        functionsMap.entrySet().forEach(entry->{
           List<FunctionDetails> functionList = entry.getValue().values().stream().filter(function->function.endDate!=null
            ).collect(Collectors.toList());
            functionsListMap.put(entry.getKey(),functionList);

        });

    }

    //for each function calculate its max value,sum,min and avg
    private void calculateAndPrintResults(){

        functionsListMap.entrySet().forEach(entry->{

            long average =0;
            int count=entry.getValue().size();
            List<FunctionDetails> functionList =entry.getValue();
            Long max =   functionList.stream().mapToLong(FunctionDetails::getDuration).max().getAsLong();
            Long sum =  functionList.stream().mapToLong(FunctionDetails::getDuration).sum();
            Long min =   functionList.stream().mapToLong(FunctionDetails::getDuration).min().getAsLong();
            Long id = Collections.max(functionList,Comparator.comparing(FunctionDetails::getDuration)).getId();
            average=sum/count;

            String finalResult = FunctionUtils.getFinalMessage(min,max,average,id,count,entry.getKey());
            System.out.println(finalResult);
        });
    }
}
