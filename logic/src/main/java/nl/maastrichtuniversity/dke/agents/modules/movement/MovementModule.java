package nl.maastrichtuniversity.dke.agents.modules.movement;

import lombok.extern.slf4j.Slf4j;
import nl.maastrichtuniversity.dke.agents.modules.AgentModule;
import nl.maastrichtuniversity.dke.agents.util.Direction;
import nl.maastrichtuniversity.dke.agents.util.MoveAction;
import nl.maastrichtuniversity.dke.agents.util.exceptions.ActionIsNotRotationException;
import nl.maastrichtuniversity.dke.scenario.Scenario;
import nl.maastrichtuniversity.dke.scenario.environment.TeleportTile;
import nl.maastrichtuniversity.dke.scenario.environment.Tile;
import nl.maastrichtuniversity.dke.scenario.environment.TileType;
import nl.maastrichtuniversity.dke.scenario.util.Position;

@Slf4j
public class MovementModule extends AgentModule implements IMovementModule {

    private final double baseSpeed;
    private final double sprintSpeed;
    private double lastTimeMoved;

    public MovementModule(Scenario scenario, double baseSpeed, double sprintSpeed) {
        super(scenario);
        this.baseSpeed = baseSpeed;
        this.sprintSpeed = sprintSpeed;
        this.lastTimeMoved = -1;
    }

    @Override
    public Direction rotate(Direction currentDirection, MoveAction action) {
        try {
            checkIfActionIsRotation(action);
            return getNewRotatedDirection(currentDirection, action);
        } catch (ActionIsNotRotationException e) {
            log.error(e.getMessage());
            return currentDirection;
        }
    }

    @Override
    public Position goForward(Position currentPosition, Direction direction) {
        Position nextPosition = getForwardPosition(currentPosition, direction);

        if (enoughTimeHasElapsedSinceLastMove(baseSpeed)) {
            lastTimeMoved = getCurrentTime();
            return nextPosition;
        }

        return currentPosition;
    }

    @Override
    public Position sprint(Position currentPosition, Direction direction) {
        Position nextPosition = getForwardPosition(currentPosition, direction);

        if (enoughTimeHasElapsedSinceLastMove(sprintSpeed)) {
            lastTimeMoved = getCurrentTime();
            return nextPosition;
        }

        return currentPosition;
    }

    @Override
    public Position getForwardPosition(Position currentPosition, Direction direction) {
        Position facingPosition = getFacingPosition(currentPosition, direction);

        if (!agentCanMoveTo(facingPosition)) {
            return currentPosition;
        }

        if (isTeleportTile(facingPosition)) {
            return getTeleportDestination(facingPosition);
        }

        return facingPosition;
    }

    private boolean agentCanMoveTo(Position position) {
        if (!positionIsInMap(position)) {
            return false;
        }
        Tile tile = getTileAt(position);

        return tile.isPassable();
    }

    private boolean isTeleportTile(Position position) {
        Tile tile = getTileAt(position);
        return tile.getType() == TileType.TELEPORT;
    }

    private Position getTeleportDestination(Position position) {
        Tile tile = getTileAt(position);
        return ((TeleportTile) tile).getTargetPosition();
    }

    private Position getFacingPosition(Position position, Direction direction) {
        return position.add(new Position(direction.getMoveX(), direction.getMoveY()));
    }

    private boolean positionIsInMap(Position position) {
        Tile[][] tileMap = scenario.getEnvironment().getTileMap();
        boolean withinXBound = position.getX() >= 0 && position.getX() < tileMap.length;
        boolean withinYBound = position.getY() >= 0 && position.getY() < tileMap[0].length;

        return withinXBound && withinYBound;
    }

    private Tile getTileAt(Position position) {
        return scenario.getEnvironment().getTileMap()[position.getX()][position.getY()];
    }

    private void checkIfActionIsRotation(MoveAction action) throws ActionIsNotRotationException {
        if (action != MoveAction.ROTATE_LEFT && action != MoveAction.ROTATE_RIGHT) {
            throw new ActionIsNotRotationException();
        }
    }

    private Direction getNewRotatedDirection(Direction currentDirection, MoveAction action) {
        switch (currentDirection) {
            case NORTH -> {
                return action == MoveAction.ROTATE_RIGHT ? Direction.EAST : Direction.WEST;
            }
            case SOUTH -> {
                return action == MoveAction.ROTATE_RIGHT ? Direction.WEST : Direction.EAST;
            }
            case EAST -> {
                return action == MoveAction.ROTATE_RIGHT ? Direction.SOUTH : Direction.NORTH;
            }
            case WEST -> {
                return action == MoveAction.ROTATE_RIGHT ? Direction.NORTH : Direction.SOUTH;
            }
            default -> {
                return currentDirection;
            }
        }
    }

    private boolean enoughTimeHasElapsedSinceLastMove(double speed) {
        return getElapsedTimeSinceLastMove() >= (1.0 / speed);
    }

    private double getCurrentTime() {
        return scenario.getCurrentTime();
    }

    private double getElapsedTimeSinceLastMove() {
        return getCurrentTime() - lastTimeMoved;
    }

}


