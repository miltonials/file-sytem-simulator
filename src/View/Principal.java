/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package View;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultTreeModel;

import Controller.FileSystem;
import Model.Directory;
import Model.FileImplementation;
import Model.Node;

/**
 *
 * @author kenda
 */
public class Principal extends javax.swing.JFrame {
    private FileSystem fileSystem;
    private DefaultTreeModel treeModel;
    private boolean isSearching = false;
    private ArrayList<FileImplementation> files;
    private ArrayList<Directory> directories;

    private int getValidNumber(String message) {
        int number = -1;
        do {
            try {
                String input = JOptionPane.showInputDialog(this, message);
                number = Integer.parseInt(input);
                if (number <= 0) {
                    JOptionPane.showMessageDialog(this, "Debe ingresar un valor mayor a 0.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Debe ingresar un número entero mayor a 0.");
            }
        } while (number <= 0);
        return number;
    }

    /**
     * Creates new form Principal
     */
    public Principal() {
        initComponents();
        // Uso del método getValidNumber para obtener los valores
        int sectors = getValidNumber("Escriba la cantidad de sectores del disco:");
        int size = getValidNumber("Escriba el tamaño de los sectores del disco:");

        fileSystem = new FileSystem(sectors, size);
        
        fileSystem.addPropertyChangeListener((PropertyChangeEvent evt) -> {
            if ("current".equals(evt.getPropertyName())) {
                Directory currentDirectory = (Directory) evt.getNewValue();
                pathLbl.setText(currentDirectory.getPath());
                updateFilesTable(currentDirectory);
                treeModel.reload();
            }
        });

        
        this.treeModel = new DefaultTreeModel(fileSystem.getRoot());
        tree.setModel(treeModel);


        pathLbl.setText(fileSystem.getCurrent().getPath());

        //Habilitar la seleccion de multiples en la tabla
        filesTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            int row = tree.getRowForLocation(e.getX(), e.getY());
            if (row != -1) {
                tree.setSelectionRow(row);
                Node selectedNode = (Node) tree.getLastSelectedPathComponent();
                String nodeName = selectedNode.getName();
                int[] selectedRows = filesTable.getSelectedRows();
                if (selectedNode instanceof FileImplementation) {
                // Perform operations with selected file
                //Aquí se maneja cuando se presiona un archivo y arriba cuando se preiona un directorio
                if (e.getButton() == MouseEvent.BUTTON1) {// Left click
                    if(isSearching){
                        // se busca en el array de archivos
                        for (FileImplementation file : files) {
                            if(selectedNode.getName().equals(file.getName())){
                                JOptionPane.showMessageDialog(null, "Contenido: " + fileSystem.readFileTree(file));
                            }
                        }
                    }
                    else{   
                        JOptionPane.showMessageDialog(null, "Contenido: " + fileSystem.readFileTree(((FileImplementation) selectedNode)));
                        // fileSystem.openFile(nodeName);
                    }
                } else if (e.getButton() == MouseEvent.BUTTON3) { // Right click
                    if (row >= 0) {
                    
                    int result = JOptionPane.showOptionDialog(null,
                        "Seleccione una opción",
                        "Opciones",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new Object[]{"Copiar", "Eliminar", "Renombrar", "Mover", "Modificar", "Propiedades"},
                        "Eliminar");

                    if (result == 0) {
                        //copiar 1. ruta virtual a virtual, 2. ruta virtual a real, 3. ruta real a virtual
                        int copyOption = JOptionPane.showOptionDialog(null,
                            "Seleccione una opción",
                            "Opciones",
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            new Object[]{"Ruta virtual a virtual", "Ruta virtual a real"},
                            "Ruta virtual a virtual");

                        if (copyOption == 0) {
                            String newDirectoryName = JOptionPane.showInputDialog(null, "Escriba el directorio al que desea copiar el archivo(root/dir):","Directorio", JOptionPane.QUESTION_MESSAGE);
                            if (newDirectoryName != null && !newDirectoryName.trim().isEmpty()) {
                                if (fileSystem.directoryExistsRoot(newDirectoryName)) {
                                    for(int i = 0; i < selectedRows.length; i++){
                                        if(filesTable.getValueAt(selectedRows[i], 1).toString().equals("File")){
                                            fileSystem.copyFile(filesTable.getValueAt(selectedRows[i], 0).toString(), newDirectoryName);
                                        }
                                        else{
                                            fileSystem.copyDirectory(filesTable.getValueAt(selectedRows[i], 0).toString(), newDirectoryName);
                                        }
                                    }
                                    if(selectedRows.length == 0){
                                        fileSystem.copyFile(nodeName, newDirectoryName);
                                    }
                                    updateFilesTable(fileSystem.getCurrent());
                                    treeModel.reload();
                                }
                                else {
                                    JOptionPane.showMessageDialog(null,"El directorio no existe.","Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                            else {
                                JOptionPane.showMessageDialog(null, "El nombre del directorio no puede estar vacío.","Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                        else if (copyOption == 1) {
                            System.out.println("Copiando archivo a una ruta real");
                            //abrir ventana que permita seleccionar una ruta al usuario
                            //new javax.swing.JFileChooser().showOpenDialog(null);
                            javax.swing.JFileChooser path = new javax.swing.JFileChooser();
                            path.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
                            path.showOpenDialog(null);

                            try {
                                String copyPath = path.getSelectedFile().getAbsolutePath();
                                fileSystem.copyFileToRealPath(nodeName, copyPath);
                                System.out.println("Path: " + copyPath);
                            } catch (NullPointerException e1) {
                                System.out.println("No se seleccionó ninguna ruta.");
                            }
                        }
                    }
                    else
                    if (result == 1) {
                        // fileSystem.removeFile(nodeName);
                        // updateFilesTable(fileSystem.getCurrent());
                        // borrar archivos seleccionados
                        for (int i = 0; i < selectedRows.length; i++) {
                            // validar que sea un archivo para borrarlo
                            if(filesTable.getValueAt(selectedRows[i], 1).toString().equals("File")){
                                fileSystem.removeFile(filesTable.getValueAt(selectedRows[i], 0).toString());
                            }
                            else{
                                fileSystem.removeDirectory(filesTable.getValueAt(selectedRows[i], 0).toString());
                            }
                        }
                        if(selectedRows.length == 0){
                            fileSystem.removeFile(nodeName);
                        }
                        updateFilesTable(fileSystem.getCurrent());
                        treeModel.reload();
                    }
                    else if(result == 3){
                        //mover archivos
                        String newDirectoryName = JOptionPane.showInputDialog(null, "Escriba el directorio al que desea mover los archivos(root/dir):","Directorio", JOptionPane.QUESTION_MESSAGE);
                        if (newDirectoryName != null && !newDirectoryName.trim().isEmpty()) {
                            if (fileSystem.directoryExists(newDirectoryName)) {
                                for(int i = 0; i < selectedRows.length; i++){
                                    if(filesTable.getValueAt(selectedRows[i], 1).toString().equals("File")){
                                        fileSystem.moveFile(filesTable.getValueAt(selectedRows[i], 0).toString(), newDirectoryName);
                                    }
                                    else{
                                        fileSystem.moveDirectory(filesTable.getValueAt(selectedRows[i], 0).toString(), newDirectoryName);
                                    }
                                }
                                if(selectedRows.length == 0){
                                    fileSystem.moveFile(nodeName, newDirectoryName);
                                }
                            }
                            else {
                                JOptionPane.showMessageDialog(null, this, "El directorio no existe.", result);
                            }
                        }
                        else {
                            JOptionPane.showMessageDialog(null, "El nombre del directorio no puede estar vacío.","Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    else if (result == 4) {
                        String fileContent = fileSystem.readFileTree((FileImplementation) selectedNode);
                        String newContent = JOptionPane.showInputDialog(null, "Escriba el nuevo contenido del archivo:", fileContent);
                        if(newContent != null && !newContent.trim().isEmpty()){
                            fileSystem.modifyFile(nodeName, newContent);
                        }
                        // fileSystem.modifyFile(nodeName, newContent);
                    }
                    else if (result == 5) {
                        JOptionPane.showMessageDialog(null, "Propiedades del archivo: " + fileSystem.getFileProperties(nodeName));
                    }
                    //si selecciona varios archivos, se puede mover o eliminar varios


                    }
                    // fileSystem.removeFile(nodeName);
                }

                }
            }
            }
        });
        // Add MouseListener to tree to handle node clicks
        filesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = filesTable.rowAtPoint(e.getPoint());
                int[] selectedRows = filesTable.getSelectedRows();
                System.out.println("Selected rows: " + Arrays.toString(selectedRows));
                // mostrar el tipo del archivo o directorio seleccionado
                System.out.println(filesTable.getValueAt(row, 1));
                // imprimir el nombre del archivo o directorio seleccionados
                for (int i = 0; i < selectedRows.length; i++) {
                    System.out.println(filesTable.getValueAt(selectedRows[i], 0));
                }
                if ("Directory".equals(filesTable.getValueAt(row, 1).toString())){
                    //Aquí se maneja cuando se presiona un directorio con click izquierdo o derecho
                    if(e.getButton() == MouseEvent.BUTTON1){// Left click
                        // if (row >= 0) {
                        if(selectedRows.length >= 1){
                            if(isSearching){
                                // se mueve a la ruta del directorio
                                for (Directory directory : directories) {
                                    fileSystem.setCurrent(directory);
                                    updateFilesTable(directory);
                                    treeModel.reload();
                                }
                            }
                            else{
                                String nodeName = filesTable.getValueAt(selectedRows[0], 0).toString();
                                fileSystem.changeDirectory(nodeName);   
                            
                            }

                        }
                    }
                    else if (e.getButton() == MouseEvent.BUTTON3) { // Right click
                        System.out.println("Right click");
                        // if (row >= 0) {
                        // String nodeName = filesTable.getValueAt(row, 0).toString();
                        String nodeName = filesTable.getValueAt(row, 0).toString();
                        int result = JOptionPane.showOptionDialog(null,
                                "Seleccione una opción",
                                "Opciones",
                                JOptionPane.YES_NO_CANCEL_OPTION,
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                // new Object[]{"Copiar", "Eliminar", "Renombrar", "Mover" , "Modificar", "Propiedades"},
                                new Object[]{"Copiar", "Eliminar", "Renombrar", "Mover" , "Propiedades"},
                                "Eliminar");

                        if (result == 0) {
                            //copiar 1. ruta virtual a virtual, 2. ruta virtual a real, 3. ruta real a virtual
                            int copyOption = JOptionPane.showOptionDialog(null,
                                            "Seleccione una opción",
                                            "Opciones",
                                            JOptionPane.YES_NO_CANCEL_OPTION,
                                            JOptionPane.QUESTION_MESSAGE,
                                            null,
                                            new Object[]{"Ruta virtual a virtual", "Ruta virtual a real"},
                                            "Ruta virtual a virtual");

                            if (copyOption == 0) {
                                String newDirectoryName = JOptionPane.showInputDialog(null, "Escriba el directorio al que desea copiar el directorio(root/dir/):","Directorio", JOptionPane.QUESTION_MESSAGE);
                                if (newDirectoryName != null && !newDirectoryName.trim().isEmpty()) {
                                    if (fileSystem.directoryExistsRoot(newDirectoryName)) {
                                        System.out.println("Copiando directorio a una ruta virtual");
                                        for(int i = 0; i < selectedRows.length; i++){
                                            if(filesTable.getValueAt(selectedRows[i], 1).toString().equals("Directory")){
                                                fileSystem.copyDirectory(filesTable.getValueAt(selectedRows[i], 0).toString(), newDirectoryName);
                                            }
                                            else{
                                                fileSystem.copyFile(filesTable.getValueAt(selectedRows[i], 0).toString(), newDirectoryName);
                                            }
                                        }
                                        if (selectedRows.length == 0) {
                                            fileSystem.copyDirectory(nodeName, newDirectoryName);
                                        }
                                        updateFilesTable(fileSystem.getCurrent());
                                        treeModel.reload();
                                    }
                                    else {
                                        JOptionPane.showMessageDialog(null,"El directorio no existe.","Error", JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                                else {
                                    JOptionPane.showMessageDialog(null, "El nombre del directorio no puede estar vacío.","Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                            else if (copyOption == 1) {
                                System.out.println("Copiando directorio a una ruta real");
                                //abrir ventana que permita seleccionar una ruta al usuario
                                //new javax.swing.JFileChooser().showOpenDialog(null);
                                javax.swing.JFileChooser path = new javax.swing.JFileChooser();
                                path.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
                                path.showOpenDialog(null);

                                String copyPath = path.getSelectedFile().getAbsolutePath();
                                fileSystem.copyDirectoryToRealPath(nodeName, copyPath);
                                System.out.println("Path: " + copyPath);
                            }
                            else if (copyOption == 2) {
                                System.out.println("Copiando directorio a una ruta virtual");
                            }
                        }
                        else if (result == 1) {
                            // fileSystem.removeDirectory(nodeName);
                            // updateFilesTable(fileSystem.getCurrent());
                            // borrar directorios seleccionados
                            for (int i = 0; i < selectedRows.length; i++) {
                                //validar que sea un directorio para borrarlo
                                if(filesTable.getValueAt(selectedRows[i], 1).toString().equals("Directory")){
                                    fileSystem.removeDirectory(filesTable.getValueAt(selectedRows[i], 0).toString());
                                }
                                else{
                                    fileSystem.removeFile(filesTable.getValueAt(selectedRows[i], 0).toString());
                                }
                            }
                            if(selectedRows.length == 0){
                                fileSystem.removeDirectory(nodeName);
                                }
                            updateFilesTable(fileSystem.getCurrent());
                            treeModel.reload();
                        }
                        if(result == 2){
                            //renombrar directorio
                            System.out.println("caralolesssssssssss");
                            String newDirectoryName = JOptionPane.showInputDialog(null, "Escriba el nuevo nombre del directorio:", nodeName);
                            if (newDirectoryName != null && !newDirectoryName.trim().isEmpty()) {
                                // if (fileSystem.directoryExistsRoot(newDirectoryName)) {
                                if (fileSystem.directoryExists(newDirectoryName)) {
                                    int optio = JOptionPane.showConfirmDialog(null,
                                            "El directorio ya existe. ¿Desea sobreescribirlo?",
                                            "Confirmar sobreescritura.",
                                            JOptionPane.YES_NO_OPTION);
                                    if (optio == JOptionPane.YES_OPTION) {
                                        fileSystem.removeDirectory(newDirectoryName);
                                        fileSystem.renameDirectory(nodeName, newDirectoryName);
                                    }
                                } else {
                                    fileSystem.renameDirectory(nodeName, newDirectoryName);
                                }
                                updateFilesTable(fileSystem.getCurrent());
                                treeModel.reload();
                            }
                            else {
                                JOptionPane.showMessageDialog(null, "El nombre del directorio no puede estar vacío.","Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                        else if(result == 3){
                            //mover directorios
                            String newDirectoryName = JOptionPane.showInputDialog(null, "Escriba el directorio al que desea mover los archivos(root/dir):","Directorio", JOptionPane.QUESTION_MESSAGE);
                            if (newDirectoryName != null && !newDirectoryName.trim().isEmpty()) {
                                if (fileSystem.directoryExistsRoot(newDirectoryName)) {
                                    for(int i = 0; i < selectedRows.length; i++){
                                        if(filesTable.getValueAt(selectedRows[i], 1).toString().equals("Directory")){
                                            fileSystem.moveDirectory(filesTable.getValueAt(selectedRows[i], 0).toString(), newDirectoryName);
                                        }
                                        else{
                                            fileSystem.moveFile(filesTable.getValueAt(selectedRows[i], 0).toString(), newDirectoryName);
                                        }
                                    }
                                    if(selectedRows.length == 0){
                                        fileSystem.moveDirectory(nodeName, newDirectoryName);
                                    }
                                    updateFilesTable(fileSystem.getCurrent());
                                    treeModel.reload();

                                }
                                else {
                                    JOptionPane.showMessageDialog(null,"El directorio no existe.","Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                            else {
                                JOptionPane.showMessageDialog(null, "El nombre del directorio no puede estar vacío.","Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                        // else if (result == 4) {
                        //     String fileContent = fileSystem.readFile(nodeName);
                        //     String newContent = JOptionPane.showInputDialog(null, "Escriba el nuevo contenido del archivo:", fileContent);
                        //     fileSystem.modifyFile(nodeName, newContent);
                        // }
                        else if (result == 4) {
                            JOptionPane.showMessageDialog(null, "Propiedades del directorio: "+ fileSystem.getDirectoryProperties(nodeName));
                        }
                    
                    }
                }
                else{
                    //Aquí se maneja cuando se presiona un archivo y arriba cuando se preciona un directorio
                    if (e.getButton() == MouseEvent.BUTTON1) {// Left click
                        // Open file
                        // if (row >= 0) {
                            // String nodeName = filesTable.getValueAt(row, 0).toString();
                        if(selectedRows.length >= 1){
                            if(isSearching){
                                // se busca en el array de archivos
                                for (FileImplementation file : files) {
                                    JOptionPane.showMessageDialog(null, "Contenido: " + fileSystem.readFile(file));
                                }
                            }
                            else{
                                String nodeName = filesTable.getValueAt(selectedRows[0], 0).toString();
                                JOptionPane.showMessageDialog(null, "Contenido: " + fileSystem.readFile(nodeName));
                            }
                        }
                        // fileSystem.openFile(nodeName);
                    } else if (e.getButton() == MouseEvent.BUTTON3) { // Right click
                        if (row >= 0) {
                            String nodeName = filesTable.getValueAt(row, 0).toString();
                            System.out.println("nodeName: " + nodeName);
                            int result = JOptionPane.showOptionDialog(null,
                                    "Seleccione una opción",
                                    "Opciones",
                                    JOptionPane.YES_NO_CANCEL_OPTION,
                                    JOptionPane.QUESTION_MESSAGE,
                                    null,
                                    new Object[]{"Copiar", "Eliminar", "Renombrar", "Mover", "Modificar", "Propiedades"},
                                    "Eliminar");
                            if (result == 0) {
                                //copiar 1. ruta virtual a virtual, 2. ruta virtual a real, 3. ruta real a virtual
                                int copyOption = JOptionPane.showOptionDialog(null,
                                    "Seleccione una opción",
                                    "Opciones",
                                    JOptionPane.YES_NO_CANCEL_OPTION,
                                    JOptionPane.QUESTION_MESSAGE,
                                    null,
                                    new Object[]{"Ruta virtual a virtual", "Ruta virtual a real"},
                                    "Ruta virtual a virtual");

                                if (copyOption == 0) {
                                    String newDirectoryName = JOptionPane.showInputDialog(null, "Escriba el directorio al que desea copiar el archivo(root/dir):","Directorio", JOptionPane.QUESTION_MESSAGE);
                                    if (newDirectoryName != null && !newDirectoryName.trim().isEmpty()) {
                                        if (fileSystem.directoryExistsRoot(newDirectoryName)) {
                                            for(int i = 0; i < selectedRows.length; i++){
                                                if(filesTable.getValueAt(selectedRows[i], 1).toString().equals("File")){
                                                    fileSystem.copyFile(filesTable.getValueAt(selectedRows[i], 0).toString(), newDirectoryName);
                                                }
                                                else{
                                                    fileSystem.copyDirectory(filesTable.getValueAt(selectedRows[i], 0).toString(), newDirectoryName);
                                                }
                                            }
                                            if(selectedRows.length == 0){
                                                fileSystem.copyFile(nodeName, newDirectoryName);
                                            }
                                            updateFilesTable(fileSystem.getCurrent());
                                            treeModel.reload();
                                        }
                                        else {
                                            JOptionPane.showMessageDialog(null,"El directorio no existe.","Error", JOptionPane.ERROR_MESSAGE);
                                        }
                                    }
                                    else {
                                        JOptionPane.showMessageDialog(null, "El nombre del directorio no puede estar vacío.","Error", JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                                else if (copyOption == 1) {
                                    System.out.println("Copiando archivo a una ruta real");
                                    //abrir ventana que permita seleccionar una ruta al usuario
                                    //new javax.swing.JFileChooser().showOpenDialog(null);
                                    javax.swing.JFileChooser path = new javax.swing.JFileChooser();
                                    path.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
                                    path.showOpenDialog(null);

                                    try {
                                        String copyPath = path.getSelectedFile().getAbsolutePath();
                                        fileSystem.copyFileToRealPath(nodeName, copyPath);
                                        JOptionPane.showMessageDialog(null, "Archivo copiado a la ruta: " + copyPath);
                                    } catch (NullPointerException e1) {
                                        System.out.println("No se seleccionó ninguna ruta.");
                                    }
                                }

                                
                            }
                            else if (result == 1) {
                                // fileSystem.removeFile(nodeName);
                                // updateFilesTable(fileSystem.getCurrent());
                                // borrar archivos seleccionados
                                for (int i = 0; i < selectedRows.length; i++) {
                                    // validar que sea un archivo para borrarlo
                                    if(filesTable.getValueAt(selectedRows[i], 1).toString().equals("File")){
                                        fileSystem.removeFile(filesTable.getValueAt(selectedRows[i], 0).toString());
                                    }
                                    else{
                                        fileSystem.removeDirectory(filesTable.getValueAt(selectedRows[i], 0).toString());
                                    }
                                }
                                if(selectedRows.length == 0){
                                    fileSystem.removeFile(nodeName);
                                }
                                updateFilesTable(fileSystem.getCurrent());
                                treeModel.reload();
                            }
                            else if(result == 2){
                                //renombrar archivo
                                String newDirectoryName = JOptionPane.showInputDialog(null, "Escriba el nuevo nombre del archivo:", nodeName);
                                if (newDirectoryName != null && !newDirectoryName.trim().isEmpty()) {
                                    if (fileSystem.fileExists(newDirectoryName)) {
                                        int optio = JOptionPane.showConfirmDialog(null,
                                                "El archivo ya existe. ¿Desea sobreescribirlo?",
                                                "Confirmar sobreescritura.",
                                                JOptionPane.YES_NO_OPTION);
                                        if (optio == JOptionPane.YES_OPTION) {
                                            fileSystem.removeFile(newDirectoryName);
                                            fileSystem.renameFile(nodeName, newDirectoryName);
                                        }
                                    } else {
                                        fileSystem.renameFile(nodeName, newDirectoryName);
                                    }
                                    updateFilesTable(fileSystem.getCurrent());
                                    treeModel.reload();
                                }
                                else if (newDirectoryName != null) {
                                    JOptionPane.showMessageDialog(null, "El nombre del archivo no puede estar vacío.","Error", JOptionPane.ERROR_MESSAGE);
                                }
                                // else {
                                // }
                            }
                            else if(result == 3){
                                //mover archivos
                                String newDirectoryName = JOptionPane.showInputDialog(null, "Escriba el directorio al que desea mover los archivos(root/dir):","Directorio", JOptionPane.QUESTION_MESSAGE);
                                if (newDirectoryName != null && !newDirectoryName.trim().isEmpty()) {
                                    if (fileSystem.directoryExistsRoot(newDirectoryName)) {
                                        for(int i = 0; i < selectedRows.length; i++){
                                            if(filesTable.getValueAt(selectedRows[i], 1).toString().equals("File")){
                                                fileSystem.moveFile(filesTable.getValueAt(selectedRows[i], 0).toString(), newDirectoryName);
                                            }
                                            else{
                                                fileSystem.moveDirectory(filesTable.getValueAt(selectedRows[i], 0).toString(), newDirectoryName);
                                            }
                                        }
                                        if (selectedRows.length == 0) {
                                            fileSystem.moveFile(nodeName, newDirectoryName);
                                        }
                                        updateFilesTable(fileSystem.getCurrent());
                                        treeModel.reload();

                                    }
                                    else {
                                        JOptionPane.showMessageDialog(null,"El directorio no existe.","Error", JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                                else {
                                    JOptionPane.showMessageDialog(null, "El nombre del directorio no puede estar vacío.","Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                            else if (result == 4) {
                                String fileContent = fileSystem.readFile(nodeName);
                                String newContent = JOptionPane.showInputDialog(null, "Escriba el nuevo contenido del archivo:", fileContent);
                                fileSystem.modifyFile(nodeName, newContent);
                            }
                            else if (result == 5) {
                                JOptionPane.showMessageDialog(null, "Propiedades del archivo: " + fileSystem.getFileProperties(nodeName));
                            }
                            //si selecciona varios archivos, se puede mover o eliminar varios


                        }
                        // fileSystem.removeFile(nodeName);
                    }
                }
            }
        });

        updateFilesTable(fileSystem.getCurrent());
        treeModel.reload();
    }
    
    private void updateFilesTable(Directory directory) {
        String[] columnNames = {"Name", "Type"};
        Object[][] data = Arrays.stream(directory.getChildren()) // Convert array to stream
                .map(node -> new Object[]{node.getName(), node instanceof Directory ? "Directory" : "File"})
                .toArray(Object[][]::new);
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        filesTable.setModel(model);
        fileSystem.getDisk().diskToFile();
    }

    private void backToFatherDirectoryBtnActionPerformed(java.awt.event.ActionEvent evt) {
        fileSystem.changeDirectory("..");
    }

    private void createDirectoryBtnActionPerformed(java.awt.event.ActionEvent evt) {
        // CreateDirectoryDialog dialog = new CreateDirectoryDialog(this,true);
        // dialog.setVisible(true);
        // String newDirectoryName = dialog.getDirectoryName();
        String newDirectoryName = JOptionPane.showInputDialog(this, "Escriba el nombre del directorio:");
        if (newDirectoryName != null && !newDirectoryName.trim().isEmpty()) {
            if (fileSystem.directoryExists(newDirectoryName)) {
                int result = JOptionPane.showConfirmDialog(this,
                        "El directorio ya existe. ¿Desea sobreescribirlo?",
                        "Confirmar sobreescritura.",
                        JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    fileSystem.removeDirectory(newDirectoryName);
                    fileSystem.createDirectory(newDirectoryName);
                    
                }
            } else {
                //joption pane de texto
                fileSystem.createDirectory(newDirectoryName);
                
            }
            
            updateFilesTable(fileSystem.getCurrent());
            treeModel.reload();
        }
        else {
            JOptionPane.showMessageDialog(this, "El nombre del directorio no puede estar vacío.");
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {

        toolsPanel = new javax.swing.JPanel();
        backToFatherDirectoryBtn = new javax.swing.JButton();
        pathLbl = new javax.swing.JLabel();
        searchTextBox = new javax.swing.JTextField();
        search = new javax.swing.JButton();
        lowPanel = new javax.swing.JPanel();
        buttonsPanel = new javax.swing.JPanel();
        createDirectoryBtn = new javax.swing.JButton();
        createFileBtn = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tree = new javax.swing.JTree();
        copyFileRealToVirtual = new javax.swing.JButton();
        filesPanel = new javax.swing.JScrollPane();
        filesTable = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        backToFatherDirectoryBtn.setText("<");
        backToFatherDirectoryBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backToFatherDirectoryBtnActionPerformed(evt);
            }
        });

        search.setText("Buscar");
        search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout toolsPanelLayout = new javax.swing.GroupLayout(toolsPanel);
        toolsPanel.setLayout(toolsPanelLayout);
        toolsPanelLayout.setHorizontalGroup(
            toolsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(toolsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(backToFatherDirectoryBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pathLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 403, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(search)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        toolsPanelLayout.setVerticalGroup(
            toolsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(toolsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(toolsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(backToFatherDirectoryBtn)
                    .addComponent(pathLbl)
                    .addComponent(searchTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(search))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        createDirectoryBtn.setText("Crear un directorio");
        createDirectoryBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createDirectoryBtnActionPerformed(evt);
            }
        });

        createFileBtn.setText("Nuevo archivo");
        createFileBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createFileBtnActionPerformed(evt);
            }
        });

        jScrollPane1.setViewportView(tree);

        copyFileRealToVirtual.setText("Copiar desde PC");
        copyFileRealToVirtual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyFileRealToVirtualActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout buttonsPanelLayout = new javax.swing.GroupLayout(buttonsPanel);
        buttonsPanel.setLayout(buttonsPanelLayout);
        buttonsPanelLayout.setHorizontalGroup(
            buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, buttonsPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(copyFileRealToVirtual, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(createDirectoryBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(createFileBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        buttonsPanelLayout.setVerticalGroup(
            buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(createDirectoryBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(createFileBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(copyFileRealToVirtual)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 49, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        filesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Nombre", "Tamaño", "Extensión"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        filesPanel.setViewportView(filesTable);

        javax.swing.GroupLayout lowPanelLayout = new javax.swing.GroupLayout(lowPanel);
        lowPanel.setLayout(lowPanelLayout);
        lowPanelLayout.setHorizontalGroup(
            lowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, lowPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(buttonsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(filesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 563, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        lowPanelLayout.setVerticalGroup(
            lowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lowPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(lowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(lowPanelLayout.createSequentialGroup()
                        .addComponent(buttonsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(filesPanel)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(toolsPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lowPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(toolsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lowPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>                        

    // function to search files and directories
    private void searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchActionPerformed
        String search = searchTextBox.getText();
        System.out.println("Searching for: " + search);
        // search for the file or directory
        files = fileSystem.searchFile(search, fileSystem.getCurrent());
        directories = fileSystem.searchDirectory(search, fileSystem.getCurrent());
        // se muestra en la tabla y se habilita la opción de abrir el archivo o directorio
        String[] columnNames = {"Name", "Type"};
        Object[][] data = new Object[files.size() + directories.size()][2];
        for (int i = 0; i < files.size(); i++) {
            data[i][0] = files.get(i).getName();
            data[i][1] = "File";
        }
        for (int i = 0; i < directories.size(); i++) {
            data[files.size() + i][0] = directories.get(i).getName();
            data[files.size() + i][1] = "Directory";
        }
        // si es distinto de vacío se pasa a true
        isSearching = !search.isEmpty();
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        filesTable.setModel(model);

    }//GEN-LAST:event_searchActionPerformed

    private void copyFileRealToVirtualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyFileRealToVirtualActionPerformed
        System.out.println("Copiando archivo a una ruta virtual");
       //abrir ventana que permita seleccionar una ruta al usuario
       //new javax.swing.JFileChooser().showOpenDialog(null);
       javax.swing.JFileChooser path = new javax.swing.JFileChooser();
       path.setFileSelectionMode(javax.swing.JFileChooser.FILES_AND_DIRECTORIES);
       path.showOpenDialog(null);

       try {
           String copyPath = path.getSelectedFile().getAbsolutePath();
           fileSystem.copyRealDirectoryToVirtualPath(path.getSelectedFile(), fileSystem.getCurrent());
           System.out.println("Path: " + copyPath);
           updateFilesTable(fileSystem.getCurrent());
           treeModel.reload();
       } catch (NullPointerException e) {
           System.out.println("No se seleccionó ninguna ruta.");
         }

    }//GEN-LAST:event_copyFileRealToVirtualActionPerformed

    private void createFileBtnActionPerformed(java.awt.event.ActionEvent evt) {//
        //input dialog para el nombre del archivo
        String newFileName = JOptionPane.showInputDialog(this, "Escriba el nombre del archivo:");
        String content = JOptionPane.showInputDialog(this, "Escriba el contenido del archivo:");
        if (newFileName != null && !newFileName.trim().isEmpty()) {
            if (fileSystem.fileExists(newFileName)) {
                int result = JOptionPane.showConfirmDialog(this,
                        "El archivo ya existe. ¿Desea sobreescribirlo?",
                        "Confirmar sobreescritura.",
                        JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    fileSystem.removeFile(newFileName);
                    fileSystem.createFile(newFileName, content);
                }
            } else {
                fileSystem.createFile(newFileName, content);
            }
            
            updateFilesTable(fileSystem.getCurrent());
            treeModel.reload();
        }
        else {
            JOptionPane.showMessageDialog(this, "El nombre del archivo no puede estar vacío.");
        }
    }                                             

    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Principal().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backToFatherDirectoryBtn;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JButton copyFileRealToVirtual;
    private javax.swing.JButton createDirectoryBtn;
    private javax.swing.JButton createFileBtn;
    private javax.swing.JScrollPane filesPanel;
    private javax.swing.JTable filesTable;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel lowPanel;
    private javax.swing.JLabel pathLbl;
    private javax.swing.JButton search;
    private javax.swing.JTextField searchTextBox;
    private javax.swing.JPanel toolsPanel;
    private javax.swing.JTree tree;
    // End of variables declaration//GEN-END:variables
}
