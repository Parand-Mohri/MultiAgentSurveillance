package nl.maastrichtuniversity.dke.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.Timer;

import nl.maastrichtuniversity.dke.agents.Direction;
import nl.maastrichtuniversity.dke.discrete.*;
import nl.maastrichtuniversity.dke.gui.ImageFactory;
import nl.maastrichtuniversity.dke.util.Position;

public class GameComponent extends JComponent{

	private final Scenario scenario;
	private Environment environment;
	private int textureSize;

	private int guardY = 100;
	private int guardX = 0;
	private int panningX=0;
	private int panningY=0;

	int frame = 0;//Current animation fram
    int frameInterval = 0;//Interval to load next frame


	public GameComponent(Scenario scenario ,Environment environment){
		this.scenario = scenario;
		double scale = scenario.getScaling()*100;
		textureSize = (int) scale;
		this.environment = environment;
		moveGuards();

	}


	public void paintComponent(Graphics g) {

		var agent = scenario.getGuards().get(0);
		var sound = scenario.getSoundMap();

		drawSound(g,sound, ImageFactory.get("soundTexture"));

		drawAreas(g, environment.get(TileType.WALL), ImageFactory.get("wallTexture"));
		drawAreas(g, environment.get(TileType.TELEPORT), ImageFactory.get("teleportTexture"));
		drawAreas(g, environment.get(TileType.SPAWN_GUARDS), ImageFactory.get("spawnAreaTexture"));
		drawAreas(g, environment.get(TileType.SPAWN_INTRUDERS), ImageFactory.get("spawnAreaTexture"));
		drawAreas(g, environment.get(TileType.WINDOW), ImageFactory.get("windowTexture"));
		drawAreas(g, environment.get(TileType.DOOR), ImageFactory.get("doorTexture"));
		drawAreas(g, environment.get(TileType.SENTRY), ImageFactory.get("sentryTowerTexture"));
		drawAreas(g, environment.get(TileType.TARGET), ImageFactory.get("targetTexture"));
		drawAreas(g, environment.get(TileType.SHADED), ImageFactory.get("shadedTexture"));
		drawAreas(g, environment.get(TileType.UNKNOWN), ImageFactory.get("unknownTexture"));
		if(agent.getDirection() == Direction.NORTH){
			if (frame==0) {
				g.drawImage(ImageFactory.get("guardNorth"),panningX+agent.getPosition().getX() * textureSize,panningY+ agent.getPosition().getY()*textureSize, textureSize, textureSize,null);
			}
			if (frame==1) {
				g.drawImage(ImageFactory.get("guardNorth2"),panningX+agent.getPosition().getX() * textureSize,panningY+ agent.getPosition().getY()*textureSize, textureSize, textureSize,null);
			}
			if (frame==2) {
				g.drawImage(ImageFactory.get("guardNorth3"),panningX+agent.getPosition().getX() * textureSize,panningY+ agent.getPosition().getY()*textureSize, textureSize, textureSize,null);
			}
			if (frame==3) {
				g.drawImage(ImageFactory.get("guardNorth4"),panningX+agent.getPosition().getX() * textureSize,panningY+ agent.getPosition().getY()*textureSize, textureSize, textureSize,null);
			}

		}
		if(agent.getDirection() == Direction.SOUTH){
			if (frame==0) {
				g.drawImage(ImageFactory.get("guardSouth"),panningX+agent.getPosition().getX() * textureSize,panningY+ agent.getPosition().getY()*textureSize, textureSize, textureSize,null);
			}
			if (frame==1) {
				g.drawImage(ImageFactory.get("guardSouth2"),panningX+agent.getPosition().getX() * textureSize,panningY+ agent.getPosition().getY()*textureSize, textureSize, textureSize,null);
			}
			if (frame==2) {
				g.drawImage(ImageFactory.get("guardSouth3"),panningX+agent.getPosition().getX() * textureSize,panningY+ agent.getPosition().getY()*textureSize, textureSize, textureSize,null);
			}
			if (frame==3) {
				g.drawImage(ImageFactory.get("guardSouth4"),panningX+agent.getPosition().getX() * textureSize,panningY+ agent.getPosition().getY()*textureSize, textureSize, textureSize,null);
			}		}
		if(agent.getDirection() == Direction.EAST){
			if (frame==0) {
				g.drawImage(ImageFactory.get("guardWest"),panningX+agent.getPosition().getX() * textureSize,panningY+ agent.getPosition().getY()*textureSize, textureSize, textureSize,null);
			}
			if (frame==1) {
				g.drawImage(ImageFactory.get("guardWest2"),panningX+agent.getPosition().getX() * textureSize,panningY+ agent.getPosition().getY()*textureSize, textureSize, textureSize,null);
			}
			if (frame==2) {
				g.drawImage(ImageFactory.get("guardWest3"),panningX+agent.getPosition().getX() * textureSize,panningY+ agent.getPosition().getY()*textureSize, textureSize, textureSize,null);
			}
			if (frame==3) {
				g.drawImage(ImageFactory.get("guardWest4"),panningX+agent.getPosition().getX() * textureSize,panningY+ agent.getPosition().getY()*textureSize, textureSize, textureSize,null);
			}		}
		if(agent.getDirection() == Direction.WEST){
			if (frame==0) {
				g.drawImage(ImageFactory.get("guardEast"),panningX+agent.getPosition().getX() * textureSize,panningY+ agent.getPosition().getY()*textureSize, textureSize, textureSize,null);
			}
			if (frame==1) {
				g.drawImage(ImageFactory.get("guardEast2"),panningX+agent.getPosition().getX() * textureSize,panningY+ agent.getPosition().getY()*textureSize, textureSize, textureSize,null);
			}
			if (frame==2) {
				g.drawImage(ImageFactory.get("guardEast3"),panningX+agent.getPosition().getX() * textureSize,panningY+ agent.getPosition().getY()*textureSize, textureSize, textureSize,null);
			}
			if (frame==3) {
				g.drawImage(ImageFactory.get("guardEast4"),panningX+agent.getPosition().getX() * textureSize,panningY+ agent.getPosition().getY()*textureSize, textureSize, textureSize,null);
			}		}
		//HandleFrames
        if(frameInterval % 1 == 0)
        {
            if(frame < 3)
            {
                frame++;
            }
            else
            {
                frame = 0;
            }
            frameInterval = 0;
        }
        System.out.println(frame);
		System.out.println(frameInterval);


        frameInterval++;
		
	}


	private void drawAreas(Graphics g, List<Tile> tiles, BufferedImage image ) {

			for (Tile tile : tiles) {
				drawArea(g, tile, image);
				if(tile.getCommunicationMarks().size()>0){
					drawMark(g, tile);
				}
			}


	}
	private void drawSound(Graphics g, List<Sound> sounds, BufferedImage image ) {
		System.out.println(sounds.size());
		for (Sound sound : sounds) {
			g.drawImage(
					image,
					panningX +  (int)(sound.getPosition().getX() * (textureSize)),
					panningY +  (int)(sound.getPosition().getY() * (textureSize)),
					textureSize,
					textureSize,
					null
			);
			System.out.println("ss" + sound.getPosition().getX());
		}


	}


	private void drawArea(Graphics g, Tile tile, BufferedImage image) {

			g.drawImage(
					image,
					panningX +  (int)(tile.getPosition().getX() * (textureSize)),
					panningY +  (int)(tile.getPosition().getY() * (textureSize)),
					textureSize,
					textureSize,
					null
			);



		}
	private void drawMark(Graphics g, Tile tile) {
		g.setColor(tile.getCommunicationMarks().get(0).getColor());
		g.fillOval(panningX +  (int)(tile.getPosition().getX() * (textureSize)),
							panningY +  (int)(tile.getPosition().getY() * (textureSize)),
							textureSize,
							textureSize);
	}





	public void moveGuards(){
		GameSystem system = new GameSystem(scenario);
		AtomicReference<Double> time = new AtomicReference<>((double) 0);

		Timer timer = new Timer(300, e -> {

			system.update(time.get());
			time.updateAndGet(v -> new Double((double) (v + (double) scenario.getTimeStep())));
			system.resetNoise();
			repaint();


		});
		timer.start();
	}
	public void moveIntuders(){
		
	}
	public void zoomIn(){
		textureSize = textureSize +1;
	}
	public void zoomOut(){
		textureSize = textureSize -1;
	}
	public void panning(int x,int y){
		panningX += x/15;
		panningY += y/15;
		
	}
	public void resize(){
		textureSize = (int) (scenario.getScaling()*100);
		panningX = 0;
		panningY = 0; 		
	}
	public void isAgentMap(){
		textureSize = textureSize-7;
	}
	public Environment getAgentMap(){
		return scenario.getGuards().get(0).getMemoryModule().getMap();
	}
	public void setAgentMap(Environment environment){
		this.environment = environment;

	}



}
