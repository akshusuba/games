package games.dungeon_puzzle;

public class Score implements Comparable<Score> {
    private String playerName;
    private int score;
    private long timeTaken;

    public Score(String playerName, int score, long timeTaken) {
        this.playerName = playerName;
        this.score = score;
        this.timeTaken = timeTaken;
    }

    public String getPlayerName() { return playerName; }
    public int getScore() { return score; }
    public long getTimeTaken() { return timeTaken; }

    @Override
    public int compareTo(Score other) {
        // Sort by score (descending) first
        if (this.score != other.score) {
            return other.score - this.score;
        }
        // If scores are equal, sort by time (ascending)
        return Long.compare(this.timeTaken, other.timeTaken);
    }

    @Override
    public String toString() {
        return String.format("%-15s Score: %-6d Time: %d min %d sec", 
            playerName, score, timeTaken/60, timeTaken%60);
    }
} 