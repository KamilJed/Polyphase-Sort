package disk;

import records.Record;

import java.io.*;
import java.nio.ByteBuffer;

public class Tape {
    private byte[] readBuffer;
    private byte[] writeBuffer;
    private InputStream inputStream;
    private OutputStream outputStream;
    private int bytesRead = 0;
    private int readMark = 0;
    private int writeMark = 0;
    private int pagesRead = 0;
    private int pagesWritten = 0;

    public Tape(String tapeName, int pageSize){
        try{
            File f = new File(tapeName);
            f.createNewFile();
            inputStream = new FileInputStream(f);
            outputStream = new FileOutputStream(f);
        }
        catch (IOException e){
            System.out.println("ERROR! Couldn't create tape: " + tapeName);
        }
        readBuffer = new byte[pageSize];
        writeBuffer = new byte[pageSize];
    }

    public Record getNextRecord(){
        double pA = getNextDouble();
        if(Double.isInfinite(pA))
            return null;
        double pB = getNextDouble();
        double pAB = getNextDouble();
        return new Record(pA, pB, pAB);
    }

    public void saveRecord(Record record){
        saveDouble(record.getProbabilityOfA());
        saveDouble(record.getProbabilityOfB());
        saveDouble(record.getProbabilityOfUnion());
    }

    public void flush(){
        savePage();
    }

    public int getPagesWritten() {
        return pagesWritten;
    }

    public int getPagesRead() {
        return pagesRead;
    }

    private double getNextDouble(){
        boolean hasNext = true;
        if(readMark == readBuffer.length || bytesRead == 0)
            hasNext = getNextPage();

        if(!hasNext)
            return Double.POSITIVE_INFINITY;

        double value = ByteBuffer.wrap(readBuffer, readMark, Double.BYTES).getDouble();
        readMark += Double.BYTES;

        if(Double.isNaN(value))
            value = getNextDouble();
        return value;
    }

    private boolean getNextPage(){
        try{
            readMark = 0;
            int bytes = inputStream.read(readBuffer);
            if(bytes > 0){
                bytesRead += bytes;
                pagesRead++;
                return true;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("ERROR getting next page of tape");
        }
        return false;
    }

    private void saveDouble(double val){
        if(writeMark == writeBuffer.length)
            savePage();

        byte[] byteDouble = new byte[8];
        ByteBuffer.wrap(byteDouble).putDouble(val);

        System.arraycopy(byteDouble, 0, writeBuffer, writeMark, byteDouble.length);
        writeMark += byteDouble.length;
    }

    private void savePage(){
        try{
            if(writeMark != writeBuffer.length)
                fillBuffer();
            outputStream.write(writeBuffer);
            writeMark = 0;
            pagesWritten++;
        }
        catch (IOException e){
            e.printStackTrace();
            System.out.println("ERROR saving page to disk");
        }
    }

    private void fillBuffer(){
        byte[] filler = new byte[8];
        ByteBuffer.wrap(filler).putDouble(Double.NaN);
        while(writeMark != writeBuffer.length){
            System.arraycopy(filler, 0, writeBuffer, writeMark, filler.length);
            writeMark += filler.length;
        }
    }
}
