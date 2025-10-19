package gui;

import edu.hitsz.dao.Score;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.util.List;

public class ScoreGUI {
    private JPanel mainPanel;
    private JPanel topPanel;
    private JPanel bottomPanel;
    private JLabel headerLabel;
    private JScrollPane tableScrollPanel;
    private JButton deleteButton;
    private JButton returnToMenu; // 现在作为返回主菜单按钮
    private JTable scoreTable;
    private DefaultTableModel tableModel;

    public ScoreGUI() {
        initializeTable();
    }

    private void initializeTable() {
        // 创建表格模型
        String[] columnNames = {"ID", "用户名", "得分", "时间"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 所有单元格不可编辑
            }
        };

        scoreTable = new JTable(tableModel);
        scoreTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scoreTable.getTableHeader().setReorderingAllowed(false);

        if (tableScrollPanel != null) {
            tableScrollPanel.setViewportView(scoreTable);
        }
    }

    // 更新表格数据
    public void updateTable(List<Score> scores) {
        tableModel.setRowCount(0); // 清空现有数据

        for (Score score : scores) {
            Object[] rowData = {
                    score.getId(),
                    score.getUsername(),
                    score.getScore(),
                    score.getTime()
            };
            tableModel.addRow(rowData);
        }
    }

    // 获取选中的行ID
    public Integer getSelectedScoreId() {
        int selectedRow = scoreTable.getSelectedRow();
        if (selectedRow >= 0) {
            return (Integer) tableModel.getValueAt(selectedRow, 0);
        }
        return null;
    }

    // 添加事件监听器
    public void addDeleteButtonListener(ActionListener listener) {
        deleteButton.addActionListener(listener);
    }

    public void addPlayAgainButtonListener(ActionListener listener) {
        returnToMenu.addActionListener(listener);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public JTable getScoreTable() {
        return scoreTable;
    }
}
