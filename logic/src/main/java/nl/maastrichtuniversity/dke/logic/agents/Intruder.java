package nl.maastrichtuniversity.dke.logic.agents;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import nl.maastrichtuniversity.dke.logic.agents.modules.communication.CommunicationMark;
import nl.maastrichtuniversity.dke.logic.agents.modules.communication.CommunicationType;
import nl.maastrichtuniversity.dke.logic.scenario.environment.Tile;
import nl.maastrichtuniversity.dke.logic.scenario.environment.TileType;
import nl.maastrichtuniversity.dke.logic.scenario.util.Position;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class Intruder extends Agent {

    private @Getter
    @Setter
    boolean isCaught;
    boolean alive;
    private boolean navigatedToBlueMark; // whether the agent has navigated to the mark that another agent dropped
    private boolean droppedBlueMark; // did it drop the mark already

    public Intruder() {
        super();
        this.isCaught = false;
        this.alive = true;
        this.navigatedToBlueMark = false;
        this.droppedBlueMark = false;

    }

    @Override
    public void update() {
        if (this.isCaught) {
            return;
        }

        if (seesGuard()) {
            avoidGuards();
        } else if (seesTarget()) {
            if (!droppedBlueMark) {
                super.dropMark(CommunicationType.VISION_BLUE);
                this.droppedBlueMark = true;
            }
            navigateToTarget();
        } else if (seesBlueMark() && !navigatedToBlueMark) {
            this.navigatedToBlueMark = true;
            navigateToBlueMark();
        } else {
            super.explore();
        }


        super.update();
    }

    private void navigateToTarget() {
        Tile target = getTarget();
        Position targetPosition = target.getPosition();
        moveToLocation(targetPosition);
    }

    private boolean seesTarget() {
        List<Tile> obstacles = super.getVisionModule().getVisibleTiles();

        return containsTarget(obstacles);
    }

    private void avoidGuards() {
        /* run away from the seen guard */
        List<Guard> visibleGuards = getVisibleGuards();
        Position toGuard = getPathFinderModule().getShortestPath(getPosition(),
                visibleGuards.get(0).getPosition()).get(0);

        if (toGuard.getX() != getPosition().getX()) {
            if (toGuard.getX() < getPosition().getX()) {
                Position avoid = new Position(getPosition().getX() + 1, getPosition().getY());
                moveToLocation(avoid);
            } else {
                Position avoid = new Position(getPosition().getX() - 1, getPosition().getY());
                moveToLocation(avoid);
            }
        } else {
            if (toGuard.getY() < getPosition().getY()) {
                Position avoid = new Position(getPosition().getX(), getPosition().getY() + 1);
                moveToLocation(avoid);
            } else {
                Position avoid = new Position(getPosition().getX(), getPosition().getY() - 1);
                moveToLocation(avoid);
            }
        }
    }

    private boolean seesGuard() {
        return getVisibleGuards().size() > 0;
    }

    private List<Guard> getVisibleGuards() {
        List<Agent> visibleAgents = super.getVisibleAgents();
        List<Agent> visibleGuards = filterGuards(visibleAgents);

        return castAgentsToGuards(visibleGuards);
    }

    private List<Agent> filterGuards(List<Agent> agents) {
        return agents.stream()
                .filter(agent -> agent instanceof Guard)
                .collect(Collectors.toList());
    }

    private List<Guard> castAgentsToGuards(List<Agent> agents) {
        return agents.stream()
                .map(agent -> (Guard) agent)
                .collect(Collectors.toList());
    }

    private boolean containsTarget(List<Tile> obstacles) {
        for (Tile tile : obstacles) {
            if (isTarget(tile)) {
                return true;
            }
        }
        return false;
    }

    private boolean isTarget(Tile tile) {
        return tile.getType().equals(TileType.TARGET);
    }

    private Tile getTarget() {
        List<Tile> obstacles = super.getVisionModule().getVisibleTiles();
        return obstacles.stream().filter(tile -> tile.getType().equals(TileType.TARGET)).findFirst().get();
    }

    private boolean seesBlueMark() {
        List<Tile> obstacles = super.getVisionModule().getVisibleTiles();

        return containsBlueMark(obstacles);
    }

    private boolean containsBlueMark(List<Tile> obstacles) {
        for (Tile tile : obstacles) {
            if (isBlueMark(tile)) {
                return true;
            }
        }
        return false;
    }

    private boolean isBlueMark(Tile tile) {
        Position position = tile.getPosition();

        return super.getCommunicationModule().tileHasMark(position, CommunicationType.VISION_BLUE);

    }

    private Position getBlueMark() {
        return super.getCommunicationModule().getMark(CommunicationType.VISION_BLUE);
    }

    private void navigateToBlueMark() {
        Position target = getBlueMark();
        moveToLocation(target);
    }

}