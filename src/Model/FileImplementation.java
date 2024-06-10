package Model;

public class FileImplementation extends Node{
    private int start;
    private String created;

    public FileImplementation(String name, String path, Directory parent, int start) {
        super(name, path, parent);
        this.start = start;
    }

    public int getStart() {
        return start;
    }
    
}
