package com.example.demo.beam;



/**
 * Description：describe this class function
 * Author；JinHuatao
 * Date: 2019/5/9 12:11
 */
public class AlarmTable {

    private String alarmid;

    private String alarmTitle;

    private String deviceModel;

    private Integer alarmSource;

    private String alarmMsg;

    private String alarmTime;

//    private String alarmTime = "未知";

    public String getAlarmid() {
        return alarmid;
    }

    public void setAlarmid(String alarmid) {
        this.alarmid = alarmid;
    }

    public String getAlarmTitle() {
        return alarmTitle;
    }

    public void setAlarmTitle(String alarmTitle) {
        this.alarmTitle = alarmTitle;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public Integer getAlarmSource() {
        return alarmSource;
    }

    public void setAlarmSource(Integer alarmSource) {
        this.alarmSource = alarmSource;
    }

    public String getAlarmMsg() {
        return alarmMsg;
    }

    public void setAlarmMsg(String alarmMsg) {
        this.alarmMsg = alarmMsg;
    }

    public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }

   /* public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }*/


    @Override
    public String toString() {
        return "AlarmTable{" +
                "alarmid='" + alarmid + '\'' +
                ", alarmTitle='" + alarmTitle + '\'' +
                ", deviceModel='" + deviceModel + '\'' +
                ", alarmSource=" + alarmSource +
                ", alarmMsg='" + alarmMsg + '\'' +
                ", alarmTime='" + alarmTime + '\'' +
                '}';
    }
}
