package nl.maastrichtuniversity.dke.logic;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nl.maastrichtuniversity.dke.logic.agents.Fleet;
import nl.maastrichtuniversity.dke.logic.agents.Guard;
import nl.maastrichtuniversity.dke.logic.agents.Intruder;
import nl.maastrichtuniversity.dke.logic.agents.modules.victory.Victory;
import nl.maastrichtuniversity.dke.logic.scenario.Scenario;
import nl.maastrichtuniversity.dke.logic.scenario.util.MapParser;

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
    private @Getter double time;
    private @Getter int currentTimeStep;
    private @Getter final Victory victory;

    public void reset() {
        scenario = new MapParser(mapFile).createScenario();
        currentTimeStep = 0;
        game.time = 0.0;
        init();
    }

    public void init() {
        scenario.getGuards().forEach(Guard::spawn);
        scenario.getIntruders().forEach(Intruder::spawn);
    }

    public void update() {
        resetNoise();
        time += scenario.getTimeStep();
        currentTimeStep++;

        updateGuards();
        updateIntruders();
    }

    private void updateIntruders() {
        Fleet<Intruder> intruders = scenario.getIntruders();
        intruders.forEach(Intruder::update);
    }

    private void updateGuards() {
        Fleet<Guard> guards = scenario.getGuards();
        guards.forEach(Guard::update);
    }

    public void resetNoise() {
        scenario.getSoundMap().clear();
    }

    protected Game() {
        this.scenario = new MapParser(mapFile).createScenario();
        this.time = 0.0;
        this.currentTimeStep = 0;
        this.victory = new Victory(this.scenario);
        init();
    }

}
