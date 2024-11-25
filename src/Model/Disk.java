/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import utils.FilesManagement;

/**
 *
 * @author milto
 */
public class Disk {
    private ArrayList<Sector> sectors;
    private ArrayList<FileImplementation> files;
    private int size;
    private int freeSize;

    public Disk(int pSectorsQuantity, int pSizeSector) {
        this.size = pSectorsQuantity * pSizeSector;
        this.freeSize = size;
        this.sectors = new ArrayList<>();
        for (int i = 0; i < pSectorsQuantity; i++) {
            sectors.add(new Sector(pSizeSector));
        }

        diskToFile();
        System.out.println("Disk created with " + pSectorsQuantity + " sectors of " + pSizeSector + " bytes.");
        System.out.println("Total size: " + size + " bytes.");
    }

    public int getSize() {
        return size;
    }

    public ArrayList<Sector> getSectors() {
        return sectors;
    }

    private void calcFreeSize() {
        int size = 0;
        for (Sector sector : sectors) {
            if (sector.getContent().equals("")) {
                size += sector.getSize();
            }
        }
        this.freeSize = size;
    }

    public int getFreeSize() {
        calcFreeSize();
        return freeSize;
    }

    public String readFile(int pSectorId) {
        Sector sector = sectors.get(pSectorId);
        if (sector != null) {
            System.out.println(sector.getAllContent());
            return sector.getAllContent();
        }
        return "sector not found";
    }

    public int newFile(String pName, String pContent) {
        int sectorId = -1;
        Sector sector;
        Sector previousSector = null;

        if (pContent.length() > getFreeSize()) {
            String message = "No hay suficiente espacio en disco para guardar el archivo " + pName + ".";
            message += "\nEspacio libre: " + getFreeSize() + " bytes.";
            message += "\nTamaño del archivo: " + pContent.length() + " bytes.";

            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            return -1;
        }

        for (int i = 0; i < sectors.size(); i++) {
            sector = sectors.get(i);
            if (sector.getContent().equals("") && sector.isFree()) {
                if (sectorId == -1) {
                    sectorId = i;
                }
                if (previousSector != null) {
                    previousSector.setNext(sector);
                }
                if (pContent.length() <= sector.getSize()) {
                    sector.setContent(pContent);
                    pContent = "";
                    break;
                }
                else {
                    sector.setContent(pContent.substring(0, sector.getSize()));
                    pContent = pContent.substring(sector.getSize());
                    previousSector = sector;
                }
            }
        }
        return sectorId;
    }

    public int getFreeSpaceForFile(int sectorId) {
        Sector sector =sectors.get(sectorId);
        return getFreeSize() + (sector.getSectorsCount() * sector.getSize());
    }

    public int modifyFile(int startSectorId, String pContent) {
        if (pContent == null) {
            return -1;
        }

        Sector sector =sectors.get(startSectorId);
        int sectorId = -1;
        Sector previousSector = null;
        int freeSpace = getFreeSpaceForFile(startSectorId);


        if (pContent.length() > freeSpace) {
            String message = "No hay suficiente espacio en disco para modificar el archivo.";
            message += "\nEspacio total disponible para el archivo: " + freeSpace + " bytes.";
            message += "\nTamaño del contenido modificado: " + pContent.length() + " bytes.";

            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            // System.out.println("No hay suficiente espacio en disco para modificar el archivo.");
            // System.out.println("Espacio total disponible para el archivo: " + freeSpace + " bytes.");
            // System.out.println("Tamaño del contenido modificado: " + pContent.length() + " bytes.");
            return -1;
        }
        deleteFile(startSectorId);

        for (int i = 0; i < sectors.size(); i++) {
            sector = sectors.get(i);
            if (sector.getContent().equals("")) {
                if (sectorId == -1) {
                    sectorId = i;
                }
                if (previousSector != null) {
                    previousSector.setNext(sector);
                }
                if (pContent.length() <= sector.getSize()) {
                    sector.setContent(pContent);
                    pContent = "";
                    return sectorId;
                }
                else {
                    sector.setContent(pContent.substring(0, sector.getSize()));
                    pContent = pContent.substring(sector.getSize());
                    previousSector = sector;
                }
            }
        }

        // for (int i = 0; i < startSectorId; el directorio ya++) {
        //     sector = sectors.get(i);
        //     if (sector.getContent().equals("")) {
        //         if (sectorId == -1) {
        //             sectorId = i;
        //         }
        //         if (previousSector != null) {
        //             previousSector.setNext(sector);
        //         }
        //         if (pContent.length() <= sector.getSize()) {
        //             sector.setContent(pContent);
        //             pContent = "";
        //             break;
        //         }
        //         else {
        //             sector.setContent(pContent.substring(0, sector.getSize()));
        //             pContent = pContent.substring(sector.getSize());
        //             previousSector = sector;
        //         }
        //     }
        // }
        return sectorId;
    }

    public boolean deleteFile(int startSectorId) {
        Sector sector = sectors.get(startSectorId);
        Sector nextSector = sector.getNext();
        sector.cleanSector();
        while (nextSector != null) {
            sector = nextSector;
            nextSector = sector.getNext();
            sector.cleanSector();
        }
        return false;
    }

    public void diskToFile() {
        // se crea un archivo con el contenido del disco en el momento en formato json
        String content = "{\n";
        for (int i = 0; i < sectors.size(); i++) {
            content += "  \"Sector " + sectors.get(i).getSectorId() + "\": {\n";
            content += "    \"Content\": \"" + sectors.get(i).getContent() + "\",\n";
            content += "    \"Next\": " + sectors.get(i).getNextId() + "\n";
            if (i == sectors.size() - 1) {
                content += "  }\n";
            }
            else {
                content += "  },\n";
            }
        }
        content += "}";
        
        FilesManagement.crearArchivo("disk.json");
        FilesManagement.escribirArchivo("disk.json", content);
    }

    public String toString() {
        String result = "";
        for (int i = 0; i < sectors.size(); i++) {
            result += "Sector " + sectors.get(i).getSectorId() + ": " + sectors.get(i).getContent() + " ---->"
                    + " Next: " + sectors.get(i).getNextId() + "\n";
        }
        diskToFile();
        return result;
    }
}
