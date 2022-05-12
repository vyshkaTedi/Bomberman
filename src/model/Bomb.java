package model;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;

/**
 * Bomb objects that are created by players.
 * firepower Strength of the bomb explosionContact
 * pierce Whether or not the explosions will pierce soft walls
 */
public class Bomb extends GameObject{

    private final Player player;
    private double startTime;
    private final Board board;
    private final int firepower;
    private final boolean pierce;
    
    // Kicking bomb
    private boolean kicked;
    private KickDirection kickDirection;

    private int spriteIndex = -1;
    private boolean hasExploded = false;
    private int stopTop;
    private int stopDown;
    private int stopLeft;
    private int stopRight;
    private boolean willBeExploding;
    private int fuse;
    private ArrayList<Case> explodingCase;
    private boolean kill;


    /**
     * Constructs a bomb object with values passed in by a player object.
     * @param player Original player that placed this bomb
     */
    public Bomb(int x, int y, Player player, Board board) {
        super(x,y);

    	this.board = board;
    	
        // Stats
        this.firepower = player.getFirepower();
        this.pierce = player.getPierce();
        this.player = player;
        // Kicking bomb
        this.kicked = false;
        this.kickDirection = KickDirection.Nothing;
        this.willBeExploding = false;
        this.fuse = 0;
        this.startTime = System.currentTimeMillis();
        explodingCase = new ArrayList<>();

        //Set bomb in case
        board.getCases()[x][y].setBomb(this);


    }


    /**
     * Function that kills players in a cross-shaped area (with each extension of length firepower)
     * and destroy wall (if allowed)
     */
    public void explode() {
        if(hasExploded) return; //Pour qu'il n'y ait qu'un seul appel d'explode par bombes.
        hasExploded = true;
        
    	Case [][] c = board.getCases();
        int lineLeft = Math.max(((int) position.y - firepower), 0);
        int lineRight = Math.min(((int) position.y + firepower), 14);
        int columnTop = Math.max(((int) position.x - firepower), 0);
        int columnDown = Math.min(((int) position.x + firepower), 12);
        Case current = c[(int)position.x][(int)position.y];
        int i;
        boolean end = false;
        explodingCase.add(current);
        for(i = (int)position.y + 1 ;i <= lineRight && !end; i++ ){
            current = c[(int)position.x][i];
            if (current.getWall() != null) {
                if(current.getWall().isBreakable()) {
                    current.setWall(null);
                    end = (!pierce);
                }
                else {
                	end = true;
                }
            } else {
                explodingCase.add(current);
            }
		}
        stopRight = i;
        end = false;
        for(i = (int)position.y - 1 ;i >= lineLeft && !end; i-- ){
            current = c[(int)position.x][i];
            if (current.getWall() != null) {
                if(current.getWall().isBreakable()) {
                    current.setWall(null);
                    end = (!pierce);
                }
                else {
                	end = true;
                }
            } else {
                explodingCase.add(current);

            }
        }
        stopLeft = i;
        end = false;
        for(i = (int)position.x - 1 ; i >= columnTop && !end; i-- ){
            current = c[i][(int)position.y];
            if (current.getWall() != null) {
                if(current.getWall().isBreakable()) {
                    current.setWall(null);
                    end = (!pierce);
                    explodingCase.add(current);
                }
                else {
                	end = true;
                }
            } else {
                explodingCase.add(current);

            }
        }
        stopTop = i;
        end = false;
        for(i = (int)position.x + 1 ; i <= columnDown && !end; i++ ){
            current = c[i][(int)position.y];
            if (current.getWall() != null) {
                if(current.getWall().isBreakable()) {
                    current.setWall(null);
                    end = (!pierce);
                    explodingCase.add(current);
                }
                else {
                	end = true;
                }
            } else {
                explodingCase.add(current);
            }
        }
        stopDown = i;
    }

    public int kill() {
        int pointsCount = 0;
        for (Case c: explodingCase) {
            pointsCount += c.killMoveables(player);
        }
        return pointsCount;
    }
    
    /**
     * Plays audio file
     * @param soundFile audio file path
     */
    void playSound(String soundFile) throws Exception {
        File f = new File("resources/SFX/" + soundFile);
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(f.toURI().toURL());  
        Clip clip = AudioSystem.getClip();
        clip.open(audioIn);
        clip.start();
    }
    
    public void setKicked(boolean kicked, KickDirection kickDirection) {
        this.kicked = kicked;
        this.kickDirection = kickDirection;
    }

    public boolean isKicked() {
        return this.kicked;
    }

    public void stopKick() {
        this.kicked = false;
        this.kickDirection = KickDirection.Nothing;
    }

    public KickDirection getKick() {
    	return this.kickDirection;
    }


    // setter et getter :

    public double getStartTime() {
        return startTime;
    }


	public Player getPlayer() {
		return player;
	}

    public int getSpriteIndex() {
        return spriteIndex;
    }

    public void setSpriteIndex(int spriteIndex) {
        this.spriteIndex = spriteIndex;
    }
    
    public void setStartTime(double time) {
    	this.startTime = time;
    }

    public int getFirepower() {
        return firepower;
    }

    public int getStopDown() {
        return stopDown;
    }

    public int getStopLeft() {
        return stopLeft;
    }

    public int getStopRight() {
        return stopRight;
    }

    public int getStopTop() {
        return stopTop;
    }

    public boolean getwillBeExploding() {
        return willBeExploding;
    }

    public void setWillBeExploding() {
        this.willBeExploding = true;
    }
    public int getFuse() {
        return fuse;
    }

    public void setFuse(int fuse) {
        this.fuse = fuse;
    }
    public boolean getKill() {
        return kill;
    }
    public void setKill(boolean kill) {
        this.kill = kill;
    }
}

/**
 * Provides the speed for bomb moving from kick. Speed should be 6 to ensure the kicking logic is as smooth
 * as possible. Changing the value is dangerous and can introduce bugs to the kicking logic.
 */
enum KickDirection {

    FromTop(new Point2D.Float(1, 0)),
    FromBottom(new Point2D.Float(-1, 0)),
    FromLeft(new Point2D.Float(0, 1)),
    FromRight(new Point2D.Float(0, -1)),
    Nothing(new Point2D.Float(0, 0));

    private Point2D.Float velocity;

    KickDirection(Point2D.Float velocity) {
        this.velocity = velocity;
    }

	public Point2D.Float getVelocity() {
        return this.velocity;
    }

}