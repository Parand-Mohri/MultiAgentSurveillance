package nl.maastrichtuniversity.dke;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nl.maastrichtuniversity.dke.agents.Fleet;
import nl.maastrichtuniversity.dke.agents.Guard;
import nl.maastrichtuniversity.dke.agents.Intruder;
import nl.maastrichtuniversity.dke.agents.modules.victory.Victory;
import nl.maastrichtuniversity.dke.scenario.Scenario;
import nl.maastrichtuniversity.dke.scenario.util.MapParser;

import java.io.File;

@Slf4j
public class Game {

    private static File mapFile;
    private static Game game;

    private static final File DEFAULT_MAP = new File("src/main/resources/maps/testmap.txt");

    public static Game getInstance() {
        if (game == null) {
            setMapFile(DEFAULT_MAP);
        }

        return game;
    }

    public static void setMapFile(File mapFile) {
        Game.mapFile = mapFile;

        if (game == null) {
            log.info("Creating new game instance.");
            game = new Game();
        } else {
            log.info("Map file changed, resetting game.");
            game.reset();
        }
    }


    private @Getter Scenario scenario;
    private @Getter Victory victory;

    public void reset() {
        scenario = new MapParser(mapFile).createScenario();
        init();
    }

    public void init() {
        scenario.getGuards().forEach(Guard::spawn);
        scenario.getIntruders().forEach(Intruder::spawn);
        victory = new Victory(this.scenario);
    }

    public void update() {
        scenario.incrementTimeStep();
        scenario.getSoundMap().clear();
        moveAgents();
        updateAgentInternals();
    }

    public boolean checkVictory() {
        if (victory.checkGuardVictory()) {
            return true;
        }
        if (victory.checkIntruderVictory()) {
            return true;
        }

        return false;
    }

    // TODO: display victory message on screen
    public void victoryMessage(int gameNumber) {
        if (victory.checkGuardVictory()) {
            log.info("Game " + gameNumber + ": Guards win");
        }
        if (victory.checkIntruderVictory()) {
            log.info("Game " + gameNumber + ":Intruders win");
        }
    }

    private void moveAgents() {
        Fleet<Guard> guards = scenario.getGuards();
        Fleet<Intruder> intruders = scenario.getIntruders();

        guards.forEach(Guard::move);
        intruders.forEach(Intruder::move);
    }

    private void updateAgentInternals() {
        Fleet<Intruder> intruders = scenario.getIntruders();
        Fleet<Guard> guards = scenario.getGuards();

        guards.forEach(Guard::updateInternals);
        intruders.forEach(Intruder::updateInternals);
    }

    protected Game() {
        this.scenario = new MapParser(mapFile).createScenario();
        init();
    }

}