package ru.simplgroupp.rest.api.user;

import ru.simplgroupp.data.ErrorData;
import ru.simplgroupp.exception.KassaException;
import ru.simplgroupp.rest.api.JsonResult;
import ru.simplgroupp.rest.api.data.bonus.FriendData;
import ru.simplgroupp.rest.api.service.FriendsService;
import ru.simplgroupp.rest.api.service.UserService;
import ru.simplgroupp.transfer.Users;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

@Path("/friends")
public class FriendsController {
    @Inject
    private FriendsService friendsService;
    @Inject
    private UserService userServ;


    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<Map<String, Object>> getFriends(@Context HttpServletRequest request) {
        Users user = userServ.getUser();
        return new JsonResult<>(friendsService.getPeopleFriends(user));
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<List<ErrorData>> addFriend(FriendData friend) {
        Users user = userServ.getUser();
        try {
            friendsService.addFriend(user, friend);
            return new JsonResult<>();
        } catch (Exception e) {
            return new JsonResult<>(e);
        }
    }

    @PUT
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public JsonResult<List<ErrorData>> updateFriend(FriendData friend) {
        try {
            friendsService.updateFriend(friend);
            return new JsonResult<>();
        } catch (KassaException e) {
            return new JsonResult<>(e);
        }
    }

    @DELETE
    @Path("/{friendId}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public void removeFriend(@PathParam("friendId") Integer friendId) {
        friendsService.removeFriend(friendId);
    }
}
