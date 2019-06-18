package minercrystal.gamefield;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import minercrystal.gamefield.WorkScreens.MenuSelectScreen;
import minercrystal.gamefield.WorkScreens.PlayScreen;


public class GameClass extends Game {
	SpriteBatch batch;
	@Override
	public void create() {
		batch = new SpriteBatch();
		this.setScreen(new MenuScreen(this));
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void dispose() {
		super.dispose();
		batch.dispose();
	}
}