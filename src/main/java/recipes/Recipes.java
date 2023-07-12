package recipes;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import recipes.entity.Recipe;
import recipes.exception.DbException;
import recipes.service.RecipeService;

public class Recipes {

	private Scanner scanner = new Scanner(System.in);
	private RecipeService recipeService = new RecipeService();
	private Recipe currentRecipe;

	private List<String> operations = List.of(
				"1) Create and populate tables", 
				"2) Add a recipe",
				"3) List recipes",
				"4) Select working recipe"
	);

	public static void main(String[] args) {
		new Recipes().displayMenu();

	}

	private void displayMenu() {
		boolean done = false;

		while (!done) {
			try {
				int operation = getOperation();
				switch (operation) {
				case -1:
					done = exitMenu();
					break;

				case 1:
					createTables();
					break;

				case 2:
					addRecipe();
					break;

				case 3:
					listRecipes();
					break;

				case 4:
					setCurrentRecipe();
					break;

				default:
					System.out.println("\n" + operation + " is not valid. Please try again.");
					break;

				}
			} catch (Exception e) {
				System.out.println("\nError: " + e.toString() + " Try again.");
			}
		}

	}

	private void setCurrentRecipe() {
		List<Recipe> recipes = listRecipes();
		
		Integer recipeId = getIntInput("Select a recipe ID");
		currentRecipe = null;

		for (Recipe recipe : recipes) {
			if (recipe.getRecipeId().equals(recipeId)) {
				currentRecipe = recipeService.fetchRecipeById(recipeId);
				break;
			}
		}
		if (Objects.isNull(currentRecipe)) {
			System.out.println("\nInvalid recipe Selected.");
		}
	}

	private List<Recipe> listRecipes() {
		List<Recipe> recipes = recipeService.fetchRecipes();

		System.out.println("\nRecipes:");

		recipes.forEach(recipe -> System.out.println("      " + recipe.getRecipeId() + ": " + recipe.getRecipeName()));

		return recipes;
	}

	private void addRecipe() {
		String name = getStringInput("Enter the recipe name ");
		String notes = getStringInput("Enter the recipe notes ");
		Integer NumServings = getIntInput("Enter the number of servings ");
		Integer prepMinutes = getIntInput("Enter prep time in minutes ");
		Integer cookMinutes = getIntInput(" Enter cook time in minutes ");

		LocalTime preptime = minutesToLocalTime(prepMinutes);
		LocalTime cooktime = minutesToLocalTime(cookMinutes);

		Recipe recipe = new Recipe();

		recipe.setRecipeName(name);
		recipe.setNotes(notes);
		recipe.setNumServings(NumServings);
		recipe.setPrepTime(preptime);
		recipe.setCookTime(cooktime);

		Recipe dbRecipe = recipeService.addRecipe(recipe);
		System.out.println("You added this recipe: \n" + dbRecipe);

		//29:00 Doctor Rob does not add the class name in front of currentRecipe, when i do
		//this i get an error
		currentRecipe = recipeService.fetchRecipeById(dbRecipe.getRecipeId());

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

		operations.forEach(op -> System.out.println("   " + op));
		
		if(Objects.isNull(currentRecipe)) {
			System.out.println("\nYou are currently not working with a recipe!");
		}else {
			System.out.println("\nYou are working with recipe " + currentRecipe);
		}
	}

	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);

		if (Objects.isNull(input)) {
			return null;
		}
		try {
			return Integer.parseInt(input);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid number.");
		}
	}

	@SuppressWarnings("unused")
	private Double getDoubleInput(String prompt) {
		String input = getStringInput(prompt);

		if (Objects.isNull(input)) {
			return null;
		}
		try {
			return Double.parseDouble(input);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid number.");
		}
	}

	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		String line = scanner.nextLine();

		return line.isBlank() ? null : line.trim();
	}

}
