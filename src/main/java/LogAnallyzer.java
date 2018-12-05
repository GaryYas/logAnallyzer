import javafx.util.Pair;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Jary on 12/5/2018.
 */
public class LogAnallyzer {
    private Map<String,List<FunctionDetails>> functionsListMap = new HashMap<>();
    private Map<String,Map<Long,FunctionDetails>> functionsMap = new HashMap<>();

    public void analyze(String path) throws IOException {

                Files.lines(Paths.get(path), Charset.defaultCharset()).forEach(line -> {
                    String[] splits= line.split("\\s+");

                    Pair<String,Long> nameAndId = FunctionUtils.getFunctionNameAndId(splits[5]);
                    String functionName=  nameAndId.getKey();
                    Long id = nameAndId.getValue();
                    Date entryDate = FunctionUtils.covertToDate(splits[0]);

                    functionsMap.compute(functionName,(key, value)->{
                        if(!functionsMap.containsKey(key))
                        {
                            FunctionDetails function = new FunctionDetails(entryDate,splits[3],functionName,id);
                            Map<Long,FunctionDetails>map = new HashMap<>();
                            map.put(function.getId(),function);
                            return map;
                        }

                        Map<Long,FunctionDetails> funcMap = functionsMap.get(key);
                        //add to check whether exit
                        if(funcMap.containsKey(id)){
                            FunctionDetails functionDetails = funcMap.get(id);
                            functionDetails.setEndDate(entryDate);
                            functionDetails.calculateDuration();
                            return funcMap;
                        }
                        FunctionDetails function = new FunctionDetails(entryDate,splits[3],functionName,id);
                        funcMap.put(id,function);
                        return funcMap;

                    });

                });

        filterInternalList();
        calculateAndPrintResults();

    }

    private void filterInternalList(){

        functionsMap.entrySet().forEach(entry->{
           List<FunctionDetails> functionList = entry.getValue().values().stream().filter(function->function.endDate!=null
            ).collect(Collectors.toList());
            functionsListMap.put(entry.getKey(),functionList);

        });

    }

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
