package jp.co.basenet.wg.cfroomserver.model;

public class RoomDetailInfo {
    int id;
    String meetingName;
    String locate;
    String startTime;
    String endTime;
    String chairManUserId;
    String chairManName;
    String status;

	public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMeetingName() {
        return meetingName;
    }

    public void setMeetingName(String meetingName) {
        this.meetingName = meetingName;
    }

    public String getLocate() {
        return locate;
    }

    public void setLocate(String locate) {
        this.locate = locate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getChairManUserId() {
        return chairManUserId;
    }

    public void setChairManUserId(String chairManUserId) {
        this.chairManUserId = chairManUserId;
    }

    public String getChairManName() {
        return chairManName;
    }

    public void setChairManName(String chairManName) {
        this.chairManName = chairManName;
    }
    

    public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
