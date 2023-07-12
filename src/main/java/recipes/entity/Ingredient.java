package recipes.entity;

import java.math.BigDecimal;
import java.util.Objects;

import provided.entity.EntityBase;

public class Ingredient extends EntityBase{
	private Integer ingredientId;
	private Integer recipe_id;
	private Unit unit;
	private String ingredientName;
	private String instruction;
	private Integer ingredientOrder;
	private BigDecimal amount;
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		
		b.append("ID =").append(ingredientId).append(": ");
		b.append(toFraction(amount));
		
		if(Objects.nonNull(unit) && Objects.nonNull(unit.getUnitId())) {
			String singular = unit.getUnitNameSingular();
			String plural = unit.getUnitNamePlural();
			String word = amount.compareTo(BigDecimal.ONE) > 0 ? plural : singular;
		
			b.append(word).append(" ");
		}
		b.append(ingredientName);
		if(Objects.nonNull(instruction)) {
			b.append(", ").append(instruction);
		}
		return b.toString();
	}
	public Integer getIngregredient_id() {
		return ingredientId;
	}
	public void setIngregredient_id(Integer ingregredient_id) {
		this.ingredientId = ingregredient_id;
	}
	public Integer getRecipe_id() {
		return recipe_id;
	}
	public void setRecipe_id(Integer recipe_id) {
		this.recipe_id = recipe_id;
	}
	public Unit getUnit() {
		return unit;
	}
	public void setUnit(Unit unit) {
		this.unit = unit;
	}
	public String getIngredientName() {
		return ingredientName;
	}
	public void setIngredientName(String ingredientName) {
		this.ingredientName = ingredientName;
	}
	public String getInstruction() {
		return instruction;
	}
	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}
	public Integer getIngredientOrder() {
		return ingredientOrder;
	}
	public void setIngredientOrder(Integer ingredientOrder) {
		this.ingredientOrder = ingredientOrder;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

} 
