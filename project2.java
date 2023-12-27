import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.io.File;
import java.util.List;


public class project2 {
    public static void main(String[] args) throws IOException {
        File myObj = new File(args[0]);
        Scanner myReader = new Scanner(myObj);
        MyHashMap3<String, Employee> hashMap = new MyHashMap3<>();
        FileWriter myWriter = new FileWriter(args[2]);
        while(myReader.hasNextLine()){
            String[] data = myReader.nextLine().split(", ");
            String place = data[0] +" "+ data[1];
            String name = data[2];
            String job = data[3];
            String placeAndName = place + " " + name;
            Employee employee = new Employee(place,name,job);
            hashMap.insert(placeAndName, employee);
        }
        File myObj2 = new File(args[1]);
        Scanner myReader2 = new Scanner(myObj2);
        MyHashMap2<String,Integer> overallBonusMap = new MyHashMap2<String,Integer>();
        MyHashMap2<String,Integer> monthlyBonusMap = new MyHashMap2<String,Integer>();
        while(myReader2.hasNextLine()){
            String[] datas = myReader2.nextLine().split(":|,");
            for(int i = 0; i< datas.length; i++){
                String str = datas[i].strip();
                datas[i] = str;
            }
            if(datas[0].equals("January")||datas[0].equals("February")||datas[0].equals("March")
                    ||datas[0].equals("April")||datas[0].equals("May")||datas[0].equals("June")
                    ||datas[0].equals("July") ||datas[0].equals("August")||datas[0].equals("September")
                    ||datas[0].equals("October")||datas[0].equals("November")||datas[0].equals("December")){
                List<Employee> allEmployees = hashMap.getAllValues();
                for (Employee employee : allEmployees) {
                    employee.monthlyBonus=0;
                    employee.score = 0;
                    employee.newAddedBonus = 0;
                    String name = employee.name;
                    String place = employee.place;
                    String placeAndName = place + " " + name;
                    hashMap.insert(placeAndName,employee);
                }
                if(monthlyBonusMap.size==0){
                    continue;
                }else{
                    monthlyBonusMap.clear();
                }
            }else if(datas[0].equals("PERFORMANCE_UPDATE")){
                String city = datas[1];
                String district = datas[2];
                String place = city + " " + district;
                String name = datas[3];
                String placeAndName = place + " " + name;
                if(!hashMap.contains(placeAndName)){
                    myWriter.write("There is no such employee.\n");
                }else{
                    int score = Integer.parseInt(datas[4]);
                    Employee x = hashMap.get(placeAndName);
                    x.addScore(score);
                    if(overallBonusMap.contains(place)){
                        int curroverallbonus = overallBonusMap.getValueForKey(place);
                        curroverallbonus += x.newAddedBonus;
                        overallBonusMap.put(place,curroverallbonus);
                    }else{
                        overallBonusMap.put(place, x.newAddedBonus);
                    }
                    x.newAddedBonus = 0;
                    if(monthlyBonusMap.contains(place)){
                        int currmonthlybonus = monthlyBonusMap.getValueForKey(place);
                        currmonthlybonus += x.monthlyBonus;
                        monthlyBonusMap.put(place,currmonthlybonus);
                    }else{
                        monthlyBonusMap.put(place,x.monthlyBonus);
                    }
                    if(x.job.equals("CASHIER")){
                        ArrayList<Employee> branchEmp = hashMap.getEmployeesByPlace(place);
                        ArrayList<Employee> cashierList = new ArrayList<>();
                        if(x.promotionPoints>=3){
                            int cashierNumber = 0;
                            for(Employee e : branchEmp){
                                if(e.job.equals("CASHIER")){
                                    cashierNumber+=1;
                                    cashierList.add(e);
                                }
                            }
                            if(cashierNumber>1){
                                x.job = "COOK";
                                x.promotionPoints -= 3;
                                x.eligibleForPromotion = false;
                                x.wantToBeDismissed = false;
                                hashMap.insert(placeAndName, x);
                                myWriter.write(name + " is promoted from Cashier to Cook.\n");
                            }else{
                                x.eligibleForPromotion = true;
                                hashMap.insert(placeAndName, x);
                            }
                        }else if(x.promotionPoints<=-5){
                            int cashierNumber = 0;
                            for(Employee e : branchEmp){
                                if(e.job.equals("CASHIER")){
                                    cashierNumber+=1;
                                }
                            }
                            if(cashierNumber==1){
                                x.wantToBeDismissed = true;
                                hashMap.insert(placeAndName,x);
                            }else if(cashierNumber>1){
                                myWriter.write(name + " is dismissed from branch: " + district + ".\n" );
                                hashMap.remove(placeAndName);
                            }
                        }else{
                            hashMap.insert(placeAndName,x);
                        }
                    }else if(x.job.equals("COOK")){
                        ArrayList<Employee> branchEmp = hashMap.getEmployeesByPlace(place);
                        if(x.promotionPoints>=10){
                            x.eligibleForPromotion = true;
                            hashMap.insert(placeAndName,x);
                            boolean dissmisalmanager = false;
                            ArrayList<Employee> dissmisalmanagerlist = new ArrayList<>();
                            for(Employee e: branchEmp){
                                if(e.job.equals("MANAGER")){
                                    if(e.wantToBeDismissed){
                                        dissmisalmanager = true;
                                        dissmisalmanagerlist.add(e);
                                    }
                                }
                            }
                            if(dissmisalmanager){
                                Employee y = dissmisalmanagerlist.get(0);
                                String yName = y.name;
                                String yPlace = y.place;
                                String[] cityDistrict = yPlace.split(" ");
                                String yPlaceandYname = yPlace + " " + yName;
                                for(int i = 0; i<cityDistrict.length; i++){
                                    String str = cityDistrict[i].strip();
                                    cityDistrict[i] = str;
                                }
                                myWriter.write(yName + " is dismissed from branch: " + cityDistrict[1] + ".\n");
                                hashMap.remove(yPlaceandYname);
                                x.job = "MANAGER";
                                x.promotionPoints -=10;
                                x.eligibleForPromotion = false;
                                x.wantToBeDismissed = false;
                                myWriter.write(name + " is promoted from Cook to Manager.\n" );
                                hashMap.insert(placeAndName,x);
                            }
                        }else if(x.promotionPoints<=-5){
                            int cookNumber = 0;
                            for(Employee e: branchEmp){
                                if(e.job.equals("COOK")){
                                    cookNumber +=1;
                                }
                            }
                            if(cookNumber==1){
                                x.wantToBeDismissed = true;
                                hashMap.insert(placeAndName,x);
                            }else{
                                myWriter.write(name + " is dismissed from branch: " + district + ".\n");
                                hashMap.remove(placeAndName);
                            }
                        }
                        else {
                            hashMap.insert(placeAndName,x);
                        }
                    }else if(x.job.equals("MANAGER")){
                        ArrayList<Employee> branchEmp = hashMap.getEmployeesByPlace(place);
                        if(x.promotionPoints<=-5){
                            int wantpromocook = 0;
                            int numberofcooks = 0;
                            ArrayList<Employee> cooksthatneedpromo = new ArrayList<>();
                            for(Employee e : branchEmp){
                                if(e.job.equals("COOK")){
                                    numberofcooks += 1;
                                    if(e.eligibleForPromotion){
                                        wantpromocook +=1;
                                        cooksthatneedpromo.add(e);
                                    }
                                }
                            }
                            if(numberofcooks>1 && wantpromocook>0){
                                myWriter.write(name + " is dismissed from branch: " + district + ".\n");
                                hashMap.remove(placeAndName);
                                Employee y = cooksthatneedpromo.get(0);
                                String yName = y.name;
                                String yPlace = y.place;
                                String yPlaceandYname = yPlace + " " + yName;
                                y.job = "MANAGER";
                                y.promotionPoints -=10;
                                y.eligibleForPromotion = false;
                                y.wantToBeDismissed = false;
                                myWriter.write(yName + " is promoted from Cook to Manager.\n");
                                hashMap.insert(yPlaceandYname, y);
                            }else{
                                x.wantToBeDismissed = true;
                            }
                        }else{
                            hashMap.insert(placeAndName,x);
                        }
                    }else if(x.job.equals("COURIER")){
                        ArrayList<Employee> branchEmp = hashMap.getEmployeesByPlace(place);
                        if(x.promotionPoints<=-5){
                            int numberofcouriers = 0;
                            for(Employee e : branchEmp){
                                if(e.job.equals("COURIER")){
                                    numberofcouriers += 1;
                                }
                            }
                            if(numberofcouriers ==1){
                                x.wantToBeDismissed = true;
                                hashMap.insert(placeAndName,x);
                            }else{
                                myWriter.write(name + " is dismissed from branch: " + district + ".\n");
                                hashMap.remove(placeAndName);
                            }
                        }else{
                            hashMap.insert(placeAndName,x);
                        }
                    }
                }
            }else if(datas[0].equals("ADD")){
                String city = datas[1];
                String district = datas[2];
                String place = city + " " + district;
                String name = datas[3];
                String placeAndName = place + " " + name;
                String job = datas[4];
                Employee employee = new Employee(place,name,job);
                hashMap.insert(placeAndName,employee);
                ArrayList<Employee> branchEmp = hashMap.getEmployeesByPlace(place);
                if(employee.job.equals("COURIER")){
                    ArrayList<Employee> dissmisalcourier = new ArrayList<>();
                    for(Employee e : branchEmp){
                        if(e.job.equals("COURIER")){
                            if(e.wantToBeDismissed){
                                dissmisalcourier.add(e);
                            }
                        }
                    }
                    if(dissmisalcourier.size()>0){
                        Employee y = dissmisalcourier.get(0);
                        String yName = y.name;
                        String yPlace = y.place;
                        String[] cityDistrict = yPlace.split(" ");
                        String yPlaceandYname = yPlace + " " + yName;
                        for(int i = 0; i<cityDistrict.length; i++){
                            String str = cityDistrict[i].strip();
                            cityDistrict[i] = str;
                        }
                        myWriter.write(yName + " is dismissed from branch: " + cityDistrict[1] + ".\n");
                        hashMap.remove(yPlaceandYname);
                    }
                }else if(employee.job.equals("CASHIER")){
                    ArrayList<Employee> dissmisalcashier = new ArrayList<>();
                    ArrayList<Employee> promocashier = new ArrayList<>();
                    for(Employee e : branchEmp){
                        if(e.job.equals("CASHIER")){
                            if(e.wantToBeDismissed){
                                dissmisalcashier.add(e);
                            }
                            else if(e.eligibleForPromotion){
                                promocashier.add(e);
                            }
                        }
                    }
                    if(promocashier.size()>0){
                        Employee y = promocashier.get(0);
                        String yName = y.name;
                        String yPlace = y.place;
                        String[] cityDistrict = yPlace.split(" ");
                        String yPlaceandYname = yPlace + " " + yName;
                        for(int i = 0; i<cityDistrict.length; i++){
                            String str = cityDistrict[i].strip();
                            cityDistrict[i] = str;
                        }
                        y.job="COOK";
                        y.promotionPoints -=3;
                        y.eligibleForPromotion = false;
                        y.wantToBeDismissed = false;
                        hashMap.insert(yPlaceandYname,y);
                        myWriter.write(yName + " is promoted from Cashier to Cook.\n");
                    } else if(dissmisalcashier.size()>0){
                        Employee y = dissmisalcashier.get(0);
                        String yName = y.name;
                        String yPlace = y.place;
                        String[] cityDistrict = yPlace.split(" ");
                        String yPlaceandYname = yPlace + " " + yName;
                        for(int i = 0; i<cityDistrict.length; i++){
                            String str = cityDistrict[i].strip();
                            cityDistrict[i] = str;
                        }
                        myWriter.write(yName + " is dismissed from branch: " + cityDistrict[1] + ".\n");
                        hashMap.remove(yPlaceandYname);
                    }
                }else if(employee.job.equals("COOK")){
                    ArrayList<Employee> dissmisalcook = new ArrayList<>();
                    ArrayList<Employee> promocook = new ArrayList<>();
                    int totalcook = 0;
                    ArrayList<Employee> dissmisalmanager = new ArrayList<>();
                    for(Employee e : branchEmp){
                        if(e.job.equals("COOK")){
                            totalcook +=1;
                            if(e.wantToBeDismissed){
                                dissmisalcook.add(e);
                            }
                            else if(e.eligibleForPromotion){
                                promocook.add(e);
                            }
                        }else if(e.job.equals("MANAGER")){
                            if(e.wantToBeDismissed){
                                dissmisalmanager.add(e);
                            }
                        }
                    }
                    if(dissmisalcook.size()>1){
                        Employee y = dissmisalcook.get(0);
                        String yName = y.name;
                        String yPlace = y.place;
                        String[] cityDistrict = yPlace.split(" ");
                        String yPlaceandYname = yPlace + " " + yName;
                        for(int i = 0; i<cityDistrict.length; i++){
                            String str = cityDistrict[i].strip();
                            cityDistrict[i] = str;
                        }
                        myWriter.write(yName + " is dismissed from branch: " + cityDistrict[1] + ".\n");
                        hashMap.remove(yPlaceandYname);
                    }
                    if(promocook.size()>0 && dissmisalmanager.size()>0){
                        Employee y = promocook.get(0);
                        String yName = y.name;
                        String yPlace = y.place;
                        String[] cityDistricty = yPlace.split(" ");
                        String yPlaceandYname = yPlace + " " + yName;
                        for(int i = 0; i<cityDistricty.length; i++){
                            String str = cityDistricty[i].strip();
                            cityDistricty[i] = str;
                        }
                        Employee x = dissmisalmanager.get(0);
                        String xName = y.name;
                        String xPlace = y.place;
                        String[] cityDistrictx = yPlace.split(" ");
                        String xPlaceandxname = yPlace + " " + yName;
                        for(int i = 0; i<cityDistrictx.length; i++){
                            String str = cityDistrictx[i].strip();
                            cityDistrictx[i] = str;
                        }
                        y.job = "MANAGER";
                        y.promotionPoints -=10;
                        y.eligibleForPromotion = false;
                        y.wantToBeDismissed = false;
                        myWriter.write(xName + " is dismissed from branch: " + cityDistrictx[1] + ".\n");
                        hashMap.remove(xPlaceandxname);
                        hashMap.insert(yPlaceandYname,y);
                        myWriter.write(yName + " is promoted from Cook to Manager.\n");
                    }
                }
                employee.newAddedBonus=0;
            }else if(datas[0].equals("LEAVE")){
                String city = datas[1];
                String district = datas[2];
                String place = city + " " + district;
                String name = datas[3];
                String placeAndName = place + " " + name;
                if(!hashMap.contains(placeAndName)){
                    myWriter.write("There is no such employee.\n");
                }else{
                    ArrayList<Employee> branchEmp = hashMap.getEmployeesByPlace(place);
                    ArrayList<Employee> leftOne = new ArrayList<>();
                    for(Employee e: branchEmp){
                        if(e.name.equals(name)){
                            leftOne.add(e);
                        }
                    }
                    Employee tempEmployee = leftOne.get(0);
                    tempEmployee.wantToBeDismissed = true;
                    hashMap.insert(placeAndName,tempEmployee);
                    Employee employee = hashMap.get(placeAndName);
                    if(employee.job.equals("COURIER")){
                        int couriernumber = 0;
                        for(Employee e : branchEmp){
                            if(e.job.equals("COURIER")){
                                couriernumber += 1;
                            }
                        }
                        if(couriernumber>1){
                            myWriter.write(name + " is leaving from branch: " + district + ".\n");
                            hashMap.remove(placeAndName);
                        }else{
                            employee.wantToBeDismissed=false;
                            //employee.monthlyBonus += 200;
                            int monthbonus = monthlyBonusMap.get(place);
                            monthbonus+=200;
                            monthlyBonusMap.put(place,monthbonus);
                            int currbonus = overallBonusMap.get(place);
                            currbonus+=200;
                            overallBonusMap.put(place,currbonus);
                            //employee.totalBonus += 200;
                        }
                    }else if (employee.job.equals("CASHIER")){
                        int cashiernumber = 0;
                        for(Employee e : branchEmp){
                            if(e.job.equals("CASHIER")){
                                cashiernumber += 1;
                            }
                        }
                        if(cashiernumber>1){
                            myWriter.write(name + " is leaving from branch: " + district + ".\n");
                            hashMap.remove(placeAndName);
                        }else{
                            employee.wantToBeDismissed=false;
                            //employee.monthlyBonus += 200;
                            if(monthlyBonusMap.get(place) != null){
                                int monthbonus = monthlyBonusMap.get(place);
                                monthbonus+=200;
                                monthlyBonusMap.put(place,monthbonus);
                            }
                            int currbonus = overallBonusMap.get(place);
                            currbonus+=200;
                            overallBonusMap.put(place,currbonus);
                            //employee.totalBonus += 200;
                        }
                    }else if (employee.job.equals("COOK")){
                        int cooknumber = 0;
                        for(Employee e : branchEmp){
                            if(e.job.equals("COOK")){
                                cooknumber +=1;
                            }
                        }
                        if(cooknumber>1){
                            myWriter.write(name + " is leaving from branch: " + district + ".\n");
                            hashMap.remove(placeAndName);
                        }else{
                            employee.wantToBeDismissed=false;
                            //employee.monthlyBonus += 200;
                            int monthbonus = monthlyBonusMap.get(place);
                            monthbonus+=200;
                            monthlyBonusMap.put(place,monthbonus);
                            int currbonus = overallBonusMap.get(place);
                            currbonus+=200;
                            overallBonusMap.put(place,currbonus);
                            //employee.totalBonus += 200;
                        }
                    }else if(employee.job.equals("MANAGER")){
                        ArrayList<Employee> overtenpointcooks = new ArrayList<>();
                        int numberofcooks = 0;
                        for(Employee e: branchEmp){
                            if(e.job.equals("COOK")){
                                numberofcooks += 1;
                                if(e.eligibleForPromotion){
                                    overtenpointcooks.add(e);
                                }
                            }
                        }
                        if(numberofcooks>1 && overtenpointcooks.size()>0){
                            myWriter.write(name + " is leaving from branch: " + district + ".\n");
                            hashMap.remove(placeAndName);
                            Employee y = overtenpointcooks.get(0);
                            String yName = y.name;
                            String yPlace = y.place;
                            String[] cityDistrict = yPlace.split(" ");
                            String yPlaceandname = yPlace + " " + yName;
                            for(int i = 0; i<cityDistrict.length; i++){
                                String str = cityDistrict[i].strip();
                                cityDistrict[i] = str;
                            }
                            y.job = "MANAGER";
                            y.promotionPoints -= 10;
                            y.wantToBeDismissed = false;
                            y.eligibleForPromotion = false;
                            hashMap.insert(yPlaceandname,y);
                            myWriter.write(yName + " is promoted from Cook to Manager.\n");
                        }else{
                            employee.wantToBeDismissed = false;
                            //employee.monthlyBonus += 200;
                            if(monthlyBonusMap.get(place) != null){
                                int monthbonus = monthlyBonusMap.get(place);
                                monthbonus+=200;
                                monthlyBonusMap.put(place,monthbonus);
                            }
                            int currbonus = overallBonusMap.get(place);
                            currbonus+=200;
                            overallBonusMap.put(place,currbonus);
                            //employee.totalBonus += 200;
                        }
                    }
                }
            }else if(datas[0].equals("PRINT_MONTHLY_BONUSES")){
                String city = datas[1];
                String district = datas[2];
                String place = city + " " + district;
                ArrayList<Employee> branchEmp = hashMap.getEmployeesByPlace(place);
                if(monthlyBonusMap.contains(place)){
                    int answer = monthlyBonusMap.get(place);
                    myWriter.write("Total bonuses for the " + district + " branch this month are: " + answer + "\n");
                }else{
                    myWriter.write("Total bonuses for the " + district + " branch this month are: 0\n");
                }
            }else if(datas[0].equals("PRINT_OVERALL_BONUSES")){
                String city = datas[1];
                String district = datas[2];
                String place = city + " " + district;
                if(overallBonusMap.contains(place)){
                    int answer = overallBonusMap.get(place);
                    myWriter.write("Total bonuses for the " + district + " branch are: " + answer + "\n");
                }else{
                    myWriter.write("Total bonuses for the " + district + " branch are: 0\n");
                }

            }else if(datas[0].equals("PRINT_MANAGER")){
                String city = datas[1];
                String district = datas[2];
                String place = city + " " + district;
                ArrayList<Employee> branchEmp = hashMap.getEmployeesByPlace(place);
                ArrayList<Employee> manager = new ArrayList<>();
                for(Employee e : branchEmp){
                    if(e.job.equals("MANAGER")){
                        manager.add(e);
                    }
                }
                myWriter.write("Manager of the " + district + " branch is " + manager.get(0).name + ".\n");
            }
        }
        myWriter.close();
    }
}