package Model;

public class File extends Node{
    private int start;
    private String created;

    public File(String name, String path, Directory parent, int start) {
        super(name, path, parent);
        this.start = start;
    }

    public int getStart() {
        return start;
    }
    
}
