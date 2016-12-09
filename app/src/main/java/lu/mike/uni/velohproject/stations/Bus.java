package lu.mike.uni.velohproject.stations;

/**
 * Created by Dren413 on 07.12.16.
 */

public class Bus {
    private String name;
    private String direction;
    private String time;

    public Bus(String name, String time, String direction){
        setName(name);
        setDirection(direction);
        setTime(time);
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

    public String getRtTime() {return this.time;}

    public void setTime(String rtTime) {this.time = rtTime;}
}
