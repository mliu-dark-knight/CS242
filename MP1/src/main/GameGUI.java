package main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Created by mengxiongliu on 8/29/16.
 */
public class GameGUI {
    private JFrame gameFrame;
    private JPanel gamePanel;
    private JButton gameButtons[][];

    public GameGUI(GameControl game) {
        gameFrame = new JFrame("Chess GameState");
        gameFrame.setSize(512, 512);
        gameFrame.setResizable(true);
        gameFrame.setLocationRelativeTo(null);
        gamePanel = initializePanel();
        gamePanel.setLayout(new GridLayout(9, 8));
        gameButtons = new JButton[8][8];
        initPieces(game);
        initMenuButton(game);
        gameFrame.setContentPane(gamePanel);
        gameFrame.setVisible(true);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public JFrame getGameFrame() {
        return gameFrame;
    }

    private JPanel initializePanel() {
        JPanel myPanel = new JPanel();
        myPanel.setPreferredSize(new Dimension(512,512));
        myPanel.setLayout(new BorderLayout());
        return myPanel;
    }

    /**
     *
     * Initialize all pieces on game board, set event handlers for all buttons
     */
    private void initPieces(GameControl game) {
        for (int y = 0; y < 8; y++)
            for (int x = 0; x < 8; x++) {
                initPieceButton(y, x);
                final int finalY = y;
                final int finalX = x;
                gameButtons[y][x].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        game.pieceButtonHandler(finalY, finalX);
                    }
                });
                drawButton(y, x, game.getIcon(y, x));
            }
    }

    private void initPieceButton(int row, int col) {
        gameButtons[row][col] = new JButton();
        JButton currentButton = gameButtons[row][col];
        if ((row + col) % 2 == 0)
            currentButton.setBackground(Color.white);
        else
            currentButton.setBackground(Color.gray);
        currentButton.setOpaque(true);
        currentButton.setBorderPainted(false);
        gamePanel.add(currentButton);
    }

    public void drawButton(int row, int col, ImageIcon icon) {
        JButton currentButton = gameButtons[row][col];
        currentButton.setIcon(icon);
    }

    public void popupDialog(String message) {
        JOptionPane.showMessageDialog(gameFrame, message);
    }

    public void closeFrame() {
        gameFrame.setVisible(false);
    }

    private void initMenuButton(GameControl game) {
        JButton newButton = new JButton("New");
        newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.newHandler();
            }
        });
        gamePanel.add(newButton);

        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.undoHandler();
            }
        });
        gamePanel.add(undoButton);

        JButton redoButton = new JButton("Redo");
        redoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.redoHandler();
            }
        });
        gamePanel.add(redoButton);

        JButton resignButton = new JButton("Resign");
        resignButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.resignHandler();
            }
        });
        gamePanel.add(resignButton);

    }
}
