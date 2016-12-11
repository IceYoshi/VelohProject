package lu.mike.uni.velohproject;

import java.util.HashMap;

/**
 * Created by Dren413 on 07.12.16.
 */

interface ICountDownTerminatorProtocol{
    void didFinishCountdown();
}

public class CountDownTerminator {
    private HashMap<String, Integer> map = new HashMap<>();
    private ICountDownTerminatorProtocol delegator;

    public  CountDownTerminator(ICountDownTerminatorProtocol delegator){this.delegator = delegator;}

    public void addCounter(String key, int finalValue){
        getMap().put(key, finalValue);
    }

    public void incProgress(String key){
        getMap().put(key, getMap().get(key) - 1);

        if(finish()) {
            delegator.didFinishCountdown();
            for(String k : map.keySet())
                map.put(k, -1);
        }
    }

    public Boolean finish(){
        Boolean finish = true;
        for(Integer v : getMap().values())
            finish = finish & (v==0);

        return finish;
    }

    public void clear(){
        getMap().clear();
    }

    public HashMap<String, Integer> getMap() {
        return map;
    }
}
