package games.dungeon_puzzle;

import java.io.*;
import java.util.*;

public class ScoreManager {
    private static String SCORES_FILE;
    private List<Score> highScores;
    private static final int MAX_SCORES = 10;

    public ScoreManager() {
        // Create scores directory if it doesn't exist
        File directory = new File("scores");
        if (!directory.exists()) {
            directory.mkdir();
        }
        
        // Use absolute path for scores file
        SCORES_FILE = directory.getAbsolutePath() + "/dungeon_puzzle_scores.txt";
        highScores = new ArrayList<>();
        loadScores();
    }

    public void addScore(Score newScore) {
        highScores.add(newScore);
        sortScores();
        trimScores();
        saveScores();
    }

    private void sortScores() {
        Collections.sort(highScores);
    }

    private void trimScores() {
        if (highScores.size() > MAX_SCORES) {
            highScores = highScores.subList(0, MAX_SCORES);
        }
    }

    public List<Score> getHighScores() {
        return new ArrayList<>(highScores);
    }

    private void saveScores() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SCORES_FILE))) {
            for (Score score : highScores) {
                writer.println(String.format("%s,%d,%d", 
                    score.getPlayerName(), 
                    score.getScore(), 
                    score.getTimeTaken()));
            }
        } catch (IOException e) {
            System.err.println("Error saving scores: " + e.getMessage());
        }
    }

    private void loadScores() {
        File file = new File(SCORES_FILE);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    highScores.add(new Score(
                        parts[0],
                        Integer.parseInt(parts[1]),
                        Long.parseLong(parts[2])
                    ));
                }
            }
            sortScores();
        } catch (IOException e) {
            System.err.println("Error loading scores: " + e.getMessage());
        }
    }
} 