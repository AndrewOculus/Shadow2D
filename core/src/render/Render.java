package render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Render {

	private Array<GameObject> gameObjects;
	
	private Texture texture , MapM;
	private TextureRegion MapR , MapS ;
	private SpriteBatch batch ;
	private ShaderProgram ShaderData , ShaderMap , ShadowShader , MixShader;
	private FrameBuffer FBOData, FBOMap , FBOShadow;
	private int lightSize = 600;
	private int x ,y , lx , ly;
	private BitmapFont bfont;
	private OrthographicCamera camera;
	private float angl = 0;
	
	public Render()
	{
		gameObjects = new Array<GameObject>();
		
		ShaderProgram.pedantic = false;
		ShaderData = new ShaderProgram(Gdx.files.internal("shaders/vert") , Gdx.files.internal("shaders/fragData"));
		ShaderMap = new ShaderProgram(Gdx.files.internal("shaders/vert"),Gdx.files.internal("shaders/linesFrag"));
		ShadowShader =  new ShaderProgram(Gdx.files.internal("shaders/vert"),Gdx.files.internal("shaders/ShadFrag"));
		MixShader = new ShaderProgram(Gdx.files.internal("shaders/vert"),Gdx.files.internal("shaders/MixShader"));
		FBOMap = new FrameBuffer(Pixmap.Format.RGB888 , Gdx.graphics.getWidth() , Gdx.graphics.getHeight(),false);
		FBOData = new FrameBuffer(Pixmap.Format.RGB888 , lightSize , 1 ,false);
		FBOShadow = new FrameBuffer(Pixmap.Format.RGB888 , Gdx.graphics.getWidth() , Gdx.graphics.getHeight(),false);
		texture = new Texture(Gdx.files.internal("345.png"));
		batch = RenderKit.get().getSpriteBatch();
		bfont = new BitmapFont();
		camera = RenderKit.get().getOrthCamera();
	}
	public void addObject(GameObject go)
	{
		gameObjects.add(go);
	}
	public void update(float dt)
	{
		//draw shadow map
		camera.update();
		camera.setToOrtho(false , Gdx.graphics.getWidth() , Gdx.graphics.getWidth());
		FBOMap.begin();
	    Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		batch.setShader(null);
		batch.begin();
		//for(int i = 0 ; i < gameObjects.size ; i ++)
		//	gameObjects.get(i).update(dt);
		batch.draw(texture, 100 , 100, 300 , 300);
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
		//FBOShadow.begin();
		
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
		//FBOShadow.end();
		
		/*
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
		*/
		/*
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
		*/
		//Gdx.gl.glClearColor(1f, 1f, 1f, 0f);
	    //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.setToOrtho(false , Gdx.graphics.getWidth() , Gdx.graphics.getHeight());
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.setShader(null);
		batch.begin();
		//for(int i = 0 ; i < gameObjects.size ; i ++)
		//	gameObjects.get(i).update(dt);
		batch.draw(texture, 100 , 100 + Gdx.graphics.getWidth()/2, 300 , 300);
		batch.end();
		
		ShowFPS();
		
		angl = MathUtils.degreesToRadians* (new Vector2(Gdx.input.getX() - Gdx.graphics.getWidth()/2 , Gdx.input.getY() - Gdx.graphics.getHeight()/2)).angle()/3.14f;

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
}
