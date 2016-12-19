package INF1771_GameAI.Map;

import INF1771_GameAI.GameAI;

public class Zone {

	// Dictionary for types:
	// '.' == nothing
	// 'd' == danger (hole)
	// 'w' == wall
	// 'p' == prize
	// 'h' == health
	// 'u' == unknown
	
	private int i, j;
	private char type;
	private GameAI bot = null;
	//private Enemy enemy;
	private boolean visited = false, /*stepSounds = false,*/ breeze = false, flash = false,
					/*damageEnemyDoubt = false,*/ holeWallDoubt = false, teleportEnemyDoubt = false;
	
	// For AStar usage
	private int g = -1; // G
    private int f = -1; // G + H
	private int h;
	private Zone parent;
    
	public void setI(int i) {
		this.i = i;
	}
	
	public void setJ(int j) {
		this.j = j;
	}
    
	public void setType(char t) {
		type = t;
	}
	
	public void setBot(GameAI b) {
		bot = b;
	}
	
	/*public void setEnemy(Enemy e) {
		enemy = e;
	}*/
	
	public void setVisited() {
		visited = true;
	}
	
	/*public void setStepSounds(boolean condition) {
		stepSounds = condition;
	}*/

	public void setBreeze(boolean condition) {
		breeze = condition;
	}

	public void setFlash(boolean condition) {
		flash = condition;
	}
	
	/*public void setDamageEnemyDoubt(boolean condition) {
		damageEnemyDoubt = condition;
	}*/

	public void setHoleWallDoubt(boolean condition) {
		holeWallDoubt = condition;
	}

	public void setTeleportEnemyDoubt(boolean condition) {
		teleportEnemyDoubt = condition;
	}
	
	public void setG(int g) {
		this.g = g;
	}
	
	public void setF(int f) {
		this.f = f;
	}
	
	public void setH(int h) {
		this.h = h;
	}
	
	public void setParent(Zone parent) {
		this.parent = parent;
	}
	
	public int getI() { 
		return i;
	}
	
	public int getJ() {
		return j;
	}
	
	public char getType() {
		return type;
	}
	
	public GameAI getBot() {
		return bot;
	}
	
	/*public Enemy getEnemy() {
		return enemy;
	}*/
	
	public int getG() {
		return g;
	}
	
	public int getF() {
		return f;
	}
	
	public int getH() {
		return h;
	}
	
	public Zone getParent() {
		return parent;
	}
	
	public boolean isVisited() {
		return visited;
	}
	
	/*public boolean isStepSounds() {
		return stepSounds;
	}*/

	public boolean isBreeze() {
		return breeze;
	}

	public boolean isFlash() {
		return flash;
	}
	
	/*public boolean isDamageEnemyDoubt() {
		return damageEnemyDoubt;
	}*/

	public boolean isHoleWallDoubt() {
		return holeWallDoubt;
	}

	public boolean isTeleportEnemyDoubt() {
		return teleportEnemyDoubt;
	}
}