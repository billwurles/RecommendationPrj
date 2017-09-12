package database;

public class Genre implements Comparable{
    private String name;
    private int weight;

    public Genre(String name) {
        this.name = name;
        this.weight = -1;
    }

    public String getName() {
        return name;
    }
    
    public void setWeight(int weight){
        this.weight = weight;
    }
    
    public int getWeight(){
        return weight;
    }
    
    public int simMod(int rating){
        if(weight < 2){
            return rating / 2;
        } else {
            return rating / (weight / 2);
        }
    }
    
    @Override
    public String toString(){
        return name;
    }
    
    @Override
    public int compareTo(Object comp) {
        int compareWeight = ((Genre)comp).getWeight();
        return this.weight-compareWeight;
    }
    
}
