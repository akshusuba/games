package games.dungeon_puzzle;

import java.util.*;

public class Game {
    private Map<String, Room> rooms;
    private Room currentRoom;
    private MovementHistory moveHistory;
    private Map<String, Item> inventory;
    private Queue<GameEvent> eventQueue;
    private PriorityQueue<Puzzle> puzzles;
    private Set<String> completedPuzzles;
    private int score;
    private EnemySpawner enemySpawner;
    private static final int TOTAL_ITEMS = 6;
    private static final int TOTAL_PUZZLES = 3;
    private Map<String, List<Item>> requiredItemsForPuzzle;
    private Player player;
    private long startTime;
    private static final long TIME_LIMIT = 300000; // 5 minutes in milliseconds
    private boolean timeExpired = false;
    private static List<Score> highScores = new ArrayList<>();
    private static final int MAX_HIGH_SCORES = 10;
    private String playerName;

    public Game() {
        rooms = new HashMap<>();
        moveHistory = new MovementHistory();
        inventory = new HashMap<>();
        eventQueue = new LinkedList<>();
        puzzles = new PriorityQueue<>(Comparator.comparingInt(Puzzle::getDifficulty));
        completedPuzzles = new HashSet<>();
        score = 0;
        requiredItemsForPuzzle = new HashMap<>();
        player = new Player();
        enemySpawner = new EnemySpawner();
        initializeGame();
        startTime = System.currentTimeMillis();
    }

    private void initializeGame() {
        createRooms();
        initializePuzzles();
        initializePuzzleRequirements();
    }

    private void createRooms() {
        Room entrance = new Room("Entrance", "A dimly lit entrance to an ancient dungeon.");
        Room hallway = new Room("Hallway", "A long, dark hallway with torches on the walls.");
        Room library = new Room("Library", "An old library filled with dusty books.");
        Room treasury = new Room("Treasury", "A room that once held valuable treasures.");
        Room laboratory = new Room("Laboratory", "A mysterious room with ancient equipment.");
        Room crypt = new Room("Crypt", "A spooky crypt with mysterious symbols.");
        
        connectRooms(entrance, hallway, library, treasury, laboratory, crypt);
        distributeItems(entrance, hallway, library, treasury, laboratory, crypt);
        
        rooms.put("Entrance", entrance);
        rooms.put("Hallway", hallway);
        rooms.put("Library", library);
        rooms.put("Treasury", treasury);
        rooms.put("Laboratory", laboratory);
        rooms.put("Crypt", crypt);
        
        currentRoom = entrance;
    }

    private void connectRooms(Room... roomArray) {
        Map<Room, List<Room>> connections = new HashMap<>();
        for (Room room : roomArray) {
            connections.put(room, new ArrayList<>());
        }
        
        connectBidirectional(connections, roomArray[0], roomArray[1], "north", "south");
        connectBidirectional(connections, roomArray[1], roomArray[2], "east", "west");
        connectBidirectional(connections, roomArray[1], roomArray[3], "west", "east");
        connectBidirectional(connections, roomArray[2], roomArray[4], "north", "south");
        connectBidirectional(connections, roomArray[3], roomArray[5], "north", "south");
    }

    private void connectBidirectional(Map<Room, List<Room>> connections, 
                                    Room room1, Room room2, 
                                    String dir1, String dir2) {
        room1.addExit(dir1, room2);
        room2.addExit(dir2, room1);
        connections.get(room1).add(room2);
        connections.get(room2).add(room1);
    }

    private void initializePuzzles() {
        puzzles.offer(new Puzzle("LIGHT_RITUAL", 1,
            "The room is pitch black. You need items worth at least 25 points for the light ritual.",
            "torch and crystal"));

        puzzles.offer(new Puzzle("ANCIENT_TEXT", 2,
            "Ancient text needs powerful items (35+ points combined) to decipher.",
            "book and scroll"));

        puzzles.offer(new Puzzle("PORTAL", 3,
            "Portal requires your most powerful items (60+ points combined).",
            "amulet and wand"));
    }

    private void initializePuzzleRequirements() {
        List<Item> lightRitualItems = new ArrayList<>();
        lightRitualItems.add(new Item("torch", "A burning torch", 5));
        lightRitualItems.add(new Item("crystal", "A glowing crystal", 20));
        requiredItemsForPuzzle.put("LIGHT_RITUAL", lightRitualItems);

        List<Item> textItems = new ArrayList<>();
        textItems.add(new Item("book", "An ancient spellbook", 15));
        textItems.add(new Item("scroll", "A mysterious scroll", 12));
        requiredItemsForPuzzle.put("ANCIENT_TEXT", textItems);

        List<Item> portalItems = new ArrayList<>();
        portalItems.add(new Item("amulet", "A magical amulet", 30));
        portalItems.add(new Item("wand", "A wooden wand", 15));
        requiredItemsForPuzzle.put("PORTAL", portalItems);
    }

    private void distributeItems(Room... rooms) {
        Item[] allItems = {
            // Light Ritual Items (need 25+ points)
            new Item("torch", "A burning torch", 10),
            new Item("crystal", "A glowing crystal", 20),
            new Item("orb", "A mystical orb", 30),
            
            // Ancient Text Items (need 35+ points)
            new Item("book", "An ancient spellbook", 15),
            new Item("scroll", "A mysterious scroll", 25),
            new Item("relic", "An ancient relic", 40),
            
            // Portal Items (need 60+ points)
            new Item("amulet", "A magical amulet", 35),
            new Item("wand", "A powerful wand", 30),
            new Item("crown", "A golden crown", 35),
            
            // Support Items
            new Item("potion", "A healing potion", 15),
            new Item("gem", "A power-enhancing gem", 20),
            new Item("ring", "A magic ring", 25),
            
            // Common Items
            new Item("key", "A rusty key", 5),
            new Item("map", "A torn map", 5),
            new Item("compass", "A broken compass", 5)
        };
        
        // Knuth shuffle
        Random random = new Random();
        for (int i = allItems.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Item temp = allItems[i];
            allItems[i] = allItems[j];
            allItems[j] = temp;
        }
        
        // Distribute shuffled items
        int itemIndex = 0;
        for (Room room : rooms) {
            for (int i = 0; i < 3 && itemIndex < allItems.length; i++) {
                room.addItem(allItems[itemIndex++]);
            }
        }
    }

    public void play() {
        Scanner scanner = new Scanner(System.in);
        boolean playing = true;

        System.out.println("Welcome to Dungeon Puzzler!");
        System.out.println("A Text-Based Adventure Game");
        System.out.println("Enter your name:");
        playerName = scanner.nextLine();
        
        System.out.println("You have 10 minutes to collect items and solve all puzzles!");
        System.out.println("Type 'help' for a list of commands.");

        while (playing && !timeExpired) {
            checkTimeAndDisplay();
            
            System.out.println("\n" + currentRoom.getDescription());
            System.out.print("> ");
            String command = scanner.nextLine().toLowerCase();
            
            processCommand(command, scanner);
            processEvents();
            
            if (checkWinCondition()) {
                handleWin();
                playing = false;
            }
        }

        if (timeExpired) {
            handleTimeout();
        }
    }

    private void checkTimeAndDisplay() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;
        
        if (elapsedTime >= TIME_LIMIT) {
            timeExpired = true;
            System.out.println("\nTime's up! Game Over!");
            return;
        }

        long remainingTime = (TIME_LIMIT - elapsedTime) / 1000;
        System.out.println("\nTime remaining: " + remainingTime/60 + " minutes " + remainingTime%60 + " seconds");
    }

    private void processCommand(String command, Scanner scanner) {
        switch (command) {
            case "help": showHelp(); break;
            case "look": look(); break;
            case "inventory": showInventory(); break;
            case "back": goBack(); break;
            case "solve": solvePuzzle(scanner); break;
            case "status": showStatus(); break;
            case "scores": showHighScores(); break;
            case "quit": System.exit(0); break;
            case "forward": goForward(); break;
            case "hint": showHint(); break;
            case "search": handleSearchCommand(scanner); break;
            default: handleDefaultCommands(command);
        }
    }

    private void handleWin() {
        long completionTime = (System.currentTimeMillis() - startTime) / 1000;
        System.out.println("\nCongratulations! You've won the game!");
        System.out.println("Time taken: " + completionTime/60 + " minutes " + completionTime%60 + " seconds");
        System.out.println("Final Score: " + score);
        
        int timeBonus = calculateTimeBonus(completionTime);
        System.out.println("Time Bonus: " + timeBonus);
        score += timeBonus;
        System.out.println("Total Score with Time Bonus: " + score);
        
        updateHighScores(playerName, score, completionTime);
    }

    private void handleTimeout() {
        System.out.println("Game Over! You ran out of time.");
        showFinalStatus();
    }

    private boolean checkWinCondition() {
        if (completedPuzzles.size() >= TOTAL_PUZZLES) {
            for (List<Item> requiredItems : requiredItemsForPuzzle.values()) {
                for (Item item : requiredItems) {
                    if (!inventory.containsKey(item.getName())) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    private void showHelp() {
        System.out.println("Available commands:");
        System.out.println("  go <direction> - Move in a direction (north, south, east, west)");
        System.out.println("  look          - Look around the current room");
        System.out.println("  inventory     - Show your inventory");
        System.out.println("  take <item>   - Take an item from the room");
        System.out.println("  drop <item>   - Drop an item from your inventory");
        System.out.println("  solve         - Try to solve the current puzzle");
        System.out.println("  status        - Show game progress");
        System.out.println("  back          - Go back to the previous room");
        System.out.println("  scores        - Show high scores");
        System.out.println("  help          - Show this help message");
        System.out.println("  quit          - Exit the game");
        System.out.println("  forward       - Move forward in your path");
        System.out.println("  hint          - Show hint for current puzzle");
        System.out.println("  search        - Search for items in a value range");
    }

    private void look() {
        System.out.println(currentRoom.getFullDescription());
    }

    private void move(String direction) {
        Room nextRoom = currentRoom.getExit(direction);
        if (nextRoom != null) {
            moveHistory.addMove(currentRoom);
            currentRoom = nextRoom;
            
            enemySpawner.updateProgress(completedPuzzles.size());
            if (Math.random() < 0.3) {
                Enemy enemy = enemySpawner.getNextEnemy();
                handleCombat(enemy);
            }
        } else {
            System.out.println("You can't go that way!");
        }
    }

    private void goBack() {
        Room previousRoom = moveHistory.goBack();
        if (previousRoom != null) {
            currentRoom = previousRoom;
            System.out.println("You went back to " + currentRoom.getName());
        } else {
            System.out.println("You can't go back any further!");
        }
    }

    private void goForward() {
        Room nextRoom = moveHistory.goForward();
        if (nextRoom != null) {
            currentRoom = nextRoom;
            System.out.println("You went forward to " + currentRoom.getName());
        } else {
            System.out.println("You can't go forward any further!");
        }
    }

    private void handleDefaultCommands(String command) {
        if (command.equals("forward")) {
            goForward();
        } else if (command.startsWith("go ")) {
            move(command.substring(3));
        } else if (command.startsWith("take ")) {
            takeItem(command.substring(5));
        } else if (command.startsWith("drop ")) {
            dropItem(command.substring(5));
        } else {
            System.out.println("I don't understand that command.");
        }
    }

    private void takeItem(String itemName) {
        Item item = currentRoom.removeItem(itemName);
        if (item != null) {
            inventory.put(itemName, item);
            score += item.getValue();
            System.out.println("Taken: " + itemName);
        } else {
            System.out.println("There's no " + itemName + " here!");
        }
    }

    private void dropItem(String itemName) {
        Item item = inventory.remove(itemName);
        if (item != null) {
            currentRoom.addItem(item);
            System.out.println("Dropped: " + itemName);
        } else {
            System.out.println("You don't have " + itemName + "!");
        }
    }

    private void showInventory() {
        if (inventory.isEmpty()) {
            System.out.println("Your inventory is empty.");
        } else {
            System.out.println("Inventory:");
            for (Item item : inventory.values()) {
                System.out.println("  " + item.getName() + " - " + item.getDescription());
            }
        }
    }

    private void solvePuzzle(Scanner scanner) {
        if (puzzles.isEmpty()) {
            System.out.println("No more puzzles to solve!");
            return;
        }

        Puzzle currentPuzzle = puzzles.peek();
        List<Item> requiredItems = requiredItemsForPuzzle.get(currentPuzzle.getType());
        
        if (!checkRequiredItems(requiredItems)) {
            return;
        }

        System.out.println("\nCurrent Puzzle:");
        System.out.println(currentPuzzle.getQuestion());
        System.out.println("(Hint: You have the required items in your inventory)");
        System.out.print("Your answer: ");
        String answer = scanner.nextLine().toLowerCase();

        if (answer.equals(currentPuzzle.getAnswer().toLowerCase())) {
            handleCorrectPuzzleSolution(currentPuzzle);
        } else {
            System.out.println("Incorrect. Try again later!");
        }
    }

    private boolean checkRequiredItems(List<Item> requiredItems) {
        List<String> missingItems = new ArrayList<>();
        int totalValue = 0;
        
        // Check if we have the specific items
        for (Item requiredItem : requiredItems) {
            if (!inventory.containsKey(requiredItem.getName())) {
                missingItems.add(requiredItem.getName());
            } else {
                totalValue += inventory.get(requiredItem.getName()).getValue();
            }
        }

        // Check if items meet value requirement
        int requiredValue = 0;
        Puzzle currentPuzzle = puzzles.peek();
        switch(currentPuzzle.getType()) {
            case "LIGHT_RITUAL": requiredValue = 25; break;
            case "ANCIENT_TEXT": requiredValue = 35; break;
            case "PORTAL": requiredValue = 60; break;
        }

        if (totalValue < requiredValue) {
            System.out.println("\nYour items aren't powerful enough!");
            System.out.println("Current combined value: " + totalValue);
            System.out.println("Required value: " + requiredValue);
            return false;
        }

        // Check for alternative solutions
        if (checkAlternativeSolution(currentPuzzle.getType(), 
                                   new ArrayList<>(inventory.values()))) {
            return true;
        }

        if (!missingItems.isEmpty()) {
            System.out.println("\nYou don't have the required items!");
            System.out.println("Missing items: " + String.join(", ", missingItems));
            return false;
        }
        return true;
    }

    private void handleCorrectPuzzleSolution(Puzzle puzzle) {
        puzzles.poll();
        completedPuzzles.add(puzzle.getType());
        int puzzleScore = 50 * puzzle.getDifficulty();
        score += puzzleScore;
        System.out.println("Correct! You earned " + puzzleScore + " points!");
    }

    private void handleCombat(Enemy enemy) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nYou encounter a " + enemy.getName() + "!");
        
        while (enemy.isAlive() && player.isAlive()) {
            displayCombatStatus(enemy);
            int choice = getCombatChoice(scanner);
            processCombatChoice(choice, enemy);
            
            if (!player.isAlive()) {
                System.out.println("You have been defeated! Game Over.");
                System.exit(0);
            }
        }
        
        if (!enemy.isAlive()) {
            handleEnemyDefeat(enemy);
        }
    }

    private void displayCombatStatus(Enemy enemy) {
        System.out.println("\nEnemy Health: " + enemy.getHealth());
        System.out.println("Your Health: " + player.getHealth());
        System.out.println("\nWhat would you like to do?");
        System.out.println("1. Attack");
        System.out.println("2. Use Health Potion");
        System.out.println("3. Try to Run");
    }

    private int getCombatChoice(Scanner scanner) {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                if (input.matches("\\d+")) {
                    int choice = Integer.parseInt(input);
                    if (choice >= 1 && choice <= 3) {
                        return choice;
                    }
                }
                System.out.println("Please enter a number (1-3):");
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number (1-3):");
            }
        }
    }

    private void processCombatChoice(int choice, Enemy enemy) {
        switch (choice) {
            case 1: handleAttack(enemy); break;
            case 2: handlePotion(); break;
            case 3: handleEscape(enemy); break;
        }
    }

    private void handleAttack(Enemy enemy) {
        int playerDamage = player.attack();
        enemy.takeDamage(playerDamage);
        System.out.println("You deal " + playerDamage + " damage!");
        
        if (enemy.isAlive()) {
            int enemyDamage = enemy.attack();
            player.takeDamage(enemyDamage);
            System.out.println("Enemy deals " + enemyDamage + " damage!");
        }
    }

    private void handlePotion() {
        if (inventory.containsKey("potion")) {
            player.heal(30);
            inventory.remove("potion");
            System.out.println("You used a health potion! Health restored to " + player.getHealth());
        } else {
            System.out.println("You don't have any health potions!");
        }
    }

    private void handleEscape(Enemy enemy) {
        if (Math.random() < 0.5) {
            System.out.println("You successfully ran away!");
        } else {
            System.out.println("Couldn't escape!");
            int damage = enemy.attack();
            player.takeDamage(damage);
            System.out.println("Enemy deals " + damage + " damage!");
        }
    }

    private void handleEnemyDefeat(Enemy enemy) {
        System.out.println("You defeated the " + enemy.getName() + "!");
        Item loot = enemy.getLoot();
        if (loot != null) {
            inventory.put(loot.getName(), loot);
            System.out.println("You found: " + loot.getName() + " - " + loot.getDescription());
            score += loot.getValue();
        }
    }

    private void showStatus() {
        System.out.println("\nGame Status:");
        System.out.println("Items Collected: " + inventory.size() + "/" + TOTAL_ITEMS);
        System.out.println("Puzzles Solved: " + completedPuzzles.size() + "/" + TOTAL_PUZZLES);
        System.out.println("Current Score: " + score);
        
        // Add inventory display with values
        if (!inventory.isEmpty()) {
            System.out.println("\nInventory Items:");
            int totalValue = 0;
            for (Item item : inventory.values()) {
                System.out.printf("  %s (%d points) - %s%n", 
                    item.getName(), 
                    item.getValue(), 
                    item.getDescription());
                totalValue += item.getValue();
            }
            System.out.println("Total Item Value: " + totalValue + " points");
        }
        
        long remainingTime = (TIME_LIMIT - (System.currentTimeMillis() - startTime)) / 1000;
        System.out.println("\nTime Remaining: " + remainingTime/60 + " minutes " + remainingTime%60 + " seconds");
    }

    private void showFinalStatus() {
        System.out.println("\nFinal Status:");
        System.out.println("Items Collected: " + inventory.size() + "/" + TOTAL_ITEMS);
        System.out.println("Puzzles Solved: " + completedPuzzles.size() + "/" + TOTAL_PUZZLES);
        System.out.println("Final Score: " + score);
        
        if (!puzzles.isEmpty()) {
            System.out.println("\nUnsolved Puzzles:");
            for (Puzzle puzzle : puzzles) {
                System.out.println("- " + puzzle.getType());
            }
        }
    }

    private int calculateTimeBonus(long completionTime) {
        long timeRemaining = TIME_LIMIT/1000 - completionTime;
        if (timeRemaining > 0) {
            return (int)(timeRemaining / 60) * 50;
        }
        return 0;
    }

    private void updateHighScores(String playerName, int finalScore, long timeTaken) {
        highScores.add(new Score(playerName, finalScore, timeTaken));
        
        if (highScores.size() > 1) {
            mergeSort(highScores, 0, highScores.size() - 1);
        }
        
        if (highScores.size() > MAX_HIGH_SCORES) {
            highScores = highScores.subList(0, MAX_HIGH_SCORES);
        }
        
        showHighScores();
    }

    private void mergeSort(List<Score> scores, int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSort(scores, left, mid);
            mergeSort(scores, mid + 1, right);
            merge(scores, left, mid, right);
        }
    }

    private void merge(List<Score> scores, int left, int mid, int right) {
        List<Score> leftList = new ArrayList<>(scores.subList(left, mid + 1));
        List<Score> rightList = new ArrayList<>(scores.subList(mid + 1, right + 1));
        
        int i = 0, j = 0, k = left;
        
        while (i < leftList.size() && j < rightList.size()) {
            if (leftList.get(i).compareTo(rightList.get(j)) <= 0) {
                scores.set(k++, leftList.get(i++));
            } else {
                scores.set(k++, rightList.get(j++));
            }
        }
        
        while (i < leftList.size()) {
            scores.set(k++, leftList.get(i++));
        }
        
        while (j < rightList.size()) {
            scores.set(k++, rightList.get(j++));
        }
    }

    private void showHighScores() {
        System.out.println("\n=== HIGH SCORES ===");
        System.out.println("Name            Score   Time");
        System.out.println("--------------------------------");
        for (int i = 0; i < highScores.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, highScores.get(i));
        }
    }

    private void processEvents() {
        while (!eventQueue.isEmpty()) {
            GameEvent event = eventQueue.poll();
            System.out.println("\n" + event.getMessage());
            
            if (event.getType().equals("RANDOM_ITEM")) {
                if (Math.random() < 0.5) {
                    Item randomItem = new Item("gem", "A mysterious gem", 20);
                    currentRoom.addItem(randomItem);
                    System.out.println("A mysterious gem appeared in the room!");
                }
            }
        }
    }

    private void showHint() {
        if (!puzzles.isEmpty()) {
            Puzzle currentPuzzle = puzzles.peek();
            System.out.println("\nHint for current puzzle:");
            switch(currentPuzzle.getType()) {
                case "LIGHT_RITUAL":
                    System.out.println("The darkness might be dispelled by combining a light source with a magical focus...");
                    break;
                case "ANCIENT_TEXT":
                    System.out.println("Knowledge often comes from combining different sources of information...");
                    break;
                case "PORTAL":
                    System.out.println("Magical portals usually require both a focus and a channeling item...");
                    break;
            }
        }
    }

    private List<Item> getAllItems() {
        List<Item> allItems = new ArrayList<>();
        // Collect items from all rooms and inventory
        for (Room room : rooms.values()) {
            allItems.addAll(room.getItems().values());
        }
        allItems.addAll(inventory.values());
        
        // Sort by value for binary search
        allItems.sort(Comparator.comparingInt(Item::getValue));
        return allItems;
    }

    private List<Item> findItemsInValueRange(int minValue, int maxValue) {
        List<Item> allItems = getAllItems();
        List<Item> result = new ArrayList<>();
        
        // Binary search for lower bound
        int low = binarySearchLowerBound(allItems, minValue);
        // Binary search for upper bound
        int high = binarySearchUpperBound(allItems, maxValue);
        
        // Add all items in range
        for (int i = low; i <= high && i < allItems.size(); i++) {
            if (allItems.get(i).getValue() >= minValue && 
                allItems.get(i).getValue() <= maxValue) {
                result.add(allItems.get(i));
            }
        }
        return result;
    }

    private int binarySearchLowerBound(List<Item> items, int targetValue) {
        int left = 0;
        int right = items.size() - 1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (items.get(mid).getValue() < targetValue) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return left;
    }

    private int binarySearchUpperBound(List<Item> items, int targetValue) {
        int left = 0;
        int right = items.size() - 1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (items.get(mid).getValue() <= targetValue) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return right;
    }

    private void handleSearchCommand(Scanner scanner) {
        System.out.println("Enter minimum value:");
        int minValue = scanner.nextInt();
        System.out.println("Enter maximum value:");
        int maxValue = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        List<Item> foundItems = findItemsInValueRange(minValue, maxValue);
        
        if (foundItems.isEmpty()) {
            System.out.println("No items found in that value range.");
        } else {
            System.out.println("Items found in value range " + minValue + "-" + maxValue + ":");
            for (Item item : foundItems) {
                System.out.printf("  %s (%d points) - %s%n", 
                    item.getName(), item.getValue(), item.getDescription());
            }
        }
    }

    private boolean checkAlternativeSolution(String puzzleType, List<Item> items) {
        int totalValue = items.stream().mapToInt(Item::getValue).sum();
        switch(puzzleType) {
            case "LIGHT_RITUAL":
                return totalValue >= 25 && 
                       (items.stream().anyMatch(i -> i.getName().contains("torch") || 
                                                   i.getName().contains("crystal") ||
                                                   i.getName().contains("orb")));
            case "ANCIENT_TEXT":
                return totalValue >= 35 && 
                       (items.stream().anyMatch(i -> i.getName().contains("book") || 
                                                   i.getName().contains("scroll") ||
                                                   i.getName().contains("relic")));
            case "PORTAL":
                return totalValue >= 60 && 
                       (items.stream().anyMatch(i -> i.getName().contains("amulet") || 
                                                   i.getName().contains("wand") ||
                                                   i.getName().contains("crown")));
        }
        return false;
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.play();
    }
} 