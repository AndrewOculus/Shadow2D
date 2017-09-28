package render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class RenderKit {

	private static SpriteBatch batch;
	private static OrthographicCamera orthCamera;
	private static RenderKit rKit;

	public SpriteBatch getSpriteBatch(){return batch;}
	public OrthographicCamera getOrthCamera(){return orthCamera;}

	public RenderKit()
	{
		batch = new SpriteBatch();
		orthCamera = new OrthographicCamera(Gdx.graphics.getWidth() , Gdx.graphics.getHeight());
	}
	
	public static RenderKit get()
	{
		if(rKit == null)
			rKit = new RenderKit();
		return rKit;
		
		//return rKit == null? rKit = new RenderKit() : rKit;
	}
}
