package recipes;

import recipes.entity.Recipe;
import recipes.exception.*;
import recipes.service.RecipeService;

import java.time.LocalTime;
import java.util.*;

public class Recipes {
	
	private Scanner scanner = new Scanner(System.in);
	private RecipeService recipeService = new RecipeService();

	private List<String> operations = List.of(
			"1) Create and populate tables",
			"2) Add a recipe"
			);
	
	public static void main(String[] args) {
		new Recipes().displayMenu();
		

	}

	private void displayMenu() {
		boolean done = false;
		
		while(!done) {
			try {
			int operation = getOperation();
			switch(operation) {
			case -1:
				done = exitMenu();
				break;
				
			case 1:
				createTables();
				break;
				
			case 2:
				addRecipe();
				break;
				
			default:
				System.out.println("\n"+ operation + " is not valid. Please try again.");
				break;
				
			}
			}catch(Exception e) {
				System.out.println("\nError: "+ e.toString() + " Try again.");
			}
		}
		
	}

	private void addRecipe() {
		String name = getStringInput("Enter the recipe name: ");
		String notes = getStringInput("Enter the recipe notes: ");
		Integer NumServings = getIntInput("Enter the number of servings: ");
		Integer prepMinutes = getIntInput("Enter prep time in minutes: ");
		Integer cookMinutes = getIntInput(" Enter cook time in minutes: ");
		
		LocalTime preptime = minutesToLocalTime(prepMinutes);
		LocalTime cooktime = minutesToLocalTime(cookMinutes);
		
		Recipe recipe = new Recipe();
		
		recipe.setRecipeName(name);
		recipe.setNotes(notes);
		recipe.setNumServings(NumServings);
		recipe.setPrepTime(preptime);
		recipe.setCookTime(cooktime);
		
		Recipe dbRecipe = recipeService.addRecipe(recipe);
		System.out.println("You added this recipe: \n"+ dbRecipe);
	
		
	}

	private LocalTime minutesToLocalTime(Integer numMinutes) {
		int min = Objects.isNull(numMinutes) ? 0 : numMinutes;
		int hours = min / 60;
		int minutes = min % 60;
		
		return LocalTime.of(hours, minutes);
		
	}

	private void createTables() {
		recipeService.createAndPopulateTables();
		System.out.println("\nTables created and populated!");
		
	}

	private boolean exitMenu() {
		System.out.println("\nYou have now EXITED the menu.");
		return true;
	}

	private int getOperation() {
		printOperations();
		Integer op = getIntInput("\nEnter a operation number (Press ENTER to quit)");
		
		return Objects.isNull(op) ? -1 : op;
	} 
	

	private void printOperations() {
		System.out.println();
		System.out.println("Here's what you can do:");
		
		operations.forEach(op -> System.out.println("   "+ op));
		
	}
	
	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);
		
		if(Objects.isNull(input)) {
			return null;
		}
		try {
			return Integer.parseInt(input);
		} catch(NumberFormatException e) {
			throw new DbException(input + " is not a valid number.");
		}
	}
	
	@SuppressWarnings("unused")
	private Double getDoubleInput(String prompt) {
		String input = getStringInput(prompt);
		
		if(Objects.isNull(input)) {
			return null;
		}
		try {
			return Double.parseDouble(input);
		} catch(NumberFormatException e) {
			throw new DbException(input + "is not a valid number.");
		}
	}

	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		String line = scanner.nextLine();
		
		return line.isBlank() ? null : line.trim();
	}

}
