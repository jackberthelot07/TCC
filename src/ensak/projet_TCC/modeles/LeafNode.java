package ensak.projet_TCC.modeles;

import java.util.HashSet;
import java.util.Set;

public class LeafNode extends Node {
    private int number;
    private Set<Integer> followPos;

    public LeafNode(String symbol, int number) {
        super(symbol);
        this.followPos = new HashSet<>();
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Set<Integer> getFollowPos() {
        return followPos;
    }

    public void setFollowPos(Set<Integer> followPos) {
        this.followPos = followPos;
    }

    public void addToFollowPos(int number){
        followPos.add(number);
    }


}
