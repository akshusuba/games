package games.dungeon_puzzle;

public class Enemy {
    private String name;
    private int health;
    private int maxHealth;
    private int attackPower;
    private Item loot;
    private int difficulty;

    public Enemy(String name, int health, int attackPower, Item loot, int difficulty) {
        this.name = name;
        this.health = health;
        this.maxHealth = health;
        this.attackPower = attackPower;
        this.loot = loot;
        this.difficulty = difficulty;
    }

    public int attack() {
        // Add some randomness to attack power
        return attackPower + (int)(Math.random() * 5);
    }

    public void takeDamage(int damage) {
        health -= damage;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public Item getLoot() {
        return loot;
    }

    public int getDifficulty() { return difficulty; }
    public int getMaxHealth() { return maxHealth; }
    public int getAttackPower() { return attackPower; }
} 