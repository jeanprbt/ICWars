package ch.epfl.cs107.play.game.areagame.actor;

import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.math.Positionable;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;


public class RPGSprite extends Sprite {

	private final float depthCorrection;

	/**
	 * Creates a new Sprite.
	 * @param name (String): image name, may be null
	 * @param width (int): actual image width, before transformation
	 * @param height (int): actual image height, before transformation
	 * @param parent (Positionable): parent of this, not null
	 * @param roi (RegionOfInterest): region of interest into the image as a rectangle in the image. May be null
	 * @param anchor (Vector): image anchor, not null
	 * @param alpha (float): transparency, between 0 (invisible) and 1 (opaque)
	 * @param depthCorrection (float): correction of the deepness defined by the parent position if exists
	 */
	public RPGSprite(String name, float width, float height, Positionable parent, RegionOfInterest roi, Vector anchor, float alpha, float depthCorrection) {
		super(name, width, height, parent, roi, anchor, alpha, -parent.getPosition().y+depthCorrection);
		this.depthCorrection = depthCorrection;
	}

	/**
	 * Creates a new image graphics.
	 * @param name (String): image name, not null
	 * @param width (int): actual image width, before transformation
	 * @param height (int): actual image height, before transformation
	 * @param parent (Positionable): parent of this, not null
	 * @param roi (RegionOfInterest): region of interest into the image as a rectangle in the image. May be null
	 * @param anchor (Vector): image anchor, not null
	 */
	public RPGSprite(String name, float width, float height, Positionable parent, RegionOfInterest roi, Vector anchor) {
		super(name, width, height, parent, roi, anchor);
		this.depthCorrection = 0;
	}

	/**
	 * Creates a new image graphics.
	 * @param name (String): image name, not null
	 * @param width (int): actual image width, before transformation
	 * @param height (int): actual image height, before transformation
	 * @param parent (Positionable): parent of this, not null
	 * @param roi (RegionOfInterest): region of interest into the image as a rectangle in the image. May be null
	 */
	public RPGSprite(String name, float width, float height, Positionable parent, RegionOfInterest roi) {
		super(name, width, height, parent, roi);
		this.depthCorrection = 0;
	}

	/**
	 * Creates a new image graphics.
	 * @param name (String): image name, not null
	 * @param width (int): actual image width, before transformation
	 * @param height (int): actual image height, before transformation
	 * @param parent (Positionable): parent of this, not null
	 */
	public RPGSprite(String name, float width, float height, Positionable parent) {
		super(name, width, height, parent);
		this.depthCorrection = 0;
	}

	@Override
	public void draw(Canvas canvas) {
		if(getParent() != null){
			setDepth(-getParent().getPosition().y+depthCorrection);
		}
		super.draw(canvas);
	}
	// Utilities for creating animations from sprites

	/**
	 * Creates an array of 4 animations (one animation per orientation)
	 * the entry indexed by Orientation.dir.ordinal() is the animation corresponding 
	 * to the orientation Orientation.dir
	 * @param animationDuration (int): the animation duration
	 * @param sprites (Sprite[][]): sprites to be played by each animation
	 * sprites[Orientation.dir.ordinal()] is the set of sprites to be played 
	 * by the animation indexed by Orientation.dir.ordinal()
	 * @param repeat (boolean) : true if the animations must be repeated
	 * @return an array of 4 animations (one animation per orientation)
	 */
	public static Animation[] createAnimations(int animationDuration, Sprite[][] sprites, boolean repeat)
	{
		Animation[] animations = new Animation[4];
		for (Orientation direction : Orientation.values()) {	
			int index = direction.ordinal();
			animations[index] = new Animation(animationDuration, sprites[index], repeat);
		}
		return animations;
	}
	
	/**
	 * Creates an array of 4 animations (one animation per orientation)
	 * the entry indexed by Orientation.dir.ordinal() is the animation corresponding 
	 * to the orientation Orientation.dir. The animations are repeated by default.
	 * @param animationDuration (int): the animation duration
	 * @param sprites (Sprite[][]): sprites to be played by each animation
	 * sprites[Orientation.dir.ordinal()] is the set of sprites to be played by 
	 * the animation indexed by Orientation.dir.ordinal()
	 * @return an array of 4 animations (one animation per orientation)
	 */
	public static Animation[] createAnimations(int animationDuration, Sprite[][] sprites)
	{
		return createAnimations(animationDuration, sprites, true);
	}
	
	/**
	 * Extracts from an image the sprites corresponding to a given orientation
	 * the returned array has 4 entry (one per orientation)
	 * the content of each entry is an array of sprites corresponding to the given orientation
	 * (the entry indexed by Orientation.dir.ordinal() is the array of sprites corresponding 
	 * to the orientation Orientation.dir). 
	 * @param name (String): the name of the image 
	 * @param nbFrames (int): number of frames in each row
	 * @param width (int): actual image width, before transformation
     * @param height (int): actual image height, before transformation
     * @param parent (Positionable): parent of this, not null
     * @param regionWidth (int): width of frame (number of pixels in the image)
     * @param regionHeight (int): height of frame (number of pixels in the image)
	 * @param order (Orientation[]): order of the frames in the image
	 *
	 *
	 * @return an array of 4 Sprite[] (one Sprite[] per orientation)
	 */
	public static Sprite[][] extractSprites(String name, int nbFrames, float width, float height, Positionable parent, int regionWidth, int regionHeight, Orientation[] order){

		Sprite[][] sprites = new Sprite[4][nbFrames];

		for(int i = 0; i < nbFrames; i++){
			int j = 0;
			sprites[order[0].ordinal()][i]  = new RPGSprite(name, width, height, parent, new RegionOfInterest(i*regionWidth, regionHeight*j++, regionWidth, regionHeight));
			sprites[order[1].ordinal()][i]  = new RPGSprite(name, width, height, parent, new RegionOfInterest(i*regionWidth, regionHeight*j++, regionWidth, regionHeight));
			sprites[order[2].ordinal()][i]  = new RPGSprite(name, width, height, parent, new RegionOfInterest(i*regionWidth, regionHeight*j++, regionWidth, regionHeight));
			sprites[order[3].ordinal()][i]  = new RPGSprite(name, width, height, parent, new RegionOfInterest(i*regionWidth, regionHeight*j, regionWidth, regionHeight));
		}
		return sprites;
	}

	/**
	 * Extracts from an image the sprites corresponding to a given orientation
	 * the returned array has 4 entry (one per orientation)
	 * the content of each entry is an array of sprites corresponding to the given orientation
	 * (the entry indexed by Orientation.dir.ordinal() is the array of sprites corresponding 
	 * to the orientation Orientation.dir). 
	 * @param name (String): the name of the image 
	 * @param nbFrames (int): number of frames in each row
	 * @param width (int): actual image width, before transformation
     * @param height (int): actual image height, before transformation
     * @param parent (Positionable): parent of this, not null
     * @param anchor (Vector) : image anchor, not null
     * @param regionWidth (int): width of frame (number of pixels in the image)
     * @param regionHeight (int): height of frame (number of pixels in the image)
	 * @param order (Orientation[]): order of the frames in the image
	 *
	 *
	 * @return an array of 4 Sprite[] (one Sprite[] per orientation)
	 */
	public static Sprite[][] extractSprites(String name, int nbFrames, float width, float height, Positionable parent, int regionWidth, int regionHeight, Vector anchor, Orientation[] order){  	

		Sprite[][] sprites = new Sprite[4][nbFrames];

		for(int i = 0; i < nbFrames; i++){
			int j = 0;
			sprites[order[0].ordinal()][i]  = new RPGSprite(name, width, height, parent, new RegionOfInterest(i*regionWidth, regionHeight*j++, regionWidth, regionHeight), anchor);
			sprites[order[1].ordinal()][i]  = new RPGSprite(name, width, height, parent, new RegionOfInterest(i*regionWidth, regionHeight*j++, regionWidth, regionHeight), anchor);
			sprites[order[2].ordinal()][i]  = new RPGSprite(name, width, height, parent, new RegionOfInterest(i*regionWidth, regionHeight*j++, regionWidth, regionHeight), anchor);
			sprites[order[3].ordinal()][i]  = new RPGSprite(name, width, height, parent, new RegionOfInterest(i*regionWidth, regionHeight*j, regionWidth, regionHeight), anchor);
		}
		return sprites;
	}
}
