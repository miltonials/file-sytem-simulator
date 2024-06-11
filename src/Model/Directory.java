package Model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;

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
    public int getChildCount() {
        return children.size();
    }
    public Node getChildAt(int index) {
        return (Node) children.toArray()[index];
    }
    public boolean isLeaf() {
        return false;
    }
    public boolean getAllowsChildren() {
        return true;
    }
    public int getIndex(Node node) {
        return new ArrayList<>(children).indexOf(node);
    }

    public Enumeration<Node> children() {
        return Collections.enumeration(children);
    }

    public Collection<? extends FileImplementation> searchFile(String name) {
        ArrayList<FileImplementation> files = new ArrayList<>();
        for (Node node : children) {
            if (node instanceof FileImplementation && node.getName().contains(name)) {
                files.add((FileImplementation) node);
            }
            // Si es un directorio, se busca en sus hijos
            if (node instanceof Directory) {
                files.addAll(((Directory) node).searchFile(name));
            }
        }
        return files;
    }

    public Collection<? extends Directory> searchDirectory(String name) {
        ArrayList<Directory> directories = new ArrayList<>();
        for (Node node : children) {
            if (node instanceof Directory && node.getName().contains(name)) {
                directories.add((Directory) node);
            }
            // Si es un directorio, se busca en sus hijos
            if (node instanceof Directory) {
                directories.addAll(((Directory) node).searchDirectory(name));
            }
        }
        return directories;
    }

    //verifica si existe un directorio con el path que se le pasa
    public boolean directoryExists(String path, Node node) {
        for (Node child : ((Directory) node).children) {
            if (child instanceof Directory) {
                Directory directory = (Directory) child;
                System.out.println("path: " + path);
                System.out.println("directory.getPath(): " + directory.getPath());
                if (directory.getPath().equals(path)) {
                    return true;
                }
                if (directory.directoryExists(path, child)) {
                    return true;
                }
            }
        }
        return false;

    }

    public Directory getDirectory(String path, Node node) {
        for (Node child : ((Directory) node).children) {
            if (child instanceof Directory) {
                Directory directory = (Directory) child;
                if (directory.getPath().equals(path)) {
                    return directory;
                }
                if (directory.directoryExists(path, child)) {
                    return directory;
                }
            }
        }
        return null;
    }

    

}
