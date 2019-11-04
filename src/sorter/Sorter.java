package sorter;

import disk.Tape;
import records.Record;

import java.io.File;
import java.util.Scanner;

public class Sorter {
    private File file;
    private Tape[] tapes;
    private int pageSize;
    private boolean debug;
    private int curTape;
    private int mergePhases = 0;

    public Sorter(String fileName, int pageSize, boolean debug){
        this.pageSize = pageSize;
        file = new File(fileName);
        tapes = new Tape[3];
        for(int i = 0; i < tapes.length; i++)
            tapes[i] = new Tape("tape" + i, pageSize);
        this.debug = debug;
    }

    public void sort(){
        int dummyRuns = distribute();
        if(debug){
            if(debugQuestion("distribution")){
                for(Tape t : tapes)
                    t.print();
                System.out.println("DUMMMY RUNS: " + dummyRuns);
            }
        }
        Record cur = eliminateDummys(dummyRuns, curTape ^ 1, 2);
        int sortedTapeIndex = mergeTapes(curTape, curTape ^ 1, 2, cur);
        if(debug){
            if(debugQuestion("merging")){
                for(Tape t : tapes)
                    t.print();
            }
        }
        System.out.println("Number of phases: " + mergePhases);
        System.out.println("Sorted tape: tape" + sortedTapeIndex);
        int pagesRead = 0, pagesWritten = 0;
        for(Tape t : tapes){
            pagesRead += t.getPagesRead();
            pagesWritten += t.getPagesWritten();
        }
        System.out.println("Number of disk reads: " + pagesRead);
        System.out.println("Number of disk writes: " + pagesWritten);
        for(int i = 0; i < tapes.length; i++)
            if(i != sortedTapeIndex){
                tapes[i].delete();
            }

    }

    private int distribute(){
        Tape distTape = new Tape(file, pageSize);
        Record curRecord;
        Record prevRecord = null;
        Record[] lastRecord = {null, null};
        curTape = 0;
        int lastWritten = 0;
        int[] fibonacci = {1, 0};
        int fibCounter = 1;
        while((curRecord = distTape.getNextRecord()) != null){
            if(prevRecord != null){
                if(curRecord.compareTo(prevRecord) < 0){
                    fibCounter--;
                    if(fibCounter == 0){
                        fibonacci[curTape] += fibonacci[curTape ^ 1];
                        fibCounter = fibonacci[curTape];
                        curTape ^= 1;
                    }
                }
            }

            if(curTape != lastWritten && lastRecord[curTape] != null && lastRecord[curTape].compareTo(curRecord) <= 0)
                fibCounter++;
            tapes[curTape].saveRecord(curRecord);
            lastWritten = curTape;
            lastRecord[curTape] = curRecord;
            prevRecord = curRecord;
        }
        tapes[curTape].flush();
        tapes[curTape ^ 1].flush();
        return fibCounter - 1;
    }

    private Record eliminateDummys(int dummyRuns, int tapeIndex, int mergeTapeIndex){
        Record curRecord = null;
        for(int i = 0; i < dummyRuns; i++){
            curRecord = writeRun(curRecord, tapeIndex, mergeTapeIndex);
        }
        return curRecord;
    }

    private int mergeTapes(int tape1Index, int tape2Index, int mergeTapeIndex, Record cur2){
        Record prev1, prev2;
        Record cur1;
        boolean run1 = false, run2 = false;
        if(cur2 == null)
            cur2 = tapes[tape2Index].getNextRecord();

        cur1 = tapes[tape1Index].getNextRecord();

        prev1 = cur1;
        prev2 = cur2;

        while(cur1 != null && cur2 != null){
            run1 = false;
            run2 = false;
            if(cur1.compareTo(cur2) >= 0){
                tapes[mergeTapeIndex].saveRecord(cur2);
                prev2 = cur2;
                cur2 = tapes[tape2Index].getNextRecord();
                if(cur2 == null || cur2.compareTo(prev2) < 0)
                    run2 = true;
            }
            else{
                tapes[mergeTapeIndex].saveRecord(cur1);
                prev1 = cur1;
                cur1 = tapes[tape1Index].getNextRecord();
                if(cur1 == null || cur1.compareTo(prev1) < 0)
                    run1 = true;
            }

            if(run1){
                cur2 = writeRun(cur2, tape2Index, mergeTapeIndex);
            }
            else if(run2){
                cur1 = writeRun(cur1, tape1Index, mergeTapeIndex);
            }
        }

        tapes[mergeTapeIndex].flush();
        mergePhases++;
        if(debug){
            if(debugQuestion("merging phase " + mergePhases)){
                System.out.println("--------------------");
                for(Tape t : tapes)
                    t.print();
            }
        }
        if(cur1 != null)
            return mergeTapes(mergeTapeIndex, tape1Index, tape2Index, cur1);
        else{
            return mergeTapeIndex;
        }
    }

    private Record writeRun(Record cur, int sourceTapeIndex, int destTapeIndex){
        Record prev = cur;
        if(cur != null)
            tapes[destTapeIndex].saveRecord(cur);
        while((cur = tapes[sourceTapeIndex].getNextRecord()) != null && cur.compareTo(prev) >= 0){
            tapes[destTapeIndex].saveRecord(cur);
            prev = cur;
        }
        return cur;
    }

    private boolean debugQuestion(String phase){
        System.out.println("[DEBUG INFO]Do you want to print files after " + phase + " [y/n]");
        Scanner scanner = new Scanner(System.in);
        String answer = "n";
        if(scanner.hasNextLine())
            answer = scanner.nextLine();
        return answer.toLowerCase().toCharArray()[0] == 'y';
    }
}
