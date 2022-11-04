package deedee;
import robocode.*;
import java.awt.*;

// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * DevilRobot_v2_0 - a robot by LittleDevils
 */
public class LittleDevilRobot extends AdvancedRobot 
{
	//Default 
	int moveDirection=1;
	boolean isNearWall;
	boolean movingForward; // Is set to true when setAhead is called, set to false on setBack 
	double battleFieldWidth;
	double battleFieldHeight;
	double bulletPower;
	boolean escapingWall;
	boolean tooSlow;
	int turnNumber = 0;
	
	public void run() {

          setColors(
            new Color(199, 1, 96),     //bodyColor
            new Color(11, 22, 65),      //gunColor
            new Color(11, 22, 65),    //radarColor
            Color.white,        //bulletColor
            Color.red);        //scanArcColor
		
		double robotHeight = getHeight();
		double robotWidth =  getWidth();
		
		battleFieldWidth = getBattleFieldWidth();
		battleFieldHeight = getBattleFieldHeight();
					
  		setAdjustRadarForRobotTurn(true);//keep the radar still while we turn      
		setAdjustGunForRobotTurn(true); // Keep the gun still when we turn
		setAdjustRadarForGunTurn(true);	
		
		turnRadarRightRadians(Double.POSITIVE_INFINITY);//keep turning radar right

		setAhead(battleFieldWidth/4); // go ahead until you get commanded to do differently
		setTurnRadarRight(360); // scan until you find your first enemy		
		
		while (true)
		{
			// If the radar stopped turning, take a scan of the whole field until we find a new enemy
			if (getRadarTurnRemaining() == 0.0){
			setTurnRadarRight(360);
			}
									
			if(escapingWall){
			if(getDistanceRemaining()<10){
			escapingWall=false;
			}
			}
			execute(); // execute all actions set.
		}
	}
	

	public void onScannedRobot(ScannedRobotEvent e) {
		turnNumber++;
		        setColors(
                new Color(199, 1, 96),     //bodyColor
                new Color(11, 22, 65),      //gunColor
                new Color(11, 22, 65),    //radarColor
                getBulletColor(turnNumber),        //bulletColor
                Color.red);        //scanArcColor
		double absBearing = e.getBearingRadians() + getHeadingRadians();//enemies absolute bearing
	    double latVel = e.getVelocity() * Math.sin(e.getHeadingRadians() - absBearing);//enemies later velocity
	    double gunTurnAmt;//amount to turn our gun
		setTurnRadarLeftRadians(getRadarTurnRemainingRadians());//lock on the radar
		
		if(getEnergy()<70){
			setAhead(e.getDistance()/2);
		}
		
		if(e.getVelocity()==0){
		fire(3);
		}

		if(getVelocity()<4){
		out.println("too slow");
		escapingWall=true;
		tooSlow=true;
		}else{
		tooSlow=false;
		}
				
		if(bulletPower==0)
		{
			bulletPower = 1;
		}
		
		//we are close enough and we cannot miss by that much so we hit hard
		if(e.getDistance()<50){
			bulletPower = 3;
		}	

		
	    if(Math.random()>.5){
	        setMaxVelocity((12*Math.random())+10);//randomly change speed
	    }
	    if (e.getDistance() > 400) {//if distance is greater than 150
			if(!tooSlow){
	        gunTurnAmt = robocode.util.Utils.normalRelativeAngle(absBearing- getGunHeadingRadians()+latVel/18);//amount to turn our gun, lead just a little bit
	        setTurnGunRightRadians(gunTurnAmt); //turn our gun
	        setTurnRightRadians(robocode.util.Utils.normalRelativeAngle(absBearing-getHeadingRadians()+latVel/getVelocity())+Math.random()*25);//drive towards the enemies predicted future location
			}
	        
			if(!escapingWall){
				setAhead((e.getDistance()/(10*Math.random())) * moveDirection);//move forward
			}
	        
			setFire(1);//we are too far, we always fire level 1
	    }
	    else{//if we are close enough...
			if(!tooSlow){
	        gunTurnAmt = robocode.util.Utils.normalRelativeAngle(absBearing- getGunHeadingRadians()+latVel/15);//amount to turn our gun, lead just a little bit
	        setTurnGunRightRadians(gunTurnAmt);//turn our gun
	        setTurnLeft(-92-e.getBearing()); //turn perpendicular to the enemy
			}
			
			if(!escapingWall){
	        setAhead((e.getDistance()/(10*Math.random()))*moveDirection);//move forward
			}
						
	        setFire(bulletPower);//fire
	    }
		
			if(tooSlow && getDistanceRemaining()<10){
			setTurnRight(e.getBearing()+60*Math.random());
			if(Math.random()<0.5){
			setAhead(Math.random()*200);
			}else{
			setBack(Math.random()*200);
			}
			}
			execute();

	}
	
	public void onBulletMissed(BulletMissedEvent event) {
     	bulletPower = 2;
   	}
	
	public void onBulletHit(BulletHitEvent event) {
       	bulletPower = 2.5;
   }

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		//setTurnLeft(-180*Math.random()-e.getBearing()); //turn perpendicular to the enemy
		setMaxVelocity(7+7*Math.random());//randomly change speed
		if(Math.random()<0.5){
		//setAhead(100);//move forward
		}else{
		//setBack(100);
		}
		

	}
	
	public void onHitWall(HitWallEvent e) {
		escapingWall=true;
		// Bounce off!
		if(e.getBearing()<0){
	   setTurnLeft(-90-e.getBearing()); //turn perpendicular to the enemy
	   setAhead(battleFieldWidth/8);//move forward		
		}else{
		setTurnRight(-90-e.getBearing()); //turn perpendicular to the enemy
	   setAhead(battleFieldWidth/8);//move forward
		}

	}
	
    public Color getBulletColor(int tickCount) {
            
        Color bulletColor = Color.white;
        
        switch(tickCount%7) {
          case 0:
            bulletColor = Color.red;
            break;
          case 1:
            bulletColor = Color.orange;
            break;
          case 2:
            bulletColor = Color.yellow;
            break;
          case 3:
            bulletColor = Color.green;
            break;
          case 4:
            bulletColor = Color.blue;
            break;
          case 5:
            bulletColor = new Color(75, 0, 130);
            break;
          case 6:
            bulletColor = new Color(148, 0, 211);
            break;
          default:
            bulletColor = Color.white;
        }
        
        return bulletColor;
        
    }	
		

}
