package controllers;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index(Http.Request request) {
        return ok(Json.toJson("Done"));
        // return ok(views.html.index.render());
    }

    public Result help() {

        String string = null;
        if (request().hasBody() && request().body().asJson() != null) {
            string = request().body().asJson().toString();
            return ok(Json.toJson(string));
        } else {
            return badRequest();
        }
    }

    public Result postreq(Http.Request request) {

        String string = null;
        if (request.hasBody() && request.body().asJson() != null) {
            string = request.body().asJson().toString();
            return ok(Json.toJson("Response:" + string));
        } else {
            return badRequest();
        }
    }

}
