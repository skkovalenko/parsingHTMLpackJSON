package forJson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonResponse {

    private Map<String, ArrayList<String>> stations = new HashMap<>();
    private List<LineForJson> lines = new ArrayList<>();
    private List<ArrayList<ConnectionsForJson>> connections = new ArrayList<>();

    public Map<String, ArrayList<String>> getStations() {
        return stations;
    }

    public void addStations(String numberLine, ArrayList<String> stations) {
        this.stations.put(numberLine, stations);
    }

    public List<LineForJson> getLines() {
        return lines;
    }

    public void addLine(LineForJson line) {
        this.lines.add(line);
    }

    public List<ArrayList<ConnectionsForJson>> getConnections() {
        return connections;
    }

    public void addConnections(ArrayList<ConnectionsForJson> connections) {
        this.connections.add(connections);
    }
}
