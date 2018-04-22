package com.elliot.footballmanager.fixture;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.elliot.footballmanager.database.SqliteDatabaseConnector;
import com.elliot.footballmanager.footballteam.FootballTeam;
import com.elliot.footballmanager.footballteam.FootballTeamDao;
import com.elliot.footballmanager.footballteam.FootballTeamDaoImpl;

public class FixtureDaoImpl implements FixtureDao {
	
	private static SimpleDateFormat FIXTURE_DATE_FORMAT = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy");
	
	@Override
	public void insertFixturesIntoDatabase(List<String> allFixtures) {
		try (Connection conn = SqliteDatabaseConnector.connect();
				Statement stmt = conn.createStatement()) {
			for (String fixture : allFixtures) {
				stmt.executeUpdate(fixture);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Fixture> getFootballTeamsUpcomingFixtures(FootballTeam footballTeam) {
		String query = "SELECT * FROM FIXTURE WHERE HOME_TEAM = ? OR AWAY_TEAM = ?";
		
		try (Connection conn = SqliteDatabaseConnector.connect();
				PreparedStatement pstmt = conn.prepareStatement(query)) {
			if (footballTeam == null) {
				return new ArrayList<Fixture>();
			}
			
			pstmt.setString(1, footballTeam.getTeamName());
			pstmt.setString(2, footballTeam.getTeamName());
			
			ResultSet rs = pstmt.executeQuery();
			
			if (!rs.next()) {
				return new ArrayList<Fixture>();
			}
			
			List<Fixture> upcomingFixtures = new ArrayList<Fixture>();
			FootballTeamDao footballTeamDao = new FootballTeamDaoImpl();
			while (rs.next()) {
				FootballTeam homeTeam = footballTeamDao.getFootballTeamByName(rs.getString("HOME_TEAM"));
				FootballTeam awayTeam = footballTeamDao.getFootballTeamByName(rs.getString("AWAY_TEAM"));
				
				String dateString = rs.getString("DATE_OF_MATCH");
				Date date = FIXTURE_DATE_FORMAT.parse(dateString);
				
				upcomingFixtures.add(new Fixture(homeTeam, awayTeam,
						date, rs.getInt("LEAGUE_ID")));
			}
			return upcomingFixtures;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new ArrayList<Fixture>();
	}
}
