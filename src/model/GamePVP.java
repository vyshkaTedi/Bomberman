package model;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.awt.event.KeyEvent;

import controller.PlayerInput;
import view.Gui;

public class GamePVP extends Game{
    private ArrayList<Player> playerList;
    private PlayerInput key1,key2,key3,key4;
    private Player player1,player2,player3,player4;
    private Board board;
    private Gui gui;
    public static double timer;
    private ArrayList<Monster> monsterList;
    // should use game class as starter for choosing modes
    public GamePVP() {
        playerList = new ArrayList<Player>();
        monsterList = new ArrayList<Monster>();
    }

    public void init() {
		try {
			board = new Board("maps/default.csv",playerList,monsterList); // fait
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		this.gui=new Gui(board);
        key1 = new PlayerInput(board.getPlayer(0));
    	gui.addKeyListener(key1);
        key2 = new PlayerInput(board.getPlayer(1));
    	gui.addKeyListener(key2);
        //key3 = new PlayerInput(board.getPlayer(2));
    	//gui.addKeyListener(key3);
        monsterList.add(new WalkingMonster(0, 0, board));
        monsterList.add(new FlyingMonster(0, 0, board));
        //monsterList.add(new MonstreDeux(0, 0, board));
        //key4 = new PlayerInput(board.getPlayer(3));
        //gui.addKeyListener(key4);
        this.addPlayers();
    }

    public void addPlayers() {
        try {
            player1 = board.getPlayer(0);
            player1.setPlayer(0, 1.4F, 1.4F);
            player1.bindKeys(KeyEvent.VK_Z, KeyEvent.VK_S, KeyEvent.VK_Q, KeyEvent.VK_D, KeyEvent.VK_CONTROL);
            player2 = board.getPlayer(1);
            player2.setPlayer(1,1.4F,13.4F);
            player2.bindKeys(KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT,KeyEvent.VK_ALT_GRAPH);
            /*
            player3 = board.getPlayer(2);
            player3.setPlayer(2,11.4F, 1.4F);
            player3.bindKeys(KeyEvent.VK_NUMPAD8, KeyEvent.VK_NUMPAD5, KeyEvent.VK_NUMPAD4, KeyEvent.VK_NUMPAD6,KeyEvent.VK_NUMPAD2);
            */
            //player4 = board.getPlayer(3);
            //player4.setPlayer(3,11.4F, 13.4F);
            //player4.bindKeys(KeyEvent.VK_U, KeyEvent.VK_J, KeyEvent.VK_H, KeyEvent.VK_K,KeyEvent.VK_SPACE);
            board.getMonster(0).setMonster(11.4F, 13.4F);
            board.getMonster(1).setMonster(11.4F, 1.4F);
            // should be in gamemonsters
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void gameLoop() {

        double loopTimeInterval = 1000 / FPS;
        double lastTime = System.currentTimeMillis();
        double currentTime;

        while(!this.hasEnded()){
            long startLoopTime = System.currentTimeMillis();

            //instructions timer
            timer += (startLoopTime - lastTime);
            lastTime = startLoopTime;
            // fin timer

            //début des instructions de jeu
            bombUpdate();
            playerUpdate(loopTimeInterval);
            monsterUpdate(loopTimeInterval);
            gui.repaint();
            //fin des instructions de jeu

            long endLoopTime = System.currentTimeMillis();
            try{
                Thread.sleep((long)loopTimeInterval - (endLoopTime - startLoopTime));
            }catch (java.lang.InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    private void playerUpdate(double deltaTime) {
        for(Player p : playerList){
            p.update(deltaTime);
        }
    }

    private void monsterUpdate(double deltaTime) {
        for(Monster m : monsterList){
            m.update(deltaTime);
        }
    }
    
    private void bombUpdate() {
        for(Player p : playerList){
            p.bombUpdate();
        }
    }

    private double printTime(double timer2) {
        if(timer >= timer2 + 100){
            //System.out.println("---------------------------------- Timer : " + (int)timer/1000 + " s " + (int)timer%1000/100 + " ms " + " ------------------------------------");
            return timer;
        }
        return timer2;
    }

    @Override
    public boolean hasEnded() { // verification de la victoire
        return false;
    }
    public static void main(String[] args){
        GamePVP game=new GamePVP();
        game.init();
        game.gameLoop();
    }
}
