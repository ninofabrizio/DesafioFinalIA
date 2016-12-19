package INF1771_GameAI.Map;

public class MapKnowledge {
	
	private static Zone gameMap[][] = new Zone[59][34];
	
	private Zone botZone = null;
	private boolean prizeZoneFound = false;
	private boolean healthZoneFound = false;
	
	public MapKnowledge() {
		
		for(int i = 0; i < 59; i++)
			for(int j = 0; j < 34; j++) {
				
				gameMap[i][j] = new Zone();
				gameMap[i][j].setI(i);
				gameMap[i][j].setJ(j);
				gameMap[i][j].setType('u');
			}
	}
	
	public boolean hasZone(char type) {
		
		if((type == 'p' && !prizeZoneFound) || (type == 'h' && !healthZoneFound))
			for(int i = 0; i < 59; i++)
				for(int j = 0; j < 34; j++) {
					if(type == 'p' && gameMap[i][j].getType() == type) {
						prizeZoneFound = true;
						i = 58;
						break;
					}
					else if(type == 'h' && gameMap[i][j].getType() == type) {
						healthZoneFound = true;
						i = 58;
						break;
					}
				}
		
		if(type == 'p')
			return prizeZoneFound;
		else
			return healthZoneFound;
	}
	
	public static Zone[][] getZones() {	

		return gameMap;
	}

	public Zone getBotZone() {
		
		return botZone;
	}
	
	public void setBotZone(Zone zone) {
		
		botZone = zone;
		
		if(!botZone.isVisited())
			botZone.setVisited();
	}
}