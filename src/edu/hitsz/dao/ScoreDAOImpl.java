package edu.hitsz.dao;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class ScoreDAOImpl implements DAO{

    private final List<Score> scores;
    private final String FILE_NAME = "game_scores.csv";
    private int nextId = 1;

    public ScoreDAOImpl() {
        scores = loadScoresFromFile();
        // 更新下一个可用的ID
        if (!scores.isEmpty()) {
            nextId = scores.stream().mapToInt(Score::getId).max().getAsInt() + 1;
        }
    }

    private List<Score> loadScoresFromFile() {
        List<Score> scoreList = new ArrayList<>();
        File file = new File(FILE_NAME);

        // 如果文件不存在，直接返回空列表
        if (!file.exists()) {
            return scoreList;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                // 跳过CSV文件头（如果存在）
                if (isFirstLine && (line.startsWith("id") || line.startsWith("序号"))) {
                    isFirstLine = false;
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    try {
                        int id = Integer.parseInt(parts[0].trim());
                        String username = parts[1].trim();
                        int score = Integer.parseInt(parts[2].trim());
                        String time = parts[3].trim();

                        scoreList.add(new Score(id, username, score, time));
                    } catch (NumberFormatException e) {
                        System.out.println("数据格式错误，跳过此行: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("加载得分记录失败: " + e.getMessage());
        }

        return scoreList;
    }


    private void saveScoresToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            // 写入CSV文件头
            writer.write("id,username,score,time");
            writer.newLine();

            // 写入数据
            for (Score score : scores) {
                writer.write(score.getId() + "," +
                        score.getUsername() + "," +
                        score.getScore() + "," +
                        score.getTime());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("保存得分记录失败: " + e.getMessage());
        }
    }

    @Override
    public void addScore(Score score) {
        // 设置ID并递增
        score.setId(nextId++);
        scores.add(score);
        saveScoresToFile();
        System.out.println("添加得分记录: " + score);
    }

    @Override
    public List<Score> getAllScores() {
        return new ArrayList<>(scores);
    }

    @Override
    public void printAllScores() {
        if (scores.isEmpty()) {
            System.out.println("暂无得分记录");
            return;
        }

        System.out.println("------------------------------------------------");
        System.out.println("=== 所有得分记录 (" + scores.size() + "条) ===");


        List<Score> sortedScores = scores.stream()
                .sorted((s1, s2) -> Integer.compare(s2.getScore(), s1.getScore()))
                .collect(Collectors.toList());

        for (Score score : sortedScores) {
            score.info();
        }
        System.out.println("------------------------------------------------");
    }

    @Override
    public List<Score> getTopScores(int topN) {
        return scores.stream()
                .sorted((s1, s2) -> Integer.compare(s2.getScore(), s1.getScore())) // 降序排序
                .limit(topN)
                .collect(Collectors.toList());
    }

    @Override
    public void printLeaderboard(int topN) {
        List<Score> topScores = getTopScores(topN);

        if (topScores.isEmpty()) {
            System.out.println("暂无得分记录");
            return;
        }

        System.out.println("------------------------------------------------");
        System.out.println("=== 排行榜===");

        for (int i = 0; i < topScores.size(); i++) {
            Score score = topScores.get(i);
            System.out.println("第"+(i+1)+"名");
            score.info();
        }
        System.out.println("------------------------------------------------");
    }

    @Override
    public List<Score> getScoresByUsername(String username) {
        return scores.stream()
                .filter(score -> score.getUsername().equalsIgnoreCase(username))
                .sorted((s1, s2) -> Integer.compare(s2.getScore(), s1.getScore()))
                .collect(Collectors.toList());
    }

    @Override
    public void printUserScores(String username) {
        List<Score> userScores = getScoresByUsername(username);

        if (userScores.isEmpty()) {
            System.out.println("玩家 '" + username + "' 暂无得分记录");
            return;
        }

        int personalBest = userScores.stream()
                .mapToInt(Score::getScore)
                .max()
                .orElse(0);
        System.out.println("------------------------------------------------");
        System.out.println("=== 玩家 '" + username + "' 的得分记录 ===");
        System.out.println("个人最佳: " + personalBest + "分");
        System.out.println("总游戏次数: " + userScores.size() + "次");



        for (Score score : userScores) {
            score.info();
        }
        System.out.println("------------------------------------------------");
    }

    @Override
    public void clearAllScores() {
        scores.clear();
        nextId = 1;
        saveScoresToFile();
        System.out.println("已清空所有得分记录");
    }


    public int getPersonalBest(String username) {
        return scores.stream()
                .filter(score -> score.getUsername().equalsIgnoreCase(username))
                .mapToInt(Score::getScore)
                .max()
                .orElse(0);
    }

}
