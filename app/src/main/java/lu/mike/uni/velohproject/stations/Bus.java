package lu.mike.uni.velohproject.stations;

/**
 * Created by Dren413 on 07.12.16.
 */

public class Bus {
    private String name;
    private String direction;
    private String rtTime;

    public Bus(String name, String direction, String rtTime){
        setName(name);
        setDirection(direction);
        setRtTime(rtTime);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    public String getRtTime() {return rtTime;}

    public void setRtTime(String rtTime) {this.rtTime = rtTime;}
}
