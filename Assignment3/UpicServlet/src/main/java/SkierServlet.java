
import CS6650_Servler.LiftRide;
import CS6650_Servler.RMQChannelFactory;
import CS6650_Servler.RMQChannelPool;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

@WebServlet(name = "SkierServlet", value = "/SkierServlet")
public class SkierServlet extends HttpServlet {
    private int resortID;
    private int seasonID;
    private int dayID;
    private int skierID;
    private int liftTime;
    private int liftID;
    private static final String DELIMITER = " ";
    private static final String EXCHANGE_NAME = "liftrideRecord";

    private final static String QUEUE_NAME = "skiersPost";

    private static Connection connection;
    private RMQChannelFactory rmqChannelFactory;
    private RMQChannelPool rmqChannelPool;

    @Override
    public void init(){
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("52.12.44.5");
            factory.setUsername("admin");
            factory.setPassword("password");
            try {
                connection = factory.newConnection();
            } catch (IOException | TimeoutException e) {
                System.out.println("error while trying to new a connection");
                e.printStackTrace();
            }

            rmqChannelFactory = new RMQChannelFactory(connection);
            rmqChannelPool = new RMQChannelPool(200, rmqChannelFactory);

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        String urlPath = request.getPathInfo();
        StringBuilder sb = new StringBuilder();
        String s;

        Gson gson = new Gson();
        while ((s = request.getReader().readLine()) != null) {
            sb.append(s);
        }

        LiftRide liftRide = (LiftRide) gson.fromJson(sb.toString(), LiftRide.class);


        // check we have a URL and request body
        if (urlPath == null || urlPath.isEmpty() || liftRide == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("missing paramterers");
            return;
        }

        String[] urlParts = urlPath.split("/");
        // and now validate url path and return the response status code
        // (and maybe also some value if input is valid)

            /*
    /skiers/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}
            int32               2019            1-366           int32
     */

        if (!isRequestValid(urlParts, liftRide)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("inValid Parameter");
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            String message = skierID + DELIMITER + resortID + DELIMITER + seasonID + DELIMITER + dayID + DELIMITER + liftTime + DELIMITER + liftID;
            Channel channel = null;
            try {
                channel = rmqChannelPool.borrowObject();
                channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
                channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes(StandardCharsets.UTF_8));

            } catch (IOException e) {
                throw e;
            } catch (Exception e) {
                //System.out.println("error. can't borrow channel from pool");
                throw new RuntimeException("Unable to borrow channel from pool" + e.toString());
            } finally {
                try {
                    if (channel != null) {
                        rmqChannelPool.returnObject(channel);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            response.getWriter().write("It works!");
        }
    }

    private boolean isRequestValid(String[] urlPath, LiftRide liftRide) {
        String seasons = urlPath[2];
        String days = urlPath[4];
        String skiers2 = urlPath[6];
        if(!seasons.equals("seasons") || !days.equals("days") || !skiers2.equals("skiers")){
            return false;

        }
        if (liftRide == null) {
            return false;
        }
        try {
            resortID = Integer.parseInt(urlPath[1]);
            seasonID = Integer.parseInt(urlPath[3]);
            dayID = Integer.parseInt(urlPath[5]);
            skierID = Integer.parseInt(urlPath[7]);
            liftTime = liftRide.getLiftTime();
            liftID = liftRide.getLiftID();

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
        if (liftTime < 1 || liftTime > 360) {
            return false;
        }
        if (liftID < 1 || liftID > 40){
            return false;
        }
        return true;
    }


}
