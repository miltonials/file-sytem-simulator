package Model;
import java.util.Date;

public class Node {
    private String name;
    private String path;
    private Directory parent;
    private String created;

    public Node(String name, String path, Directory parent) {
        this.name = name;
        this.path = path;
        this.parent = parent;
        this.created = new Date().toString();
    }
    
    public Directory getParent() {
        return parent;
    }

    public String getCreated() {
        return created;
    }
    
    public void delete() {
        parent.removeChild(name);
    }
    
    public void setParent(Directory parent) {
        this.parent = parent;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
}
