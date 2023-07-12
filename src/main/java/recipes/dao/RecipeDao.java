package recipes.dao;

import java.sql.*;
import java.util.*;
import provided.util.DaoBase;
import recipes.entity.*;
import recipes.exception.DbException;
import java.time.*;

public class RecipeDao extends DaoBase {

	private static final String CATEGORY_TABLE = "category";
	private static final String INGREDIENT_TABLE = "ingredient";
	private static final String RECIPE_TABLE = "recipe";
	private static final String RECIPE_CATEGORY_TABLE = "recipe_category";
	private static final String STEP_TABLE = "step";
	private static final String UNIT_TABLE = "unit";
	
	public List<Recipe> fetchAllRecipes() {
		String sql = "SELECT * FROM " + RECIPE_TABLE + " ORDER BY recipe_id";

		try (Connection connection = DbConnection.getConnection()) {
			startTransaction(connection);
			
			try (PreparedStatement statement = connection.prepareStatement(sql)) {
				try (ResultSet resultset = statement.executeQuery()) {
					List<Recipe> recipes = new LinkedList<>();

					while (resultset.next()) {
						recipes.add(extract(resultset, Recipe.class));
					}
					return recipes;
				}
			} catch (Exception e) {
				rollbackTransaction(connection);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}
	
	
	public Optional<Recipe> fetchRecipeById(Integer recipeId){
		String sql = "SELECT * FROM " + RECIPE_TABLE + " WHERE recipe_id = ?";
		
		try(Connection connection = DbConnection.getConnection()) {
			startTransaction(connection);
			
			try {
				Recipe recipe = null;
		
				try(PreparedStatement statement = connection.prepareStatement(sql)){
					setParameter(statement, 1, recipeId, Integer.class);
					
					try(ResultSet resultset = statement.executeQuery()){
						if(resultset.next()) {
							recipe = extract(resultset, Recipe.class);
						}
					}
				}
				
				if(Objects.nonNull(recipe)) {
					recipe.getIngredients().addAll(fetchRecipeIngredients(connection, recipeId));
					
					recipe.getSteps().addAll(fetchRecipeSteps(connection, recipeId));
					recipe.getCategories().addAll(fetchRecipeCategories(connection, recipeId));
				}
				//first missing line of code
				commitTransaction(connection);
				return Optional.ofNullable(recipe);
			}
			catch(Exception e) {
				rollbackTransaction(connection);
				throw new DbException(e);
			}
			
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	private List<Category> fetchRecipeCategories(Connection connection, Integer recipeId) throws  SQLException{
		String sql = ""
				+ "SELECT c.* "
				+ "FROM " + RECIPE_CATEGORY_TABLE + " rc "
				+ "JOIN " + CATEGORY_TABLE + " c USING (category_id) "
				+ "WHERE rc.recipe_id = ? "
				+ "ORDER BY c.category_name";
		
		try(PreparedStatement statement = connection.prepareStatement(sql)){
			setParameter(statement, 1, recipeId, Integer.class);
			
			try(ResultSet resultSet = statement.executeQuery()){
				List<Category> categories = new LinkedList<Category>();
				
				while (resultSet.next()) {
					categories.add(extract(resultSet, Category.class));
				}
				
				return categories;
			}
		}
	}

	private List<Step> fetchRecipeSteps(Connection connection, Integer recipeId) throws  SQLException{
		String sql = "SELECT * FROM " + STEP_TABLE + " s WHERE s.recipe_id = ?";
		
		try(PreparedStatement statement = connection.prepareStatement(sql)){
			setParameter(statement, 1, recipeId, Integer.class);
			
			try(ResultSet resultSet = statement.executeQuery()){
				List<Step> steps = new LinkedList <Step>();
				
				while(resultSet.next()) {
					steps.add(extract(resultSet, Step.class));
				}
				
				return steps;
			}
		}
	}

	private List<Ingredient> fetchRecipeIngredients(Connection connection, Integer recipeId) throws SQLException {
		String sql = ""
				+ "SELECT i.*, u.unit_name_singular, u.unit_name_plural "
				+ "FROM " + INGREDIENT_TABLE + " i "
				+ "LEFT JOIN " + UNIT_TABLE + " u USING (unit_id) "
				+ "WHERE recipe_id = ? "
				+ "ORDER BY i.ingredient_order";
		
		try(PreparedStatement statement = connection.prepareStatement(sql)){
			setParameter(statement, 1, recipeId, Integer.class);
			
			try(ResultSet resultset = statement.executeQuery()){
				List<Ingredient> ingredients = new LinkedList<Ingredient>();
				
				while(resultset.next()){
					Ingredient ingredient = extract(resultset, Ingredient.class);
					Unit unit = extract(resultset, Unit.class);
					
					ingredient.setUnit(unit);
					ingredients.add(ingredient);
				}
				
				return ingredients;
			}
		}
	}

	public Recipe insertRecipe(Recipe recipe) {
		String sql = "" 
				+ "INSERT INTO " + RECIPE_TABLE + " "
				+ "(recipe_name , notes , num_servings, prep_time, cook_time) " 
				+ "VALUES " 
				+ "(?, ?, ?, ?, ?)";

		try (Connection connection = DbConnection.getConnection()) {
			startTransaction(connection);
			
			try (PreparedStatement statement = connection.prepareStatement(sql)) {
				setParameter(statement, 1, recipe.getRecipeName(), String.class);
				setParameter(statement, 2, recipe.getNotes(), String.class);
				setParameter(statement, 3, recipe.getNumServings(), Integer.class);
				setParameter(statement, 4, recipe.getPrepTime(), LocalTime.class);
				setParameter(statement, 5, recipe.getCookTime(), LocalTime.class);

				statement.executeUpdate();
				Integer recipeId = getLastInsertId(connection, RECIPE_TABLE);

				commitTransaction(connection);

				recipe.setRecipeId(recipeId);
				return recipe;

			} catch (Exception e) {
				rollbackTransaction(connection);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	public void executeBatch(List<String> sqlBatch) {
		try (Connection connection = DbConnection.getConnection()) {
			startTransaction(connection);

			try (Statement statement = connection.createStatement()) {
				for (String sql : sqlBatch) {
					statement.addBatch(sql);
				}

				statement.executeBatch();
				commitTransaction(connection);

			} catch (Exception e) {
				rollbackTransaction(connection);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

}
