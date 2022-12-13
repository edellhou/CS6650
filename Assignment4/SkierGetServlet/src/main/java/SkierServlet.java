import redis.clients.jedis.JedisPooled;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "SkierServlet", value = "/SkierServlet")
public class SkierServlet extends HttpServlet {
    private int resortID;
    private int seasonID;
    private int dayID;
    private int skierID;
    JedisPooled jedis;

    @Override
    public void init(){
        try {
            jedis = new JedisPooled("54.189.195.124", 6379);
        } catch(Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //GET/skiers/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}
        //GET/skiers/{skierID}/vertical

        response.setContentType("text/plain");
        String urlPath = request.getPathInfo();


        // check we have a URL
        if (urlPath == null || urlPath.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("missing paramterers");
            return;
        }


        String[] urlParts = urlPath.split("/");


        if (urlParts.length == 8){
            if (!isGetRequestValid1(urlParts)) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("inValid Parameter");
            }else{
                StringBuilder key = new StringBuilder();
                key.append("resortID:7:seasonID:2022:dayID:");
                key.append(urlParts[5]);
                key.append(":");
                key.append("skierID:");
                key.append(urlParts[7]);
                String vertical = jedis.get(key.toString());
                response.getWriter().write(vertical);

            }

        } else{
            //GET/skiers/{skierID}/vertical
            String resortID = request.getParameter("resort");
            String seasonID = request.getParameter("season");
            if (!isGetRequestValid2(urlParts, resortID)) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("inValid Parameter");
            }else{
                System.out.println("step 1");
                StringBuilder key = new StringBuilder();
                key.append("resortID:7:seasonID:2022:skierID:");
                System.out.println("step 2");
                key.append(urlParts[1]);
                System.out.println("step 3");
                String vertical = jedis.get(key.toString());
                response.getWriter().write(vertical);
                System.out.println("step 4");

            }

        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
    private boolean isGetRequestValid1(String[] urlParts){
        //GET/skiers/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}
        String seasons = urlParts[2];
        String days = urlParts[4];
        String skiers = urlParts[6];
        if (!seasons.equals("seasons") || !days.equals("days") || !skiers.equals("skiers")) {
            return false;
        }
        try {
            resortID = Integer.parseInt(urlParts[1]);
            seasonID = Integer.parseInt(urlParts[3]);
            dayID = Integer.parseInt(urlParts[5]);
            skierID = Integer.parseInt(urlParts[7]);
        } catch (NumberFormatException e) {
            return false;
        }
        if (seasonID != 2022 || resortID != 7) {
            return false;
        }

        if (skierID < 1 || skierID > 100000){
            return false;
        }
        if (dayID != 1 && dayID != 2 && dayID != 3){
            return false;
        }
        return true;
    }

    private boolean isGetRequestValid2(String[] urlParts, String resortID){
        //GET/skiers/{skierID}/vertical
        // resort is required as parameter, season is optional
        String vertical = urlParts[2];
        if (!vertical.equals("vertical")) {
            return false;
        }
        try {
            skierID = Integer.parseInt(urlParts[1]);
        } catch (NumberFormatException e) {

            return false;
        }
        if (skierID < 1 || skierID > 100000){

            return false;
        }
        if (resortID == null) return false;

        return true;
    }


}
