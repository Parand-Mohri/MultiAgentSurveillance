package nl.maastrichtuniversity.dke.agents.modules.policy;


import lombok.Getter;
import nl.maastrichtuniversity.dke.agents.modules.exploration.DQN;
import nl.maastrichtuniversity.dke.agents.modules.exploration.NeuralGameState;
import nl.maastrichtuniversity.dke.agents.util.MoveAction;
import org.deeplearning4j.rl4j.policy.DQNPolicy;
import org.nd4j.linalg.factory.Nd4j;

import java.io.IOException;

public class PolicyModule implements IPolicyModule {
    DQNPolicy<NeuralGameState> policy;
    @Getter public int inputSize;


    public PolicyModule(String path, int inputSize) {
        path = DQN.PATH_TO_BINS + path;
        this.inputSize = inputSize;
        try {
            policy = DQNPolicy.load(path);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public MoveAction nextMove(double[] input) {
        Integer action = policy.nextAction(Nd4j.expandDims(Nd4j.create(input), 0));
        return MoveAction.values()[action];
    }


}