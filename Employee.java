public class Employee {
    public String name;
    public int score =0;
    public int promotionPoints = 0;
    public int monthlyBonus;
    public String place;
    public String job;
    public boolean wantToBeDismissed = false;
    public boolean eligibleForPromotion = false;

    public int newAddedBonus;
    public Employee(String place, String name, String job){
        this.place = place;
        this.name = name;
        this.job = job;
    }

    public void addScore(int added){
        score += added;
        organize();
    }

    public void organize(){
        promotionPoints += (score-(score%200))/200;
        int addedBonus = score%200;
        if(addedBonus>0){
            monthlyBonus += addedBonus;
            newAddedBonus += addedBonus;
            //totalBonus += addedBonus;
        }
    }
}
