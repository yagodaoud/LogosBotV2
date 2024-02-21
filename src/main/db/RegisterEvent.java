package main.db;

import java.sql.Connection;
import java.sql.PreparedStatement;

import java.util.Map;

public class RegisterEvent {

    public static final String INSERT = "INSERT";
    public static final String UPDATE = "UPDATE";
    public static final String DELETE = "DELETE";

    public static int registerEventOnDB(String table, String type, Map<String, String> info) {

        Connection connector = MySQLConnector.getConnection();

        switch (type) {
            case INSERT -> {
                String columns = " (UserName, UserId, GuildId, TextChannelId, DateAdded, Type, Threshold, PreviousThreshold, PriceTrendDesired) ";

                String sql =
                        "INSERT INTO " +
                            table +
                            columns +
                        "VALUES " +
                            "(" +
                                "'" + info.get("UserName") + "'" + "," +
                                "'" + info.get("UserId") + "'" + "," +
                                "'" + info.get("GuildId")  + "'" + "," +
                                "'" + info.get("TextChannelId")  + "'" + "," +
                                "'" + info.get("DateAdded")  + "'" + "," +
                                "'" + info.get("Type") + "'"  + "," +
                                "'" + info.get("Threshold") + "'" + "," +
                                "'" + info.get("PreviousThreshold") + "'" + "," +
                                info.get("PriceTrendDesired") +
                            ")" +
                        ";";

                System.out.println(sql);

                try {
                    PreparedStatement statement = connector.prepareStatement(sql);
                    statement.executeUpdate();
                    connector.close();
                    statement.close();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            }
            case UPDATE -> {
                String sql =
                        "UPDATE " +
                            table +
                        " SET " +
                            "UserName = " + "'" + info.get("UserName") + "'" +
                            ", UserId = " + "'" + info.get("UserId") + "'" +
                            ", GuildId = " + "'" + info.get("GuildId") + "'" +
                            ", TextChannelId = " +  "'" + info.get("TextChannelId") +  "'" +
                            ", DateAdded = " + "'" + info.get("DateAdded") + "'" +
                            ", Type = " + "'" + info.get("Type") + "'" +
                            ", Threshold = " + "'" + info.get("Threshold") + "'" +
                            ", PreviousThreshold = " + "'" + info.get("PreviousThreshold") + "'" +
                            ", PriceTrendDesired = " + "'" + info.get("PriceTrendDesired") + "'" +
                        " WHERE " +
                            "TextChannelId = " +  "'" + info.get("TextChannelId") + "'" +
                            " AND " + "GuildId = " + "'" + info.get("GuildId") + "'" +
                            " AND " + "UserName LIKE " + "'" + info.get("UserName") + "'" +
                        ";";

                System.out.println(sql);

                try {
                    PreparedStatement statement = connector.prepareStatement(sql);
                    statement.executeUpdate();
                    connector.close();
                    statement.close();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            case DELETE -> {
                String sql =
                        "DELETE FROM " +
                            table +
                        " WHERE " +
                            "TextChannelId = " + "'" + info.get("TextChannelId") + "'" +
                            " AND " + "GuildId = " + "'" + info.get("GuildId") + "'";

                if (info.containsKey("UserId")) {
                    sql += " AND " + "UserId LIKE " + "'" + info.get("UserId") + "'";
                }
                if (info.containsKey("Type")) {
                    sql += " AND " + "Type LIKE " + "'" + info.get("Type") + "'";
                }
                    sql += ";";

                System.out.println(sql);

                try {
                    PreparedStatement statement = connector.prepareStatement(sql);
                    int affectedRows = statement.executeUpdate();
                    connector.close();
                    statement.close();

                    return affectedRows;
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        return 0;
    }
}
