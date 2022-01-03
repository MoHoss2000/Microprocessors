public class QueueCell {
    String instruction;
    String i;
    String j;
    String k;

    int issue;
    int executionBegin;
    int executionEnd;
    int writeResult;

    String reservationStation;


    public QueueCell(String instructionString) {


        String[] splitted = instructionString.split(" ");

        instruction = splitted[0];

        splitted = splitted[1].split(",");
        i = splitted[0];
        j = splitted[1];

        if (!instruction.equals("L.D") && !instruction.equals("S.D")) {
            k = splitted[2];
        }

    }

    public String toString() {
        return instruction + " " + i + " " + j + " " + k + " " + issue + " "
                + executionBegin + " " + executionEnd + " " + writeResult;
    }
}
