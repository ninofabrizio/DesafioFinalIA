package INF1771_GameAI;

import INF1771_GameAI.Map.AStar;
import INF1771_GameAI.Map.MapKnowledge;
import INF1771_GameAI.Map.Position;
import INF1771_GameAI.Map.Zone;

import java.util.ArrayList;
import java.util.List;

public class GameAI
{
	// The states of our AI's machine
	public enum State {
		allFine,
		sawEnemy,
		heardEnemy,
		sawDanger,
		sawHealth,
		sawTreasure,
		foundWall,
		aStar
	}
	
	State currentState = State.allFine;
	boolean shot = true;
	String lastCommand; //armazena o "undo" do último comando, ou seja, o comando contrário. Usado ao encontrar perigo.
	int shotsFired; //guarda quantos tiros foram dados sem confirmação de hit
	MapKnowledge map = new MapKnowledge();
	
	// Things for AStar
	int decisionsCount = 0;
	List<Zone> aStarPath = null;
	Zone nextDestination = null;
	
    Position player = new Position();
    String state = "ready";
    public String dir = "north";
    long score = 0;
    int energy = 0;

    
    /**
     * Refresh player status
     * @param x			player position x
     * @param y			player position y
     * @param dir		player direction
     * @param state		player state
     * @param score		player score
     * @param energy	player energy
     */
    public void SetStatus(int x, int y, String dir, String state, long score, int energy)
    {
        player.x = x;
        player.y = y;
        this.dir = dir.toLowerCase();

        if(map.getBotZone() != null)
        	map.getBotZone().setBot(null);
        
        map.getZones()[player.y][player.x].setBot(this);
        map.setBotZone(map.getZones()[player.y][player.x]);
        
        if(map.getBotZone().getType() == 'u')
        	map.getBotZone().setType('.');
        
        /*System.out.println("\nMAP:");
		for(int i = 0; i < 59; i++) {
			for(int j = 0; j < 34; j++)
				System.out.print(map.getZones()[i][j].getType() + " ");
			System.out.println();
		}*/
        
        this.state = state;
        this.score = score;
        this.energy = energy;        
    }

    /**
     * Get list of observable adjacent positions
     * @return List of observable adjacent positions 
     */
    public List<Position> GetObservableAdjacentPositions()
    {
        List<Position> ret = new ArrayList<Position>();

        ret.add(new Position(player.x - 1, player.y));
        ret.add(new Position(player.x + 1, player.y));
        ret.add(new Position(player.x, player.y - 1));
        ret.add(new Position(player.x, player.y + 1));

        return ret;
    }

    /**
     * Get list of all adjacent positions (including diagonal)
     * @return List of all adjacent positions (including diagonal)
     */
    public List<Position> GetAllAdjacentPositions()
    {
        List<Position> ret = new ArrayList<Position>();

        ret.add(new Position(player.x - 1, player.y - 1));
        ret.add(new Position(player.x, player.y - 1));
        ret.add(new Position(player.x + 1, player.y - 1));

        ret.add(new Position(player.x - 1, player.y));
        ret.add(new Position(player.x + 1, player.y));

        ret.add(new Position(player.x - 1, player.y + 1));
        ret.add(new Position(player.x, player.y + 1));
        ret.add(new Position(player.x + 1, player.y + 1));

        return ret;
    }

    /**
     * Get next forward position
     * @return next forward position
     */
    public Position NextPosition()
    {
        Position ret = null;
        if(dir.equals("north"))
                ret = new Position(player.x, player.y - 1);
        else if(dir.equals("east"))
                ret = new Position(player.x + 1, player.y);
        else if(dir.equals("south"))
                ret = new Position(player.x, player.y + 1);
        else if(dir.equals("west"))
                ret = new Position(player.x - 1, player.y);

        return ret;
    }

    /**
     * Player position
     * @return player position
     */
    public Position GetPlayerPosition()
    {
        return player;
    }
    
    /**
     * Set player position
     * @param x		x position
     * @param y		y position
     */
    public void SetPlayerPosition(int x, int y)
    {
        player.x = x;
        player.y = y;

    }

    /**
     * Observations received
     * @param o	 list of observations
     */
    public void GetObservations(List<String> o) {
		
		State lastState = currentState;
		currentState = State.allFine;
    	
		// aStar activation parameters
		if(aStarPath == null && decisionsCount == 40 && map.hasZone('p')) {
			
			decisionsCount = 0;
			currentState = State.aStar;
			aStarPath = getClosestPath('p');
			
			aStarPath.remove(aStarPath.size()-1); // Getting rid of the bot's position
			
			for(int k = 1; k < 13; k++) {
				for(int l = 1; l < 13; l++) {
					map.getZones()[k][l].setF(-1);
					map.getZones()[k][l].setG(-1);
					map.getZones()[k][l].setParent(null);
				}
			}
		}
		else if(aStarPath == null && energy <= 50 && map.hasZone('h')) {
			
			decisionsCount = 0;
			currentState = State.aStar;
			aStarPath = getClosestPath('h');
			
			aStarPath.remove(aStarPath.size()-1); // Getting rid of the bot's position
			
			for(int k = 1; k < 13; k++) {
				for(int l = 1; l < 13; l++) {
					map.getZones()[k][l].setF(-1);
					map.getZones()[k][l].setG(-1);
					map.getZones()[k][l].setParent(null);
				}
			}
		}
		else if(decisionsCount == 40)
			decisionsCount = 0;
		
		if(aStarPath == null){
			if (o.isEmpty())
				System.out.println("Sem observações");

			for (String s : o) {
				System.out.println("Observations: " + s);
				if (s.equals("blocked")) {
					currentState = State.foundWall;
				} else if (s.contains("enemy")) {
					currentState = State.sawEnemy;
				} else if (s.equals("steps")) {
					currentState = State.heardEnemy;
				} else if (s.equals("damage")) {
					currentState = State.heardEnemy;
				} else if (s.equals("breeze")) {
					currentState = State.sawDanger;
				} else if (s.equals("flash")) {
					currentState = State.sawDanger;
				} else if (s.equals("blueLight")) {
					currentState = State.sawHealth;
				} else if (s.equals("redLight")) {
					currentState = State.sawTreasure;
				} else if (s.equals("greenLight")) {
					currentState = State.allFine;
				} else if (s.equals("weakLight")) {
					currentState = State.allFine;
				} else if (s.equals("hit")) {

					shotsFired = 0;
					currentState = lastState;
				} else {
					currentState = State.allFine;
				}
			}
		}
        o.clear();
	}

    /**
     * No observations received
     */
    public void GetObservationsClean()
    {
        
    }

    /**
     * Get Decision
     * @return command string to new decision
     * 
     */
	public String GetDecision() {
		
		/*if(currentState == State.allFine){
			
			java.util.Random rand = new java.util.Random();

	    	int  n = rand.nextInt(2);
	    	
	    	switch(n){
		    	case 0:
		    		lastCommand = "virar_direita";
		            return "virar_esquerda";
		    	case 1:
		    		lastCommand = "andar_re";
		            return "andar";
	    	}
		}*/
		
		if(currentState != State.aStar)
			decisionsCount++;
		
		switch(currentState){
		
			case allFine:
				lastCommand = "andar";
				return "andar";
			
			case foundWall:
				
				if(lastCommand.contains("virar")) {
					lastCommand = "andar";
					return "andar";
				}
				
				lastCommand = "virar_esquerda";
				return "virar_esquerda";
				
			case sawDanger:
				if(lastCommand == null)
					return "andar_re";
				
				return lastCommand;
				
			case heardEnemy:
				
		    	if(shot) {
		    		
		    		shot = false;
		    		lastCommand = "virar_esquerda";
		    		return "virar_esquerda";
		    	}
		    	else { 
		    		
		    		shot = true;
		    		
		    		if(shotsFired < 5){
		    			shotsFired++;
		    			lastCommand = "atacar";
		    			return "atacar";
		    		}
		    		else{
		    			shotsFired = 0;
		    			lastCommand = "andar";
		    			return "andar";
		    		}
		    	}
		    case sawEnemy:
		    	lastCommand = "atacar";
		    	return "atacar";
			case sawHealth:
				map.getBotZone().setType('h');
				
				if(energy <= 70) {
					lastCommand = "pegar_powerup";
					return "pegar_powerup";
				}
				else {
					lastCommand = "andar";
					return "andar";
				}
			case sawTreasure:
				map.getBotZone().setType('p');
				
				lastCommand = "pegar_ouro";
				return "pegar_ouro";
				
			case aStar:
				
				if(nextDestination == null && aStarPath != null)
					nextDestination = aStarPath.remove(aStarPath.size()-1);
				
				if(map.getBotZone().getI() + 1 == nextDestination.getI() && map.getBotZone().getJ() == nextDestination.getJ()) {
						
					if(dir.contains("south")) {
						nextDestination = null;
						lastCommand = "andar";
						return "andar";
					}
					else if(dir.contains("west") || dir.contains("north")) {
						lastCommand = "virar_esquerda";
						return "virar_esquerda";
					}
					else if(dir.contains("east")) {
						lastCommand = "virar_direita";
						return "virar_direita";
					}
				}
				else if(map.getBotZone().getI() - 1 == nextDestination.getI() && map.getBotZone().getJ() == nextDestination.getJ() && this.dir.contains("north")) {
				
					if(dir.contains("north")) {
						nextDestination = null;
						lastCommand = "andar";
						return "andar";
					}
					else if(dir.contains("west") || dir.contains("south")) {
						lastCommand = "virar_direita";
						return "virar_direita";
					}
					else if(dir.contains("east")) {
						lastCommand = "virar_esquerda";
						return "virar_esquerda";
					}
				}
				else if(map.getBotZone().getJ() - 1 == nextDestination.getJ() && map.getBotZone().getI() == nextDestination.getI() && this.dir.contains("west")) {
				
					if(dir.contains("west")) {
						nextDestination = null;
						lastCommand = "andar";
						return "andar";
					}
					else if(dir.contains("south") || dir.contains("east")) {
						lastCommand = "virar_direita";
						return "virar_direita";
					}
					else if(dir.contains("north")) {
						lastCommand = "virar_esquerda";
						return "virar_esquerda";
					}
				}
				else if(map.getBotZone().getJ() + 1 == nextDestination.getJ() && map.getBotZone().getI() == nextDestination.getI() && this.dir.contains("east")) {
					
					if(dir.contains("east")) {
						nextDestination = null;
						lastCommand = "andar";
						return "andar";
					}
					else if(dir.contains("north") || dir.contains("east")) {
						lastCommand = "virar_direita";
						return "virar_direita";
					}
					else if(dir.contains("south")) {
						lastCommand = "virar_esquerda";
						return "virar_esquerda";
					}	
				}	
				
			default:
				lastCommand = "andar";
				return "andar";	
		}
	}
	
	// Returns the path to the closest zone of type asked
		private List<Zone> getClosestPath(char type) {
			
			List<Zone> closestPath = null;
			
			for(int i = 1, bestPathCost = 0; i < 13; i++) {
				for(int j = 1; j < 13; j++) {
					if(map.getZones()[i][j].getType() == type) {
						
						AStar star = new AStar(map.getZones(), map.getBotZone(), map.getZones()[i][j]);
						
						if(closestPath == null) {
							closestPath = star.aStar();
							
							if(closestPath == null)
								continue;
							
							bestPathCost = closestPath.get(0).getF();
						}
						else {
							List<Zone> candidatePath = star.aStar();
							
							if(candidatePath == null)
								continue;
							
							if(candidatePath.get(0).getF() < bestPathCost) {
								closestPath = candidatePath;
								bestPathCost = candidatePath.get(0).getF();
							}
						}
						
						for(int k = 1; k < 13; k++) {
							for(int l = 1; l < 13; l++) {
								map.getZones()[k][l].setF(-1);
								map.getZones()[k][l].setG(-1);
								map.getZones()[k][l].setParent(null);
							}
						}
					}
				}
			}
			
			return closestPath;
		}
}
