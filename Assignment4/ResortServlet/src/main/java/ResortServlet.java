import redis.clients.jedis.JedisPooled;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.awt.*;
import java.io.IOException;

@WebServlet(name = "ResortServlet", value = "/ResortServlet")
public class ResortServlet extends HttpServlet {
    JedisPooled jedis;
    private int resortID;
    private int seasonID;


    public void init(){
        try {
            jedis = new JedisPooled("54.189.195.124", 6379);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            //http://localhost:8080/ResortServlet_war_exploded/
            response.setContentType("text/plain");
            String urlPath = request.getPathInfo();
            String[] urlParts = urlPath.split("/");
            // check we have a URL
            if (urlPath == null || urlPath.isEmpty() || urlParts.length != 7) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("missing paramterers");
                return;
            }

            if (!isRequestValid(urlParts)) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("inValid Parameter");
            } else {
                StringBuilder keyResortAndDay = new StringBuilder();
                keyResortAndDay.append("resortID:7:seasonID:2022:dayID:");
                keyResortAndDay.append(urlParts[5]);
                long numSkiers = jedis.pfcount(keyResortAndDay.toString());
                response.getWriter().write(String.valueOf(numSkiers));
            }
        }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}

    private boolean isRequestValid (String[] urlPath) {
        // /resorts/{resortID}/seasons/{seasonID}/day/{dayID}/skiers
        String seasons = urlPath[2];
        String days = urlPath[4];
        String skiers = urlPath[6];
        if (!seasons.equals("seasons") || !days.equals("day") || !skiers.equals("skiers")) {
            return false;
        }
        try {
            resortID = Integer.parseInt(urlPath[1]);
            seasonID = Integer.parseInt(urlPath[3]);
        } catch (NumberFormatException e) {
            return false;
        }
        if (seasonID != 2022 || resortID != 7) {
            return false;
        }
        return true;
    }


}
