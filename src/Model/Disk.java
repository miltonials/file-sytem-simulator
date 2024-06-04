/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.util.ArrayList;

/**
 *
 * @author milto
 */
public class Disk {
    private ArrayList<Sector> sectors;
    private ArrayList<File> files;
    private int size;
    private int freeSize;

    public Disk(int pSectorsQuantity, int pSizeSector) {
        this.size = pSectorsQuantity * pSizeSector;
        this.freeSize = size;
        this.sectors = new ArrayList<>();
        for (int i = 0; i < pSectorsQuantity; i++) {
            sectors.add(new Sector(pSizeSector));
        }

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

    public int readFile(int pSectorId) {
        Sector sector = sectors.get(pSectorId);
        if (sector != null) {
            System.out.println(sector.getAllContent());
            return 1;
        }
        return -1;
    }

    public void newFile(String pName, String pContent) {
        Sector sector;
        Sector previousSector = null;

        // if (pContent.length() > getFreeSize()) {
        //     System.out.println("No hay suficiente espacio en disco para guardar el archivo " + pName + ".");
        //     System.out.println("Espacio libre: " + getFreeSize() + " bytes.");
        //     System.out.println("Tamaño del archivo: " + pContent.length() + " bytes.");
        //     return;
        // }

        for (int i = 0; i < sectors.size(); i++) {
            sector = sectors.get(i);
            if (sector.getContent().equals("")) {
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

        if (pContent.length() > 0) {
            System.out.println("File " + pName + " is too big for the disk.");
        }
    }

    public String toString() {
        String result = "";
        for (int i = 0; i < sectors.size(); i++) {
            result += "Sector " + sectors.get(i).getSectorId() + ": " + sectors.get(i).getContent() + " ---->"
                    + " Next: " + sectors.get(i).getNextId() + "\n";
        }
        return result;
    }
}
