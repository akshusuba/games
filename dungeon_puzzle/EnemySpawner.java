package games.dungeon_puzzle;

import java.util.PriorityQueue;
import java.util.Comparator;

public class EnemySpawner {
    private PriorityQueue<Enemy> enemyQueue;
    private int puzzlesSolved;  // Track player progress

    public EnemySpawner() {
        // Initialize queue with custom comparator based on enemy difficulty
        enemyQueue = new PriorityQueue<>(new Comparator<Enemy>() {
            @Override
            public int compare(Enemy e1, Enemy e2) {
                return Integer.compare(e1.getDifficulty(), e2.getDifficulty());
            }
        });
        initializeEnemies();
    }

    private void initializeEnemies() {
        // Add enemies in order of difficulty
        enemyQueue.offer(new Enemy("Skeleton", 30, 10, new Item("bone", "A mysterious glowing bone", 15), 1));
        enemyQueue.offer(new Enemy("Ghost", 20, 15, new Item("ectoplasm", "A ghostly substance", 20), 2));
        enemyQueue.offer(new Enemy("Goblin", 40, 8, new Item("gold_coin", "A shiny gold coin", 25), 3));
        enemyQueue.offer(new Enemy("Dark Wizard", 50, 12, new Item("spell_scroll", "A powerful magic scroll", 30), 4));
    }

    public Enemy getNextEnemy() {
        // Return appropriate enemy based on puzzles solved
        Enemy enemy = null;
        for (Enemy e : enemyQueue) {
            if (e.getDifficulty() <= puzzlesSolved + 1) {
                enemy = e;
                break;
            }
        }
        return enemy != null ? createEnemyCopy(enemy) : createDefaultEnemy();
    }

    public void updateProgress(int solvedPuzzles) {
        this.puzzlesSolved = solvedPuzzles;
    }

    private Enemy createEnemyCopy(Enemy original) {
        return new Enemy(
            original.getName(),
            original.getMaxHealth(),
            original.getAttackPower(),
            original.getLoot(),
            original.getDifficulty()
        );
    }

    private Enemy createDefaultEnemy() {
        return new Enemy("Skeleton", 30, 10, new Item("bone", "A mysterious glowing bone", 15), 1);
    }
} 