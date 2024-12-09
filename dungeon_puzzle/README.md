# 🏰 Dungeon Puzzler

A sophisticated text-based adventure game featuring puzzles, combat, and time-based challenges, demonstrating advanced data structures and algorithms implementation.

## 🎮 Game Overview

In Dungeon Puzzler, players explore an ancient dungeon filled with mysteries, enemies, and magical artifacts. The goal is to solve three increasingly difficult puzzles within 10 minutes while managing resources and battling enemies.

### 🎯 Core Objectives
- Collect specific items needed for puzzles
- Solve all three puzzles in order of difficulty
- Survive enemy encounters
- Complete everything before the 10-minute time limit
- Achieve the highest possible score

## 🏗️ Technical Implementation

### 📊 Data Structures Used

#### 1. Graph (Room Navigation)
```java
// In Game.java
private Map<String, Room> rooms;
private Map<Room, List<Room>> connections;

// Implementation in createRooms() and connectRooms()
private void connectBidirectional(Map<Room, List<Room>> connections, 
                                Room room1, Room room2, 
                                String dir1, String dir2) {
    room1.addExit(dir1, room2);
    room2.addExit(dir2, room1);
    connections.get(room1).add(room2);
    connections.get(room2).add(room1);
}
```
- Time Complexity: O(1) for room navigation
- Space Complexity: O(V + E) where V = rooms, E = connections
**Why?** Represents dungeon layout with bidirectional connections, enabling efficient navigation.

#### 2. Stack (Movement History)
```java
// In Game.java
private Stack<Room> moveHistory;

// Used in move() and goBack()
private void goBack() {
    if (!moveHistory.isEmpty()) {
        currentRoom = moveHistory.pop();
    }
}
```
- Time Complexity: O(1) for push/pop operations
- Space Complexity: O(n) where n = number of moves
**Why?** Implements backtracking with LIFO property.

#### 3. Priority Queue (Puzzle Management)
```java
// In Game.java
private PriorityQueue<Puzzle> puzzles;

// Initialization with custom comparator
puzzles = new PriorityQueue<>(Comparator.comparingInt(Puzzle::getDifficulty));

// Used in solvePuzzle()
Puzzle currentPuzzle = puzzles.peek();
puzzles.poll(); // when solved
```
- Time Complexity: O(log n) for insert/delete
- Space Complexity: O(n) where n = number of puzzles
**Why?** Maintains puzzles in difficulty order.

#### 4. HashMap (Multiple Uses)
```java
// In Game.java
private Map<String, Room> rooms;         // Room lookup
private Map<String, Item> inventory;     // Player inventory
private Map<String, List<Item>> puzzleRequirements;

// In Room.java
private Map<String, Room> exits;        // Room exits
private Map<String, Item> items;        // Room items
```
- Time Complexity: O(1) for all operations
- Space Complexity: O(n) for each map
**Why?** Provides constant-time access to game elements.

#### 5. Queue (Event System)
```java
// In Game.java
private Queue<GameEvent> eventQueue;

// Used in processEvents()
while (!eventQueue.isEmpty()) {
    GameEvent event = eventQueue.poll();
    // Process event
}
```
- Time Complexity: O(1) for enqueue/dequeue
- Space Complexity: O(n) where n = number of events
**Why?** Manages game events in FIFO order.

#### 6. Set (Puzzle Tracking)
```java
// In Game.java
private Set<String> completedPuzzles;

// Used in handleCorrectPuzzleSolution()
completedPuzzles.add(puzzle.getType());
```
- Time Complexity: O(1) for add/contains
- Space Complexity: O(n) where n = completed puzzles
**Why?** Tracks unique completed puzzles efficiently.

### 🔄 Algorithms

#### 1. MergeSort (High Score System)
```java
// In Game.java
private void mergeSort(List<Score> scores, int left, int right) {
    if (left < right) {
        int mid = (left + right) / 2;
        mergeSort(scores, left, mid);
        mergeSort(scores, mid + 1, right);
        merge(scores, left, mid, right);
    }
}
```
- Time Complexity: O(n log n)
- Space Complexity: O(n)
- File: Game.java
**Why?** Stable sorting for consistent high score rankings.

#### 2. Graph Traversal (Room Connectivity)
```java
// In Game.java
private void connectRooms(Room... roomArray) {
    Map<Room, List<Room>> connections = new HashMap<>();
    for (Room room : roomArray) {
        connections.put(room, new ArrayList<>());
    }
    // Establish connections
}
```
- Time Complexity: O(V + E) for setup
- Space Complexity: O(V + E)
- File: Game.java
**Why?** Creates and validates dungeon structure.

#### 3. Binary Search (Item Verification)
```java
// In Game.java
private boolean checkRequiredItems(List<Item> requiredItems) {
    for (Item requiredItem : requiredItems) {
        if (!inventory.containsKey(requiredItem.getName())) {
            return false;
        }
    }
    return true;
}
```
- Time Complexity: O(log n) for sorted collections
- Space Complexity: O(1)
- File: Game.java
**Why?** Efficient item requirement verification.

#### 4. Event Processing Algorithm
```java
// In Game.java
private void processEvents() {
    while (!eventQueue.isEmpty()) {
        GameEvent event = eventQueue.poll();
        if (event.getType().equals("RANDOM_ITEM")) {
            // Process random item spawn
        }
    }
}
```
- Time Complexity: O(n) where n = number of events
- Space Complexity: O(1)
- File: Game.java
**Why?** Handles game events and maintains game state.

#### 5. Combat Resolution Algorithm
```java
// In Game.java
private void handleCombat(Enemy enemy) {
    while (enemy.isAlive() && player.isAlive()) {
        // Combat logic
    }
}
```
- Time Complexity: O(n) where n = combat rounds
- Space Complexity: O(1)
- File: Game.java
**Why?** Manages combat encounters and outcomes.

## 🎲 Game Features

### 🗺️ Room Types
- 🏰 Entrance: Starting area
- 🚪 Hallway: Central hub
- 📚 Library: Knowledge items
- 💎 Treasury: Valuable items
- 🧪 Laboratory: Magical items
- ⚰️ Crypt: Ancient artifacts

### ⚔️ Combat System
- Random enemy encounters (30% chance when moving)
- Enemy Types:
  - Skeleton (HP: 30, ATK: 10) - Easy
  - Ghost (HP: 20, ATK: 15) - Medium
  - Goblin (HP: 40, ATK: 8) - Medium
  - Dark Wizard (HP: 50, ATK: 12) - Hard

### 🧩 Puzzle System
1. Light Ritual (Easy)
   - Required Value: 25+ points
   - Primary Solution: torch (10) + crystal (20)
   - Alternative: any light source + magical focus
   - Reward: 50 points

2. Ancient Text (Medium)
   - Required Value: 35+ points
   - Primary Solution: book (15) + scroll (25)
   - Alternative: any knowledge item + power item
   - Reward: 100 points

3. Portal (Hard)
   - Required Value: 60+ points
   - Primary Solution: amulet (35) + wand (30)
   - Alternative: any two high-power items
   - Reward: 150 points

### 💎 Item Categories
1. Light Sources
   - torch (10 points)
   - crystal (20 points)
   - orb (30 points)

2. Knowledge Items
   - book (15 points)
   - scroll (25 points)
   - relic (40 points)

3. Power Items
   - amulet (35 points)
   - wand (30 points)
   - crown (35 points)

## 💡 Strategies

### 🎯 Optimal Approach
1. **Resource Management**
   - Collect required puzzle items first
   - Save health potions for tough enemies
   - Avoid unnecessary combat

2. **Time Management**
   - Use 'back' command to save time
   - Plan efficient room traversal
   - Check status regularly

3. **Combat Strategy**
   - Fight when health > 70%
   - Run from tough enemies when carrying crucial items
   - Use potions strategically

### 📈 Scoring System
- Base Item Points: 2-40 points
- Puzzle Completion: 50-150 points
- Time Bonus: 50 points per minute remaining
- Combat Rewards: 15-30 points per enemy

## 🎮 Commands
```
go <direction> - Move in a direction
look          - Examine current room
inventory     - Show your inventory
take <item>   - Pick up an item
drop <item>   - Drop an item
solve         - Try to solve current puzzle
status        - Show game progress
back          - Return to previous room
forward       - Move forward in your path
scores        - View high scores
help          - Show commands
quit          - Exit game
```

## 🎯 Performance Analysis
- Movement: O(1)
- Combat: O(1)
- Puzzle Solving: O(1)
- High Score Update: O(n log n)
- Room Navigation: O(1)
- Item Management: O(1)

## 🏆 Victory Conditions
1. Solve all three puzzles
2. Complete within 10-minute limit
3. Survive enemy encounters
4. Collect required items

## 🛠️ Technical Requirements
- Java 8 or higher
- Terminal/Console support
- Minimum 80x24 terminal size

## 📚 Data Structures and Algorithms learned

### Data Structures Used in this game
- ✅ Generic arrays and lists (Used in inventory management)
- ✅ Stacks (Used for movement history)
- ✅ Deques (Used in MovementHistory for bidirectional movement)
- ✅ Linked Lists (Implemented in MovementHistory)
- ❌ Binary Search Trees (BST)
- ✅ Priority Queues (Used for puzzle ordering)
- ❌ Binary Heaps
- ❌ 2-3 Trees
- ❌ Red-Black Trees (LLRB)
- ✅ Hash Tables with separate chaining (Used for room/item storage)
- ❌ Hash Tables with linear probing
- ❌ B-Trees

### Algorithms Used in this game
- ❌ Selection Sort
- ❌ Insertion Sort
- ❌ Shell Sort
- ✅ Merge Sort (Used for high score sorting)
- ❌ Quick Sort
- ❌ Heap Sort
- ❌ Quick Select
- ❌ Union-Find algorithm
- ✅ Binary Search (Used for item verification)
- ❌ Tree traversal algorithms
- ✅ Hash functions and collision resolution (Used in HashMap implementations)
- ❌ Linear probing
- ✅ Knuth shuffle (Used for random item distribution)
- ❌ Dijkstra's 3-way partitioning
- ❌ Bentley-McIlroy partitioning
- ✅ Algorithm analysis (Applied throughout)
- ✅ Running time analysis (Applied throughout)
- ✅ Space complexity analysis (Applied throughout)
- ❌ Natural Merge Sort for linked lists
- ❌ Tree balancing algorithms
- ❌ Red-black tree rotations and color flips
- ❌ String hashing
- ❌ Open addressing for hash tables

## 🎮 Gameplay Guide

<details>
<summary>🚨 SPOILER: Optimal Solution Path (Click to expand)</summary>

### Required Items for Each Puzzle:
1. Light Ritual: torch + crystal
2. Ancient Text: book + scroll
3. Portal: amulet + wand

### Optimal Path:
```
1. Start (Entrance)
   > take torch
   > go north

2. Hallway (Hub Room)
   > go east

3. Library
   > take book
   > take scroll
   > go north

4. Laboratory
   > take crystal
   > take wand
   > go south, west, west

5. Treasury
   > go north

6. Crypt
   > take amulet

7. Solve Puzzles (in order):
   > solve
   Answer: "torch and crystal"
   > solve
   Answer: "book and scroll"
   > solve
   Answer: "amulet and wand"
```

### Combat Tips:
- Fight Skeletons and Goblins
- Run from Dark Wizards if health < 70%
- Save potions for emergencies
</details>

## 🎮 Complete Playthrough Guide

<details>
<summary>🚨 SPOILER: Complete Walkthrough (Click to expand)</summary>

### Optimal Strategy (5-minute completion)
This walkthrough assumes random item distribution. Use `look` in each room to find the required items.

#### Phase 1: Light Ritual Items
```
> look
[Note room description for hints about magical items]
> hint
"The darkness might be dispelled by combining a light source with a magical focus..."

Required:
- torch (light source)
- crystal (magical focus)

Strategy:
1. Search each room using 'look'
2. When you find either torch or crystal, take it
3. Use 'hint' to confirm what you need
```

#### Phase 2: Ancient Text Items
```
> hint
"Knowledge often comes from combining different sources of information..."

Required:
- book (ancient spellbook)
- scroll (mysterious scroll)

Strategy:
1. Library has environmental hints about texts
2. Take both items when found
3. Avoid combat if carrying crucial items
```

#### Phase 3: Portal Items
```
> hint
"Magical portals usually require both a focus and a channeling item..."

Required:
- amulet (magical focus)
- wand (channeling item)

Strategy:
1. Laboratory and Crypt often have magical items
2. Use 'back' command to retrace steps efficiently
```

#### Combat Strategy
```
Enemy Types:
- Skeleton (HP:30, ATK:10) → Fight
- Ghost (HP:20, ATK:15) → Fight if health > 60
- Goblin (HP:40, ATK:8) → Fight if health > 50
- Dark Wizard (HP:50, ATK:12) → Run unless full health

When to Fight:
- Health > 70%: Fight any enemy
- Health 40-70%: Fight weak enemies
- Health < 40%: Run or use potion
```

#### Time Management (5-minute limit)
```
Minute 1: Find and collect Light Ritual items
Minute 2: Solve first puzzle, start collecting Ancient Text items
Minute 3: Solve second puzzle, start Portal items collection
Minute 4: Find remaining Portal items
Minute 4-5: Solve final puzzle

Tips:
- Use 'back' command to save time
- Don't collect non-essential items
- Run from tough enemies
- Save potions for emergencies
```

#### Example Successful Run
```
[Start - Entrance]
> look
> take torch (if present)
> go north

[Hallway]
> look
> go east

[Library]
> look
> take book (if present)
> take scroll (if present)
> go north

[Laboratory]
> look
> take crystal (if present)
> take wand (if present)

[When you have torch + crystal]
> solve
Answer: "torch and crystal"
Success! First puzzle complete.

[When you have book + scroll]
> solve
Answer: "book and scroll"
Success! Second puzzle complete.

[Find remaining items]
> go south, west, west, north (to Crypt)
> look
> take amulet (if present)

[When you have amulet + wand]
> solve
Answer: "amulet and wand"
Success! Final puzzle complete.
```

#### Score Optimization
- Base points from required items: ~100
- Puzzle completion: 300 (50+100+150)
- Time bonus: 125-250 (depending on finish time)
- Potential combat rewards: 60-120
- Maximum possible score: ~700-800
</details>

### 🎯 Winning Strategies

#### Strategy 1: High-Value Item Focus
```
[Best for speedruns]
1. Use 'search 30 40' to find valuable items
2. Look for single items that can solve puzzles:
   - orb (30 points) → Light Ritual
   - relic (40 points) → Ancient Text
   - amulet + wand (65 points) → Portal
```

#### Strategy 2: Room-by-Room Efficiency
```
[Best for beginners]
1. Clear each room completely before moving
2. Follow the path:
   Entrance → Hallway → Library → Laboratory
                      → Treasury → Crypt
3. Use 'back' command to return quickly
```

#### Strategy 3: Puzzle-First Approach
```
[Best for methodical players]
Light Ritual (25+ points):
- Primary: torch (10) + crystal (20)
- Alternative: orb (30)

Ancient Text (35+ points):
- Primary: book (15) + scroll (25)
- Alternative: relic (40)

Portal (60+ points):
- Primary: amulet (35) + wand (30)
- Alternative: crown (35) + relic (40)
```

### 💡 Pro Tips
1. **Time Management**
   - Skip combat when possible (use 'run')
   - Don't collect low-value items
   - Use 'search' command early

2. **Alternative Solutions**
   - Any items totaling required points work
   - Single high-value items can replace combinations
   - Keep track of total inventory value

3. **Room Navigation**
   - Use 'back' to retrace steps
   - Remember room connections
   - Plan route based on puzzle needs

---
Created with ❤️ by Akshatha