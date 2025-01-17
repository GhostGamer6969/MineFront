package com.mime.minefront.input;

public class Controller {
	public double x,y, z, rotation, xa, za, rotationa;
	public static boolean turnLeft = false;
	public static boolean turnRight = false;
	public static boolean walk = false;
	public static boolean crouchWalk = false;
	public static boolean runWalk = false;

	public void tick(boolean forward, boolean back, boolean left, boolean right,boolean jump,boolean crouch,boolean run) {
		double rotationSpeed = 0.025;
		double walkSpeed = 0.5;
		double jumpHeight = 0.5;
		double crouchHeight = 0.3;	
		double xMove = 0;
		double zMove = 0;
		
		if(forward) {
			zMove++;
			walk = true;
			
		}
		if(back) {
			zMove--;
			walk = true;
		}
		if(right) {
			xMove++;
			walk = true;
		}
		if(left) {
			xMove--;
			walk = true;
		}
		if(turnLeft) {
			rotationa -= rotationSpeed;
		}
		if(turnRight) {
			rotationa += rotationSpeed;
		}
		
		if(jump) {
			y+= jumpHeight;
			run= false;
			walk=false;
		}
		if(crouch) {
			y-= crouchHeight;
			run = false; 	
			walkSpeed = 0.2;
		}
		if(run) {
			walkSpeed = 1;
			runWalk = true;
		}
		if(!run) {
			runWalk = false;
		}
		if(!forward && !back && !left && !right) {
			walk = false;
		}
		if(!crouch) {
			crouchWalk = false;
		}
		
		xa = (xMove * Math.cos(rotation)+zMove * Math.sin(rotation)) * walkSpeed;
		za = (zMove * Math.cos(rotation)+xMove * Math.sin(rotation)) * walkSpeed;
		
		x+=xa;
		z+=za;
		y*=0.9;
		xa*=0.1;
		za*=0.1;

		rotation+=rotationa;
		rotationa*=0.5;
		
	}
}
