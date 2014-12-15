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
        name = "gPSDataApi",
        version = "v1",
        resource = "gPSData",
        namespace = @ApiNamespace(
                ownerDomain = "backend.technotracks.ch",
                ownerName = "backend.technotracks.ch",
                packagePath = ""
        )
)
public class GPSDataEndpoint {

    private static final Logger logger = Logger.getLogger(GPSDataEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(GPSData.class);
    }

    /**
     * Returns the {@link GPSData} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code GPSData} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "gPSData/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public GPSData get(@Named("id") Long id) throws NotFoundException {
        logger.info("Getting GPSData with ID: " + id);
        GPSData gPSData = ofy().load().type(GPSData.class).id(id).now();
        if (gPSData == null) {
            throw new NotFoundException("Could not find GPSData with ID: " + id);
        }
        return gPSData;
    }

    /**
     * Inserts a new {@code GPSData}.
     */
    @ApiMethod(
            name = "insert",
            path = "gPSData",
            httpMethod = ApiMethod.HttpMethod.POST)
    public GPSData insert(GPSData gPSData) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that gPSData.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(gPSData).now();
        logger.info("Created GPSData with ID: " + gPSData.getId());

        return ofy().load().entity(gPSData).now();
    }

    /**
     * Updates an existing {@code GPSData}.
     *
     * @param id      the ID of the entity to be updated
     * @param gPSData the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code GPSData}
     */
    @ApiMethod(
            name = "update",
            path = "gPSData/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public GPSData update(@Named("id") Long id, GPSData gPSData) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(gPSData).now();
        logger.info("Updated GPSData: " + gPSData);
        return ofy().load().entity(gPSData).now();
    }

    /**
     * Deletes the specified {@code GPSData}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code GPSData}
     */
    @ApiMethod(
            name = "remove",
            path = "gPSData/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") Long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(GPSData.class).id(id).now();
        logger.info("Deleted GPSData with ID: " + id);
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
            path = "gPSData",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<GPSData> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<GPSData> query = ofy().load().type(GPSData.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<GPSData> queryIterator = query.iterator();
        List<GPSData> gPSDataList = new ArrayList<GPSData>(limit);
        while (queryIterator.hasNext()) {
            gPSDataList.add(queryIterator.next());
        }
        return CollectionResponse.<GPSData>builder().setItems(gPSDataList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(GPSData.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find GPSData with ID: " + id);
        }
    }
}