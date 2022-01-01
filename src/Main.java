import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    ReservationStation[] multiply;
    ReservationStation[] add;
    StoreBuffer[] store;
    LoadBuffer[] load;
    QueueCell[] queue;
    RegisterFile registerFile;
    int clock;

    int addLatency;
    int subLatency;
    int mulLatency;
    int divLatency;
    int storeLatency;
    int loadLatency;

    String fileName;
    ArrayList<String> program;

    public Main(int addLatency, int subLatency, int mulLatency,
                int divLatency, int storeLatency, int loadLatency,
                String fileName) {
        multiply = new ReservationStation[2];
        add = new ReservationStation[3];
        load = new LoadBuffer[3];
        store = new StoreBuffer[3];

        registerFile = new RegisterFile(16);

        this.addLatency = addLatency;
        this.subLatency = subLatency;
        this.mulLatency = mulLatency;
        this.divLatency = divLatency;
        this.storeLatency = storeLatency;
        this.loadLatency = loadLatency;
        this.fileName = fileName;

        program = readFile(fileName);
        queue = new QueueCell[program.size()];
        runProgram();

    }

    public void runProgram() {
        if (clock != program.size()) {
            QueueCell queueCell = new QueueCell(program.get(clock), clock);
            queue[clock] = queueCell;

            boolean someoneWroteBack = false;

            for (QueueCell cell : queue
            ) {

                // started but didnt finish
                if (cell.executionBegin != 0 && cell.executionEnd == 0) {
                    int end = cell.executionBegin;

                    switch (cell.instruction) {
                        case "ADD.D":
                            end += addLatency;

                            break;
                        case "SUB.D":
                            end += subLatency;
                            break;
                        case "MUL.D":
                            end += mulLatency;
                            break;
                        case "DIV.D":
                            end += divLatency;
                            break;
                        case "L.D":
                            end += loadLatency;
                            break;
                        default:
                            end += storeLatency;
                            break;
                    }

                    if (end == clock) {
                        cell.executionEnd = clock;
                    }
                } else if (cell.executionBegin != 0 && cell.executionEnd != 0 && cell.writeResult == 0 && !someoneWroteBack) {
                    someoneWroteBack = true;
                    cell.writeResult = clock;



                }
            }
        }


        clock++;
    }

    public void writeBack(QueueCell cell) {
        String reservationStation = cell.reservationStation;

    }


    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter the latency for add");
        int addLatency = sc.nextInt();

        System.out.println("Please enter the latency for sub");
        int subLatency = sc.nextInt();

        System.out.println("Please enter the latency for mult");
        int multLatency = sc.nextInt();

        System.out.println("Please enter the latency for divide");
        int divLatency = sc.nextInt();

        System.out.println("Please enter the latency for load");
        int loadLatency = sc.nextInt();

        System.out.println("Please enter the latency for store");
        int storeLatency = sc.nextInt();

        System.out.println("Please enter the program's file name");
        String fileName = sc.next();

        Main cpu = new Main(addLatency, subLatency, multLatency, divLatency, loadLatency, storeLatency, fileName);
    }


    public static ArrayList<String> readFile(String fileName) {
        ArrayList<String> Inst = new ArrayList<>();
        try {
            File myObj = new File("./src/" + fileName);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                Inst.add(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
        return Inst;
    }

}
