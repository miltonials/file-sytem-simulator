package Model;
import java.util.Date;
import java.util.Enumeration;

import javax.swing.tree.TreeNode;

public class Node implements TreeNode{
    private String name;
    private String path;
    private Directory parent;
    private String created;
    private String modified = "";

    public Node(String name, String path, Directory parent) {
        this.name = name;
        this.path = path;
        this.parent = parent;
        this.created = new Date().toString();
        this.modified = this.created;
    }
    
    @Override
    public Directory getParent() {
        return parent;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getCreated() {
        return created;
    }
    
    public void delete() {
        parent.removeChild(name);
    }
    
    public void setParent(Directory parent) {
        this.parent = parent;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public TreeNode getChildAt(int childIndex) {
        return null;
    }

    @Override
    public int getChildCount() {
       return -1;
    }

    @Override
    public int getIndex(TreeNode node) {
        return -1;
    }

    @Override
    public boolean getAllowsChildren() {
        return false;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public Enumeration<? extends TreeNode> children() {
        return null;
    }
    public String toString(){
        return name;
    }

    public String getModified() {
        return modified;
    }

    public void setModified() {
        this.modified = new Date().toString();
    }
    
    
}
