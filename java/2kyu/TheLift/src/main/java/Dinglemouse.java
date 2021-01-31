import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class Dinglemouse {

    private static Boolean goingUp = true;
    private static Boolean requestChange = false;
    private static ArrayList<Integer> liftPeople = new ArrayList<Integer>();
    private static Integer toDeliver = 0;
    private static int[][] queuesLocal;

    public static int[] theLift(final int[][] queues, final int capacity) {
        goingUp = true;
        ArrayList<Integer> result = new ArrayList<Integer> ();
        Boolean goingup =true;

        int i = 0;

        queuesLocal = new int[queues.length][queues.length];
        for (int[] queue : queues) {
            toDeliver += queue.length;
            queuesLocal[i] = new int[queue.length];
            int j = 0;
            for (int value : queues[i]) {
                queuesLocal[i][j] = queues[i][j];
                j++;
            }
            i++;
        }


        int currentFloor = 0;
        int MAX_FLOORS = queuesLocal.length;
        result.add(currentFloor);

        //wladowanie ludzi
        for (int placeInQueue=0; placeInQueue<queues[currentFloor].length; placeInQueue++){
            int passengerDestination = queuesLocal[currentFloor][placeInQueue];

            if (liftPeople.size() == capacity)
                break;

            if ((passengerDestination > currentFloor && goingUp) || (passengerDestination < currentFloor && !goingUp)) {
                if (passengerDestination != -1) {
                    liftPeople.add(passengerDestination);
                    queuesLocal[currentFloor][placeInQueue] = -1;
                }
            }
        }


        while(toDeliver > 0) {
            Iterator<Integer> iter = liftPeople.iterator();

            getIntoAndgetOutOfLift(currentFloor, capacity);


            //ktos na gorze jescze czeka
            //lub ktos na dole jeszcze czeka
            int nextFloor = calculateNextFloor(currentFloor, queuesLocal, MAX_FLOORS, liftPeople);

            //jak jade na gore lub w dol to zatrzymuje sie po drodze jak ktos chce wysiasc
            nextFloor= liftPeopleChangeDestination(nextFloor);

            //nikogo nie ma w kierunku jazdy nikt w windzie nie jedzie w kierunku jazdy
            if (nextFloor == 100 || nextFloor == -2 || nextFloor == -1){
                //zmiana kierunku
                goingUp = !goingUp;
                getIntoAndgetOutOfLift(currentFloor, capacity);
                nextFloor = calculateNextFloor(currentFloor, queuesLocal, MAX_FLOORS, liftPeople);
                nextFloor= liftPeopleChangeDestination(nextFloor);

            }
            result.add(nextFloor);
            currentFloor = nextFloor;

        }

        result.remove(result.size()-1);
        result.add(0);
        if (result.size() >=2 && result.get(result.size()-2) == 0)
            result.remove(result.size()-1);


        final int[] arr = new int[result.size()];
        int index = 0;
        for (Integer value: result) {
            arr[index++] = value;
        }

        return arr;
    }

    private static int calculateNextFloor(int currentFloor, int[][] queues, int MAX_FLOORS, ArrayList<Integer> liftPeople) {
        int nextFloor = -1;

        requestChange=false;
        if (goingUp && currentFloor!=MAX_FLOORS) {
            nextFloor = 100;
            for (int floor=currentFloor+1; floor<queuesLocal.length; floor++) {
                for (int placeInQueue=0; placeInQueue<queuesLocal[floor].length; placeInQueue++) {
                    int passengerDestination = queuesLocal[floor][placeInQueue];
                    //musi jechac w gore
                    if (passengerDestination != -1 && (passengerDestination > floor) ) {
                        //najmniejsze pietro ktos chce w gore jechac
                        if (floor < nextFloor)
                            nextFloor = floor;
                    }
                }
            }
            //nikt nie jedzie w gore
            if (nextFloor == 100){
                for (int floor=currentFloor+1; floor<queuesLocal.length; floor++)  {
                    for (int placeInQueue = 0; placeInQueue < queuesLocal[floor].length; placeInQueue++) {
                        int passengerDestination = queuesLocal[floor][placeInQueue];
                        // ostatni co jedzie w dol
                        if (passengerDestination != -1 && liftPeople.isEmpty()) {
                            nextFloor = floor;
                        }
                    }
                }
            }

        } else if (!goingUp && currentFloor!=0) {
            nextFloor = -2;
            //pierwszy co w dol
            for (int floor=currentFloor-1; floor>=0; floor--) {
                for (int placeInQueue=0; placeInQueue<queuesLocal[floor].length; placeInQueue++) {
                    int passengerDestination = queuesLocal[floor][placeInQueue];
                    if (passengerDestination != -1 && (passengerDestination < floor)) {
                        if (floor > nextFloor)
                            nextFloor = floor;
                    }
                }
            }
            if (nextFloor == -2) {
                for (int floor=currentFloor-1; floor>=0; floor--){
                    for (int placeInQueue=0; placeInQueue<queuesLocal[floor].length; placeInQueue++) {
                        int passengerDestination = queuesLocal[floor][placeInQueue];
                        //ostatni co jedzie w gore
                        if (passengerDestination != -1 && liftPeople.isEmpty()) {
                            nextFloor = floor;
                        }
                    }
                }
            }
        } else {
            nextFloor = -1;
        }

        return nextFloor;
    }

    private static void getIntoAndgetOutOfLift(Integer currentFloor, Integer capacity) {
        getIntoLift(currentFloor, capacity);
        getOutLift(currentFloor, capacity);
    }

    private static void getIntoLift(Integer currentFloor, Integer capacity) {
        Iterator<Integer> iter = liftPeople.iterator();
        while (iter.hasNext()) {
            Integer str = iter.next();
            if (str == currentFloor) {
                iter.remove();
                toDeliver--;
            }
        }
    }

    private static void getOutLift(Integer currentFloor, Integer capacity) {
        for (int placeInQueue=0; placeInQueue<queuesLocal[currentFloor].length; placeInQueue++){
            int passengerDestination = queuesLocal[currentFloor][placeInQueue];
            if (liftPeople.size() == capacity)
                break;
            if ((passengerDestination > currentFloor && goingUp) || (passengerDestination < currentFloor && !goingUp)) {
                if (passengerDestination != -1) {
                    liftPeople.add(passengerDestination);
                    queuesLocal[currentFloor][placeInQueue] = -1;
                }
            }
        }
    }

    private static Integer liftPeopleChangeDestination(Integer nextFloor) {
        if (!liftPeople.isEmpty()) {
            if (goingUp) {
                if (Collections.min(liftPeople) < nextFloor)
                    return Collections.min(liftPeople);
            } else {
                if (Collections.max(liftPeople) > nextFloor)
                    return Collections.max(liftPeople);
            }
        }
        return nextFloor;
    }

}