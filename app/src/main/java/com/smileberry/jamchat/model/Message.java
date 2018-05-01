package com.smileberry.jamchat.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "message")
public class Message {

    @DatabaseField(generatedId = true, columnName = Constants.ID_FIELD)
    private Integer id;

    @DatabaseField(canBeNull = true, columnName = Constants.GCM_ID_FIELD)
    private String gcmId;

    @DatabaseField(canBeNull = false, columnName = Constants.MESSAGE_FIELD)
    private String message;

    @DatabaseField(canBeNull = false, columnName = Constants.LAT_FIELD)
    private String lat;

    @DatabaseField(canBeNull = false, columnName = Constants.LNG_FIELD)
    private String lng;

    @DatabaseField(canBeNull = false, columnName = Constants.TIME_FIELD)
    private long time;

    @DatabaseField(canBeNull = false, columnName = Constants.READ_FIELD)
    private Boolean readStatus = false;

    @DatabaseField(canBeNull = true, columnName = Constants.DEVICE_TOKEN)
    private String deviceToken;

    private String markerId;

    public Message() {
    }

    public Message(Integer id, String gcmId, String message, String lat, String lng, long time, boolean read) {
        this.id = id;
        this.gcmId = gcmId;
        this.message = message;
        this.lat = lat;
        this.lng = lng;
        this.time = time;
        this.readStatus = read;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGcmId() {
        return gcmId;
    }

    public void setGcmId(String gcmId) {
        this.gcmId = gcmId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getMarkerId() {
        return markerId;
    }

    public void setMarkerId(String markerId) {
        this.markerId = markerId;
    }

    public boolean isTheSameDevice(String deviceIdToCheck) {
        return this.gcmId != null && deviceIdToCheck != null && this.gcmId.equals(deviceIdToCheck);
    }

    public Boolean getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(Boolean readStatus) {
        this.readStatus = readStatus;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message1 = (Message) o;

        if (time != message1.time) return false;
        if (id != null ? !id.equals(message1.id) : message1.id != null) return false;
        if (gcmId != null ? !gcmId.equals(message1.gcmId) : message1.gcmId != null) return false;
        return !(message != null ? !message.equals(message1.message) : message1.message != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (gcmId != null ? gcmId.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (int) (time ^ (time >>> 32));
        return result;
    }
}
