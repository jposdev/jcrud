package net.pdp7.jcrud.petclinic;

import java.sql.SQLException;
import java.util.Collections;

import javax.sql.DataSource;

import net.pdp7.jcrud.TableController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import schemacrawler.schema.Database;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.RoutineType;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.utility.SchemaCrawlerUtility;

@Configuration
@EnableAutoConfiguration
public class Application {

	@Autowired
	public DataSource dataSource;
	
	@Autowired
	public NamedParameterJdbcTemplate jdbcTemplate;
	
	@Bean
	public Schema schema() {
		return database().getSchema("TESTDB.PUBLIC");
	}
	
	@Bean
	@DependsOn("dataSourceAutoConfigurationInitializer")
	public Database database() {
		SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
		schemaCrawlerOptions.setRoutineTypes(Collections.<RoutineType>emptyList());
		try {
			return SchemaCrawlerUtility.getDatabase(dataSource.getConnection(), schemaCrawlerOptions);
		} catch (SchemaCrawlerException e) {
			throw new RuntimeException(e);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Bean
	public IndexController indexController() {
		return new IndexController(crudControllers());
	}

	@Bean
	public TableController[] crudControllers() {
		return new TableController[] { vetController(), ownerController(), petTypeController(), visitController(), petController() };
	}
	
	@Bean
	public VetController vetController() {
		VetController vetController = new VetController(table("VETS"), jdbcTemplate);
		vetController.addInline(foreignKey("VET_SPECIALTIES", "VET_SPECIALTY__VET"), vetSpecialtyController());
		vetController.addInline(foreignKey("VISITS", "VISIT__VET"), visitController());
		return vetController;
	}

	@Bean
	public VetSpecialtyController vetSpecialtyController() {
		return new VetSpecialtyController(table("VET_SPECIALTIES"), jdbcTemplate);
	}
	
	
	@Bean
	public OwnerController ownerController() {
		OwnerController ownerController = new OwnerController(table("OWNERS"), jdbcTemplate);
		ownerController.addInline(foreignKey("PETS", "PET__OWNER"), petController());
		return ownerController;
	}

	@Bean
	public PetController petController() {
		PetController petController = new PetController(table("PETS"), jdbcTemplate);
		petController.addInline(foreignKey("VISITS", "VISIT__PET"), visitController());
		return petController;
	}

	
	@Bean
	public PetTypeController petTypeController() {
		return new PetTypeController(table("PET_TYPES"), jdbcTemplate);
	}

	@Bean
	public VisitController visitController() {
		return new VisitController(table("VISITS"), jdbcTemplate);
	}

	protected Table table(String tableName) {
		return database().getTable(schema(), tableName);
	}

	protected ForeignKey foreignKey(String tableName, String foreignKeyName) {
		return table(tableName).getForeignKeys().stream().filter(fk -> fk.getName().equals(foreignKeyName)).findFirst().get();
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
