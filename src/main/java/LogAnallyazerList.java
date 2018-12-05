import javafx.util.Pair;
import sun.rmi.runtime.Log;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Jary on 12/6/2018.
 */
public class LogAnallyazerList {

    private Map<String,List<FunctionDetails>> functionsMap= new HashMap<>();
    private Map<String,Map<Long,FunctionDetails>> functionsMapT= new HashMap<>();
    int exitCount=0;
    int entryCount = 0;

    public void analyze(String path) throws IOException {

        Files.lines(Paths.get(path), Charset.defaultCharset()).forEach(line -> {
            String[] splits= line.split("\\s+");

            Pair<String,Long> nameAndId = FunctionUtils.getFunctionNameAndId(splits[5]);
            String functionName=  nameAndId.getKey();
            Long id = nameAndId.getValue();
            Date entryDate = FunctionUtils.covertToDate(splits[0]);


            functionsMap.compute(functionName,(key,value)->{
                if(!functionsMap.containsKey(key))
                {
                    FunctionDetails function = new FunctionDetails(entryDate,splits[3],functionName,id);


                         List<FunctionDetails> functionList = new ArrayList<>();
                           functionList.add(function);


                    return functionList;
                }

                  List<FunctionDetails> functionList = functionsMap.get(key);


                        for(FunctionDetails functionDetails:functionList){

                            if(functionDetails.getId().equals(id)){ //&&function.getAction().equals("exit")
                                functionDetails.setEndDate(entryDate);

                                functionDetails.calculateDuration();
                                return functionList;
                            }

                        }
                        FunctionDetails function = new FunctionDetails(entryDate,splits[3],functionName,id);
                        functionList.add(function);

                        return functionList;
            });

        });

        filterAndSortInternalList();
        functionsMap.entrySet().forEach(entry->{
            StringBuilder finalValues = new StringBuilder("OperationsImpl:");
            long average =0;
            int count=entry.getValue().size();
         List<FunctionDetails> functionList =entry.getValue();
         Long max =   functionList.stream().mapToLong(FunctionDetails::getDuration).max().getAsLong();
         Long sum =  functionList.stream().mapToLong(FunctionDetails::getDuration).sum();
         Long min =   functionList.stream().mapToLong(FunctionDetails::getDuration).min().getAsLong();
         Long id = Collections.max(functionList,Comparator.comparing(FunctionDetails::getDuration)).getId();

            average=sum/count;

            finalValues.append(entry.getKey())
                    .append(" min: ")
                    .append(min)
                    .append(", max ")
                    .append(max)
                    .append(", avg ")
                    .append(average)
                    .append(", max id ")
                    .append(id)
                    .append(", count ")
                    .append(count);

            System.out.println(finalValues);
        });

        //OperationsImpl:getData min 123, max 846, avg 315, max id 22, count 333

    }

    private void filterAndSortInternalList(){

            functionsMap.entrySet().forEach(entry->{
            List<FunctionDetails> functionList = entry.getValue().stream().filter(function->function.endDate!=null
            )//.sorted(Comparator.comparing(a->a.duration))
                    .collect(Collectors.toList());
            functionsMap.put(entry.getKey(),functionList);

        });

    }


}
