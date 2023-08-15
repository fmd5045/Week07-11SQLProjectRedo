package projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import projects.entity.Category;
import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import projects.exception.DbException;
import provided.util.DaoBase;

public class ProjectDao extends DaoBase {

	private static final String CATEGORY_TABLE = "category";
	private static final String MATERIAL_TABLE = "material";
	private static final String PROJECT_TABLE = "project";
	private static final String PROJECT_CATEGORY_TABLE = "project_category";
	private static final String STEP_TABLE = "step";

	public List<Project> fetchAllProjects() {
		String sql = "SELECT * FROM " + PROJECT_TABLE + " ORDER BY project_id";

		try (Connection connection = DbConnection.getConnection()) {
			startTransaction(connection);

			try (PreparedStatement statement = connection.prepareStatement(sql)) {
				try (ResultSet resultset = statement.executeQuery()) {
					List<Project> projects = new LinkedList<>();

					while (resultset.next()) {
						projects.add(extract(resultset, Project.class));
					}
					return projects;
				}
			} catch (Exception exception) {
				rollbackTransaction(connection);
				throw new DbException(exception);
			}
		} catch (SQLException exception) {
			throw new DbException(exception);
		}
	}

	public Optional<Project> fetchProjectById(Integer projectId) {
		String sql = "SELECT * FROM " + PROJECT_TABLE + " WHERE project_id = ?";

		try (Connection connection = DbConnection.getConnection()) {
			startTransaction(connection);

			try {
				Project project = null;

				try (PreparedStatement statement = connection.prepareStatement(sql)) {
					setParameter(statement, 1, projectId, Integer.class);

					try (ResultSet resultset = statement.executeQuery()) {
						if (resultset.next()) {
							project = extract(resultset, Project.class);
						}
					}
				}
				if (Objects.nonNull(project)) {
					project.getMaterials().addAll(fetchProjectMaterials(connection, projectId));

					project.getSteps().addAll(fetchProjectSteps(connection, projectId));
					project.getCategories().addAll(fetchProjectCategories(connection, projectId));
				}
				// first missing line of code
				commitTransaction(connection);
				return Optional.ofNullable(project);
				
			} catch (Exception exception) {
				rollbackTransaction(connection);
				throw new DbException(exception);
			}

		} catch (SQLException exception) {
			throw new DbException(exception);
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

			} catch (Exception exception) {
				rollbackTransaction(connection);
				throw new DbException(exception);
			}
		} catch (SQLException exception) {
			throw new DbException(exception);
		}
	}

	private List<Category> fetchProjectCategories(Connection connection, Integer projectId) throws SQLException {
		String sql = "" + "SELECT ct.* " + "FROM " + CATEGORY_TABLE + " ct " + "JOIN " + PROJECT_CATEGORY_TABLE + " pct USING (category_id) " + "WHERE project_id = ? ";

		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			setParameter(statement, 1, projectId, Integer.class);

			try (ResultSet resultSet = statement.executeQuery()) {
				List<Category> categories = new LinkedList<Category>();

				while (resultSet.next()) {
					categories.add(extract(resultSet, Category.class));
				}

				return categories;
			}
		}
	}

	private List<Step> fetchProjectSteps(Connection connection, Integer projectId) throws SQLException {
		String sql = "SELECT * FROM " + STEP_TABLE + " WHERE project_id = ?";

		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			setParameter(statement, 1, projectId, Integer.class);

			try (ResultSet resultSet = statement.executeQuery()) {
				List<Step> steps = new LinkedList<Step>();

				while (resultSet.next()) {
					steps.add(extract(resultSet, Step.class));
				}

				return steps;
			}
		}
	}

	private List<Material> fetchProjectMaterials(Connection connection, Integer projectId) throws SQLException {
		String sql = "SELECT * FROM " + MATERIAL_TABLE + "WHERE project_id = ?";

		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			setParameter(statement, 1, projectId, Integer.class);

			try (ResultSet resultset = statement.executeQuery()) {
				List<Material> materials = new LinkedList<Material>();

				while (resultset.next()) {
					Material material = extract(resultset, Material.class);

					materials.add(material);
				}

				return materials;
			}
		}
	}

	public Project insertProject(Project project) {
		String sql ="INSERT INTO " + PROJECT_TABLE + " "
				+ "(project_name , estimated_hours, actual_hours , difficulty, notes) " + "VALUES " + "(?, ?, ?, ?, ?)";

		try (Connection connection = DbConnection.getConnection()) {
			startTransaction(connection);

			try (PreparedStatement statement = connection.prepareStatement(sql)) {
				setParameter(statement, 1, project.getProjectName(), String.class);
				setParameter(statement, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(statement, 3, project.getActualHours(), BigDecimal.class);
				setParameter(statement, 4, project.getDifficulty(), Integer.class);
				setParameter(statement, 5, project.getNotes(), String.class);
				statement.executeUpdate();
				
				Integer projectId = getLastInsertId(connection, PROJECT_TABLE);
				commitTransaction(connection);

				project.setProjectId(projectId);
				return project;

			} catch (Exception exception) {
				rollbackTransaction(connection);
				throw new DbException(exception);
			}
		} catch (SQLException exception) {
			throw new DbException(exception);
		}
	}

//	public void addMaterialToProject(Material material) {
//		String sql = "INSERT INTO " + MATERIAL_TABLE + " (material_id, project_id, material_name, num_required, cost) "
//				+ " VALUES (?, ?, ?, ?, ?)";
//
//		try (Connection connection = DbConnection.getConnection()) {
//			startTransaction(connection);
//
//			try {
//				Integer order = getNextSequenceNumber(connection, material.getProjectId(), MATERIAL_TABLE, "project_id");
//				try (PreparedStatement statement = connection.prepareStatement(sql)) {
//					setParameter(statement, 1, material.getMaterialId(), Integer.class);
//					setParameter(statement, 2, material.getProjectId(), Integer.class);
//					setParameter(statement, 3, material.getMaterialName(), String.class);
//					setParameter(statement, 4, material.getNumRequired(), BigDecimal.class);
//					setParameter(statement, 5, material.getCost(), BigDecimal.class);
//
//					statement.executeUpdate();
//					commitTransaction(connection);
//
//				}
//			} catch (Exception exception) {
//				rollbackTransaction(connection);
//				throw new DbException(exception);
//			}
//		} catch (SQLException exception) {
//			throw new DbException(exception);
//		}
//
//	}

//	public void addStepToProject(Step step) {
//		String sql = "INSERT INTO " + STEP_TABLE + " (project_id, step_order, step_text)" + " VALUES (?, ?, ?)";
//
//		try (Connection connection = DbConnection.getConnection()) {
//			startTransaction(connection);
//
//			Integer order = getNextSequenceNumber(connection, step.getProjectId(), STEP_TABLE, "project_id");
//
//			try (PreparedStatement statement = connection.prepareStatement(sql)) {
//				setParameter(statement, 1, step.getProjectId(), Integer.class);
//				setParameter(statement, 2, order, Integer.class);
//				setParameter(statement, 3, step.getStepText(), String.class);
//
//				statement.executeUpdate();
//				commitTransaction(connection);
//			} catch (Exception exception) {
//				rollbackTransaction(connection);
//				throw new DbException(exception);
//			}
//		} catch (SQLException exception) {
//			throw new DbException(exception);
//		}
//	}

//	public List<Category> fetchAllCategories() {
//		String sql = "SELECT * FROM " + CATEGORY_TABLE + " ORDER BY category_name";
//		try (Connection connection = DbConnection.getConnection()) {
//			startTransaction(connection);
//
//			try (PreparedStatement statement = connection.prepareStatement(sql)) {
//				try (ResultSet resultSet = statement.executeQuery()) {
//					List<Category> categories = new LinkedList<>();
//
//					while (resultSet.next()) {
//						categories.add(extract(resultSet, Category.class));
//					}
//					return categories;
//				}
//			} catch (Exception exception) {
//				rollbackTransaction(connection);
//				throw new DbException(exception);
//			}
//		} catch (SQLException exception) {
//			throw new DbException(exception);
//		}
//	}

//	public void addCategoryToProject(Integer projectId, String category) {
//		String subQuery = "(SELECT category_id FROM " + CATEGORY_TABLE + " WHERE category_name = ?)";
//
//		String sql = "INSERT INTO " + PROJECT_CATEGORY_TABLE + " (project_id, category_id) VALUES (?, " + subQuery + ")";
//
//		try (Connection connection = DbConnection.getConnection()) {
//			startTransaction(connection);
//
//			try (PreparedStatement statement = connection.prepareStatement(sql)) {
//				setParameter(statement, 1, projectId, Integer.class);
//				setParameter(statement, 2, category, String.class);
//
//				statement.executeUpdate();
//				commitTransaction(connection);
//
//			} catch (Exception exception) {
//				rollbackTransaction(connection);
//				throw new DbException(exception);
//			}
//
//		} catch (SQLException exception) {
//			throw new DbException(exception);
//		}
//	}

	public boolean modifyProjectDetails(Project project) {
		//@formatter: off
		String sql = ""
        + "UPDATE " + PROJECT_TABLE + " SET "
        + "project_name = ?, "
        + "estimated_hours = ?, "
        + "actual_hours = ?, "
        + "difficulty = ?, "
        + "notes = ? "
        + "WHERE project_id = ?";
		//@formatter: off

		try (Connection connection = DbConnection.getConnection()) {
			startTransaction(connection);

      try (PreparedStatement statement = connection.prepareStatement(sql)) {
        setParameter(statement, 1, project.getProjectName(), String.class);
        setParameter(statement, 2, project.getEstimatedHours(), BigDecimal.class);
        setParameter(statement, 3, project.getActualHours(), BigDecimal.class);
        setParameter(statement, 4, project.getDifficulty(), Integer.class);
        setParameter(statement, 5, project.getNotes(), String.class);
        setParameter(statement, 6, project.getProjectId(), Integer.class);

				boolean updated = statement.executeUpdate() == 1;
				commitTransaction(connection);

				return updated;

			} catch (Exception exception) {
				rollbackTransaction(connection);
				throw new DbException(exception);
			}

		} catch (Exception exception) {
			throw new DbException(exception);
		}
	}

	public boolean deleteProject(Integer projectId) {
		String sql = "DELETE FROM " + PROJECT_TABLE + " WHERE project_id = ?";

		try (Connection connection = DbConnection.getConnection()) {
			startTransaction(connection);
			try (PreparedStatement statement = connection.prepareStatement(sql)) {
				setParameter(statement, 1, projectId, Integer.class);

				boolean deleted = statement.executeUpdate() == 1;

				commitTransaction(connection);
				return deleted;

			} catch (Exception exception) {
				rollbackTransaction(connection);
				throw new DbException(exception);
			}
		} catch (SQLException exception) {
			throw new DbException(exception);
		}
	}
}