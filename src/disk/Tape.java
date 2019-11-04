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
    private int pageSize;
    private File tape;
    private boolean printMode = false;
    private boolean empty = true;

    public Tape(String tapeName, int pageSize){
        this(pageSize);
        try{
            tape = new File(tapeName);
            tape.createNewFile();
        }
        catch (IOException e) {
            System.out.println("ERROR! Couldn't create tape: " + tapeName);
        }
    }

    public Tape(File tapeFile, int pageSize){
        this(pageSize);
        tape = tapeFile;
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
        empty = false;
        saveDouble(record.getProbabilityOfA());
        saveDouble(record.getProbabilityOfB());
        saveDouble(record.getProbabilityOfUnion());
    }

    public void flush(){
        if(writeMark > 0)
            savePage();

        try {
            if(outputStream != null)
                outputStream.close();
            outputStream = null;
        }
        catch (IOException e){
            e.printStackTrace();
            System.out.println("ERROR closing stream");
        }
    }

    public void print(){
        System.out.println("[DEBUG INFO] PRINTING TAPE: " + tape.getName());
        if(empty)
            return;
        printMode = true;
        if(inputStream == null) {
            try {
                inputStream = new BufferedInputStream(new FileInputStream(tape));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.out.println("ERROR creating stream");
                printMode = false;
                return;
            }
        }

        byte[] buffBakcup = new byte[readBuffer.length];
        int readMarkBackup = readMark;
        int bytesReadBackup = bytesRead;
        int pagesReadBackup = pagesRead;
        System.arraycopy(readBuffer, 0, buffBakcup, 0, readBuffer.length);

        inputStream.mark(0);
        if(inputStream.markSupported()){
            Record r;
            while((r = getNextRecord()) != null)
                System.out.println(r);

            try {
                inputStream.reset();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("ERROR reseting input stream. Tape corrupted");
            }
        }
        printMode = false;
        readMark = readMarkBackup;
        bytesRead = bytesReadBackup;
        pagesRead = pagesReadBackup;
        System.arraycopy(buffBakcup, 0, readBuffer, 0, readBuffer.length);
    }

    public int getPagesWritten() {
        return pagesWritten;
    }

    public int getPagesRead() {
        return pagesRead;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void delete(){
        tape.delete();
    }

    private Tape(int pageSize){
        readBuffer = new byte[pageSize];
        writeBuffer = new byte[pageSize];
        this.pageSize = pageSize;
    }

    private double getNextDouble(){
        boolean hasNext = true;
        if(readMark == readBuffer.length || bytesRead == 0)
            hasNext = getNextPage();

        if(!hasNext){
            if(!printMode)
                empty = true;
            try{
                if(inputStream != null && !printMode){
                    inputStream.close();
                    inputStream = null;
                }
                bytesRead = 0;
            }
            catch (IOException e){
                e.printStackTrace();
                System.out.println("ERROR closing stream");
            }
            return Double.POSITIVE_INFINITY;
        }

        double value = ByteBuffer.wrap(readBuffer, readMark, Double.BYTES).getDouble();
        readMark += Double.BYTES;

        if(Double.isNaN(value))
            value = getNextDouble();
        return value;
    }

    private boolean getNextPage(){
        try{
            if(inputStream == null)
                inputStream = new BufferedInputStream(new FileInputStream(tape));

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
            if(outputStream == null)
                outputStream = new FileOutputStream(tape);

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
