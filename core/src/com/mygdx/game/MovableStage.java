package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FillViewport;

public class MovableStage extends Stage {

    private final CameraController cameraController;

    private final int worldWidth;
    private final int worldHeight;

    public MovableStage(int worldWidth, int worldHeight) {
        super();
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.cameraController = new CameraController(this);
        Gdx.input.setInputProcessor(cameraController);

        setupViewport();
    }

    private void setupViewport() {
        super.setViewport(new FillViewport(worldWidth, worldHeight, cameraController.getCamera()));
        super.getViewport().apply();
    }

    @Override
    public void draw() {
        updateCamera();
    }

    private void updateCamera() {
        cameraController.update(getBatch());
    }

    public void resize(int width, int height) {
        super.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        super.dispose();
        getBatch().dispose();
    }

    @Override
    public float getWidth() {
        return super.getWidth();
    }

    @Override
    public float getHeight() {
        return super.getHeight();
    }

}