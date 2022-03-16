package nl.maastrichtuniversity.dke.logic.agents.modules.communication;

import lombok.Getter;
import nl.maastrichtuniversity.dke.logic.agents.modules.AgentModule;
import nl.maastrichtuniversity.dke.logic.scenario.CommunicationMark;
import nl.maastrichtuniversity.dke.logic.scenario.Scenario;
import nl.maastrichtuniversity.dke.logic.scenario.util.Position;

public class CommunicationModule extends AgentModule implements ICommunicationModule {

    private final @Getter
    int numberOfMarkers;

    public CommunicationModule(Scenario scenario, int numberOfMarkers) {
        super(scenario);
        this.numberOfMarkers = numberOfMarkers;
    }

    @Override
    public void addMark(int x, int y, CommunicationMark device) {
        //getMarks(x, y).add(device);
        scenario.getCommunicationMarks().add(device);
    }

    @Override
    public boolean hasMark(int x, int y) {
        Position p = new Position(x, y);
        for (CommunicationMark cm : scenario.getCommunicationMarks()) {
            if (cm.getPosition().equals(p)) {
                return true;
            }
        }
        return false;
        //return getMarks(x, y).size() > 0;
    }

    /*
    @Override
    public List<CommunicationMark> getMarks(int x, int y) {
        var environment = scenario.getEnvironment();
        var tile = environment.getTileMap()[x][y];

        return tile.getCommunicationMarks();
    }
*/

}