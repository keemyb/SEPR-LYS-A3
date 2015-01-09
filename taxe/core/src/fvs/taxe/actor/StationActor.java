package fvs.taxe.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import gameLogic.map.IPositionable;

public class StationActor extends Image {
    private int width = 32;
    private int height = 32;

    public StationActor(IPositionable location) {
        super(new Texture(Gdx.files.internal("station_dot.png")));

        setSize(width, height);
        setPosition(location.getX() - width / 2, location.getY() - height / 2);
    }
}