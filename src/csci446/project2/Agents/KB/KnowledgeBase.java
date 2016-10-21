package csci446.project2.Agents.KB;

import csci446.project2.Agents.Inference.Clause;
import csci446.project2.Agents.Inference.Predicate;
import csci446.project2.Agents.Inference.Variable;
import csci446.project2.Util.LocationCalc;
import csci446.project2.Util.Orientation;
import csci446.project2.Util.Pair;
import csci446.project2.Util.Action;
import csci446.project2.WumpusWorld.Percept;
import csci446.project2.WumpusWorld.WumpusWorld;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by cetho on 10/11/2016.
 */
public class KnowledgeBase {

    public Cell[][] KBMap;

    public Cell[] KBList;

    public ArrayList<Cell> exploredCells;
    public ArrayList<Cell> unexploredCells;

    public ArrayList<Action> actionsTaken;

    public int locationX;
    public int locationY;

    public Orientation orientation;

    public FactsList facts;


    public KnowledgeBase(WumpusWorld world) {
        //Store Knowledge at "Knowledge Cells" in a location-based array, and a list. Each cell has an ID to be identified for rule application.
        this.KBMap = new Cell[world.worldSize][world.worldSize];
        this.KBList = new Cell[world.worldSize*world.worldSize];

        this.exploredCells = new ArrayList<Cell>();

        this.unexploredCells = new ArrayList<Cell>();
        this.unexploredCells.addAll(Arrays.asList(KBList));

        int k = 0;
        for (int i = 0; i < KBMap.length; i++) {
            for(int j = 0; j < KBMap.length; j++) {
                this.KBMap[i][j] = new Cell(k,i,j);
                this.KBList[this.KBMap[i][j].id] = this.KBMap[i][j];
                k++;
            }
        }

        //Initialize start location
        locationX = world.playerStartX;
        locationY = world.playerStartY;
        //Remove it from the unexploredCells and add it to the explored ones.
        exploredCells.add(KBMap[locationX][locationY]);
        unexploredCells.remove(KBMap[locationX][locationY]);
        //Mark the cell safe. I would argue the game isn't that mean.
        KBMap[locationX][locationY].safe = true;
    }

    public void addPercepts(ArrayList<Percept> percepts) {
        //Must always check for bump first, so that the percepts can be based on the correct location as well as movement and orientation changes.
        if(percepts.contains(Percept.Bump)) {
            /*
            We bumped into something. So we didn't move.
            Mark the cell next in our orientation as an obstacle.
            (Alternatively we crashed into a wall, which shouldn't ever happen given we know where we are relative to the world.)
            */

            Pair<Integer, Integer> bumpedLocation = LocationCalc.NextLocation(locationX, locationY, orientation);
            try {
                KBMap[bumpedLocation.left][bumpedLocation.right].isObstacle = true;
                Cell bumpedCell = KBMap[bumpedLocation.left][bumpedLocation.right];
                facts.add(Clause.fact(Predicate.Obstacle, bumpedCell));
            } catch (IndexOutOfBoundsException exception) {}

        } else if(lastAction() == Action.MoveForward) {

            Pair<Integer, Integer> newLocation = LocationCalc.NextLocation(locationX, locationY, orientation);
            locationX = newLocation.left;
            locationY = newLocation.right;
        } else if(lastAction() == Action.TurnLeft || lastAction() == Action.TurnRight) {
            this.orientation = LocationCalc.NextOrientation(lastAction(), orientation);
        }
        Cell cur = KBMap[locationX][locationY];
        for (Percept percept : percepts) {
            switch (percept) {
                case Breeze:
                    cur.pBreeze = true;
                    facts.add(Clause.fact(Predicate.Breeze, cur));
                    break;
                case Smell:
                    cur.pStench = true;
                    facts.add(Clause.fact(Predicate.Stench, cur));
                    break;
            }
        }

        if(lastAction() == Action.FireArrow && percepts.contains(Percept.Scream)) {
            //Track where the arrow should have hit and remove the wumpus.
            if(orientation == Orientation.North) {
                for(int i = locationY; i < KBMap.length; i++) {
                    if(KBMap[locationX][i].isWumpus) {
                        KBMap[locationX][i].isWumpus = false;
                        facts.add(Clause.fact(Predicate.NotWumpus, KBMap[locationX][i]));
                        break;
                    }
                }
            } else if(orientation == Orientation.East) {
                for(int i = locationY; i < KBMap.length; i++) {
                    if(KBMap[i][locationY].isWumpus) {
                        KBMap[i][locationY].isWumpus = false;
                        facts.add(Clause.fact(Predicate.NotWumpus, KBMap[i][locationY]));
                        break;
                    }
                }
            } else if(orientation == Orientation.South) {
                for(int i = locationY; i >= 0; i--) {
                    if(KBMap[locationX][i].isWumpus) {
                        KBMap[locationX][i].isWumpus = false;
                        facts.add(Clause.fact(Predicate.NotWumpus, KBMap[locationX][i]));
                        break;
                    }
                }
            } else {
                //West
                for(int i = locationY; i >= 0; i--) {
                    if(KBMap[i][locationY].isWumpus) {
                        KBMap[i][locationY].isWumpus = false;
                        facts.add(Clause.fact(Predicate.NotWumpus, KBMap[i][locationY]));
                        break;
                    }
                }
            }
        }

    }

    public Action lastAction() {
        try {
            return actionsTaken.get(actionsTaken.size() - 1);
        }
        catch (NullPointerException e) {}
        return null;
    }

}
