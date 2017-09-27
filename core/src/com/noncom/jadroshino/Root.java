package com.noncom.jadroshino;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Button;

public class Root extends ApplicationAdapter implements InputProcessor {

	Texture texture , MapM;
	TextureRegion MapR , MapS ;
	SpriteBatch batch ;
	ShaderProgram ShaderData , ShaderMap , ShadowShader , MixShader;
	FrameBuffer FBOData, FBOMap , FBOShadow;
	int lightSize = 600;
	int x ,y;
	int lx , ly;
	BitmapFont bfont;
	OrthographicCamera camera;
	float angl = 0;
	
	
	@Override
	public void create () {
		
		Gdx.input.setInputProcessor(this);
		ShaderProgram.pedantic = false;
		ShaderData = new ShaderProgram(Gdx.files.internal("shaders/vert") , Gdx.files.internal("shaders/fragData"));
		ShaderMap = new ShaderProgram(Gdx.files.internal("shaders/vert"),Gdx.files.internal("shaders/linesFrag"));
		ShadowShader =  new ShaderProgram(Gdx.files.internal("shaders/vert"),Gdx.files.internal("shaders/ShadFrag"));
		MixShader = new ShaderProgram(Gdx.files.internal("shaders/vert"),Gdx.files.internal("shaders/MixShader"));
		FBOMap = new FrameBuffer(Pixmap.Format.RGB888 , Gdx.graphics.getWidth() , Gdx.graphics.getHeight(),false);
		FBOData = new FrameBuffer(Pixmap.Format.RGB888 , lightSize , 1 ,false);
		FBOShadow = new FrameBuffer(Pixmap.Format.RGB888 , Gdx.graphics.getWidth() , Gdx.graphics.getHeight(),false);
		texture = new Texture(Gdx.files.internal("star.png"));
		batch = new SpriteBatch();
		bfont = new BitmapFont();
		camera = new OrthographicCamera(Gdx.graphics.getWidth() , Gdx.graphics.getHeight());
	}

	@Override
	public void render () {
		x=Gdx.input.getX();
		y=Gdx.input.getY();
		
		//draw shadow map
		camera.update();
		camera.setToOrtho(false , Gdx.graphics.getWidth() , Gdx.graphics.getWidth());
		FBOMap.begin();
	    Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		batch.setShader(null);
		batch.begin();
		batch.draw(texture , x , y , 100 , 100);
		batch.end();
		FBOMap.end();
		
		
		MapR = new TextureRegion(FBOMap.getColorBufferTexture());
		MapR.flip(false , true);
		
		//draw 1d data
		camera.update();
		FBOData.begin();
		Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		SetResData();
		batch.setShader(ShaderData);
		batch.begin();
		batch.draw(MapR, 0 , 0 , Gdx.graphics.getWidth(), Gdx.graphics.getWidth());
		batch.end();
		FBOData.end();
		
		MapS = new TextureRegion(FBOData.getColorBufferTexture());
		MapS.flip(false , true);
		
		//draw shadow
		FBOShadow.begin();
		camera.update();
		camera.setToOrtho(false);
		Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		SetShadowShader();
		batch.setProjectionMatrix(camera.combined);
		batch.setShader(ShadowShader);
		batch.begin();
		batch.setColor(0.9f , 0.9f , 0.9f , 1f);
		batch.draw(MapS.getTexture(), 0, Gdx.graphics.getWidth()/2, Gdx.graphics.getWidth(), Gdx.graphics.getWidth());
		batch.end();
		FBOShadow.end();
		
		//draw objects
		camera.update();
		camera.setToOrtho(false , Gdx.graphics.getWidth() , Gdx.graphics.getHeight());
		FBOMap.begin();
	    Gdx.gl.glClearColor(0.8f, 0.8f, 0.8f, 1f);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		batch.setShader(null);
		batch.begin();
		batch.draw(texture , x , y + Gdx.graphics.getWidth()/2, 100 , 100);
		batch.end();
		FBOMap.end();
		MapM = FBOShadow.getColorBufferTexture();
		Texture t = MapR.getTexture();
		
		
		//draw by mix shader
		Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.setToOrtho(false , Gdx.graphics.getWidth() , Gdx.graphics.getHeight());
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		MixShader.begin();
		t.bind(1);
		MixShader.setUniformi("u_texture_two", 1);
		MapM.bind(0);
		MixShader.setUniformi("u_texture", 0);
		MixShader.end();
		batch.setShader(MixShader);
		batch.begin();
		batch.draw(MapM ,0 ,0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.end();
		
		ShowFPS();
		
	
	}
	
	@Override
	public void dispose () {

	}
	
	public void SetResData()
	{
		ShaderData.begin();
		ShaderData.setUniformf("resolution", lightSize, lightSize);
		ShaderData.setUniformf("angl", angl);
		ShaderData.end();
	}
	public void SetShadowShader()
	{
		ShadowShader.begin();
		ShadowShader.setUniformf("resolution", lightSize, lightSize);
		ShadowShader.setUniformf("softShadows", 1f);
		ShadowShader.end();
	}
	public void ShowFPS()
	{
		batch.setShader(null);
		batch.begin();
		bfont.draw(batch , Float.toString(Gdx.graphics.getFramesPerSecond()),100 ,100);
		batch.end();
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {

		if(keycode == Keys.W)
		{
			angl +=0.1f;
			System.out.println(angl);
		}
		
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
