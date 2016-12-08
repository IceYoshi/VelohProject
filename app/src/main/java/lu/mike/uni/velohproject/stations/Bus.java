package lu.mike.uni.velohproject.stations;

/**
 * Created by Dren413 on 07.12.16.
 */

public class Bus {
    private String name;
    private String direction;

    public Bus(){}
    public Bus(String name, String direction){
        setName(name);
        setDirection(direction);
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
}
