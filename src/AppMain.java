import disk.Tape;
import generator.DataSetGenerator;
import records.Record;
import sorter.Sorter;
import java.io.File;
import java.util.Scanner;


public class AppMain {
    static boolean debug = false;
    static boolean mainLoop = true;
    public static void main(String[] args) {

        while(mainLoop){
            printInfo();
            processInput();
        }
    }

    private static void printInfo(){
        System.out.println("What do you want to do?");
        System.out.println("Type r to generate and sort randomly generated records");
        System.out.println("Type m to manually type in records to sort");
        System.out.println("Type e to sort binary file of your choice");
        System.out.println("Type f to read and display binary file");
        System.out.println("Type d to change debug setting (currently debug = " + debug + ")");
        System.out.println("Type q to exit");
    }

    private static void processInput(){
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        if(input == null || input.isEmpty())
            return;

        switch (input.toLowerCase().toCharArray()[0]){
            case 'r':
                generateAndSort();
                break;
            case 'm':
                typeAndSort();
                break;
            case 'e':
                getFileNameAndSort();
                break;
            case 'f':
                displayBinaryFile();
                break;
            case 'd':
                debug = !debug;
                break;
            case 'q':
                mainLoop = false;
                default:
        }
    }

    private static void generateAndSort(){
        System.out.println("Type in the number of records to generate and sort");
        Scanner scanner = new Scanner(System.in);
        int number = scanner.nextInt();
        System.out.println("Type in the page size");
        int pageSize = scanner.nextInt() * 8;
        DataSetGenerator generator = new DataSetGenerator("dataSet");
        generator.generateDataSet(number, pageSize);
        AppMain.sort("dataSet", pageSize);
    }

    private static void typeAndSort(){
        System.out.println("Type in page size");
        Scanner scanner = new Scanner(System.in);
        int pageSize = scanner.nextInt() * 8;
        System.out.println("Type in records");
        Tape dataSetTape = new Tape("dataSet", pageSize);
        int counter = 0;
        double[] record = new double[3];
        while(scanner.hasNextDouble()){
            if(counter == 3){
                dataSetTape.saveRecord(new Record(record[0], record[1], record[2]));
                counter = 0;
            }
            record[counter++] = scanner.nextDouble();
        }
        if(counter == 3)
            dataSetTape.saveRecord(new Record(record[0], record[1], record[2]));
        dataSetTape.flush();
        sort("dataSet", pageSize);
    }

    private static void getFileNameAndSort(){
        System.out.println("Type in file to sort");
        Scanner scanner = new Scanner(System.in);
        String fileName = scanner.nextLine();
        sort(fileName, 0);
    }

    private static void displayBinaryFile(){
        System.out.println("Type in file to display");
        Scanner scanner = new Scanner(System.in);
        String fileName = scanner.nextLine();
        displayBinaryFile(fileName);
    }

    private static void displayBinaryFile(String fileName){
        File file = new File(fileName);
        Tape tape = new Tape(file, 3*8);
        Record r;
        System.out.println(fileName + " contents:");
        while((r = tape.getNextRecord()) != null)
            System.out.println(r);
    }

    private static void sort(String fileName, int pageSize){
        if(pageSize == 0){
            System.out.println("Type in the page size");
            Scanner scanner = new Scanner(System.in);
            pageSize = scanner.nextInt() * 8;
        }
        displayBinaryFile(fileName);
        Sorter sorter = new Sorter(fileName, pageSize, debug);
        sorter.sort();
        mainLoop = false;
    }
}
