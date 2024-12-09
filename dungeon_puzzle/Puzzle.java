package games.dungeon_puzzle;

public class Puzzle implements Comparable<Puzzle> {
    private String type;
    private int difficulty;
    private String question;
    private String answer;

    public Puzzle(String type, int difficulty, String question, String answer) {
        this.type = type;
        this.difficulty = difficulty;
        this.question = question;
        this.answer = answer;
    }

    public String getType() { return type; }
    public int getDifficulty() { return difficulty; }
    public String getQuestion() { return question; }
    public String getAnswer() { return answer; }

    @Override
    public int compareTo(Puzzle other) {
        return Integer.compare(this.difficulty, other.difficulty);
    }
} 