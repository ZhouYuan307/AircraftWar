package edu.hitsz.dao;

public class DAODemo {
    public static void main(String[] args) {
        DAO scoreDao = new ScoreDAOImpl();

        // 添加测试数据
        scoreDao.addScore(new Score("张三", 1500));
        scoreDao.addScore(new Score("李四", 2800));
        scoreDao.addScore(new Score("王五", 3200));
        scoreDao.addScore(new Score("张三", 1900));
        scoreDao.addScore(new Score("赵六", 4100));

        scoreDao.printAllScores();
        System.out.println();
        scoreDao.printLeaderboard(5);

        System.out.println();
        scoreDao.printUserScores("张三");


        System.out.println();
        scoreDao.printAllScores();

        scoreDao.clearAllScores();
        scoreDao.printAllScores();
    }
}
