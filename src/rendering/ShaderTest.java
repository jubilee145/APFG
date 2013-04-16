package rendering;


import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.newdawn.slick.opengl.shader.ShaderProgram;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import svb.Manager;

public class ShaderTest extends BasicGameState {

	private StateBasedGame game;
	
	//our texture to blur
	TextureLoader loader = new TextureLoader();
	Image image;
	Texture trixie, trixieNormals;
	Texture cHeart, cHeartNormals;
	
	float time;
	
	boolean shaderWorks;
	ShaderProgram shader;
	
	SpriteSheet pinkie;
	SpriteSheet pinkie_n;
	
	Animation a;
	Animation a_n;

	public static final float DEFAULT_LIGHT_Z = 0.075f;
	
	public static final Color LIGHT_POS = new Color(0f,0f,DEFAULT_LIGHT_Z);
	
	//Light RGB and intensity (alpha)
	public static final Color LIGHT_COLOR = new Color(1f, 0.4f, 0.9f, 1f);

	//Ambient RGB and intensity (alpha)
	public static final Color AMBIENT_COLOR = new Color(0.6f, 0.6f, 1f, 0.2f);

	//Attenuation coefficients for light falloff
	public static final Color FALLOFF = new Color(.4f, .3f, 10f);
	


	
	@Override
	public void init(GameContainer container, StateBasedGame game)
        throws SlickException {
		
		this.game = game;

		try {
			FileInputStream trixieIn = new FileInputStream("assets/shaderTest/ShaderTest_censored.png");
			FileInputStream trixieIn_N = new FileInputStream("assets/shaderTest/ShaderTest_n.png");
			
			//load our texture with linear filter
			trixie = TextureLoader.getTexture("assets/shaderTest/ShaderTest_censored.png", trixieIn, Image.FILTER_LINEAR);
			trixieNormals = TextureLoader.getTexture("assets/shaderTest/ShaderTest_n.png", trixieIn_N, Image.FILTER_LINEAR);
			
			pinkie = new SpriteSheet(new Image("assets/shaderTest/pony_censored.png"), 369,331);
			pinkie_n = new SpriteSheet(new Image("assets/shaderTest/pinkie_n.png"), 369,331);
			
			a = new Animation(pinkie, 50);
			a_n = new Animation(pinkie_n, 50);
			
			a.stopAt(23);
			
			image = new Image(trixie);
		} catch (IOException e) {
			throw new RuntimeException("couldn't decode texture");
		}
		
		//load our shader program
		try {

			try {
				// load our vertex and fragment shaders
				String VERT = "assets/shaderTest/pass.vert";
				String FRAG = "assets/shaderTest/light.frag";
				shader = ShaderProgram.loadProgram(VERT, FRAG);
				shaderWorks = true;
			} catch (SlickException e) {
				// there was a problem compiling our source! show the log
				e.printStackTrace();
			}
			
			ShaderProgram.setStrictMode(false);
			
			//Good idea to log any warnings if they exist
			if (shader.getLog().length()!=0)
				System.out.println(shader.getLog());
			
			//always a good idea to set up default uniforms...
			shader.bind();
			
			//our normal map
			shader.setUniform1i("u_normals", 1); //GL_TEXTURE1
			shader.setUniform1i("u_texture", 0); //GL_TEXTURE1
			
			//light/ambient colors
			shader.setUniform4f("LightColor", LIGHT_COLOR.r, LIGHT_COLOR.g,LIGHT_COLOR.b, LIGHT_COLOR.a);
			shader.setUniform4f("AmbientColor", AMBIENT_COLOR.r, AMBIENT_COLOR.g, AMBIENT_COLOR.b, AMBIENT_COLOR.a);
			shader.setUniform3f("Falloff", FALLOFF.r, FALLOFF.g, FALLOFF.b);
			
			shader.unbind();
			//batch = new SpriteBatch(shader);
		} catch (Exception e) { 
			//simple exception handling...
			e.printStackTrace();
			System.exit(0);
		}
		
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {

		if(shaderWorks)
		{
			
			shader.bind();
			//update light position, normalized to screen resolution
			float x = (float)container.getInput().getMouseX() / (float)container.getWidth();
			float y = (float)container.getInput().getMouseY() / (float)container.getHeight();
			LIGHT_POS.r = x;
			LIGHT_POS.g = y;
			//send a Vector4f to GLSL
			shader.setUniform3f("LightPos", LIGHT_POS.r,1-LIGHT_POS.g,LIGHT_POS.b);
			//shader.setUniform3f("LightPos", 0.5f,0f,0f);

			//bind normal map to texture unit 2
			glActiveTexture(GL_TEXTURE1);
			trixieNormals.bind();
	
			//bind diffuse color to texture unit 1
			glActiveTexture(GL_TEXTURE0);
			trixie.bind();

			//draw the texture unit 1 with our shader effect applied
			g.drawImage(image, 50, 50);
			
			glActiveTexture(GL_TEXTURE1);
			pinkie_n.bind();
			glActiveTexture(GL_TEXTURE0);
			pinkie.bind();
			
			g.drawAnimation(a, 800, 250);
			
	    	//Disable shader.
			shader.unbind();
	    	glActiveTexture(GL_TEXTURE1);
	    	GL11.glDisable(GL11.GL_TEXTURE_2D);
	    	glActiveTexture(GL_TEXTURE0);
	    	GL11.glDisable(GL11.GL_TEXTURE_2D);	

		}
		
	}

	public void setUniformVariables(GameContainer gc)
	{
		//GL20.glUseProgram(shader.getID());
		shader.bind();
		int loc1 = GL20.glGetUniformLocation(shader.getID(), "time");

		GL20.glUniform1f(loc1, time);
		
		int loc2 = GL20.glGetUniformLocation(shader.getID(), "mouse");
		GL20.glUniform2f(loc2, (float)gc.getInput().getMouseX()/(float)gc.getWidth(), 1 - (float)gc.getInput().getMouseY()/(float)gc.getHeight());
		
		shader.setUniform2f("Resolution", (float)gc.getWidth(), (float)gc.getHeight());
		//shader.unbind();
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		// TODO Auto-generated method stub
		time += delta * 0.001f;
		setUniformVariables(container);
		
		if(a.isStopped())
			a.restart();
	}
	
    public void keyPressed(int key, char c) {
    	
    	game.enterState(Manager.StateIndex.MAIN_MENU.ordinal(), new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
    }
    	

	@Override
	public int getID() {
		return Manager.StateIndex.SHADER_TEST.ordinal();
	}

}