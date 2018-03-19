package com.elliot.footballmanager.footballteam;

import java.util.Map;

/**
 * @author Elliot
 *
 */
public interface FootballTeamDao {
	
	/**
	 * @param leagueId The leagueId to return all the FootballTeams currently
	 * associated with that leagueId.
	 * @return A Map of all the FootballTeams that are currently within the 
	 * League via the leagueId.
	 * Key : footballTeamId
	 * Value : FootballTeam object
	 */
	public Map<Integer, FootballTeam> getAllFootballTeams(Integer leagueId);
	
	/**
	 * @param footballTeamId The unique Id for the FootballTeam you want to retrieve.
	 * @return FootballTeam information linked to the given footballTeamId.
	 */ 
	public FootballTeam getFootballTeamById(Integer footballTeamId);

}
