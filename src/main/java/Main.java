import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import forJson.ConnectionsForJson;
import forJson.JsonResponse;
import forJson.LineForJson;
import forJsoup.StationForJsoup;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;


public class Main {

    private static final int INDEX_LINE = 0;
    private static final int INDEX_NAME = 1;
    private static final int INDEX_TRANSITIONS = 3;

    private static final String DOUBLE_LINE = "8А 11";
    private static final String LINE_8A = "8А";
    private static final String LINE_11 = "11";
    private static final String NAME_LINE_11 = "Большая кольцевая линия";

    private static final String LINK_WIKI_MOSCOW_METRO = "https://ru.wikipedia.org/wiki/Список_станций_Московского_метрополитена";
    private static final String PATH_JSONFILE = "data/map_Moscow_Metro.json";
    private static Document documentMoscowMetro;

    private static ArrayList<StationForJsoup> stationsForJsoup = new ArrayList<>();
    public static void main(String[] args) {

        try {
            documentMoscowMetro = Jsoup.connect(LINK_WIKI_MOSCOW_METRO).maxBodySize(0).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        parseHTML();
        createAndAddConnectStationForJsoup();
        String json = new GsonBuilder().setPrettyPrinting().create().toJson(CreateAndWriteJsonResponse());
        try {
            if(Files.notExists(Paths.get(PATH_JSONFILE))){
                if (Files.notExists(Paths.get(PATH_JSONFILE).getParent())){
                    Files.createDirectory(Paths.get(PATH_JSONFILE).getParent());
                }
                Files.createFile(Paths.get(PATH_JSONFILE));
            }
            FileWriter fileWriter = new FileWriter(PATH_JSONFILE);
            fileWriter.write(json);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader( new FileReader(PATH_JSONFILE));
            JsonResponse fromJsonResponse = new Gson().fromJson(reader, JsonResponse.class);

            for(LineForJson line : fromJsonResponse.getLines()){
                System.out.printf("Stations count %s on the line -- %s\n", fromJsonResponse.getStations().get(line.getNumber()).size(), line.getName());
            }

        }catch (FileNotFoundException e){
            e.getStackTrace();
        }

    }

    private static JsonResponse CreateAndWriteJsonResponse(){

        JsonResponse jsonResponse = new JsonResponse();

        ArrayList<StationForJsoup> connectedStations = new ArrayList<>();

        for (StationForJsoup station : stationsForJsoup){
            //number, name

            LineForJson line = new LineForJson(station.getNumberLine(), station.getNameLine());
            if(!jsonResponse.getLines().contains(line)){
                jsonResponse.addLine(line);
            }

            if(!jsonResponse.getStations().containsKey(station.getNumberLine())){
                jsonResponse.addStations(station.getNumberLine(), new ArrayList<>());
            }
            jsonResponse.getStations().get(station.getNumberLine()).add(station.getNameStation());

            if(station.getStationsOfConnect().size() != 0 && !connectedStations.contains(station)){
                ArrayList<ConnectionsForJson> connections = new ArrayList<>();
                //line, name
                ConnectionsForJson connectionsForJson = new ConnectionsForJson(station.getNumberLine(), station.getNameStation());
                connections.add(connectionsForJson);
                connectedStations.add(station);
                for(StationForJsoup stationConnect : station.getStationsOfConnect()){
                    ConnectionsForJson connectionsForJsonSecond = new ConnectionsForJson(stationConnect.getNumberLine(), stationConnect.getNameStation());
                    connections.add(connectionsForJsonSecond);
                    connectedStations.add(stationConnect);
                }
            jsonResponse.addConnections(connections);
            }
        }
        return jsonResponse;
    }

    private static void parseHTML(){
        documentMoscowMetro.select(".standard.sortable").select("tr").stream()
                .map(element -> element.select("td"))
                .filter(elements -> elements.size() != 0)
                .forEach(Main::createStationFromJsoup);
    }

    private static void createStationFromJsoup(Elements elements){
        if(parseLineNumber(elements.get(INDEX_LINE).text()).equals(DOUBLE_LINE)){
            //nameStation, nameLine, numberLine
            StationForJsoup stationForJsoup8A = new StationForJsoup(
                    elements.get(INDEX_NAME).select("a").first().text(),
                    elements.get(INDEX_LINE).select("span").attr("title"),
                    LINE_8A);
            StationForJsoup stationForJsoup11 = new StationForJsoup(
                    elements.get(INDEX_NAME).select("a").first().text(),
                    NAME_LINE_11,
                    LINE_11
            );
            stationForJsoup8A.setListLinesOfConnect(parseNumberLinesConnect(elements));
            stationForJsoup8A.setListStationsNameOfConnect(parseNameStationsConnect(elements));
            stationForJsoup11.setListLinesOfConnect(parseNumberLinesConnect(elements));
            stationForJsoup11.setListStationsNameOfConnect(parseNameStationsConnect(elements));
            stationsForJsoup.add(stationForJsoup8A);
            stationsForJsoup.add(stationForJsoup11);
        }else {
            //nameStation, nameLine, numberLine
            StationForJsoup stationForJsoup = new StationForJsoup(
                    elements.get(INDEX_NAME).select("a").first().text(),
                    elements.get(INDEX_LINE).select("span").attr("title"),
                    parseLineNumber(elements.get(INDEX_LINE).text()));
            stationForJsoup.setListLinesOfConnect(parseNumberLinesConnect(elements));
            stationForJsoup.setListStationsNameOfConnect(parseNameStationsConnect(elements));
            stationsForJsoup.add(stationForJsoup);

        }

    }

    private static ArrayList<String> parseNumberLinesConnect(Elements elements){
        String[] stringsNumberLinesConnectStation = elements.get(INDEX_TRANSITIONS).text().split(" ");
        ArrayList<String> listNumberLinesForConnect = new ArrayList<>(Arrays.asList(stringsNumberLinesConnectStation));
        for (String str : stringsNumberLinesConnectStation){
            if(str.equals(DOUBLE_LINE.replace(" ", ""))){
                listNumberLinesForConnect.add(LINE_8A);
                listNumberLinesForConnect.add(LINE_11);
                listNumberLinesForConnect.remove(str);
            }
        }
        return listNumberLinesForConnect;
    }
    private static ArrayList<String> parseNameStationsConnect(Elements elementStation){
        ArrayList<String> strings = new ArrayList<>();
        ArrayList <Element> elementsSpan = elementStation.get(INDEX_TRANSITIONS).select("span");
        for (Element elementSpan : elementsSpan){
            if(elementSpan.attr("title").length() > 0){
                strings.add(elementSpan.attr("title"));
            }
        }
        return strings;
    }

    private static String parseLineNumber(String lineNumber){
        return lineNumber.substring(0, lineNumber.length() - 2);
    }

    private static void createAndAddConnectStationForJsoup(){
        int count = 0;
        for (StationForJsoup stationFromJsoup : stationsForJsoup){
            for (StationForJsoup stationFromJsoup1 : stationsForJsoup){
                if(stationFromJsoup1.getListStationsNameOfConnect().size() != 0){
                    for (String connect : stationFromJsoup.getListLinesOfConnect()){
                        if(connect.equals(stationFromJsoup1.getNumberLine())){
                            for (String stationsTo : stationFromJsoup.getListStationsNameOfConnect()){
                                if(stationsTo.contains(stationFromJsoup1.getNameStation())){
                                    if(!stationsForJsoup.get(count).getStationsOfConnect().contains(stationFromJsoup1))
                                        stationsForJsoup.get(count).addStationsOfConnect(stationFromJsoup1);
                                }
                            }
                        }
                    }
                }
            }
            count++;
        }
    }


}
