package games.dungeon_puzzle;

import java.util.*;

public class Room {
    private String name;
    private String description;
    private Map<String, Room> exits;
    private Map<String, Item> items;

    public Room(String name, String description) {
        this.name = name;
        this.description = description;
        exits = new HashMap<>();
        items = new HashMap<>();
    }

    public void addExit(String direction, Room room) {
        exits.put(direction, room);
    }

    public Room getExit(String direction) {
        return exits.get(direction);
    }

    public void addItem(Item item) {
        items.put(item.getName(), item);
    }

    public Item removeItem(String itemName) {
        return items.remove(itemName);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getFullDescription() {
        StringBuilder sb = new StringBuilder(description);
        
        // Add puzzle hints based on room
        if (name.equals("Laboratory")) {
            sb.append("\nAncient runes suggest magical items here might help with rituals...");
        } else if (name.equals("Library")) {
            sb.append("\nScattered notes mention combining texts for knowledge...");
        }
        
        if (!exits.isEmpty()) {
            sb.append("\nExits: ");
            sb.append(String.join(", ", exits.keySet()));
        }
        
        if (!items.isEmpty()) {
            sb.append("\nItems: ");
            for (Item item : items.values()) {
                sb.append("\n  ").append(item.getName()).append(" - ").append(item.getDescription());
            }
        }
        
        return sb.toString();
    }

    public Map<String, Item> getItems() {
        return items;
    }
} 