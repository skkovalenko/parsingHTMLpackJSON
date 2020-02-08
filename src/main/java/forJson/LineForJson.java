package forJson;

public class LineForJson {
    private String number;
    private String name;

    public LineForJson(String number, String name){
        this.number = number;
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        LineForJson line = (LineForJson) obj;
        return (number.equals(line.number) && name.equals(line.name));
    }
}
