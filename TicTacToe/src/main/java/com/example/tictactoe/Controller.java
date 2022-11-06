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
    private static final String PLAYERX = "Spieler X ist Dran";
    private static final String PLAYERO = "Spieler O ist Dran";
    private static final String X = "X";
    private static final String O = "O";
    private static final String PLAYERXWIN = "Spieler X hat gewonnen";
    private static final String PLAYEROWIN = "Spieler O hat gewonnen";
    private static final String DRAW = "Unentschieden";
    private static final String CLEAR = "";
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
    // --- Game start --- //
    @FXML
    private void buttonStartClicked() {
        newGame();
        whoMakeFirstTurn();
        play();
    }
    // --- Preparing new game --- //
    private void newGame() {
        buttons = new ArrayList<>(Arrays.asList(button1, button2, button3, button4, button5, button6, button7, button8, button9));
        buttons.forEach(button -> {
            button.setDisable(false);
            button.setText(CLEAR);
        });
        checkBoxVsComputer.setDisable(true);
        radioButtonHard.setDisable(true);
        radioButtonEasy.setDisable(true);
    }
    // --- End Game --- //
    private void endGame() {
        buttons.forEach(button -> button.setDisable(true));
        checkBoxVsComputer.setDisable(false);
        radioButtonDisable();
    }
    // --- Checking game mode --- //
    private void play() {
        if (checkBoxVsComputer.isSelected()) {
            setPlayerX();
            buttons.forEach(button ->
                    playerMoveAiMove(button));
        } else
            buttons.forEach(button ->
                    playerMove(button));
    }

    // <<<--->>> Game mode player vs player , player vs computer (easy) , player vs computer (hard) <<<--->>> //
    // --- Player vs Player --- //
    private void playerMove(Button button) {
        button.setOnMouseClicked(MouseEvent -> {
            if (xTurn) {
                button.setText(X);
                button.setDisable(true);
                setPlayerO();
            } else {
                button.setText(O);
                button.setDisable(true);
                setPlayerX();
            }
            anybodyWin();
        });
    }
    // --- Player vs Computer (easy) or (hard) --- //
    private void playerMoveAiMove(Button button) {
        button.setOnMouseClicked(MouseEvent -> {
            if (xTurn) {
                button.setText(X);
                button.setDisable(true);
                setPlayerO();
                anybodyWin();
                if (radioButtonEasy.isSelected()) easyAiMove();
                if (radioButtonHard.isSelected()) hardAiMove();
                anybodyWin();
            }
        });
    }
    // <<<--->>>  <<<--->>> //

    // <<<--->>> AI Logic <<<--->>> //
    // --- Game mode "easy". Pick a random move and when game state is --- //
    // --- X X/ XX/XX / pick winning move --- //
    private void easyAiMove() {
        easyAiCheck();
        if (!label.getText().equals(PLAYERX)) {
            int aiMove = random.nextInt(8);
            if (buttons.get(aiMove).getText().equals(CLEAR)) {
                buttons.get(aiMove).setText(O);
                buttons.get(aiMove).setDisable(true);
                setPlayerX();
            } else {
                easyAiMove();
            }
        }
    }
    // --- Check game state for X X/XX / XX/ situation and pick winning move --- //
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
                    if (button.getText().equals(CLEAR)) {
                        button.setText(O);
                        button.setDisable(true);
                        setPlayerX();
                    }
                });
            }
        }
    }
    // --- Game mode "hard". Pick always the best move --- //
    private void hardAiMove() {
        int bestScore = -10;
        int move = 0;
        for (int i = 0; i < 9; i++) {
            if (buttons.get(i).getText().equals(CLEAR)) {
                buttons.get(i).setText(O);
                int score = minimax(buttons, false);
                buttons.get(i).setText(CLEAR);
                if (score > bestScore) {
                    bestScore = score;
                    move = i;
                }
            }
        }
        buttons.get(move).setText(O);
        buttons.get(move).setDisable(true);
        setPlayerX();
    }
    // --- Minimax algorithms implementation --- //
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
                if (buttons.get(i).getText().equals(CLEAR)) {
                    buttons.get(i).setText(O);
                    int score = minimax(buttons, false);
                    buttons.get(i).setText(CLEAR);
                    bestScore = Math.max(score, bestScore);
                }
            }
            return bestScore;
        }else{
            int bestScore = 10;
            for(int i = 0; i < 9; i++){
                if(buttons.get(i).getText().equals(CLEAR)){
                    buttons.get(i).setText(X);
                    int score = minimax(buttons, true);
                    buttons.get(i).setText(CLEAR);
                    bestScore = Math.min(score, bestScore);
                }
            }
            return bestScore;
        }
    }
    // <<<--->>>  <<<--->>> //

    // <<<--->>> Check game state Win/Draw <<<--->>> //
    // --- Checking if someone won and end the game --- //
    private void anybodyWin() {
        for (int i = 0; i < 8; i++) {
            String win = checkWin(i);
            if (win.equals("XXX")) {
                label.setText(PLAYERXWIN);
                endGame();
            }
            if (win.equals("OOO")) {
                label.setText(PLAYEROWIN);
                endGame();
            }
        }
        // --- Draw check and last move draw check (when last move give us win don't call draw) --- //
        if (draw()) {
            if (!label.getText().equals(PLAYERXWIN) && !label.getText().equals(PLAYEROWIN)) {
                label.setText(DRAW);
                endGame();
            }
        }

    }
    // --- Checking game state  --- //
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
    // --- Check draw and return True/False --- //
    private boolean draw(){
        int howManyTurn = 0;
        for(int i = 0; i < 9; i++){
            if(!buttons.get(i).getText().equals(CLEAR)){
                howManyTurn++;
            }
        }
        return howManyTurn == 9;
    }
    // <<<--->>>  <<<--->>> //

    // --- Set player O --- //
    private  void setPlayerO(){
        xTurn = false;
        label.setText(PLAYERO);
    }
    // --- Set player X --- //
    private void setPlayerX(){
        xTurn = true;
        label.setText(PLAYERX);
    }
    // --- Picking a random player --- //
    private void whoMakeFirstTurn() {
        if (random.nextInt(2) == 0) {
            setPlayerX();
        } else {
            setPlayerO();
        }
        start.setText("Neu Start");
    }
    // --- Radio Button --- //
    private void radioButtonDisable(){
        if (checkBoxVsComputer.isSelected()) {
            radioButtonHard.setDisable(false);
            radioButtonEasy.setDisable(false);
        } else {
            radioButtonHard.setDisable(true);
            radioButtonEasy.setDisable(true);
        }
    }
}