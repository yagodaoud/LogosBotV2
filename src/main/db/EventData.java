package main.db;

public class EventData {
    private String userName;
    private String userId;
    private String guildId;
    private String textChannelId;
    private String dateAdded;
    private String type;
    private String threshold;
    private String priceTrendDesired;

    private String previousThreshold;

    public EventData(String userName, String userId, String guildId, String textChannelId, String dateAdded, String type, String threshold, String priceTrendDesired, String previousThreshold) {
        this.userName = userName;
        this.userId = userId;
        this.guildId = guildId;
        this.textChannelId = textChannelId;
        this.dateAdded = dateAdded;
        this.type = type;
        this.threshold = threshold;
        this.priceTrendDesired = priceTrendDesired;
        this.previousThreshold = previousThreshold;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserId() {
        return userId;
    }

    public String getGuildId() {
        return guildId;
    }

    public String getTextChannelId() {
        return textChannelId;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public String getType() {
        return type;
    }

    public String getThreshold() {
        return threshold;
    }
    public String getPriceTrendDesired() {
        return priceTrendDesired;
    }
    public String getPreviousThreshold() {
        return previousThreshold;
    }

    @Override
    public String toString() {
        return "EventData{" +
                "userName='" + userName + '\'' +
                ", userId='" + userId + '\'' +
                ", guildId='" + guildId + '\'' +
                ", textChannelId='" + textChannelId + '\'' +
                ", dateAdded='" + dateAdded + '\'' +
                ", type='" + type + '\'' +
                ", threshold='" + threshold + '\'' +
                ", threshold='" + threshold + '\'' +
                
                '}';
    }
}
