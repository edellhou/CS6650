import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "SkierServlet", value = "/SkierServlet")
public class SkierServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        String urlPath = request.getPathInfo();

        // check we have a URL!
        if (urlPath == null || urlPath.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("missing paramterers");
            return;
        }

        String[] urlParts = urlPath.split("/");
        // and now validate url path and return the response status code
        // (and maybe also some value if input is valid)

        if (!isUrlValid(urlParts)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("inValid Parameter");
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            // do any sophisticated processing with urlParts which contains all the url params
            // TODO: process url params in `urlParts`

            response.getWriter().write("It works!");
        }
    }

    private boolean isUrlValid(String[] urlPath) {
        String seasons = urlPath[2];
        String days = urlPath[4];
        String skiers2 = urlPath[6];
        if(!seasons.equals("seasons") || !days.equals("days") || !skiers2.equals("skiers")){
            return false;
        }
        int resortID;
        int seasonID;
        int dayID;
        int skierID;
        try {
            resortID = Integer.parseInt(urlPath[1]);
            seasonID = Integer.parseInt(urlPath[3]);
            dayID = Integer.parseInt(urlPath[5]);
            skierID = Integer.parseInt(urlPath[7]);
        } catch (NumberFormatException e) {
            return false;
        }
        if(seasonID != 2022 || dayID !=1){
            return false;
        }
        if (resortID > 10 || resortID < 1) {
            return false;
        }

        if (skierID < 1 || skierID > 100000){
            return false;
        }
        return true;
    }

}
