package forJsoup;

import java.util.ArrayList;

public class StationForJsoup {

    private String nameLine;
    private String numberLine;
    private String nameStation;
    private ArrayList<String> listLinesOfConnect;
    private ArrayList<String>listStationsNameOfConnect;
    private ArrayList<StationForJsoup> stationsOfConnect = new ArrayList<>();

    public StationForJsoup(String nameStation, String nameLine, String numberLine){
        this.nameStation = nameStation;
        this.nameLine = nameLine;
        this.numberLine = numberLine;
    }

    public String getNameStation() {
        return nameStation;
    }

    public String getNameLine() {
        return nameLine;
    }

    public String getNumberLine() {
        return numberLine;
    }

    public ArrayList<String> getListLinesOfConnect() {
        return listLinesOfConnect;
    }

    public ArrayList<StationForJsoup> getStationsOfConnect() {
        return stationsOfConnect;
    }

    public void addStationsOfConnect(StationForJsoup stationOfConnect) {
        this.stationsOfConnect.add(stationOfConnect);
    }

    public void setListLinesOfConnect(ArrayList<String> listLinesOfConnect) {
        this.listLinesOfConnect = listLinesOfConnect;
    }
    public void setListStationsNameOfConnect(ArrayList<String> listStationsNameOfConnect) {
        this.listStationsNameOfConnect = listStationsNameOfConnect;
    }

    public ArrayList<String> getListStationsNameOfConnect() {
        return listStationsNameOfConnect;
    }

    @Override
    public String toString() {

        return nameStation + " / " + nameLine + " / " + numberLine + " / "+ listLinesOfConnect + " / "+ listStationsNameOfConnect;
    }
}
