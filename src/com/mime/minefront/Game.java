package com.mime.minefront;

import java.awt.event.KeyEvent;

import com.mime.minefront.input.Controller;

public class Game {
	public int time;
	
	public Controller controls;
	
	public Game() {
		controls = new Controller();
		
	}
	
	public void tick(boolean[] key) {
		time++;
		boolean forward = key[KeyEvent.VK_W];
		boolean back = key[KeyEvent.VK_S];
		boolean left = key[KeyEvent.VK_A];
		boolean right = key[KeyEvent.VK_D];
		boolean turnRight = key[KeyEvent.VK_RIGHT];
		boolean turnLeft = key[KeyEvent.VK_LEFT];
		
		controls.tick(forward,back,left,right,turnRight,turnLeft);
	}
}
