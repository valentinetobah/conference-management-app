package za.co.onespark;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConferenceManagementApp {

    private static final LocalTime morningSessionStartTime = LocalTime.parse("09:00");
    private static final LocalTime lunchStartTime = LocalTime.parse("12:00");
    private static final LocalTime lunchEndTime = lunchStartTime.plusMinutes(60);
    private static final LocalTime networkingEventEarlyStartTime = LocalTime.parse("16:00");
    private static final LocalTime networkingEventLateStartTime = LocalTime.parse("17:00");
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("hh:mma");

    public static void main(String[] args) {
        String  inputFile = "/Users/valentine/workspace/conference-managment-app/src/main/resources/talks.txt";
        try {
            List<String> lines = readFileLines(inputFile);
            Map<String, Integer> map = convertListToMap(lines);
            scheduleConferenceTalks(map);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static List<String> readFileLines(String inputFile) throws IOException {
        return Files.readAllLines(Paths.get(inputFile));
    }
    public static Map<String, Integer> convertListToMap(List<String> lines){
        String talk ;
        int duration;
        Map<String, Integer> talksMap = new LinkedHashMap<>();
        for(String line: lines){

            if("".equals(line))
                continue;

            if(line.endsWith("lightning")){
                int index = line.lastIndexOf("lightning");
                talk = line.substring(0, index);
                duration = 5;
            } else {
                duration =  Integer.parseInt(line.replaceAll("\\D", ""));
                talk = line.replaceAll("\\d+min",  "");
            }
            talksMap.put(talk, duration);
        }
        return talksMap;
    }

    public static  void scheduleConferenceTalks(Map<String, Integer> map){
        LocalTime currentTime = morningSessionStartTime;
        LocalTime nextCurrentTime = morningSessionStartTime;
        int trackNumber = 1;

        System.out.println("\nTrack " + trackNumber + ":\n");

        Iterator<Map.Entry<String, Integer>> it = map.entrySet().iterator();

        while(it.hasNext()){
            Map.Entry<String, Integer> entry = it.next();
            int durationInMins = entry.getValue();
            nextCurrentTime = currentTime.plusMinutes(durationInMins);
            if(nextCurrentTime.isAfter(lunchStartTime) && nextCurrentTime.isBefore(lunchEndTime)){

                System.out.println(lunchStartTime.format(dtf) + " Lunch");
                currentTime = lunchEndTime;

            } else if (nextCurrentTime.isAfter(networkingEventEarlyStartTime)
                    && nextCurrentTime.isAfter(networkingEventLateStartTime)) {

                System.out.println(networkingEventLateStartTime.format(dtf) + " Networking Event");
                currentTime = morningSessionStartTime;
                trackNumber++;
                System.out.println("\nTrack " + trackNumber + ":\n");
            }

            System.out.println(currentTime.format(dtf) + " " + entry.getKey() + durationInMins + "min");
            currentTime = currentTime.plusMinutes(durationInMins);

            if (!it.hasNext() && nextCurrentTime.isAfter(networkingEventEarlyStartTime)) {
                System.out.println(networkingEventLateStartTime.format(dtf) + " Networking Event");
            }
        }
    }
}