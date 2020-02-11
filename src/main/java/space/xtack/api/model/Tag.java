package space.xtack.api.model;

public class Tag {

    private String name;
    private String display;

    public Tag(String name, String display) {
        this.name = name;
        this.display = display;
    }

    public String getName() {
        return name;
    }

    public String getDisplay() {
        return display;
    }
}
