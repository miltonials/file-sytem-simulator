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

import Controller.FileSystem;
import Model.Directory;
import Model.File;

/**
 *
 * @author kenda
 */
public class Principal extends javax.swing.JFrame {
    private FileSystem fileSystem;
    /**
     * Creates new form Principal
     */
    public Principal() {
        initComponents();
        int sectorsQuantity = Integer.parseInt(JOptionPane.showInputDialog(this, "Escriba a cantidad de sectores del disco:"));
        int sizeSector = Integer.parseInt(JOptionPane.showInputDialog(this, "Escriba el tamaño de los sectores del disco:"));

        fileSystem = new FileSystem(sectorsQuantity, sizeSector);
        fileSystem.addPropertyChangeListener((PropertyChangeEvent evt) -> {
            if ("current".equals(evt.getPropertyName())) {
                Directory currentDirectory = (Directory) evt.getNewValue();
                pathLbl.setText(currentDirectory.getPath());
                updateFilesTable(currentDirectory);
            }
        });
        pathLbl.setText(fileSystem.getCurrent().getPath());

        //Habilitar la seleccion de multiples en la tabla
        filesTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Add MouseListener to filesTable to handle row clicks
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
                            // String nodeName = filesTable.getValueAt(row, 0).toString();
                            // fileSystem.changeDirectory(nodeName);
                            String nodeName = filesTable.getValueAt(selectedRows[0], 0).toString();
                            fileSystem.changeDirectory(nodeName);
                        }
                    }
                    else if (e.getButton() == MouseEvent.BUTTON3) { // Right click
                        // if (row >= 0) {
                            // String nodeName = filesTable.getValueAt(row, 0).toString();
                        if(selectedRows.length >= 1){
                            String nodeName = filesTable.getValueAt(selectedRows[0], 0).toString();
                            int result = JOptionPane.showOptionDialog(null,
                                    "Seleccione una opción",
                                    "Opciones",
                                    JOptionPane.YES_NO_CANCEL_OPTION,
                                    JOptionPane.QUESTION_MESSAGE,
                                    null,
                                    new Object[]{"Eliminar", "Renombrar", "Mover" , "Modificar", "Propiedades"},
                                    "Eliminar");

                            if (result == 0) {
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
                                updateFilesTable(fileSystem.getCurrent());
                            }
                            else if (result == 3) {
                                String fileContent = fileSystem.readFile(nodeName);
                                String newContent = JOptionPane.showInputDialog(null, "Escriba el nuevo contenido del archivo:", fileContent);
                                fileSystem.modifyFile(nodeName, newContent);
                            }
                            else if (result == 4) {
                                JOptionPane.showMessageDialog(null, "Propiedades del directorio: " );
                            }
                        }
                    }
                }
                else{
                    //Aquí se maneja cuando se presiona un archivo y arriba cuando se preiona un directorio
                    if (e.getButton() == MouseEvent.BUTTON1) {// Left click
                        // Open file
                        // if (row >= 0) {
                            // String nodeName = filesTable.getValueAt(row, 0).toString();
                        if(selectedRows.length >= 1){
                            String nodeName = filesTable.getValueAt(selectedRows[0], 0).toString(); 
                            JOptionPane.showMessageDialog(null, "Contenido: " + fileSystem.readFile(nodeName));
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
                                    new Object[]{"Eliminar", "Renombrar", "Mover", "Modificar", "Propiedades"},
                                    "Eliminar");
                            if (result == 0) {
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
                                updateFilesTable(fileSystem.getCurrent());
                            }
                            else if (result == 3) {
                                String fileContent = fileSystem.readFile(nodeName);
                                String newContent = JOptionPane.showInputDialog(null, "Escriba el nuevo contenido del archivo:", fileContent);
                                fileSystem.modifyFile(nodeName, newContent);
                            }
                            else if (result == 4) {
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
    }
    
    private void updateFilesTable(Directory directory) {
        String[] columnNames = {"Name", "Type"};
        Object[][] data = Arrays.stream(directory.getChildren()) // Convert array to stream
                .map(node -> new Object[]{node.getName(), node instanceof Directory ? "Directory" : "File"})
                .toArray(Object[][]::new);
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        filesTable.setModel(model);
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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
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

        javax.swing.GroupLayout buttonsPanelLayout = new javax.swing.GroupLayout(buttonsPanel);
        buttonsPanel.setLayout(buttonsPanelLayout);
        buttonsPanelLayout.setHorizontalGroup(
            buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonsPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(createDirectoryBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(createFileBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)))
        );
        buttonsPanelLayout.setVerticalGroup(
            buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(createDirectoryBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(createFileBtn)
                .addContainerGap(386, Short.MAX_VALUE))
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(filesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 563, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(lowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(lowPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(buttonsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(578, Short.MAX_VALUE)))
        );
        lowPanelLayout.setVerticalGroup(
            lowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lowPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(filesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE))
            .addGroup(lowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(lowPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(buttonsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
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
    }// </editor-fold>//GEN-END:initComponents

    // function to search files and directories
    private void searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchActionPerformed
        String search = searchTextBox.getText();
        System.out.println("Searching for: " + search);
        // search for the file or directory
        ArrayList<File> files = fileSystem.searchFile(search);
        ArrayList<Directory> directories = fileSystem.searchDirectory(search);
        // show the results
        if(files.size() > 0 || directories.size() > 0){
            // show the results in a table
            String[] columnNames = {"Name", "Type"};
            Object[][] data = new Object[files.size() + directories.size()][2];
            int i = 0;
            for (File file : files) {
                data[i][0] = file.getName();
                data[i][1] = "File";
                i++;
            }
            for (Directory directory : directories) {
                data[i][0] = directory.getName();
                data[i][1] = "Directory";
                i++;
            }
            DefaultTableModel model = new DefaultTableModel(data, columnNames);
            filesTable.setModel(model);
            
        }
        else{
            JOptionPane.showMessageDialog(this, "No se encontraron resultados.");
        }
    }//GEN-LAST:event_searchActionPerformed

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
                    if(fileSystem.createFile(newFileName, content)){
                        JOptionPane.showMessageDialog(this, "Archivo creado exitosamente.");
                    }
                    else {
                        JOptionPane.showMessageDialog(this, "No hay suficiente espacio en el disco.");
                    }
                }
            } else {
                if(fileSystem.createFile(newFileName, content)){
                    JOptionPane.showMessageDialog(this, "Archivo creado exitosamente.");
                }
                else {
                    JOptionPane.showMessageDialog(this, "No hay suficiente espacio en el disco.");
                }
            }
            
            updateFilesTable(fileSystem.getCurrent());
        }
        else {
            JOptionPane.showMessageDialog(this, "El nombre del archivo no puede estar vacío.");
        }
    }//GEN-LAST:event_createFileBtnActionPerformed

    
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
    private javax.swing.JButton createDirectoryBtn;
    private javax.swing.JButton createFileBtn;
    private javax.swing.JScrollPane filesPanel;
    private javax.swing.JTable filesTable;
    private javax.swing.JButton search;
    private javax.swing.JPanel lowPanel;
    private javax.swing.JLabel pathLbl;
    private javax.swing.JTextField searchTextBox;
    private javax.swing.JPanel toolsPanel;
    // End of variables declaration//GEN-END:variables
}
