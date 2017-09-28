package gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import render.GameObject;
import render.RenderKit;

public class Wall implements GameObject {

	private SpriteBatch batch;
	private OrthographicCamera orthCamera;
	private Texture texture;
	private Vector2 position;
	
	public Wall() {
		batch = RenderKit.get().getSpriteBatch();
		orthCamera = RenderKit.get().getOrthCamera();
		texture = new Texture(Gdx.files.internal("345.png"));
		position = new Vector2(100,100);
	}
	
	@Override
	public void update(float dt) {
		batch.draw(texture,position.x + Gdx.graphics.getWidth()/2, position.y);
	}

}
