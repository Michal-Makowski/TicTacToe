package com.example.tictactoe;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Controller {

    private boolean xTurn;

    private Random random = new Random();

    private ArrayList<Button> buttons;

    @FXML
    private Label label;

    @FXML
    private Button button1, button2, button3, button4, button5, button6, button7, button8, button9, start;

    @FXML
    private CheckBox checkBoxVsComputer;
    @FXML
    private RadioButton radioButtonEasy, radioButtonHard;

    @FXML
    private void vsComputer(ActionEvent event) {
        radioButtonDisable();
    }

    // Game Start //
    @FXML
    private void buttonStartClicked() {
        newGame();
        whoMakeFirstTurn();
        play();
    }

    // New game preparing //
    private void newGame() {
        buttons = new ArrayList<>(Arrays.asList(button1, button2, button3, button4, button5, button6, button7, button8, button9));
        buttons.forEach(button -> {
            button.setDisable(false);
            button.setText("");
        });
        checkBoxVsComputer.setDisable(true);
        radioButtonHard.setDisable(true);
        radioButtonEasy.setDisable(true);
    }

    // Picking a random player //
    private void whoMakeFirstTurn() {
        if (random.nextInt(2) == 0) {
            xTurn = true;
            label.setText("Spieler X ist Dran");
        } else {
            xTurn = false;
            label.setText("Spieler O ist Dran");
        }
        start.setText("Neu Start");
    }




    // Game logic //
    private void play() {
        if (checkBoxVsComputer.isSelected()) {
            xTurn = true;
            label.setText("Spieler X ist Dran");
            buttons.forEach(button ->
                    playerMoveAiMove(button));
        } else
            buttons.forEach(button ->
                    playerMove(button));
    }

    // End game //
    private void endGame() {
        buttons.forEach(button -> button.setDisable(true));
        checkBoxVsComputer.setDisable(false);
        radioButtonDisable();
    }
    // Game mode player vs player , player vs computer (easy) , player vs computer (hard) //
    //player vs player //
    private void playerMove(Button button) {
        button.setOnMouseClicked(MouseEvent -> {
            if (xTurn) {
                button.setText("X");
                button.setDisable(true);
                xTurn = false;
                label.setText("Spieler O ist Dran");
            } else {
                button.setText("O");
                button.setDisable(true);
                xTurn = true;
                label.setText("Spieler X ist Dran");
            }
            anybodyWin();
        });
    }

    // player vs computer (easy) or (hard)//
    private void playerMoveAiMove(Button button) {
        button.setOnMouseClicked(MouseEvent -> {
            if (xTurn) {
                button.setText("X");
                button.setDisable(true);
                xTurn = false;
                label.setText("Spieler O ist Dran");
                anybodyWin();
                if (radioButtonEasy.isSelected()) easyAiMove();
                if (radioButtonHard.isSelected()) hardAiMove();
                anybodyWin();
            }
        });
    }



    // computer easy logic //
    private void easyAiMove() {
        easyAiCheck();
        if (!label.getText().equals("Spieler X ist Dran")) {
            int aiMove = random.nextInt(8);
            if (buttons.get(aiMove).getText().equals("")) {
                buttons.get(aiMove).setText("O");
                buttons.get(aiMove).setDisable(true);
                xTurn = true;
                label.setText("Spieler X ist Dran");
            } else {
                easyAiMove();
            }
        }
    }

    private void easyAiCheck() {
        ArrayList<Button> easyCheck = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            String win = checkWin(i);

            if (win.equals("OO")) {
                switch (i) {
                    case 0 -> {
                        easyCheck.add(button1); easyCheck.add(button2); easyCheck.add(button3);
                    }
                    case 1 -> {
                        easyCheck.add(button4); easyCheck.add(button5); easyCheck.add(button6);
                    }
                    case 2 -> {
                        easyCheck.add(button7); easyCheck.add(button8); easyCheck.add(button9);
                    }
                    case 3 -> {
                        easyCheck.add(button1); easyCheck.add(button4); easyCheck.add(button7);
                    }
                    case 4 -> {
                        easyCheck.add(button2); easyCheck.add(button5); easyCheck.add(button8);
                    }
                    case 5 -> {
                        easyCheck.add(button3); easyCheck.add(button6); easyCheck.add(button9);
                    }
                    case 6 -> {
                        easyCheck.add(button1); easyCheck.add(button5); easyCheck.add(button9);
                    }
                    case 7 -> {
                        easyCheck.add(button3); easyCheck.add(button5); easyCheck.add(button7);
                    }

                }
                easyCheck.forEach(button -> {
                    if (button.getText().equals("")) {
                        button.setText("O");
                        button.setDisable(true);
                        xTurn = true;
                        label.setText("Spieler X ist Dran");
                    }
                });
            }
        }

    }
    // computer hard logic (Minimax Algorithm)//
    private void hardAiMove() {
        int bestScore = -10;
        int move = 0;
        for (int i = 0; i < 9; i++) {
            if (buttons.get(i).getText().equals("")) {
                buttons.get(i).setText("O");
                int score = minimax(buttons, false);
                buttons.get(i).setText("");
                if (score > bestScore) {
                    bestScore = score;
                    move = i;
                }
            }
        }
        buttons.get(move).setText("O");
        buttons.get(move).setDisable(true);
        xTurn = true;
        label.setText("Spieler X ist Dran");
    }

    private int minimax(ArrayList<Button> buttons, boolean isMaximizing) {
        for (int i = 0; i < 8; i++) {
            String win = checkWin(i);
            if (win.equals("XXX")) return -1;
            if (win.equals("OOO")) return 1;
        }
        if(draw()) return 0;
        if (isMaximizing) {
            int bestScore = -10;
            for (int i = 0; i < 9; i++) {
                if (buttons.get(i).getText().equals("")) {
                    buttons.get(i).setText("O");
                    int score = minimax(buttons, false);
                    buttons.get(i).setText("");
                    bestScore = Math.max(score, bestScore);
                }
            }
            return bestScore;
        }else{
            int bestScore = 10;
            for(int i = 0; i < 9; i++){
                if(buttons.get(i).getText().equals("")){
                    buttons.get(i).setText("X");
                    int score = minimax(buttons, true);
                    buttons.get(i).setText("");
                    bestScore = Math.min(score, bestScore);
                    }
                }
            return bestScore;
            }
        }

    private void radioButtonDisable(){
        if (checkBoxVsComputer.isSelected()) {
            radioButtonHard.setDisable(false);
            radioButtonEasy.setDisable(false);
        } else {
            radioButtonHard.setDisable(true);
            radioButtonEasy.setDisable(true);
        }
    }
    private String checkWin(int i){
        String win = switch (i) {
            case 0 -> button1.getText() + button2.getText() + button3.getText();
            case 1 -> button4.getText() + button5.getText() + button6.getText();
            case 2 -> button7.getText() + button8.getText() + button9.getText();
            case 3 -> button1.getText() + button4.getText() + button7.getText();
            case 4 -> button2.getText() + button5.getText() + button8.getText();
            case 5 -> button3.getText() + button6.getText() + button9.getText();
            case 6 -> button1.getText() + button5.getText() + button9.getText();
            case 7 -> button3.getText() + button5.getText() + button7.getText();
            default -> null;
        };
        return win;
    }
    private boolean draw(){
        int howManyTurn = 0;
        for(int i = 0; i < 9; i++){
            if(!buttons.get(i).getText().equals("")){
                howManyTurn++;
            }
        }
        return howManyTurn == 9;
    }

    // Checking if someone won //
    private void anybodyWin() {

        for (int i = 0; i < 8; i++) {
            String win = checkWin(i);

            if (win.equals("XXX")) {
                label.setText("Spieler X hat gewonnen");
                endGame();
            }
            if (win.equals("OOO")) {
                label.setText("Spieler O hat gewonnen");
                endGame();
            }
        }
        // Draw check //
        if (draw()) {
            if (!label.getText().equals("Spieler X hat gewonnen") && !label.getText().equals("Spieler O hat gewonnen")) {
                label.setText("Unentschieden");
                endGame();
            }
        }

    }
    }


