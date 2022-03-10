package nl.maastrichtuniversity.dke.agents.modules.memory;

import lombok.Getter;
import lombok.Setter;
import nl.maastrichtuniversity.dke.agents.Agent;
import nl.maastrichtuniversity.dke.agents.modules.AgentModule;
import nl.maastrichtuniversity.dke.agents.modules.vision.IVisionModule;
import nl.maastrichtuniversity.dke.agents.modules.vision.VisionModule;
import nl.maastrichtuniversity.dke.discrete.Environment;
import nl.maastrichtuniversity.dke.discrete.EnvironmentFactory;
import nl.maastrichtuniversity.dke.discrete.Scenario;
import nl.maastrichtuniversity.dke.discrete.Tile;
import nl.maastrichtuniversity.dke.util.Position;

import java.util.List;

@Getter
public class MemoryModule extends AgentModule implements IMemoryModule {

    private final Environment map;
    private List<Agent> agents;

    private @Setter Position startPosition;

    public MemoryModule(Scenario scenario) {
        super(scenario);

        int width = scenario.getEnvironment().getWidth();
        int height = scenario.getEnvironment().getHeight();
        this.map = new Environment(width, height, new Tile[width][height]);
    }

    public void update(IVisionModule vision) {
        for(Tile tile: vision.getObstacles()) {
            int x = tile.getPosition().getX();
            int y = tile.getPosition().getY();
            map.getTileMap()[x][y] = tile;
        }
        agents = vision.getAgents();
    }



}
