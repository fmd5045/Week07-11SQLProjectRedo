package projects.entity;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

public class Project {
	private Integer projectId;
	private String projectName;
	private BigDecimal estimatedHours;
	private BigDecimal actualHours;
	private Integer difficulty;
	private String notes;

	private List<Material> materials = new LinkedList<>();
	private List<Step> steps = new LinkedList<>();
	private List<Category> categories = new LinkedList<>();
	
	@Override
	public String toString() {
		String project = " ";
		
		project += "\n ID = " + projectId;
		project += "\n Project Name = " + projectName;
		project += "\n Estimated hours = " + estimatedHours;
		project += "\n Actual Hours = " + actualHours;
		project += "\n Difficulty = " + difficulty;
		project += "\n Notes = " + notes;
		
		project += "\n Materials: ";
		
		for (Material material : materials) {
			project += "\n		" + material;
		}
		
		project += "\n	Steps";
		
		for (Step step : steps) {
			project += "\n		" + step;
		}
		
		project += "\n	Categories";
		
		for(Category category : categories) {
			project+= "\n		"+ category;
		}
		
		return project;
	}

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public BigDecimal getEstimatedHours() {
		return estimatedHours;
	}

	public void setEstimatedHours(BigDecimal estimatedHours) {
		this.estimatedHours = estimatedHours;
	}

	public BigDecimal getActualHours() {
		return actualHours;
	}

	public void setActualHours(BigDecimal actualHours) {
		this.actualHours = actualHours;
	}

	public List<Material> getMaterials() {
		return materials;
	}

	public void setMaterials(List<Material> materials) {
		this.materials = materials;
	}

	public List<Step> getSteps() {
		return steps;
	}

	public void setSteps(List<Step> steps) {
		this.steps = steps;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}
	public int getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(Integer difficulty) {
		this.difficulty = difficulty;
	}

}
