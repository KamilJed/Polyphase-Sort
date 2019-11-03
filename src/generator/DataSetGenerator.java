package generator;

import records.Record;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

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

    public void generateDataSet(long setSize){
        try(FileOutputStream writer = new FileOutputStream(dataFile)){
            byte[] buffer = new byte[3*8];
            for(int i = 0; i < setSize; i++){
                Record r = new Record();
                ByteBuffer.wrap(buffer).putDouble(r.getProbabilityOfA()).putDouble(r.getProbabilityOfB()).putDouble(r.getProbabilityOfUnion());
                writer.write(buffer);
            }
        }
        catch (IOException e){
            e.printStackTrace();
            System.out.println("ERROR writing to data set!");
        }
    }
}
