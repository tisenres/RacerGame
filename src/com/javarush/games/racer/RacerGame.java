package com.javarush.games.racer;

import com.javarush.engine.cell.Color;
import com.javarush.engine.cell.Game;
import com.javarush.engine.cell.Key;
import com.javarush.games.racer.road.RoadManager;

public class RacerGame extends Game {
    public static final int WIDTH = 64;
    public static final int HEIGHT = 64;
    public static final int CENTER_X = WIDTH / 2;
    public static final int ROADSIDE_WIDTH = 14;
    private RoadMarking roadMarking;
    private PlayerCar player;
    private RoadManager roadManager;
    private boolean isGameStopped;
    private FinishLine finishLine;
    private static final int RACE_GOAL_CARS_COUNT = 40;
    private ProgressBar progressBar;
    private int score;


    @Override
    public void initialize() {
        showGrid(false);
        setScreenSize(WIDTH, HEIGHT);
        createGame();
    }

    private void createGame() {
        roadMarking = new RoadMarking();
        player = new PlayerCar();
        roadManager = new RoadManager();
        finishLine = new FinishLine();
        progressBar = new ProgressBar(RACE_GOAL_CARS_COUNT);
        drawScene();
        setTurnTimer(40);
        isGameStopped = false;
        score = 3500;
    }

    private void drawScene() {
        drawField();
        progressBar.draw(this);
        finishLine.draw(this);
        roadManager.draw(this);
        roadMarking.draw(this);
        player.draw(this);

    }

    private void moveAll() {
        roadMarking.move(player.speed);
        player.move();
        roadManager.move(player.speed);
        finishLine.move(player.speed);
        progressBar.move(roadManager.getPassedCarsCount());
    }

    @Override
    public void onKeyPress(Key key) {
        if (key == Key.LEFT) {
            player.setDirection(Direction.LEFT);
        } else if (key == Key.RIGHT) {
            player.setDirection(Direction.RIGHT);
        } else if (key == Key.UNKNOWN){
            player.setDirection(Direction.NONE);
        } else if (key == Key.SPACE && isGameStopped) {
            createGame();
        } else if (key == Key.UP) {
            player.speed = 2;
        }
    }

    @Override
    public void onTurn(int sleep) {
        if (roadManager.checkCrush(player)) {
            gameOver();
            drawScene();
            return;
        }
        roadManager.generateNewRoadObjects(this);
        if (roadManager.getPassedCarsCount() >= RACE_GOAL_CARS_COUNT) {
            finishLine.show();
        }
        if (finishLine.isCrossed(player)) {
            win();
            drawScene();
            return;
        }
        moveAll();
        score -= 5;
        setScore(score);
        drawScene();
    }

    @Override
    public void setCellColor(int x, int y, Color color) {
        try {
            if (x < WIDTH && y < HEIGHT) {
                super.setCellColor(x, y, color);
            }
        } catch (Exception e) {

        }
        
    }

    private void drawField() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if ((x >= 0 && x < ROADSIDE_WIDTH) || (x >= WIDTH - ROADSIDE_WIDTH)) {
                    setCellColor(x, y, Color.GREEN);
                }
                if (x != CENTER_X && x >= ROADSIDE_WIDTH && x < WIDTH - ROADSIDE_WIDTH) {
                    setCellColor(x, y, Color.DIMGRAY);
                }
                if (x == CENTER_X) {
                    setCellColor(x, y, Color.WHITE);
                }
            }
        }
    }

    @Override
    public void onKeyReleased(Key key) {
        if (key == Key.RIGHT && player.getDirection() == Direction.RIGHT) {
            player.setDirection(Direction.NONE);
        } else if (key == Key.LEFT && player.getDirection() == Direction.LEFT) {
            player.setDirection(Direction.NONE);
        } if (key == Key.UP) {
            player.speed = 1;
        }
    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.WHITE, "GAME OVER", Color.RED, 70);
        stopTurnTimer();
        player.stop();
    }

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.YELLOW, "YOU WIN", Color.RED, 70);
        stopTurnTimer();
    }
    
    @Override
    public void setScore(int score) {
        this.score = score;
    }
}
