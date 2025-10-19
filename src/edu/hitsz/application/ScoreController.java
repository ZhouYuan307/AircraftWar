package edu.hitsz.application;

import edu.hitsz.dao.Score;
import edu.hitsz.dao.ScoreDAOImpl;
import gui.ScoreGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ScoreController {
    private final ScoreGUI scoreGUI;
    private final ScoreDAOImpl scoreDAO;
    private String currentPlayerName;
    private int currentPlayerScore;

    public ScoreController() {
        this.scoreGUI = new ScoreGUI();
        this.scoreDAO = new ScoreDAOImpl();

        initializeController();
        loadScoresToGUI();
    }

    public ScoreController(String playerName, int score) {
        this();
        this.currentPlayerName = playerName;
        this.currentPlayerScore = score;

        // 自动添加当前游戏分数
        addCurrentScore();
    }

    private void initializeController() {
        // 删除按钮事件
        scoreGUI.addDeleteButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedScore();
            }
        });

        scoreGUI.addPlayAgainButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnToMainMenu();
            }
        });
    }

    private void addCurrentScore() {
        if (currentPlayerName != null && !currentPlayerName.trim().isEmpty()) {
            Score newScore = new Score(currentPlayerName, currentPlayerScore);
            scoreDAO.addScore(newScore);
            loadScoresToGUI(); // 刷新显示
        }
    }



    private void loadScoresToGUI() {
        // 获取所有分数并按降序排列
        List<Score> allScores = scoreDAO.getAllScores();

        List<Score> sortedScores = allScores.stream()
                .sorted((s1, s2) -> Integer.compare(s2.getScore(), s1.getScore()))
                .collect(Collectors.toList());
        scoreGUI.updateTable(sortedScores);
    }

    private void deleteSelectedScore() {
        Integer selectedId = scoreGUI.getSelectedScoreId();

        if (selectedId == null) {
            JOptionPane.showMessageDialog(scoreGUI.getMainPanel(),
                    "请先选择要删除的记录！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(scoreGUI.getMainPanel(),
                "确定要删除这条记录吗？", "确认删除", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            deleteScoreById(selectedId);
            loadScoresToGUI();
            JOptionPane.showMessageDialog(scoreGUI.getMainPanel(),
                    "删除成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void deleteScoreById(int id) {
        List<Score> allScores = scoreDAO.getAllScores();
        List<Score> updatedScores = new ArrayList<>();

        for (Score score : allScores) {
            if (score.getId() != id) {
                updatedScores.add(score);
            }
        }

        // 清空并重新添加
        scoreDAO.clearAllScores();
        for (Score score : updatedScores) {
            // 创建新对象避免ID冲突
            Score newScore = new Score(score.getUsername(), score.getScore());
            newScore.setTime(score.getTime());
            scoreDAO.addScore(newScore);
        }
    }

    private void returnToMainMenu() {
        //回到主菜单
        try {
            // 获取cardPanel并切换到第一个面板（主菜单）
            CardLayout layout = Main.cardLayout;
            JPanel cardPanel = Main.cardPanel;
            layout.first(cardPanel);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(scoreGUI.getMainPanel(),
                    "返回主菜单失败", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public ScoreGUI getScoreGUI() {
        return scoreGUI;
    }
}