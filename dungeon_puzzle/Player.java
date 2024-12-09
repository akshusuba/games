package games.dungeon_puzzle;

import java.util.Map;
import java.util.HashMap;

public class Player {
    private int health;
    private int maxHealth;
    private int attackPower;
    private Map<String, Item> inventory;

    public Player() {
        this.maxHealth = 100;
        this.health = maxHealth;
        this.attackPower = 15;
        this.inventory = new HashMap<>();
    }

    public int attack() {
        // Base attack + random bonus
        return attackPower + (int)(Math.random() * 10);
    }

    public void heal(int amount) {
        health = Math.min(health + amount, maxHealth);
    }

    public void takeDamage(int damage) {
        health -= damage;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public int getHealth() {
        return health;
    }

    public void addItem(Item item) {
        inventory.put(item.getName(), item);
    }

    public Map<String, Item> getInventory() {
        return inventory;
    }
} 