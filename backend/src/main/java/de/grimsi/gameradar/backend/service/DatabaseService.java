package de.grimsi.gameradar.backend.service;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DatabaseService<Entity, Dto, Repository extends JpaRepository<Entity, Long>> {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private Repository repository;

    @Autowired
    private ModelMapper mapper;

    private final Logger log;
    private final Class<Entity> entityClass;
    private final Class<Dto> dtoClass;
    private final String entityName;

    @SuppressWarnings("unchecked")
    public DatabaseService() {
        Class<?>[] typeArguments = GenericTypeResolver.resolveTypeArguments(getClass(), DatabaseService.class);
        assert typeArguments != null;
        entityClass = (Class<Entity>) typeArguments[0];
        dtoClass = (Class<Dto>) typeArguments[1];
        entityName = entityClass.getSimpleName().toLowerCase();
        log = LoggerFactory.getLogger(buildLoggerName());
    }

    protected Dto save(Dto dto) {
        Entity entity = convertToEntity(dto);
        entity = repository.save(entity);
        return convertToDto(entity);
    }

    public Dto create(Dto dto) {
        log.debug("create {}: {}", entityName, dto);
        return save(dto);
    }

    public List<Dto> getAll() {
        log.debug("get all {}s", entityName);
        return convertToDto(repository.findAll());
    }

    protected Entity getEntityById(Long id) {
        log.debug("find {} with id '{}'", entityName, id);
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, entityName + ".invalid-id"));
    }

    public Dto getById(Long id) {
        return convertToDto(getEntityById(id));
    }

    public Dto edit(Long id, Dto dto) {
        log.debug("edit by id '{}' {}: {}", id, entityName, dto);
        return save(dto);
    }

    public void delete(Long id) {
        log.debug("delete {}: '{}'", entityName, id);
        repository.deleteById(id);
    }

    protected Dto convertToDto(Entity entity) {
        return mapper.map(entity, dtoClass);
    }

    protected List<Dto> convertToDto(List<Entity> entities) {
        return entities.stream().map(this::convertToDto).toList();
    }

    protected Entity convertToEntity(Dto dto) {
        return mapper.map(dto, entityClass);
    }

    protected List<Entity> convertToEntity(List<Dto> dtos) {
        return dtos.stream().map(this::convertToEntity).toList();
    }

    protected Set<Entity> convertToEntity(Set<Dto> dtos) {
        return dtos.stream().map(this::convertToEntity).collect(Collectors.toUnmodifiableSet());
    }

    private String buildLoggerName() {
        /* this does rely on all services being named after the pattern {entityName}Service, e.g. RoleService, GameService, UserService, ... */
        //TODO: find out how to use custom named loggers in Spring
        assert entityName != null;
        return MethodHandles.lookup().lookupClass().getPackageName() + "." + entityName.substring(0, 1).toUpperCase() + entityName.substring(1) + "Service";
    }
}
