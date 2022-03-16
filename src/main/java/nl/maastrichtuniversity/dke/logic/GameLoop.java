package nl.maastrichtuniversity.dke.logic;

import nl.maastrichtuniversity.dke.logic.agents.Agent;
import nl.maastrichtuniversity.dke.logic.scenario.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameLoop {

    private static final Logger logger = LoggerFactory.getLogger(GameLoop.class);
    private final Scenario scenario;

    public GameLoop(Scenario scenario) {
        this.scenario = scenario;
        scenario.getGuards().forEach(Agent::spawn);
    }

    public void update(double time) {
        for (Agent agent : scenario.getGuards()) {
            moveAgentRandomly(agent, time);
        }

        for (Agent agent : scenario.getGuards()) {
            agent.listen();
        }
    }

    private void moveAgentRandomly(Agent agent, double time) {
        int rotation = getRandomRotation();

        if (rotation == 0)
            agent.goForward(time);
        else
            agent.rotate(rotation, time);
    }

    private int getRandomRotation() {
        if (Math.random() < 0.5)
            return 0;

        if (Math.random() < 0.5)
            return 1;
        else
            return -1;
    }

    public void resetNoise(){
        scenario.getSoundMap().clear();
    }

}