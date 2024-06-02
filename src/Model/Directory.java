package Model;

import java.util.ArrayList;
import java.util.Collection;

public class Directory extends Node {
    private Collection<Node> children;

    public Directory(String name, String path, Directory parent) {
        super(name, path, parent);
        this.children = new ArrayList<>();
    }
    
    public void addChild(Node node) {
        this.children.add(node);
    }
    
    public void removeChild(String name) {
        for (Node node : children) {
            if (node.getName().equals(name)) {
                children.remove(node);
                break;
            }
        }
    }
    
    public void delete(){
        for (Node node : children) {
            node.delete();
        }
        this.getParent().removeChild(this.getName());
        //FALTA MANEJAR EL BORRADO DE ARCHIVOS EN DISCO
    }
    
    public Node getChild(String name) {
        for (Node node : children) {
            if (node.getName().equals(name)) {
                return node;
            }
        }
        return null;
    }
    
    public Node[] getChildren() {
        return children.toArray(new Node[children.size()]);
    }
}
