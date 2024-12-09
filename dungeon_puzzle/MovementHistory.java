package games.dungeon_puzzle;

public class MovementHistory {
    private class Node {
        Room room;
        Node prev, next;
        
        Node(Room room) {
            this.room = room;
        }
    }
    
    private Node current;
    private int size;
    
    public MovementHistory() {
        this.current = null;
        this.size = 0;
    }
    
    public void addMove(Room room) {
        Node newNode = new Node(room);
        if (current != null) {
            newNode.prev = current;
            current.next = newNode;
        }
        current = newNode;
        size++;
    }
    
    public Room goBack() {
        if (current != null && current.prev != null) {
            current = current.prev;
            return current.room;
        }
        return null;
    }
    
    public Room goForward() {
        if (current != null && current.next != null) {
            current = current.next;
            return current.room;
        }
        return null;
    }
    
    public boolean canGoBack() {
        return current != null && current.prev != null;
    }
    
    public boolean canGoForward() {
        return current != null && current.next != null;
    }
    
    public int size() {
        return size;
    }
} 