package com.elliot.footballmanager.fixture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.elliot.footballmanager.footballteam.FootballTeam;
import com.elliot.footballmanager.footballteam.FootballTeamDao;
import com.elliot.footballmanager.footballteam.FootballTeamDaoImpl;
import com.elliot.footballmanager.league.League;
import com.elliot.footballmanager.league.LeagueDao;
import com.elliot.footballmanager.league.LeagueDaoImpl;

/**
 * Given a <link>League</link> containing all the <link>FootballTeam</link>'s 
 * that are part of that league this will return a collection of 
 * <link>Fixture</link>'s generated using a round robin scheduling system.
 * @author Elliot
 *
 */
public class FixtureGenerator {

	private Integer TOTAL_GAMES_IN_SEASON;
	private Integer HALF_GAMES_IN_SEASON;
	
	private List<League> leaguesForGeneration = new ArrayList<League>();
	private List<String> allFixtures = new ArrayList<String>();	
	
	public FixtureGenerator() {
		prepareFixtureGeneration();
		addFixturesToDatabase();
	}
	
	private void prepareFixtureGeneration() {
		LeagueDao leagueDao = new LeagueDaoImpl();
		this.setLeaguesForGeneration(leagueDao.getAllLeagues());
		
		FootballTeamDao footballTeamDao = new FootballTeamDaoImpl();
		for (League league : this.getLeaguesForGeneration()) {
			//TODO: Create a simpler way of getting the set of FootballTeams?
			// Can create the method in League or change SELECT statement
			league.setFootballTeams(new ArrayList<FootballTeam>(footballTeamDao.getAllFootballTeams(league.getLeagueId()).values()));
		}
		
		for (League league : this.getLeaguesForGeneration()) {
			allFixtures.addAll(buildFixtureList(league.getFootballTeams()));
		}
	}
	
	private List<String> buildFixtureList(List<FootballTeam> footballTeams) {	
		if (footballTeams.size() == 0 || footballTeams == null) {
			return new ArrayList<String>();
		}
		Collections.shuffle(footballTeams);
		
		List<String> selectedLeagueFixtures = new ArrayList<String>();	
		
		FootballTeam[] footballTeamsArray = new FootballTeam[footballTeams.size()];
		footballTeams.toArray(footballTeamsArray);
		
		TOTAL_GAMES_IN_SEASON = (footballTeams.size() * footballTeams.size()) - footballTeams.size();
		HALF_GAMES_IN_SEASON = TOTAL_GAMES_IN_SEASON / 2;
		
		// Home Fixtures
		while (selectedLeagueFixtures.size() != HALF_GAMES_IN_SEASON) {
			generateFixturesFromArray(selectedLeagueFixtures, footballTeamsArray);
			// Shift all teams one place to the right (Bar the first one | Round Robin method)
			shiftFootballTeamArray(Arrays.copyOfRange(footballTeamsArray, 1, footballTeamsArray.length));
		} 
		
		// Reverse FootballTeams array to generate away Fixtures
		reverseArrayOrder(footballTeamsArray);
		
		// Away Fixtures
		 while (selectedLeagueFixtures.size() != TOTAL_GAMES_IN_SEASON) {
			generateFixturesFromArray(selectedLeagueFixtures, footballTeamsArray);
			// Shift all teams one place to the right (Bar the first one | Round Robin method)
			shiftFootballTeamArray(Arrays.copyOfRange(footballTeamsArray, 1, footballTeamsArray.length));
		};
		
		return selectedLeagueFixtures;
	}
	
	private void generateFixturesFromArray(List<String> allFixtures, FootballTeam[] footballTeams) {
		int halfway = footballTeams.length / 2;
		for (int i = 0; i < footballTeams.length / 2; i++) {
			allFixtures.add(createFixtureInsertStatement(footballTeams[i], footballTeams[halfway], 
					new Date()));
			halfway++;
		}
	}

	private void shiftFootballTeamArray(FootballTeam[] footballTeams) {
		FootballTeam[] shiftedFootballTeams = new FootballTeam[footballTeams.length];
		for (int i = 0; i < footballTeams.length - 1; i++) {
			shiftedFootballTeams[i + 1] = footballTeams[i];
		}
		shiftedFootballTeams[0] = footballTeams[footballTeams.length - 1];
		footballTeams = shiftedFootballTeams;
	}
	
	private void reverseArrayOrder(FootballTeam[] footballTeams) {
		for (int i = 0; i < footballTeams.length / 2; i++) {
			FootballTeam temp = footballTeams[i];
			footballTeams[i] = footballTeams[footballTeams.length - i - 1];
			footballTeams[footballTeams.length - i - 1] = temp;
		}
	}
	
	private String createFixtureInsertStatement(FootballTeam homeTeam, FootballTeam awayTeam, Date dateOfFixture) {
		//TODO: Improve the creation of the INSERT method (String.format?)
		return "INSERT INTO FIXTURE (HOME_TEAM, AWAY_TEAM, DATE_OF_MATCH, LEAGUE_ID) "
				+ "VALUES ('"+homeTeam.getTeamName()+"', '"+awayTeam.getTeamName()+"'"
				+ ", '"+dateOfFixture+"', "+homeTeam.getLeagueId()+")";
	}
	
	private void addFixturesToDatabase() {
		FixtureDao fixtureDao = new FixtureDaoImpl();
		fixtureDao.insertFixturesIntoDatabase(allFixtures);
	}
	
	public List<League> getLeaguesForGeneration() {
		return leaguesForGeneration;
	}

	private void setLeaguesForGeneration(List<League> leaguesForGeneration) {
		this.leaguesForGeneration = leaguesForGeneration;
	}

	public List<String> getAllFixtures() {
		return allFixtures;
	}

	public void setAllFixtures(List<String> allFixtures) {
		this.allFixtures = allFixtures;
	}
}
