package gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.hitsz.application.*;
public class MenuGUI {
    private JPanel mainPanel;
    private JButton easyButton;
    private JButton normalButton;
    private JButton hardButton;
    private JComboBox comboBox1;
    private JTextField getName;
    private JButton ensureButton;
    private String userName;
    private boolean nameCheck;

    public MenuGUI(){



        easyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(nameCheck){
                    nameCheck = false;
                    Game game = new GameEasy(comboBox1.getSelectedItem().toString(), userName);
                    game.setBackground(ImageManager.BACKGROUND_IMAGE_EASY);
                    Main.cardPanel.add(game);
                    Main.cardLayout.last(Main.cardPanel);
                    game.action();
                }else{
                    JOptionPane.showMessageDialog(mainPanel,
                            "Please enter your name or click OK!",
                            "Oops",
                            JOptionPane.WARNING_MESSAGE);
                }

            }
        });

        normalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(nameCheck){
                    nameCheck = false;
                    Game game = new GameNormal(comboBox1.getSelectedItem().toString(), userName);
                    game.setBackground(ImageManager.BACKGROUND_IMAGE_NORMAL);
                    Main.cardPanel.add(game);
                    Main.cardLayout.last(Main.cardPanel);
                    game.action();
                }else{
                    JOptionPane.showMessageDialog(mainPanel,
                            "Please enter your name or click OK!",
                            "Oops",
                            JOptionPane.WARNING_MESSAGE);
                }

            }
        });


        hardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(nameCheck){
                    nameCheck = false;
                    Game game = new GameHard(comboBox1.getSelectedItem().toString(), userName);
                    game.setBackground(ImageManager.BACKGROUND_IMAGE_HARD);
                    Main.cardPanel.add(game);
                    Main.cardLayout.last(Main.cardPanel);
                    game.action();
                }else{
                    JOptionPane.showMessageDialog(mainPanel,
                            "Please enter your name or click OK!",
                            "Oops",
                            JOptionPane.WARNING_MESSAGE);
                }

            }
        });


        ensureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userName = getName.getText().trim();
                if (userName.isEmpty()){
                    JOptionPane.showMessageDialog(null,
                            "Please enter your name and click OK!",
                            "Oops",
                            JOptionPane.ERROR_MESSAGE);
                    nameCheck = false;
                    return;
                }
                if (userName.length() > 10) {
                    JOptionPane.showMessageDialog(mainPanel,
                            "Your name is too long!",
                            "Oops",
                            JOptionPane.WARNING_MESSAGE);
                    nameCheck = false;
                    return;
                }
                nameCheck = true;
                JOptionPane.showMessageDialog(mainPanel,
                        "Your name is set",
                        "info",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }
    public JPanel getMainPanel() {
        return mainPanel;
    }


}
