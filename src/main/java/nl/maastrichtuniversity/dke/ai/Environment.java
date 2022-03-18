package nl.maastrichtuniversity.dke.ai;

import nl.maastrichtuniversity.dke.logic.Game;
import nl.maastrichtuniversity.dke.logic.agents.Agent;
import nl.maastrichtuniversity.dke.logic.scenario.environment.Tile;
import nl.maastrichtuniversity.dke.logic.scenario.environment.TileType;
import nl.maastrichtuniversity.dke.reinforcement.Reward;
import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;

import java.util.LinkedList;

/**
 * This is the environment that is used in the reinforcement learning.
 * It is kind of like a playground for the agents to play in.
 */
public class Environment implements MDP<NeuralGameState, Integer, DiscreteSpace> {

    private final DiscreteSpace actionSpace = new DiscreteSpace(Network.NUM_OUTPUTS);
    private Game game;

    @Override
    public StepReply<NeuralGameState> step(Integer integer) {
        var agentActions = game.getAgentActions();
        var totalAgents = game.getScenario().getGuards().size();
        var agents = game.getScenario().getGuards();

        Agent agent;

        if (agentActions.size() == totalAgents) {
            game.update();
            agent = agents.get(0);
        } else {
            game.getAgentActions().add(integer-1);
            agent = agents.get(agentActions.size()-1);
        }

        return new StepReply<>(
                new NeuralGameState(agent.getStateVector()),
                Reward.calculateReward(game.getScenario()),
                isDone(),
                null
        );
    }

    @Override
    public boolean isDone() {
        var scenario = game.getScenario();
        int numberOfTiles = scenario.getEnvironment().getHeight() * scenario.getEnvironment().getWidth();
        LinkedList<Tile> exploredTiles = new LinkedList<>();

        for (Agent a : scenario.getGuards()) {
            for (Tile[] tCol : a.getMemoryModule().getMap().getTileMap()) {
                for (Tile t : tCol) {
                    if (t.getType()!= TileType.UNKNOWN && !exploredTiles.contains(t)) {
                       exploredTiles.add(t);
                    }
                    if (exploredTiles.size() == numberOfTiles) {
                        return true;
                    }
                }
            }
        }
        return exploredTiles.size() == numberOfTiles;

    }

    @Override
    public NeuralGameState reset() {
        if (game == null) {
            game = Game.getInstance();
        } else {
            game.reset();
        }

        return new NeuralGameState(game.getScenario().getGuards().get(0).getStateVector());
    }

    @Override
    public MDP<NeuralGameState, Integer, DiscreteSpace> newInstance() { return new Environment(); }

    @Override
    public ObservationSpace<NeuralGameState> getObservationSpace() { return new GameObservationSpace(); }

    @Override
    public DiscreteSpace getActionSpace() { return actionSpace; }

    @Override
    public void close() {}
}