package main.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SelectEvent {

    public static List<EventData> getEvent(String table) {
        List<EventData> result = new ArrayList<>();
        Connection connector = MySQLConnector.getConnection();

        String sql = "SELECT * FROM " + table + ";";

        try {
            PreparedStatement statement = connector.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String userName = resultSet.getString("UserName");
                String userId = resultSet.getString("UserId");
                String guildId = resultSet.getString("GuildId");
                String textChannelId = resultSet.getString("TextChannelId");
                String dateAdded = resultSet.getString("DateAdded");
                String type = resultSet.getString("Type");
                String threshold = resultSet.getString("Threshold");
                String priceTrendDesired = resultSet.getString("PriceTrendDesired");
                String previousThreshold = resultSet.getString("PreviousThreshold");
                EventData eventData = new EventData(userName, userId,  guildId, textChannelId, dateAdded, type, threshold, previousThreshold, priceTrendDesired);
                result.add(eventData);
            }
            resultSet.close();
            statement.close();
            connector.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

}
