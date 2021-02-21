package com.javarush.games.racer.road;

import com.javarush.engine.cell.Game;
import com.javarush.games.racer.GameObject;
import com.javarush.games.racer.PlayerCar;
import com.javarush.games.racer.RacerGame;

import java.util.ArrayList;
import java.util.List;

public class RoadManager {
    public static final int LEFT_BORDER = RacerGame.ROADSIDE_WIDTH;
    public static final int RIGHT_BORDER = RacerGame.WIDTH - LEFT_BORDER;
    private static final int FIRST_LANE_POSITION = 16;
    private static final int FOURTH_LANE_POSITION = 44;
    private static final int PLAYER_CAR_DISTANCE = 12;
    private int passedCarsCount = 0;

    private List<RoadObject> items = new ArrayList<>();

    private RoadObject createRoadObject(RoadObjectType type, int x, int y) {
        if (type == RoadObjectType.THORN) {
            return new Thorn(x, y);
        } if (type == RoadObjectType.DRUNK_CAR) {
            return new MovingCar(x, y);
        } else {
            return new Car(type, x, y);
        }
    }

    private void addRoadObject(RoadObjectType type, Game game) {
        int x = game.getRandomNumber(FIRST_LANE_POSITION, FOURTH_LANE_POSITION);
        int y = -1 * RoadObject.getHeight(type);
        RoadObject roadObject = createRoadObject(type, x, y);
        if ((roadObject != null) && isRoadSpaceFree(roadObject)) {
            items.add(createRoadObject(type, x, y));
        }
    }

    public void draw(Game game) {
        for (int i = 0; i < items.size(); i++) {
            items.get(i).draw(game);
        }
    }

    public void move(int boost) {
        for (int i = 0; i < items.size(); i++) {
            items.get(i).move(items.get(i).speed + boost, items);
        }
        deletePassedItems();
    }

    private boolean isThornExists() {
        boolean isThornExists = false;
        for (RoadObject element : items) {
            if (element.type == RoadObjectType.THORN) {
                isThornExists = true;
            }
        }
        return isThornExists;
    }

    private void generateThorn(Game game) {
        int number = game.getRandomNumber(100);

        if (number < 10 && !isThornExists()) {
            addRoadObject(RoadObjectType.THORN, game);
        }
    }

    public void generateNewRoadObjects(Game game) {
        generateRegularCar(game);
        generateThorn(game);
        generateMovingCar(game);
    }

    private void deletePassedItems() {
        for (RoadObject item : items) {
            if (item.y >= RacerGame.HEIGHT && !(item instanceof Thorn)) {
                passedCarsCount += 1;
            }
        }
        items.removeIf(roadObject -> roadObject.y >= RacerGame.HEIGHT);
    }

    public boolean checkCrush(PlayerCar playerCar) {
        boolean crush = false;
        for (GameObject gameObject : items) {
            if (gameObject.isCollision(playerCar)) {
                crush = true;
                break;
            }
        }
        return crush;
    }

    private void generateRegularCar(Game game) {
        int carTypeNumber = game.getRandomNumber(4);
        if (game.getRandomNumber(100) < 30) {
            addRoadObject(RoadObjectType.values()[carTypeNumber], game);
        }
    }

    private boolean isRoadSpaceFree(RoadObject object) {
        boolean isNormalDisatance = true;
        for (RoadObject item : items) {
            if (item.isCollisionWithDistance(object, PLAYER_CAR_DISTANCE)) {
                isNormalDisatance = false;
                break;
            }
        }
        return isNormalDisatance;
    }

    private boolean isMovingCarExists() {
        for (RoadObject item : items) {
            if (item instanceof MovingCar) {
                return true;
            }
        }
        return false;
        
    }

    private void generateMovingCar(Game game) {
        if (game.getRandomNumber(100) < 10 && !isMovingCarExists()) {
            addRoadObject(RoadObjectType.DRUNK_CAR, game);
        }
    }

    public int getPassedCarsCount() {
        return passedCarsCount;
    }

}
