package Controller;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

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
        
        createFile("archivo1.txt", "xxxxxxxxxxxx");
        createFile("archivo2.txt", "yyyyyyyyyyyy");
        createFile("archivo3.txt", "zzzzzzzzzzzz");
        // disk.getSectors().get(2).cleanSector();
        // disk.getSectors().get(4).cleanSector();
        // disk.getSectors().get(6).cleanSector();
        // disk.newFile("segundoArchivo.txt", "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        // System.out.println(disk.toString());
        // System.out.println("------------------------");
        // disk.readFile(0);
        // disk.readFile(2);
        removeFile("primerArchivo");
        System.out.println(disk.toString());
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
    
    public boolean createFile(String name, String content) {
        int startSector = disk.newFile(name, content);
        if (fileExists(name)) {
            System.out.println("File already exists");
            return false;
        }
        if (startSector == -1) {
            System.out.println("Not enough space");
            return false;
        }

        current.addChild(new File(name, current.getPath() + name + "/", current, startSector));
        System.out.println(disk.toString());
        return true;
    }

    public boolean fileExists(String name) {
        Node[] children = current.getChildren();
        for (int i = 0; i < children.length; i++) {
            Node node = children[i];
            if (node.getName().equals(name) && node instanceof File) {
                return true;
            }
        }
        return false;
    }

    public String readFile(String name) {
        File file = null;
        for (Node node : current.getChildren()) {
            if (node.getName().equals(name) && node instanceof File) {
                file = (File) node;
                break;
            }
        }
        if (file == null) {
            return null;
        }
        // return disk.readFile(file.getStart());
        return disk.readFile(file.getStart());
    }

    public void modifyFile(String name, String content) {
        File file = null;
        for (Node node : current.getChildren()) {
            if (node.getName().equals(name) && node instanceof File) {
                file = (File) node;
                break;
            }
        }
        if (file == null) {
            return;
        }

        disk.modifyFile(file.getStart(), content);
        // file.setStart(disk.newFile(name, content));
        System.out.println(disk.toString());
    }

    public void removeFile(String name) {
        File file = null;
        for (Node node : current.getChildren()) {
            if (node.getName().equals(name) && node instanceof File) {
                file = (File) node;
                break;
            }
        }
        if (file == null) {
            return;
        }
        
        disk.deleteFile(file.getStart());
        current.removeChild(name);
        System.out.println(disk.toString());
    }

    public String getFileProperties(String name) {
        File file = null;
        String properties = "";
        for (Node node : current.getChildren()) {
            if (node.getName().equals(name) && node instanceof File) {
                file = (File) node;
                break;
            }
        }
        if (file == null) {
            return null;
        }

        properties = "\nName: " + file.getName();
        properties += "\nPath: " + file.getPath();
        properties += "\nSize: " + disk.getSectors().get(file.getStart()).getAllContent().length() + " bytes";
        properties += "\nCreated: " + file.getCreated();
        return properties;
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

    // Funtion what search a files in the disk
    public ArrayList<File> searchFile(String name) {
        ArrayList<File> files = new ArrayList<>();
        for (Node node : root.getChildren()) {
            if (node instanceof File) {
                File file = (File) node;
                if (file.getName().contains(name)) {
                    files.add(file);
                }
            }
        }
        return files;
    }

    // Funtion what search a directory in the disk
    public ArrayList<Directory> searchDirectory(String name) {
        ArrayList<Directory> directories = new ArrayList<>();
        for (Node node : root.getChildren()) {
            if (node instanceof Directory) {
                Directory directory = (Directory) node;
                if (directory.getName().contains(name)) {
                    directories.add(directory);
                }
            }
        }
        return directories;
    }
}
