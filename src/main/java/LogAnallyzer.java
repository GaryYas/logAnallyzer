import javafx.util.Pair;

import javax.jws.soap.SOAPBinding;
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


                    functionsMapT.compute(functionName,(key,value)->{
                        if(!functionsMapT.containsKey(key))
                        {
                            FunctionDetails function = new FunctionDetails(entryDate,splits[3],functionName,id);


                       //     List<FunctionDetails> functionList = new ArrayList<>();
                     //       functionList.add(function);

                            Map<Long,FunctionDetails>map = new HashMap<>();
                            map.put(function.getId(),function);
                            return map;
                        }

                      //  List<FunctionDetails> functionList = functionsMapT.get(key);
                       // ListIterator<FunctionDetails>listIterator = functionList.listIterator();
                        Map<Long,FunctionDetails> funcMap = functionsMapT.get(key);
                        if(funcMap.containsKey(id)){
                            FunctionDetails functionDetails = funcMap.get(id);
                            functionDetails.setEndDate(entryDate);
                            functionDetails.calculateDuration();
                            return funcMap;
                        }
                        FunctionDetails function = new FunctionDetails(entryDate,splits[3],functionName,id);
                        funcMap.put(id,function);
                        return funcMap;

                        /*
                        while(listIterator.hasNext()){

                            FunctionDetails functionDetails = listIterator.next();
                            if(functionDetails.getId().equals(id)){ //&&function.getAction().equals("exit")
                                functionDetails.setEndDate(function.getEntryDate());
                                functionDetails.calculateDuration();
                                return functionList;
                            }
                        }*/



                      //  functionList.add(function);
                       // listIterator.add(function);
                        /*
                        for(FunctionDetails functionDetails:functionList){

                            if(functionDetails.getId().equals(id)){ //&&function.getAction().equals("exit")
                            //    functionDetails.setEndDate(function.getEntryDate());
                                functionDetails.setEndDate(entryDate);

                                functionDetails.calculateDuration();
                                return functionList;
                            }

                        }
                        FunctionDetails function = new FunctionDetails(entryDate,splits[3],functionName,id);
                        functionList.add(function);

                        return functionList;*/
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
    }

    private void filterAndSortInternalList(){


        functionsMapT.entrySet().forEach(entry->{
               Map<Long,FunctionDetails> functionsMap = entry.getValue().values().stream().filter(function->function.endDate!=null
            )//.sorted(Comparator.comparing(a->a.duration))
                    .collect(Collectors.toMap(FunctionDetails::getId, a->a));
            functionsMapT.put(entry.getKey(),functionsMap);

        });


        functionsMapT.entrySet().forEach(entry->{
           List<FunctionDetails> functionList = entry.getValue().values().stream().filter(function->function.endDate!=null
            )//.sorted(Comparator.comparing(a->a.duration))
                   .collect(Collectors.toList());
            functionsMap.put(entry.getKey(),functionList);

        });

    }
}
