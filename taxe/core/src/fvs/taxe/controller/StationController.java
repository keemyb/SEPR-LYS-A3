package fvs.taxe.controller;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import fvs.taxe.StationClickListener;
import fvs.taxe.TaxeGame;
import fvs.taxe.Tooltip;
import fvs.taxe.actor.CollisionStationActor;
import fvs.taxe.actor.StationActor;
import fvs.taxe.dialog.DialogStationMultitrain;
import gameLogic.Game;
import gameLogic.GameState;
import gameLogic.Player;
import gameLogic.map.CollisionStation;
import gameLogic.map.Connection;
import gameLogic.map.IPositionable;
import gameLogic.map.Station;
import gameLogic.resource.Resource;
import gameLogic.resource.Train;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class StationController {
    public final static int CONNECTION_LINE_WIDTH = 5;

    private Context context;
    private Tooltip tooltip;
    /*
    have to use CopyOnWriteArrayList because when we iterate through our listeners and execute
    their handler's method, one case unsubscribes from the event removing itself from this list
    and this list implementation supports removing elements whilst iterating through it
    */
    private static List<StationClickListener> stationClickListeners = new CopyOnWriteArrayList<StationClickListener>();

    public StationController(Context context, Tooltip tooltip) {
        this.context = context;
        this.tooltip = tooltip;
    }

    public static void subscribeStationClick(StationClickListener listener) {
        stationClickListeners.add(listener);
    }

    public static void unsubscribeStationClick(StationClickListener listener) {
        stationClickListeners.remove(listener);
    }

    private static void stationClicked(Station station) {
        for (StationClickListener listener : stationClickListeners) {
            listener.clicked(station);
        }
    }

    private void renderStation(final Station station) {
        final StationActor stationActor = new StationActor(station.getLocation());

        stationActor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(Game.getInstance().getState() == GameState.NORMAL){
                	DialogStationMultitrain dia = new DialogStationMultitrain(station, context.getSkin(), context);
                	if(dia.getIsTrain()) {
                		dia.show(context.getStage());
                	}
                }
                stationClicked(station);
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                tooltip.setPosition(stationActor.getX() -65, stationActor.getY() + 23);
                tooltip.show(station.getName());
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                tooltip.hide();
            }
        });

        station.setActor(stationActor);

        context.getStage().addActor(stationActor);
    }

    private void renderCollisionStation(final CollisionStation collisionStation) {
    	final CollisionStationActor collisionStationActor = CollisionStationActor.createCollisionStationActor(collisionStation);

    	collisionStationActor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(Game.getInstance().getState() == GameState.NORMAL){
                    DialogStationMultitrain dia = new DialogStationMultitrain(collisionStation, context.getSkin(), context);
                    if(dia.getIsTrain()) {
                        dia.show(context.getStage());
                    }
                }
                stationClicked(collisionStation);
            }
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                tooltip.setPosition(collisionStationActor.getX() -65, collisionStationActor.getY() + 23);
                tooltip.show(collisionStation.getName());
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                tooltip.hide();
            }
        });

        context.getStage().addActor(collisionStationActor);
    }

    public void renderStations() {
        List<Station> stations = context.getGameLogic().getMap().getStations();

        for (Station station : stations) {
        	if(station instanceof CollisionStation) {
        		renderCollisionStation((CollisionStation) station);
        	} else {
        		renderStation(station);
        	}
        }
    }

    /**
     * Renders all connections. If the connection is broken it will be coloured red.
     * @param connections The connection to render.
     * @param unbrokenColor The colour to use if the connection is unbroken.
     */
    public void renderConnections(List<Connection> connections, Color unbrokenColor) {
        TaxeGame game = context.getTaxeGame();

        Color brokenColor = Color.RED;

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(unbrokenColor);

        for (Connection connection : connections) {
            IPositionable start = connection.getStation1().getLocation();
            IPositionable end = connection.getStation2().getLocation();
            if (connection.isBroken()) {
            	game.shapeRenderer.setColor(brokenColor);
            }	else {
            	game.shapeRenderer.setColor(unbrokenColor);
            }
            game.shapeRenderer.rectLine(start.getX(), start.getY(), end.getX(), end.getY(), CONNECTION_LINE_WIDTH);
            
        }
        game.shapeRenderer.end();
    }

    public void displayNumberOfTrainsAtStations() {
    	TaxeGame game = context.getTaxeGame();
		game.batch.begin();
		game.fontSmall.setColor(Color.BLACK);

        for(Station station : context.getGameLogic().getMap().getStations()) {
            if(trainsAtStation(station) > 0) {
                game.fontSmall.draw(game.batch, trainsAtStation(station) + "", (float) station.getLocation().getX() - 6, (float) station.getLocation().getY() + 26);
            }
        }

        game.batch.end();
    }

    private int trainsAtStation(Station station) {
        int count = 0;

        for(Player player : context.getGameLogic().getPlayerManager().getAllPlayers()) {
            for(Resource resource : player.getResources()) {
                if(resource instanceof Train) {
                    if(((Train) resource).getActor() != null) {
                        if(((Train) resource).getPosition().equals(station.getLocation())) {
                            count++;
                        }
                    }
                }
            }
        }
        return count;
    }
}
