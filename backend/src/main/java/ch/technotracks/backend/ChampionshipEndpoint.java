package ch.technotracks.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p/>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */
@Api(
        name = "championshipApi",
        version = "v1",
        resource = "championship",
        namespace = @ApiNamespace(
                ownerDomain = "backend.technotracks.ch",
                ownerName = "backend.technotracks.ch",
                packagePath = ""
        )
)
public class ChampionshipEndpoint {

    private static final Logger logger = Logger.getLogger(ChampionshipEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(Championship.class);
    }

    /**
     * Returns the {@link Championship} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Championship} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "championship/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Championship get(@Named("id") Long id) throws NotFoundException {
        logger.info("Getting Championship with ID: " + id);
        Championship championship = ofy().load().type(Championship.class).id(id).now();
        if (championship == null) {
            throw new NotFoundException("Could not find Championship with ID: " + id);
        }
        return championship;
    }

    /**
     * Inserts a new {@code Championship}.
     */
    @ApiMethod(
            name = "insert",
            path = "championship",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Championship insert(Championship championship) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that championship.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(championship).now();
        logger.info("Created Championship with ID: " + championship.getId());

        return ofy().load().entity(championship).now();
    }

    /**
     * Updates an existing {@code Championship}.
     *
     * @param id           the ID of the entity to be updated
     * @param championship the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Championship}
     */
    @ApiMethod(
            name = "update",
            path = "championship/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Championship update(@Named("id") Long id, Championship championship) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(championship).now();
        logger.info("Updated Championship: " + championship);
        return ofy().load().entity(championship).now();
    }

    /**
     * Deletes the specified {@code Championship}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Championship}
     */
    @ApiMethod(
            name = "remove",
            path = "championship/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") Long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(Championship.class).id(id).now();
        logger.info("Deleted Championship with ID: " + id);
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "list",
            path = "championship",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Championship> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Championship> query = ofy().load().type(Championship.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Championship> queryIterator = query.iterator();
        List<Championship> championshipList = new ArrayList<Championship>(limit);
        while (queryIterator.hasNext()) {
            championshipList.add(queryIterator.next());
        }
        return CollectionResponse.<Championship>builder().setItems(championshipList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(Championship.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Championship with ID: " + id);
        }
    }
}