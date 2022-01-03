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

    ArrayList<String> issuedInstructions;

    double[] memory;

    int clock;


    int addLatency;
    int subLatency;
    int mulLatency;
    int divLatency;
    int storeLatency;
    int loadLatency;

    String fileName;
    ArrayList<String> program;

    // should we handle the scenario where the queue is less than the size of the program ?!

    public Main(int addLatency, int subLatency, int mulLatency,
                int divLatency, int storeLatency, int loadLatency,
                String fileName) {
        multiply = new ReservationStation[2];
        add = new ReservationStation[3];
        load = new LoadBuffer[3];
        store = new StoreBuffer[3];

        for (int i = 0; i < multiply.length; i++) {
            multiply[i] = new ReservationStation();
        }

        for (int i = 0; i < add.length; i++) {
            add[i] = new ReservationStation();
        }

        registerFile = new RegisterFile(16);

        memory = new double[50];
        clock = 1;

        this.addLatency = addLatency;
        this.subLatency = subLatency;
        this.mulLatency = mulLatency;
        this.divLatency = divLatency;
        this.storeLatency = storeLatency;
        this.loadLatency = loadLatency;
        this.fileName = fileName;

        program = readFile(fileName);
        issuedInstructions = new ArrayList<String>();
        queue = new QueueCell[program.size()]; //how big should it be ?

        for (int i = 0; i < queue.length; i++) {
            queue[i] = new QueueCell(program.get(i));
        }
        printQueue();

        runProgram();
    }

    public void printQueue() {
        for (QueueCell cell : queue
        ) {
            System.out.println(cell);
        }
    }

    public boolean allWroteBack() {
        for (QueueCell queueCell : queue
        ) {
            // didnt write back
            if (queueCell.writeResult == 0) {
                return false;
            }
        }

        return true;
    }

    public int checkFreeAddStation() {
        for (int i = 0; i < add.length; i++) {
            if (!add[i].busy)
                return i;
        }

        return -1;
    }

    public int checkFreeMulStation() {
        for (int i = 0; i < multiply.length; i++) {
            if (!multiply[i].busy)
                return i;
        }

        return -1;
    }

    public void runProgram() {

        while (!allWroteBack()) {
            boolean someoneIssued = false;

            for (QueueCell queueCell : queue
            ) {
                // not issued
                if (queueCell.issue == 0 && !someoneIssued) {
                    someoneIssued = true;
                    switch (queueCell.instruction) {
                        case "ADD.D":
                        case "SUB.D":
                            int freeStationIndex = checkFreeAddStation();

                            if (freeStationIndex != -1) {
                                add[freeStationIndex].busy = true;
                                add[freeStationIndex].op = queueCell.instruction;

                                int i = Integer.parseInt(queueCell.i.substring(1));
                                int j = Integer.parseInt(queueCell.j.substring(1));
                                int k = Integer.parseInt(queueCell.k.substring(1));

                                if (registerFile.qi[j] == null) {
                                    add[freeStationIndex].vj = registerFile.value[j];
                                } else {
                                    add[freeStationIndex].qj = registerFile.qi[j];
                                }

                                if (registerFile.qi[k] == null) {
                                    add[freeStationIndex].vk = registerFile.value[k];
                                } else {
                                    add[freeStationIndex].qk = registerFile.qi[k];
                                }

                                registerFile.qi[i] = "A" + freeStationIndex;
                                queueCell.reservationStation = "A" + freeStationIndex;
                            }

                            break;
                        case "MUL.D":
                        case "DIV.D":
                            int multStationIndex = checkFreeAddStation();

                            if (multStationIndex != -1) {
                                multiply[multStationIndex].busy = true;
                                multiply[multStationIndex].op = queueCell.instruction;

                                int i = Integer.parseInt(queueCell.i.substring(1));
                                int j = Integer.parseInt(queueCell.j.substring(1));
                                int k = Integer.parseInt(queueCell.k.substring(1));

                                if (registerFile.qi[j] == null) {
                                    multiply[multStationIndex].vj = registerFile.value[j];
                                } else {
                                    multiply[multStationIndex].qj = registerFile.qi[j];
                                }

                                if (registerFile.qi[k] == null) {
                                    multiply[multStationIndex].vk = registerFile.value[k];
                                } else {
                                    multiply[multStationIndex].qk = registerFile.qi[k];
                                }

                                registerFile.qi[i] = "M" + multStationIndex;
                                queueCell.reservationStation = "M" + multStationIndex;

                            }
                            break;
                        case "L.D":

                            break;

                        case "S.D":
                            break;

                    }
                }

                // issued but didnt begin
                if (queueCell.issue != 0 && queueCell.executionBegin == 0) {
                    String reservationStation = queueCell.reservationStation;

                    String type = reservationStation.substring(0, 1);
                    int index = Integer.parseInt(reservationStation.substring(1));

                    switch (type) {
                        case "A":
                            if (add[index].qk == null && add[index].qj == null) {
                                queueCell.executionBegin = clock;
                            }

                            break;
                        case "M":
//                            multiply[index];
                    }

                }

            }
        }
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
//    	System.out.println(Integer.parseInt("100",2));
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
