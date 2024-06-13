package Controller;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;

import Model.Directory;
import Model.Disk;
import Model.FileImplementation;
import Model.Node;
import utils.FilesManagement;

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
        removeFile("primerArchivo");
        System.out.println(disk.toString());
    }

    public Directory getRoot() {
        return root;
    }

    public Directory getCurrent() {
        return current;
    }

    private Node findNode(String name) {
        Node node = null;
        for (Node n : current.getChildren()) {
            if (n.getName().equals(name)) {
                node = n;
                break;
            }
        }
        return node;
    }

    private FileImplementation findFile(String name) {
        Node node = findNode(name);

        if (node == null || !(node instanceof FileImplementation)) {
            return null;
        }

        return (FileImplementation) node;
    }

    private Directory findDirectory(String name) {
        Node node = findNode(name);

        if (node == null || !(node instanceof Directory)) {
            return null;
        }

        return (Directory) node;
    }

    public void setCurrent(Directory current) {
        Directory oldCurrent = this.current;
        this.current = current;
        support.firePropertyChange("current", oldCurrent, current);
    }

    public void setCurrent(String path) {
        Directory directory = getDirectory(path);
        if (directory != null) {
            setCurrent(directory);
        }
    }

    public void changeDirectory(String name) {
        if (name.equals("..")) {
            if (current != root) {
                setCurrent(current.getParent());
            }
        } else {
            Directory dir = findDirectory(name);
            if (dir != null) {
                setCurrent(dir);
            } else {
                System.out.println("Directory not found");
            }
        }
    }

    public void createDirectory(String name) {
        current.addChild(new Directory(name, current.getPath() + name + "/", current));
    }
    
    public boolean directoryExists(String name) {
        Directory directory = findDirectory(name);

        if (directory != null) {
            return true;
        }

        return false;
    }
    
    public void removeDirectory(String name) {
        current.removeChild(name);
    }
    
    public boolean createFile(String name, String content) {
        int startSector = disk.newFile(name, content);
        if (fileExists(name)) {
            System.out.println("FileImplementation already exists");
            return false;
        }
        if (startSector == -1) {
            System.out.println("Not enough space");
            return false;
        }

        current.addChild(new FileImplementation(name, current.getPath() + name + "/", current, startSector));
        System.out.println(disk.toString());
        return true;
    }

    public boolean fileExists(String name) {
        FileImplementation file = findFile(name);
        if (file != null) {
            return true;
        }
        return false;
    }

    // lee el archivo que se le pasa por parametro
    public String readFile(FileImplementation file) {
        //mover el archivo a la raiz, sacar el contenido y devolverlo a su lugar
        String content = disk.readFile(file.getStart());
        return content;    
    }

    public String readFile(String name) {
        FileImplementation file = findFile(name);

        if (file == null) {
            return null;
        }

        return disk.readFile(file.getStart());
    }
    public String readFileTree(FileImplementation file){
        return disk.readFile(file.getStart());
    }

    public void modifyFile(String name, String content) {
        FileImplementation file = findFile(name);
        if (file == null) {
            return;
        }
        disk.modifyFile(file.getStart(), content);
        // file.setStart(disk.newFile(name, content));
        System.out.println(disk.toString());
    }

    public void removeFile(String name) {
        System.out.println("Removing file: " + name);
        FileImplementation file = findFile(name);
        if (file == null) {
            return;
        }
        
        disk.deleteFile(file.getStart());
        current.removeChild(name);
        System.out.println(disk.toString());
    }

    public String getFileProperties(String name) {
        FileImplementation file = findFile(name);
        String properties;

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

    // funcion que busca un archivo en el disco, si lo encuentra lo retorna, debe buscar en todos los directorios del disco
    public ArrayList<FileImplementation> searchFile(String name, Directory uDirectory) {
        ArrayList<FileImplementation> files = new ArrayList<>();
        // si el name esta vacio retorna los archivos y directorios del directorio actual
        if (name.equals("")) {
            for (Node node : uDirectory.getChildren()) {
                if (node instanceof FileImplementation) {
                    files.add((FileImplementation) node);
                }
            }
            return files;
        }
        for (Node node : uDirectory.getChildren()) {
            if (node instanceof FileImplementation) {
                FileImplementation file = (FileImplementation) node;
                if (file.getName().contains(name)) {
                    files.add(file);
                }
            }
            if (node instanceof Directory) {
                files.addAll(((Directory) node).searchFile(name));
            }
        }
        return files;
    }

    // funcion que busca un directorio en el disco, si lo encuentra lo retorna, debe buscar en todos los directorios del disco
    public ArrayList<Directory> searchDirectory(String name, Directory uDirectory) {
        ArrayList<Directory> directories = new ArrayList<>();
        if (name.equals("")) {
            for (Node node : uDirectory.getChildren()) {
                if (node instanceof Directory) {
                    directories.add((Directory) node);
                }
            }
            return directories;
        }
        for (Node node : uDirectory.getChildren()) {
            if (node instanceof Directory) {
                Directory directory = (Directory) node;
                if (directory.getName().contains(name)) {
                    directories.add(directory);
                }
                directories.addAll(((Directory) node).searchDirectory(name));
            }
        }
        return directories;
    }

    // mover un archivo de un directorio a otro
    public void moveFile(String name, String path) {
        // recorre todo el arbol hasta llegar a la ruta donde se desea mover el archivo
        Directory directory = getDirectory(path);
        if (directory == null) {
            return;
        }
        FileImplementation file = findFile(name);
        if (file == null) {
            return;
        }
        // se elimina el archivo del directorio actual
        current.removeChild(name);
        // se agrega el archivo al nuevo directorio
        file.setParent(directory);
        file.setPath(directory.getPath() + name + "/");
        directory.addChild(file);

    }

    // mover un directorio de un directorio a otro
    public void moveDirectory(String name, String path) {
        Directory directory = findDirectory(name);
        if (directory == null) {
            return;
        }
        Directory newDirectory = findDirectory(path);
        if (newDirectory == null) {
            return;
        }
        directory.setParent(newDirectory);
        directory.setPath(newDirectory.getPath() + name + "/");
        newDirectory.addChild(directory);
        current.removeChild(name);
    }
    // recorre todo el arbol de directorios y archivos para validar si existe un directorio con el path que se le pasa
    public boolean directoryExistsRoot(String path) {
        System.out.println("path: " + path);
        if(root.getPath().equals(path)){
            return true;
        }
        for (Node node : root.getChildren()) {
            if (node instanceof Directory) {
                Directory directory = (Directory) node;
                if (directory.getPath().equals(path)) {
                    return true;
                }
                if (directory.directoryExists(path, node)) {
                    return true;
                }
            }
        }
        return false;    
       
    }
    // retorna el directorio que tiene el path que se le pasa
    public Directory getDirectory(String path) {
        for (Node node : root.getChildren()) {
            if (node instanceof Directory) {
                Directory directory = (Directory) node;
                if (directory.getPath().equals(path)) {
                    return directory;
                }
                if (directory.directoryExists(path, node)) {
                    return directory.getDirectory(path, node);
                }
            }
        }
        return null;
    }

    public void copyFileToRealPath(String name, String path) {
        System.out.println("Copiando archivo" + name + " a " + path);
        FileImplementation file = findFile(name);
        if (file == null) {
            return;
        }
        
        FilesManagement.crearArchivo(path + "\\" + name);
        FilesManagement.escribirArchivo(path + "\\" + name, disk.readFile(file.getStart()));
    }

    public void copyDirectoryToRealPath(String name, String path) {
        System.out.println("Copiando " + name + " a " + path);
        Directory directory = findDirectory(name);
        if (directory == null) {
            return;
        }
        FilesManagement.newFolder(path + "\\" + name);
        for (Node node : directory.getChildren()) {
            if (node instanceof FileImplementation) {
                FileImplementation file = (FileImplementation) node;
                String filename = file.getName().replace("/", "");
                FilesManagement.crearArchivo(path + "\\" + name + "\\" + filename);
                FilesManagement.escribirArchivo(path + "\\" + name + "\\" + filename, disk.readFile(file.getStart()));
            } else if (node instanceof Directory) {
                Directory dir = (Directory) node;
                String folderName = dir.getName().replace("/", "");
                // FilesManagement.newFolder(path + "\\" + name + "\\" + folderName);
                setCurrent(directory);
                copyDirectoryToRealPath(folderName, path + "\\" + name);
                setCurrent(directory.getParent());
            }
        }
    }
    



    public void copyRealDirectoryToVirtualPath(File generalFile, Directory directory) {
        setCurrent(directory);
        if (!generalFile.isDirectory()) {
            
           createFile(generalFile.getName(), FilesManagement.getContenido(generalFile.getAbsolutePath()));
        }
        else{
            createDirectory(generalFile.getName());
            setCurrent(findDirectory(generalFile.getName()));
            for (File file : generalFile.listFiles()) {
                copyRealDirectoryToVirtualPath(file, getCurrent());
            }
        }
    }

    public void copyDirectory(String name, String newDirectoryName) {
        Directory directory = findDirectory(name);
        if (directory == null) {
            return;
        }
        Directory newDirectory = new Directory(name, current.getPath() + newDirectoryName + "/", current);
        directory.addChild(newDirectory);
        for (Node node : directory.getChildren()) {
            if (node instanceof FileImplementation) {
                FileImplementation file = (FileImplementation) node;
                newDirectory.addChild(new FileImplementation(file.getName(), newDirectory.getPath() + file.getName() + "/", newDirectory, file.getStart()));
                createFile(file.getName(), disk.readFile(file.getStart()));
            } else if (node instanceof Directory) {
                Directory dir = (Directory) node;
                copyDirectory(dir.getName(), newDirectoryName);
            }
        }
        //imprimimos el disco para ver los cambios
        System.out.println(disk.toString());
    }

    public void copyFile(String name, String newDirectoryName) {
        // sacamos el directorio dondde se desea copiar el archivo
        Directory directory = getDirectory(newDirectoryName);
        if (directory == null) {
            return;
        }
        // sacamos el archivo que se desea copiar
        FileImplementation file = findFile(name);
        if (file == null) {
            return;
        }
        // copiamos el archivo al nuevo directorio
        directory.addChild(new FileImplementation(name, directory.getPath() + name + "/", directory, file.getStart()));
        createFile(name, disk.readFile(file.getStart()));
        //imprimimos el disco para ver los cambios
        System.out.println(disk.toString());

    }

    public void renameDirectory(String nodeName, String newDirectoryName) {
        Directory directory = findDirectory(nodeName);
        if (directory == null) {
            return;
        }
        directory.setName(newDirectoryName);
        directory.setPath(directory.getParent().getPath() + newDirectoryName + "/");

    }

    public void renameFile(String nodeName, String newDirectoryName) {
        FileImplementation file = findFile(nodeName);
        if (file == null) {
            return;
        }
        file.setName(newDirectoryName);
        file.setPath(file.getParent().getPath() + newDirectoryName + "/");
    }

}