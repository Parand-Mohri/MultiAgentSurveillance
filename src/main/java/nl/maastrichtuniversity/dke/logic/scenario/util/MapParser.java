package nl.maastrichtuniversity.dke.logic.scenario.util;

import nl.maastrichtuniversity.dke.logic.scenario.Scenario;
import nl.maastrichtuniversity.dke.logic.scenario.environment.TileType;
import nl.maastrichtuniversity.dke.logic.agents.factory.AgentFactory;
import nl.maastrichtuniversity.dke.logic.scenario.factory.EnvironmentFactory;
import nl.maastrichtuniversity.dke.logic.scenario.factory.ScenarioFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Scanner;

public class MapParser {

    private static final Logger logger = LoggerFactory.getLogger(MapParser.class);
    private Scanner scanner;

    private ScenarioFactory scenarioFactory;
    private EnvironmentFactory envBuilder;
    private AgentFactory agentFactory;

    public MapParser(File file) {
        try {
            scanner = new Scanner(file);
            scenarioFactory = new ScenarioFactory();
            agentFactory = new AgentFactory();
        } catch (Exception e) {
            logger.error("Error while parsing file: ");
            e.printStackTrace();
        }
    }

    public Scenario createScenario() {
        envBuilder = new EnvironmentFactory();

        while (scanner.hasNextLine())
            createFieldFromLine(scanner.nextLine());

        scenarioFactory.setEnvironment(envBuilder.build());
        return scenarioFactory.build(agentFactory);
    }

    private void createFieldFromLine(String line) {
        Scanner lineScanner = new Scanner(line);
        lineScanner.useDelimiter("=");

        while(lineScanner.hasNext()) {
            String key = lineScanner.next().trim();
            String value = lineScanner.next().trim();

            createScenarioField(key, value);
        }
    }

    private void createScenarioField(String key, String value) {
        String[] values = value.split(" ");

        switch (key) {
            case "name" -> scenarioFactory.setName(value);
            case "gameFile" -> logger.error("GameFile not implemented yet");
            case "gameMode" -> scenarioFactory.setGameMode(Integer.parseInt(value));
            case "height" -> envBuilder.setHeight(Integer.parseInt(value));
            case "width" -> envBuilder.setWidth(Integer.parseInt(value));
            case "scaling" -> scenarioFactory.setScaling(Double.parseDouble(value));
            case "numGuards" -> scenarioFactory.setNumberOfGuards(Integer.parseInt(value));
            case "numIntruders" -> scenarioFactory.setNumberOfIntruders((int)Double.parseDouble(value));
            case "baseSpeedIntruder" -> agentFactory.setBaseSpeedIntruders((int)Double.parseDouble(value));
            case "sprintSpeedIntruder" -> agentFactory.setSprintSpeedIntruders((int)Double.parseDouble(value));
            case "baseSpeedGuard" -> agentFactory.setBaseSpeedGuards((int)Double.parseDouble(value));
            case "timeStep" -> scenarioFactory.setTimeStep(Double.parseDouble(value));
            case "targetArea" -> addArea(values, TileType.TARGET);
            case "spawnAreaIntruders" -> addArea(values, TileType.SPAWN_INTRUDERS);
            case "spawnAreaGuards" -> addArea(values, TileType.SPAWN_GUARDS);
            case "wall" -> addArea(values, TileType.WALL);
            case "teleport" -> {
                addArea(values, TileType.TELEPORT);
                addArea(new String[]{values[4], values[5], values[4], values[5]}, TileType.DESTINATIONTELEPORT);
            }
            case "shaded" -> addArea(values, TileType.SHADED);
            case "texture" -> logger.error("Texture not implemented yet");
            case "window" -> addArea(values, TileType.WINDOW);
            case "door" -> addArea(values, TileType.DOOR);
            case "sentrytower" -> addArea(values, TileType.SENTRY);
            case "distanceViewing" -> agentFactory.setViewingDistance(Integer.parseInt(value));
            case "distanceHearingWalking" -> agentFactory.setHearingDistanceWalking(Integer.parseInt(value));
            case "distanceHearingSprinting" -> agentFactory.setHearingDistanceSprinting(Integer.parseInt(value));
            case "distanceSmelling" -> agentFactory.setSmellingDistance(Integer.parseInt(value));
            case "numberOfMarkers" -> agentFactory.setNumberOfMarkers(Integer.parseInt(value));
            default -> logger.error("Unknown value: " + key);
        }
    }

    private void addArea(String[] values, TileType type) {
        envBuilder.addArea(
                Integer.parseInt(values[0]),
                Integer.parseInt(values[1]),
                Integer.parseInt(values[2]),
                Integer.parseInt(values[3]),
                type
        );

    }

}