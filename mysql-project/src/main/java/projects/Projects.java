package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class Projects {
	private Scanner scanner = new Scanner(System.in);
	private ProjectService projectService = new ProjectService();
	private Project currentProject;

	//@formatter:off
	 	private List<String> operations = List.of(
        "1) Add a project",
        "2) List projects",
        "3) Select a project",
        "4) Update project details",
        "5) Delete a Project");
	   //@formatter:on

		private void createTables() {
			projectService.createTables();
			System.out.println("\nTables created!");

		}
	 	
	public static void main(String[] args) {
		new Projects().createTables();
		new Projects().displayUserOptions();
	}
	

	private void displayUserOptions() {
		boolean done = false;

		while (!done) {
			try {
				int operation = getOperation();
				switch (operation) {
				case -1:
					done = exitOptionsMenu();
					break;

				case 1:
					addProject();
					break;

				case 2:
					listProjects();
					break;

				case 3:
					selectProject();
					break;

				case 4:
					udpdateProjectDetails();
					break;

				case 5:
					deleteProject();
					break;

				default:
					System.out.println("\n" + operation + " is not valid. Please try again.");
					break;

				}
			} catch (Exception exception) {
				System.out.println("\nError: " + exception.toString() + " Try again.");
			}
		}
	}


	private void deleteProject() {
		listProjects();
		Integer projectId =getIntInput("Enter the ID of the project to delete");
		
		if(Objects.nonNull(projectId)) {
			projectService.deleteProject(projectId);
			
			System.out.println("You have deleted project with id of : " + projectId);
			
			if(Objects.nonNull(currentProject) && currentProject.getProjectId().equals(projectId)) {
				currentProject = null;
			}
		}
	}
	
	private void udpdateProjectDetails() {
		 if (Objects.isNull(currentProject)) {
       System.out.println("\nPlease select a project.");
       return;
   }

   String projectName = getStringInput("Enter the prject name " + currentProject.getProjectName());

   BigDecimal estimatedHours = getBigDecimalInput(
           "Enter the estimated hours " + currentProject.getEstimatedHours());

   BigDecimal actualHours = getBigDecimalInput("Enter the actual hours" + currentProject.getActualHours());

   Integer difficulty = getIntInput("Enter the difficulty (1-5)"  + currentProject.getDifficulty());

   String notes = getStringInput("Enter the project notes " + currentProject.getNotes());

   //update the project from inputs
   Project project = new Project();

   project.setProjectId(currentProject.getProjectId());
   project.setProjectName(Objects.isNull(projectName) ? currentProject.getProjectName() : projectName);
   project.setEstimatedHours(Objects.isNull(estimatedHours) ? currentProject.getEstimatedHours() : estimatedHours);
   project.setActualHours(Objects.isNull(actualHours) ? currentProject.getActualHours() : actualHours);
   project.setDifficulty(Objects.isNull(difficulty) ? currentProject.getDifficulty() : difficulty);
   project.setNotes(Objects.isNull(notes) ? currentProject.getNotes() : notes);
   projectService.modifyProjectDetails(project);
   currentProject = projectService.fetchProjectById(currentProject.getProjectId());
}
	
	private void selectProject() {
		listProjects();
		Integer projectId = getIntInput("Enter a project ID to select a project");
		currentProject = projectService.fetchProjectById(projectId);
		
	}


	private List<Project> listProjects() {
		List<Project> projects = projectService.fetchAllProjects();

		System.out.println("\nProjects:");

		projects
				.forEach(project -> System.out.println("      " + project.getProjectId() + ": " + project.getProjectName()));

		return projects;

	}

	private void addProject() {
		String name = getStringInput("Enter the project name ");
		BigDecimal estimatedHours = getBigDecimalInput("Enter the estimated hours ");
		BigDecimal actualHours = getBigDecimalInput("Enter the actual hours ");
		Integer difficulty = getIntInput("Enter the difficulty ");
		String notes = getStringInput("Enter the project notes ");

		Project project = new Project();

		project.setProjectName(name);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);

		Project dbProject = projectService.addProject(project);
		System.out.println("You added the following project: \n" + dbProject);

		currentProject = projectService.fetchProjectById(dbProject.getProjectId());

	}

	private boolean exitOptionsMenu() {
		System.out.println("\nYou have now EXITED the menu.");
		return true;
	}

	private int getOperation() {
		printOperations();
		Integer operation = getIntInput("\nEnter a operation number (Press ENTER to quit)");

		return (int) (Objects.isNull(operation) ? -1 : operation);
	}

	@SuppressWarnings("unused")
	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);

		if (Objects.isNull(input)) {
			return null;
		}
		try {
			return Integer.valueOf(input);
		} catch (NumberFormatException exception) {
			throw new DbException(input + " is not a valid number.");
		}
	}

	@SuppressWarnings("unused")
	private BigDecimal getBigDecimalInput(String prompt) {
		String input = getStringInput(prompt);

		if (Objects.isNull(input)) {
			return null;
		}
		try {
			return new BigDecimal(input).setScale(2);
		} catch (NumberFormatException exception) {
			throw new DbException(input + " is not a valid decimal number.");
		}
	}

	private void printOperations() {
		System.out.println();
		System.out.println("Here's what you can do:");

		operations.forEach(operation -> System.out.println("   " + operation));

		if (Objects.isNull(currentProject)) {
			System.out.println("\nYou are currently not working with a project!");
		} else {
			System.out.println("\nYou are working with project " + currentProject);
		}
	}

	private String getStringInput(String prompt) {
		System.out.println(prompt + ": ");
		String line = scanner.nextLine();

		return line.isBlank() ? null : line.trim();
	}
}



//Methods i thought i needed based off Dr.Robs videos
//private void modifyStepInCurrentProject() {
//	if (Objects.isNull(currentProject)) {
//		System.out.println("\nPlease select a project first.");
//		return;
//	}
//
//	List<Step> steps = projectService.fetchSteps(currentProject.getProjectId());
//
//	System.out.println("\nSteps for current project");
//	steps.forEach(step -> System.out.println("   " + step));
//
//	Integer stepId = getIntInput("Enter step ID of step to modify");
//
//	if (Objects.nonNull(stepId)) {
//		String stepText = getStringInput("Enter new step text");
//
//		if (Objects.nonNull(stepText)) {
//			Step step = new Step();
//
//			step.setStepId(stepId);
//			step.setStepText(stepText);
//
//			projectService.modifyStep(step);
//			currentProject = projectService.fetchProjectById(currentProject.getProjectId());
//		}
//
//	}
//}

//private void addCategoryToCurrentProject() {
//	if (Objects.isNull(currentProject)) {
//		System.out.println("\nPlease select a project first.");
//		return;
//	}
//
//	List<Category> categories = projectService.fetchCategories();
//
//	categories.forEach(category -> System.out.println("   " + category.getCategoryName()));
//
//	String category = getStringInput("Enter the category to add");
//
//	if (Objects.nonNull(category)) {
//		projectService.addCategoryToProject(currentProject.getProjectId(), category);
//		currentProject = projectService.fetchProjectById(currentProject.getProjectId());
//	}
//}
//
//private void addStepToCurrentProject() {
//	if (Objects.isNull(currentProject)) {
//		System.out.println("\nPlease select a project first.");
//		return;
//	}
//
//	String stepText = getStringInput("Enter the step text");
//
//	if (Objects.nonNull(stepText)) {
//		Step step = new Step();
//
//		step.setProjectId(currentProject.getProjectId());
//		step.setStepText(stepText);
//
//		projectService.addStep(step);
//		currentProject = projectService.fetchProjectById(step.getProjectId());
//	}
//
//}
//
//private void setCurrentProject() {
//	List<Project> projects = listProjects();
//
//	Integer projectId = getIntInput("Select a project ID");
//	currentProject = null;
//
//	for (Project project : projects) {
//		if (project.getProjectId().equals(projectId)) {
//			currentProject = projectService.fetchProjectById(projectId);
//			break;
//		}
//	}
//	if (Objects.isNull(currentProject)) {
//		System.out.println("\nInvalid project Selected.");
//	}
//
//}
//
//private void createTables() {
//	projectService.createAndPopulateTables();
//	System.out.println("\nTables created and populated!");
//
//}

