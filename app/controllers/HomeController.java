package controllers;

import play.mvc.*;
import play.libs.Json;
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
    public Result index() {
        return ok(Json.toJson("Done"));
        // return ok(views.html.index.render());
    }

//     public Result sayHello(Http.Request request) {
//         JsonNode json = request.body().asJson();
//         if (json == null) {
//           return badRequest("Expecting Json data");
//         } else {
//           String name = json.findPath("name").textValue();
//           if (name == null) {
//             return badRequest("Missing parameter [name]");
//           } else {
//             return ok(Json.toJason("Hello " + name));
//           }
//         }
//       }
}
