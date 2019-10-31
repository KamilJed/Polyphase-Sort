import disk.Tape;
import records.Record;


public class AppMain {
    public static void main(String[] args) {
        Record r = new Record(1.0, 2.0, 3.0);
        Tape t = new Tape("test", 4*8);
        t.saveRecord(r);
        t.flush();
        t.saveRecord(r);
        t.saveRecord(r);
        t.flush();
        while((r = t.getNextRecord()) != null)
            System.out.println("Jest");
    }
}
