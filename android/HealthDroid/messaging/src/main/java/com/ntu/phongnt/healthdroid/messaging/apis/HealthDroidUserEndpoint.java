package com.ntu.phongnt.healthdroid.messaging.apis;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.appengine.api.users.User;
import com.ntu.phongnt.healthdroid.messaging.entities.HealthDroidUser;
import com.ntu.phongnt.healthdroid.messaging.secured.AppConstants;

import java.util.ArrayList;
import java.util.List;

import static com.ntu.phongnt.healthdroid.messaging.OfyService.ofy;

@Api(
        name = "user",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "data.healthdroid.phongnt.ntu.com",
                ownerName = "data.healthdroid.phongnt.ntu.com",
                packagePath = ""
        ),
        clientIds = {AppConstants.WEB_CLIENT_ID, AppConstants.ANDROID_CLIENT_ID, AppConstants.IOS_CLIENT_ID, com.google.api.server.spi.Constant.API_EXPLORER_CLIENT_ID},
        audiences = {AppConstants.ANDROID_AUDIENCE},
        scopes = {"https://www.googleapis.com/auth/userinfo.email"}
)

public class HealthDroidUserEndpoint {
    @ApiMethod(name = "add")
    public HealthDroidUser addData(User user) {
        if (user != null) {
            HealthDroidUser healthDroidUser = new HealthDroidUser(user.getEmail());
            healthDroidUser.setEmail(user.getEmail());
            ofy().save().entity(healthDroidUser);

            //Add document for search API
            Document userDocument = Document.newBuilder()
                    .setId(healthDroidUser.getId())
                    .addField(Field.newBuilder().setName("email").setText(user.getEmail()))
                    .build();
            IndexSpec indexSpec = IndexSpec.newBuilder().setName("user").build();
            Index index = SearchServiceFactory.getSearchService().getIndex(indexSpec);
            index.put(userDocument);

            return healthDroidUser;
        }
        return null;
    }

    @ApiMethod(name = "get")
    public List<HealthDroidUser> getUser(@Nullable @Named("userId") String userId) {
        List<HealthDroidUser> list = null;
        if (userId == null) {
            list = HealthDroidUser.getAllUsers();
        } else {
            HealthDroidUser user = HealthDroidUser.getUser(userId);
            list = new ArrayList<HealthDroidUser>();
            list.add(user);
        }
        return list;
    }

    @ApiMethod(name = "query")
    public List<HealthDroidUser> queryUser(@Named("queryString") String queryString) {
        List<HealthDroidUser> list = new ArrayList<>();
        IndexSpec indexSpec = IndexSpec.newBuilder().setName("user").build();
        Index index = SearchServiceFactory.getSearchService().getIndex(indexSpec);
        Results<ScoredDocument> results = index.search(queryString);
        for (ScoredDocument scoredDocument : results.getResults()) {
            String email = scoredDocument.getOnlyField("email").getText();
            if (email != null && !email.isEmpty()) {
                HealthDroidUser user = HealthDroidUser.getUser(email);
                list.add(user);
            }
        }
        return list;
    }
}
