package generator;

import disk.Tape;
import records.Record;

import java.io.File;
import java.io.IOException;

public class DataSetGenerator {
    private final File dataFile;

    public DataSetGenerator(String fileName){
        dataFile = new File(fileName);
        try{
            dataFile.createNewFile();
        }
        catch(IOException e){
            e.printStackTrace();
            System.out.println("ERROR creating data set file!");
        }
    }

    public void generateDataSet(long setSize, int pageSize){
        Tape tape = new Tape("dataSet", pageSize);
        for(int i = 0; i < setSize; i++){
            tape.saveRecord(new Record());
        }
        tape.flush();
    }
}
