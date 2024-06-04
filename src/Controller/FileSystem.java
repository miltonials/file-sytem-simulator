package Controller;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import Model.Directory;
import Model.Disk;
import Model.File;
import Model.Node;

public class FileSystem {
    private final Directory root;
    private Directory current;
    private PropertyChangeSupport support;
    private Disk disk;

    public FileSystem(int pSectorsQuantity, int pSizeSector) {
        root = new Directory("root", "root/", null);
        current = root;
        support = new PropertyChangeSupport(this);
        disk = new Disk(pSectorsQuantity, pSizeSector);
        
        disk.newFile("primerArchivo.txt", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        disk.getSectors().get(2).cleanSector();
        disk.getSectors().get(4).cleanSector();
        disk.getSectors().get(6).cleanSector();
        disk.newFile("segundoArchivo.txt", "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        System.out.println(disk.toString());
        System.out.println("------------------------");
        disk.readFile(0);
        disk.readFile(2);
    }

    public Directory getRoot() {
        return root;
    }

    public Directory getCurrent() {
        return current;
    }

    public void setCurrent(Directory current) {
        Directory oldCurrent = this.current;
        this.current = current;
        support.firePropertyChange("current", oldCurrent, current);
    }

    public void changeDirectory(String name) {
        if (name.equals("..")) {
            if (current != root) {
                setCurrent(current.getParent());
            }
        } else {
            for (Node node : current.getChildren()) {
                if (node.getName().equals(name) && node instanceof Directory) {
                    setCurrent((Directory) node);
                    break;
                }
            }
        }
    }

    public void createDirectory(String name) {
        current.addChild(new Directory(name, current.getPath() + name + "/", current));
    }
    
    public boolean directoryExists(String name) {
        Node[] children = current.getChildren();
        for (int i = 0; i < children.length; i++) {
            Node node = children[i];
            if (node.getName().equals(name) && node instanceof Directory) {
                return true;
            }
        }
        return false;
    }
    
    public void removeDirectory(String name) {
        current.removeChild(name);
    }
    
    public void createFile(String name) {
        current.addChild(new File(name, current.getPath() + name, current));
    }

    public void remove(String name) {
        current.removeChild(name);
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }
}