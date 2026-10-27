import java.util.*;

class Food 
{
    int calories;
    int protein;
    int carbs;
    int fats;

    Food(int calories, int protein, int carbs, int fats) 
  {
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fats = fats;
    }
}

public class Main 
{

    public static void main(String[] args)
  {

        Scanner sc = new Scanner(System.in);

        HashMap<String, Food> foodDB = new HashMap<>();

        // Milkshakes
        foodDB.put("banana milkshake", new Food(200,8,30,5));
        foodDB.put("chocolate milkshake", new Food(220,7,32,6));
        foodDB.put("peanut butter milkshake", new Food(320,14,28,18));
        foodDB.put("strawberry milkshake", new Food(210,6,30,5));
        foodDB.put("mango milkshake", new Food(230,6,35,5));
        foodDB.put("protein milkshake", new Food(250,25,15,3));
        foodDB.put("oats milkshake", new Food(270,10,40,6));
        foodDB.put("almond milkshake", new Food(260,9,28,12));

        // Smoothies
        foodDB.put("banana smoothie", new Food(180,5,30,2));
        foodDB.put("strawberry smoothie", new Food(170,4,28,2));
        foodDB.put("mango smoothie", new Food(190,4,33,2));
        foodDB.put("blueberry smoothie", new Food(160,3,27,2));
        foodDB.put("green smoothie", new Food(140,4,20,1));
        foodDB.put("peanut butter smoothie", new Food(240,10,20,12));
        foodDB.put("oats smoothie", new Food(200,7,35,4));
        foodDB.put("avocado smoothie", new Food(250,4,15,20));

        // Protein Bars
        foodDB.put("peanut butter protein bar", new Food(220,12,18,10));
        foodDB.put("chocolate protein bar", new Food(210,15,20,8));
        foodDB.put("almond protein bar", new Food(200,10,15,12));
        foodDB.put("oats energy bar", new Food(180,6,25,6));
        foodDB.put("granola bar", new Food(190,5,28,6));
        foodDB.put("date energy bar", new Food(170,4,30,2));
        foodDB.put("protein granola bar", new Food(210,12,22,8));

        // High Protein Foods
        foodDB.put("paneer", new Food(265,18,1,20));
        foodDB.put("tofu", new Food(144,17,3,8));
        foodDB.put("greek yogurt", new Food(59,10,3,0));
        foodDB.put("cottage cheese", new Food(98,11,3,4));
        foodDB.put("lentils", new Food(116,9,20,0));
        foodDB.put("chickpeas", new Food(164,9,27,2));
        foodDB.put("rajma", new Food(127,9,22,0));
        foodDB.put("soy chunks", new Food(345,52,33,0));

        // Carbohydrates
        foodDB.put("oats", new Food(389,17,66,7));
        foodDB.put("brown rice", new Food(216,5,45,2));
        foodDB.put("white rice", new Food(205,4,45,0));
        foodDB.put("sweet potato", new Food(86,2,20,0));
        foodDB.put("whole wheat bread", new Food(247,13,41,4));
        foodDB.put("quinoa", new Food(120,4,21,2));
        foodDB.put("pasta", new Food(131,5,25,1));
        foodDB.put("corn", new Food(96,3,21,1));

        // Healthy Fats
        foodDB.put("almonds", new Food(579,21,22,50));
        foodDB.put("cashews", new Food(553,18,30,44));
        foodDB.put("walnuts", new Food(654,15,14,65));
        foodDB.put("peanut butter", new Food(588,25,20,50));
        foodDB.put("chia seeds", new Food(486,17,42,31));
        foodDB.put("flax seeds", new Food(534,18,29,42));

        // Fruits
        foodDB.put("banana", new Food(89,1,23,0));
        foodDB.put("apple", new Food(52,0,14,0));
        foodDB.put("orange", new Food(47,1,12,0));
        foodDB.put("mango", new Food(60,1,15,0));
        foodDB.put("pineapple", new Food(50,1,13,0));

        int totalCalories = 0;
        int totalProtein = 0;
        int totalCarbs = 0;
        int totalFats = 0;

        System.out.println(" NUTRILOG - NUTRITION CALCULATOR ");
        System.out.println("Enter food items (type 'exit' to finish)");

        while(true) 
        {

            System.out.print("Enter food: ");
            String food = sc.nextLine().toLowerCase();

            if(food.equals("exit")) 
            {
                break;
            }

            if(foodDB.containsKey(food)) 
            {

                Food f = foodDB.get(food);

                totalCalories += f.calories;
                totalProtein += f.protein;
                totalCarbs += f.carbs;
                totalFats += f.fats;

                System.out.println("Added: " + food);

            } 
            else 
            {

                System.out.println("Food not found in database.");
            }
        }

        System.out.println("\n DAILY NUTRITION TOTAL ");

        System.out.println("Calories: " + totalCalories + " kcal");
        System.out.println("Protein: " + totalProtein + " g");
        System.out.println("Carbs: " + totalCarbs + " g");
        System.out.println("Fats: " + totalFats + " g");

        sc.close();
    }
}




