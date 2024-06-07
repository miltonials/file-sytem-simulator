package Model;

public class Sector {
    public static int id = 0;
    private int sectorId;
    private int size;
    private String content;
    private Sector next;

    public Sector(int pSize) {
        this.sectorId = id++;
        this.size = pSize;
        this.content = "";
        this.next = null;
    }

    public int getSectorId() {
        return sectorId;
    }

    public int getSize() {
        return size;
    }

    public String getContent() {
        return content;
    }

    public String getAllContent() {
        // return content;
        if (next != null) {
            return content + next.getAllContent();
        } else {
            return content;
        }
    }

    public int getNextId() {
        if (next == null) {
            return -1;
        }
        return next.getSectorId();
    }

    public Sector getNext() {
        if (next == null) {
            return null;
        }
        return next;
    }

    public int getSectorsCount() {
        if (next == null) {
            return 1;
        }
        return 1 + next.getSectorsCount();
    }

    public void setContent(String pContent) {
        this.content = pContent;
    }

    public void setNext(Sector pNext) {
        this.next = pNext;
    }

    public void cleanSector() {
        this.content = "";
        this.next = null;
    }

}
