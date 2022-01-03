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

    int issueInstruction;

    String fileName;
    ArrayList<String> program;

    // should we handle the scenario where the queue is less than the size of the program ?!

    public Main(int addLatency, int subLatency, int mulLatency,
                int divLatency, int loadLatency, int storeLatency,
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

        for (int i = 0; i < load.length; i++) {
            load[i] = new LoadBuffer();
        }

        for (int i = 0; i < store.length; i++) {
            store[i] = new StoreBuffer();
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
        queue = new QueueCell[20]; //how big should it be ?

        for (int i = 0; i < program.size(); i++) {
            queue[i] = new QueueCell(program.get(i));
        }

        registerFile.value[2] = 2;
        registerFile.value[4] = 4;


        // printQueue();

        runProgram();
    }

    public void printQueue() {
        for (int i = 0; i < program.size(); i++) {

            QueueCell cell = queue[i];
            System.out.println(i + ": " + cell);
        }
    }

    public void printRegisterFile() {
        System.out.println("==========================");
        System.out.println("Register File");
        System.out.println("==========================");
        for (int i = 0; i < registerFile.qi.length; i++) {

            System.out.println("F" + i + ": " + registerFile.qi[i] + " " + registerFile.value[i]);
        }
        System.out.println();
    }

    public void printReservationStations() {

        System.out.println("==========================");
        System.out.println("Add reservation stations");
        System.out.println("==========================");
        for (int i = 0; i < add.length; i++) {
            System.out.println(i + ": " + add[i]);
        }

        System.out.println();

        System.out.println("==========================");
        System.out.println("Multiply reservation stations");
        System.out.println("==========================");
        for (int i = 0; i < multiply.length; i++) {
            System.out.println(i + ": " + multiply[i]);
        }

        System.out.println();

        System.out.println("==========================");
        System.out.println("Load buffer");
        System.out.println("==========================");
        for (int i = 0; i < load.length; i++) {
            System.out.println(i + ": " + load[i]);
        }

        System.out.println();

        System.out.println("==========================");
        System.out.println("Store buffer");
        System.out.println("==========================");
        for (int i = 0; i < store.length; i++) {
            System.out.println(i + ": " + store[i]);
        }

        System.out.println();
    }

    public boolean allWroteBack() {
        for (int i = 0; i < program.size(); i++) {
            // didnt write back
            if (queue[i].writeResult == 0) {
                return false;
            }
        }

        return true;
    }

    public boolean allIssued() {
        for (int i = 0; i < program.size(); i++) {
            if (queue[i].issue == 0) {
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

    public int checkFreeLoadBuffer() {
        for (int i = 0; i < load.length; i++) {
            if (!load[i].busy)
                return i;
        }

        return -1;
    }

    public int checkFreeStoreBuffer() {
        for (int i = 0; i < store.length; i++) {
            if (!store[i].busy)
                return i;
        }

        return -1;
    }

    public void executeInstruction(QueueCell queueCell) {
        String reservationStation = queueCell.reservationStation;
        int reservationStationIndex = Integer.parseInt(reservationStation.substring(1));

        switch (queueCell.instruction) {
            case "ADD.D":
                add[reservationStationIndex].result = add[reservationStationIndex].vj
                        + add[reservationStationIndex].vk;

//                System.out.println(add[reservationStationIndex].result);
                break;
            case "SUB.D":
                add[reservationStationIndex].result = add[reservationStationIndex].vj
                        - add[reservationStationIndex].vk;

//                System.out.println(add[reservationStationIndex].result);

                break;
            case "MUL.D":
                multiply[reservationStationIndex].result = multiply[reservationStationIndex].vj
                        * multiply[reservationStationIndex].vk;

//                System.out.println(multiply[reservationStationIndex].result);

                break;
            case "DIV.D":
                multiply[reservationStationIndex].result = multiply[reservationStationIndex].vj
                        / multiply[reservationStationIndex].vk;
                break;
            case "L.D":
                load[reservationStationIndex].result = memory[Integer.parseInt(load[reservationStationIndex].a)];
                break;
        }
    }

    public void broadcastResult(double value, String reservationStation) {
        for (int i = 0; i < add.length; i++) {
            if (add[i].qj != null && add[i].qj.equals(reservationStation)) {
                add[i].qj = null;
                add[i].vj = value;
            }

            if (add[i].qk != null && add[i].qk.equals(reservationStation)) {
                add[i].qk = null;
                add[i].vk = value;
            }
        }

        for (int i = 0; i < multiply.length; i++) {
            if (multiply[i].qj != null && multiply[i].qj.equals(reservationStation)) {
                multiply[i].qj = null;
                multiply[i].vj = value;
            }

            if (multiply[i].qk != null && multiply[i].qk.equals(reservationStation)) {
                multiply[i].qk = null;
                multiply[i].vk = value;
            }
        }

        for (int i = 0; i < store.length; i++) {
            if (store[i] != null && store[i].q != null && store[i].q.equals(reservationStation)) {
                store[i].q = null;
                store[i].v = value + "";
            }
        }
    }

    public void runProgram() {

        while (!allWroteBack()) {

            System.out.println("Cycle No: " + clock);

            if (issueInstruction < program.size() && queue[issueInstruction].issue == 0) {
                QueueCell queueCell = queue[issueInstruction];

                switch (queueCell.instruction) {
                    case "ADD.D":
                    case "SUB.D":
                        int freeStationIndex = checkFreeAddStation();

                        if (freeStationIndex != -1) {
                            queueCell.issue = clock;
                            issueInstruction++;

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
                        int multStationIndex = checkFreeMulStation();

                        if (multStationIndex != -1) {
                            queueCell.issue = clock;

                            issueInstruction++;

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
                        int loadBufferIndex = checkFreeLoadBuffer();

                        if (loadBufferIndex != -1) {
                            queueCell.issue = clock;

                            issueInstruction++;

                            load[loadBufferIndex].busy = true;

                            int i = Integer.parseInt(queueCell.i.substring(1)); // register to write in
                            int j = Integer.parseInt(queueCell.j); // address


                            load[loadBufferIndex].a = "" + j;

                            registerFile.qi[i] = "L" + loadBufferIndex;
                            queueCell.reservationStation = "L" + loadBufferIndex;

                        }
                        break;

                    case "S.D":
                        int storeBufferIndex = checkFreeStoreBuffer();

                        if (storeBufferIndex != -1) {
                            queueCell.issue = clock;

                            issueInstruction++;

                            store[storeBufferIndex].busy = true;

                            int i = Integer.parseInt(queueCell.i.substring(1)); // register to write in
                            int j = Integer.parseInt(queueCell.j); // address

                            if (registerFile.qi[i] == null) {
                                // has correct value

                                store[storeBufferIndex].v = "" + registerFile.value[i];
                            } else {
                                store[storeBufferIndex].q = registerFile.qi[i];
                            }

                            store[storeBufferIndex].a = "" + j;

                            queueCell.reservationStation = "S" + storeBufferIndex;

                        }
                        break;
                }
            }


            boolean someoneWroteBack = false;
            for (int programIndex = 0; programIndex < program.size(); programIndex++) {
                QueueCell queueCell = queue[programIndex];


                // issued but didnt begin
                if (queueCell.issue != 0 && queueCell.executionBegin == 0 && queueCell.issue != clock) {


                    String reservationStation = queueCell.reservationStation;


                    String type = reservationStation.charAt(0) + "";
                    int index = Integer.parseInt(reservationStation.substring(1));


                    switch (type) {
                        case "A":
                            if (add[index].qk == null && add[index].qj == null) {
                                queueCell.executionBegin = clock;
                            }

                            break;
                        case "M":
                            if (multiply[index].qk == null && multiply[index].qj == null) {
                                queueCell.executionBegin = clock;
                            }
                            break;
                        case "L":
                            queueCell.executionBegin = clock;
                            break;
                        case "S":
                            if (store[index].q == null) {
                                queueCell.executionBegin = clock;
                            }

                            break;
                    }
                }

                if (queueCell.issue != 0 && queueCell.executionBegin != 0 && queueCell.executionEnd == 0) {
                    String reservationStation = queueCell.reservationStation;

                    int end = queueCell.executionBegin - 1;
                    switch (queueCell.instruction) {
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
                        case "S.D":
                            end += storeLatency;
                            break;
                    }

                    if (end == clock) {
                        // execute instruction
                        executeInstruction(queueCell);

                        queueCell.executionEnd = clock;
                    }
                }

            }

            for (int i = 0; i < program.size(); i++) {
                QueueCell queueCell = queue[i];

                if (queueCell.writeResult == 0 && queueCell.executionEnd + 1 <= clock
                        && queueCell.executionEnd != 0 && !someoneWroteBack) {

                    String reservationStation = queueCell.reservationStation;
                    int reservationStationIndex = Integer.parseInt(reservationStation.substring(1));

                    queueCell.writeResult = clock;
                    someoneWroteBack = true;

                    switch (queueCell.instruction) {
                        case "ADD.D":
                        case "SUB.D":
                            double addResult = add[reservationStationIndex].result;
                            int wbRegister = Integer.parseInt(queueCell.i.substring(1));

                            if (registerFile.qi[wbRegister] != null &&
                                    registerFile.qi[wbRegister].equals(queueCell.reservationStation)) {
                                registerFile.qi[wbRegister] = null;
                                registerFile.value[wbRegister] = addResult;
                            }

                            System.out.println();
                            System.out.println("Reservation station " + reservationStation + " wrote the value " +
                                    +addResult + " to the bus"
                            );
                            System.out.println();

                            broadcastResult(addResult, reservationStation);

                            add[reservationStationIndex] = new ReservationStation();
                            break;
                        case "MUL.D":
                        case "DIV.D":
                            double multiplyResult = add[reservationStationIndex].result;
                            int wbRegisterMul = Integer.parseInt(queueCell.i.substring(1));

                            if (registerFile.qi[wbRegisterMul] != null &&
                                    registerFile.qi[wbRegisterMul].equals(queueCell.reservationStation)) {
                                registerFile.qi[wbRegisterMul] = null;
                                registerFile.value[wbRegisterMul] = multiplyResult;
                            }

                            System.out.println();
                            System.out.println("Reservation station " + reservationStation + " wrote the value " +
                                    +multiplyResult + " to the bus"
                            );
                            System.out.println();

                            broadcastResult(multiplyResult, reservationStation);

                            multiply[reservationStationIndex] = new ReservationStation();
                            break;
                        case "L.D":
                            double loadResult = load[reservationStationIndex].result;
                            int wbRegisterLoad = Integer.parseInt(queueCell.i.substring(1));

                            if (registerFile.qi[wbRegisterLoad] != null &&
                                    registerFile.qi[wbRegisterLoad].equals(queueCell.reservationStation)) {
                                registerFile.qi[wbRegisterLoad] = null;
                                registerFile.value[wbRegisterLoad] = loadResult;
                            }

                            System.out.println();
                            System.out.println("Reservation station " + reservationStation + " wrote the value " +
                                    +loadResult + " to the bus"
                            );
                            System.out.println();

                            broadcastResult(loadResult, reservationStation);

                            load[reservationStationIndex] = new LoadBuffer();

                            break;
                        case "S.D":
                            int memoryAddress = Integer.parseInt(store[reservationStationIndex].a);
                            double value = Double.parseDouble(store[reservationStationIndex].v);


                            System.out.println();
                            System.out.println("Reservation station " + reservationStation + " wrote the value " +
                                    +value + " to the bus"
                            );

                            System.out.println();

                            memory[memoryAddress] = value;

                            store[reservationStationIndex] = new StoreBuffer();
                            break;
                    }
                }
            }


            printQueue();
            printReservationStations();
            printRegisterFile();

            System.out.println();
            clock++;
        }

        System.out.println(memory[49]);
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
